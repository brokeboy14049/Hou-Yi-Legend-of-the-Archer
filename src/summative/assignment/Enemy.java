/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;

public class Enemy {
    float x, y;
    int health = 100;
    PImage sprite;
    String name;
    String[] attackTexts;
    
    public Enemy(float x, float y, PImage sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        
        String[] names = {"Froggit", "Whimsun", "Moldsmal", "Loox", "Vegetoid"};
        String[][] attacks = {
            {"Froggit hops around!", "Ribbit!"},
            {"Whimsun flutters shyly", "*sniff*"},
            {"Moldsmal jiggles", "..."},
            {"Loox stares at you", "You feel judged"},
            {"Vegetoid wobbles", "It's showing off its veggies"}
        };
        
        int r = (int)(Math.random() * names.length);
        this.name = names[r];
        this.attackTexts = attacks[r];
    }
    
    public void display(PApplet app) {
        app.image(sprite, x, y, 60, 60);
    }
    
    public String getRandomAttackText() {
        return attackTexts[(int)(Math.random() * attackTexts.length)];
    }
}