package ui.dashboard;

import ui.landing.LandingFrame;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60); // Kept for function box color
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

        // Layout is now just BorderLayout, no side panel
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(mainPanelColor);

        createMainContent(username);

        // Add main content panel directly to the CENTER
        add(mainContentPanel, BorderLayout.CENTER);
        cardLayout.show(mainContentPanel, "HOME");
    }

    /**
     * Creates the main content area, split into profile, functions, and right-side buttons.
     * The Title is at the top of the entire panel.
     */
    private void createMainContent(String username) {

        // --- 1. Home Panel (The only panel) ---
        // This panel now holds the full-height right panel (EAST)
        // and a new container (CENTER) for everything else.
        JPanel homePanel = new JPanel(new BorderLayout(0, 0)); // No gaps
        homePanel.setBackground(mainPanelColor);

        // --- EAST: New Right Side Panel ---
        // We create and add this first at the main level to make it full-height.
        JPanel rightSidePanel = createRightSidePanel();
        homePanel.add(rightSidePanel, BorderLayout.EAST);

        // --- New "Center and Left" Container ---
        // This panel will hold the title, profile, and function boxes.
        // It will be placed in the main homePanel's CENTER.
        JPanel centerAndLeftContainer = new JPanel(new BorderLayout(0, 0));
        centerAndLeftContainer.setBackground(mainPanelColor);

        // --- TOP: Title Panel ---
        // The title goes at the NORTH of this new container.
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor); // Match the background
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Padding

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(welcomeLabel);

        // Add title to the new container
        centerAndLeftContainer.add(titlePanel, BorderLayout.NORTH);

        // --- WEST: Admin Profile Panel ---
        // The profile panel goes at the WEST of this new container,
        // which places it *below* the title.
        JPanel profilePanel = createAdminProfilePanel(username);
        centerAndLeftContainer.add(profilePanel, BorderLayout.WEST);

        // --- CENTER: Function Buttons Panel (The eye-catcher) ---
        // The function panel goes in the CENTER of the new container.
        JPanel functionPanel = createFunctionPanel();
        centerAndLeftContainer.add(functionPanel, BorderLayout.CENTER);

        // --- Add the container for (Title + Profile + Functions) to the main panel ---
        homePanel.add(centerAndLeftContainer, BorderLayout.CENTER);

        // Add the single home panel to the card layout
        mainContentPanel.add(homePanel, "HOME");
    }

    /**
     * Creates the left-side panel for the admin's profile.
     */
    private JPanel createAdminProfilePanel(String username) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(mainPanelColor);
        panel.setPreferredSize(new Dimension(300, 0)); // Set preferred width

        // Create a compound border:
        // 1. A MatteBorder on the right to act as a separator line
        // 2. An EmptyBorder for internal padding
        Border lineBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY);
        Border paddingBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        panel.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        // --- Profile Photo Placeholder ---
        JPanel photoPanel = new JPanel();
        photoPanel.setBackground(Color.YELLOW); // As requested
        Dimension photoSize = new Dimension(150, 150);
        photoPanel.setPreferredSize(photoSize);
        photoPanel.setMaximumSize(photoSize);
        photoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Details ---
        JLabel nameLabel = new JLabel("Name: " + username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("ID: " + this.adminID);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setForeground(textColor);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Add components to the panel ---
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Top spacing
        panel.add(photoPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Space below photo
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(idLabel);
        panel.add(Box.createVerticalGlue()); // Pushes everything to the top

        return panel;
    }

    /**
     * Creates the center panel for the main function buttons.
     */
    private JPanel createFunctionPanel() {
        // Use FlowLayout to place boxes from left-to-right and leave empty space
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        // --- Create Function Boxes (Buttons) ---
        JButton addUserBox = createFunctionBox("Add Users");
        addUserBox.addActionListener(e -> createPlaceholderFrame("Add Users"));

        JButton manageUsersBox = createFunctionBox("Manage Users");
        manageUsersBox.addActionListener(e -> createPlaceholderFrame("Manage Users"));

        JButton manageScoresBox = createFunctionBox("Manage Scores");
        manageScoresBox.addActionListener(e -> createPlaceholderFrame("Manage Scores"));

        JButton maintenanceBox = createFunctionBox("Maintenance");
        maintenanceBox.addActionListener(e -> createPlaceholderFrame("Maintenance"));

        JButton logoutBox = createFunctionBox("Logout");
        logoutBox.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logout Successful");
            new LandingFrame().setVisible(true);
            dispose();
        });


        // --- Add boxes to the panel ---
        panel.add(addUserBox);
        panel.add(manageUsersBox);
        panel.add(manageScoresBox);
        panel.add(maintenanceBox);
        panel.add(logoutBox); // Added logout button here

        return panel;
    }

    /**
     * Creates the new right-side panel for extra buttons.
     */
    private JPanel createRightSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(mainPanelColor); // Match the background
        panel.setPreferredSize(new Dimension(220, 0)); // Give it a fixed width

        // Separator line on the left, padding on the inside
        Border lineBorder = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY);
        // Top padding is 0 because we use a precise rigid area for alignment
        Border paddingBorder = BorderFactory.createEmptyBorder(0, 20, 20, 20);
        panel.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        // --- Add Buttons ---
        JButton b1 = createStyledButton("Button 1");
        JButton b2 = createStyledButton("Button 2");
        JButton b3 = createStyledButton("Button 3");

        // Add action listeners (placeholder)
        b1.addActionListener(e -> System.out.println("Button 1 pressed"));
        b2.addActionListener(e -> System.out.println("Button 2 pressed"));
        b3.addActionListener(e -> System.out.println("Button 3 pressed"));

        // --- MODIFIED LINE ---
        // This spacer pushes the buttons down to align with the center function boxes.
        // (Approx Title Panel Height [85-90px] + Function Panel Top Padding [20px])
        panel.add(Box.createRigidArea(new Dimension(0, 105))); // Was 20

        panel.add(b1);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(b2);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(b3);
        panel.add(Box.createVerticalGlue()); // Push buttons to the top

        return panel;
    }


    /**
     * Helper method to create a single styled square button for the function panel.
     */
    private JButton createFunctionBox(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 180)); // Square size
        button.setBackground(sideMenuColor); // Use side menu color for contrast
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(buttonColor, 1)); // Theme color border

        return button;
    }

    /**
     * Helper method to create a styled button for the right-side panel.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Smaller font
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Set a fixed size to control width and height
        // *** CHANGED HEIGHT FROM 40 to 50 ***
        Dimension buttonSize = new Dimension(200, 60);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);

        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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