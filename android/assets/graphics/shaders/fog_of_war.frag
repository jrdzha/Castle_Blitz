#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform float brightness;
uniform float contrast;

void main(){
    vec4 color = v_color * texture2D(u_texture, v_texCoords);
    if(color.a != 0.0){
        //color.rgb /= color.a;

        //color.rgb *= color.a;
    }
    color.rgb = ((color.rgb - 0.5) * max(contrast, 0.0)) + 0.5;
    color.rgb += brightness;

    gl_FragColor = color;
}