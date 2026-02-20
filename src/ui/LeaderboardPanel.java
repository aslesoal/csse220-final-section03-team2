package ui;

import model.ScoreManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LeaderboardPanel {

    // Prevent instantiation (utility class)
    private LeaderboardPanel() {}

    private static void populatePanel(JPanel panel) {
        panel.removeAll();

        List<String[]> scores = ScoreManager.loadScores();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Leaderboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(10));

        int limit = Math.min(10, scores.size());
        for (int i = 0; i < limit; i++) {
            String[] s = scores.get(i);
            JLabel entry = new JLabel((i + 1) + ". " + s[0] + " — " + s[1]);
            entry.setFont(new Font("Arial", Font.PLAIN, 18));
            entry.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(entry);
        }

        if (limit == 0) {
            JLabel empty = new JLabel("No scores yet.");
            empty.setFont(new Font("Arial", Font.PLAIN, 18));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(empty);
        }

        panel.add(Box.createVerticalStrut(20));

        JButton resetButton = new JButton("Reset Leaderboard");
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        resetButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    panel,
                    "Reset leaderboard?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                ScoreManager.resetScores();
                populatePanel(panel);
                panel.revalidate();
                panel.repaint();
            }
        });

        panel.add(resetButton);
    }

    public static void showLeaderboard(Component parent) {
        JPanel panel = new JPanel();
        populatePanel(panel);

        JOptionPane.showMessageDialog(
                parent,
                panel,
                "Leaderboard",
                JOptionPane.PLAIN_MESSAGE
        );
    }
}
