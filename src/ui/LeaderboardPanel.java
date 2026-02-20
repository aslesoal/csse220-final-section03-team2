package ui;

import model.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Utility class for displaying the leaderboard in a modal dialog.
 * Loads scores, formats them into a panel, and provides a reset option.
 */
public class LeaderboardPanel {

    /** Prevent instantiation (utility class). */
    private LeaderboardPanel() {}

    /**
     * Populates the given panel with leaderboard entries and a reset button.
     *
     * @param panel the panel to populate
     */
    private static void populatePanel(JPanel panel) {
        panel.removeAll();

        // Load saved scores (each entry is [name, score])
        List<String[]> scores = ScoreManager.loadScores();

        // Vertical layout for title, entries, and reset button
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Title label
        JLabel title = new JLabel("Leaderboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(10));

        // Show up to the top 10 scores
        int limit = Math.min(10, scores.size());
        for (int i = 0; i < limit; i++) {
            String[] s = scores.get(i);

            // Format: "1. Alice — 1200"
            JLabel entry = new JLabel((i + 1) + ". " + s[0] + " — " + s[1]);
            entry.setFont(new Font("Arial", Font.PLAIN, 18));
            entry.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(entry);
        }

        // If no scores exist, show a placeholder message
        if (limit == 0) {
            JLabel empty = new JLabel("No scores yet.");
            empty.setFont(new Font("Arial", Font.PLAIN, 18));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(empty);
        }

        panel.add(Box.createVerticalStrut(20));

        // Button to clear all leaderboard entries
        JButton resetButton = new JButton("Reset Leaderboard");
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        resetButton.addActionListener(e -> {
            // Confirm before wiping scores
            int confirm = JOptionPane.showConfirmDialog(
                    panel,
                    "Reset leaderboard?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Clear scores and refresh the panel UI
                ScoreManager.resetScores();
                populatePanel(panel);
                panel.revalidate();
                panel.repaint();
            }
        });

        panel.add(resetButton);
    }

    /**
     * Displays the leaderboard in a modal dialog.
     *
     * @param parent the parent component for dialog positioning
     */
    public static void showLeaderboard(Component parent) {
        JPanel panel = new JPanel();

        // Build the leaderboard UI
        populatePanel(panel);

        // Show it inside a JOptionPane dialog
        JOptionPane.showMessageDialog(
                parent,
                panel,
                "Leaderboard",
                JOptionPane.PLAIN_MESSAGE
        );
    }
}
