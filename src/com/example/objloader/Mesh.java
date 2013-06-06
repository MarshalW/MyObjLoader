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

    private FloatBuffer vertexBuffer, textureCoordBuffer;

    private int[] textureId;

    private float[] vertexes, texCoodes;

    public Mesh(Context context, String vertexShaderFileName, String fragmentShaderFileName) {
        shader = new Shader();
        shader.setProgram(getShaderString(context, vertexShaderFileName), getShaderString(context, fragmentShaderFileName));
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

        //获取shader的aPosition变量“指针”
        int aPosition = this.shader.getHandle("aPosition");
        //给Shader中aPosition变量赋值（顶点缓冲）
        glVertexAttribPointer(aPosition, 3, GL_FLOAT, false,
                3 * 4, vertexBuffer);
        glEnableVertexAttribArray(aPosition);

        //绑定纹理
        glBindTexture(GL_TEXTURE_2D, textureId[0]);

        //设置纹理坐标
        int aTextureCoord = this.shader.getHandle("aTextureCoord");
        glVertexAttribPointer(aTextureCoord, 2, GL_FLOAT, false,
                0, textureCoordBuffer);
        glEnableVertexAttribArray(aTextureCoord);

        //绘制三角形
        glDrawArrays(GL_TRIANGLES, 0, vertexes.length / 3);
    }

    /**
     * 设置顶点缓冲
     */
    public void setVertexBuffer(float[] vertexes) {
        this.vertexes = vertexes;
        vertexBuffer = ByteBuffer.allocateDirect(vertexes.length * 4).
                order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexes);
        vertexBuffer.position(0);
    }

    public void setTexCoodBuffer(float[] texCoodes) {
        this.texCoodes = texCoodes;

        textureCoordBuffer = ByteBuffer.allocateDirect(texCoodes.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(texCoodes);
        textureCoordBuffer.position(0);
    }
}
