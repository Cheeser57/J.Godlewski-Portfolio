#version 330

uniform sampler2D textureMap0;
uniform sampler2D textureMap1;
uniform bool isSpecular;
uniform float isReflective;

in vec2 iTexCoord;
in vec4 iNormal;
in vec4 ic;
in vec4 l1;
in vec4 l2;
in vec4 v;
out vec4 pixelColor;

void main(void) {
	vec4 mn = normalize(iNormal);
	vec4 mv = normalize(v);

	vec4 ml1 = normalize(l1);
	vec4 ml2 = normalize(l2);

	vec4 mr1 = reflect(-ml1, mn);
	vec4 mr2 = reflect(-ml2, mn);

	float nl1 = clamp(dot(mn, ml1), 0, 1);
	float nl2 = clamp(dot(mn, ml2), 0, 1);

	float rv1 = pow(clamp(dot(mr1, mv), 0, 1), 50);
	float rv2 = pow(clamp(dot(mr2, mv), 0, 1), 100);

	vec4 ambient = vec4(0.1,0.1,0.1,0);
	vec3 ks1 = vec3(1, 0.8, 0.3);
	vec3 ks2 = vec3(0.5, 0.5, 1);
	//if(isSpecular) ks1 = vec3(texture(textureMap1, iTexCoord));
	vec4 texCol = texture(textureMap0,iTexCoord);

	pixelColor= vec4(texCol.rgb * nl1, texCol.a) + vec4(ks1*rv1*isReflective, 0) + ambient
			   +vec4(texCol.rgb * ks2 * nl2, texCol.a) + vec4(ks2*rv2*isReflective, 0);
	//pixelColor=vec4(color, texCol.a);
}
