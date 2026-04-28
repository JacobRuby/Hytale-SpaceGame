package dev.jacobruby.hytale.spacegame.incursion.ore;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.resource.LootPool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LootPoolComponent implements Component<EntityStore> {
    private LootPool lootPool;

    public static ComponentType<EntityStore, LootPoolComponent> getComponentType() {
        return Core.get().getLootPoolComponentType();
    }

    public LootPoolComponent() {
    }

    public LootPoolComponent(@Nonnull LootPool lootPool) {
        this.lootPool = lootPool;
    }

    public LootPool getLootPool() {
        return lootPool;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new LootPoolComponent(this.lootPool);
    }
}
