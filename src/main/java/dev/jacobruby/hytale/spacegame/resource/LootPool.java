package dev.jacobruby.hytale.spacegame.resource;

public class LootPool {

    protected ResourceEntry[] entries;

    public LootPool(ResourceEntry... entries) {
        this.entries = entries;
    }

    public ResourceEntry[] getEntries() {
        return this.entries.clone();
    }
}
