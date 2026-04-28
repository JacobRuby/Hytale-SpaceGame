package dev.jacobruby.hytale.spacegame.util;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.PropComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public final class GenUtil {

    private GenUtil() {
    }


    public static @Nullable Ref<EntityStore> spawnEmptyPrefabMarker(TransformComponent transform, Store<EntityStore> store, @Nullable String nameplate) {
        Model model = Model.createUnitScaleModel(ModelAsset.DEBUG);

        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.addComponent(TransformComponent.getComponentType(), transform);
        holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
        holder.addComponent(PropComponent.getComponentType(), new PropComponent());
        holder.addComponent(PrefabCopyableComponent.getComponentType(), new PrefabCopyableComponent());
//        holder.addComponent(PropInfoComponent.getComponentType(), new PropInfoComponent());
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        UUIDComponent uuidComponent = holder.ensureAndGetComponent(UUIDComponent.getComponentType());
        String uuidString = uuidComponent.getUuid().toString();
        holder.addComponent(Nameplate.getComponentType(), new Nameplate(nameplate == null ? uuidString : nameplate));

        return store.addEntity(holder, AddReason.SPAWN);
    }
}
