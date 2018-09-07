package com.aesophor.medievania.screens;

import com.aesophor.medievania.GameStateManager;
import com.aesophor.medievania.GameWorldManager;
import com.aesophor.medievania.character.Character;
import com.aesophor.medievania.character.Enemy;
import com.aesophor.medievania.character.Player;
import com.aesophor.medievania.event.*;
import com.aesophor.medievania.map.GameMap;
import com.aesophor.medievania.map.Portal;
import com.aesophor.medievania.map.WorldContactListener;
import com.aesophor.medievania.system.*;
import com.aesophor.medievania.ui.DamageIndicator;
import com.aesophor.medievania.ui.StatusBars;
import com.aesophor.medievania.ui.NotificationArea;
import com.aesophor.medievania.util.CameraShake;
import com.aesophor.medievania.util.CameraUtils;
import com.aesophor.medievania.util.Constants;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

import java.util.EventListener;

public class MainGameScreen extends AbstractScreen implements GameWorldManager {

    private final GameEventManager gameEventManager;
    private final PooledEngine engine;

    private final StatusBars statusBars;
    private final DamageIndicator damageIndicator;
    private final NotificationArea notificationArea;

    private final AssetManager assets;
    private final TmxMapLoader mapLoader;

    private World world;
    private GameMap currentMap;

    private final Player player;
    private Array<Character> enemies;

    public MainGameScreen(GameStateManager gsm) {
        super(gsm);
        assets = gsm.getAssets();

        // Since we will be rendering TiledMaps, we should scale the viewport with PPM.
        getViewport().setWorldSize(Constants.V_WIDTH / Constants.PPM, Constants.V_HEIGHT / Constants.PPM);

        // Initialize the GameEventManager.
        gameEventManager = GameEventManager.getInstance();

        // Initialize the world, and register the world contact listener.
        world = new World(new Vector2(0, Constants.GRAVITY), true);
        world.setContactListener(new WorldContactListener());

        // Initialize damage indicators and notificationArea area.
        statusBars = new StatusBars(gsm);
        damageIndicator = new DamageIndicator(getBatch(), gsm.getFont().getDefaultFont(), getCamera(), 1.5f);
        notificationArea = new NotificationArea(getBatch(), gsm.getFont().getDefaultFont(), 6, .3f);

        // Initialize PooledEngine and systems.
        engine = new PooledEngine();
        engine.addSystem(new TiledMapRendererSystem((OrthographicCamera) getCamera())); // Renders TiledMap textures.
        engine.addSystem(new CharacterRendererSystem(getBatch(), getCamera(), world));  // Renders entities (player/enemies/obj)
        engine.addSystem(new CharacterAISystem());
        engine.addSystem(new PlayerControlSystem());
        engine.addSystem(new B2DebugRendererSystem(world, getCamera()));                // Renders physics debug profiles.
        engine.addSystem(new B2LightsSystem(world, getCamera()));                       // Renders Dynamic box2d lights.
        engine.addSystem(new DamageIndicatorSystem(getBatch(), damageIndicator));       // Renders damage indicators.
        engine.addSystem(new NotificationSystem(getBatch(), notificationArea));         // Renders Notifications.
        engine.addSystem(new PlayerStatusBarsSystem(getBatch(), statusBars));           // Renders player status bars.
        engine.addSystem(new ScreenFadeSystem(this));                   // Renders screen fade effects. (not suitable for ECS ?)

        // Load the map and spawn player.
        mapLoader = new TmxMapLoader();
        setGameMap("map/starting_point.tmx");
        player = currentMap.spawnPlayer();
        engine.addEntity(player);

        statusBars.registerPlayer(player);
    }


    public void handleInput(float delta) {

    }


    public void update(float delta) {
        handleInput(delta);

        world.step(1/60f, 6, 2);

        if (CameraShake.getShakeTimeLeft() > 0){
            CameraShake.update(Gdx.graphics.getDeltaTime());
            getCamera().translate(CameraShake.getPos());
        } else {
            CameraUtils.lerpToTarget(getCamera(), player.getB2Body().getPosition());
        }

        // Make sure to bound the camera within the TiledMap.
        CameraUtils.boundCamera(getCamera(), getCurrentMap());
    }

    @Override
    public void render(float delta) {
        update(delta);
        gsm.clearScreen();
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // Fire a screen-resize event to update the viewport of DamageIndicator and RayHandler.
        int viewportX = getViewport().getScreenX();
        int viewportY = getViewport().getScreenY();
        int viewportWidth = getViewport().getScreenWidth();
        int viewportHeight = getViewport().getScreenHeight();
        gameEventManager.fireEvent(new MainGameScreenResizeEvent(viewportX, viewportY, viewportWidth, viewportHeight));
    }

    @Override
    public void dispose() {
        //renderer.dispose();
        //b2dr.dispose();
        //statusBars.dispose();
        currentMap.dispose();
        world.dispose();
        player.dispose();
        enemies.forEach((Character c) -> c.dispose());
    }


    /**
     * Sets the speicified GameMap as the current one.
     * @param gameMapFile path to the .tmx tiled map.
     */
    @Override
    public void setGameMap(String gameMapFile) {
        // Dispose previous map data if there is any.
        if (currentMap != null) {
            // Stop the background music, lights and dispose previous GameMap.
            currentMap.getBackgroundMusic().stop();
            currentMap.dispose();

            // Destroy all bodies except player's body.
            Array<Body> bodies = new Array<>();
            world.getBodies(bodies);

            for(int i = 0; i < bodies.size; i++)
            {
                if (!bodies.get(i).equals(player.getB2Body())) {
                    world.destroyBody(bodies.get(i));
                }
            }
        }

        // Load the new map from gameMapFile.
        currentMap = new GameMap(this, gameMapFile);
        currentMap.playBackgroundMusic();

        gameEventManager.fireEvent(new MapChangedEvent(currentMap));

        // TODO: Don't respawn enemies whenever a map loads.
        enemies = currentMap.spawnNPCs();
        enemies.forEach(e -> engine.addEntity(e));
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public AssetManager getAssets() {
        return assets;
    }

    @Override
    public TmxMapLoader getMapLoader() {
        return mapLoader;
    }

    @Override
    public NotificationArea getNotificationArea() {
        return notificationArea;
    }

    @Override
    public DamageIndicator getDamageIndicator() {
        return damageIndicator;
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

}