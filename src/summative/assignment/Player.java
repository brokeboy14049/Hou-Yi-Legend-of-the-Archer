/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;

public class Player {
    public float x, y;
    public int health = 100;
    public PImage img;
    public ArrayList<String> items = new ArrayList<>();
    int maxHealth;
    float size;

    public Player(float x, float y, PImage img) {
        this.x = x;
        this.y = y;
        this.img = img;
        items.add("Heal");
        items.add("Power");
    }

    public void display(PApplet p) {
        p.image(img, x, y, 40, 40);
    }
}