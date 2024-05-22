#include "fontFunctions.h"

font initFont()
{
    FT_Library ft;
    if (FT_Init_FreeType(&ft))
    {
        printf("ERROR::FREETYPE: Could not init FreeType Library\n");
        exit(0);
    }

    FT_Face face;
    if (FT_New_Face(ft, "resurces/TeachersStudent-Regular.ttf", 0, &face))
    {
        printf("ERROR::FREETYPE: Failed to load font\n");
        exit(0);
    }
    FT_Set_Pixel_Sizes(face, 0, 96);
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    font fon;
    //Create glyphs (chararcters)
    unsigned char c;
    for (c = 0; c < 128; c++)
    {
        if (FT_Load_Char(face, c, FT_LOAD_RENDER))
        {
            printf("ERROR::FREETYTPE: Failed to load Glyph\n");
            continue;
        }
        unsigned int texture;
        glGenTextures(1, &texture);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, face->glyph->bitmap.width, face->glyph->bitmap.rows, 0, GL_RED, GL_UNSIGNED_BYTE, face->glyph->bitmap.buffer);
        //Texture settings
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        fon.glyphs[c].textureID = texture;
        fon.glyphs[c].size.x = face->glyph->bitmap.width;
        fon.glyphs[c].size.y = face->glyph->bitmap.rows;
        fon.glyphs[c].bearing.x = face->glyph->bitmap_left;
        fon.glyphs[c].bearing.y = face->glyph->bitmap_top;
        fon.glyphs[c].advance = face->glyph->advance.x;
    }
    fon = initTextQuad(fon);
    FT_Done_Face(face);
    FT_Done_FreeType(ft);
    return fon;
}

font initTextQuad(font fon)
{
    unsigned int VAO, VBO;
    glGenVertexArrays(1, &VAO);
    glGenBuffers(1, &VBO);
    glBindVertexArray(VAO);
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, sizeof(float) * 6 * 4, NULL, GL_DYNAMIC_DRAW);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(float), 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
    fon.VBO = VBO;
    fon.VAO = VAO;
    return fon;
}

void renderText(font fon, char* text, unsigned int shader, int x, int y, float scale)
{
    glUseProgram(shader);
    //Uniforms
    int scrW = glGetUniformLocation(shader, "sizeX");
    int scrH = glGetUniformLocation(shader, "sizeY");
    glUniform1ui(scrW, SCR_WIDTH);
    glUniform1ui(scrH, SCR_HEIGHT);

    //glActiveTexture(GL_TEXTURE0);
    glBindVertexArray(fon.VAO);
    
    unsigned int i;
    for (i = 0; i < strlen(text); i++)
    {
        char c = text[i];
        vec2 pos = { x + fon.glyphs[c].bearing.x * scale,
                     y - (fon.glyphs[c].size.y - fon.glyphs[c].bearing.y) * scale };

        float w = fon.glyphs[c].size.x * scale;
        float h = fon.glyphs[c].size.y * scale;

        float vertices[6][4] = {
        { pos.x,     pos.y + h,   0.0f, 1.0f },
        { pos.x,     pos.y,       0.0f, 0.0f },
        { pos.x + w, pos.y,       1.0f, 0.0f },

        { pos.x,     pos.y + h,   0.0f, 1.0f },
        { pos.x + w, pos.y,       1.0f, 0.0f },
        { pos.x + w, pos.y + h,   1.0f, 1.0f }
        };

        glBindTexture(GL_TEXTURE_2D, fon.glyphs[c].textureID);
        glBindBuffer(GL_ARRAY_BUFFER, fon.VBO);
        glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(vertices), vertices);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        x += (fon.glyphs[c].advance >> 6) * scale;

    }
    glBindVertexArray(0);
    glBindTexture(GL_TEXTURE_2D, 0);
}