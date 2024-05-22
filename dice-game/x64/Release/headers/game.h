#pragma once
#include <glad/glad.h> 
#include <GLFW/glfw3.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <math.h>

#include "glHelpers.h"
#include "fontFunctions.h"

extern int i, j;

enum tiles { EMPTY1, EMPTY2, GRASS1, GRASS2, FOREST1, FOREST2, HILL1, HILL2, CASTLE, FARM, SAWMILL, MINE, RUIN, WIZARD, KEEP, PYRAMID, PYRAMID2, WALL, STAR,WALL2};
enum  ui{LINE=7,TURNBUTTON=8,FOOD=10,WOOD=11,METAL=12,DUDE=13};

struct board
{
    vec2 size;
    vec2 startPos;
    vec2 tileSize;
    int tileTypeProcentage[4];
    int tileState[9][11];
    unsigned int VAO[79];
    int tileType[79];
    vec2 tileSlot[79];
    int filled[79];
};
typedef struct board board;

struct pipette
{
    unsigned int sprites[20];
    unsigned int tiles[20];
};
typedef struct pipette pipette;

struct sprite
{
    unsigned int quad;
    vec2 pos;
    int texture;
    bool renderFlag;
};
typedef struct sprite sprite;

struct diceInfo
{
    int n;
    int value[10];
    int order[10];
    vec2 startPos;

    int dragSelect;
    float scale[10];
};
typedef struct diceInfo diceInfo;

struct resources
{
     int wood;
     int metal;
     int food;
};
typedef struct resources resources;

struct game
{
    int numOfSprites;
    sprite sprites[20];
    diceInfo dice;
    board bd;
    pipette textures;
    resources res;
    font fonts;
    int gamestate;
    int turnCounter;
    int waveCounter;
    int waveFreq;
    resources costs[9];

    vec2 mouse;
};
typedef struct game game;

extern game gm;

board initGameBoard();
void newGameBoard();
int randTile(int procentage[]);
pipette loadTextures();
void initGame();
void initNewGame();
void loadGame();
void saveGame();
void initSprites();
void addSprite(int posX, int posY, int sizeX, int sizeY);

void loadSettings();

void gameStateUpdate();
void rollDice();
void nextTurn();
bool tileTrigger(int tileIndex, int tileType, int diceValue, int isFilled);
void shiftOrderFirst(int index);

vec2 pixelToHex(vec2 point);
int getTileIndex(vec2 offset);
bool isWithin(int posX, int posY, int startX, int startY, int endX, int endY);

void renderer(unsigned int staticShader, unsigned int dynamicShader, unsigned int textShader);
void drawText(unsigned int textShader);
void processInput(GLFWwindow* window);
void mouse_button_callback(GLFWwindow* window, int button, int action, int mods);

