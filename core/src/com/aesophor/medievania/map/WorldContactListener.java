package com.aesophor.medievania.map;

import com.aesophor.medievania.util.CategoryBits;
import com.aesophor.medievania.character.Character;
import com.aesophor.medievania.character.Player;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {
    
    public WorldContactListener() {
        
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
        
        switch (cDef) {
            case CategoryBits.FEET | CategoryBits.GROUND:
                if (fixtureA.getFilterData().categoryBits == CategoryBits.FEET) {
                    ((Character) fixtureA.getUserData()).setIsJumping(false);
                } else {
                    ((Character) fixtureB.getUserData()).setIsJumping(false);
                }
                break;
                
            case CategoryBits.FEET | CategoryBits.PLATFORM:
                if (fixtureA.getFilterData().categoryBits == CategoryBits.FEET) {
                    ((Character) fixtureA.getUserData()).setIsJumping(false);
                    ((Character) fixtureA.getUserData()).setIsOnPlatform(true);
                } else {
                    ((Character) fixtureB.getUserData()).setIsJumping(false);
                    ((Character) fixtureB.getUserData()).setIsOnPlatform(true);
                }
                break;

            case CategoryBits.PLAYER | CategoryBits.PORTAL:
                if (fixtureA.getFilterData().categoryBits == CategoryBits.PLAYER) {
                    ((Player) fixtureA.getUserData()).setCurrentPortal((Portal) fixtureB.getUserData());
                } else {
                    ((Player) fixtureB.getUserData()).setCurrentPortal((Portal) fixtureA.getUserData());
                }
                break;
                
                
            case CategoryBits.PLAYER | CategoryBits.ENEMY:
                if (fixtureA.getFilterData().categoryBits == CategoryBits.PLAYER) {
                    ((Player) fixtureA.getUserData()).receiveDamage(25);
                    ((Player) fixtureA.getUserData()).knockedBack(1f);
                } else {
                    ((Player) fixtureB.getUserData()).receiveDamage(25);
                    ((Player) fixtureB.getUserData()).knockedBack(1f);
                }
                break;
                
            case CategoryBits.ENEMY | CategoryBits.CLIFF_MARKER:
                if (fixtureA.getFilterData().categoryBits == CategoryBits.ENEMY) {
                    ((Character) fixtureA.getUserData()).getBehavioralModel().reverseDirection();
                } else {
                    ((Character) fixtureB.getUserData()).getBehavioralModel().reverseDirection();
                }
                break;
                
            case CategoryBits.MELEE_WEAPON | CategoryBits.ENEMY:
                Player player;
                Character target;
                // Set enemy as player's current target (so he can inflict damage to it).
                if (fixtureA.getFilterData().categoryBits == CategoryBits.ENEMY) {
                    target = (Character) (fixtureA.getUserData());
                    player = (Player) (fixtureB.getUserData());
                    
                    player.setInRangeTarget(target);
                } else {
                    target = (Character) (fixtureB.getUserData());
                    player = (Player) (fixtureA.getUserData());
                    
                    player.setInRangeTarget(target);
                }
                break;
                
            case CategoryBits.MELEE_WEAPON | CategoryBits.PLAYER:
                Player p;
                Character t;
                // Set enemy as player's current target (so he can inflict damage to it).
                if (fixtureA.getFilterData().categoryBits == CategoryBits.PLAYER) {
                    p = (Player) (fixtureA.getUserData());
                    t = (Character) (fixtureB.getUserData());
                    
                    t.setInRangeTarget(p);
                } else {
                    p = (Player) (fixtureB.getUserData());
                    t = (Character) (fixtureA.getUserData());
                    
                    t.setInRangeTarget(p);
                }
                break;
                
            default:
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
        
        switch (cDef) {
            case CategoryBits.FEET | CategoryBits.GROUND:
                if (fixtureA.getFilterData().categoryBits == CategoryBits.FEET) {
                    if (((Character) fixtureA.getUserData()).getB2Body().getLinearVelocity().y < .5f) {
                        ((Character) fixtureA.getUserData()).setIsJumping(true);
                    }
                } else {
                    if (((Character) fixtureB.getUserData()).getB2Body().getLinearVelocity().y < .5f) {
                        ((Character) fixtureB.getUserData()).setIsJumping(true);
                    }
                }
                break;
                
            case CategoryBits.FEET | CategoryBits.PLATFORM:
                if (fixtureA.getFilterData().categoryBits == CategoryBits.FEET) {
                    if (((Character) fixtureA.getUserData()).getB2Body().getLinearVelocity().y < -.5f) {
                        ((Character) fixtureA.getUserData()).setIsJumping(true);
                        ((Character) fixtureA.getUserData()).setIsOnPlatform(false);
                    }
                } else {
                    if (((Character) fixtureB.getUserData()).getB2Body().getLinearVelocity().y < -.5f) {
                        ((Character) fixtureB.getUserData()).setIsJumping(true);
                        ((Character) fixtureB.getUserData()).setIsOnPlatform(false);
                    }
                }
                break;


            case CategoryBits.PLAYER | CategoryBits.PORTAL:
                if (fixtureA.getFilterData().categoryBits == CategoryBits.PLAYER) {
                    ((Player) fixtureA.getUserData()).setCurrentPortal(null);
                } else {
                    ((Player) fixtureB.getUserData()).setCurrentPortal(null);
                }
                break;

                
            case CategoryBits.MELEE_WEAPON | CategoryBits.ENEMY:
                Player player;
                // Clear player's in range target when contact ends.
                if (fixtureA.getFilterData().categoryBits == CategoryBits.ENEMY) {
                    player = (Player) (fixtureB.getUserData());
                    player.setInRangeTarget(null);
                } else {
                    player = (Player) (fixtureA.getUserData());
                    player.setInRangeTarget(null);
                }
                break;
                
            case CategoryBits.MELEE_WEAPON | CategoryBits.PLAYER:
                Character t;
                // Clear target's in range target when contact ends.
                if (fixtureA.getFilterData().categoryBits == CategoryBits.PLAYER) {
                    t = (Character) (fixtureB.getUserData());
                    t.setInRangeTarget(null);
                } else {
                    t = (Character) (fixtureA.getUserData());
                    t.setInRangeTarget(null);
                }
                break;
                
            default:
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
        
        switch (cDef) {
            // Allow player to pass through platforms and collide on the way down.
            case CategoryBits.PLAYER | CategoryBits.PLATFORM:
                float playerY;
                float platformY;
                
                if (fixtureA.getFilterData().categoryBits == CategoryBits.PLAYER) {
                    playerY = fixtureA.getBody().getPosition().y;
                    platformY = fixtureB.getBody().getPosition().y;
                } else {
                    playerY = fixtureB.getBody().getPosition().y;
                    platformY = fixtureA.getBody().getPosition().y;
                }
                
                if (playerY > platformY + .15f) { // Player is about to land on the platform.
                    contact.setEnabled(true);
                } else {
                    contact.setEnabled(false);
                }
                
                break;
                
            default:
                break;
        }
        
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        
    }

}