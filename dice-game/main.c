#include <stdlib.h>
#include <stdio.h>
#include <glad/glad.h> 
#include <GLFW/glfw3.h>
#include <stdbool.h>
#include <math.h>


#include "shaderFunctions.h"
#include "fontFunctions.h"
#include "glHelpers.h"
#include "stb_image.h"
#include "game.h"


int main()
{
    initGLFW();
    GLFWwindow* window = createWindow();
    initGlad();
    glViewport(0, 0, SCR_WIDTH, SCR_HEIGHT);
    
    unsigned int staticShader  = createShader("source/vertexShaderSource.glsl", "source/fragmentShaderSource.glsl");
    unsigned int dynamicShader = createShader("source/vertexShaderDynamicSource.glsl", "source/fragmentShaderSource.glsl");
    unsigned int textShader = createShader("source/vertexTextShader.glsl", "source/fragmentTextSource.glsl");

    glfwSetFramebufferSizeCallback(window, framebuffer_size_callback);
    glfwSetMouseButtonCallback(window, mouse_button_callback);

    initGame();
    if (file_exists("files/gameSave.txt")) loadGame();
    else initNewGame();

    float deltaTime = 0.0f;
    float lastFrame = 0.0f;

    //main loop
    while (!glfwWindowShouldClose(window))
    {
        //Time
        float currentFrame = glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;
        //printf("%f\n", 60/deltaTime);

        //input
        processInput(window);
        //GameLogic
        gameStateUpdate(window,currentFrame);

        //renderers
        renderer(staticShader, dynamicShader, textShader);

        glfwSwapBuffers(window);
    }

    saveGame();

    glDeleteProgram(staticShader);
    glDeleteProgram(dynamicShader);
    glDeleteProgram(textShader);
    glfwTerminate();
}

