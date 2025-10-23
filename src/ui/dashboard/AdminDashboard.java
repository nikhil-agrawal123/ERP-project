package ui.dashboard;

import ui.landing.LandingFrame;
import javax.swing.*;
import java.awt.*;

/**
 * An admin dashboard window.
 * Features a side navigation menu that opens new frames for each option
 * and a simple main content area.
 */
public class AdminDashboard extends JFrame {

    // --- Style Colors ---
    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;

    private String adminID;

    // --- Main Layout Components ---
    private JPanel mainContentPanel;
    private CardLayout cardLayout;


    public AdminDashboard(String adminID, String username) {
        super("Admin Dashboard - " + username);
        this.adminID = adminID;

        // --- Basic Frame Setup ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        // Use BorderLayout for the main frame
        setLayout(new BorderLayout());

        // --- Create and add the side menu and main content panels ---
        JPanel sideMenuPanel = createSideMenuPanel(username);
        add(sideMenuPanel, BorderLayout.WEST);

        // The mainContentPanel will use CardLayout to show the home screen
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(mainPanelColor);

        // Create the simplified main content
        createMainContent(username);

        add(mainContentPanel, BorderLayout.CENTER);

        // Show the initial "home" card
        cardLayout.show(mainContentPanel, "HOME");
    }

    /**
     * Creates the side navigation panel with buttons.
     *
     * @return The fully constructed side menu JPanel.
     */
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

        // Create navigation buttons
        JButton homeButton = createMenuButton("Dashboard");
        JButton addUserButton = createMenuButton("Add Users");
        JButton manageUsersButton = createMenuButton("Manage Users");
        JButton manageScoresButton = createMenuButton("Manage Scores");
        JButton maintenanceButton = createMenuButton("Maintenance"); // Corrected spelling
        JButton logoutButton = createMenuButton("Logout");

        // --- Add Action Listeners to buttons ---

        // Home button just shows the main dashboard panel
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

    /**
     * Creates the main content panel, which is just the blank home screen.
     */
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

    /**
     * A helper method to quickly create placeholder frames for new features.
     * @param title The title for the new frame.
     */
    private void createPlaceholderFrame(String title) {
        JFrame placeholderFrame = new JFrame(title);
        placeholderFrame.setSize(800, 600);
        placeholderFrame.setLocationRelativeTo(this); // Open near the main dashboard
        placeholderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(mainPanelColor);

        JLabel label = new JLabel(title + " functionality will be here.");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        label.setForeground(textColor);

        panel.add(label);

        placeholderFrame.add(panel);
        placeholderFrame.setVisible(true);
    }
}