package dev.jacobruby.hytale.spacegame;

import com.hypixel.hytale.builtin.hytalegenerator.assets.props.PropAsset;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.shape.Box2D;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector2d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.FlatWorldGenProvider;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import dev.jacobruby.hytale.spacegame.asset.component.MarkerComponent;
import dev.jacobruby.hytale.spacegame.asset.component.RunnableComponent;
import dev.jacobruby.hytale.spacegame.asset.generator.MarkerPropAsset;
import dev.jacobruby.hytale.spacegame.command.BlockComponentCommand;
import dev.jacobruby.hytale.spacegame.command.ComponentCommand;
import dev.jacobruby.hytale.spacegame.command.GameCommand;
import dev.jacobruby.hytale.spacegame.incursion.generation.MarkerSystem;
import dev.jacobruby.hytale.spacegame.incursion.ore.LootPoolComponent;
import dev.jacobruby.hytale.spacegame.incursion.ore.MineableEntityComponent;
import dev.jacobruby.hytale.spacegame.incursion.ore.OreSystems;
import dev.jacobruby.hytale.spacegame.interaction.ExecuteRunnableInteraction;
import dev.jacobruby.hytale.spacegame.interaction.StartIncursionInteraction;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public class Core extends JavaPlugin {
    public static HytaleLogger.Api info() {
        return INSTANCE.getLogger().atInfo();
    }

    public static HytaleLogger.Api warn() {
        return INSTANCE.getLogger().atWarning();
    }

    public static HytaleLogger.Api severe() {
        return INSTANCE.getLogger().atSevere();
    }

    private static Core INSTANCE;

    public static Core get() {
        return INSTANCE;
    }

    private ComponentType<EntityStore, MarkerComponent> markerComponentType;
    private ComponentType<EntityStore, MineableEntityComponent> mineableEntityComponentType;
    private ComponentType<EntityStore, LootPoolComponent> lootPoolComponentType;
    private ComponentType<EntityStore, RunnableComponent> runnableComponentType;
//    private ComponentType<EntityStore, PropInfoComponent> propInfoComponentType;
//    private ComponentType<ChunkStore, PropInfoBlockComponent> propInfoBlockComponentType;

    private World hubWorld;

    @Nullable
    private Game game;

    public Core(@NonNull JavaPluginInit init) {
        super(init);
        INSTANCE = this;
    }

    @Override
    protected void setup() {
        getEventRegistry().register(PlayerConnectEvent.class, event -> {
            info().log("Sending to hub world.");
            event.setWorld(this.hubWorld);
            event.getPlayerRef().sendMessage(Message.join(Message.raw("Type "), Message.raw("'/game start'").color(Color.GREEN), Message.raw(" to begin a new game!")).color(Color.LIGHT_GRAY).bold(true));
        });

        getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {
            if (event.getWorld() == this.hubWorld) {
                Transform spawnPoint = this.hubWorld.getWorldConfig().getSpawnProvider().getSpawnPoint(this.hubWorld, null);
                event.getHolder().replaceComponent(TransformComponent.getComponentType(), new TransformComponent(spawnPoint.getPosition(), new Vector3f(0, 0, 0)));
            }
        });

        CommandRegistry commandRegistry = getCommandRegistry();
        commandRegistry.registerCommand(new GameCommand());
        commandRegistry.registerCommand(new ComponentCommand());
        commandRegistry.registerCommand(new BlockComponentCommand());

        getCodecRegistry(Interaction.CODEC).register("GameStartIncursion", StartIncursionInteraction.class, StartIncursionInteraction.CODEC);
        getCodecRegistry(Interaction.CODEC).register("ExecuteRunnable", ExecuteRunnableInteraction.class, ExecuteRunnableInteraction.CODEC);
        getCodecRegistry(PropAsset.CODEC).register("Marker", MarkerPropAsset.class, MarkerPropAsset.CODEC);

        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        this.markerComponentType = entityStoreRegistry.registerComponent(MarkerComponent.class, "Marker", MarkerComponent.CODEC);
        this.mineableEntityComponentType = entityStoreRegistry.registerComponent(MineableEntityComponent.class, MineableEntityComponent::new);
        this.lootPoolComponentType = entityStoreRegistry.registerComponent(LootPoolComponent.class, LootPoolComponent::new);
        this.runnableComponentType = entityStoreRegistry.registerComponent(RunnableComponent.class, RunnableComponent::new);


        entityStoreRegistry.registerSystem(new MarkerSystem());

        new OreSystems(this);

//        this.propInfoComponentType = entityStoreRegistry.registerComponent(PropInfoComponent.class, "PropInfo", PropInfoComponent.CODEC);
//        entityStoreRegistry.registerSystem(new PropSpawnSystem());
//
//        ComponentRegistryProxy<ChunkStore> chunkStoreRegistry = this.getChunkStoreRegistry();
//        this.propInfoBlockComponentType = chunkStoreRegistry.registerComponent(PropInfoBlockComponent.class, "PropInfoBlock", PropInfoBlockComponent.CODEC);
//        chunkStoreRegistry.registerSystem(new PropBlockSpawnSystem());

    }

    @Override
    protected void start() {
        WorldConfig worldConfig = new WorldConfig();
        worldConfig.setDeleteOnUniverseStart(true);
        worldConfig.setDeleteOnRemove(true);
        worldConfig.setWorldGenProvider(new FlatWorldGenProvider());
        worldConfig.setSpawnProvider(new GlobalSpawnProvider(new Transform(0, 2, 0)));
        worldConfig.getChunkConfig().setPregenerateRegion(new Box2D(new Vector2d(-512.0, -512.0), new Vector2d(512.0, 512.0)));
        worldConfig.getChunkConfig().setKeepLoadedRegion(new Box2D(new Vector2d(-512.0, -512.0), new Vector2d(512.0, 512.0)));
//        worldConfig.setBlockTicking();

        String worldName = "Hub";
        Path worldPath = Constants.UNIVERSE_PATH.resolve("worlds").resolve(worldName);

        try {
            if (worldPath.toFile().exists()) {
                FileUtil.deleteDirectory(worldPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Universe.get().makeWorld(worldName, worldPath, worldConfig).whenComplete((world, throwable) -> {
            info().log("The world has been made.");
            this.hubWorld = world;

        });
    }

    public World getHubWorld() {
        return hubWorld;
    }

    @Nullable
    public Game getGame() {
        return this.game;
    }

    public boolean isGameRunning() {
        return this.game != null;
    }

    public void startNewGame() {
        if (isGameRunning()) {
            throw new IllegalStateException("A game is already running");
        }

        this.game = new Game(this);

        this.game.init();

    }

    public void forceStopGame() {
        if (!isGameRunning()) {
            throw new IllegalStateException("No game is running");
        }

        try {
            this.game.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.game = null;
    }

    public ComponentType<EntityStore, MarkerComponent> getMarkerComponentType() {
        return markerComponentType;
    }

    public ComponentType<EntityStore, MineableEntityComponent> getMineableEntityComponentType() {
        return mineableEntityComponentType;
    }

    public ComponentType<EntityStore, LootPoolComponent> getLootPoolComponentType() {
        return lootPoolComponentType;
    }

    public ComponentType<EntityStore, RunnableComponent> getRunnableComponentType() {
        return runnableComponentType;
    }

//    public ComponentType<EntityStore, PropInfoComponent> getPropInfoComponentType() {
//        return propInfoComponentType;
//    }
//
//    public ComponentType<ChunkStore, PropInfoBlockComponent> getPropInfoBlockComponentType() {
//        return propInfoBlockComponentType;
//    }
}
