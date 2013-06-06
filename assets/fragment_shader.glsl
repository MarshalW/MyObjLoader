precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D texture;
void main() {
   gl_FragColor = texture2D(texture, vTextureCoord);
   //gl_FragColor = vec4(1,0.8,0.2,0);
}