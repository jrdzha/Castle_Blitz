#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform float radius;
uniform vec2 dir;

void main() {
	vec4 sum = vec4(0.0);
	vec2 tc = v_texCoords;
    
    float hstep = dir.x;
    float vstep = dir.y;
    
	sum += texture2D(u_texture, v_texCoords - (vec2(4.0) * dir * radius)) * 0.000229;
	sum += texture2D(u_texture, v_texCoords - (vec2(3.0) * dir * radius)) * 0.005977;
	sum += texture2D(u_texture, v_texCoords - (vec2(2.0) * dir * radius)) * 0.060598;
	sum += texture2D(u_texture, v_texCoords - (vec2(1.0) * dir * radius)) * 0.241732;
	
	sum += texture2D(u_texture, v_texCoords) * 0.382928;
	
	sum += texture2D(u_texture, v_texCoords + (vec2(1.0) * dir * radius)) * 0.241732;
	sum += texture2D(u_texture, v_texCoords + (vec2(2.0) * dir * radius)) * 0.060598;
	sum += texture2D(u_texture, v_texCoords + (vec2(3.0) * dir * radius)) * 0.005977;
	sum += texture2D(u_texture, v_texCoords + (vec2(4.0) * dir * radius)) * 0.000229;

	gl_FragColor = v_color * vec4(sum.rgb, 1.0);
}
