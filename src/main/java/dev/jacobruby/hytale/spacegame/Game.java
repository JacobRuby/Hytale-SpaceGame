package dev.jacobruby.hytale.spacegame;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import dev.jacobruby.hytale.spacegame.card.Card;
import dev.jacobruby.hytale.spacegame.card.ResourceCard;
import dev.jacobruby.hytale.spacegame.incursion.Incursion;
import dev.jacobruby.hytale.spacegame.resource.ResourceType;
import dev.jacobruby.hytale.spacegame.ship.Ship;
import dev.jacobruby.hytale.spacegame.util.TeleportUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Game {

    private final Core core;
    private final Ship ship;

    private List<PlayerRef> players = new ArrayList<>();
    private int round = 0;

    private List<Card> cards = new ArrayList<>();

    private Incursion incursion;

    public Game(Core core) {
        this.core = core;
        this.ship = new Ship(this);
    }

    public void init() {
        Universe.get().sendMessage(Message.raw("The game is starting...").color(Color.GRAY));
        this.players.addAll(Universe.get().getPlayers());

        // Initial cards
        this.cards.addAll(List.of(
            new ResourceCard(ResourceType.Fuel),
            new ResourceCard(ResourceType.Scrap),
            new ResourceCard(ResourceType.Scrap),
            new ResourceCard(ResourceType.Scrap),
            new ResourceCard(ResourceType.Scrap)
        ));

        World shipWorld = this.ship.createShipWorld().join();


        Universe.get().sendMessage(Message.raw("Teleporting %s players...".formatted(this.players.size())).color(Color.GRAY));

//        InstancesPlugin.teleportPlayerToInstance();
        this.players.forEach(TeleportUtil.teleportPlayersToInstance(shipWorld));


    }

    public void startIncursion() {
        this.round++;
        this.incursion = new Incursion(this, this.round);
        this.incursion.init().whenComplete((world, throwable) -> {
            Core.info().log("The Incursion Instance has been made.");
            this.ship.getWorld().execute(() -> {
                this.players.forEach(TeleportUtil.teleportPlayersToInstance(this.incursion.getWorld()));
            });
        });
    }

    public void endIncursion() {
        if (this.incursion == null)
            throw new IllegalStateException("Incursion is not running");

        this.incursion.cleanup();

        this.incursion = null;
    }

    public void cleanup() {
        if (this.incursion != null) {
            this.incursion.cleanup();
        }

        World shipWorld = this.ship.getWorld();

        if (shipWorld.isAlive()) {

            try {
                shipWorld.drainPlayersTo(this.core.getHubWorld(), this.players);
            } catch (Exception e) {
                e.printStackTrace();
            }

            InstancesPlugin.safeRemoveInstance(shipWorld);
        }
    }

    public void addNewCard(ResourceType type) {
        this.cards.add(new ResourceCard(type));
    }

    public Ship getShip() {
        return ship;
    }

    public List<PlayerRef> getPlayers() {
        return players;
    }

    public List<Card> getCards() {
        return List.copyOf(this.cards);
    }

    public Incursion getIncursion() {
        return incursion;
    }
}
