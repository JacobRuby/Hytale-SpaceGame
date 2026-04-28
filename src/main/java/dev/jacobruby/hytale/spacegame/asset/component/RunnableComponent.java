package dev.jacobruby.hytale.spacegame.asset.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This whole class is probably one big memory leak... but that's a problem for later.
 */
public class RunnableComponent implements Component<EntityStore> {
    private Consumer<InteractionContext> runnable;

    public static ComponentType<EntityStore, RunnableComponent> getComponentType() {
        return Core.get().getRunnableComponentType();
    }

    public RunnableComponent() {
    }

    public RunnableComponent(Consumer<InteractionContext> runnable) {
        this.runnable = runnable;
    }

    public Consumer<InteractionContext> getRunnable() {
        return runnable;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new RunnableComponent(this.runnable);
    }
}
