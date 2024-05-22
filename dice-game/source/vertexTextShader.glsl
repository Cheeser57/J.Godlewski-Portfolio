#version 410 core
layout (location = 0) in vec4 vertex;

out vec2 TexCoords;
uniform unsigned int sizeX;
uniform unsigned int sizeY;
void main()
{
   gl_Position = vec4(((2*vertex.x/sizeX)-1),((2*vertex.y/sizeY)-1)*(-1), 0.0, 1.0);
   TexCoords = vertex.zw;
}