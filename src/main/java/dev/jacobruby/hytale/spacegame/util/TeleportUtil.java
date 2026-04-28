package dev.jacobruby.hytale.spacegame.util;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class TeleportUtil {
    private TeleportUtil() {
    }


    public static Consumer<PlayerRef> teleportPlayersToInstance(World world) {
        return teleportPlayersToInstance(world, null);
    }

    public static Consumer<PlayerRef> teleportPlayersToInstance(World world, Transform overrideReturn) {
        return playerRef -> {
            Ref<EntityStore> ref = playerRef.getReference();
            InstancesPlugin.teleportPlayerToInstance(ref, ref.getStore(), world, overrideReturn);
        };
    }

    public static Consumer<PlayerRef> teleportPlayers(World world) {
        Transform globalSpawnPoint = getGlobalSpawnPoint(world);
        return teleportPlayers(world, globalSpawnPoint.getPosition(), globalSpawnPoint.getRotation());
    }

    public static Consumer<PlayerRef> teleportPlayers(World world, Vector3d position) {
        return teleportPlayers(world, position, null);
    }

    public static Consumer<PlayerRef> teleportPlayers(World world, Vector3d position, @Nullable Vector3f rotation) {
        return playerRef -> {
            Core.info().log("Inside the lambda");
            teleportPlayer(playerRef, world, position, rotation);
        };
    }

    public static void teleportPlayer(PlayerRef playerRef, World world, Vector3d position) {
        teleportPlayer(playerRef, world, position, null);
    }

    public static void teleportPlayer(PlayerRef playerRef, World world, Vector3d position, @Nullable Vector3f rotation) {
        Core.info().log("Start Teleporting");
        Ref<EntityStore> ref = playerRef.getReference();

        Store<EntityStore> store = ref.getStore();
        if (rotation == null) {
            TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
            if (transform != null) {
                rotation = transform.getRotation();
            } else {
                rotation = new Vector3f();
            }
        }

        final var finalRotation = rotation;

        Core.info().log("Teleporting");
        Core.info().log("Teleporting %s to %s".formatted(playerRef.getUsername(), position));

        store.addComponent(ref, Teleport.getComponentType(), new Teleport(world, position, finalRotation));
//        playerRef.getReference().getStore().getExternalData().getWorld().execute(() -> {
//        });

    }

    public static Transform getGlobalSpawnPoint(World world) {
        return world.getWorldConfig().getSpawnProvider().getSpawnPoint(world, null);
    }
}
