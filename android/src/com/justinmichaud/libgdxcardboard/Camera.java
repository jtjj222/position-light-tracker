package com.justinmichaud.libgdxcardboard;

import com.badlogic.gdx.backends.android.CardboardCamera;
import com.badlogic.gdx.math.Matrix4;
import com.google.vrtoolkit.cardboard.Eye;

public class Camera {

    public final CardboardCamera camera;

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 1000.0f;

    public Camera() {
        camera = new CardboardCamera();
        camera.position.set(0f, 0f, 0);
        camera.lookAt(0,0,1);
        camera.near = Z_NEAR;
        camera.far = Z_FAR;
    }

    public void update() {

    }

    public void updateEye(Eye eye) {
        camera.setEyeViewAdjustMatrix(new Matrix4(eye.getEyeView()));

        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
        camera.setEyeProjection(new Matrix4(perspective));
        camera.update();
    }
}
