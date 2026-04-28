package dev.jacobruby.hytale.spacegame.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;

import javax.annotation.Nonnull;
import java.awt.*;

public class GameCommand extends AbstractCommandCollection {


    public GameCommand() {
        super("game", "");

        this.addSubCommand(new StartCommand());
        this.addSubCommand(new StopCommand());
    }

    private static class StartCommand extends AbstractPlayerCommand {
        public StartCommand() {
            super("start", "");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            if (Core.get().isGameRunning()) {
                context.sendMessage(Message.raw("A game is already running!").color(Color.RED));
                return;
            }

            context.sendMessage(Message.raw("Starting a new game.").color(Color.GREEN));
            Core.get().startNewGame();
        }
    }

    private static class StopCommand extends AbstractPlayerCommand {
        public StopCommand() {
            super("stop", "");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            if (!Core.get().isGameRunning()) {
                context.sendMessage(Message.raw("There is no game running!").color(Color.RED));
                return;
            }

            Core.get().forceStopGame();
            context.sendMessage(Message.raw("Stopped the game.").color(Color.GREEN));
        }
    }
}
