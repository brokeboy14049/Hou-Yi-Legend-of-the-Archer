/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;

public class Enemy {
    public float x, y;
    public int health = 100;
    public PImage img;
    public int attackPattern; // 0=Single, 1=Spread, 2=Spiral
    public String name;
    
    public Enemy(float x, float y, PImage img, int pattern) {
        this.x = x;
        this.y = y;
        this.img = img;
        this.attackPattern = pattern;
        
        // Assign names and adjust health based on pattern
        String[] names = {"Basic Enemy", "Spread Shooter", "Spiral Attacker"};
        this.name = names[pattern];
        this.health = 80 + pattern * 20; // Harder enemies have more health
    }

    public void display(PApplet p) {
        p.image(img, x, y, 60, 60);
    }
}