#include "game.h"

int i, j;
game gm;

board initGameBoard()
{
    board bd;

    bd.size.x = 11;
    bd.size.y = 9;
    bd.startPos.x = 200;
    bd.startPos.y = 200;
    bd.tileSize.x = 80;
    bd.tileSize.y = 70;

    bd.tileTypeProcentage[0] = 60;
    bd.tileTypeProcentage[1] = 20;
    bd.tileTypeProcentage[2] = 10;
    bd.tileTypeProcentage[3] = 10;

    int n = 0;
    for (i = 0; i < 5; i++)
    {
        for (j = 0; j < 11; j++)
        {
            if (j >= 2 - ((i + 1) / 2) && j <= 8 + (i / 2))
            {
                bd.tileState[i][j] = 1;
            }
            else bd.tileState[i][j] = -1;
        }
    }
    for (i = 5; i < 9; i++)
    {
        for (j = 0; j < 11; j++)
        {
            if (j >= ((i - 4) / 2) && j <= 9 - ((i - 5) / 2))
            {
                bd.tileState[i][j] = 1;
            }
            else bd.tileState[i][j] = -1;
        }
    }

    int x = bd.startPos.x - (bd.tileSize.x / 2);
    int y = bd.startPos.y, k = 0;
    for (i = 0; i < 9; i++)
    {
        for (j = 0; j < 11; j++)
        {
            if (bd.tileState[i][j] > 0)
            {
                bd.VAO[k] = initQuad(x, y, 105, 105);
                bd.tileState[i][j] = k;
                bd.tileSlot[k].x = x;
                bd.tileSlot[k].y = y;
                
                k++;
            }
            x += bd.tileSize.x;
        }
        if (i % 2 == 1) x = bd.startPos.x - (bd.tileSize.x / 2);
        else x = bd.startPos.x;
        y += bd.tileSize.y;
    }

    return bd;
}

void newGameBoard()
{
    for (i = 0; i < 79; i++)
    {
        gm.bd.tileType[i] = randTile(gm.bd.tileTypeProcentage);
        gm.bd.filled[i] = 0;
        gm.bd.filledE[i] = false;
    }
    gm.bd.tileType[39] = CASTLE;
}


int randTile(int procentage[])
{
    int val = rand() % 100;
    int total = 0;
    int t;
    for (t = 0; t < 8; t++)
    {
        total += procentage[t / 2] / 2;
        if (val < total) return t;
    }
    return 0;
}

pipette loadTextures()
{
    pipette textures;
    char filename[22] = "resurces/tiles/00.png";
    int numOfTiles = 20;
    for (i = 1; i <= numOfTiles; i++)
    {
        filename[15] = (i / 10) + 48;
        filename[16] = (i % 10) + 48;

        textures.tiles[i - 1] = generateTexture(filename, true);
    }
    textures.sprites[0] = generateTexture("resurces/sprites/01.png", true);
    char filename2[21] = "resurces/dices/0.png";
    for (i = 1;i <=6; i++)
    {
        filename2[15] = i + 48;
        textures.sprites[i] = generateTexture(filename2, true);
    }    
    char filename3[24] = "resurces/sprites/00.png";
    for (i = 1; i <= 13; i++)
    {
        filename3[17] = (i / 10) + 48;
        filename3[18] = (i % 10) + 48;
        textures.sprites[6+i] = generateTexture(filename3, true);
    }

    return textures;
}

void initGame()
{
    srand(time(NULL));
    
    gm.textures = loadTextures();
    gm.fonts = initFont();
    
    gm.dice.startPos.x = 100;
    gm.dice.startPos.y = 910;
    gm.dice.dragSelect = 0;
    for (i = 0; i < 10; i++)
    {
        gm.dice.value[i] = 0;
        gm.dice.scale[i] = 1.0f;
        gm.dice.order[i] = i;
    }

    gm.numOfSprites = 0;
    initSprites();

    gm.bd = initGameBoard();
    initEnemies();
}

void initNewGame()
{
    loadSettings();
    for (i = 3; i < 10; i++) gm.sprites[i].renderFlag = false;
    gm.gamestate = 1;
    gm.turnCounter = 0;
    gm.waveCounter = gm.waveFreq;
    gm.waveNumber = 0;
    newGameBoard();
    gm.dragons.total = 0;
    for (i = 0; i < 20; i++) gm.sprites[gm.dragons.spriteIndex[i]].renderFlag = false;
}

