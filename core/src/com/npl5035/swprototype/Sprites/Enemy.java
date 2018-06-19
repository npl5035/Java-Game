package com.npl5035.swprototype.Sprites;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.npl5035.swprototype.SWPrototype;
import com.npl5035.swprototype.Screens.PlayScreen;




public class Enemy extends Sprite {
    protected World world;
    private Body enemyBody;
    private TextureRegion enemyStand;
    protected PlayScreen screen;

    private float stateTime;
    private Animation fireAnimation;
    private Animation deathAnimation;
    private Array<TextureRegion> frames;

    private boolean setToDestroy;
    private boolean destroyed;
    private boolean playerSeen;
    private boolean playerDetected;
    private boolean playerInRange;

    public enum State {GUARD, CHASE, RESUME_GUARD, ALERT, SHOOT}

    public State currentState;
    public State previousState;
    private float stateTimer;
    private Vector2 guardPost;
    private float deltaX, deltaY;

    //raycast test
    public ShapeRenderer sr = new ShapeRenderer();
    public Vector2 point1, point2, collision, normal;
    private float closestFraction = 2f;
    short raycastedFix = 0;
    RayCastCallback callback;

    /**
     * calls world.rayCast and sets the RayCastCallBack
     * Checks for line of sight on player against any physics objects that might be in between the player and the enemy
     */
    private void rayCaster() {

         callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
               // collision.set(point);
                //Enemy.this.normal.set(normal).add(point);
                if (fraction < Enemy.this.closestFraction) {
                    Enemy.this.closestFraction = fraction;
                    //Enemy.this.collision.set(point);
                    raycastedFix = fixture.getFilterData().categoryBits;

                }
                return fraction;
            }
        };
        point1 = enemyBody.getPosition();
        point2 = screen.player.b2Body.getPosition();
        world.rayCast(callback, point1, point2);
        callback = null;
        closestFraction = 2f;
    }


    public Enemy(PlayScreen screen, float startX, float startY){
        super(screen.getAtlas().findRegion("enemyStand"));
        this.screen = screen;
        this.world = screen.getWorld();

        point1 = new Vector2();
        point2 = new Vector2();
        collision= new Vector2();
        normal = new Vector2();


        enemyStand = new TextureRegion(getTexture(), 779, 879, 128, 128);

        // Setting bounds of sprite
        setBounds(0,0, 128/SWPrototype.PPM, 128/SWPrototype.PPM);
        setRegion(enemyStand);
        defineEnemy(startX, startY);

        currentState = State.GUARD;
        previousState = State.GUARD;
        stateTimer = 0f;

        setToDestroy = false;
        destroyed = false;
        playerDetected = false;
        playerSeen = false;
        playerInRange = false;


    }

    /**
     * Updates enemy every frame/ game update
     * @param dTime
     * @param playerX
     * @param playerY
     */
    public void update(float dTime, float playerX, float playerY) {
        stateTime += dTime;
        if(setToDestroy && !destroyed){
            world.destroyBody(enemyBody);
            destroyed = true;
            stateTime = 0;
        }else {
            //connects the Box2D object with the sprite
            setOrigin(getWidth() / 2, getWidth() / 2);
            setPosition(enemyBody.getPosition().x - getWidth() / 2, enemyBody.getPosition().y - getWidth() / 2);
        }
        rayCaster();
        System.out.println(raycastedFix);
        if(raycastedFix == SWPrototype.PLAYER_BIT || raycastedFix == SWPrototype.PLAYER_HITBOX_BIT){
            rotateToPlayer();
        }
        raycastedFix = 0;
        getDecision(dTime);
    }

    /**
     * instantiates the physics body and any physics sensors for Enemy
     */
    public void defineEnemy(float startX, float startY){
        BodyDef bdef = new BodyDef();
        bdef.position.set(startX, startY);
        guardPost = new Vector2(startX, startY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        enemyBody = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(32 / SWPrototype.PPM);
        fdef.filter.categoryBits = SWPrototype.ENEMY_BIT;
        fdef.filter.maskBits = SWPrototype.PLAYER_BIT |
                SWPrototype.DEFAULT_BIT |
                SWPrototype.DOOR_BIT |
                SWPrototype.WALL_BIT |
                SWPrototype.BOLT_BIT |
                SWPrototype.ENEMY_BIT;
        fdef.shape = shape;
        enemyBody.createFixture(fdef).setUserData(this);

        //creates enemy hitbox
        CircleShape body = new CircleShape();
        body.setRadius(36/SWPrototype.PPM );
        fdef.shape = body;
        fdef.isSensor = true;
        fdef.restitution = 0;
        fdef.filter.categoryBits = SWPrototype.ENEMEY_HITBOX_BIT;
        fdef.filter.maskBits = SWPrototype.BOLT_BIT;

        enemyBody.createFixture(fdef).setUserData(this);


        //creates enemy field of view
        PolygonShape fov = new PolygonShape();
        Vector2[] vertices = new Vector2[3];
        vertices[0] = new Vector2(500/SWPrototype.PPM, 200/SWPrototype.PPM);
        vertices[1] = new Vector2(500/SWPrototype.PPM, -200/SWPrototype.PPM);
        vertices[2] = new Vector2(0, 0);
        fov.set(vertices);

        fdef.shape = fov;
        fdef.restitution = 0f;
        fdef.isSensor = true;
        fdef.filter.categoryBits = SWPrototype.ENEMY_FOV_BIT;
        fdef.filter.maskBits = SWPrototype.PLAYER_BIT |
                SWPrototype.DOOR_BIT |
                SWPrototype.WALL_BIT |
                SWPrototype.ENEMY_BIT;
        enemyBody.createFixture(fdef).setUserData(this);

        //creates enemy detection field
        CircleShape sensor = new CircleShape();
        sensor.setRadius(300/SWPrototype.PPM );
        fdef.shape = sensor;
        fdef.isSensor = true;
        fdef.restitution = 0;
        fdef.filter.categoryBits = SWPrototype.ENEMY_DETECTION_SENSOR_BIT;
        fdef.filter.maskBits = SWPrototype.PLAYER_BIT;

        enemyBody.createFixture(fdef).setUserData(this);

    }

    public State getState(){
       if((playerDetected && !playerSeen && !screen.player.b2Body.getLinearVelocity().isZero()) || (playerDetected && !playerSeen && Gdx.input.justTouched())){
           return State.ALERT;
       }else if(playerSeen && !playerDetected){
           return State.CHASE;
       }else if(playerSeen && playerDetected){
           return State.SHOOT;
       }else if(!playerSeen && !playerDetected && enemyBody.getWorldCenter() != guardPost ){
           return State.RESUME_GUARD;
       }else{
          // System.out.println("Guard State!");
           return State.GUARD;
       }
    }

    public void getDecision(float dTime){
        currentState = getState();
        switch(currentState){
            case GUARD:
                /*float desiredAngle = (float)(Math.atan2(MathUtils.random((float) -Math.PI, (float) Math.PI), MathUtils.random((float) -Math.PI, (float) Math.PI)));
                if(desiredAngle < 0){
                    desiredAngle += (Math.PI * 2f);
                }
                enemyBody.setTransform(enemyBody.getWorldCenter(), desiredAngle);
                setRotation((float) Math.toDegrees(desiredAngle));*/
                break;
            case ALERT:
                //Gdx.app.log("Enemy", "I see you");
                break;
            case CHASE:
                break;
            case SHOOT:
                //Gdx.app.log("Enemy", "Im gonna shoot you");
                break;
            case RESUME_GUARD:
                currentState = State.GUARD;
                break;
        }
        stateTimer = currentState == previousState ? stateTimer +dTime : 0;
        previousState = currentState;
    }

    public void setPlayerToUnseen(){
        playerSeen = false;
    }
    public void setPlayerToSeen(){
        //playerSeen = true;
        //Gdx.app.log("Enemy", "Seen");
    }
    public void setPlayerToDetected(){
        playerDetected = true;

    }
    public void setPlayerToUndetected(){
        playerDetected = false;
        raycastedFix = 0;

    }

    //range needs to be added as a new fixture sensor in the future
    public void playerRangeCheck(){
        if(playerSeen && playerDetected){
            playerInRange = true;
        }else{playerInRange = false;
        }
    }

    public void rotateToPlayer(){
        //sets rotation of enemy body and sprite towards player (temporary until tracking implemented)
        deltaX = screen.player.b2Body.getWorldCenter().x - enemyBody.getPosition().x;
        deltaY = screen.player.b2Body.getWorldCenter().y - enemyBody.getPosition().y;
        float desiredAngle = (float)(Math.atan2(deltaY, deltaX));
        if(desiredAngle < 0){
            desiredAngle += (Math.PI * 2f);
        }
        enemyBody.setTransform(enemyBody.getWorldCenter(), desiredAngle);
        setRotation((float) Math.toDegrees(desiredAngle));
    }


    public void hitDetected() {
        setToDestroy = true;
    }

}