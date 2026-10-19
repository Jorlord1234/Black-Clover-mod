package com.clovermagic.skill;

/** A playstyle branch of a tree (shown as a banner / lane). */
public record SkillBranch(String id, String name, String subtitle, boolean locked, int y) {
}