void loadGame()
{
    FILE* file;
    errno_t error = fopen_s(&file, "files/gameSave.txt", "r");
    if (error)
    {
        printf("Failed to load settings file\n");
        exit(0);
    }
    //game state data
    fscanf_s(file, "%d\n", &gm.gamestate);
    fscanf_s(file, "%d\n", &gm.turnCounter);
    fscanf_s(file, "%d\n", &gm.waveCounter);
    fscanf_s(file, "%d\n", &gm.waveFreq);
    fscanf_s(file, "%d\n", &gm.waveNumber);
    fscanf_s(file, "%d\n", &gm.castleHP);
    //resources
    fscanf_s(file, "%d\n", &gm.res.food);
    fscanf_s(file, "%d\n", &gm.res.wood);
    fscanf_s(file, "%d\n", &gm.res.metal);
    //dice
    fscanf_s(file, "%d\n", &gm.dice.n);
    for (i = 0; i < 10; i++) fscanf_s(file, "%d", &gm.dice.value[i]);
    for (i = 0; i < 10; i++) fscanf_s(file, "%d", &gm.sprites[i].texture);
    for (i = 0; i < 10; i++) fscanf_s(file, "%d", &gm.sprites[i].pos.x);
    for (i = 0; i < 10; i++) fscanf_s(file, "%d", &gm.sprites[i].pos.y);
    for (i = 0; i < 10; i++) fscanf_s(file, "%f", &gm.dice.scale[i]);
    for (i = 0; i < gm.dice.n; i++) gm.sprites[i].renderFlag = true;
    //Game board data
    for (i = 0; i < 79; i++) fscanf_s(file, "%d", &gm.bd.tileType[i]);
    for (i = 0; i < 79; i++) fscanf_s(file, "%d", &gm.bd.filled[i]);
    for (i = 0; i < 79; i++) fscanf_s(file, "%d", &gm.bd.filledE[i]);
    //Enemies
    fscanf_s(file, "%d\n", &gm.dragons.total);
    for (i = 0; i < 20; i++) fscanf_s(file, "%d", &gm.dragons.spriteIndex[i]);
    for (i = 0; i < 20; i++) fscanf_s(file, "%d", &gm.sprites[gm.dragons.spriteIndex[i]].pos.x);
    for (i = 0; i < 20; i++) fscanf_s(file, "%d", &gm.sprites[gm.dragons.spriteIndex[i]].pos.y);
    for (i = 0; i < 20; i++) fscanf_s(file, "%d", &gm.sprites[gm.dragons.spriteIndex[i]].renderFlag);
    for (i = 0; i < 20; i++) if (gm.sprites[gm.dragons.spriteIndex[i]].pos.x > SCR_WIDTH / 2) gm.sprites[gm.dragons.spriteIndex[i]].texture = BAZYL4;

    fclose(file);

    if (gm.gamestate == 0)
    {
        newGameBoard();
        initNewGame();
    }
}

void saveGame()
{
    if (file_exists("files/gameSave.txt")) remove("files/gameSave.txt");

    FILE* file;
    errno_t error = fopen_s(&file, "files/gameSave.txt", "w+");
    if (error)
    {
        printf("Failed to load save file\n");
        exit(0);
    }

    //game state data
    fprintf_s(file, "%d\n", gm.gamestate);
    fprintf_s(file, "%d\n", gm.turnCounter);
    fprintf_s(file, "%d\n", gm.waveCounter);
    fprintf_s(file, "%d\n", gm.waveFreq);
    fprintf_s(file, "%d\n", gm.waveNumber);
    fprintf_s(file, "%d\n", gm.castleHP);
    //resources
    fprintf_s(file, "%d\n", gm.res.food);
    fprintf_s(file, "%d\n", gm.res.wood);
    fprintf_s(file, "%d\n", gm.res.metal);
    //dice
    fprintf_s(file, "%d\n", gm.dice.n);
    for (i = 0; i < 10; i++)  fprintf_s(file, "%d ", gm.dice.value[i]);
    for (i = 0; i < 10; i++)  fprintf_s(file, "%d ", gm.sprites[i].texture);
    for (i = 0; i < 10; i++)  fprintf_s(file, "%d ", gm.sprites[i].pos.x);
    for (i = 0; i < 10; i++)  fprintf_s(file, "%d ", gm.sprites[i].pos.y);
    for (i = 0; i < 10; i++)  fprintf_s(file, "%f ", gm.dice.scale[i]);
    fprintf_s(file, "\n");
    //Game board data
    for (i = 0; i < 79; i++)  fprintf_s(file, "%d ", gm.bd.tileType[i]);
    for (i = 0; i < 79; i++)  fprintf_s(file, "%d ", gm.bd.filled[i]);
    for (i = 0; i < 79; i++)  fprintf_s(file, "%d ", gm.bd.filledE[i]);
    fprintf_s(file, "\n");
    //Enemies
    fprintf_s(file, "%d\n", gm.dragons.total);
    for (i = 0; i < 20; i++)  fprintf_s(file, "%d ", gm.dragons.spriteIndex[i]);
    for (i = 0; i < 20; i++)  fprintf_s(file, "%d ", gm.sprites[gm.dragons.spriteIndex[i]].pos.x);
    for (i = 0; i < 20; i++)  fprintf_s(file, "%d ", gm.sprites[gm.dragons.spriteIndex[i]].pos.y);
    for (i = 0; i < 20; i++)  fprintf_s(file, "%d ", gm.sprites[gm.dragons.spriteIndex[i]].renderFlag);
    fclose(file);
}


