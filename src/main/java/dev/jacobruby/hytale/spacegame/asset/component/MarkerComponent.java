package dev.jacobruby.hytale.spacegame.asset.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.asset.MarkerType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MarkerComponent implements Component<EntityStore> {
    @Nonnull
    public static final BuilderCodec<MarkerComponent> CODEC = BuilderCodec.builder(MarkerComponent.class, MarkerComponent::new)
        .append(new KeyedCodec<>("Marker", MarkerType.CODEC, true), (asset, value) -> asset.type = value, asset -> asset.type)
        .add()
        .build();

    @Nonnull
    private MarkerType type = MarkerType.Empty;

    @Nonnull
    public static ComponentType<EntityStore, MarkerComponent> getComponentType() {
        return Core.get().getMarkerComponentType();
    }

    public MarkerComponent() {
    }

    public MarkerComponent(@Nonnull MarkerType type) {
        this.type = type;
    }

    @Nonnull
    public MarkerType getType() {
        return type;
    }

    public void setType(@Nonnull MarkerType type) {
        this.type = type;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new MarkerComponent(this.type);
    }
}
