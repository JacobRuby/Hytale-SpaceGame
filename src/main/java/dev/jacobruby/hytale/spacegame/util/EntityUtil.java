package dev.jacobruby.hytale.spacegame.util;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.asset.component.RunnableComponent;
import dev.jacobruby.hytale.spacegame.interaction.ExecuteRunnableInteraction;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class EntityUtil {

    private EntityUtil() {
    }

    public static Holder<EntityStore> newHolder(TransformComponent transform, Model model) {
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.addComponent(TransformComponent.getComponentType(), transform);
        holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(model.getBoundingBox()));
        holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
        holder.addComponent(PropComponent.getComponentType(), new PropComponent());

        return holder;
    }

    @Nullable
    public static Ref<EntityStore> spawnEntity(TransformComponent transform, Store<EntityStore> store, Model model, Consumer<Holder<EntityStore>> consumer) {
        Holder<EntityStore> holder = newHolder(transform, model);

        if (consumer != null)
            consumer.accept(holder);

        return store.addEntity(holder, AddReason.SPAWN);
    }

    public static Holder<EntityStore> newInteractable(TransformComponent transformComponent, Model model, InteractionType interactionType, Consumer<InteractionContext> onInteract) {
        Holder<EntityStore> holder = newHolder(transformComponent, model);

        Interactions interactions = holder.ensureAndGetComponent(Interactions.getComponentType());
        interactions.setInteractionId(interactionType, "ExecuteRunnable");

        holder.addComponent(RunnableComponent.getComponentType(), new RunnableComponent(onInteract));
        return holder;
    }
}
