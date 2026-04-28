package dev.jacobruby.hytale.spacegame.resource;

import java.util.ArrayList;
import java.util.List;

public class IncrementalLootPool extends LootPool {

    private float progress = 0.0f; // From 0.0f to 1.0f

    public IncrementalLootPool(ResourceEntry... entries) {
        super(entries);
    }

    public List<ResourceEntry> submitProgress(float progress) {
        if (this.progress >= 1.0f)
            return List.of();

        float oldProgress = this.progress;
        float newProgress = this.progress += progress;

        List<ResourceEntry> shavedEntries = new ArrayList<>();
        for (ResourceEntry entry : this.entries) {
            int totalCount = entry.count();

            int alreadyGiven = (int) (totalCount * oldProgress);
            int newGiven = (int) (totalCount * newProgress);

            int delta = newGiven - alreadyGiven;

            // I guess we might as well allow for negative progress?
            if (delta == 0)
                continue;

            shavedEntries.add(new ResourceEntry(entry.type(), delta));
        }

        return shavedEntries;
    }

    public float getProgress() {
        return progress;
    }
}
