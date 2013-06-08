package com.example.objloader;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-6-5
 * Time: 下午5:14
 * To change this template use File | Settings | File Templates.
 */
public class ObjLoader {

    Map<String, PrefixCallback> callbackMap = new HashMap<String, PrefixCallback>();

    PrefixCallback[] prefixCallbacks = new PrefixCallback[]{
            new PrefixCallback() {
                @Override
                public String getPrefix() {
                    return "v";
                }

                @Override
                public void callback(String[] content) {
                    Vertex vertex = new Vertex();
                    vertex.x = Float.parseFloat(content[1]);
                    vertex.y = Float.parseFloat(content[2]);
                    vertex.z = Float.parseFloat(content[3]);
                    vertexs.add(vertex);
                }
            },
            new PrefixCallback() {
                @Override
                public String getPrefix() {
                    return "vt";
                }

                @Override
                public void callback(String[] content) {
                    Vertex vertex = new Vertex();
                    vertex.x = Float.parseFloat(content[1]);
                    vertex.y = 1 - Float.parseFloat(content[2]);
                    textureCoods.add(vertex);
                }
            },
            new PrefixCallback() {
                @Override
                public String getPrefix() {
                    return "f";
                }

                @Override
                public void callback(String[] content) {
                    for (int i = 1; i < content.length; i++) {
                        String s = content[i];
                        String[] data = s.split("/");

                        short vertexIndex = (short) (Short.parseShort(data[0]) - 1);
                        vertexIndexes.add(vertexIndex);

                        short texCoodIndex = (short) (Short.parseShort(data[1]) - 1);
                        textureCoodIndexes.add(texCoodIndex);
                    }
                }
            }
    };

    List<Vertex> vertexs = new ArrayList<Vertex>();

    List<Vertex> textureCoods = new ArrayList<Vertex>();

    List<Short> vertexIndexes = new ArrayList<Short>();

    List<Short> textureCoodIndexes = new ArrayList<Short>();

    public ObjLoader(InputStream obj) {
        for (PrefixCallback prefixCallback : prefixCallbacks) {
            callbackMap.put(prefixCallback.getPrefix(), prefixCallback);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(obj));
        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] s = line.split("[ ]");
                doCallback(s);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public float[] getExtVertexArray() {
        float[] array = new float[vertexIndexes.size() * 3];//x,y,z

        for (int i = 0; i < vertexIndexes.size(); i++) {
            short index = vertexIndexes.get(i);
            Vertex v = vertexs.get(index);
            float[] position = v.getPosition3D();
            System.arraycopy(position, 0, array, i * 3, position.length);
        }

        return array;
    }

    public float[] getTexCoodArray() {
        float[] array = new float[textureCoodIndexes.size() * 2];

        for (int i = 0; i < textureCoodIndexes.size(); i++) {
            short index = textureCoodIndexes.get(i);
            Vertex v = textureCoods.get(index);
            float[] position = v.getPosition2D();
            System.arraycopy(position, 0, array, i * 2, position.length);
        }

        return array;
    }

    private void doCallback(String[] content) {
        PrefixCallback callback = callbackMap.get(content[0]);
        if (callback != null) {
            callback.callback(content);
        }
    }

    interface PrefixCallback {
        String getPrefix();

        void callback(String[] content);
    }
}
