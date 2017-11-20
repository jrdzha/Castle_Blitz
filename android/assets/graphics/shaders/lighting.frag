#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying LOWP vec2 v_texCoords;
uniform LOWP sampler2D u_texture;

uniform float brightness;
uniform float contrast;

const float scale = SCALEMARKER;

uniform LOWP vec3 pointLightRGB;
uniform LOWP vec2 pointLightXY;
uniform LOWP float pointLightIntensity;

void main(){
    vec4 color = v_color * texture2D(u_texture, v_texCoords);
    lighting = max(vec3(pointLightRGB * pointLightIntensity / (length((pointLightXY - gl_FragCoord.xy) / scale) + 10.0)), vec3(0.8));
    color.rgb = ((color.rgb * lighting - 0.5) * contrast) + 0.5 + brightness;
    gl_FragColor = color * 1.5;
}