package dev.jacobruby.hytale.spacegame.card;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import dev.jacobruby.hytale.spacegame.incursion.generation.poi.ResourceNode;
import dev.jacobruby.hytale.spacegame.resource.ResourceType;

public class ResourceCard extends Card {
    private ResourceType resource;

    public ResourceCard(ResourceType resource) {
        super(resource.name(), Item.getAssetMap().getAsset("Resource_%s".formatted(resource)));
        this.resource = resource;
    }

    public ResourceType getResource() {
        return resource;
    }

    public ResourceNode.NodeSize getNodeSize() {
        return ResourceNode.NodeSize.values()[Integer.min(3, this.getLevel()) - 1];
    }
}