void loadSettings()
{
    FILE* file;
    errno_t error = fopen_s(&file,"files/settings.txt", "r");
    if(error)
    {
        printf("Failed to load settings file\n");
        exit(0);
    }
    char text[20];
    int n1, n2;
    fscanf_s(file, "%s", text,20);
    for (i = 0; i < 9; i++)
    {
        fscanf_s(file, "%d %d,", &gm.costs[i].wood, &gm.costs[i].metal);
    }
    fscanf_s(file, "%s", text, 20);
    fscanf_s(file, "%d\n",&gm.dice.n);
    fscanf_s(file, "%s", text, 20);
    fscanf_s(file, "%d\n", &gm.res.food);
    fscanf_s(file, "%s", text, 20);
    fscanf_s(file, "%d\n",&gm.res.metal);
    fscanf_s(file, "%s", text, 20);
    fscanf_s(file, "%d\n",&gm.res.wood);
    fscanf_s(file, "%s", text, 20);
    fscanf_s(file, "%d\n", &gm.waveFreq);
    fscanf_s(file, "%s", text, 20);
    fscanf_s(file, "%d\n", &gm.castleHP);
    fclose(file);
}



void addSprite(int texture, int posX, int posY, int sizeX, int sizeY, bool render)
{
    gm.sprites[gm.numOfSprites].quad= initQuad(0,0,sizeX,sizeY);
    gm.sprites[gm.numOfSprites].texture=texture;
    vec2 pos={posX,posY};
    gm.sprites[gm.numOfSprites].pos = pos;
    gm.sprites[gm.numOfSprites].renderFlag = render;
    gm.numOfSprites++;
}

void initSprites()
{
    for(i=0;i<10;i++) addSprite(0, gm.dice.startPos.x+(i*100), gm.dice.startPos.y, 90, 90, false);
    addSprite(LINE, 50, 140,1100 ,30,true);
    addSprite(TURNBUTTON, 950, 25, 200, 105,true);
    addSprite(FOOD, 50, 30, 90, 90, true);
    addSprite(WOOD, 240, 40, 80, 80, true);
    addSprite(METAL, 450, 30, 100, 100, true);
}

void initEnemies()
{
    gm.dragons.lastFrame = 0;
    gm.dragons.total = 0;
    for (i = 0; i < 20; i++)
    {
        gm.dragons.hp[i] = 5;
        addSprite(BAZYL1, 20, 15, 75, 75, false);
        gm.dragons.spriteIndex[i] = gm.numOfSprites - 1;
    }
}

