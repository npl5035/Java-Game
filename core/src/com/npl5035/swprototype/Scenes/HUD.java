package com.npl5035.swprototype.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.npl5035.swprototype.SWPrototype;


public class HUD implements Disposable{
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    private Label countDownLabel;
    private Label scoreLabel;
    private Label levelLabel;
    private Label timeLabel;

    public HUD(SpriteBatch sb){

        worldTimer = 0;
        timeCount = 0;
        score = 0;

        viewport = new StretchViewport(SWPrototype.V_WIDTH, SWPrototype.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);
        countDownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(timeLabel).expandX().padTop(10);
        table.add(countDownLabel).expandX().padTop(10);

        stage.addActor(table);



    }

    public void update(float dTime){
        timeCount += dTime;
        if(timeCount >= 1){
            worldTimer ++;
            countDownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public void addEXP(){

    }

    /**
     * Releases all resources of this object.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
