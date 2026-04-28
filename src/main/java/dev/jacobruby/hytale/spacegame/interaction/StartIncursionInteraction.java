package dev.jacobruby.hytale.spacegame.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.Game;

import javax.annotation.Nonnull;

public class StartIncursionInteraction extends GameInteraction {
    @Nonnull
    public static final BuilderCodec<StartIncursionInteraction> CODEC = BuilderCodec.builder(
        StartIncursionInteraction.class, StartIncursionInteraction::new, SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@Nonnull Game game, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        if (game.getIncursion() != null) {
            Ref<EntityStore> ref = context.getEntity();
            PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());

            if (playerRef != null && playerRef.isValid()) {
                playerRef.sendMessage(Message.raw("An incursion is already running!"));
            }
            context.getState().state = InteractionState.Failed;
            return;
        }

        game.startIncursion();
    }
}