void triggerEnemies()
{
    for (i = 0; i < gm.dragons.total; i++)
    {
        int index = gm.dragons.spriteIndex[i];
        bool attackFlag = false;
        
        
        vec2 pos = pixelToHex(gm.sprites[index].pos,-30,-40);
        int tile;
        //Attacking
        for (j = 0; j < 6; j++)
        {
            tile = shiftNeighbor(pos, j);
           
            if (tile < 0) continue;

            if (gm.bd.tileType[tile] >=8)
            {
                switch (gm.bd.tileType[tile])
                {
                case CASTLE:
                    gm.castleHP -= 1;
                    if (gm.castleHP == 0)
                    {
                        gm.gamestate = 0;
                        //gm.bd.tileType[tile] = 0;
                    }
                    break;
                case FARM:
                    gm.bd.tileType[tile] = GRASS1 + rand() % 2;
                    break;
                case SAWMILL:
                    gm.bd.tileType[tile] = FOREST1 + rand() % 2;
                    break;
                case MINE:
                    gm.bd.tileType[tile] = HILL1 + rand() % 2;
                    break;
                case WALL:
                    gm.bd.filled[tile] -= 1;
                    if(gm.bd.filled[tile]<=0) gm.bd.tileType[tile] = EMPTY1 + rand() % 2;
                    break;
                case WALL2: 
                    gm.bd.filled[tile] -= 1;
                    if (gm.bd.filled[tile] <= 0) gm.bd.tileType[tile] = EMPTY1 + rand() % 2;
                    break;
                default:
                    gm.bd.tileType[tile] = EMPTY1 + rand() % 2;
                    break;
                }
                attackFlag = true;
                break;
            }
        }

        //move
        if (!attackFlag)
        {
            vec2 vect = { (SCR_WIDTH / 2 + 10) - gm.sprites[index].pos.x + 30,(SCR_HEIGHT / 2 + 10) - gm.sprites[index].pos.y + 40 };
            float dir = atan((float)vect.y / (float)vect.x) * (180.0f / 3.1415f) + 330;
            if (vect.x >= 0) dir += 180;
            if (dir > 360) dir -= 360;
            int target = (int)(dir / 60);

            for(int i=0;i<3;i++)
            {
                int target1 = (target + i)%6;
                int target2 = (target - i);
                if (target2 < 0) target2 += 6;
                tile = shiftNeighbor(pos, target1);
                if (!gm.bd.filledE[tile])
                {
                    gm.sprites[index].pos.x = gm.bd.tileSlot[tile].x + 20;
                    gm.sprites[index].pos.y = gm.bd.tileSlot[tile].y + 12;
                    if (gm.sprites[index].pos.x > SCR_WIDTH / 2)
                    {
                        gm.sprites[index].texture = BAZYL4;
                        gm.sprites[index].pos.x -= 10;
                    }
                    gm.bd.filledE[tile] = true;
                    gm.bd.filledE[getTileIndex(pos)] = false;

                    break;
                }
                tile = shiftNeighbor(pos, target2);
                if (!gm.bd.filledE[tile])
                {
                    gm.sprites[index].pos.x = gm.bd.tileSlot[tile].x + 20;
                    gm.sprites[index].pos.y = gm.bd.tileSlot[tile].y + 12;
                    if (gm.sprites[index].pos.x > SCR_WIDTH / 2)
                    {
                        gm.sprites[index].texture = BAZYL4;
                        gm.sprites[index].pos.x -= 10;
                    }
                    gm.bd.filledE[tile] = true;
                    gm.bd.filledE[getTileIndex(pos)] = false;

                    break;
                }
            }           
        }

    }
}

int shiftNeighbor(vec2 pos, int j)
{
    int eX[6] = { -1,0,1,0,-1,-1 };
    int oX[6] = { 0,1,1,1,0,-1 };
    int y[6] = { -1,-1,0,1,1,0 };
    vec2 pos2;
    if (pos.y % 2) pos2.x = pos.x + oX[j];
    else pos2.x = pos.x + eX[j];
    pos2.y = pos.y + y[j];
    return getTileIndex(pos2);

}

void SpawnEnemies()
{
    int prev = gm.dragons.total;
    gm.dragons.total += gm.waveNumber + rand() % gm.waveNumber;
    if (gm.dragons.total > 20) gm.dragons.total = 20;

    for (i = prev; i < gm.dragons.total; i++)
    {
        int index = gm.dragons.spriteIndex[i];
        gm.dragons.hp[i] = 5;
        int l[28] = { 0,1,2,3,4,5,6,7,14,15,23,24,33,34,44,45,54,55,63,64,71,72,73,74,75,76,77,78};
        int choice, j;
        bool flag = true;
        for(j=0;j<40;j++)
        {
            choice = l[rand() % 28];
            if ((gm.bd.tileType[choice] < 8) && !(gm.bd.filledE[choice]))
            {
                flag = false;
                break;
            }
        }
        if(flag) choice = l[rand() % 28];
        
        gm.sprites[index].texture = BAZYL1;
        gm.sprites[index].pos.x = gm.bd.tileSlot[choice].x+20;
        gm.sprites[index].pos.y = gm.bd.tileSlot[choice].y+12;
        gm.sprites[index].renderFlag = true;
        gm.bd.filledE[choice] = true;
        if (gm.sprites[index].pos.x > SCR_WIDTH / 2)
        {
            gm.sprites[index].texture = BAZYL4;
            gm.sprites[index].pos.x -= 10;
        }

    }
}

