package com.clovermagic;

import com.clovermagic.item.GrimoireItem;
import com.clovermagic.magic.GrimoireManager;
import com.clovermagic.magic.MagicAttribute;
import com.clovermagic.magic.MagicData;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Test commands (op only):
 *   /clover give <3|4|5> <attribute>   gives you a bound grimoire
 *   /clover level <1-10>               sets your magic level
 *   /clover mana                       refills mana
 *   /clover squad <id>|leave            join or leave a squad (test)
 *   /clover stats reset                 gives all stat points back
 *   /clover spirit                      the spirit of your element bonds with you (test)
 *   /clover cutscene <3|4|5> <attribute>  previews the first-grimoire cutscene
 *   /clover reset                      forgets your grimoire so you can redo the ceremony
 */
public final class ModCommands {
    private ModCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        // /devildeal <second_grimoire|fifth_leaf>: needs no permission, it is what the Devil's Contract chat buttons run
        event.getDispatcher().register(Commands.literal("devildeal")
                .then(Commands.argument("kind", StringArgumentType.word()).executes(ctx -> {
                    net.minecraft.server.level.ServerPlayer sp = ctx.getSource().getPlayerOrException();
                    return com.clovermagic.magic.DevilDeals.apply(sp, StringArgumentType.getString(ctx, "kind")) ? 1 : 0;
                })));
        event.getDispatcher().register(Commands.literal("clover")
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("give")
                        .then(Commands.argument("leaves", IntegerArgumentType.integer(3, 5))
                                .then(Commands.argument("attribute", StringArgumentType.word())
                                        .suggests((ctx, b) -> SharedSuggestionProvider.suggest(MagicAttribute.ids(), b))
                                        .executes(ctx -> {
                                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                                            int leaves = IntegerArgumentType.getInteger(ctx, "leaves");
                                            String id = StringArgumentType.getString(ctx, "attribute");
                                            MagicAttribute attr = MagicAttribute.fromId(id);
                                            if (attr == null) {
                                                ctx.getSource().sendFailure(Component.literal("Unknown attribute: " + id));
                                                return 0;
                                            }
                                            if (leaves == 5) attr = MagicAttribute.ANTI_MAGIC;
                                            MagicData d = p.getData(ModAttachments.MAGIC.get());
                                            d.leaves = leaves;
                                            d.attribute = attr.id;
                                            d.selected = 0;
                                            d.mana = GrimoireItem.maxMana(d.level(), leaves);
                                            GrimoireManager.issue(p, d);
                                            ctx.getSource().sendSuccess(() -> Component.translatableWithFallback("msg.clovermagic.gave_a_bound_grimoire_41f6", "Gave a bound grimoire"), false);
                                            return 1;
                                        }))))
                .then(Commands.literal("squad")
                        .then(Commands.literal("leave").executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            MagicData d = p.getData(ModAttachments.MAGIC.get());
                            d.squad = "";
                            com.clovermagic.magic.StatLogic.apply(p, d);
                            com.clovermagic.magic.MagicSync.send(p);
                            return 1;
                        }))
                        .then(Commands.argument("id", StringArgumentType.word())
                                .suggests((ctx, b) -> SharedSuggestionProvider.suggest(com.clovermagic.magic.SquadLogic.SQUADS.keySet(), b))
                                .executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                                    String id = StringArgumentType.getString(ctx, "id");
                                    if (!com.clovermagic.magic.SquadLogic.SQUADS.containsKey(id)) {
                                        ctx.getSource().sendFailure(Component.translatableWithFallback("msg.clovermagic.unknown_squad_cfc8", "Unknown squad"));
                                        return 0;
                                    }
                                    MagicData d = p.getData(ModAttachments.MAGIC.get());
                                    d.squad = id;
                                    com.clovermagic.magic.StatLogic.apply(p, d);
                                    com.clovermagic.magic.MagicSync.send(p);
                                    com.clovermagic.magic.QuestHooks.award(p, "joined_squad");
                                    return 1;
                                })))
                .then(Commands.literal("stats").then(Commands.literal("reset").executes(ctx -> {
                    com.clovermagic.magic.StatLogic.reset(ctx.getSource().getPlayerOrException());
                    return 1;
                })))
                .then(Commands.literal("spirit")
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            MagicData d = p.getData(ModAttachments.MAGIC.get());
                            MagicAttribute a = MagicAttribute.fromId(d.attribute);
                            if (a == null || d.leaves == 0) {
                                ctx.getSource().sendFailure(Component.translatableWithFallback("msg.clovermagic.you_have_no_grimoire_b21f", "You have no grimoire"));
                                return 0;
                            }
                            com.clovermagic.magic.CastHandler.bondSpirit(p, d, a);
                            com.clovermagic.magic.MagicSync.send(p);
                            return 1;
                        }))
                .then(Commands.literal("cutscene")
                        .then(Commands.argument("leaves", IntegerArgumentType.integer(3, 5))
                                .then(Commands.argument("attribute", StringArgumentType.word())
                                        .suggests((ctx, b) -> SharedSuggestionProvider.suggest(MagicAttribute.ids(), b))
                                        .executes(ctx -> {
                                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                                            MagicAttribute attr = MagicAttribute.fromId(StringArgumentType.getString(ctx, "attribute"));
                                            int leaves = IntegerArgumentType.getInteger(ctx, "leaves");
                                            if (attr == null) {
                                                ctx.getSource().sendFailure(Component.translatableWithFallback("msg.clovermagic.unknown_attribute_d739", "Unknown attribute"));
                                                return 0;
                                            }
                                            if (leaves == 5) attr = MagicAttribute.ANTI_MAGIC;
                                            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(p,
                                                    new com.clovermagic.net.CutscenePayload(attr.id, leaves));
                                            return 1;
                                        }))))
                .then(Commands.literal("level")
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 50))
                                .executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                                    MagicData d = p.getData(ModAttachments.MAGIC.get());
                                    int lvl = IntegerArgumentType.getInteger(ctx, "level");
                                    d.xp = MagicData.xpForLevel(lvl);
                                    com.clovermagic.magic.MagicSync.send(p);
                                    int shown = d.level();
                                    ctx.getSource().sendSuccess(() -> Component.literal("Magic level set to " + shown
                                            + (shown < lvl ? " (the maximum, see maxMagicLevel in the config)" : "")), false);
                                    return 1;
                                })))
                .then(Commands.literal("mana")
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            MagicData d = p.getData(ModAttachments.MAGIC.get());
                            d.mana = GrimoireItem.maxMana(d.level(), Math.max(3, d.leaves));
                            ctx.getSource().sendSuccess(() -> Component.translatableWithFallback("msg.clovermagic.mana_refilled_e8e9", "Mana refilled"), false);
                            return 1;
                        }))
                .then(Commands.literal("unlock")
                        .then(Commands.argument("node", StringArgumentType.word())
                                .executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                                    MagicData d = p.getData(ModAttachments.MAGIC.get());
                                    com.clovermagic.skill.SkillTree tree = com.clovermagic.magic.SkillLogic.treeFor(d);
                                    String id = StringArgumentType.getString(ctx, "node");
                                    if (tree == null || tree.get(id) == null) {
                                        ctx.getSource().sendFailure(Component.literal("Unknown node (or you have no skill tree): " + id));
                                        return 0;
                                    }
                                    d.unlocked.add(id);
                                    com.clovermagic.magic.MagicSync.send(p);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Unlocked " + id + " (ignores points and requirements)"), false);
                                    return 1;
                                })))
                .then(Commands.literal("tree")
                        .then(Commands.literal("reset")
                                .executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                                    MagicData d = p.getData(ModAttachments.MAGIC.get());
                                    d.unlocked.clear();
                                    com.clovermagic.magic.MagicSync.send(p);
                                    ctx.getSource().sendSuccess(() -> Component.translatableWithFallback("msg.clovermagic.skill_tree_reset_for_free_c9c1", "Skill tree reset for free"), false);
                                    return 1;
                                })))
                .then(Commands.literal("reset")
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            MagicData d = p.getData(ModAttachments.MAGIC.get());
                            d.leaves = 0;
                            d.attribute = "none";
                            d.xp = 0;
                            d.selected = 0;
                            d.generation++;
                            ctx.getSource().sendSuccess(() -> Component.translatableWithFallback("msg.clovermagic.magic_reset_visit_a_tower_altar_5790", "Magic reset. Visit a tower altar."), false);
                            return 1;
                        })));
    }
}
