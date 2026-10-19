package com.clovermagic.skill;

import java.util.Set;

/** Skill point rules: 1 point per magic level, +1 at levels 5, 10, 15 and +2 at 20 (and every 20 after). */
public final class SkillPoints {
    private SkillPoints() {
    }

    public static int earned(int level) {
        int pts = level;
        for (int m = 5; m <= level; m += 5) {
            pts += (m % 20 == 0) ? 2 : 1;
        }
        return pts;
    }

    public static int spent(SkillTree tree, Set<String> unlocked) {
        int sum = 0;
        for (String id : unlocked) {
            SkillNode n = tree.get(id);
            if (n != null && n.type() != NodeType.ROOT) sum += n.cost();
        }
        return sum;
    }

    public static int available(SkillTree tree, Set<String> unlocked, int level) {
        return earned(level) - spent(tree, unlocked);
    }
}
