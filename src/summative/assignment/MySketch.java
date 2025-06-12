package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.Random;

public class MySketch extends PApplet {
    // Game state
    String screen = "menu";
    Player player;
    PImage playerImg, enemyImg1, enemyImg2, enemyImg3, enemyImg4, enemyImg5, bossImg;
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
    String[] battleOptions = {"Fight", "Act", "Item", "Mercy"};
    int selectedOption = 0;
    int attackTimer = 0;
    int currentAttackPattern = 0;
    
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
        
        player = new Player(width/2, height/2, playerImg);
        resetBattle();
        
        // Load saved game
        int[] savedData = GameData.loadGame();
        currentLevel = savedData[0];
        player.health = savedData[1];
        enemiesDefeated = savedData[2];
    }

    void resetBattle() {
        heartX = width/2;
        heartY = height/2 + 150;
        attackTimer = 0;
        playerTurn = true;
        inFightMenu = false;
        inItemMenu = false;
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
        // Title
        fill(255);
        textSize(48);
        text("Hou Yi RPG", width/2, 100);
        
        // Play button
        drawButton("Play", width/2, 250, 200, 50);
        
        // Load button
        drawButton("Load Game", width/2, 320, 200, 50);
        
        // Instructions
        textSize(16);
        text("WASD to move", width/2, 400);
        text("Current Level: " + currentLevel, width/2, 450);
    }

    void drawButton(String label, float x, float y, float w, float h) {
        fill(mouseOver(x, y, w, h) ? color(150) : color(100));
        rect(x, y, w, h, 10);
        fill(255);
        textSize(24);
        text(label, x, y);
    }

    void drawWorld() {
        // Player movement
        if (keys['w'] || keys['W']) player.y -= 3;
        if (keys['s'] || keys['S']) player.y += 3;
        if (keys['a'] || keys['A']) player.x -= 3;
        if (keys['d'] || keys['D']) player.x += 3;
        
        player.display(this);
        
        // Enemies
        for (Enemy e : enemies) {
            e.display(this);
            
            // Start battle when close
            if (dist(player.x, player.y, e.x, e.y) < 50) {
                currentEnemy = e;
                screen = "battle";
                resetBattle();
            }
        }
        
        // UI
        fill(255);
        textSize(20);
        text("Level " + currentLevel, width-100, 30);
        text("Defeated: " + enemiesDefeated + "/5", 100, 30);
    }

    void drawBattle() {
        // Battle box
        stroke(255);
        noFill();
        rect(width/2, height/2+100, 500, 200, 10);
        
        // Enemy
        image(currentEnemy.img, width/2, 150, 100, 100);
        fill(255);
        text(currentEnemy.name, width/2, 120);
        
        // Health bars
        drawHealthBar(width/2, 100, 150, currentEnemy.health, 100, color(255,0,0));
        drawHealthBar(width/2, height-50, 150, player.health, 100, color(0,255,0));
        
        if (playerTurn) {
            // Player's turn
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
            else {
                // Battle menu
                for (int i = 0; i < battleOptions.length; i++) {
                    float x = width/2 - 100 + (i%2)*200;
                    float y = height/2 + 30 + (i/2)*60;
                    
                    fill(i == selectedOption ? color(255,255,0) : color(255));
                    text(battleOptions[i], x, y);
                }
            }
        } 
        else {
            // Enemy's turn - attack phase
            fill(255,0,0);
            ellipse(heartX, heartY, 20, 20);
            
            // Enemy attack logic
            attackTimer++;
            
            // Create attacks based on pattern
            if (attackTimer % (max(10, 40 - currentLevel*2)) == 0) { // Faster attacks at higher levels
                createEnemyAttack(currentEnemy.attackPattern);
            }
            
            // Update and draw bullets
            for (int i = enemyBullets.size()-1; i >= 0; i--) {
                Bullet b = enemyBullets.get(i);
                b.update();
                b.display();
                
                // Check collision with player's heart
                if (dist(b.x, b.y, heartX, heartY) < 15) {
                    player.health -= 5 + currentLevel/2; // Damage increases with level
                    enemyBullets.remove(i);
                } 
                // Remove bullets that go off screen
                else if (b.y > height || b.x < 0 || b.x > width) {
                    enemyBullets.remove(i);
                }
            }
            
            // Move soul with arrow keys
            if (keys[LEFT] || keys['a']) heartX = max(width/2-200, heartX-3);
            if (keys[RIGHT] || keys['d']) heartX = min(width/2+200, heartX+3);
            if (keys[UP] || keys['w']) heartY = max(height/2+50, heartY-3);
            if (keys[DOWN] || keys['s']) heartY = min(height/2+150, heartY+3);
            
            // End enemy turn after 3 seconds
            if (attackTimer >= 180) {
                playerTurn = true;
                attackTimer = 0;
                enemyBullets.clear();
                
                // Check if enemy defeated
                if (currentEnemy.health <= 0) {
                    enemies.remove(currentEnemy);
                    enemiesDefeated++;
                    if (enemiesDefeated >= 5) bossReady = true;
                    screen = "level";
                }
            }
        }
    }

