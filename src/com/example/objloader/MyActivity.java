package com.example.objloader;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.io.IOException;

import static android.opengl.GLES20.*;

public class MyActivity extends Activity implements GLSurfaceView.Renderer {

    private float ratio;

    private int width, height;

    private GLSurfaceView surfaceView;

    //投影矩阵
    private float[] projectionMatrix = new float[16];

    //模型矩阵
    private float[] modelMatrix = new float[16];

    //视图矩阵
    private float[] viewMatrix = new float[16];

    //模型视图投影矩阵
    private float[] mvpMatrix = new float[16];

    private ObjLoader loader;

    private Mesh mesh;

    long angle=40;

    String objName;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        surfaceView = new GLSurfaceView(this);
        surfaceView.setEGLContextClientVersion(2);

        //设置背景透明
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        surfaceView.setZOrderOnTop(true);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        surfaceView.setRenderer(this);
//        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        setContentView(surfaceView);

        objName = "task310";

        try {
            loader = new ObjLoader(getAssets().open(objName + ".obj"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0, 0, 0, 0);

        //照相机位置
        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = 37;

        //照相机拍照方向
        float lookX = 0.0f;
        float lookY = 0.0f;
        float lookZ = -1.0f;

        //照相机的垂直方向
        float upX = 0f;
        float upY = 1f;
        float upZ = 0f;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        mesh = new Mesh(this, "vertex_shader.glsl", "fragment_shader.glsl");

        try {
            mesh.loadTexture(getAssets().open(objName + ".png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;
        glViewport(0, 0, width, height);

        ratio = width / (float) height;

        float left = -ratio;
        float right = ratio;
        float top = 1;
        float bottom = -1;
        float near = 7;
        float far = 100;
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angle, 1, 1, 0);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);

        mesh.setVertexBuffer(loader.getVertexArray());
        mesh.setTexCoodBuffer(loader.getTexCoodArray());
        mesh.draw(mvpMatrix);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {

        }

        angle++;
    }
}
