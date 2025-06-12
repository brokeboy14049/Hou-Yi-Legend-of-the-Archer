/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import processing.core.PApplet;
import processing.core.PImage;

public class Boss extends Enemy {
    private String type;

    public Boss(float x, float y, PImage sprite, String type) {
        super(x, y, sprite);
        this.type = type;
    }

    @Override
    public void display(PApplet app) {
        super.display(app);
        app.fill(255);
        app.text(type, x + 20, y - 10);
    }

    public String getType() {
        return type;
    }
}

