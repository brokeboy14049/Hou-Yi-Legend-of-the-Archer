package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.Random;

public class MySketch extends PApplet {
    // Game state
    String screen = "menu";
    Player player;
    PImage playerImg, enemyImg1, enemyImg2, enemyImg3, enemyImg4, enemyImg5, bossImg, arrowImg, fireballImg;
    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Bullet> enemyBullets = new ArrayList<>();
    Enemy currentEnemy;
    Boss boss;
    
    // Game progress
    int enemiesDefeated = 0;
    boolean bossReady = false;
    int currentLevel = 1;
    boolean levelWon = false;
    
    // Battle variables
    boolean playerTurn = true;
    float heartX, heartY;
    boolean inFightMenu = false;
    boolean inItemMenu = false;
    boolean inActMenu = false;
    String[] battleOptions = {"Fight", "Act", "Item", "Mercy"};
    String[] actOptions = {"Check", "Talk", "Insult"};
    int selectedOption = 0;
    int attackTimer = 0;
    int currentAttackPattern = 0;
    
    // Map boundaries
    float worldWidth = 1600;
    float worldHeight = 1200;
    float viewX, viewY;
    
    // Health values
    int playerMaxHealth = 100;
    int enemyMaxHealth = 100;
    int bossBaseHealth = 200;
    
    // Controls
    boolean[] keys = new boolean[256];

    public static void main(String[] args) {
        PApplet.main("summative.assignment.MySketch");
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        textAlign(CENTER, CENTER);
        rectMode(CENTER);
        imageMode(CENTER);
        
        // Load images
        playerImg = loadImage("images/player.png");
        enemyImg1 = loadImage("images/enemy1.png");
        enemyImg2 = loadImage("images/enemy2.png");
        enemyImg3 = loadImage("images/enemy3.png");
        enemyImg4 = loadImage("images/enemy4.png");
        enemyImg5 = loadImage("images/enemy5.png");
        bossImg = loadImage("images/sun.png");
        arrowImg = loadImage("images/arrow.png");
        fireballImg = loadImage("images/fireball.png");
        
        player = new Player(width/2, height/2, playerImg);
        player.health = playerMaxHealth;
        player.maxHealth = playerMaxHealth;
        resetBattle();
        createEnemies();
        
        // Initialize view
        viewX = player.x - width/2;
        viewY = player.y - height/2;
        
        // Load saved game
        int[] savedData = GameData.loadGame();
        if (savedData != null) {
            currentLevel = savedData[0];
            player.health = savedData[1];
            enemiesDefeated = savedData[2];
            bossReady = enemiesDefeated >= 5;
        }
    }

    void resetBattle() {
        heartX = width/2;
        heartY = height/2 + 150;
        attackTimer = 0;
        playerTurn = true;
        inFightMenu = false;
        inItemMenu = false;
        inActMenu = false;
        enemyBullets.clear();
    }

    public void draw() {
        background(30);
        
        switch(screen) {
            case "menu": drawMenu(); break;
            case "level": drawWorld(); break;
            case "battle": drawBattle(); break;
            case "boss": drawBossFight(); break;
            case "victory": drawWinScreen(); break;
        }
        
        // Save button (except in menu)
        if (!screen.equals("menu")) {
            drawSaveButton();
        }
    }

    void drawMenu() {
        fill(255);
        textSize(48);
        text("Hou Yi: Legend of the Archer", width/2, 100);
        drawButton("Play", width/2, 250, 200, 50);
        drawButton("Load Game", width/2, 320, 200, 50);
        textSize(16);
        text("WASD to move", width/2, 400);
        text("Current Level: " + currentLevel, width/2, 450);
    }

    void drawButton(String label, float x, float y, float w, float h) {
        fill(mouseX > x-w/2 && mouseX < x+w/2 && mouseY > y-h/2 && mouseY < y+h/2 ? 
            color(100, 150, 255) : color(70, 120, 200));
        rect(x, y, w, h, 5);
        fill(255);
        textSize(20);
        text(label, x, y);
    }

    void drawSaveButton() {
        drawButton("Save Game", width-100, 30, 150, 40);
    }

    void drawWorld() {
        pushMatrix();
        translate(-viewX, -viewY);
        
        noFill();
        stroke(100);
        rect(worldWidth/2, worldHeight/2, worldWidth, worldHeight);
        
        updatePlayerPosition();
        player.display(this);
        
        for (Enemy e : enemies) {
            e.display(this);
            if (dist(player.x, player.y, e.x, e.y) < 50) {
                currentEnemy = e;
                screen = "battle";
                resetBattle();
                break;
            }
        }
        
if (bossReady && dist(player.x, player.y, worldWidth / 2, worldHeight / 2) < 60) {
    boss = new Boss(worldWidth / 2, worldHeight / 2, bossImg, currentLevel);
    screen = "boss";
    resetBattle();
}

        
        popMatrix();
        
        fill(255);
        textSize(20);
        text("Level " + currentLevel, width-100, 30);
        text("Defeated: " + enemiesDefeated + "/5", 100, 30);
    }

