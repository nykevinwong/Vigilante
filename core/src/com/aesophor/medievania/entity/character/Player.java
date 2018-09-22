package com.aesophor.medievania.entity.character;

import com.aesophor.medievania.component.Mappers;
import com.aesophor.medievania.component.character.*;
import com.aesophor.medievania.component.physics.B2BodyComponent;
import com.aesophor.medievania.component.sound.SoundComponent;
import com.aesophor.medievania.component.sound.SoundType;
import com.aesophor.medievania.entity.item.Item;
import com.aesophor.medievania.event.GameEventManager;
import com.aesophor.medievania.event.combat.CharacterKilledEvent;
import com.aesophor.medievania.event.combat.ItemPickedUpEvent;
import com.aesophor.medievania.util.CategoryBits;
import com.aesophor.medievania.util.Constants;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Player extends Character {

    public Player(AssetManager assets, World world, float x, float y) {
        super("Player", assets, world, x, y);

        add(new PickupItemTargetComponent());
        add(new ControllableComponent());
        add(new PortalTargetComponent());
        add(new EquipmentSlotsComponent());

        // Create body and fixtures.
        short bodyCategoryBits = CategoryBits.PLAYER;
        short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM | CategoryBits.WALL | CategoryBits.PORTAL | CategoryBits.ENEMY | CategoryBits.MELEE_WEAPON | CategoryBits.ITEM;
        short feetMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM;
        short weaponMaskBits = CategoryBits.ENEMY | CategoryBits.OBJECT;
        super.defineBody(BodyDef.BodyType.DynamicBody, bodyCategoryBits, bodyMaskBits, feetMaskBits, weaponMaskBits);

        Mappers.SPRITE.get(this).setBounds(0, 0, 115 / Constants.PPM, 115 / Constants.PPM);
    }

    public void pickup(Item item) {
        B2BodyComponent itemB2Body = Mappers.B2BODY.get(item);
        InventoryComponent inventory = Mappers.INVENTORY.get(this);
        SoundComponent sounds = Mappers.SOUNDS.get(this);

        inventory.get(item.getType()).add(item);
        itemB2Body.getWorld().destroyBody(itemB2Body.getBody());
        GameEventManager.getInstance().fireEvent(new ItemPickedUpEvent(item));

        sounds.get(SoundType.ITEM_PICKEDUP).play();
    }

    public void reposition(Vector2 position) {
        Mappers.B2BODY.get(this).getBody().setTransform(position, 0);
    }

    public void reposition(float x, float y) {
        Mappers.B2BODY.get(this).getBody().setTransform(x, y, 0);
    }

    @Override
    public void inflictDamage(Character target, int damage) {
        super.inflictDamage(target, damage);

        if (Mappers.STATE.get(target).isSetToKill()) {
            GameEventManager.getInstance().fireEvent(new CharacterKilledEvent(this, target));
        }
    }

    @Override
    public void receiveDamage(Character source, int damage) {
        StateComponent state = Mappers.STATE.get(this);

        super.receiveDamage(source, damage);
        state.setInvincible(true);

        Timer.schedule(new Task() {
            @Override
            public void run() {
                if (!state.isSetToKill()) {
                    state.setInvincible(false);
                }
            }
        }, 3f);
    }

}