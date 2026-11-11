package net.modcrafters.minecraft.feastdominion.group;

import net.modcrafters.minecraft.feastdominion.wellness.FoodGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Fills in cooked and mixed foods from the raw groups and the loaded recipes. */
public final class GroupInference {
    private GroupInference() {
    }

    public record Part(GroupShares shares, int weight) {
    }

    public record Slot(List<String> items, List<String> tags) {
    }

    public record Recipe(String id, List<Slot> inputs, List<String> results, String type) {
    }

    public static GroupShares mix(List<Part> parts) {
        double total = 0.0;
        double[] sums = new double[FoodGroup.values().length];
        for (Part part : parts) {
            if (part.weight() <= 0 || part.shares() == null) {
                continue;
            }
            total += part.weight();
            for (FoodGroup group : FoodGroup.values()) {
                sums[group.ordinal()] += part.weight() * part.shares().get(group);
            }
        }
        if (total <= 0.0) {
            return GroupShares.EMPTY;
        }
        float[] values = new float[sums.length];
        for (int i = 0; i < sums.length; i++) {
            values[i] = (float) (sums[i] / total);
        }
        return GroupShares.from(values);
    }

    public static Map<String, GroupShares> infer(
            Map<String, GroupShares> authored,
            List<Recipe> recipes,
            Map<String, List<String>> tags,
            Map<String, Integer> hunger
    ) {
        Map<String, GroupShares> known = new HashMap<>(authored);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, GroupShares> found : resolve(recipes, known, tags, hunger, false).entrySet()) {
                if (known.containsKey(found.getKey())) {
                    continue;
                }
                known.put(found.getKey(), found.getValue());
                changed = true;
            }
        }
        changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, GroupShares> found : resolve(recipes, known, tags, hunger, true).entrySet()) {
                if (known.putIfAbsent(found.getKey(), found.getValue()) == null) {
                    changed = true;
                }
            }
        }
        return known;
    }

    private static Map<String, GroupShares> resolve(
            List<Recipe> recipes,
            Map<String, GroupShares> known,
            Map<String, List<String>> tags,
            Map<String, Integer> hunger,
            boolean finish
    ) {
        Map<String, Candidate> best = new HashMap<>();
        for (Recipe recipe : recipes) {
            Mix mixed = mix(recipe, known, tags, hunger, finish);
            if (mixed == null) {
                continue;
            }
            Candidate next = new Candidate(recipe.id(), mixed.shares(), mixed.parts());
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
        Map<String, GroupShares> found = new HashMap<>();
        for (Map.Entry<String, Candidate> entry : best.entrySet()) {
            found.put(entry.getKey(), entry.getValue().shares());
        }
        return found;
    }

    private static Mix mix(
            Recipe recipe,
            Map<String, GroupShares> known,
            Map<String, List<String>> tags,
            Map<String, Integer> hunger,
            boolean finish
    ) {
        List<Part> parts = new ArrayList<>();
        for (Slot slot : recipe.inputs()) {
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
            int weight = GroupWeight.of(hunger.getOrDefault(standIn, 0), true);
            if (weight <= 0) {
                continue;
            }
            parts.add(new Part(known.get(standIn), weight));
        }
        if (parts.isEmpty()) {
            return null;
        }
        return new Mix(mix(parts), parts.size());
    }

    private static List<String> candidates(Slot slot, Map<String, List<String>> tags) {
        List<String> ids = new ArrayList<>(slot.items());
        for (String tag : slot.tags()) {
            ids.addAll(tags.getOrDefault(tag, List.of()));
        }
        return ids;
    }

    private record Mix(GroupShares shares, int parts) {
    }

    private record Candidate(String id, GroupShares shares, int parts) {
        private boolean plainest(Candidate other) {
            if (this.parts != other.parts) {
                return this.parts < other.parts;
            }
            return this.id.compareTo(other.id) < 0;
        }
    }
}