    void updatePlayerPosition() {
        if (keys['w'] || keys['W']) player.y = max(player.size/2, player.y - 3);
        if (keys['s'] || keys['S']) player.y = min(worldHeight - player.size/2, player.y + 3);
        if (keys['a'] || keys['A']) player.x = max(player.size/2, player.x - 3);
        if (keys['d'] || keys['D']) player.x = min(worldWidth - player.size/2, player.x + 3);
        
        viewX = constrain(player.x - width/2, 0, worldWidth - width);
        viewY = constrain(player.y - height/2, 0, worldHeight - height);
    }

    void drawBattle() {
        float battleBoxWidth = 500;
        float battleBoxHeight = 200;
        float battleBoxX = width/2 - battleBoxWidth/2;
        float battleBoxY = height/2 + 100 - battleBoxHeight/2;
        
        stroke(255);
        noFill();
        rect(width/2, height/2+100, battleBoxWidth, battleBoxHeight, 10);
        
        image(currentEnemy.img, width/2, 150, 100, 100);
        fill(255);
        text(currentEnemy.name, width/2, 120);
        
        drawHealthBar(width/2, 100, 150, currentEnemy.health, currentEnemy.maxHealth, color(255,0,0));
        drawHealthBar(width/2, height-50, 150, player.health, player.maxHealth, color(0,255,0));
        
        if (playerTurn) {
            if (inFightMenu) {
                textSize(24);
                fill(255);
                text("Attacking!", width/2, height/2);
                drawButton("Back", width/2, height/2+80, 100, 30);
            } 
            else if (inItemMenu) {
                textSize(24);
                fill(255);
                text("ITEMS", width/2, height/2-50);
                for (int i = 0; i < player.items.size(); i++) {
                    fill(i == selectedOption ? color(255,255,0) : color(255));
                    text(player.items.get(i), width/2, height/2 + i*30);
                }
                drawButton("Back", width/2, height/2+100, 100, 30);
            }
            else if (inActMenu) {
                textSize(24);
                fill(255);
                text("ACTIONS", width/2, height/2-50);
                for (int i = 0; i < actOptions.length; i++) {
                    fill(i == selectedOption ? color(255,255,0) : color(255));
                    text(actOptions[i], width/2, height/2 + i*30);
                }
                drawButton("Back", width/2, height/2+100, 100, 30);
            }
            else {
                for (int i = 0; i < battleOptions.length; i++) {
                    float x = width/2 - 100 + (i%2)*200;
                    float y = height/2 + 30 + (i/2)*60;
                    fill(i == selectedOption ? color(255,255,0) : color(255));
                    text(battleOptions[i], x, y);
                }
            }
        } else {
            fill(255,0,0);
            ellipse(heartX, heartY, 20, 20);
            
            attackTimer++;
            if (attackTimer % (max(10, 40 - currentLevel*2)) == 0) {
                createEnemyAttack(currentEnemy.attackPattern);
            }
            
            for (int i = enemyBullets.size()-1; i >= 0; i--) {
                Bullet b = enemyBullets.get(i);
                b.update();
                b.display();
                if (dist(b.x, b.y, heartX, heartY) < 15) {
                    player.health -= 5 + currentLevel/2;
                    enemyBullets.remove(i);
                } else if (b.y > height || b.x < 0 || b.x > width) {
                    enemyBullets.remove(i);
                }
            }
            
            if (keys[LEFT] || keys['a']) heartX = max(battleBoxX + 20, heartX-3);
            if (keys[RIGHT] || keys['d']) heartX = min(battleBoxX + battleBoxWidth - 20, heartX+3);
            if (keys[UP] || keys['w']) heartY = max(battleBoxY + 20, heartY-3);
            if (keys[DOWN] || keys['s']) heartY = min(battleBoxY + battleBoxHeight - 20, heartY+3);
            
            if (attackTimer >= 180) {
                playerTurn = true;
                attackTimer = 0;
                enemyBullets.clear();
                if (currentEnemy.health <= 0) {
                    enemies.remove(currentEnemy);
                    enemiesDefeated++;
                    if (enemiesDefeated >= 5) bossReady = true;
                    screen = "level";
                    if (enemies.isEmpty()) createEnemies();
                }
            }
        }
    }

void drawHealthBar(float x, float y, float w, float current, float max, int c) {
    fill(50);
    rect(x, y, w, 20, 5);
    float healthWidth = map(current, 0, max, 0, w);
    fill(c);
    rect(x - w / 2 + healthWidth / 2, y, healthWidth, 20, 5);
    fill(255);
    textSize(12);
    text((int)current + "/" + (int)max, x, y);
}

