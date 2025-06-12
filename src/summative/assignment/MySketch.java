package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.Random;

public class MySketch extends PApplet {
    // Game state variables
    String screen = "menu";
    Player player;
    PImage playerImg, enemy1Img, enemy2Img, enemy3Img, enemy4Img, enemy5Img, sunImg, arrowImg, fireballImg;
    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Arrow> arrows = new ArrayList<>();
    ArrayList<Fireball> fireballs = new ArrayList<>();
    ArrayList<Bullet> bullets = new ArrayList<>();
    Enemy currentEnemy;
    SunBoss sunBoss;
    boolean[] keys = new boolean[128];
    int defeatedEnemies = 0;
    boolean bossUnlocked = false;
    int mapX = 0, mapY = 0;

    // Battle system variables
    boolean playerTurn = true;
    boolean inBossFight = false;
    int fireballTimer = 0;
    int laserTimer = 0;
    ArrayList<Laser> lasers = new ArrayList<>();
    int battleCooldown = 0;
    boolean canBattle = true;

    // Undertale-style battle variables
    int SOUL_COLOR = color(255, 0, 0);
    float soulX, soulY;
    float soulSpeed = 3;
    boolean inAttackMenu = false;
    boolean inItemMenu = false;
    ArrayList<String> attackOptions = new ArrayList<>();
    int selectedOption = 0;

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
        enemy1Img = loadImage("images/enemy1.png");
        enemy2Img = loadImage("images/enemy2.png");
        enemy3Img = loadImage("images/enemy3.png");
        enemy4Img = loadImage("images/enemy4.png");
        enemy5Img = loadImage("images/enemy5.png");
        sunImg = loadImage("images/sun.png");
        arrowImg = loadImage("images/arrow.png");
        fireballImg = loadImage("images/fireball.png");

        player = new Player(width / 2, height / 2, playerImg);
        
        attackOptions.add("Fight");
        attackOptions.add("Act");
        attackOptions.add("Item");
        attackOptions.add("Mercy");
        
        soulX = width/2;
        soulY = height/2 + 150;

