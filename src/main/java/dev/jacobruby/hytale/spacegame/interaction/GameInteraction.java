package dev.jacobruby.hytale.spacegame.interaction;

import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.Game;

import javax.annotation.Nonnull;

public abstract class GameInteraction extends SimpleInstantInteraction {

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        Core core = Core.get();
        if (!core.isGameRunning()) {
            context.getState().state = InteractionState.Failed;
            return;
        }

        Game game = core.getGame();
        firstRun(game, type, context, cooldownHandler);
    }

    protected abstract void firstRun(@Nonnull Game game, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler);
}
