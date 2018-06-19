package com.npl5035.swprototype.Tools;

import com.badlogic.gdx.physics.box2d.*;
import com.npl5035.swprototype.SWPrototype;
import com.npl5035.swprototype.Sprites.BlasterBolt;
import com.npl5035.swprototype.Sprites.Enemy;


public class WorldContactListener implements ContactListener {
    /**
     * Called when two fixtures begin to touch.
     *
     * @param contact
     */
    @Override
    public void beginContact(Contact contact) {

        Fixture ObjFixA = contact.getFixtureA();
        Fixture ObjFixB = contact.getFixtureB();

        int cDef = ObjFixA.getFilterData().categoryBits | ObjFixB.getFilterData().categoryBits;

        switch(cDef){
            case SWPrototype.ENEMEY_HITBOX_BIT | SWPrototype.BOLT_BIT:
                if(ObjFixA.getFilterData().categoryBits == SWPrototype.ENEMEY_HITBOX_BIT){
                    ((Enemy)ObjFixA.getUserData()).hitDetected();
                    ((BlasterBolt)ObjFixB.getUserData()).hitDetected();
                }else
                    if(ObjFixB.getFilterData().categoryBits == SWPrototype.ENEMEY_HITBOX_BIT){
                    ((Enemy)ObjFixB.getUserData()).hitDetected();
                    ((BlasterBolt)ObjFixA.getUserData()).hitDetected();
                }
                break;
            case SWPrototype.WALL_BIT | SWPrototype.BOLT_BIT:
                if(ObjFixA.getFilterData().categoryBits == SWPrototype.BOLT_BIT){
                    ((BlasterBolt)ObjFixA.getUserData()).hitDetected();
                }else
                if(ObjFixB.getFilterData().categoryBits == SWPrototype.BOLT_BIT){
                    ((BlasterBolt)ObjFixB.getUserData()).hitDetected();
                }
                break;
            case SWPrototype.PLAYER_BIT | SWPrototype.ENEMY_DETECTION_SENSOR_BIT:
                if(ObjFixA.getFilterData().categoryBits == SWPrototype.ENEMY_DETECTION_SENSOR_BIT){
                    ((Enemy)ObjFixA.getUserData()).setPlayerToDetected();

                }else
                if(ObjFixB.getFilterData().categoryBits == SWPrototype.ENEMY_DETECTION_SENSOR_BIT){
                    ((Enemy)ObjFixB.getUserData()).setPlayerToDetected();

                }
                break;
            case SWPrototype.PLAYER_BIT | SWPrototype.ENEMY_FOV_BIT:
                if(ObjFixA.getFilterData().categoryBits == SWPrototype.ENEMY_FOV_BIT){
                    ((Enemy)ObjFixA.getUserData()).setPlayerToSeen();

                }else
                if(ObjFixB.getFilterData().categoryBits == SWPrototype.ENEMY_FOV_BIT){
                    ((Enemy)ObjFixB.getUserData()).setPlayerToSeen();

                }
                break;

        }


    }

    /**
     * Called when two fixtures cease to touch.
     *
     * @param contact
     */
    @Override
    public void endContact(Contact contact) {
        Fixture ObjFixA = contact.getFixtureA();
        Fixture ObjFixB = contact.getFixtureB();

        int cDef = ObjFixA.getFilterData().categoryBits | ObjFixB.getFilterData().categoryBits;

        switch (cDef) {
            case SWPrototype.PLAYER_BIT | SWPrototype.ENEMY_DETECTION_SENSOR_BIT:
                if (ObjFixA.getFilterData().categoryBits == SWPrototype.ENEMY_DETECTION_SENSOR_BIT) {
                    //((Enemy) ObjFixA.getUserData()).setPlayerToUndetected();

                } else if (ObjFixB.getFilterData().categoryBits == SWPrototype.ENEMY_DETECTION_SENSOR_BIT) {
                    //((Enemy) ObjFixB.getUserData()).setPlayerToUndetected();

                }
                break;
            case SWPrototype.PLAYER_BIT | SWPrototype.ENEMY_FOV_BIT:
                if(ObjFixA.getFilterData().categoryBits == SWPrototype.ENEMY_FOV_BIT){
                    ((Enemy)ObjFixA.getUserData()).setPlayerToUnseen();

                }else
                if(ObjFixB.getFilterData().categoryBits == SWPrototype.ENEMY_FOV_BIT){
                    ((Enemy)ObjFixB.getUserData()).setPlayerToUnseen();

                }
                break;

        }
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
