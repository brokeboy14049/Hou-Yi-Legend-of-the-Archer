/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;

public class Fireball {
    float x, y;
    float speedY = 5;
    PImage sprite;

    public Fireball(float x, float y, PImage sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }

    public void update() {
        y += speedY;
    }

    public void display(PApplet app) {
        app.image(sprite, x, y);
    }

    public boolean hits(Player p) {
        return PApplet.dist(x, y, p.x, p.y) < 30;
    }
}

