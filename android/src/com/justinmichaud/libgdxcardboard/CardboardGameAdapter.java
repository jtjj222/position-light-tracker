package com.justinmichaud.libgdxcardboard;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.CardBoardAndroidApplication;
import com.badlogic.gdx.backends.android.CardBoardApplicationListener;
import com.badlogic.gdx.backends.android.CardboardCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import java.util.ArrayList;

public class CardboardGameAdapter extends CardBoardAndroidApplication
        implements CardBoardApplicationListener {

    private CardboardCamera cam;
    private ArrayList<ModelInstance> models = new ArrayList<ModelInstance>();
    private ModelBatch batch;
    private Environment environment;
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 1000.0f;
    private static final float CAMERA_Z = 0.01f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(this, config);
    }

    @Override
    public void create() {
        cam = new CardboardCamera();
        cam.position.set(0f, 0f, CAMERA_Z);
        cam.lookAt(0,0,0);
        cam.near = Z_NEAR;
        cam.far = Z_FAR;

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        Texture txt = new Texture("badlogic.jpg");
        txt.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        Material mat = new Material(TextureAttribute.createDiffuse(txt));

        ModelBuilder modelBuilder = new ModelBuilder();
        ModelInstance instance = new ModelInstance(modelBuilder.createBox(15f, 15f, 15f, mat,
                VertexAttributes.Usage.Position
                        | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates));
        instance.transform.translate(0, 0, -50);
        models.add(instance);

        instance = new ModelInstance(modelBuilder.createBox(1000f, 0.1f, 1000f, mat,
                VertexAttributes.Usage.Position
                        | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates));
        instance.transform.translate(0, -20, 0);
        models.add(instance);

        batch = new ModelBatch();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        for (ModelInstance instance : models) instance.model.dispose();
    }

    @Override
    public void onNewFrame(HeadTransform paramHeadTransform) {
        models.get(0).transform.rotate(0, 1, 0, Gdx.graphics.getDeltaTime() * 30);
        models.get(0).transform.rotate(0, 0, 1, Gdx.graphics.getDeltaTime() * 30);
        models.get(0).transform.rotate(1, 0, 1, Gdx.graphics.getDeltaTime() * 30);
    }

    @Override
    public void onDrawEye(Eye eye) {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the camera.
        cam.setEyeViewAdjustMatrix(new Matrix4(eye.getEyeView()));

        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
        cam.setEyeProjection(new Matrix4(perspective));
        cam.update();

        batch.begin(cam);
        for (ModelInstance instance : models) batch.render(instance, environment);
        batch.end();
    }

    @Override
    public void onFinishFrame(Viewport paramViewport) {

    }

    @Override
    public void onRendererShutdown() {

    }

    @Override
    public void onCardboardTrigger() {

    }
}
