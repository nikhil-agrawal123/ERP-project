package ui.StudentFrame;

import javax.swing.*;
import java.awt.*;

public class Notifications extends JFrame {

    private String rollNumber;
    private String username;

    // Use consistent colors from the dashboard
    private Color bgColor = new Color(45, 45, 45);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color textColor = Color.WHITE;

    public Notifications(String rollNumber, String username) {
        super("Notifications - " + username);
        this.rollNumber = rollNumber;
        this.username = username;

        // Set properties for the new frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only closes this window
        setSize(700, 500);
        setLocationRelativeTo(null); // Center
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(20, 20));

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(mainPanelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        // Title
        JLabel titleLabel = new JLabel("Your Notifications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Placeholder content
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Fee payment for Semester 3 is due on 2025-11-15.");
        listModel.addElement("Your grade for 'Advanced Algorithms' has been posted.");
        listModel.addElement("Library Book 'Intro to AI' is due tomorrow.");
        listModel.addElement("[MAINTENANCE] The ERP portal will be down on 2025-11-10 from 2 AM to 4 AM.");

        JList<String> notificationList = new JList<>(listModel);
        notificationList.setBackground(mainPanelColor);
        notificationList.setForeground(textColor);
        notificationList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        notificationList.setSelectionBackground(new Color(57, 174, 168));

        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setBorder(BorderFactory.createLineBorder(bgColor));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
}