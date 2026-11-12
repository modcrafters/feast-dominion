package net.modcrafters.minecraft.feastdominion.taste;

import net.modcrafters.minecraft.feastdominion.group.GroupInference;
import net.modcrafters.minecraft.feastdominion.group.GroupTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Fills in mixed and cut foods from the tastes on their ingredients. */
public final class TasteInference {
    private TasteInference() {
    }

    public static Map<String, TasteProfile> infer(
            Map<String, TasteProfile> authored,
            List<GroupInference.Recipe> recipes,
            Map<String, List<String>> tags
    ) {
        Map<String, TasteProfile> known = new HashMap<>(authored);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, TasteProfile> found : resolve(recipes, known, tags, false).entrySet()) {
                if (known.putIfAbsent(found.getKey(), found.getValue()) == null) {
                    changed = true;
                }
            }
        }
        changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, TasteProfile> found : resolve(recipes, known, tags, true).entrySet()) {
                if (known.putIfAbsent(found.getKey(), found.getValue()) == null) {
                    changed = true;
                }
            }
        }
        return known;
    }

    /**
     * An item file wins. A narrower tag replaces a broad crop tag for the same member.
     */
    public static Map<String, TasteProfile> authored(
            Map<String, TasteProfile> items,
            Map<String, TasteProfile> tagTastes,
            Map<String, List<String>> tagMembers
    ) {
        return GroupTags.assign(items, tagTastes, tagMembers, GroupTags.BROAD);
    }

    private static Map<String, TasteProfile> resolve(
            List<GroupInference.Recipe> recipes,
            Map<String, TasteProfile> known,
            Map<String, List<String>> tags,
            boolean finish
    ) {
        Map<String, Candidate> best = new HashMap<>();
        for (GroupInference.Recipe recipe : recipes) {
            Mix mixed = mix(recipe, known, tags, finish);
            if (mixed == null) {
                continue;
            }
            Candidate next = new Candidate(recipe.id(), mixed.profile(), mixed.parts());
            for (String result : recipe.results()) {
                if (result.isEmpty() || known.containsKey(result)) {
                    continue;
                }
                Candidate current = best.get(result);
                if (current == null || next.plainest(current)) {
                    best.put(result, next);
                }
            }
        }
        Map<String, TasteProfile> found = new HashMap<>();
        for (Map.Entry<String, Candidate> entry : best.entrySet()) {
            found.put(entry.getKey(), entry.getValue().profile());
        }
        return found;
    }

    private static Mix mix(
            GroupInference.Recipe recipe,
            Map<String, TasteProfile> known,
            Map<String, List<String>> tags,
            boolean finish
    ) {
        List<TasteProfile> parts = new ArrayList<>();
        for (GroupInference.Slot slot : recipe.inputs()) {
            String standIn = null;
            boolean unseen = false;
            for (String id : candidates(slot, tags)) {
                if (known.containsKey(id)) {
                    if (standIn == null || id.compareTo(standIn) < 0) {
                        standIn = id;
                    }
                } else {
                    unseen = true;
                }
            }
            if (standIn == null) {
                if (!finish && unseen) {
                    return null;
                }
                continue;
            }
            parts.add(known.get(standIn));
        }
        if (parts.isEmpty()) {
            return null;
        }
        TasteProfile profile = TasteProfile.cover(parts);
        if (heat(recipe.type())) {
            profile = profile.cook();
        }
        return new Mix(profile, parts.size());
    }

    private static boolean heat(String type) {
        return type.endsWith("smelting")
                || type.endsWith("smoking")
                || type.endsWith("campfire_cooking")
                || type.endsWith("blasting")
                || type.endsWith(":cooking");
    }

    private static List<String> candidates(GroupInference.Slot slot, Map<String, List<String>> tags) {
        List<String> ids = new ArrayList<>(slot.items());
        for (String tag : slot.tags()) {
            ids.addAll(tags.getOrDefault(tag, List.of()));
        }
        return ids;
    }

    private record Mix(TasteProfile profile, int parts) {
    }

    private record Candidate(String id, TasteProfile profile, int parts) {
        private boolean plainest(Candidate other) {
            if (this.parts != other.parts) {
                return this.parts < other.parts;
            }
            return this.id.compareTo(other.id) < 0;
        }
    }
}
