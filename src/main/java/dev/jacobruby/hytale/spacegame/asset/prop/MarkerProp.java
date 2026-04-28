package dev.jacobruby.hytale.spacegame.asset.prop;

import com.hypixel.hytale.builtin.hytalegenerator.EntityPlacementData;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.asset.MarkerType;
import dev.jacobruby.hytale.spacegame.asset.component.MarkerComponent;

import javax.annotation.Nonnull;

public class MarkerProp extends Prop {

    @Nonnull
    private final Bounds3i writeBounds = new Bounds3i();

    @Nonnull
    private final MarkerType type;

    private final int hash = this.hashCode();

    public MarkerProp(@Nonnull MarkerType type) {
        this.type = type;
        this.writeBounds.encompass(Vector3i.ZERO);
    }

    @Override
    public boolean generate(@Nonnull Context context) {
        Vector3d entityPosition = new Vector3d().assign(context.position);

        if (context.entityWriteBuffer.getBounds().contains(entityPosition)) {
            Model model = Model.createUnitScaleModel(ModelAsset.DEBUG);

            Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
            holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(entityPosition, new Vector3f(0, 0, 0)));
            holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
            holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
            holder.addComponent(MarkerComponent.getComponentType(), new MarkerComponent(this.type));
            holder.addComponent(Nameplate.getComponentType(), new Nameplate(type.name()));
            holder.addComponent(PropComponent.getComponentType(), new PropComponent());
            holder.ensureComponent(HiddenFromAdventurePlayers.getComponentType());
            holder.ensureComponent(UUIDComponent.getComponentType());

            EntityPlacementData placementData = new EntityPlacementData(new Vector3i(), PrefabRotation.ROTATION_0, holder, this.hash);
            context.entityWriteBuffer.addEntity(placementData);
        }

        return true;
    }

    @Nonnull
    @Override
    public Bounds3i getReadBounds_voxelGrid() {
        return Bounds3i.ZERO;
    }

    @Nonnull
    @Override
    public Bounds3i getWriteBounds_voxelGrid() {
        return this.writeBounds;
    }
}
