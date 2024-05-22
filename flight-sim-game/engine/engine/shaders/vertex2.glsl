#version 330

layout (location = 0) in vec3 vertex;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord;

//Zmienne jednorodne
uniform mat4 P;
uniform mat4 V;
uniform mat4 M;

uniform vec3 lightPos;
uniform vec3 lightPos2;

out vec2 iTexCoord;
out vec4 iNormal;
out vec4 ic;
out vec4 l1;
out vec4 l2;
out vec4 v;

void main(void) {
    vec4 vertex4 =  vec4(vertex,1.0);
    
    vec4 lp1 = vec4(lightPos,1.0);
    vec4 lp2 = vec4(lightPos2,1.0);
    l1 = normalize(V * lp1 - V*M*vertex4);
    l2 = normalize(V * lp2 - V*M*vertex4);

    v = normalize(vec4(0, 0, 0, 1) - V * M * vertex4);

    iNormal = normalize(V * M * vec4(normal,0.0));
    iTexCoord = texCoord;
    gl_Position=P*V*M*vertex4;
}
