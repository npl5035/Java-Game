package com.npl5035.swprototype.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.npl5035.swprototype.SWPrototype;
import com.npl5035.swprototype.Screens.PlayScreen;
import com.npl5035.swprototype.Sprites.Enemy;
import com.npl5035.swprototype.Sprites.Wall;


public class B2WorldCreator {



    private Array<Enemy> enemies;

    public B2WorldCreator(PlayScreen screen){

        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //creates walls + fixtures
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){

            new Wall(screen, object);
        }

        //creates all enemies
        enemies = new Array<Enemy>();
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){

            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            enemies.add(new Enemy(screen, rect.getX() / SWPrototype.PPM, rect.getY() / SWPrototype.PPM));
        }

    }

    /**
     * Returns an Array of type Enemy
     * @return
     */
    public Array<Enemy> getEnemies() {
        return enemies;
    }

}
