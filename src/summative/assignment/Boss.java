/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;

public class Boss {
    public float x, y;
    public int health = 200;
    public PImage img;

    public Boss(float x, float y, PImage img) {
        this.x = x;
        this.y = y;
        this.img = img;
    }

    public void display(PApplet p) {
        p.image(img, x, y, 120, 120);
    }
}
