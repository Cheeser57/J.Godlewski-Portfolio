#pragma once

#include <glad/glad.h> 
#include <GLFW/glfw3.h>
#include <ft2build.h>
#include <stdio.h>
#include <stdlib.h>
#include "string.h"
#include FT_FREETYPE_H  
#include "glHelpers.h"


struct character {
    unsigned int textureID;  // ID handle of the glyph texture
    vec2   size;       // Size of glyph
    vec2   bearing;    // Offset from baseline to left/top of glyph
    unsigned int advance;    // Offset to advance to next glyph
};
typedef struct character character;

struct font
{
    character glyphs[128];
    unsigned int VAO;
    unsigned int VBO;
};
typedef struct font font;

font initFont();
font initTextQuad(font fon);
void renderText(font fon, char* text, unsigned int shader, int x, int y, float scale);