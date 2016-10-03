package com.justinmichaud.libgdxcardboard;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

public class World {

    public final Camera camera;
    public final TexturedCube p1;
    public final TexturedCube floor;

    private final ModelBatch batch;
    private final Environment environment;

    public World() {
        batch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera = new Camera();
        camera.camera.position.set(0,0,-2);

        p1 = new TexturedCube("p1", 0.5f,0.5f,0.5f, 1,1, "badlogic.jpg");
        floor = new TexturedCube("floor", 100,0,100, 10f, 10f, "floor.png");
        floor.position.set(0,-2,0);
    }

    public void update(HeadTransform paramHeadTransform) {
        camera.update(paramHeadTransform);
        p1.update();
        floor.update();
    }

    public void drawEye(Eye eye) {
        camera.updateEye(eye);
        batch.begin(camera.camera);
        p1.draw(batch);
        floor.draw(batch);
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        p1.dispose();
    }

}
