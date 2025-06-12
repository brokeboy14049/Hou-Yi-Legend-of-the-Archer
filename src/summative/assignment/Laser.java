/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;

public class Laser {
    float x, y;
    float angle;
    float length = 300;
    int duration = 60;
    int life = 0;

    public Laser(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void update() {
        life++;
    }

    public void display(PApplet app) {
        app.pushMatrix();
        app.translate(x, y);
        app.rotate(angle);
        app.stroke(255, 0, 0);
        app.strokeWeight(6);
        app.line(0, 0, length, 0);
        app.popMatrix();
    }

    public boolean isActive() {
        return life < duration;
    }

    public boolean hits(Player p) {
        float px = p.x - x;
        float py = p.y - y;
        float dx = (float) Math.cos(angle);
        float dy = (float) Math.sin(angle);
        float dot = px * dx + py * dy;
        if (dot < 0 || dot > length) return false;
        float cross = Math.abs(px * dy - py * dx);
        return cross < 10;
    }
}
