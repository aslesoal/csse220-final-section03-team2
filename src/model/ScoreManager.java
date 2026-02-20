package model;

import java.io.*;
import java.util.*;

/*
 * Saves the score after the game so that the leaderboard works
 */
public class ScoreManager {

    private static final String FILE = "scores.txt";

    // Prevent instantiation 
    private ScoreManager() {}

    //Puts scores into scores.txt
    public static void saveScore(String name, int score) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE, true))) {
            out.println(name + "," + score);
        } catch (IOException e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }

    //Loads the scores
    public static List<String[]> loadScores() {
        List<String[]> list = new ArrayList<>();

        try (Scanner sc = new Scanner(new File(FILE))) {
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(",");
                if (parts.length == 2) list.add(parts);
            }
        } catch (FileNotFoundException ignored) {}

        list.sort((a, b) -> Integer.compare(
                Integer.parseInt(b[1]),
                Integer.parseInt(a[1])
        ));

        return list;
    }

    //Empty the leaderboard
    public static void resetScores() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE))) {
            // overwrite with empty
        } catch (IOException e) {
            System.err.println("Error resetting scores: " + e.getMessage());
        }
    }

    //Sorts the leaderboard
    public static boolean isNewHighScore(int score) {
        List<String[]> scores = loadScores();
        if (scores.isEmpty()) return true;
        int best = Integer.parseInt(scores.get(0)[1]);
        return score > best;
    }
}
