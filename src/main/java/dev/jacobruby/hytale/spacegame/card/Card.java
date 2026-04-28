package dev.jacobruby.hytale.spacegame.card;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;

public class Card {

    private String name;
    private Item icon;

    private int level = 1;

    public Card(String name, Item icon) {
        this.name = name;
        this.icon = icon;
    }

    public void upgrade() {
        this.level++;
    }

    public String getName() {
        return name;
    }

    public Item getIcon() {
        return icon;
    }

    public int getLevel() {
        return level;
    }
}
