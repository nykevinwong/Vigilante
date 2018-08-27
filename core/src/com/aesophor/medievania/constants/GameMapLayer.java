package com.aesophor.medievania.constants;

public enum GameMapLayer {

    GROUND,          // 0: Player can only jump while standing on the ground.
    PLATFORM,        // 1: Floating and one-way passthrough platforms. Player can also jump on this.
    WALL,           // 2: Verical walls or ceiling. Colliding with these wont set isJumping back to false.
    CLIFF_MARKER;    // 3: Alert an NPC that it is near a cliff.
    
}