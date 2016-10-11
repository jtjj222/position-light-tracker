package com.justinmichaud.lighttracker;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.vrtoolkit.cardboard.Eye;

import java.io.IOException;

public class CameraRenderer {

    private android.hardware.Camera camera;
    private SurfaceTexture cameraPreview;
    private int cameraTextureUnit = 0;
    private Mesh mesh;
    private ShaderProgram externalShader;

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
                    "attribute vec4 a_position;\n" +
                    "attribute vec2 a_texCoord0;\n" +
                    "\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "\n" +
                    "void main() {\n" +
                    "   v_TexCoord = a_texCoord0;\n" +
                    "   gl_Position = a_position;\n" +
                    "}\n";

    public CameraRenderer(Activity activity) {
        while (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    0);
        }

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
                {-1.6f*0.7f, -1.0f*0.7f, 0, 1, 1, 1, 1, 0, 1,
                  1.6f*0.7f, -1.0f*0.7f, 0, 1, 1, 1, 1, 1, 1,
                  1.6f*0.7f,  1.0f*0.7f, 0, 1, 1, 1, 1, 1, 0,
                 -1.6f*0.7f,  1.0f*0.7f, 0, 1, 1, 1, 1, 0, 0});
        mesh.setIndices(new short[] {0, 1, 2, 2, 3, 0});

        externalShader = new ShaderProgram(externalVertexShader, externalFragmentShader);
    }

    public void drawEye(Eye eye) {
        Gdx.gl.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                cameraTextureUnit);
        cameraPreview.updateTexImage();

        externalShader.begin();
        mesh.render(externalShader, GL20.GL_TRIANGLES);
        externalShader.end();
    }

    public void dispose() {
        camera.unlock();
    }

}
