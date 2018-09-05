package com.aesophor.medievania;

import com.aesophor.medievania.character.Player;
import com.aesophor.medievania.map.GameMap;
import com.aesophor.medievania.ui.DamageIndicator;
import com.aesophor.medievania.ui.NotificationArea;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public interface GameWorldManager extends Disposable {

    public void setGameMap(String gameMapFile);

    public World getWorld();
    public AssetManager getAssets();
    public TmxMapLoader getMapLoader();

    public NotificationArea getNotificationArea();
    public DamageIndicator getDamageIndicator();

    public GameMap getCurrentMap();
    public Player getPlayer();

}