void gameStateUpdate(GLFWwindow* window, float time)
{
    if (time - gm.dragons.lastFrame > 0.2)
    {
        gm.dragons.lastFrame = time;
        for (int i = 0; i < gm.dragons.total; i++)
        {
            gm.sprites[gm.dragons.spriteIndex[i]].texture += 1;
            if (gm.sprites[gm.dragons.spriteIndex[i]].texture == BAZYL3 + 1) gm.sprites[gm.dragons.spriteIndex[i]].texture = BAZYL1;
            if (gm.sprites[gm.dragons.spriteIndex[i]].texture == BAZYL6 + 1) gm.sprites[gm.dragons.spriteIndex[i]].texture = BAZYL4;
        }

    }

    //Pause
    if (gm.gamestate == 0)
    {

    }
    //NewTurn + Roll dice
    if (gm.gamestate == 1)
    {
        for (i = 0; i < gm.dice.n; i++)
        {
            vec2 pos = { gm.dice.startPos.x + i * 100,gm.dice.startPos.y };
            gm.sprites[i].pos = pos;
            gm.dice.scale[i] = 1.0f;
        }

        gm.sprites[11].texture = 8;
        gm.turnCounter += 1;
        rollDice();
        gm.gamestate = 2;
    }
    //Dice Animation
    if (gm.gamestate == 2)
    {
        gm.gamestate = 3;
    }
    //Turn logic
    if (gm.gamestate == 3)
    {
        
    }
    //Dice lock
    if (gm.gamestate == 4)
    {

        vec2 pos = getMousePos(window);
        if (pos.y > 860 && pos.y < 940) gm.dice.scale[gm.dice.dragSelect] = (pos.y - 860) / 160.0f + 0.5f;
        if (pos.y < 860) gm.dice.scale[gm.dice.dragSelect] = 0.5;
        int index = getTileIndex(pixelToHex(pos, 0, 0));
        gm.sprites[gm.dice.dragSelect].pos.x = pos.x-40;
        gm.sprites[gm.dice.dragSelect].pos.y = pos.y-40;
    }
    //End Turn
    if (gm.gamestate == 5)
    {
        gm.gamestate = 1;
    }


}


void rollDice()
{
    int val = 0;
    for (i = 0; i < gm.dice.n; i++)
    {
        val = (rand() % 6) + 1;
        gm.dice.value[i] = val;
        gm.sprites[i].texture = val;
        gm.sprites[i].renderFlag = true;
    }
}

void nextTurn()
{
    gm.gamestate = 5;
    gm.res.food -= gm.dice.n;
    gm.waveCounter -= 1;
    triggerEnemies();
    if (gm.waveCounter==0)
    {
        if(gm.waveFreq>5) gm.waveFreq -= 5;
        gm.waveCounter = gm.waveFreq;
        gm.waveNumber += 1;
        SpawnEnemies();
    }

    if (gm.res.food < 0)
    {
        gm.dice.n += gm.res.food;
        for (i = gm.dice.n; i < gm.dice.n-gm.res.food;i++) gm.sprites[i].renderFlag = false;
        gm.res.food = 0;
    }
    if (gm.dice.n <= 0) gm.gamestate = 0;


    for (i = 0; i < 79; i++)
    {
        if (gm.bd.tileType[i] != WALL && gm.bd.tileType[i] != WALL2) gm.bd.filled[i] = 0;
        if (gm.bd.tileType[i] == PYRAMID2) gm.bd.tileType[i] = PYRAMID;
    }
    gm.sprites[11].texture = 9;
}

vec2 pixelToHex(vec2 point,int offsetX, int offsetY)
{
    point.x -= gm.bd.startPos.x+12+offsetX;
    point.y -= gm.bd.startPos.y+52+offsetY;

    //printf("%d,%d |", point.x,point.y);

    float fq = ((sqrt(3.0f)/3.0f*(float)point.x) - (1.0f/3.0f * (float)point.y)) / 46.18f;
    float fr = (2.0f / 3.0f * (float)point.y) / 46.18f;
    float fs = (-1.0f)*(fq+fr);

    int q = (int)round(fq);
    int r = (int)round(fr);
    int s = (int)round(fs);

    int q_diff = abs(q - fq);
    int r_diff = abs(r - fr);
    int s_diff = abs(s - fs);

    if (q_diff > r_diff && q_diff > s_diff) q = -r - s;
    else if (r_diff > s_diff) r = -q - s;
    else s = -q - r;

    vec2 offset = { q + (r - (r & 1)) / 2 ,r };
    //printf("%d,%d |", offset.x, offset.y);
    //printf("%d,%d,%d\n", q, r, s);
    return offset;
}

int getTileIndex(vec2 offset)
{
    if (offset.x<0 || offset.y<0 || offset.x>gm.bd.size.x - 1 || offset.y>gm.bd.size.y - 1) return -1;
    return gm.bd.tileState[offset.y][offset.x];
}

