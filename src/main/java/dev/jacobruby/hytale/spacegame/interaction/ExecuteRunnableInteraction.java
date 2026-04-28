package dev.jacobruby.hytale.spacegame.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.asset.component.RunnableComponent;

import javax.annotation.Nonnull;

public class ExecuteRunnableInteraction extends SimpleInstantInteraction {
    @Nonnull
    public static final BuilderCodec<ExecuteRunnableInteraction> CODEC = BuilderCodec.builder(
        ExecuteRunnableInteraction.class, ExecuteRunnableInteraction::new, SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getTargetEntity();

        RunnableComponent component = ref.getStore().getComponent(ref, RunnableComponent.getComponentType());

        if (component == null) {
            Core.warn().log("ExecuteRunnableInteraction fired on entity with no RunnableComponent.");
            return;
        }

        component.getRunnable().accept(context);
    }
}
