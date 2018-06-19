package com.npl5035.swprototype.Sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.npl5035.swprototype.SWPrototype;
import com.npl5035.swprototype.Screens.PlayScreen;


public abstract class InteractiveTileObject {

    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected PlayScreen screen;
    protected Body body;
    protected Fixture fixture;
    protected MapObject object;

    public InteractiveTileObject(PlayScreen screen, MapObject object){
        this.object = object;
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + bounds.getWidth()/2)/ SWPrototype.PPM, (bounds.getY() + bounds.getHeight()/2)/SWPrototype.PPM);

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth()/2 /SWPrototype.PPM, bounds.getHeight()/2 /SWPrototype.PPM);
        fdef.shape = shape;
        fixture = body.createFixture(fdef);

    }


    /**
     * sets the Category filter
     * @param filterBit
     */
    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);

    }

}