void shiftOrderFirst(int index)
{
    int posI;
    for (i = 0; i < 10; i++) if (gm.dice.order[i]==index)
    {
       posI = i;
       break;
    } 
    for (i = posI; i > 0; i--)
    {
        gm.dice.order[i] = gm.dice.order[i - 1];
    }
    gm.dice.order[0] = index;
}

void renderer(unsigned int staticShader, unsigned int dynamicShader, unsigned int textShader)
{
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    int posX = 0, posY = 0;

    //Static 
    glUseProgram(staticShader);
    int locationW = glGetUniformLocation(staticShader, "sizeX");
    int locationH = glGetUniformLocation(staticShader, "sizeY");
    glUniform1ui(locationW, SCR_WIDTH);
    glUniform1ui(locationH, SCR_HEIGHT);
    
    //Tiles
    for (i = 0; i < 79; i++) drawSprite(gm.bd.VAO[i], gm.textures.tiles[gm.bd.tileType[i]]);

    //UI
    drawText(textShader);

    //Sprites
    glUseProgram(dynamicShader);
    int locationW2 = glGetUniformLocation(dynamicShader, "sizeX");
    int locationH2 = glGetUniformLocation(dynamicShader, "sizeY");
    int locationX  = glGetUniformLocation(dynamicShader, "posX");
    int locationY  = glGetUniformLocation(dynamicShader, "posY");
    int locationS  = glGetUniformLocation(dynamicShader, "scale");
    glUniform1ui(locationW2, SCR_WIDTH);
    glUniform1ui(locationH2, SCR_HEIGHT);


    glUniform2f(locationS, 1, 1);
    //Ui & Other
    for (i = 10; i < gm.numOfSprites; i++)
    {
        if (gm.sprites[i].renderFlag)
        {
            posX = gm.sprites[i].pos.x;
            posY = gm.sprites[i].pos.y;
            glUniform1i(locationX, posX);
            glUniform1i(locationY, posY);
            drawSprite(gm.sprites[i].quad, gm.textures.sprites[gm.sprites[i].texture]);
        }
    }
    //Dice
    for (i = 9; i >= 0; i--)
    {
        int die = gm.dice.order[i];
        if (gm.sprites[die].renderFlag)
        {
            glUniform2f(locationS, gm.dice.scale[die], gm.dice.scale[die]);
            posX = gm.sprites[die].pos.x + (-40 * gm.dice.scale[die] + 40);
            posY = gm.sprites[die].pos.y + (-40 * gm.dice.scale[die] + 40);
            glUniform1i(locationX, posX);
            glUniform1i(locationY, posY);
            drawSprite(gm.sprites[die].quad, gm.textures.sprites[gm.sprites[die].texture]);
        }
    }

    //events & buffers
    glfwPollEvents();
}

void drawText(unsigned int textShader)
{
    char text[8];
    sprintf_s(text, 8, "%d", gm.res.food);
    renderText(gm.fonts, text, textShader, 130, 45, 1);
    sprintf_s(text, 8, "%d", gm.res.wood);
    renderText(gm.fonts, text, textShader, 330, 45, 1);
    sprintf_s(text, 8, "%d", gm.res.metal);
    renderText(gm.fonts, text, textShader, 550, 45, 1);
    strcpy_s(text, 8, "TURN");
    renderText(gm.fonts, text, textShader, 650, 31, 0.6);
    sprintf_s(text, 8, "%d", gm.turnCounter);
    renderText(gm.fonts, text, textShader, 698 - strlen(text) * 10, 71, 0.6);
    strcpy_s(text, 8, "WAVE");
    renderText(gm.fonts, text, textShader, 800, 30, 0.6);
    sprintf_s(text, 8, "%d", gm.waveCounter);
    renderText(gm.fonts, text, textShader, 848 - strlen(text) * 10, 70, 0.6);

}


void processInput(GLFWwindow* window)
{
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true);
    if (glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS)
    {
        newGameBoard();
        initNewGame();
    }
    if (glfwGetKey(window, GLFW_KEY_M) == GLFW_PRESS)
    {
        vec2 pos = getMousePos(window);
        printf("x:%d y:%d", pos.x, pos.y);
        vec2 vect = { (SCR_WIDTH /2+10) - pos.x,(SCR_HEIGHT/2+10) - pos.y };
        //float mag = sqrt(vect.x * vect.y + vect.x * vect.y);
        float dir = atan((float)vect.y /(float) vect.x)*(180.0f/3.1415f)+330;
        if (vect.x >= 0) dir += 180;
        if(dir > 360) dir -= 360;
        int target = (int)(dir/60);
        printf(" A:%f %d\n", dir, target);
    }

        
}

