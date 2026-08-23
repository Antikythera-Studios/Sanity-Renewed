package guivnf.sanity_renewed;

import guivnf.sanity_renewed.capability.IPassiveSanity;
import guivnf.sanity_renewed.capability.IPersistentSanity;
import guivnf.sanity_renewed.capability.ISanity;
import guivnf.sanity_renewed.capability.Sanity;
import guivnf.sanity_renewed.capability.SanityHolder;
import guivnf.sanity_renewed.config.ConfigBrokenBlock;
import guivnf.sanity_renewed.config.ConfigBrokenBlockCategory;
import guivnf.sanity_renewed.config.ConfigItem;
import guivnf.sanity_renewed.config.ConfigItemCategory;
import guivnf.sanity_renewed.config.ConfigProxy;
import guivnf.sanity_renewed.item.ItemRegistry;
import guivnf.sanity_renewed.net.PacketHandler;
import guivnf.sanity_renewed.passive.BlockStuck;
import guivnf.sanity_renewed.passive.Darkness;
import guivnf.sanity_renewed.passive.DirtPath;
import guivnf.sanity_renewed.passive.EnderManAnger;
import guivnf.sanity_renewed.passive.Hungry;
import guivnf.sanity_renewed.passive.IPassiveSanitySource;
import guivnf.sanity_renewed.passive.InWaterOrRain;
import guivnf.sanity_renewed.passive.Jukebox;
import guivnf.sanity_renewed.passive.Lightness;
import guivnf.sanity_renewed.passive.Monster;
import guivnf.sanity_renewed.passive.Passive;
import guivnf.sanity_renewed.passive.PassiveBlocks;
import guivnf.sanity_renewed.passive.Pet;
import guivnf.sanity_renewed.passive.PlayerCompany;
import guivnf.sanity_renewed.passive.TemperatureExtreme;
import guivnf.sanity_renewed.passive.Thirsty;
import guivnf.sanity_renewed.util.MathHelper;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class SanityProcessor
{
    private static final RandomSource RAND = RandomSource.create();

    public static final int MAX_GARLAND_TIMER = 60;
    public static final float SANITY_TARGET_THRESHOLD = .87f;
    public static final List<IPassiveSanitySource> PASSIVE_SANITY_SOURCES = Arrays.asList(
            new Passive(),
            new InWaterOrRain(),
            new Hungry(),
            new EnderManAnger(),
            new Pet(),
            new Monster(),
            new Darkness(),
            new Lightness(),
            new PassiveBlocks(),
            new PlayerCompany(),
            new Jukebox(),
            new BlockStuck(),
            new DirtPath(),
            new Thirsty(),
            new TemperatureExtreme()
    );

    private SanityProcessor() {}

    private static ResourceLocation dimOf(ServerPlayer player)
    {
        return player.level().dimension().location();
    }

    private static float calcPassive(ServerPlayer player, ISanity sanity)
    {
        ResourceLocation dim = dimOf(player);
        float passive = 0;

        for (IPassiveSanitySource pss : PASSIVE_SANITY_SOURCES)
        {
            float val = pss.get(player, sanity, dim);
            val *= getSanityMultiplier(player, val);
            passive += val;
        }

        ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.is(ItemRegistry.GARLAND.get()) && sanity instanceof IPersistentSanity ps)
        {
            passive -= .00005 * ConfigProxy.getPosMul(dim);

            int timer = ps.getGarlandTimer() - 1;
            if (timer <= 0)
            {
                headItem.hurtAndBreak(player.isInWaterOrRain() ? 2 : 1, player, EquipmentSlot.HEAD);
                timer = MAX_GARLAND_TIMER;
            }
            ps.setGarlandTimer(timer);
        }

        return passive;
    }

    private static void shareSanity(ServerPlayer player, Sanity cap)
    {
        if (cap.getDirty())
        {
            PacketHandler.sendSanityToPlayer(player, cap);
            cap.setDirty(false);
        }
    }

    public static float getGarlandMultiplier(ServerPlayer player)
    {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ItemRegistry.GARLAND.get()) ? .92f : 1.0f;
    }

    public static float getSanityMultiplier(ServerPlayer player, float value)
    {
        ResourceLocation dim = dimOf(player);
        return value >= 0 ? ConfigProxy.getNegMul(dim) * getGarlandMultiplier(player) : ConfigProxy.getPosMul(dim);
    }

    public static void addSanity(@NotNull ISanity sanity, float value, @NotNull ServerPlayer player)
    {
        if (value == 0.0f)
            return;
        sanity.setSanity(sanity.getSanity() + value * getSanityMultiplier(player, value));
    }

    public static void tickPlayer(final ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator())
            return;

        Sanity s = SanityHolder.get(player);
        if (s == null) return;

        float passive = calcPassive(player, s);
        float snapshot = s.getSanity();
        s.setSanity(s.getSanity() + passive);
        s.setPassiveIncrease(snapshot != s.getSanity() ? passive : 0);

        int[] cds = s.getActiveSourcesCooldowns();
        for (int i = 0; i < cds.length; ++i)
            cds[i] = Mth.clamp(cds[i] - 1, 0, Integer.MAX_VALUE);

        decay(s.getItemCooldowns());
        decay(s.getBrokenBlocksCooldowns());

        shareSanity(player, s);

        guivnf.sanity_renewed.entity.InnerEntitySpawner.trySpawnForPlayer(player);
    }

    private static void decay(Map<Integer, Integer> map)
    {
        for (Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<Integer, Integer> entry = it.next();
            int v = entry.getValue() - 1;
            if (v <= 0) it.remove();
            else entry.setValue(v);
        }
    }

    public static void tickLevel(final ServerLevel level)
    {
        // InnerEntity tracking is deferred to Phase I.
    }

    public static List<Player> getInsanePlayersInArea(final Level levelIn, BlockPos center, int blockRadius)
    {
        if (levelIn == null || center == null)
            return null;
        List<Player> list = new ArrayList<>();
        for (Player player : levelIn.getEntitiesOfClass(
                Player.class,
                AABB.encapsulatingFullBlocks(center.offset(blockRadius, blockRadius, blockRadius), center.offset(-blockRadius, -blockRadius, -blockRadius))))
        {
            Sanity s = SanityHolder.get(player);
            if (s != null && s.getSanity() >= SANITY_TARGET_THRESHOLD)
                list.add(player);
        }
        return list;
    }

    public static Player getMostInsanePlayer(final Level levelIn)
    {
        return getMostInsanePlayer(levelIn, SANITY_TARGET_THRESHOLD);
    }

    public static Player getMostInsanePlayer(final Level levelIn, float sanityThreshold)
    {
        if (levelIn == null) return null;
        Player toReturn = null;
        float maxSanity = Float.MIN_VALUE;
        for (Player player : levelIn.players())
        {
            if (player.isCreative() || player.isSpectator())
                continue;
            Sanity s = SanityHolder.get(player);
            if (s == null) continue;
            float sanity = s.getSanity();
            if (sanity >= sanityThreshold && sanity > maxSanity)
            {
                maxSanity = sanity;
                toReturn = player;
            }
        }
        return toReturn;
    }

    public static void handleActiveSourceForPlayer(
            ServerPlayer player,
            int id,
            Function<ResourceLocation, Integer> cdSupplier,
            Function<ResourceLocation, Float> sanitySupplier)
    {
        if (player == null || player.isCreative() || player.isSpectator())
            return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;

        ResourceLocation dim = dimOf(player);
        int cd = cdSupplier.apply(dim);

        if (cd <= 0)
        {
            addSanity(s, sanitySupplier.apply(dim), player);
            return;
        }

        int[] cds = s.getActiveSourcesCooldowns();
        int timePassed = cd - cds[id];
        addSanity(s, sanitySupplier.apply(dim) * MathHelper.clampNorm((float) timePassed / cd), player);
        cds[id] = cd;
    }

    public static void handlePlayerSlept(ServerLevel level)
    {
        for (ServerPlayer player : level.players())
        {
            if (player.isCreative() || player.isSpectator())
                continue;
            if (!player.isSleeping())
                continue;
            handleActiveSourceForPlayer(player, ActiveSanitySources.SLEEPING, ConfigProxy::getSleepingCooldown, ConfigProxy::getSleeping);
        }
    }

    public static void handlePlayerHurt(ServerPlayer player, float amount)
    {
        if (player == null || player.isCreative() || player.isSpectator() || amount <= 0) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;
        addSanity(s, amount * ConfigProxy.getHurtRatio(dimOf(player)), player);
    }

    public static void handlePlayerHurtAnimal(ServerPlayer player, Animal animal, float amount)
    {
        if (player == null || player.isCreative() || player.isSpectator() || amount <= 0) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;
        addSanity(s, amount * ConfigProxy.getAnimalHurtRatio(dimOf(player)) * (animal.isBaby() ? 2.0f : 1.0f), player);
    }

    public static void handlePlayerPetDeath(ServerPlayer player, TamableAnimal pet)
    {
        if (player == null || player.isCreative() || player.isSpectator() || !pet.isOwnedBy(player)) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;
        addSanity(s, ConfigProxy.getPetDeath(dimOf(player)), player);
    }

    public static void handlePlayerEnderManAngered(ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;
        if (s.getEnderManAngerTimer() <= 0)
            s.setEnderManAngerTimer(100);
    }

    public static void handlePlayerGotAdvancement(ServerPlayer player, AdvancementHolder holder)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        var display = holder.value().display();
        if (display.isEmpty() || !display.get().shouldAnnounceChat()) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;
        addSanity(s, ConfigProxy.getAdvancement(dimOf(player)), player);
    }

    public static void handlePlayerBredAnimals(ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        handleActiveSourceForPlayer(player, ActiveSanitySources.BREEDING_ANIMALS, ConfigProxy::getAnimalBreedingCooldown, ConfigProxy::getAnimalBreeding);
    }

    public static void handlePlayerTradedWithVillager(ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        handleActiveSourceForPlayer(player, ActiveSanitySources.VILLAGER_TRADE, ConfigProxy::getVillagerTradeCooldown, ConfigProxy::getVillagerTrade);
    }

    public static void handlePlayerUsedShears(ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        handleActiveSourceForPlayer(player, ActiveSanitySources.SHEARING, ConfigProxy::getShearingCooldown, ConfigProxy::getShearing);
    }

    public static void handlePlayerSpawnedChicken(ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        handleActiveSourceForPlayer(player, ActiveSanitySources.SPAWNING_BABY_CHICKEN, ConfigProxy::getBabyChickenSpawningCooldown, ConfigProxy::getBabyChickenSpawning);
    }

    public static void handlePlayerAte(ServerPlayer player, ItemStack itemStack)
    {
        handleActiveSourceForPlayer(
                player,
                ActiveSanitySources.EATING,
                ConfigProxy::getEatingCooldown,
                dim -> {
                    var fp = itemStack.get(net.minecraft.core.component.DataComponents.FOOD);
                    if (fp == null) return 0f;
                    float base = fp.nutrition() * ConfigProxy.getEating(dim);
                    return isUnhealthyFood(fp) ? -base : base;
                });
    }

    private static boolean isUnhealthyFood(net.minecraft.world.food.FoodProperties fp)
    {
        for (var pair : fp.effects())
        {
            var effect = pair.effect();
            if (effect == null) continue;
            if (effect.getEffect().value().getCategory() == net.minecraft.world.effect.MobEffectCategory.HARMFUL)
                return true;
        }
        return false;
    }

    public static void handlePlayerUsedItem(ServerPlayer player, ItemStack itemStack)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;

        ResourceLocation dim = dimOf(player);
        for (ConfigItem citem : ConfigProxy.getItems(dim))
        {
            Item ref = BuiltInRegistries.ITEM.get(citem.m_name);
            if (ref == null || !itemStack.is(ref))
                continue;

            if (!ConfigProxy.getIdToItemCat(dim).containsKey(citem.m_cat))
            {
                SanityMod.LOGGER.warn("player {} used {} from category {}, but no such category is present",
                        player.getDisplayName().getString(), citem.m_name, citem.m_cat);
                return;
            }

            ConfigItemCategory cat = ConfigProxy.getIdToItemCat(dim).get(citem.m_cat);
            if (cat.m_cd <= 0)
            {
                addSanity(s, citem.m_sanity, player);
                return;
            }

            Map<Integer, Integer> itemCds = s.getItemCooldowns();
            if (!itemCds.containsKey(citem.m_cat) || itemCds.get(citem.m_cat) <= 0)
            {
                addSanity(s, citem.m_sanity, player);
            }
            else
            {
                int timePassed = cat.m_cd - itemCds.get(citem.m_cat);
                addSanity(s, citem.m_sanity * MathHelper.clampNorm((float) timePassed / cat.m_cd), player);
            }
            itemCds.put(citem.m_cat, cat.m_cd);
            return;
        }

        if (itemStack.has(net.minecraft.core.component.DataComponents.FOOD))
            handlePlayerAte(player, itemStack);
    }

    public static void handlePlayerFishedItem(ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        handleActiveSourceForPlayer(player, ActiveSanitySources.FISHING, ConfigProxy::getFishingCooldown, ConfigProxy::getFishing);
    }

    public static void handlePlayerMinedBlock(ServerPlayer player, BlockPos blockPos, BlockState blockState, Block block, boolean correctTool)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;

        ResourceLocation dim = dimOf(player);
        for (ConfigBrokenBlock cbblock : ConfigProxy.getBrokenBlocks(dim))
        {
            boolean tagMatch = cbblock.m_isTag && blockState.getTags().anyMatch(tag -> tag.location().equals(cbblock.m_name));
            boolean blockMatch = !cbblock.m_isTag && block.equals(BuiltInRegistries.BLOCK.get(cbblock.m_name));
            if (!(tagMatch || blockMatch))
                continue;

            if (cbblock.m_toolRequired && !correctTool)
                return;

            if (!ConfigProxy.getIdToBrokenBlockCat(dim).containsKey(cbblock.m_cat))
            {
                SanityMod.LOGGER.warn("player {} mined {} from category {}, but no such category is present",
                        player.getDisplayName().getString(), cbblock.m_name, cbblock.m_cat);
                return;
            }

            // naturallyGend tracking via SanityLevelChunk is deferred — currently treated as natural.

            ConfigBrokenBlockCategory cat = ConfigProxy.getIdToBrokenBlockCat(dim).get(cbblock.m_cat);
            if (cat.m_cd <= 0)
            {
                addSanity(s, cbblock.m_sanity, player);
                return;
            }

            Map<Integer, Integer> brokenBlockCds = s.getBrokenBlocksCooldowns();
            if (!brokenBlockCds.containsKey(cbblock.m_cat) || brokenBlockCds.get(cbblock.m_cat) <= 0)
            {
                addSanity(s, cbblock.m_sanity, player);
            }
            else
            {
                int timePassed = cat.m_cd - brokenBlockCds.get(cbblock.m_cat);
                addSanity(s, cbblock.m_sanity * MathHelper.clampNorm((float) timePassed / cat.m_cd), player);
            }
            brokenBlockCds.put(cbblock.m_cat, cat.m_cd);
            return;
        }
    }

    public static void handlePlayerTrampledFarmland(ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;
        addSanity(s, ConfigProxy.getFarmlandTrample(dimOf(player)), player);
    }

    public static void handlePlayerPottedFlower(ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        handleActiveSourceForPlayer(player, ActiveSanitySources.POTTING_FLOWER, ConfigProxy::getPottingFlowerCooldown, ConfigProxy::getPottingFlower);
    }

    public static void handlePlayerChangedDimensions(ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;
        addSanity(s, ConfigProxy.getChangedDimension(dimOf(player)), player);
    }

    public static void handlePlayerStruckByLightning(ServerPlayer player)
    {
        if (player == null || player.isCreative() || player.isSpectator()) return;
        Sanity s = SanityHolder.get(player);
        if (s == null) return;
        addSanity(s, ConfigProxy.getStruckByLightning(dimOf(player)), player);
    }
}
