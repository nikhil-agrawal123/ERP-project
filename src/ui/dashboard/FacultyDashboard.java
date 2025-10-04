// File: ui/dashboard/StudentDashboard.java

package ui.dashboard;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * A dashboard window for a student, featuring a side navigation menu
 * and a main content area that switches between different panels.
 */
public class FacultyDashboard extends JFrame {

    // --- Style Colors ---
    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;

    // --- Main Layout Components ---
    private JPanel mainContentPanel;
    private CardLayout cardLayout;


    public FacultyDashboard(String username) {
        super("Faculty Dashboard - " + username);

        // --- Basic Frame Setup ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800); // A more standard desktop aspect ratio
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        // Use BorderLayout for the main frame
        setLayout(new BorderLayout());

        // --- Create and add the side menu and main content panels ---
        JPanel sideMenuPanel = createSideMenuPanel();
        add(sideMenuPanel, BorderLayout.WEST);

        // The mainContentPanel will use CardLayout to switch between views
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(mainPanelColor);
        createContentCards(username); // Helper to create the different pages
        add(mainContentPanel, BorderLayout.CENTER);

        // Show the initial "home" card
        cardLayout.show(mainContentPanel, "HOME");
    }

    /**
     * Creates the side navigation panel with buttons.
     * @return The fully constructed side menu JPanel.
     */
    private JPanel createSideMenuPanel() {
        JPanel panel = new JPanel();
        // Use BoxLayout to stack components vertically
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor);
        panel.setPreferredSize(new Dimension(220, 0)); // Set preferred width
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Padding

        // Add a title label
        JLabel menuTitle = new JLabel("Navigation");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        menuTitle.setForeground(textColor);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0)); // Bottom margin

        // Create navigation buttons
        JButton homeButton = createMenuButton("Dashboard Home");
        JButton gradesButton = createMenuButton("My Sections"); //SET COURSE STRUCTURE HERE (MAINE LIKHA H HATAIYO MAT YE)
        JButton coursesButton = createMenuButton("Enter Scores");
        JButton receiptButton = createMenuButton("Stats");
        JButton TAButton = createMenuButton("Stats");
        JButton logoutButton = createMenuButton("Logout");

        // --- Add Action Listeners to buttons ---
        homeButton.addActionListener(e -> cardLayout.show(mainContentPanel, "HOME"));
        gradesButton.addActionListener(e -> cardLayout.show(mainContentPanel, "Sections"));
        coursesButton.addActionListener(e -> cardLayout.show(mainContentPanel, "Scores"));
        receiptButton.addActionListener(e -> cardLayout.show(mainContentPanel, "Stats"));
        TAButton.addActionListener(e -> cardLayout.show(mainContentPanel, "TA"));
        // Add logout logic here (e.g., open login frame and dispose this one)
        logoutButton.addActionListener(e -> {
            // Placeholder for logout
            JOptionPane.showMessageDialog(this, "Logout functionality to be added.");
            // Example: new LoginFrame().setVisible(true); dispose();
        });


        // Add components to the panel
        panel.add(menuTitle);
        panel.add(homeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(gradesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(coursesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(receiptButton);

        // Pushes the logout button to the bottom
        panel.add(Box.createVerticalGlue());
        panel.add(logoutButton);

        return panel;
    }

    /**
     * Creates the different "pages" (panels) and adds them to the main content panel.
     */
    private void createContentCards(String username) {
        // --- 1. Home Panel ---
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(mainPanelColor);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Add padding

// --- Create a new panel just for the title labels ---
        JPanel titlePanel = new JPanel();
// Use BoxLayout to stack the labels vertically
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor); // Match the background

// --- Create the labels ---
        JLabel welcomeLabel = new JLabel("Welcome, Faculty");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
// Align labels to the left for a cleaner look
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

// Suggestion: Use a slightly smaller font for the details
        JLabel nameLabel = new JLabel("Faculty name: " + username);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Changed font
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rollLabel = new JLabel("Faculty ID: 12345"); // Example roll no.
        rollLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Changed font
        rollLabel.setForeground(textColor);
        rollLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


// --- Add labels to the new title panel ---
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add a small vertical space
        titlePanel.add(nameLabel);
        titlePanel.add(rollLabel);

// --- Add the single titlePanel to the NORTH of homePanel ---
        homePanel.add(titlePanel, BorderLayout.NORTH);

// Panel to hold the stats boxes in the center
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        infoPanel.setBackground(mainPanelColor);

// Create the two stat boxes using the helper method
        JPanel coueseBox = createStatBox("No. of courses Offered", "2");

// Add boxes to the stats panel
        infoPanel.add(coueseBox);

// Add the stats panel to the main home panel
        homePanel.add(infoPanel, BorderLayout.WEST);
// --- 2. Grades Panel ---
// MODIFIED SECTION
        JPanel sectionPanel = new JPanel(new BorderLayout(10, 10)); // Use BorderLayout
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        sectionPanel.setBackground(mainPanelColor);

// Add a title label to the top
        JLabel gradesTitle = new JLabel("Your Courses");
        gradesTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gradesTitle.setForeground(textColor);
        sectionPanel.add(gradesTitle, BorderLayout.WEST);


        // --- 3. Courses Panel ---
        JPanel scoresPanel = new JPanel();
        scoresPanel.setBackground(mainPanelColor);
        scoresPanel.add(new JLabel("Select course to update scores") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 24));
            setForeground(textColor);
        }});

        JPanel coursesPanel = new JPanel();
        coursesPanel.setBackground(mainPanelColor);
        coursesPanel.add(new JLabel("Your Courses Will Be Displayed Here") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 24));
            setForeground(textColor);
        }});

        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(mainPanelColor);
        statsPanel.add(new JLabel("Select the course to see stats") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 24));
            setForeground(textColor);
        }});

        JPanel TAPanel = new JPanel();
        TAPanel.setBackground(mainPanelColor);
        TAPanel.add(new JLabel("Click on the course to see TA's Assigned") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 24));
            setForeground(textColor);
        }});


        // --- Add the panels to the CardLayout container ---
        // The String is a unique key to identify each card
        mainContentPanel.add(homePanel, "HOME");
        mainContentPanel.add(sectionPanel, "Sections");
        mainContentPanel.add(scoresPanel, "Scores");
        mainContentPanel.add(statsPanel, "Stats");
        mainContentPanel.add(TAPanel, "TA");
    }


    /**
     * A helper method to create styled buttons for the side menu.
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

        // Make buttons fill the width of the side menu
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }

    private JPanel createStatBox(String title, String value) {
        JPanel boxPanel = new JPanel(new BorderLayout());
        boxPanel.setBackground(sideMenuColor); // Use a contrasting background
        boxPanel.setPreferredSize(new Dimension(250, 200));
        // Combine a colored line border with internal padding
        boxPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonColor, 2, true), // Rounded corners
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Title label for the top of the box
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textColor);
        boxPanel.add(titleLabel, BorderLayout.NORTH);

        // Value label for the center of the box
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 52));
        valueLabel.setForeground(textColor);
        boxPanel.add(valueLabel, BorderLayout.CENTER);

        return boxPanel;
    }
}