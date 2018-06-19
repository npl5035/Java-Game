package com.npl5035.swprototype.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.npl5035.swprototype.SWPrototype;
import com.npl5035.swprototype.Screens.PlayScreen;

import java.util.ArrayList;


public class Player extends Sprite {



    public enum State {STANDING, RUNNING, SHOOTING}
    public State currentState;
    public State previousState;
    private float stateTimer;
    private float shootTimer;

    public World world;
    public PlayScreen screen;
    public Body b2Body;
    private TextureRegion playerStand;
    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> playerShoot;

    private ArrayList<BlasterBolt> boltList;


    public Player(PlayScreen screen){
        super(screen.getAtlas().findRegion("playerStand"));

        //creates player texture region for standing player
        playerStand = new TextureRegion(getTexture(), 391, 489, 128, 128);
        //sets initial values for players location, width and height and initial frame as playerStand
        setBounds(0,0, 128/SWPrototype.PPM, 128/SWPrototype.PPM);
        setRegion(playerStand);

        screen.getAtlas().findRegion("playerRun");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        while (frames.size < 4) {
            frames.add(new TextureRegion(getTexture(), 391, 619, 128, 128));
            frames.add(new TextureRegion(getTexture(), 521, 749, 128, 128));
            frames.add(new TextureRegion(getTexture(), 261, 359, 128, 128));
            frames.add(new TextureRegion(getTexture(), 521, 749, 128, 128));
            frames.add(new TextureRegion(getTexture(), 391, 619, 128, 128));
        }

        playerRun = new Animation(0.195f, frames);
        setBounds(0,0, 128/SWPrototype.PPM, 128/SWPrototype.PPM);
        frames.clear();

        screen.getAtlas().findRegion("playerFire");
        while (frames.size < 2) {
            frames.add(new TextureRegion(getTexture(), 131, 231, 128, 128));
            frames.add(new TextureRegion(getTexture(), 131, 101, 128, 128));
            frames.add(new TextureRegion(getTexture(), 261, 749, 128, 128));
        }

        playerShoot = new Animation(8f, frames);
        setBounds(0,0, 128/SWPrototype.PPM, 128/SWPrototype.PPM);
        frames.clear();

        //playerShoot = new TextureRegion(getTexture(), 909, 975, 32, 32);
        //setBounds(0,0, 128/SWPrototype.PPM, 128/SWPrototype.PPM);

        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0f;
        shootTimer = 0f;
        boltList = new ArrayList<BlasterBolt>();

        //defines player in box2d
        definePlayer();
    }



    /**
     * updates player sprite and physics body every frame
     * @param dTime
     */
    public void update(float dTime){
        setOrigin(getWidth()/2, getHeight()/2);
        setPosition(b2Body.getPosition().x - getWidth()/2, b2Body.getPosition().y - getHeight()/2);
        setRegion(getFrame(dTime));
        handleInput(dTime, screen);

    }

    /**
     * Handles inputes by the user performed on the PlayScreen. Specifically player controls
     * Aligns player physics body and art towards the mouse cursor on the screen
     * Uses impulse force for player movement
     */
    public void handleInput(float dTime, PlayScreen screen){
        Vector2 restVel = new Vector2(0,0);
        //fires(spawns) blaster bolt

        if(Gdx.input.isKeyPressed(Input.Keys.D) && b2Body.getLinearVelocity().x <= 2){
            b2Body.applyLinearImpulse(new Vector2(2f, 0), b2Body.getWorldCenter(), true);
        }else if(Gdx.input.isKeyPressed(Input.Keys.A)&& b2Body.getLinearVelocity().x >= -2){
            b2Body.applyLinearImpulse(new Vector2(-2f, 0), b2Body.getWorldCenter(), true);
        }else if(Gdx.input.isKeyPressed(Input.Keys.W)&& b2Body.getLinearVelocity().y <= 2){
            b2Body.applyLinearImpulse(new Vector2(0, 2f), b2Body.getWorldCenter(), true);
        }else if(Gdx.input.isKeyPressed(Input.Keys.S)&& b2Body.getLinearVelocity().y >= -2){
            b2Body.applyLinearImpulse(new Vector2(0, -2f), b2Body.getWorldCenter(), true);
        }else if(Gdx.input.justTouched()){
            playerBolt(world, screen, b2Body.getWorldCenter().x, b2Body.getWorldCenter().y, b2Body.getAngle(), 32/SWPrototype.PPM);
        }else{
            if(b2Body.getLinearVelocity() != restVel){
                float x = b2Body.getLinearVelocity().x;
                float y = b2Body.getLinearVelocity().y;
                b2Body.applyLinearImpulse( new Vector2(-x * .5f, -y * .5f), b2Body.getWorldCenter(), true);
            }
        }
        //angles physics body and sprite to mouse position
        if(Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0){

            Vector3 mousePos3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            screen.gameCam.unproject(mousePos3);
            Vector2 mousePos2 = new Vector2(mousePos3.x, mousePos3.y);
            float deltaX = mousePos2.x - b2Body.getPosition().x ;
            float deltaY = mousePos2.y - b2Body.getPosition().y;
            float desiredAngle = (float)(Math.atan2(deltaY, deltaX));
            if(desiredAngle < 0){
                desiredAngle += (Math.PI * 2f);
            }
            b2Body.setTransform(b2Body.getWorldCenter(), desiredAngle);
            setRotation((float) Math.toDegrees(desiredAngle));
        }

    }