bool isWithin(int posX,int posY, int startX, int startY, int endX, int endY)
{
    return (posX >= startX && posY >= startY && posX <= endX && posY <= endY);
}

//void shiftPosition;

void mouse_button_callback(GLFWwindow* window, int button, int action, int mods)
{

    vec2 pos = getMousePos(window);
    int index = getTileIndex(pixelToHex(pos, 0, 0));
    if (gm.gamestate == 3 && button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS)
    {
        if (isWithin(pos.x, pos.y, 955, 30, 1145, 125)) nextTurn();
        for (i = 0; i < gm.dice.n; i++)
        {
            if (gm.sprites[i].renderFlag && isWithin(pos.x, pos.y, gm.sprites[i].pos.x + 5 + (-40 * gm.dice.scale[i] + 40),
                                                                   gm.sprites[i].pos.y + 5 + (-40 * gm.dice.scale[i] + 40),
                                                                   gm.sprites[i].pos.x + (-40 * gm.dice.scale[i] + 40) + (85 * gm.dice.scale[i]),
                                                                   gm.sprites[i].pos.y + (-40 * gm.dice.scale[i] + 40) + (85 * gm.dice.scale[i])))
            {
                if (gm.sprites[i].texture == DUDE && (gm.bd.tileType[index] == EMPTY1 || gm.bd.tileType[index] == EMPTY2))
                {
                    gm.sprites[i].texture = gm.dice.value[i];
                    gm.bd.filled[index] = 0;
                }
                else if (gm.sprites[i].texture == DUDE) continue;
                gm.gamestate = 4;
                gm.dice.dragSelect = i;
                

                //Add diff and taking dices away

                glfwSetCursorPos(window, gm.sprites[i].pos.x + 40, gm.sprites[i].pos.y + 40);
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
                shiftOrderFirst(i);
                break;
            }
            
        }
        
    }
    if (gm.gamestate == 4 && button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE)
    {
        if (index != -1)
        {
            if (tileTrigger(index, gm.bd.tileType[index], gm.dice.value[gm.dice.dragSelect], gm.bd.filled[index]))
            {
                gm.sprites[gm.dice.dragSelect].pos.x = gm.bd.tileSlot[index].x+16;// +gm.bd.offsetX[gm.bd.tileType[index]];
                gm.sprites[gm.dice.dragSelect].pos.y = gm.bd.tileSlot[index].y-13;// +gm.bd.offsetY[gm.bd.tileType[index]];
                gm.dice.scale[gm.dice.dragSelect] = 0.4f;
                if(gm.bd.tileType[index] != PYRAMID2) gm.sprites[gm.dice.dragSelect].texture = DUDE;
                
            }
        }
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        gm.gamestate = 3;
        gm.dice.dragSelect = 0;
    }
}

