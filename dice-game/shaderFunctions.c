#include "shaderFunctions.h"

unsigned int createShader(char* vertexFilename, char* fragmentFilename)
{
	//Loading shaders from files
	char* vertexSource = loadShader(vertexFilename);
	char* fragmentSource = loadShader(fragmentFilename);

	unsigned int vertex, fragment;
	int  success;
	char infoLog[512];

	//Vertex shader compilation
	vertex = glCreateShader(GL_VERTEX_SHADER);
	glShaderSource(vertex, 1, &vertexSource, NULL);
	glCompileShader(vertex);

	glGetShaderiv(vertex, GL_COMPILE_STATUS, &success);
	if (!success)
	{
		glGetShaderInfoLog(vertex, 512, NULL, infoLog);
		printf("ERROR::SHADER::VERTEX::COMPILATION_FAILED\n%s\n", infoLog);
	}

	//Fragment shader compilation
	fragment = glCreateShader(GL_FRAGMENT_SHADER);
	glShaderSource(fragment, 1, &fragmentSource, NULL);
	glCompileShader(fragment);

	glGetShaderiv(fragment, GL_COMPILE_STATUS, &success);
	if (!success)
	{
		glGetShaderInfoLog(fragment, 512, NULL, infoLog);
		printf("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED\n%s\n", infoLog);
	}

	//Linink shaders into shader program
	unsigned int shaderProgram;
	shaderProgram = glCreateProgram("vertexShaderSource.glsl");
	glAttachShader(shaderProgram, vertex);
	glAttachShader(shaderProgram, fragment);
	glLinkProgram(shaderProgram);

	glGetProgramiv(shaderProgram, GL_LINK_STATUS, &success);
	if (!success) {
		glGetProgramInfoLog(shaderProgram, 512, NULL, infoLog);
		printf("ERROR::PROGRAM::LINKING_FAILED\n%s\n", infoLog);
	}

	glDeleteShader(vertex);
	glDeleteShader(fragment);
	return shaderProgram;
}

char* loadShader(char* filename)
{
	char* buffer=0;
	long length;
	FILE* file;
	errno_t error = fopen_s(&file, filename, "rb");
	if(!error)
	{
		fseek(file, 0, SEEK_END);
		length = ftell(file);
		fseek(file, 0, SEEK_SET);
		buffer = malloc(length+1);
		if (buffer) fread(buffer, 1, length, file);
		fclose(file);
		buffer[length] = '\0';
	}
	else printf("ERROR::SHADER::FILE_NOT_SUCCESFULLY_READ");
	
	return buffer;
}

