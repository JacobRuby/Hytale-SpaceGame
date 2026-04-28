package dev.jacobruby.hytale.spacegame.incursion;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.registry.Registration;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.Game;
import dev.jacobruby.hytale.spacegame.incursion.generation.MapGeneration;
import dev.jacobruby.hytale.spacegame.util.TeleportUtil;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class Incursion {

    private final Game game;
    private final int id;

    @Nullable
    private World world;

    private MapGeneration mapGeneration;

    private Registration addPlayerEvent;


    public Incursion(Game game, int id) {
        this.game = game;
        this.id = id;
    }

    public CompletableFuture<World> init() {

        Universe.get().sendMessage(Message.raw("Incursion starting!"));
        World shipWorld = this.game.getShip().getWorld();

//        MapGenSystems mapGenSystems = new MapGenSystems(this);

//        Core.get().getChunkStoreRegistry().registerSystem(mapGenSystems.getPropBlockSpawn());
//        Core.get().getChunkStoreRegistry().(mapGenSystems.getPropBlockSpawn());

//        WorldConfig c;
//        c.setChunkConfig();


        // I unfortunately couldn't figure out how to get entities to be properly loaded before a player
        // enters the world. So, I had to resort to teleporting the players to a "waiting box" while the
        // world is generating with them in it.
        this.addPlayerEvent = Core.get().getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {
            if (event.getWorld() == this.world) {
                Core.info().log("Player joined");
                this.mapGeneration.scheduleGeneration();
            }
        });

        String instanceAssetName = "Incursion";
        return InstancesPlugin.get().spawnInstance(instanceAssetName, shipWorld, TeleportUtil.getGlobalSpawnPoint(shipWorld)).whenComplete((world, throwable) -> {
            Core.info().log("Debug");
            Core.info().log("Debug");
            Core.info().log("Debug");

            this.world = world;
            this.mapGeneration = new MapGeneration(this);
        });

//        InstancesPlugin.teleportPlayerToInstance();

        // Generate map with custom worldgen
        // Grab data points?




        // players
        // Teleport players to ship
        // Give players weapon


    }

    public void end() {
        this.game.endIncursion();
    }

    public void cleanup() {
        // TODO Talley resources from players' inventories and add it to the ship's inventory.

        this.world.drainPlayersTo(getGame().getShip().getWorld(), this.game.getPlayers());

//        if (this.world.isAlive()) {
//            try {
//                this.world.drainPlayersTo(Core.get().getHubWorld(), this.game.getPlayers());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        this.addPlayerEvent.unregister();

        InstancesPlugin.safeRemoveInstance(this.world);
    }

    public Game getGame() {
        return game;
    }

    @Nullable
    public World getWorld() {
        return world;
    }
}
