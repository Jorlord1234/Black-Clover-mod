package com.clovermagic.api;

import com.clovermagic.magic.Spell;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Fired on the NeoForge event bus (server side) around every spell cast. Addons and modpacks can use it to
 * restrict spells (cancel {@link Pre}: no mana, no cooldown, no XP is spent) or to react to them ({@link Post}).
 */
public abstract class SpellCastEvent extends PlayerEvent {
    private final Spell spell;

    protected SpellCastEvent(ServerPlayer player, Spell spell) {
        super(player);
        this.spell = spell;
    }

    public Spell getSpell() {
        return spell;
    }

    /** Before the spell takes effect. Cancel it to stop the cast. */
    public static class Pre extends SpellCastEvent implements ICancellableEvent {
        public Pre(ServerPlayer player, Spell spell) {
            super(player, spell);
        }
    }

    /** After a successful cast (mana and cooldown already spent). */
    public static class Post extends SpellCastEvent {
        private final double power;

        public Post(ServerPlayer player, Spell spell, double power) {
            super(player, spell);
            this.power = power;
        }

        public double getPower() {
            return power;
        }
    }
}
