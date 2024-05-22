#pragma once
#include <glad/glad.h> 
#include <GLFW/glfw3.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <sys/stat.h> 

#include "stb_image.h"

extern unsigned int SCR_WIDTH;
extern unsigned int SCR_HEIGHT;

struct vec2
{
    int x, y;
};
typedef struct vec2 vec2;


void initGLFW();
GLFWwindow* createWindow();
void initGlad();
unsigned int initQuad(int posX, int posY, int sizeX, int sizeY);
void drawSprite(unsigned int VAO, unsigned int texture);
unsigned int generateTexture(char* filename, bool isTransparent);

void framebuffer_size_callback(GLFWwindow* window, int width, int height);
vec2 getMousePos(GLFWwindow* window);
bool file_exists(char* filename);