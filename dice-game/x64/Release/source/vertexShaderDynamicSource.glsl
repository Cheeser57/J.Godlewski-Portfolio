#version 410 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTex;
out vec2 TexCoords;
uniform unsigned int sizeX;
uniform unsigned int sizeY;
uniform int posX;
uniform int posY;
uniform vec2 scale;
void main()
{
   gl_Position = vec4(((2*((aPos.x*scale.x)+posX)/sizeX)-1),((2*((aPos.y*scale.y)+posY)/sizeY)-1)*(-1), 0.0, 1.0);
   TexCoords = aTex;
}