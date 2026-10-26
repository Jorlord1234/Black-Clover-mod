package com.clovermagic.magic;

import com.clovermagic.ModAttachments;
import com.clovermagic.ModItems;
import com.clovermagic.item.MissionScrollItem;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Missions from the Mission Board: a Mission Scroll asks you to defeat some monsters. When you finish, the scroll is
 * paid out (Mana Crystals, Mana Potions, magic experience and vanilla experience). One mission at a time.
 */
public final class Missions {
    private Missions() {
    }

    /** Creates a mission scroll that fits the player's magic level. */
    public static ItemStack create(RandomSource r, int level, boolean squadMission) {
        String type;
        String name;
        int need;
        if (level < 5) {
            type = "clovermagic:magic_boar"; name = "Magic Boar"; need = 6;
        } else if (level < 10) {
            if (r.nextBoolean()) { type = "clovermagic:midnight_mage"; name = "Midnight Sun Mage"; need = 4; }
            else { type = "clovermagic:magic_boar"; name = "Magic Boar"; need = 12; }
        } else if (level < 15) {
            if (r.nextBoolean()) { type = "clovermagic:midnight_mage"; name = "Midnight Sun Mage"; need = 8; }
            else { type = "clovermagic:demon"; name = "Demon"; need = 1; }
        } else {
            switch (r.nextInt(3)) {
                case 0 -> { type = "clovermagic:demon"; name = "Demon"; need = 2; }
                case 1 -> { type = "clovermagic:devil"; name = "Devil"; need = 1; }
                default -> { type = "clovermagic:yultim_spider"; name = "Giant Spider"; need = 1; }
            }
        }
        if (squadMission) need = (int) Math.ceil(need * 1.5);
        int tier = Math.max(1, level / 5);
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("squad", squadMission);
        tag.putString("type", type);
        tag.putString("name", name);
        tag.putInt("need", need);
        tag.putInt("have", 0);
        int mult = squadMission ? 2 : 1;
        tag.putInt("xp", (40 * tier + 20 * need) * mult);
        tag.putInt("crystals", (2 + tier * 2) * mult);
        tag.putInt("potions", tier * mult);
        ItemStack stack = new ItemStack(ModItems.MISSION_SCROLL.get());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    public static boolean hasMission(ServerPlayer sp) {
        for (ItemStack s : sp.getInventory().items) if (s.getItem() instanceof MissionScrollItem) return true;
        return false;
    }

    /** Called when a player kills something: counts it on a matching scroll and pays out finished missions. */
    public static void onKill(ServerPlayer sp, Entity victim) {
        String id = EntityType.getKey(victim.getType()).toString();
        for (ItemStack s : sp.getInventory().items) {
            if (!(s.getItem() instanceof MissionScrollItem)) continue;
            CustomData data = s.get(DataComponents.CUSTOM_DATA);
            if (data == null) continue;
            CompoundTag tag = data.copyTag();
            if (!tag.getString("type").equals(id)) continue;
            int have = tag.getInt("have") + 1;
            tag.putInt("have", have);
            int need = tag.getInt("need");
            if (have >= need) {
                payOut(sp, tag);
                s.setCount(0);
            } else {
                s.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                sp.displayClientMessage(Component.literal("Mission: " + tag.getString("name") + " " + have + "/" + need)
                        .withStyle(ChatFormatting.GOLD), true);
            }
            return;
        }
    }

    private static void payOut(ServerPlayer sp, CompoundTag tag) {
        MagicData d = sp.getData(ModAttachments.MAGIC.get());
        int xp = (int) (tag.getInt("xp") * (1.0 + SquadLogic.bonus(d, SquadLogic.Kind.MISSION_XP)));
        if (tag.getBoolean("squad") && d.squad != null && !d.squad.isEmpty()) {
            d.squadPoints += 2;
            sp.sendSystemMessage(Component.literal("Squad points +2 (" + d.squadPoints + "), squad rank " + SquadLogic.rank(d.squadPoints) + ".").withStyle(ChatFormatting.AQUA));
            StatLogic.apply(sp, d);
        } else if (d.squad != null && !d.squad.isEmpty()) {
            d.squadPoints += 1;
            StatLogic.apply(sp, d);
        }
        sp.getInventory().placeItemBackInInventory(new ItemStack(ModItems.MANA_CRYSTAL.get(), tag.getInt("crystals")));
        sp.getInventory().placeItemBackInInventory(new ItemStack(ModItems.MANA_POTION.get(), tag.getInt("potions")));
        sp.giveExperiencePoints(xp);
        MagicSync.send(sp);
        if (d.leaves > 0) {
            d.xp += xp;
            QuestHooks.awardLevels(sp, d.level());
            MagicSync.send(sp);
        }
        QuestHooks.award(sp, "first_mission");
        sp.sendSystemMessage(Component.literal("Mission complete! Defeated " + tag.getInt("need") + " x " + tag.getString("name")
                + ". Reward: " + tag.getInt("crystals") + " Mana Crystals, " + tag.getInt("potions") + " Mana Potions and " + xp + " experience.")
                .withStyle(ChatFormatting.GREEN));
        SpellUtil.sound(sp, com.clovermagic.ModSounds.LEVEL_UP.get(), 1.0f, 1.2f);
    }
}
