package com.npl5035.swprototype.Sprites;


        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.maps.MapObject;
        import com.badlogic.gdx.maps.objects.RectangleMapObject;
        import com.badlogic.gdx.maps.tiled.TiledMap;
        import com.badlogic.gdx.math.Rectangle;
        import com.badlogic.gdx.physics.box2d.*;
        import com.npl5035.swprototype.SWPrototype;
        import com.npl5035.swprototype.Screens.PlayScreen;

public class Wall extends InteractiveTileObject{

    BodyDef bdef = new BodyDef();
    PolygonShape shape = new PolygonShape();
    FixtureDef fdef = new FixtureDef();
    Body body;

    public Wall(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(SWPrototype.WALL_BIT);
    }

}
