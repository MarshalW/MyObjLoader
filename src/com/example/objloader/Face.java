package com.example.objloader;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-6-5
 * Time: 下午5:37
 * To change this template use File | Settings | File Templates.
 */
public class Face {

    int vertexesIndex, texCoodsIndex;

    Vertex[] vertexes = new Vertex[3];

    Vertex[] texCoods = new Vertex[3];

    public void addVertex(Vertex vertex) {
        if (vertexesIndex >= vertexes.length) {
            throw new RuntimeException("out of vertex array: " + vertexesIndex);
        }
        vertexes[vertexesIndex++] = vertex;
    }

    public void addTexCood(Vertex vertex) {
        if (texCoodsIndex >= texCoods.length) {
            throw new RuntimeException("out of texcood array: " + texCoodsIndex);
        }
        texCoods[texCoodsIndex++] = vertex;
    }

    public float[] getFaceVertexesArray() {
        float[] vertexesArray = new float[vertexes.length * 3];

        for (int i = 0; i < vertexes.length; i++) {
            float[] vertexPosition = vertexes[i].getPosition3D();
            System.arraycopy(vertexPosition, 0, vertexesArray, i * 3, vertexPosition.length);
        }

        return vertexesArray;
    }

    public float[] getFaceTexCoodArray() {
        float[] texCoodArray = new float[vertexes.length * 2];

        for (int i = 0; i < texCoods.length; i++) {
            float[] texCood = texCoods[i].getPosition2D();
            System.arraycopy(texCood, 0, texCoodArray, i * 2, texCood.length);
        }

        return texCoodArray;
    }

    @Override
    public String toString() {
        return "Face{" +
                "texCoods=" + Arrays.toString(texCoods) +
                ", vertexesIndex=" + vertexesIndex +
                ", texCoodsIndex=" + texCoodsIndex +
                ", vertexes=" + Arrays.toString(vertexes) +
                '}';
    }
}
