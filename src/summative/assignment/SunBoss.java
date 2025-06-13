/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;

public class SunBoss {
    PApplet sketch;
    float x, y;
    float health;
    float maxHealth = 500;
    PImage sprite;
    int attackPattern = 0;
    
    public SunBoss(PApplet sketch) {
        this.sketch = sketch;
        this.x = sketch.width/2;
        this.y = 150;
        this.health = maxHealth;
        this.sprite = sketch.loadImage("sunboss.png");
    }
    
    public void display() {
        sketch.image(sprite, x, y, 150, 150);
    }
    
    public void attack(ArrayList<Bullet> bullets) {
        switch(attackPattern) {
            case 0: // Radial attack
                for (int i = 0; i < 12; i++) {
                    float angle = PApplet.TWO_PI * i/12;
                    bullets.add(new Bullet(
                        x, y,
                        PApplet.cos(angle) * 3,
                        PApplet.sin(angle) * 3,
                        sketch
                    ));
                }
                break;
            case 1: // Spiral attack
                float spiralAngle = sketch.frameCount * 0.1f;
                bullets.add(new Bullet(
                    x, y,
                    PApplet.cos(spiralAngle) * 4,
                    PApplet.sin(spiralAngle) * 4,
                    sketch
                ));
                break;
        }
        attackPattern = (attackPattern + 1) % 2;
    }
}