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
 *   /clover reset                      forgets your grimoire so you can redo the ceremony
 */
public final class ModCommands {
    private ModCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
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
                                            ctx.getSource().sendSuccess(() -> Component.literal("Gave a bound grimoire"), false);
                                            return 1;
                                        }))))
                .then(Commands.literal("level")
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                                .executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                                    MagicData d = p.getData(ModAttachments.MAGIC.get());
                                    d.xp = MagicData.xpForLevel(IntegerArgumentType.getInteger(ctx, "level"));
                                    ctx.getSource().sendSuccess(() -> Component.literal("Magic level set"), false);
                                    return 1;
                                })))
                .then(Commands.literal("mana")
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            MagicData d = p.getData(ModAttachments.MAGIC.get());
                            d.mana = GrimoireItem.maxMana(d.level(), Math.max(3, d.leaves));
                            ctx.getSource().sendSuccess(() -> Component.literal("Mana refilled"), false);
                            return 1;
                        }))
                .then(Commands.literal("reset")
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            MagicData d = p.getData(ModAttachments.MAGIC.get());
                            d.leaves = 0;
                            d.attribute = "none";
                            d.xp = 0;
                            d.selected = 0;
                            d.generation++;
                            ctx.getSource().sendSuccess(() -> Component.literal("Magic reset. Visit a tower altar."), false);
                            return 1;
                        })));
    }
}
