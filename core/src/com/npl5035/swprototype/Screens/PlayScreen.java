package com.npl5035.swprototype.Screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.npl5035.swprototype.SWPrototype;
import com.badlogic.gdx.graphics.GL20;
import com.npl5035.swprototype.Scenes.HUD;
import com.npl5035.swprototype.Sprites.Enemy;
import com.npl5035.swprototype.Sprites.Player;
import com.npl5035.swprototype.Tools.B2WorldCreator;
import com.npl5035.swprototype.Tools.WorldContactListener;


public class PlayScreen implements Screen {
    //reference to the game to set screens
    private SWPrototype game;
    private TextureAtlas atlas;

    //basic screen vars
    public OrthographicCamera gameCam;
    private Viewport gameView;
    private HUD hud;

    //tiled map vars
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthoCachedTiledMapRenderer renderer;

    //box2d vars
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    //sprites
    public Player player;
    //public Enemy tempEnemy;

    public PlayScreen(SWPrototype swPrototype) {
        this.game = swPrototype;
        atlas = new TextureAtlas("packed/impsAndRebs.pack");

        //creates cam to follow player through world
        gameCam = new OrthographicCamera();

        //creates Stretch viewport to maintain 1920x1080, will probably modify later
        gameView = new StretchViewport(SWPrototype.V_WIDTH/SWPrototype.PPM, SWPrototype.V_HEIGHT/SWPrototype.PPM, gameCam);

        //temporary hud, used as example for the moment
        hud = new HUD(game.batch);

        //loads map and sets up map renderer
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("testMap1_1.tmx");
        renderer = new OrthoCachedTiledMapRenderer(map, 1/SWPrototype.PPM);

        //initially sets gameGame to be centered at start of map
        gameCam.position.set(gameView.getWorldWidth()/2, gameView.getWorldHeight()/2, 0);

        //creates box2d world and allows box2d debug lines
        world = new World(new Vector2(0,0), true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        //creates player in the game world
        player = new Player( this);
        //tempEnemy = new Enemy(this, gameView.getWorldWidth()/2 , gameView.getWorldHeight()/2 + 3);



        world.setContactListener(new WorldContactListener());
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    /**
     * Called when this screen becomes the current screen for a {@link //Game}.
     */
    @Override
    public void show() {

    }

    public void update(float dTime){


        //takes 1 step in the physics sim (60 times/second)
        world.step(1/60f, 6, 2);

        player.update(dTime);
        player.b2Body.setAwake(true);
        for(Enemy enemy: creator.getEnemies()){
            enemy.update(dTime, player.b2Body.getWorldCenter().x, player.b2Body.getWorldCenter().y);
        }
        //tempEnemy.update(dTime, player.b2Body.getPosition().x, player.b2Body.getPosition().y);
        hud.update(dTime);


        if (player.getBoltList() != null){
            for(int i = 0; i < player.getBoltList().size(); ++i){
                player.getBoltList().get(i).update(dTime);
                // This checks the list for destroyed bolts and removes them to prevent null pointers
                if(player.getBoltList().get(i) == null) {
                    player.getBoltList().remove(i);
                }

            }
        }


        //attaches gameCam to player coordinates
        gameCam.position.x= player.b2Body.getPosition().x;
        gameCam.position.y= player.b2Body.getPosition().y;

        //update gameCam with correct coords after changes take place
        gameCam.update();

        //tells renderer to draw only what camera can see in the game world
        renderer.setView(gameCam);

    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        //separate update logic from render
        update(delta);

        //clear game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render the game map
        renderer.render();

        //renders box2d debug lines
        b2dr.render(world, gameCam.combined);


        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        if (player.getBoltList() != null){
            for(int i = 0; i < player.getBoltList().size(); ++i){
                player.getBoltList().get(i).draw(game.batch);
            }
        }

        player.draw(game.batch);
        //tempEnemy.draw(game.batch);
        //tempEnemy.sr.setProjectionMatrix(gameView.getCamera().combined);
        //tempEnemy.sr.begin(ShapeRenderer.ShapeType.Line);
       // tempEnemy.sr.line(tempEnemy.point1, tempEnemy.point2);
       // tempEnemy.sr.line(tempEnemy.collision, tempEnemy.normal);
        //tempEnemy.sr.end();
        for(Enemy enemy: creator.getEnemies()){
            enemy.draw(game.batch);
        }

        game.batch.end();

        //sets batch to draw what hud camera sees
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
    }

    /**
     * @param width
     * @param height
     * @see //ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        gameView.update(width, height);
    }

    /**
     * @see //ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see //ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link //Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        //disposes all open resources
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        b2dr.dispose();
        hud.dispose();


    }
}