    void createEnemyAttack(int pattern) {
        switch(pattern) {
            case 0:
                enemyBullets.add(new Bullet(width/2, 150, 0, 3 + currentLevel*0.2f, this));
                break;
            case 1:
                for(int i = -2; i <= 2; i++) {
                    enemyBullets.add(new Bullet(width/2 + i*30, 150, i*0.7f, 2.5f + currentLevel*0.15f, this));
                }
                break;
            case 2:
                float angle = attackTimer * 0.2f;
                enemyBullets.add(new Bullet(width/2 + cos(angle)*40, 150 + sin(angle)*40, 
                    cos(angle)*1.5f, sin(angle)*1.5f + 1.5f, this));
                break;
        }
    }

    void drawBossFight() {
        background(0);
        boss.display(this);
        drawHealthBar(width/2, 50, 300, boss.health, boss.maxHealth, color(255,200,0));
        drawHealthBar(width/2, height-50, 150, player.health, player.maxHealth, color(0,255,0));
        
        if (playerTurn) {
            if (inFightMenu) {
                textSize(24);
                fill(255);
                text("Attacking the Sun!", width/2, height/2);
                drawButton("Back", width/2, height/2+80, 100, 30);
            } else {
                for (int i = 0; i < battleOptions.length; i++) {
                    float x = width/2 - 100 + (i%2)*200;
                    float y = height/2 + 30 + (i/2)*60;
                    fill(i == selectedOption ? color(255,255,0) : color(255));
                    text(battleOptions[i], x, y);
                }
            }
        } else {
            fill(255,0,0);
            ellipse(heartX, heartY, 20, 20);
            attackTimer++;
            if (attackTimer % 30 == 0) createBossAttack();
            
            for (int i = enemyBullets.size()-1; i >= 0; i--) {
                Bullet b = enemyBullets.get(i);
                b.update();
                b.display();
                if (dist(b.x, b.y, heartX, heartY) < 15) {
                    player.health -= 10 + currentLevel;
                    enemyBullets.remove(i);
                } else if (b.y > height || b.x < 0 || b.x > width) {
                    enemyBullets.remove(i);
                }
            }
            
            if (keys[LEFT] || keys['a']) heartX = max(width/2-200, heartX-3);
            if (keys[RIGHT] || keys['d']) heartX = min(width/2+200, heartX+3);
            if (keys[UP] || keys['w']) heartY = max(height/2+50, heartY-3);
            if (keys[DOWN] || keys['s']) heartY = min(height/2+150, heartY+3);
            
            if (attackTimer >= 240) {
                playerTurn = true;
                attackTimer = 0;
                enemyBullets.clear();
                if (boss.health <= 0) {
                    levelWon = true;
                    screen = "victory";
                    GameData.saveGame(currentLevel+1, 100, 0);
                }
            }
        }
    }

    void createBossAttack() {
        switch((attackTimer/30) % 3) {
            case 0:
                for (int i = 0; i < 8; i++) {
                    float angle = TWO_PI * i/8;
                    enemyBullets.add(new Bullet(width/2, height/2, cos(angle)*4, sin(angle)*4, this));
                }
                break;
            case 1:
                float angle = atan2(heartY-height/2, heartX-width/2);
                enemyBullets.add(new Bullet(width/2, height/2, cos(angle)*6, sin(angle)*6, this));
                break;
            case 2:
                float spiralAngle = attackTimer * 0.1f;
                enemyBullets.add(new Bullet(width/2 + cos(spiralAngle)*50, height/2 + sin(spiralAngle)*50,
                    cos(spiralAngle + PI/2)*3, sin(spiralAngle + PI/2)*3, this));
                break;
        }
    }

    void drawWinScreen() {
        background(0);
        fill(255);
        textSize(40);
        text("LEVEL " + currentLevel + " COMPLETE!", width/2, height/2-50);
        textSize(30);
        text("YOU WIN!", width/2, height/2);
        textSize(24);
        if (currentLevel < 9) {
            text("Press C to continue to Level " + (currentLevel+1), width/2, height/2+80);
        } else {
            text("CONGRATULATIONS! YOU BEAT THE GAME!", width/2, height/2+80);
        }
        text("Press M for Menu", width/2, height/2+120);
    }

    void createEnemies() {
        enemies.clear();
        PImage[] enemyImages = {enemyImg1, enemyImg2, enemyImg3, enemyImg4, enemyImg5};
        Random rand = new Random();
        for (int i = 0; i < 3 + currentLevel; i++) {
            float x = random(width/4, worldWidth-width/4);
            float y = random(height/4, worldHeight-height/4);
            PImage img = enemyImages[rand.nextInt(enemyImages.length)];
            int pattern = rand.nextInt(3);
            Enemy e = new Enemy(x, y, img, pattern);
            e.maxHealth = enemyMaxHealth;
            e.health = enemyMaxHealth;
            enemies.add(e);
        }
    }