    /**
     * Generates a new blaster bolt in the game world and adds it to the ArrayList boltList for tracking and destruction
     * @param world
     * @param screen
     * @param posX
     * @param posY
     * @param angle
     * @param shooterRadius
     */
    public void playerBolt(World world, PlayScreen screen, float posX, float posY, float angle, float shooterRadius){

        boltList.add(new BlasterBolt(world, screen, posX, posY, angle, shooterRadius));

    }

    /**
     * Returns an ArrayList of any blaster bolts the player has fired
     * used to track and destroy blaster bolts
     * @return
     */
    public ArrayList<BlasterBolt> getBoltList(){

        return boltList;
    }

    /**
     * Returns the associated animation or art for the players current state
     * @param dTime
     * @return
     */
    public TextureRegion getFrame (float dTime){
        currentState = getState();
        TextureRegion region;
        switch(currentState){
            case RUNNING:
                region = playerRun.getKeyFrame(stateTimer, true);
                break;
            case SHOOTING:
                region = playerShoot.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
                region = playerStand;
                break;
            default:
                region = playerStand;
                break;
        }
        stateTimer = currentState == previousState ? stateTimer +dTime : 0;
        previousState = currentState;
        return region;
    }

    /**
     * Returns player current state.
     * used to mainly assign correct animations
     * @return State
     */
    public State getState(){
        if(((Gdx.input.isKeyPressed(Input.Keys.W)) ||
                (Gdx.input.isKeyPressed(Input.Keys.A)) ||
                (Gdx.input.isKeyPressed(Input.Keys.S)) ||
                (Gdx.input.isKeyPressed(Input.Keys.D))) &&
                (!Gdx.input.justTouched())){
            return State.RUNNING;
        }else if((Gdx.input.justTouched()) || ((Gdx.input.isKeyPressed(Input.Keys.W)) ||
                (Gdx.input.isKeyPressed(Input.Keys.A)) ||
                (Gdx.input.isKeyPressed(Input.Keys.S)) ||
                (Gdx.input.isKeyPressed(Input.Keys.D))) &&
                (Gdx.input.justTouched())){
            return State.SHOOTING;
        }else{
            return State.STANDING;
        }
    }

    /**
     * instantiates the physics body and any physics sensors for Player
     */
    public void definePlayer() {

        BodyDef bdef = new BodyDef();
        bdef.position.set(64 / SWPrototype.PPM,64 /SWPrototype.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2Body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(32/SWPrototype.PPM);
        //categorize the fixture and set allowed collisions
        fdef.filter.categoryBits = SWPrototype.PLAYER_BIT;
        fdef.filter.maskBits = SWPrototype.DEFAULT_BIT |
                SWPrototype.DOOR_BIT |
                SWPrototype.WALL_BIT |
                SWPrototype.BOLT_BIT |
                SWPrototype.ENEMY_BIT |
                SWPrototype.ENEMY_DETECTION_SENSOR_BIT |
                SWPrototype.ENEMY_FOV_BIT;

        fdef.shape = shape;
        b2Body.createFixture(fdef).setUserData(this);

        CircleShape body = new CircleShape();
        body.setRadius(36/SWPrototype.PPM );
        fdef.shape = body;
        fdef.isSensor = true;
        fdef.filter.categoryBits = SWPrototype.PLAYER_HITBOX_BIT;
        fdef.filter.maskBits = SWPrototype.BOLT_BIT;


        b2Body.createFixture(fdef).setUserData(this);
    }
}
