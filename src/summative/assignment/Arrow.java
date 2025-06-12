/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;

public class Arrow {
    float x, y;
    float vx, vy;
    PImage sprite;

    public Arrow(float x, float y, float vx, float vy, PImage sprite) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.sprite = sprite;
    }

    public void update() {
        x += vx;
        y += vy;
    }

    public void display(PApplet app) {
        app.image(sprite, x, y, 40, 10);
    }

    public boolean hits(SunBoss boss) {
        float d = appDist(boss.x, boss.y, x, y);
        return d < 40;
    }

    private float appDist(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}


