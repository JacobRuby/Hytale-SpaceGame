package dev.jacobruby.hytale.spacegame.incursion.generation.poi;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.jacobruby.hytale.spacegame.asset.MarkerType;
import dev.jacobruby.hytale.spacegame.incursion.ore.LootPoolComponent;
import dev.jacobruby.hytale.spacegame.incursion.ore.MineableEntityComponent;
import dev.jacobruby.hytale.spacegame.resource.IncrementalLootPool;
import dev.jacobruby.hytale.spacegame.resource.ResourceEntry;
import dev.jacobruby.hytale.spacegame.resource.ResourceType;
import dev.jacobruby.hytale.spacegame.util.EntityUtil;

public class ResourceNode extends POI {

    private static String getPrefabPath(ResourceType type, NodeSize size) {
//        return "Game/Resource/%s/%s/%s_%s_001.prefab.json".formatted(size, type, type, size);
        return "Game/Resource/Small/Scrap/Scrap_Small_001.prefab.json"; // debug, use the only one that's there right now.
        // TODO: prefab variations, hence the "001"
    }

    private final ResourceType resource;
    private final NodeSize size;

    public ResourceNode(ResourceType resource, NodeSize size) {
        super(getPrefabPath(resource, size), size.markerType);
        this.resource = resource;
        this.size = size;
    }

    @Override
    public void generate(World world, Vector3d origin) {
        super.generate(world, origin);

        Model model = getModel();
        Ref<EntityStore> ref = EntityUtil.spawnEntity(new TransformComponent(origin.clone().add(0.5, 0, 0.5), new Vector3f()), world.getEntityStore().getStore(), model, holder -> {
            if (true) {
//                Core.info().log("Yes");
                holder.ensureComponent(MineableEntityComponent.getComponentType());
                EntityStatMap statMap = holder.ensureAndGetComponent(EntityStatMap.getComponentType());
                int healthIndex = DefaultEntityStatTypes.getHealth();

                int nodeHealth = (int) (5 * this.size.multiplier);
                statMap.putModifier(healthIndex, "Ore_Max", new StaticModifier(Modifier.ModifierTarget.MAX, StaticModifier.CalculationType.ADDITIVE, -100 + nodeHealth));
                statMap.maximizeStatValue(healthIndex);
                //statMap.setStatValue(DefaultEntityStatTypes.getHealth(), 156f);

                int lootCount = (int) (2 * this.size.multiplier);
                var loot = new IncrementalLootPool(new ResourceEntry(this.resource, lootCount));
                holder.addComponent(LootPoolComponent.getComponentType(), new LootPoolComponent(loot));
            }
        });
    }

    private Model getModel() {
//        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Node_%s_%s".formatted(this.resource, this.size));
        // debug: just use multiplier as the model scale for now.
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Node_%s_Small".formatted(this.resource));

        return Model.createScaledModel(modelAsset, this.size.multiplier);
    }

    public enum NodeSize {
        Small(MarkerType.Ore_Small, 2.0f),
        Medium(MarkerType.Ore_Medium, 4.0f),
        Large(MarkerType.Ore_Large, 6.0f);

        public final MarkerType markerType;
        public final float multiplier;

        NodeSize(MarkerType markerType, float multiplier) {
            this.markerType = markerType;
            this.multiplier = multiplier;
        }
    }
}
