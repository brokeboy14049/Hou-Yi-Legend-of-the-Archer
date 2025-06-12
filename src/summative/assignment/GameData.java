/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package summative.assignment;

import java.io.*;

public class GameData {
    private static final String FILE_NAME = "progress.txt";

    public static int loadProgress() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            int progress = Integer.parseInt(reader.readLine());
            reader.close();
            return progress;
        } catch (Exception e) {
            return 1; // default level
        }
    }

    public static void saveProgress(int level) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
            writer.write(String.valueOf(level));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