        generateEnemies();
    }

    public void draw() {
        background(30);
        
        if (!canBattle) {
            battleCooldown++;
            if (battleCooldown >= 120) {
                canBattle = true;
                battleCooldown = 0;
            }
        }

        switch (screen) {
            case "menu": drawMenu(); break;
            case "level": drawLevel(); break;
            case "battle": drawBattle(); break;
            case "boss": drawBossBattle(); break;
        }
    }

    void drawMenu() {
        fill(255);
        textSize(48);
        text("Hou Yi RPG", width / 2, 100);
        drawButton("Play", width / 2, 250);
        
        textSize(16);
        text("WASD to move, SPACE to shoot arrows", width / 2, 350);
        text("Defeat 5 enemies to fight the Sun Boss", width / 2, 380);
    }

    void drawButton(String label, float x, float y) {
        fill(100);
        rect(x, y, 200, 50);
        fill(255);
        textSize(24);
        text(label, x, y);
    }

    void drawLevel() {
        player.update(keys);
        player.display(this);

        if (player.x < 0) { mapX--; player.x = width; enterNewArea(); }
        if (player.x > width) { mapX++; player.x = 0; enterNewArea(); }
        if (player.y < 0) { mapY--; player.y = height; enterNewArea(); }
        if (player.y > height) { mapY++; player.y = 0; enterNewArea(); }

        for (Enemy e : enemies) {
            e.display(this);
            
            if (canBattle && dist(player.x, player.y, e.x, e.y) < 50) {
                currentEnemy = e;
                screen = "battle";
                playerTurn = true;
                inAttackMenu = false;
                inItemMenu = false;
                soulX = width/2;
                soulY = height/2 + 150;
                break;
            }
        }
        
        fill(255);
        textSize(20);
        text("Enemies Defeated: " + defeatedEnemies + "/5", 100, 30);
    }

    void enterNewArea() {
        if (bossUnlocked && random(1) < 0.3) {
            screen = "boss";
            sunBoss = new SunBoss(width / 2, 100, sunImg);
            inBossFight = true;
            arrows.clear();
            fireballs.clear();
            lasers.clear();
        } else {
            generateEnemies();
        }
    }

    void generateEnemies() {
        enemies.clear();
        Random rand = new Random();
        PImage[] enemyImgs = {enemy1Img, enemy2Img, enemy3Img, enemy4Img, enemy5Img};
        
        for (int i = 0; i < 3; i++) {
            float ex = rand.nextInt(width - 100) + 50;
            float ey = rand.nextInt(height - 100) + 50;
            PImage sprite = enemyImgs[rand.nextInt(enemyImgs.length)];
            Enemy enemy = new Enemy(ex, ey, sprite);
            enemies.add(enemy);
        }
    }

    void drawBattle() {
        background(30);
        drawBattleBox();
        
        if (currentEnemy != null) {
            image(currentEnemy.sprite, width/2, 150, 120, 120);
            
            fill(255);
            rect(width/2, 100, 150, 10);
            fill(255, 0, 0);
            float w = map(currentEnemy.health, 0, 100, 0, 150);
            rect(width/2 - 75 + w/2, 100, w, 10);
            
            textSize(20);
            fill(255);
            text(currentEnemy.name, width/2, 70);
        }
        
        fill(SOUL_COLOR);
        noStroke();
        beginShape();
        vertex(soulX, soulY - 10);
        bezierVertex(soulX + 10, soulY - 15, soulX + 15, soulY - 5, soulX, soulY + 10);
        bezierVertex(soulX - 15, soulY - 5, soulX - 10, soulY - 15, soulX, soulY - 10);
        endShape();
        
        if (playerTurn) {
            if (inAttackMenu) {
                drawAttackMenu();
            } else if (inItemMenu) {
                drawItemMenu();
            } else {
                drawMainMenu();
            }
        } else {
            handleEnemyAttack();
        }
        
        drawPlayerHealth();
        
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update();
            b.display(this);
        }
    }

    void drawBattleBox() {
        stroke(255);
        strokeWeight(4);
        noFill();
        rect(width/2, height/2 + 100, 500, 200);
        
        noStroke();
        fill(0);
        rect(width/2, height/2 + 100, 496, 196);
    }

    void drawMainMenu() {
        float yPos = height/2 + 50;
        for (int i = 0; i < attackOptions.size(); i++) {
            if (i == selectedOption) {
                fill(255, 255, 0);
            } else {
                fill(255);
            }
            text(attackOptions.get(i), width/2 - 150 + (i % 2) * 200, yPos + (i / 2) * 40);
        }
    }

    void drawAttackMenu() {
        textSize(20);
        fill(255);
        text("Select target", width/2, height/2 + 50);
        
        if (currentEnemy != null) {
            stroke(255, 255, 0);
            strokeWeight(2);
            noFill();
            rect(width/2, 150, 130, 130);
        }
    }

    void drawItemMenu() {
        textSize(20);
        fill(255);
        text("ITEMS", width/2, height/2 + 30);
        
        for (int i = 0; i < player.items.size(); i++) {
            if (i == selectedOption) {
                fill(255, 255, 0);
            } else {
                fill(255);
            }
            text(player.items.get(i), width/2, height/2 + 60 + i * 30);
        }
    }

    void drawPlayerHealth() {
        textSize(16);
        fill(255);
        text("HP", width/2 - 180, height - 50);
        
        fill(255);
        rect(width/2 - 80, height - 50, 120, 10);
        fill(0, 200, 0);
        float pw = map(player.health, 0, 100, 0, 120);
        rect(width/2 - 140 + pw/2, height - 50, pw, 10);
        
        text(player.health + "/100", width/2 + 30, height - 50);
    }

    void handleEnemyAttack() {
        fill(255, 0, 0);
        textSize(24);
        text(currentEnemy.getRandomAttackText(), width/2, height/2);
        
        if (frameCount % 30 == 0) {
            float bx = random(width/2 - 200, width/2 + 200);
            float by = height/2 + 50;
            bullets.add(new Bullet(bx, by, 0, 3, this));
        }
        
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update();
            b.display(this);
            
            if (dist(b.x, b.y, soulX, soulY) < 10) {
                player.health -= 5;
                bullets.remove(i);
            } else if (b.y > height) {
                bullets.remove(i);
            }
        }
        
        if (frameCount % 180 == 0) {
            playerTurn = true;
            bullets.clear();
            soulX = width/2;
            soulY = height/2 + 150;
        }
    }

    void drawBossBattle() {
        background(0);
        player.display(this);
        sunBoss.display(this);
        
        for (int i = arrows.size() - 1; i >= 0; i--) {
            Arrow a = arrows.get(i);
            a.update();
            a.display(this);
            
            if (a.hits(sunBoss)) {
                sunBoss.health -= 10;
                arrows.remove(i);
            }
            
            if (a.x < 0 || a.x > width || a.y < 0 || a.y > height) {
                arrows.remove(i);
            }
        }

        fireballTimer++;
        if (fireballTimer % 90 == 0) {
            fireballs.add(new Fireball(sunBoss.x + random(-50, 50), sunBoss.y + 50, fireballImg));
        }

        for (int i = fireballs.size() - 1; i >= 0; i--) {
            Fireball f = fireballs.get(i);
            f.update();
            f.display(this);
            
            if (f.hits(player)) {
                player.health -= 15;
                fireballs.remove(i);
            } else if (f.y > height) {
                fireballs.remove(i);
            }
        }

        laserTimer++;
        if (laserTimer % 180 == 0) {
            lasers.add(new Laser(sunBoss.x, sunBoss.y, random(TWO_PI)));
        }

        for (int i = lasers.size() - 1; i >= 0; i--) {
            Laser l = lasers.get(i);
            l.update();
            l.display(this);
            
            if (l.hits(player)) {
                player.health -= 10;
            }
            
            if (!l.isActive()) {
                lasers.remove(i);
            }
        }

        if (sunBoss.health <= 0) {
            textSize(40);
            fill(255);
            text("YOU WIN!", width / 2, height / 2);
            noLoop();
        }
        
        if (player.health <= 0) {
            screen = "menu";
            player.health = 100;
            defeatedEnemies = 0;
            bossUnlocked = false;
        }
    }

    public void keyPressed() {
        if (key < keys.length) {
            keys[key] = true;
        }

        if (screen.equals("battle")) {
            if (playerTurn) {
                if (!inAttackMenu && !inItemMenu) {
                    if (keyCode == UP) selectedOption = max(0, selectedOption - 2);
                    if (keyCode == DOWN) selectedOption = min(3, selectedOption + 2);
                    if (keyCode == LEFT) selectedOption = max(0, selectedOption - 1);
                    if (keyCode == RIGHT) selectedOption = min(3, selectedOption + 1);
                    
                    if (key == ' ' || key == '\n') {
                        if (attackOptions.get(selectedOption).equals("Fight")) {
                            inAttackMenu = true;
                            selectedOption = 0;
                        } else if (attackOptions.get(selectedOption).equals("Item")) {
                            inItemMenu = true;
                            selectedOption = 0;
                        } else if (attackOptions.get(selectedOption).equals("Act")) {
                            playerTurn = false;
                        } else if (attackOptions.get(selectedOption).equals("Mercy")) {
                            playerTurn = false;
                        }
                    }
                } 
                else if (inAttackMenu) {
                    if (key == ' ' || key == '\n') {
                        currentEnemy.health -= player.attackPower;
                        inAttackMenu = false;
                        playerTurn = false;
                    } else if (key == 'e' || key == 'E') {
                        inAttackMenu = false;
                    }
                }
                else if (inItemMenu) {
                    if (keyCode == UP) selectedOption = max(0, selectedOption - 1);
                    if (keyCode == DOWN) selectedOption = min(player.items.size()-1, selectedOption + 1);
                    
                    if (key == ' ' || key == '\n') {
                        String item = player.items.get(selectedOption);
                        if (item.equals("heal")) {
                            player.health = min(100, player.health + 30);
                            player.items.remove(selectedOption);
                            textSize(20);
                            fill(0, 255, 0);
                            text("Healed 30 HP!", width/2, height/2 + 180);
                        } else if (item.equals("power")) {
                            player.attackPower += 5;
                            player.items.remove(selectedOption);
                            textSize(20);
                            fill(255, 255, 0);
                            text("Attack +5!", width/2, height/2 + 180);
                        }
                        inItemMenu = false;
                        playerTurn = false;
                    } else if (key == 'e' || key == 'E') {
                        inItemMenu = false;
                    }
                }
            } else {
                if (keyCode == LEFT) soulX = max(width/2 - 200, soulX - soulSpeed);
                if (keyCode == RIGHT) soulX = min(width/2 + 200, soulX + soulSpeed);
                if (keyCode == UP) soulY = max(height/2 + 50, soulY - soulSpeed);
                if (keyCode == DOWN) soulY = min(height/2 + 150, soulY + soulSpeed);
            }
        } else if (screen.equals("boss")) {
            if (key == ' ') {
                float dx = mouseX - player.x;
                float dy = mouseY - player.y;
                float dist = sqrt(dx * dx + dy * dy);
                float speed = 7;
                float vx = (dx / dist) * speed;
                float vy = (dy / dist) * speed;
                arrows.add(new Arrow(player.x, player.y, vx, vy, arrowImg));
            }
        }
    }

    public void keyReleased() {
        if (key < keys.length) {
            keys[key] = false;
        }
    }

    public void mousePressed() {
        if (screen.equals("menu") && isMouseInside(width / 2, 250, 200, 50)) {
            screen = "level";
            player.health = 100;
            player.attackPower = 10;
            player.items.clear();
            player.items.add("heal");
            player.items.add("power");
            defeatedEnemies = 0;
            bossUnlocked = false;
            generateEnemies();
        }
    }

    boolean isMouseInside(float x, float y, float w, float h) {
        return mouseX > x - w / 2 && mouseX < x + w / 2 && 
               mouseY > y - h / 2 && mouseY < y + h / 2;
    }
}