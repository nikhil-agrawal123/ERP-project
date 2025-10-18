// File: ui/dashboard/UpdateScoresFrame.java

package ui.dashboard;

import javax.swing.*;
import java.awt.*;

/**
 * A new frame dedicated to updating student scores for a course.
 */
public class UpdateScoresFrame extends JFrame {

    // --- Style Colors ---
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color textColor = Color.WHITE;

    public UpdateScoresFrame() {
        super("Update Scores");

        // --- Basic Frame Setup ---
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center on screen
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        // --- Content ---
        JPanel panel = new JPanel();
        panel.setBackground(mainPanelColor);

        JLabel placeholderLabel = new JLabel("Score updating interface will be here.");
        placeholderLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        placeholderLabel.setForeground(textColor);

        panel.add(placeholderLabel);
        add(panel);
    }
}