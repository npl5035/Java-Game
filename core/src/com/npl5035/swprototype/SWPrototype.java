package com.npl5035.swprototype;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.npl5035.swprototype.Screens.MainMenu;
import com.npl5035.swprototype.Screens.PlayScreen;

public class SWPrototype extends Game {



	public SpriteBatch batch;
	//virtual screen size
	public static final int V_WIDTH = 1920;
	public static final int V_HEIGHT = 1080;
	//box2d scale pixels per meter
	public static final float PPM = 128;

	//values for filters
	public static final short DEFAULT_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short WALL_BIT = 4;
	public static final short  DOOR_BIT = 8;
	public static final short  BOLT_BIT = 16;
	public static final short  ENEMY_BIT = 32;
	public static final short  DESTROYED_BIT = 64;
	public static final short ENEMEY_HITBOX_BIT = 128;
	public static final short ENEMY_FOV_BIT = 256;
	public static final short ENEMY_DETECTION_SENSOR_BIT = 512;
	public static final short PLAYER_HITBOX_BIT = 1024;


	
	@Override
	public void create () {

		batch = new SpriteBatch();
		setScreen(new MainMenu(this));
		//setScreen(new PlayScreen(this));




	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {

		super.dispose();
		batch.dispose();

	}
}
