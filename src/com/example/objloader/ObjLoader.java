package com.example.objloader;

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
                    vertex.y = 1-Float.parseFloat(content[2]);
                    textureCoods.add(vertex);
                    Log.d("objloader", "tex cood: " + vertex);
                }
            },
            new PrefixCallback() {
                @Override
                public String getPrefix() {
                    return "f";
                }

                @Override
                public void callback(String[] content) {
                    Face face = new Face();
                    for (int i = 1; i < content.length; i++) {
                        String s = content[i];
                        String[] data = s.split("/");
                        //假定有纹理索引
                        short vertexIndex = (short) (Short.parseShort(data[0]) - 1);
                        short texCoodIndex = (short) (Short.parseShort(data[1]) - 1);
                        face.addVertex(vertexs.get(vertexIndex));
                        face.addTexCood(textureCoods.get(texCoodIndex));
                    }

                    faces.add(face);
                }
            }
    };

    List<Vertex> vertexs = new ArrayList<Vertex>();

    List<Vertex> textureCoods = new ArrayList<Vertex>();

    List<Face> faces = new ArrayList<Face>();

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public float[] getVertexArray() {
        float[] vertexArray = new float[faces.size() * 3 * 3];

        for (int i = 0; i < faces.size(); i++) {
            Face f = faces.get(i);
            float[] faceVertexesArray = f.getFaceVertexesArray();
            System.arraycopy(faceVertexesArray, 0, vertexArray, i * 3 * 3, faceVertexesArray.length);
        }
        return vertexArray;
    }

    public float[] getTexCoodArray() {
        float[] texCoodArray = new float[faces.size() * 3 * 2];

        for (int i = 0; i < faces.size(); i++) {
            Face f = faces.get(i);
            float[] faceTexCoodArray = f.getFaceTexCoodArray();
            System.arraycopy(faceTexCoodArray, 0, texCoodArray, i * 3 * 2, faceTexCoodArray.length);
        }
        return texCoodArray;
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
