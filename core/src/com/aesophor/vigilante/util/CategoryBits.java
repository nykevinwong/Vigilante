package com.aesophor.vigilante.util;

public class CategoryBits {

    public static final short GROUND = 1;
    public static final short PLATFORM = 2;
    public static final short FEET = 4;
    public static final short WALL = 8;
    public static final short CLIFF_MARKER = 16;
    public static final short PORTAL = 32;
    
    public static final short PLAYER = 64;
    public static final short ENEMY = 128;
    public static final short OBJECT = 256;
    public static final short ITEM = 512;
    public static final short MELEE_WEAPON = 1024;
    public static final short DESTROYED = 2048;

    public static final short LIGHT = 4096;
    
}