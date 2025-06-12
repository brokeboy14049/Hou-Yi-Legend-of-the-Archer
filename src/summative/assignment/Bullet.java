/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;

public class Bullet {
    public float x, y;
    public float vx, vy;
    private PApplet sketch;

    public Bullet(float x, float y, float vx, float vy, PApplet sketch) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.sketch = sketch;
    }

    public void update() {
        x += vx;
        y += vy;
    }

    public void display() {  // Removed PApplet parameter since we store it in constructor
        sketch.fill(255, 0, 0);
        sketch.ellipse(x, y, 10, 10);
    }
}