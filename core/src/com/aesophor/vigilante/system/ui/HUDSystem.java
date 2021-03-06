package com.aesophor.vigilante.system.ui;

import com.aesophor.vigilante.entity.character.Player;
import com.aesophor.vigilante.ui.hud.HUD;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.Batch;

public class HUDSystem extends EntitySystem {

    private final Batch batch;
    private final HUD HUD;

    public HUDSystem(Batch batch, HUD HUD) {
        this.batch = batch;
        this.HUD = HUD;
    }


    @Override
    public void update(float delta) {
        batch.setProjectionMatrix(HUD.getCamera().combined);
        HUD.update(delta);
        HUD.draw();
    }

    public void registerPlayer(Player player) {
        HUD.registerPlayer(player);
    }

}
