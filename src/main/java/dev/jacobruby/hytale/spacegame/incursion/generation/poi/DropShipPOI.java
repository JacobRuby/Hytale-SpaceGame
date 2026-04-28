package dev.jacobruby.hytale.spacegame.incursion.generation.poi;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.asset.MarkerType;
import dev.jacobruby.hytale.spacegame.incursion.Incursion;
import dev.jacobruby.hytale.spacegame.util.EntityUtil;
import dev.jacobruby.hytale.spacegame.util.TeleportUtil;

public class DropShipPOI extends POI {

    private Incursion incursion;

    public DropShipPOI(Incursion incursion) {
        super("Game/Terminal/Drop_Ship.prefab.json", MarkerType.Terminal);
        this.incursion = incursion;
    }

    @Override
    public void generate(World world, Vector3d origin) {
        super.generate(world, origin);

        Store<EntityStore> store = world.getEntityStore().getStore();

        Model model = Model.createUnitScaleModel(ModelAsset.getAssetMap().getAsset("Warp"));
        Holder<EntityStore> holder = EntityUtil.newInteractable(new TransformComponent(origin.clone().add(2, 0, 0), new Vector3f(0, 0, 0)), model, InteractionType.Use, context -> {
            Ref<EntityStore> ref = context.getEntity();
            PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());

            if (playerRef != null && playerRef.isValid()) {
                world.sendMessage(Message.raw("%s started the drop pod. Returning to ship!".formatted(playerRef.getUsername())));
            }

            world.execute(() -> this.incursion.end());
        });
//            holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(Box.horizontallyCentered(0.8, 1.8, 0.8)));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        holder.ensureComponent(UUIDComponent.getComponentType());
        holder.addComponent(Nameplate.getComponentType(), new Nameplate("End Incursion"));
        store.addEntity(holder, AddReason.SPAWN);


        // Funky way of teleporting the players into the game when it's "ready".
        this.incursion.getGame().getPlayers().forEach(TeleportUtil.teleportPlayers(this.incursion.getWorld(), origin.clone().add(0, 1, 0)));
    }
}
