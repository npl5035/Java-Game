package com.npl5035.swprototype.Sprites;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.npl5035.swprototype.SWPrototype;
import com.npl5035.swprototype.Screens.PlayScreen;

public class BlasterBolt extends Sprite {

    private World world;
    private Body b2body;
    private TextureRegion redBolt;
    private float posX;
    private float posY;
    private float shooterRadius;
    private float radius = 6;
    private float localAngle;
    private float creationTime;
    private boolean setToDestroy;
    private boolean destroyed;
    private float stateTime;

    public BlasterBolt(World world, PlayScreen screen, float posX, float posY, float angle, float shooterRadius){

        super(screen.getAtlas().findRegion("blasterBolt"));
        this.world = world;

        this.posX = posX;
        this.posY = posY;
        this.shooterRadius = shooterRadius;

        //bullet creation timer to enable despawning
        creationTime = System.nanoTime();

        setToDestroy = false;
        destroyed = false;

        //adjusts the bolts angle for physics and placement
        localAngle = angle;

        defineBlasterBolt();

        //sets bounds of sprite
        redBolt = new TextureRegion(getTexture(), 909, 975,32,32);
        setBounds(0,0, 64/ SWPrototype.PPM, 64/SWPrototype.PPM);
        setRegion(redBolt);

        //rotation around center of sprite
        setOrigin(getWidth()/2, getHeight()/2);
       // sets sprite & body to the correct angle
        b2body.setTransform(b2body.getWorldCenter(), angle);
        setRotation((float) Math.toDegrees(angle));

    }

    /**
     * dictates blaster bolt actions every frame or game update
     * @param dTime
     */
    public void update(float dTime){
        stateTime += dTime;
        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            stateTime = 0;
        }else {
            //connects the Box2D object with the sprite
            setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2);
        }
    }

    /**
     * instantiates the physics body and any physics sensors for blaster bolt
     */
    private void defineBlasterBolt() {

        BodyDef bdef = new BodyDef();
        //math for spawning coordinates
        float boltX = posX + (1.25f*shooterRadius)*(float)(Math.cos(localAngle));
        float boltY = posY + ((1.25f*shooterRadius)*(float)(Math.sin(localAngle)));

        bdef.position.set(boltX, boltY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        //spawns with impulse
        b2body.applyLinearImpulse(15 * (float)(Math.cos(localAngle)), 15* (float)(Math.sin(localAngle)),0,0, true);
        //sets body as bullet to prevent tunneling through thin fixtures
        b2body.setBullet(true);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(radius/SWPrototype.PPM);
        fdef.shape = shape;
        b2body.createFixture(fdef);

        EdgeShape boltEdge = new EdgeShape();
        boltEdge.set(new Vector2(20 / SWPrototype.PPM, 6/ SWPrototype.PPM), new Vector2(20 / SWPrototype.PPM, -6 / SWPrototype.PPM));
        fdef.filter.categoryBits = SWPrototype.BOLT_BIT;
        fdef.filter.maskBits = SWPrototype.PLAYER_BIT |
                SWPrototype.DOOR_BIT |
                SWPrototype.WALL_BIT |
                SWPrototype.ENEMEY_HITBOX_BIT;
        fdef.shape = boltEdge;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

    }


    public boolean getDestroyed() {
        return destroyed;
    }

    /**
     * Sets body and art to bed destroyed on next update
     */
    public void hitDetected() {
        setToDestroy = true;
        //Gdx.app.log("Bolt", "Destroyed");
    }

    /**
     * Overrides sprite draw method. Ceases rendering bolt art if the bolt was destroyed or has persisted in the world
     * too long
     * @param batch
     */
    @Override
    public void draw(Batch batch){
        if(!destroyed){
            if(stateTime < .25f) {
                super.draw(batch);
            }
        }
    }
}
