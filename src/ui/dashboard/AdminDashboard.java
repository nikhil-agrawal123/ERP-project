package ui.dashboard;

import ui.landing.LandingFrame;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;
    private String adminID;

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public AdminDashboard(String adminID, String username) {
        super("Admin Dashboard - " + username);
        this.adminID = adminID;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());
        setLayout(new BorderLayout());
        JPanel sideMenuPanel = createSideMenuPanel(username);
        add(sideMenuPanel, BorderLayout.WEST);
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(mainPanelColor);

        createMainContent(username);

        add(mainContentPanel, BorderLayout.CENTER);
        cardLayout.show(mainContentPanel, "HOME");
    }

    private JPanel createSideMenuPanel(String username) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor);
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel menuTitle = new JLabel("Navigation");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        menuTitle.setForeground(textColor);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        JButton homeButton = createMenuButton("Dashboard");
        JButton addUserButton = createMenuButton("Add Users");
        JButton manageUsersButton = createMenuButton("Manage Users");
        JButton manageScoresButton = createMenuButton("Manage Scores");
        JButton maintenanceButton = createMenuButton("Maintenance"); // Corrected spelling
        JButton logoutButton = createMenuButton("Logout");

        homeButton.addActionListener(e -> cardLayout.show(mainContentPanel, "HOME"));

        // Other buttons now open new JFrames
        addUserButton.addActionListener(e -> {
            // Open the Add Users frame (placeholder)
            createPlaceholderFrame("Add Users");
        });

        manageUsersButton.addActionListener(e -> {
            // Open the Manage Users frame (placeholder)
            createPlaceholderFrame("Manage Users");
        });

        manageScoresButton.addActionListener(e -> {
            // Open the Manage Scores frame (placeholder)
            createPlaceholderFrame("Manage Scores");
        });

        maintenanceButton.addActionListener(e -> {
            // Open the Maintenance frame (placeholder)
            createPlaceholderFrame("Maintenance");
        });

        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logout Successful");
            new LandingFrame().setVisible(true);
            dispose();
        });


        // Add components to the panel
        panel.add(menuTitle);
        panel.add(homeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(addUserButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(manageUsersButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(manageScoresButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(maintenanceButton); // Added the maintenance button

        // Pushes the logout button to the bottom
        panel.add(Box.createVerticalGlue());
        panel.add(logoutButton);

        return panel;
    }
    private void createMainContent(String username) {

        // --- 1. Home Panel (The only panel) ---
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(mainPanelColor);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor); // Match the background

        // Welcome label at the top
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ID Label (replaces roll number)
        JLabel idLabel = new JLabel("ID: " + this.adminID);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setForeground(textColor);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Add labels to the title panel ---
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Vertical space
        titlePanel.add(idLabel);

        // Add title panel to the top of the home panel
        homePanel.add(titlePanel, BorderLayout.NORTH);

        // Add the single home panel to the card layout
        mainContentPanel.add(homePanel, "HOME");
    }


    /**
     * A helper method to create styled buttons for the side menu.
     *
     * @param text The text for the button.
     * @return A styled JButton.
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }
    private void createPlaceholderFrame(String title) {
        JFrame placeholderFrame = new JFrame(title);
        placeholderFrame.setSize(800, 600);
        placeholderFrame.setLocationRelativeTo(this);
        placeholderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(mainPanelColor);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        label.setForeground(textColor);

        panel.add(label);

        placeholderFrame.add(panel);
        placeholderFrame.setVisible(true);
    }
}