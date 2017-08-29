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

//Supports 100 point lights
uniform vec3 pointLightRGB[100];
uniform vec2 pointLightXY[100];
uniform float pointLightIntensity[100];

void main(){

    vec4 lighting = vec4(1.0, 1.0, 1.0, 1.0);
    vec2 pixelPosition = gl_FragCoord.xy;
    float attenuation;
    for(int i = 0; i < 30; i++){
        //if(pointLightIntensity[i] > 0.0){
            attenuation = (pointLightIntensity[i] / (length(pointLightXY[i] - pixelPosition) + 10.0));
            lighting = lighting + (vec4(pointLightRGB[i], 1.0) * vec4(attenuation, attenuation, attenuation, pow(attenuation, 3.0)));
        //}
    }

        //lighting = vec4(1.0, 1.0, 1.0, 1.0);
        //lighting = min(lighting, vec4(1.5, 1.5, 1.5, 1.0));
        lighting = lighting - 1.0;
        lighting = max(lighting, vec4(0.8, 0.8, 0.8, 1.0));

    vec4 color = v_color * texture2D(u_texture, v_texCoords) * lighting;
    /*
    if(color.a != 0.0){
        //color.rgb /= color.a;

        //color.rgb *= color.a;
    }
    */
    color.rgb = ((color.rgb - 0.5) * max(contrast, 0.0)) + 0.5;
    color.rgb += brightness;

    gl_FragColor = color;

    //grayscale
    //gl_FragColor = vec4((color.r + color.g + color.b) / 3.0, (color.r + color.g + color.b) / 3.0, (color.r + color.g + color.b) / 3.0, color.a);
}