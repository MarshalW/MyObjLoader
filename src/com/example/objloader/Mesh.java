package com.example.objloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-5-16
 * Time: 下午3:51
 * To change this template use File | Settings | File Templates.
 */
public class Mesh {

    private Shader shader;

    private int[] textureId;

    private int vertexBufferId;

    private int texCoodBufferId;

    private int vertexCount;

    public Mesh(Context context, String vertexShaderFileName, String fragmentShaderFileName) {
        shader = new Shader();
        shader.setProgram(getShaderString(context, vertexShaderFileName), getShaderString(context, fragmentShaderFileName));

        int[] bo = new int[2];
        glGenBuffers(2, bo, 0);

        vertexBufferId = bo[0];
        texCoodBufferId = bo[1];

        if (vertexBufferId == 0) {
            throw new RuntimeException("buffer object generate error.");
        }
    }

    private String getShaderString(Context context, String name) {
        StringBuilder builder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(name)));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.toString();
    }


    public void loadTexture(InputStream bitmapInputStream) {
        Bitmap texture = BitmapFactory.decodeStream(bitmapInputStream);
        this.loadTexture(texture);
        texture.recycle();
    }

    public void loadTexture(Bitmap texture) {
        if (textureId == null) {
            //创建纹理指针
            textureId = new int[1];
            glGenTextures(1, textureId, 0);

            //绑定纹理
            glBindTexture(GL_TEXTURE_2D, textureId[0]);

            //设置纹理滤镜
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        }

        //加入纹理
        glEnable(GL_TEXTURE_2D);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, texture, 0);
        glDisable(GL_TEXTURE_2D);
    }

    public void draw(float[] projectionMatrix) {
        this.shader.useProgram();

        glUniformMatrix4fv(shader.getHandle("uProjectionM"), 1, false, projectionMatrix, 0);

        //纹理处理
        glBindTexture(GL_TEXTURE_2D, textureId[0]);
        glBindBuffer(GL_ARRAY_BUFFER, texCoodBufferId);
        int aTextureCoord = this.shader.getHandle("aTextureCoord");
        glVertexAttribPointer(aTextureCoord, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(aTextureCoord);

        //获取shader的aPosition变量“指针”
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        int aPosition = this.shader.getHandle("aPosition");
        glVertexAttribPointer(aPosition, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(aPosition);

        glDrawArrays(GL_TRIANGLES, 0, vertexCount);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void setVertexBuffer(float[] vertexArray) {
        //设置vbo
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexArray.length * 4).
                order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexArray);
        vertexBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        vertexCount = vertexArray.length/3;
    }

    public void setTexCoodBuffer(float[] texCoodArray) {
        //设置vbo
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(texCoodArray.length * 4).
                order(ByteOrder.nativeOrder()).asFloatBuffer().put(texCoodArray);
        vertexBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, texCoodBufferId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
