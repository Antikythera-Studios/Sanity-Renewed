package guivnf.sanity_renewed.compat;

import guivnf.sanity_renewed.SanityMod;
import dev.architectury.platform.Platform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Reflection-only bridge to ToughAsNails / LegendarySurvivalOverhaul.
 * Returns "unavailable" if neither mod is installed, or if the expected API
 * surface is not present at runtime — callers must treat absence as a no-op.
 *
 * <p>Forge capability classes are only touched inside the LSO init branch,
 * which is gated on Architectury's mod-loaded check. LSO is Forge-only,
 * so this branch never executes on Fabric.</p>
 */
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
    // Both temperature and thirst are read via Forge capabilities on LSO.
    // getPlayerTargetTemperature() is the *environmental target*, not the body temp,
    // so reading the actual current body temp via ITemperatureCapability.getTemperatureEnum() is required.
    private static Object lsoThirstCapability;        // Capability<?> static field value
    private static Object lsoTemperatureCapability;   // Capability<?> static field value
    private static Method lsoPlayerGetCapability;     // Player.getCapability(Capability)
    private static Method lsoLazyResolve;             // LazyOptional.resolve()
    private static Method lsoThirstGetHydration;      // IThirstCapability.getHydrationLevel()
    private static Method lsoTempGetEnum;             // ITemperatureCapability.getTemperatureEnum()

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
                Class<?> capabilityCls = Class.forName("net.minecraftforge.common.capabilities.Capability");
                lsoPlayerGetCapability = Player.class.getMethod("getCapability", capabilityCls);

                Class<?> lazyOpt = Class.forName("net.minecraftforge.common.util.LazyOptional");
                lsoLazyResolve = lazyOpt.getMethod("resolve");

                Class<?> thirstProvider = Class.forName("sfiomn.legendarysurvivaloverhaul.common.capabilities.thirst.ThirstProvider");
                lsoThirstCapability = thirstProvider.getField("THIRST_CAPABILITY").get(null);

                Class<?> iThirstCap = Class.forName("sfiomn.legendarysurvivaloverhaul.api.thirst.IThirstCapability");
                lsoThirstGetHydration = iThirstCap.getMethod("getHydrationLevel");

                Class<?> tempProvider = Class.forName("sfiomn.legendarysurvivaloverhaul.common.capabilities.temperature.TemperatureProvider");
                lsoTemperatureCapability = tempProvider.getField("TEMPERATURE_CAPABILITY").get(null);

                Class<?> iTempCap = Class.forName("sfiomn.legendarysurvivaloverhaul.api.temperature.ITemperatureCapability");
                lsoTempGetEnum = iTempCap.getMethod("getTemperatureEnum");

                lsoReady = true;
                SanityMod.LOGGER.info("Sanity: LegendarySurvivalOverhaul compat enabled");
            }
            catch (Throwable t)
            {
                SanityMod.LOGGER.info("Sanity: LegendarySurvivalOverhaul detected but API surface differs; compat disabled ({})", t.toString());
            }
        }
    }

    /** Hydration remaining (0..20), or empty if no compat mod provides the data. */
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
                Object lazy = lsoPlayerGetCapability.invoke(player, lsoThirstCapability);
                if (lazy != null)
                {
                    Object optObj = lsoLazyResolve.invoke(lazy);
                    if (optObj instanceof Optional<?> opt && opt.isPresent())
                    {
                        Object cap = opt.get();
                        Object v = lsoThirstGetHydration.invoke(cap);
                        if (v instanceof Integer i) return OptionalInt.of(i);
                    }
                }
            }
            catch (Throwable ignored) {}
        }
        return OptionalInt.empty();
    }

    /** {@link #TEMP_HOT}, {@link #TEMP_NEUTRAL}, {@link #TEMP_COLD}, or {@link #TEMP_UNAVAILABLE}. */
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
                        // TAN's TemperatureLevel ladder is ICY/COLD/NEUTRAL/WARM/HOT.
                        // Only the extremes (ICY / HOT) count as "extreme" for sanity purposes;
                        // mild deviations (COLD / WARM) are normal and ignored.
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
                Object lazy = lsoPlayerGetCapability.invoke(player, lsoTemperatureCapability);
                if (lazy != null)
                {
                    Object optObj = lsoLazyResolve.invoke(lazy);
                    if (optObj instanceof Optional<?> opt && opt.isPresent())
                    {
                        Object cap = opt.get();
                        Object lvl = lsoTempGetEnum.invoke(cap);
                        if (lvl != null)
                        {
                            // LSO body-temperature ladder: FROSTBITE / COLD / NORMAL / HOT / HEAT_STROKE.
                            // Only FROSTBITE and HEAT_STROKE are extreme — being in a cold/hot biome
                            // or near warm blocks only nudges the target, not the body temp.
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
