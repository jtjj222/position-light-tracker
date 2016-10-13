package com.justinmichaud.lighttracker.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DesktopLauncher {
	public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1400;
        config.height = 800;

        new LwjglApplication(new ApplicationAdapter() {

            private ShapeRenderer shapeRenderer;
            private int frame = 0, framesPerSample = 120;
            private int patternWidth = 100;

            @Override
            public void create() {
                shapeRenderer = new ShapeRenderer();
            }

            @Override
            public void render() {
                Gdx.gl.glClearColor(0,0,0,1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                frame++;
                int elapsedSampleFrames = (frame % framesPerSample);
                int state = elapsedSampleFrames/(framesPerSample/4);

                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

                if (state == 0 || state == 2)
                    shapeRenderer.rect(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                else if (state == 1) { //Horizontal sweep
                    int rows = Gdx.graphics.getWidth()/patternWidth;
                    int totalSamples = framesPerSample/4;
                    int currentSample = (elapsedSampleFrames - state*framesPerSample/4);
                    int x = currentSample * rows / totalSamples * patternWidth;

                    shapeRenderer.rect(x,0, patternWidth, Gdx.graphics.getHeight());
                }
                else { //Vertical sweep
                    int columns = Gdx.graphics.getHeight()/patternWidth;
                    int totalSamples = framesPerSample/4;
                    int currentSample = (elapsedSampleFrames - state*framesPerSample/4);
                    int y = currentSample * columns / totalSamples * patternWidth;

                    shapeRenderer.rect(0,y, Gdx.graphics.getWidth(), patternWidth);
                }
                shapeRenderer.end();
            }

            @Override
            public void dispose() {

            }
        }, config);
	}
}
