package dev.jacobruby.hytale.spacegame.incursion.generation;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.asset.MarkerType;
import dev.jacobruby.hytale.spacegame.asset.component.MarkerComponent;
import dev.jacobruby.hytale.spacegame.card.Card;
import dev.jacobruby.hytale.spacegame.card.ResourceCard;
import dev.jacobruby.hytale.spacegame.incursion.Incursion;
import dev.jacobruby.hytale.spacegame.incursion.generation.poi.DropShipPOI;
import dev.jacobruby.hytale.spacegame.incursion.generation.poi.FloorEntrancePOI;
import dev.jacobruby.hytale.spacegame.incursion.generation.poi.POI;
import dev.jacobruby.hytale.spacegame.incursion.generation.poi.ResourceNode;
import dev.jacobruby.hytale.spacegame.resource.ResourceType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class MapGeneration {

    private final Incursion incursion;
    private final World world;
    private final Store<EntityStore> store;

    private boolean started;

    private Map<MarkerType, List<Ref<EntityStore>>> markerByType;

    public MapGeneration(Incursion incursion) {
        this.incursion = incursion;
        this.world = incursion.getWorld();
        this.store = this.world.getEntityStore().getStore();
    }

    public void scheduleGeneration() {
        if (this.started)
            return;

        this.started = true;

//        Transform spawn = this.world.getWorldConfig().getSpawnProvider().getSpawnPoint(this.world, null);
//        Vector3d spawnPosition = spawn.getPosition();
//        long chunkIndex = ChunkUtil.indexChunkFromBlock(spawnPosition.getX(), spawnPosition.getZ());
//        CompletableFuture<Void> loadTargetChunkFuture = this.world.getChunkStore()
//            .getChunkReferenceAsync(chunkIndex)
//            .thenAccept(v -> {
//                Core.info().log("Chunk loaded...");
//                Core.info().log("Chunk loaded...");
//                Core.info().log("Chunk loaded...");
//                Core.info().log("Chunk loaded...");
//            });


        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            this.world.execute(() -> {
                Core.info().log("Scanning world...");
                scanWorld();

                Core.info().log("Generating POIs...");
                generatePOIs();
            });
        }, 5, TimeUnit.SECONDS);
    }

    public void scanWorld() {
        var resource = this.store.getResource(EntityModule.get().getEntitySpatialResourceType());

        List<Ref<EntityStore>> entities = new ArrayList<>();
        resource.getSpatialStructure().ordered(new Vector3d(0, 100, 0), 200, entities);

        this.markerByType = new HashMap<>();
        for (Ref<EntityStore> ref : entities) {
            var marker = this.store.getComponent(ref, MarkerComponent.getComponentType());

            if (marker != null) {
                MarkerType type = marker.getType();

                Core.info().log("Found entity with MarkerType '%s'", type);
                this.markerByType.computeIfAbsent(type, t -> new ArrayList<>()).add(ref);
            }
        }
    }

    public void generatePOIs() {
        placeRandom(new DropShipPOI(this.incursion));
        placeRandom(new FloorEntrancePOI());

        // Generate ores from cards.
        for (Card card : this.incursion.getGame().getCards()) {
            if (card instanceof ResourceCard resourceCard) {
                ResourceType resource = resourceCard.getResource();
                ResourceNode.NodeSize nodeSize = resourceCard.getNodeSize();

                ResourceNode node = new ResourceNode(resource, nodeSize);
                placeRandom(node);
            }
        }
    }

    public void placeRandom(POI poi) {
        MarkerType markerType = poi.getMarkerType();

        var markers = this.markerByType.get(markerType);

        if (markers.isEmpty()) {
            Core.severe().log("There were no available markers for type %s", markerType);
            return;
        }

        int index = ThreadLocalRandom.current().nextInt(markers.size());
        Ref<EntityStore> selected = markers.remove(index);

        var transform = this.store.getComponent(selected, TransformComponent.getComponentType());
        poi.generate(this.world, transform.getPosition());
    }

}
