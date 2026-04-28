package dev.jacobruby.hytale.spacegame.ship;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.ModifyInventoryInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.Game;
import dev.jacobruby.hytale.spacegame.resource.ResourceEntry;
import dev.jacobruby.hytale.spacegame.resource.ResourceType;
import dev.jacobruby.hytale.spacegame.util.EntityUtil;
import dev.jacobruby.hytale.spacegame.util.TeleportUtil;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Ship {

    private final Game game;
    private World world;

    public Ship(Game game) {
        this.game = game;
    }

    public CompletableFuture<World> createShipWorld() {

        World hubWorld = Core.get().getHubWorld();

//        WorldConfig d;

//        d.setGameTime("Zone1_Sunny");
//        d.setForcedWeather("Zone1_Sunny");
//        d.setWorldGenProvider(new VoidWorldGenProvider());

        String worldName = "Ship";
        return InstancesPlugin.get().spawnInstance(worldName, hubWorld, TeleportUtil.getGlobalSpawnPoint(hubWorld)).whenComplete((world, throwable) -> {
            this.world = world;
            spawnInteractables();

            Core.info().log("The Ship Instance has been made.");
        });


    }

    private void spawnInteractables() {
        Store<EntityStore> store = this.world.getEntityStore().getStore();

        disclaimer: {
            Model model = Model.createUnitScaleModel(ModelAsset.DEBUG);
            Holder<EntityStore> holder = EntityUtil.newHolder(new TransformComponent(new Vector3d(0.5, 13, 3.5), new Vector3f(0, 0, 0)), model);
//            holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
            holder.addComponent(Nameplate.getComponentType(), new Nameplate("To avoid issues, play the demo in Creative mode."));
            store.addEntity(holder, AddReason.SPAWN);
        }

        startIncursion: {
            Model model = Model.createUnitScaleModel(ModelAsset.getAssetMap().getAsset("Warp"));
            Holder<EntityStore> holder = EntityUtil.newInteractable(new TransformComponent(new Vector3d(0.5, 13, 8.5), new Vector3f(0, 0, 0)), model, InteractionType.Use, context -> {
                if (this.game.getIncursion() != null) {
                    Ref<EntityStore> ref = context.getEntity();
                    PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());

                    if (playerRef != null && playerRef.isValid()) {
                        playerRef.sendMessage(Message.raw("An incursion is already running!"));
                    }
                    context.getState().state = InteractionState.Failed;
                    return;
                }

                this.game.startIncursion();
            });
//            holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(Box.horizontallyCentered(0.8, 1.8, 0.8)));
            holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
            holder.ensureComponent(UUIDComponent.getComponentType());
            holder.addComponent(Nameplate.getComponentType(), new Nameplate("Start Incursion"));
            store.addEntity(holder, AddReason.SPAWN);
        }


        int offset = 0;

        Map<ResourceType, ResourceEntry> prices = Map.of(
            ResourceType.Fuel, new ResourceEntry(ResourceType.Scrap, 5),
            ResourceType.Scrap, new ResourceEntry(ResourceType.Fuel, 10),
            ResourceType.Electronic, new ResourceEntry(ResourceType.Scrap, 30),
            ResourceType.Gemstone, new ResourceEntry(ResourceType.Electronic, 30),
            ResourceType.Organic, new ResourceEntry(ResourceType.Fuel, 100)
        );
        purchaseCards:
        for (ResourceType resourceType : ResourceType.values()) {
            ResourceEntry price = prices.get(resourceType);

            Model model = Model.createUnitScaleModel(ModelAsset.getAssetMap().getAsset("Node_%s_Small".formatted(resourceType)));

            Holder<EntityStore> holder = EntityUtil.newInteractable(new TransformComponent(new Vector3d(5.5, 13, 2.5 + (2 * offset++)), new Vector3f(0, 0, 0)), model, InteractionType.Use, context -> {
                Ref<EntityStore> ref = context.getEntity();
                PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                Player player = store.getComponent(ref, Player.getComponentType());

                if (playerRef == null || player == null)
                    return;

                ItemStack itemStack = new ItemStack("Resource_%s".formatted(price.type()), price.count());
                ItemStackTransaction transaction = player.getInventory().getCombinedHotbarFirst().removeItemStack(itemStack);
                if (transaction.succeeded()) {
                    this.world.sendMessage(Message.join(Message.raw("%s made a purchase! ".formatted(playerRef.getUsername())), Message.raw("+1 %s Card".formatted(resourceType)).color(Color.ORANGE).bold(true)).color(Color.LIGHT_GRAY));
                    this.game.addNewCard(resourceType);
                }
            });
            holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
            holder.ensureComponent(UUIDComponent.getComponentType());
            holder.addComponent(Nameplate.getComponentType(), new Nameplate("+1 %s Card (Costs %s %s)".formatted(resourceType, price.count(), price.type())));
            store.addEntity(holder, AddReason.SPAWN);
        }

        winCondition:
        {
            ResourceEntry price = new ResourceEntry(ResourceType.Gemstone, 50);

            Model model = Model.createUnitScaleModel(ModelAsset.getAssetMap().getAsset("Minecart"));

            Holder<EntityStore> holder = EntityUtil.newInteractable(new TransformComponent(new Vector3d(-5.5, 13, 6.5), new Vector3f(0, 0, 0)), model, InteractionType.Use, context -> {
                Ref<EntityStore> ref = context.getEntity();
                PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                Player player = store.getComponent(ref, Player.getComponentType());

                if (playerRef == null || player == null)
                    return;


                ItemStack itemStack = new ItemStack("Resource_%s".formatted(price.type()), price.count());
                ItemStackTransaction transaction = player.getInventory().getCombinedHotbarFirst().removeItemStack(itemStack);
                if (transaction.succeeded()) {
                    this.world.sendMessage(Message.join(Message.raw("%s made a purchase! ".formatted(playerRef.getUsername())), Message.raw("Jump Drive").color(Color.RED).bold(true)).color(Color.LIGHT_GRAY));
                    this.world.sendMessage(Message.raw("You win! Congratulations! Thanks for playing!").color(Color.GREEN).bold(true));
                    this.game.cleanup();
                }
            });
            holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
            holder.ensureComponent(UUIDComponent.getComponentType());
            holder.addComponent(Nameplate.getComponentType(), new Nameplate("Jump Drive - Escape the Galaxy (Costs %s %s)".formatted(price.count(), price.type())));
            store.addEntity(holder, AddReason.SPAWN);
        }

    }

    public Game getGame() {
        return game;
    }

    public World getWorld() {
        return world;
    }
}