void createEnemyAttack(int pattern) {
    switch(pattern) {
        case 0: // Single shot
            enemyBullets.add(new Bullet(
                width/2, 
                150, 
                0, 
                3 + currentLevel*0.2f, 
                this
            ));
            break;
            
        case 1: // Spread shot
            for(int i = -2; i <= 2; i++) {
                enemyBullets.add(new Bullet(
                    width/2 + i*30, 
                    150, 
                    i*0.7f, 
                    2.5f + currentLevel*0.15f, 
                    this
                ));
            }
            break;
            
        case 2: // Spiral shot
            float angle = attackTimer * 0.2f;
            enemyBullets.add(new Bullet(
                width/2 + cos(angle)*40, 
                150 + sin(angle)*40, 
                cos(angle)*1.5f, 
                sin(angle)*1.5f + 1.5f, 
                this
            ));
            break;
    }
}

    void drawBossFight() {
        background(0);
        player.display(this);
        boss.display(this);
        drawHealthBar(width/2, 50, 300, boss.health, 200, color(255,200,0));
        
        if (boss.health <= 0) {
            levelWon = true;
            screen = "victory";
            GameData.saveGame(currentLevel+1, 100, 0);
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

    void drawSaveButton() {
        drawButton("Save Game", width-80, 30, 120, 30);
    }

    void drawHealthBar(float x, float y, float w, float current, float max, int c) {
        fill(255);
        rect(x, y, w, 20, 5);
        fill(c);
        float healthW = map(current, 0, max, 0, w);
        rect(x - w/2 + healthW/2, y, healthW, 20, 5);
    }

    void createEnemies() {
        enemies.clear();
        PImage[] enemyImages = {enemyImg1, enemyImg2, enemyImg3, enemyImg4, enemyImg5};
        
        for (int i = 0; i < 3 + currentLevel; i++) {
            float x = random(width-100) + 50;
            float y = random(height-100) + 50;
            PImage img = enemyImages[(int)random(enemyImages.length)];
            int pattern = (int)random(3); // Random attack pattern
            enemies.add(new Enemy(x, y, img, pattern));
        }
    }

    boolean mouseOver(float x, float y, float w, float h) {
        return mouseX > x-w/2 && mouseX < x+w/2 && mouseY > y-h/2 && mouseY < y+h/2;
    }

    public void mousePressed() {
        if (screen.equals("menu")) {
            if (mouseOver(width/2, 250, 200, 50)) {
                screen = "level";
                player.health = 100;
                enemiesDefeated = 0;
                bossReady = false;
                createEnemies();
            } 
            else if (mouseOver(width/2, 320, 200, 50)) {
                int[] savedData = GameData.loadGame();
                currentLevel = savedData[0];
                player.health = savedData[1];
                enemiesDefeated = savedData[2];
                screen = "level";
                createEnemies();
            }
        }
        else if (screen.equals("battle") && playerTurn) {
            if (inFightMenu && mouseOver(width/2, height/2+80, 100, 30)) {
                inFightMenu = false;
            }
            else if (inItemMenu && mouseOver(width/2, height/2+100, 100, 30)) {
                inItemMenu = false;
            }
        }
        else if (!screen.equals("menu") && mouseOver(width-80, 30, 120, 30)) {
            GameData.saveGame(currentLevel, player.health, enemiesDefeated);
        }
    }

    public void keyPressed() {
        if (key < keys.length) keys[key] = true;
        
        if (screen.equals("battle") && playerTurn) {
            if (!inFightMenu && !inItemMenu) {
                if (keyCode == UP) selectedOption = max(0, selectedOption-2);
                if (keyCode == DOWN) selectedOption = min(3, selectedOption+2);
                if (keyCode == LEFT) selectedOption = max(0, selectedOption-1);
                if (keyCode == RIGHT) selectedOption = min(3, selectedOption+1);
                
                if (key == ' ' || key == '\n') {
                    if (battleOptions[selectedOption].equals("Fight")) {
                        currentEnemy.health -= 10 + currentLevel; // Damage scales with level
                        inFightMenu = true;
                    } 
                    else if (battleOptions[selectedOption].equals("Item")) {
                        inItemMenu = true;
                        selectedOption = 0;
                    }
                }
            } 
            else if (inFightMenu) {
                if (key == ' ' || key == '\n') {
                    inFightMenu = false;
                    playerTurn = false;
                }
            } 
            else if (inItemMenu) {
                if (keyCode == UP) selectedOption = max(0, selectedOption-1);
                if (keyCode == DOWN) selectedOption = min(player.items.size()-1, selectedOption+1);
                
                if (key == ' ' || key == '\n') {
                    if (player.items.get(selectedOption).equals("Heal")) {
                        player.health = min(100, player.health + 30);
                    }
                    player.items.remove(selectedOption);
                    inItemMenu = false;
                    playerTurn = false;
                }
            }
        } 
        else if (screen.equals("victory")) {
            if (key == 'c' || key == 'C') {
                if (currentLevel < 9) {
                    currentLevel++;
                    enemiesDefeated = 0;
                    createEnemies();
                    screen = "level";
                }
            } 
            else if (key == 'm' || key == 'M') {
                screen = "menu";
            }
        }
    }

    public void keyReleased() {
        if (key < keys.length) keys[key] = false;
    }
}