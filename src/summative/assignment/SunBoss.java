/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;

public class SunBoss {
    float x, y;
    int health = 200; // Boss has more health
    PImage sprite;

    public SunBoss(float x, float y, PImage sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public void display(PApplet app) {
        // Draw boss with fixed size (120x120)
        app.image(sprite, x, y, 120, 120);

        // Health bar
        app.fill(255);
        app.rect(app.width / 2, 30, 300, 20);
        app.fill(255, 200, 0);
        float w = app.map(health, 0, 200, 0, 300);
        app.rect(app.width / 2 - 150 + w / 2, 30, w, 20);
        
        // Boss name
        app.fill(255);
        app.textSize(24);
        app.text("SUN BOSS", app.width / 2, 70);
    }
}