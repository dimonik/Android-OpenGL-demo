package com.letiko.opengldemo.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.letiko.opengldemo.model.GLSquare;
import com.letiko.opengldemo.utils.MatrixHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AnotherGL20Renderer implements GLSurfaceView.Renderer {
    private static final String LOG_TAG = "AnotherGL20Renderer";
    private final Context mActivityContext;
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16]; //defines frustum
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    GLSquare mSquare = null;
    private float minL = 10f; //0.98f;
    private float maxL = 51f; //1.02f;
    private float s = maxL; //zoom starting value
    private Boolean toZoomin = true;


    public AnotherGL20Renderer(final Context activityContext) {
        mActivityContext = activityContext;
    }


    //Called every time surface is created to set up the view's OpenGL ES environment
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        Log.i(LOG_TAG, "onSurfaceCreated");
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); //probably not needed here
        //gl.glClearDepthf(1.0f);                     //Depth Buffer Setup
        mSquare = new GLSquare(mActivityContext);
    }


    //Called if the geometry of the viewport changes, for example when the device's screen orientation changes.
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix, 1, (float) width
                / (float) height, 1f, 53f);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0.3f, -1f);
    }


    //Called for each redraw of the view
    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0,
                0f, 0f, s, //eye this is where the eye will be
                0f, 0f, 0f,  //center - you’ll be looking toward the origin in front of you
                0f, 1f, 0f); //up your head will be pointing straight up

        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
        mSquare.draw(modelViewProjectionMatrix);
//turnoff to stop
        if (toZoomin) {
            if (s > minL) {
                s -= 0.1f;
            } else {
                toZoomin = false;
            }
        }

        if (!toZoomin) {
            if (s <= maxL) {
                s += 0.1f;
            } else {
                toZoomin = true;
            }
        }
//turnoff to stop		
    }

}
