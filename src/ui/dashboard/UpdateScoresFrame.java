package ui.dashboard;

import javax.swing.*;
import java.awt.*;

public class UpdateScoresFrame extends JFrame {

    private Color mainPanelColor = new Color(50, 50, 50);
    private Color textColor = Color.WHITE;

    public UpdateScoresFrame() {
        super("Update Scores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());
        JPanel panel = new JPanel();
        panel.setBackground(mainPanelColor);

        JLabel placeholderLabel = new JLabel("Score updating interface will be here.");
        placeholderLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        placeholderLabel.setForeground(textColor);

        panel.add(placeholderLabel);
        add(panel);
    }
}