    public void mousePressed() {
        if (screen.equals("menu")) {
            if (mouseX > width/2-100 && mouseX < width/2+100 && mouseY > 225 && mouseY < 275) {
                screen = "level";
                player.health = playerMaxHealth;
                enemiesDefeated = 0;
                bossReady = false;
                createEnemies();
            } 
            else if (mouseX > width/2-100 && mouseX < width/2+100 && mouseY > 295 && mouseY < 345) {
                int[] savedData = GameData.loadGame();
                if (savedData != null) {
                    currentLevel = savedData[0];
                    player.health = savedData[1];
                    enemiesDefeated = savedData[2];
                    bossReady = enemiesDefeated >= 5;
                }
                screen = "level";
                createEnemies();
            }
        }
        else if (screen.equals("battle") && playerTurn) {
            if (inFightMenu && mouseX > width/2-50 && mouseX < width/2+50 && mouseY > height/2+65 && mouseY < height/2+95) {
                inFightMenu = false;
            }
            else if ((inItemMenu || inActMenu) && mouseX > width/2-50 && mouseX < width/2+50 && mouseY > height/2+85 && mouseY < height/2+115) {
                inItemMenu = false;
                inActMenu = false;
            }
        }
        else if (!screen.equals("menu") && mouseX > width-175 && mouseX < width-25 && mouseY > 10 && mouseY < 50) {
            GameData.saveGame(currentLevel, player.health, enemiesDefeated);
        }
    }

    public void keyPressed() {
        if (key < keys.length) keys[key] = true;
        
        if (screen.equals("battle") && playerTurn) {
            if (!inFightMenu && !inItemMenu && !inActMenu) {
                if (keyCode == UP) selectedOption = max(0, selectedOption-2);
                if (keyCode == DOWN) selectedOption = min(3, selectedOption+2);
                if (keyCode == LEFT) selectedOption = max(0, selectedOption-1);
                if (keyCode == RIGHT) selectedOption = min(3, selectedOption+1);
                
                if (key == ' ' || key == '\n') {
                    switch(battleOptions[selectedOption]) {
                        case "Fight":
                            currentEnemy.health -= 10 + currentLevel;
                            inFightMenu = true;
                            break;
                        case "Act":
                            inActMenu = true;
                            selectedOption = 0;
                            break;
                        case "Item":
                            inItemMenu = true;
                            selectedOption = 0;
                            break;
                        case "Mercy":
                            attemptMercy();
                            break;
                    }
                }
            } 
            else if (inFightMenu || inItemMenu || inActMenu) {
                if (keyCode == UP) selectedOption = max(0, selectedOption-1);
                if (keyCode == DOWN) {
                    int maxOpt = inActMenu ? actOptions.length-1 : player.items.size()-1;
                    selectedOption = min(maxOpt, selectedOption+1);
                }
                
                if (key == ' ' || key == '\n') {
                    if (inFightMenu) {
                        inFightMenu = false;
                        playerTurn = false;
                    } 
                    else if (inItemMenu && player.items.size() > 0) {
                        if (player.items.get(selectedOption).equals("Heal")) {
                            player.health = min(player.maxHealth, player.health + 30);
                        }
                        player.items.remove(selectedOption);
                        inItemMenu = false;
                        playerTurn = false;
                    }
                    else if (inActMenu) {
                        if (actOptions[selectedOption].equals("Flatter")) {
                            currentEnemy.health -= 5;
                        }
                        inActMenu = false;
                        playerTurn = false;
                    }
                }
            }
        } 
        else if (screen.equals("victory")) {
            if (key == 'c' || key == 'C') {
                if (currentLevel < 9) {
                    currentLevel++;
                    enemiesDefeated = 0;
                    bossReady = false;
                    player.health = playerMaxHealth;
                    createEnemies();
                    screen = "level";
                }
            } 
            else if (key == 'm' || key == 'M') {
                screen = "menu";
            }
        }
    }

    void attemptMercy() {
        float mercyThreshold = 0.7f;
        if (currentEnemy != null && currentEnemy.health <= currentEnemy.maxHealth * mercyThreshold) {
            enemies.remove(currentEnemy);
            enemiesDefeated++;
            if (enemiesDefeated >= 5) bossReady = true;
            screen = "level";
            if (enemies.isEmpty()) createEnemies();
        } 
        else if (boss != null && boss.health <= boss.maxHealth * mercyThreshold) {
            levelWon = true;
            screen = "victory";
            GameData.saveGame(currentLevel+1, 100, 0);
        } 
        else {
            println("Not weak enough to spare!");
            playerTurn = false;
        }
    }

    public void keyReleased() {
        if (key < keys.length) keys[key] = false;
    }
}