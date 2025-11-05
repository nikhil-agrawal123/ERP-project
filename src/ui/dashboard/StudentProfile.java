package ui.dashboard;

import javax.swing.*;
import java.awt.*;

public class StudentProfile extends JFrame {

    private String rollNumber;
    private String username;

    // Use consistent colors from the dashboard
    private Color bgColor = new Color(45, 45, 45);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color textColor = Color.WHITE;

    public StudentProfile(String rollNumber, String username) {
        super("Student Profile - " + username);
        this.rollNumber = rollNumber;
        this.username = username;

        // Set properties for the new frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only closes this window
        setSize(800, 600);
        setLocationRelativeTo(null); // Center
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(20, 20));

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(mainPanelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        // Title
        JLabel titleLabel = new JLabel("Manage Account Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Placeholder content
        JTextArea profileDetails = new JTextArea();
        profileDetails.setBackground(mainPanelColor);
        profileDetails.setForeground(textColor);
        profileDetails.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        profileDetails.setEditable(false);
        profileDetails.setText(
                "Roll Number: \t" + rollNumber + "\n\n" +
                        "Username: \t" + username + "\n\n" +
                        "TODO:\n" +
                        "- Add fields to change password\n" +
                        "- Add fields to update contact information\n" +
                        "- etc."
        );
        profileDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(new JScrollPane(profileDetails), BorderLayout.CENTER);
    }
}