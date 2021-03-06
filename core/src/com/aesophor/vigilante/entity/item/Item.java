package com.aesophor.vigilante.entity.item;

import com.aesophor.vigilante.component.character.CharacterAnimationsComponent;
import com.aesophor.vigilante.component.equipment.EquipmentDataComponent;
import com.aesophor.vigilante.component.graphics.IconComponent;
import com.aesophor.vigilante.component.graphics.SpriteComponent;
import com.aesophor.vigilante.component.item.ItemDataComponent;
import com.aesophor.vigilante.component.item.ItemType;
import com.aesophor.vigilante.component.physics.B2BodyComponent;
import com.aesophor.vigilante.component.sound.SoundComponent;
import com.aesophor.vigilante.entity.data.EquipmentDataManager;
import com.aesophor.vigilante.entity.data.ItemDataManager;
import com.aesophor.vigilante.util.CategoryBits;
import com.aesophor.vigilante.util.Constants;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class Item extends Entity implements Disposable {

    private static final int itemWidth = 16;
    private static final int itemHeight = 16;

    private final AssetManager assets;

    private final ItemType type;
    private final ItemDataComponent itemData;
    private final IconComponent icon;
    private final B2BodyComponent b2body;
    private final SoundComponent sounds;

    public Item(String itemName, AssetManager assets, World world, Float x, Float y) {
        this.assets = assets;

        itemData = ItemDataManager.getInstance().get(itemName);
        icon = new IconComponent(assets.get(itemData.getImage()), x * Constants.PPM, y * Constants.PPM);
        b2body = new B2BodyComponent(world);
        sounds = new SoundComponent();
        type = itemData.getType();

        add(itemData);
        add(icon);
        add(b2body);
        add(sounds);

        // If this item is an equipment, then it should have animations for characters which
        // will be layered atop character's body sprite.
        if (type == ItemType.EQUIP) {
            EquipmentDataComponent equipmentData = EquipmentDataManager.getInstance().get(itemName);
            add(equipmentData);

            add(new CharacterAnimationsComponent());
            SpriteComponent sprite = new SpriteComponent();
            add(sprite);
            sprite.setBounds(0, 0, 105f / Constants.PPM, 105f / Constants.PPM);
        }

        constructIconBody();

        // Separate icon sprite and overlay sprite. (rename this motherfucker)
        icon.setBounds(0, 0, itemWidth / Constants.PPM, itemHeight / Constants.PPM);
    }


    public void reloadIconTexture() {
        icon.setTexture(assets.get(itemData.getImage()));
    }

    public void constructIconBody() {
        short bodyCategoryBits = CategoryBits.ITEM;
        short bodyMaskBits = CategoryBits.GROUND | CategoryBits.PLATFORM;
        defineBody(bodyCategoryBits, bodyMaskBits);
    }

    private void defineBody(short bodyCategoryBits, short bodyMaskBits) {
        Body body = b2body.getBodyBuilder().type(BodyDef.BodyType.DynamicBody)
                .position(icon.getX(), icon.getY(), Constants.PPM)
                .buildBody();

        b2body.setBody(body);
        createBodyFixture(bodyCategoryBits, bodyMaskBits);
    }

    /**
     * Builds body fixture based on this itemData's body width and height.
     * @param categoryBits category bits of body fixture.
     * @param maskBits defines which objects the body fixture can collide with.
     */
    private void createBodyFixture(short categoryBits, short maskBits) {
        Fixture bodyFixture = b2body.getBodyBuilder().newRectangleFixture(b2body.getBody().getPosition(), itemWidth / 2, itemHeight / 2, Constants.PPM)
                .categoryBits(categoryBits)
                .maskBits((short) (maskBits | CategoryBits.PLAYER))
                .setSensor(true)
                .setUserData(this)
                .buildFixture();

        b2body.setBodyFixture(bodyFixture);

        Fixture collisionFixture = b2body.getBodyBuilder().newRectangleFixture(b2body.getBody().getPosition(), itemWidth / 2, itemHeight / 2, Constants.PPM)
                .categoryBits(categoryBits)
                .maskBits(maskBits)
                .setUserData(this)
                .buildFixture();
    }


    public ItemType getType() {
        return type;
    }

    @Override
    public String toString() {
        return itemData.getName();
    }

    @Override
    public void dispose() {
        //sprite.dispose();
    }

}