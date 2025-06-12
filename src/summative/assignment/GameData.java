/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class GameData {
    private static final String SAVE_FILE = "game_save.txt";

    public static void saveGame(int level, int health, int defeated) {
        try {
            FileWriter writer = new FileWriter(SAVE_FILE);
            writer.write(level + "\n");
            writer.write(health + "\n");
            writer.write(defeated + "\n");
            writer.close();
        } catch (Exception e) {
            System.out.println("Couldn't save game");
        }
    }

    public static int[] loadGame() {
        try {
            File file = new File(SAVE_FILE);
            if (!file.exists()) return new int[]{1, 100, 0};
            
            Scanner scanner = new Scanner(file);
            int level = scanner.nextInt();
            int health = scanner.nextInt();
            int defeated = scanner.nextInt();
            scanner.close();
            
            return new int[]{level, health, defeated};
        } catch (Exception e) {
            return new int[]{1, 100, 0};
        }
    }
}