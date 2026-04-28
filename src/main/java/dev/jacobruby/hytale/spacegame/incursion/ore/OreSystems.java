package dev.jacobruby.hytale.spacegame.incursion.ore;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.Core;
import dev.jacobruby.hytale.spacegame.resource.IncrementalLootPool;
import dev.jacobruby.hytale.spacegame.resource.LootPool;
import dev.jacobruby.hytale.spacegame.resource.ResourceEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class OreSystems {
    public static Query<EntityStore> ORE_QUERY = Query.and(MineableEntityComponent.getComponentType());

    public OreSystems(PluginBase plugin) {
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = plugin.getEntityStoreRegistry();

        entityStoreRegistry.registerSystem(new Hit());
    }

    public class Hit extends DamageEventSystem {

        @Override
        public void handle(
            int index,
            @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer,
            @Nonnull Damage damage
        ) {
            if (damage.getSource() instanceof Damage.EntitySource source) {
                PlayerRef playerRef = store.getComponent(source.getRef(), PlayerRef.getComponentType());

                if (playerRef != null) {
                    LootPoolComponent lootPoolComponent = archetypeChunk.getComponent(index, LootPoolComponent.getComponentType());

                    if (lootPoolComponent != null) {
                        LootPool lootPool = lootPoolComponent.getLootPool();

                        if (lootPool instanceof IncrementalLootPool incremental) {
                            EntityStatMap statMap = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());

                            assert statMap != null;

                            float damageAmount = damage.getAmount();
                            EntityStatValue healthValue = statMap.get(DefaultEntityStatTypes.getHealth());
                            float maxHealth = healthValue.getMax();
                            float progress = damageAmount / maxHealth;

//                            playerRef.sendMessage(Message.raw("Health: %s".formatted(healthValue.get())));

                            List<ResourceEntry> rewards = incremental.submitProgress(progress);

                            if (!rewards.isEmpty()) {
                                for (ResourceEntry reward : rewards) {

                                    Player player = store.getComponent(source.getRef(), Player.getComponentType());

                                    String itemId = "Resource_%s".formatted(reward.type());
                                    ItemStack itemStack = new ItemStack(itemId, reward.count());

                                    player.getInventory().getCombinedHotbarFirst().addItemStack(itemStack);

                                    playerRef.sendMessage(Message.raw("+%s %s".formatted(reward.count(), reward.type())));
                                }
                            }
                        }
                    }
                }
            }
        }

        @Nullable
        @Override
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getInspectDamageGroup();
        }

        @Nullable
        @Override
        public Query<EntityStore> getQuery() {
            return ORE_QUERY;
        }
    }

}
