package com.clovermagic.skill;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SkillTree {
    public final String attributeId;
    public final String title;
    public final String rootId;
    public final List<SkillBranch> branches;
    public final List<SkillNode> nodes;
    private final Map<String, SkillNode> byId = new LinkedHashMap<>();

    public SkillTree(String attributeId, String title, String rootId, List<SkillBranch> branches, List<SkillNode> nodes) {
        this.attributeId = attributeId;
        this.title = title;
        this.rootId = rootId;
        this.branches = branches;
        this.nodes = nodes;
        for (SkillNode n : nodes) byId.put(n.id(), n);
    }

    public SkillNode get(String id) {
        return byId.get(id);
    }

    public SkillBranch branch(String id) {
        for (SkillBranch b : branches) if (b.id().equals(id)) return b;
        return null;
    }

    public List<SkillNode> nodesOfBranch(String branchId) {
        List<SkillNode> out = new ArrayList<>();
        for (SkillNode n : nodes) if (n.branch().equals(branchId)) out.add(n);
        return out;
    }
}
