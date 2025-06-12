/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;

public class Player {
    float x, y;
    int health = 100;
    int attackPower = 10;
    PImage sprite;
    ArrayList<String> items = new ArrayList<>();

    public Player(float x, float y, PImage sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        items.add("heal");
        items.add("power");
    }

    public void update(boolean[] keys) {
        if (keys['w'] || keys['W']) y -= 3;
        if (keys['s'] || keys['S']) y += 3;
        if (keys['a'] || keys['A']) x -= 3;
        if (keys['d'] || keys['D']) x += 3;
    }

    public void display(PApplet app) {
        app.image(sprite, x, y, 40, 40);
    }
}