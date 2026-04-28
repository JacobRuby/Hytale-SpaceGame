package dev.jacobruby.hytale.spacegame.incursion.ore;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.resource.IncrementalLootPool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MineableEntityComponent implements Component<EntityStore> {

//    public static final BuilderCodec<MineableEntityComponent> CODEC = BuilderCodec.builder(MineableEntityComponent.class, MineableEntityComponent::new).build();

   public static ComponentType<EntityStore, MineableEntityComponent> getComponentType() {
      return Core.get().getMineableEntityComponentType();
   }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new MineableEntityComponent();
    }
}
