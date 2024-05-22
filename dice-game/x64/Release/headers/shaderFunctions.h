#pragma once
#include <glad/glad.h> // include glad to get all the required OpenGL headers

#include <stdio.h>
#include <stdlib.h>

char* loadShader(char* filename);
unsigned int createShader(char* vertexFilename, char* fragmentFilename);
