package com.letiko.opengldemo.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.letiko.opengldemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GLSquare {
    static final int COORDS_PER_VERTEX = 4;
    static final int COORDS_PER_TEXTURE = 2;
    static final int vertexStride = COORDS_PER_VERTEX * 4;
    static final int textureStride = COORDS_PER_VERTEX * 2;
    private static final String LOG_TAG = "GLSquare";
    private static final int BYTES_PER_FLOAT = 4;


    static float squareVertices[] = {  // format as x y z w
            1.0f, -1.0f, 0.0f, 1.0f,   // right bottom V0
            1.0f, 1.0f, 0.0f, 1.0f,   // right top V1
            -1.0f, 1.0f, 0.0f, 1.0f,   // left top V2
            -1.0f, -1.0f, 0.0f, 1.0f}; // left bottom V3
    static final int vertexCount = squareVertices.length / COORDS_PER_VERTEX;
    final float[] textureCoords = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f
    };
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec2 a_TexCoordinate;" +
                    "varying vec2 v_TexCoordinate;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "v_TexCoordinate = a_TexCoordinate;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "uniform sampler2D u_Texture;" +
                    "varying vec2 v_TexCoordinate;" +
                    "void main() {" +
                    "gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
                    "}";
    private final Context _context;
    private final FloatBuffer vertexBuffer;
    int[] _colorRenderBuffer;
    int programHandle = 0;
    private short Indices[] = {0, 1, 2, 2, 3, 0}; // order to draw vertices
    private int _positionSlot;
    private int _colorSlot;
    private int _modelViewUniform;
    private int _textureUniform;
    private int _texCoordSlot;
    private int _floorTexture;
    private FloatBuffer textureBuffer;
    private ShortBuffer drawListBuffer;


    public GLSquare(final Context activityContext) {
        _context = activityContext;
        _colorRenderBuffer = new int[1];
        int width = 2048;
        int height = 2048;
        GLES20.glGenRenderbuffers(1, _colorRenderBuffer, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, _colorRenderBuffer[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        programHandle = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(programHandle, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(programHandle, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(programHandle);
        GLES20.glUseProgram(programHandle);
        _positionSlot = GLES20.glGetAttribLocation(programHandle, "vPosition");
        _colorSlot = GLES20.glGetUniformLocation(programHandle, "vColor");
        _modelViewUniform = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix"); //hold the location of the matrix uniform
        _textureUniform = GLES20.glGetAttribLocation(programHandle, "u_Texture");
        _texCoordSlot = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(_texCoordSlot);
        GLES20.glEnableVertexAttribArray(_positionSlot);
        GLES20.glEnableVertexAttribArray(_colorSlot);
        Log.i(LOG_TAG, "init3");
        vertexBuffer = ByteBuffer
                .allocateDirect(squareVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(squareVertices);
        vertexBuffer.position(0);
        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                Indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(Indices);
        drawListBuffer.position(0);

        textureBuffer = ByteBuffer
                .allocateDirect(textureCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer.put(textureCoords).position(0);

        Log.i(LOG_TAG, "init4");

        _floorTexture = loadTexture(_context);
    }


    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    /**
     * The texture pointer
     */
    public static int loadTexture(final Context context) {
        // generate one texture pointer

        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {

            final BitmapFactory.Options options = new BitmapFactory.Options();

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource
                    (context.getResources(), R.drawable.dashground2, options);

            Log.i(LOG_TAG, "loadTexture width = " + Integer.toString(bitmap.getWidth()));

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();

        }

        if (textureHandle[0] == 0) {
            Log.i(LOG_TAG, "load texture error");
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public void draw(float[] mvpMatrix) {

        //Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(_modelViewUniform, 1, false, mvpMatrix, 0);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(_positionSlot, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        GLES20.glVertexAttribPointer(_texCoordSlot, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        //Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _floorTexture);

        //Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(_textureUniform, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, Indices.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        // Disable vertex array
    }
}