bool tileTrigger(int tileIndex, int tileType, int diceValue, int filled)
{
    if (gm.bd.filledE[tileIndex] == true) return false;

    if (filled)
    {
        switch (tileType)
        {
        case EMPTY1:
        case EMPTY2:
            if (diceValue == 6)
            {
                switch (filled)
                {
                case 1:
                    if (gm.res.wood >= gm.costs[filled-1].wood)
                    {
                        gm.res.wood -= gm.costs[filled - 1].wood;
                        gm.bd.tileType[tileIndex] = WALL;
                        gm.bd.filled[tileIndex] = 4;
                        gm.sprites[gm.dice.dragSelect].renderFlag = false;
                        return true;
                    }
                    else return false;
                case 2:
                    if (gm.res.metal >= gm.costs[filled - 1].metal)
                    {
                        gm.res.metal -= gm.costs[filled - 1].metal;
                        gm.bd.tileType[tileIndex] = WALL2;
                        gm.bd.filled[tileIndex] = 8;
                        gm.sprites[gm.dice.dragSelect].renderFlag = false;
                        return true;
                    }
                    else return false;
                case 3:
                    if (gm.res.wood >= gm.costs[filled - 1].wood && gm.res.metal >= gm.costs[filled - 1].metal)
                    {
                        gm.res.wood -= gm.costs[filled - 1].wood;
                        gm.res.metal -= gm.costs[filled - 1].metal;
                        gm.bd.tileType[tileIndex] = KEEP;
                        gm.sprites[gm.dice.dragSelect].renderFlag = false;
                        return true;
                    }
                    else return false;
                case 4:
                    if (gm.res.wood >= gm.costs[filled - 1].wood && gm.res.metal >= gm.costs[filled - 1].metal)
                    {
                        gm.res.wood -= gm.costs[filled - 1].wood;
                        gm.res.metal -= gm.costs[filled - 1].metal;
                        gm.bd.tileType[tileIndex] = STAR;
                        gm.sprites[gm.dice.dragSelect].renderFlag = false;
                        return true;
                    }
                    else return false;
                case 5:
                    if (gm.res.wood >= gm.costs[filled - 1].wood && gm.res.metal >= gm.costs[filled - 1].metal)
                    {
                        gm.res.wood -= gm.costs[filled - 1].wood;
                        gm.res.metal -= gm.costs[filled - 1].metal;
                        gm.bd.tileType[tileIndex] = PYRAMID;
                        gm.sprites[gm.dice.dragSelect].renderFlag = false;
                        return true;
                    }
                    else return false;
                case 6:
                    if (gm.res.wood >= gm.costs[filled - 1].wood && gm.res.metal >= gm.costs[filled - 1].metal)
                    {
                        gm.res.wood -= gm.costs[filled - 1].wood;
                        gm.res.metal -= gm.costs[filled - 1].metal;
                        gm.bd.tileType[tileIndex] = WIZARD;
                        gm.sprites[gm.dice.dragSelect].renderFlag = false;
                        return true;
                    }
                    else return false;
                }
            }
            else return false;
        case CASTLE:
            if (diceValue == filled && gm.res.food>=5)
            {
                gm.res.food -= 5;
                gm.dice.n += 1;
                gm.sprites[gm.dice.dragSelect].renderFlag = false;

                return true;
            }
            else return false;
        case GRASS1:
        case GRASS2:
            if (diceValue == 6 && gm.res.wood >= gm.costs[6].wood && gm.res.metal >= gm.costs[6].metal)
            {
                gm.res.wood -= gm.costs[6].wood;
                gm.res.metal -= gm.costs[6].metal;
                gm.bd.tileType[tileIndex] = FARM;
                gm.sprites[gm.dice.dragSelect].renderFlag = false;
                return true;
            }
            else return false;
        case FOREST1:
        case FOREST2:
            if (diceValue == 6 && gm.res.wood >= gm.costs[6].wood && gm.res.metal >= gm.costs[6].metal)
            {
                gm.res.wood -= gm.costs[6].wood;
                gm.res.metal -= gm.costs[6].metal;
                gm.bd.tileType[tileIndex] = SAWMILL;
                gm.sprites[gm.dice.dragSelect].renderFlag = false;
                return true;
            }
            else return false;
        case HILL1:
        case HILL2:
            if (diceValue == 6 && gm.res.wood >= gm.costs[6].wood && gm.res.metal >= gm.costs[6].metal)
            {
                gm.res.wood -= gm.costs[6].wood;
                gm.res.metal -= gm.costs[6].metal;
                gm.bd.tileType[tileIndex] = MINE;
                gm.sprites[gm.dice.dragSelect].renderFlag = false;
                return true;
            }
            else return false;
        }

    }
    else switch(tileType)
    {
    case EMPTY1:
    case EMPTY2:
        gm.bd.filled[tileIndex] = diceValue;
        return true;
    case GRASS1:
    case GRASS2:
        gm.res.food += diceValue / 2;
        gm.bd.filled[tileIndex] = diceValue;
        return true;
    case FOREST1:
    case FOREST2:
        gm.res.wood += diceValue / 3;
        gm.res.food += diceValue % 3;
        gm.bd.filled[tileIndex] = diceValue;
        return true;
    case HILL1:
    case HILL2:
        gm.res.metal += diceValue / 6;
        gm.bd.filled[tileIndex] = diceValue;
        return true;
    case CASTLE:
        gm.bd.filled[tileIndex] = diceValue;
        return true;
    case FARM:
        gm.res.food += diceValue;
        gm.bd.filled[tileIndex] = diceValue;
        return true;
    case SAWMILL:
        gm.res.wood += diceValue / 2;
        gm.bd.filled[tileIndex] = diceValue;
        return true;
    case MINE:
        gm.res.metal += diceValue/3;
        gm.bd.filled[tileIndex] = diceValue;
        return true;
    case PYRAMID:
        gm.dice.value[gm.dice.dragSelect] = 7 - diceValue;
        gm.sprites[gm.dice.dragSelect].texture = 7 - diceValue;
        gm.bd.tileType[tileIndex] = PYRAMID2;
        return true;

    }
    return false;
}