package dev.jacobruby.hytale.spacegame.incursion.generation;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.asset.MarkerType;
import dev.jacobruby.hytale.spacegame.asset.component.MarkerComponent;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MarkerSystem extends RefSystem<EntityStore> {
    @Override
    public void onEntityAdded(@NonNull Ref<EntityStore> ref, @NonNull AddReason reason, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        MarkerComponent marker = store.getComponent(ref, MarkerComponent.getComponentType());
        MarkerType type = marker.getType();

        Core.info().log("Marker spawned of type %s", type);
    }

    @Override
    public void onEntityRemove(@NonNull Ref<EntityStore> ref, @NonNull RemoveReason reason, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {

    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return MarkerComponent.getComponentType();
    }
}
