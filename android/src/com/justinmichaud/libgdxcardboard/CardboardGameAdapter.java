package com.justinmichaud.libgdxcardboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.CardBoardAndroidApplication;
import com.badlogic.gdx.backends.android.CardBoardApplicationListener;
import com.badlogic.gdx.backends.android.CardboardCamera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import java.io.IOException;

public class CardboardGameAdapter extends CardBoardAndroidApplication
        implements CardBoardApplicationListener {

    private World world;

    private android.hardware.Camera camera;
    private SurfaceTexture cameraPreview;
    private int cameraTextureUnit = 0;
    private Mesh mesh;
    private ShaderProgram externalShader;
    private Matrix4 mvp = new Matrix4();

    private static String externalFragmentShader =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "\n" +
            "uniform samplerExternalOES u_Texture;\n" +
            "varying vec2 v_TexCoord;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_FragColor = texture2D(u_Texture, v_TexCoord);\n" +
            "}\n";
    private static String externalVertexShader =
            "uniform mat4 u_MVP;\n" +
            "\n" +
            "attribute vec4 a_position;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "\n" +
            "varying vec2 v_TexCoord;\n" +
            "\n" +
            "void main() {\n" +
            "   v_TexCoord = a_texCoord0;\n" +
            "   gl_Position = u_MVP * a_position;\n" +
            "}\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(this, config);
    }

    @Override
    public void create() {

        while (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    0);
        }

        world = new World();

        camera = android.hardware.Camera.open();

        int[] hTex = new int[1];
        GLES20.glGenTextures ( 1, hTex, 0 );
        cameraTextureUnit = hTex[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureUnit);

        cameraPreview = new SurfaceTexture(cameraTextureUnit);
        try {
            camera.setPreviewTexture(cameraPreview);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0));
        mesh.setVertices(new float[]
                {-0.5f, -0.5f, 0, 1, 1, 1, 1, 0, 1,
                  0.5f, -0.5f, 0, 1, 1, 1, 1, 1, 1,
                  0.5f,  0.5f, 0, 1, 1, 1, 1, 1, 0,
                 -0.5f,  0.5f, 0, 1, 1, 1, 1, 0, 0});
        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});

        externalShader = new ShaderProgram(externalVertexShader, externalFragmentShader);
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
        world.dispose();
        camera.unlock();
    }

    @Override
    public void onNewFrame(HeadTransform paramHeadTransform) {
        world.update();
    }

    @Override
    public void onDrawEye(Eye eye) {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //world.drawEye(eye);

        Gdx.gl.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                cameraTextureUnit);
        cameraPreview.updateTexImage();

        externalShader.begin();
        externalShader.setUniformMatrix("u_MVP", mvp);
        mesh.render(externalShader, GL20.GL_TRIANGLES);
        externalShader.end();

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
