package com.npl5035.swprototype.Sprites;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.npl5035.swprototype.SWPrototype;
import com.npl5035.swprototype.Screens.PlayScreen;

public class Door extends InteractiveTileObject{
    public Door(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(SWPrototype.DOOR_BIT);
    }


}
