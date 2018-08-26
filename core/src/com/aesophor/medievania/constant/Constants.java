package com.aesophor.medievania.constant;

public final class Constants {
    
    public static boolean DEBUG = true;

    // Graphics constants
    public static final float PPM = 100;
    public static final int V_WIDTH = 600;
    public static final int V_HEIGHT = 300;
    
    // Physics constants
    public static final int GRAVITY = -10;
    public static final int GROUND_FRICTION = 1;
    
    // CollisionBit constants
    public static final short GROUND_BIT = 1;
    public static final short OBJECT_BIT = 2;
    public static final short DESTROYED_BIT = 4;
    public static final short PLAYER_BIT = 8;
    public static final short ENEMY_BIT = 16;
    public static final short MELEE_WEAPON_BIT = 32;
    
    // TiledMap layer constants
    public static final int GROUND_LAYER = 0;
    
    
    private Constants() {
        throw new AssertionError();
    }
    
}