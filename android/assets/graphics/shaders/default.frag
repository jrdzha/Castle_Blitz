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

const int maxLights = MAXLIGHTMARKER;
const float scale = SCALEMARKER;

//Supports [maxLights] point lights
uniform LOWP vec3 pointLightRGB[maxLights];
uniform LOWP vec2 pointLightXY[maxLights];
uniform LOWP float pointLightIntensity[maxLights];

void main(){

    vec3 lighting = vec3(0.0);
    vec2 pixelPosition = gl_FragCoord.xy;
    float attenuation;
    for(int i = 0; i < maxLights; i++){
        attenuation = (pointLightIntensity[i] * 15.0 / (length((pointLightXY[i] - pixelPosition) / scale) + 10.0));
        lighting += vec3(pointLightRGB[i] * attenuation);
    }
    lighting = max(lighting, vec3(0.8));

    vec4 color = v_color * texture2D(u_texture, v_texCoords);
    color.rgb = ((color.rgb * lighting - 0.5) * contrast) + 0.5 + brightness;
    gl_FragColor = color * 1.5;
}