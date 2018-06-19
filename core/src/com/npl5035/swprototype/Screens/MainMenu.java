package com.npl5035.swprototype.Screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.npl5035.swprototype.SWPrototype;

public class MainMenu implements Screen{

    private final SWPrototype swPrototype;
    private Viewport viewport;
    private Stage stage;



    public MainMenu(SWPrototype game){


        this.swPrototype = game;
        viewport = new FitViewport(SWPrototype.V_WIDTH, SWPrototype.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, (game).batch);
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.GOLDENROD);

        Table table = new Table();
        Table table2 = new Table();
        table.center();
        table2.center();
        table.setFillParent(true);
        table2.setFillParent(true);
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.font =  new BitmapFont();

        Label titleLabel = new Label("STAR WARS", font);
        Label subtitleLabel = new Label("Shadow Trooper", font);
        TextButton playButton = new TextButton("Play", tbs);
        TextButton exitButton = new TextButton("Quit", tbs);

        table.debugCell();
        table2.debugCell();
        table.add(titleLabel).expandX();
        table.row();
        table.add(subtitleLabel).expandX().padTop(10f);

        table2.add(playButton).right().padRight(20).padTop(viewport.getScreenHeight()/3);
        table2.add(exitButton).right().padLeft(viewport.getScreenWidth()/4).padTop(viewport.getScreenHeight()/3);

        Gdx.input.setInputProcessor(stage);


        playButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new PlayScreen(swPrototype));
                dispose();
            }
        });
        exitButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                dispose();
            }
        });
        stage.addActor(table);
        stage.addActor(table2);

    }

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {

    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {

        stage.dispose();

    }
}
