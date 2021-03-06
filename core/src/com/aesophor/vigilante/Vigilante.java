package com.aesophor.vigilante;

import com.aesophor.vigilante.entity.data.CharacterDataManager;
import com.aesophor.vigilante.entity.data.EquipmentDataManager;
import com.aesophor.vigilante.entity.data.ItemDataManager;
import com.aesophor.vigilante.screen.AbstractScreen;
import com.aesophor.vigilante.screen.Screens;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Vigilante extends Game implements GameStateManager {
    
    private SpriteBatch batch;
    private AssetManager assets;
    
    @Override
    public void create () {
        this.batch = new SpriteBatch();
        this.assets = new GameAssetManager();

        // Initialize database.
        ItemDataManager.getInstance().load(GameAssetManager.ITEM_DATABASE);
        EquipmentDataManager.getInstance().load(GameAssetManager.EQUIPMENT_DATABASE);
        CharacterDataManager.getInstance().load(GameAssetManager.CHARACTER_DATABASE);

        ((GameAssetManager) assets).loadAllAssets();
        assets.finishLoading();
        
        showScreen(Screens.MAIN_MENU);
    }
    
    
    /**
     * Shows the specified Screen.
     * @param s screen to update.
     */
    @Override
    public void showScreen(Screens s) {
        // Get current screen to dispose it
        Screen currentScreen = getScreen();
 
        // Show new screen
        AbstractScreen newScreen = s.newScreen(this);
        setScreen(newScreen);
 
        // Dispose previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }
    
    /**
     * Clears the screen with pure black.
     */
    @Override
    public void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    
    
    /**
     * Gets the SpriteBatch.
     * @return sprite batch.
     */
    @Override
    public SpriteBatch getBatch() {
        return batch;
    }
    
    /**
     * Gets the AssetManager.
     * @return asset managers.
     */
    @Override
    public AssetManager getAssets() {
        return assets;
    }
    
    
    @Override
    public void render () {
        super.render();
        assets.update();
    }
    
    @Override
    public void dispose () {
        assets.dispose();
        batch.dispose();
    }
    
}