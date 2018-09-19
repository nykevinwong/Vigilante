package com.aesophor.medievania.event.character;

import com.aesophor.medievania.entity.character.Character;
import com.aesophor.medievania.entity.item.Item;
import com.aesophor.medievania.event.GameEvent;
import com.aesophor.medievania.event.GameEventType;

public class ItemEquippedEvent extends GameEvent {

    private final Character character;
    private final Item item;

    public ItemEquippedEvent(Character character, Item item) {
        super(GameEventType.ITEM_EQUIPPED);
        this.character = character;
        this.item = item;
    }


    public Character getCharacter() {
        return character;
    }

    public Item getItem() {
        return item;
    }

}