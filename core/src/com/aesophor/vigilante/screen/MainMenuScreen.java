package com.aesophor.vigilante.screen;

import com.aesophor.vigilante.GameAssetManager;
import com.aesophor.vigilante.Vigilante;
import com.aesophor.vigilante.util.Constants;
import com.aesophor.vigilante.ui.theme.Font;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class MainMenuScreen extends AbstractScreen {

    private static final String COPYRIGHT_NOTICE = "Aesophor Softworks - Build: 9/10/2018 (pre-alpha)";

    private Texture backgroundTexture;
    
    private Music backgroundMusic;
    private Sound keyPressSound;

    private String[] menuItems;
    private Label[] itemLabels;
    private int currentItem;
    
    public MainMenuScreen(Vigilante gsm) {
        super(gsm);

        backgroundMusic = gsm.getAssets().get(GameAssetManager.MAIN_MENU_MUSIC);
        backgroundTexture = gsm.getAssets().get(GameAssetManager.MAIN_MENU_BG);
        keyPressSound = gsm.getAssets().get(GameAssetManager.UI_CLICK_SOUND);

        
        Table labelTable = new Table();
        labelTable.center().padTop(100f);
        labelTable.setFillParent(true);

        menuItems = new String[] {"New Game", "Load Game", "Credits", "End"};
        itemLabels = new Label[menuItems.length];
        
        for (int i = 0; i < menuItems.length; i++) {
            itemLabels[i] = new Label(menuItems[i], new Label.LabelStyle(Font.REGULAR, Color.WHITE));
            labelTable.add(itemLabels[i]).padTop(5f).row();
        }

        Table footerTable = new Table();
        footerTable.bottom().padBottom(15f);
        footerTable.setFillParent(true);
        Label copyrightLabel = new Label(COPYRIGHT_NOTICE, new Label.LabelStyle(Font.REGULAR, Color.WHITE));
        copyrightLabel.setAlignment(Align.center);
        footerTable.add(copyrightLabel);
        
        addActor(labelTable);
        addActor(footerTable);

        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(.7f);
        backgroundMusic.play();
    }
    
    
    @Override
    public void render(float delta) {
        handleInput(delta);
        
        gsm.clearScreen();
        gsm.getBatch().begin();
        gsm.getBatch().draw(backgroundTexture, 0, 0, Constants.V_WIDTH, Constants.V_HEIGHT);
        gsm.getBatch().end();
        
        for (int i = 0; i < itemLabels.length; i++) {
            if (i == currentItem) itemLabels[i].setColor(Color.RED);
            else itemLabels[i].setColor(Color.WHITE);
        }
        
        draw();
    }
    
    public void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (currentItem > 0) {
                keyPressSound.play();
                currentItem--;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (currentItem < menuItems.length - 1) {
                keyPressSound.play();
                currentItem++;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            select();
        }
    }
    
    public void select() {
        switch (currentItem) {
            case 0:
                backgroundMusic.stop();
                gsm.showScreen(Screens.GAME);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                Gdx.app.exit();
                break;
            default:
                Gdx.app.log("Main menu", "Unknown selected itemData!");
                break;
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        backgroundTexture.dispose();
        backgroundMusic.dispose();
        //keyPressSound.dispose();
    }

}