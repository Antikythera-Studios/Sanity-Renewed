package guivnf.sanity_renewed.compat;

import guivnf.sanity_renewed.SanityMod;
import dev.architectury.platform.Platform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;
import java.util.OptionalInt;
import java.util.function.Supplier;

public final class SurvivalCompat
{
    public static final int TEMP_UNAVAILABLE = Integer.MIN_VALUE;
    public static final int TEMP_NEUTRAL = 0;
    public static final int TEMP_HOT = 1;
    public static final int TEMP_COLD = -1;

    private static final String MOD_TAN = "toughasnails";
    private static final String MOD_LSO = "legendarysurvivaloverhaul";

    private static boolean tanReady;
    private static Method tanGetThirstData;
    private static Method tanThirstGetThirst;
    private static Method tanGetTempData;
    private static Method tanTempGetLevel;

    private static boolean lsoReady;
    private static Supplier<?> lsoThirstAttachmentSupplier;
    private static Supplier<?> lsoTemperatureAttachmentSupplier;
    private static Method lsoPlayerGetData;
    private static Method lsoThirstGetHydration;
    private static Method lsoTempGetEnum;

    private static boolean initDone;

    private SurvivalCompat() {}

    public static boolean isAnyLoaded()
    {
        return Platform.isModLoaded(MOD_TAN) || Platform.isModLoaded(MOD_LSO);
    }

    private static void initIfNeeded()
    {
        if (initDone) return;
        initDone = true;

        if (Platform.isModLoaded(MOD_TAN))
        {
            try
            {
                Class<?> thirstHelper = Class.forName("toughasnails.api.thirst.ThirstHelper");
                tanGetThirstData = thirstHelper.getMethod("getThirst", Player.class);
                Class<?> iThirst = Class.forName("toughasnails.api.thirst.IThirst");
                tanThirstGetThirst = iThirst.getMethod("getThirst");

                Class<?> tempHelper = Class.forName("toughasnails.api.temperature.TemperatureHelper");
                tanGetTempData = tempHelper.getMethod("getTemperatureData", Player.class);
                Class<?> iTemp = Class.forName("toughasnails.api.temperature.ITemperature");
                tanTempGetLevel = iTemp.getMethod("getLevel");

                tanReady = true;
                SanityMod.LOGGER.info("Sanity: ToughAsNails compat enabled");
            }
            catch (Throwable t)
            {
                SanityMod.LOGGER.info("Sanity: ToughAsNails detected but API surface differs; compat disabled ({})", t.toString());
            }
        }

        if (Platform.isModLoaded(MOD_LSO))
        {
            try
            {
                Class<?> attachmentType = Class.forName("net.neoforged.neoforge.attachment.AttachmentType");
                Class<?> attachmentHolder = Class.forName("net.neoforged.neoforge.attachment.IAttachmentHolder");
                lsoPlayerGetData = attachmentHolder.getMethod("getData", attachmentType);

                Class<?> modAttachments = Class.forName("sfiomn.legendarysurvivaloverhaul.common.attachments.ModAttachments");
                lsoTemperatureAttachmentSupplier = (Supplier<?>) modAttachments.getField("TEMPERATURE").get(null);
                lsoThirstAttachmentSupplier = (Supplier<?>) modAttachments.getField("THIRST").get(null);

                Class<?> iTempAttach = Class.forName("sfiomn.legendarysurvivaloverhaul.api.temperature.ITemperatureAttachment");
                lsoTempGetEnum = iTempAttach.getMethod("getTemperatureEnum");

                Class<?> iThirstAttach = Class.forName("sfiomn.legendarysurvivaloverhaul.api.thirst.IThirstAttachment");
                lsoThirstGetHydration = iThirstAttach.getMethod("getHydrationLevel");

                lsoReady = true;
                SanityMod.LOGGER.info("Sanity: LegendarySurvivalOverhaul compat enabled (NeoForge attachments)");
            }
            catch (Throwable t)
            {
                SanityMod.LOGGER.info("Sanity: LegendarySurvivalOverhaul detected but API surface differs; compat disabled ({})", t.toString());
            }
        }
    }

    public static OptionalInt getThirst(ServerPlayer player)
    {
        initIfNeeded();
        if (tanReady)
        {
            try
            {
                Object data = tanGetThirstData.invoke(null, player);
                if (data != null)
                {
                    Object v = tanThirstGetThirst.invoke(data);
                    if (v instanceof Integer i) return OptionalInt.of(i);
                }
            }
            catch (Throwable ignored) {}
        }
        if (lsoReady)
        {
            try
            {
                Object attachmentType = lsoThirstAttachmentSupplier.get();
                if (attachmentType != null)
                {
                    Object thirstAttach = lsoPlayerGetData.invoke(player, attachmentType);
                    if (thirstAttach != null)
                    {
                        Object v = lsoThirstGetHydration.invoke(thirstAttach);
                        if (v instanceof Integer i) return OptionalInt.of(i);
                    }
                }
            }
            catch (Throwable ignored) {}
        }
        return OptionalInt.empty();
    }

    public static int getTemperatureLevel(ServerPlayer player)
    {
        initIfNeeded();
        if (tanReady)
        {
            try
            {
                Object data = tanGetTempData.invoke(null, player);
                if (data != null)
                {
                    Object lvl = tanTempGetLevel.invoke(data);
                    if (lvl != null)
                    {
                        return switch (lvl.toString())
                        {
                            case "ICY" -> TEMP_COLD;
                            case "HOT" -> TEMP_HOT;
                            default -> TEMP_NEUTRAL;
                        };
                    }
                }
            }
            catch (Throwable ignored) {}
        }
        if (lsoReady)
        {
            try
            {
                Object attachmentType = lsoTemperatureAttachmentSupplier.get();
                if (attachmentType != null)
                {
                    Object tempAttach = lsoPlayerGetData.invoke(player, attachmentType);
                    if (tempAttach != null)
                    {
                        Object lvl = lsoTempGetEnum.invoke(tempAttach);
                        if (lvl != null)
                        {
                            return switch (lvl.toString())
                            {
                                case "FROSTBITE" -> TEMP_COLD;
                                case "HEAT_STROKE" -> TEMP_HOT;
                                default -> TEMP_NEUTRAL;
                            };
                        }
                    }
                }
            }
            catch (Throwable ignored) {}
        }
        return TEMP_UNAVAILABLE;
    }
}
