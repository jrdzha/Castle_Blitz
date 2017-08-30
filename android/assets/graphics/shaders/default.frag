#ifdef GL_ES
#define LOWP lowp
#define HIGHP highp
precision mediump float;
#else
#define LOWP
#define HIGHP
#endif

varying HIGHP vec4 v_color;
varying HIGHP vec2 v_texCoords;
uniform HIGHP sampler2D u_texture;

uniform float brightness;
uniform float contrast;

const int maxLights = MAXLIGHTMARKER;
const float scale = SCALEMARKER;

//Supports 100 point lights
uniform HIGHP vec3 pointLightRGB[maxLights];
uniform HIGHP vec2 pointLightXY[maxLights];
uniform HIGHP float pointLightIntensity[maxLights];

void main(){

    vec3 lighting = vec3(0.0);
    vec2 pixelPosition = gl_FragCoord.xy;
    float attenuation;
    for(int i = 0; i < maxLights; i++){
        attenuation = (pointLightIntensity[i] / (length((pointLightXY[i] - pixelPosition) / scale) + 10.0));
        lighting += vec3(pointLightRGB[i] * attenuation);
    }
    lighting = max(lighting, vec3(0.8));

    vec4 color = v_color * texture2D(u_texture, v_texCoords);
    color.rgb = ((color.rgb * lighting - 0.5) * contrast) + 0.5 + brightness;
    gl_FragColor = color;
}