package ui.dashboard;

import javax.swing.*;
import ui.landing.LandingFrame;
import java.awt.*;
import databaseConfig.Connector;
import java.sql.*;

public class FacultyDashboard extends JFrame {
    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    private int numCourses = 0;
    private String facultyID = "N/A"; // Initialize to a default

    public FacultyDashboard(String username) {
        super("Faculty Dashboard - " + username);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        setLayout(new BorderLayout());

        // --- Create and add the side menu and main content panels ---
        JPanel sideMenuPanel = createSideMenuPanel(username);
        add(sideMenuPanel, BorderLayout.WEST);

        // The mainContentPanel will use CardLayout to switch between views
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(mainPanelColor);

        // This method now contains the database logic
        createContentCards(username);

        add(mainContentPanel, BorderLayout.CENTER);

        // Show the initial "home" card
        cardLayout.show(mainContentPanel, "HOME");
    }

    /**
     * Creates the side navigation panel with buttons.
     *
     * @param username The username of the logged-in faculty.
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
        JButton homeButton = createMenuButton("Dashboard Home");
        JButton courseButton = createMenuButton("My Courses");
        JButton scoresButton = createMenuButton("Enter Scores");
        JButton statsButton = createMenuButton("Stats");
        JButton TAButton = createMenuButton("TA Info");
        JButton logoutButton = createMenuButton("Logout");

        // --- Add Action Listeners to buttons ---
        homeButton.addActionListener(e -> cardLayout.show(mainContentPanel, "HOME"));
        courseButton.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSES"));
        scoresButton.addActionListener(e -> {
            CourseManagementFrame courseFrame = new CourseManagementFrame(username);
            courseFrame.setVisible(true);
        });
        statsButton.addActionListener(e -> cardLayout.show(mainContentPanel, "Stats"));
        TAButton.addActionListener(e -> cardLayout.show(mainContentPanel, "TA"));
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logout successful");
            LandingFrame landingFrame = new LandingFrame();
            landingFrame.setVisible(true);
            dispose();
        });

        // Add components to the panel
        panel.add(menuTitle);
        panel.add(homeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(courseButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(scoresButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(statsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(TAButton);

        panel.add(Box.createVerticalGlue()); // Pushes logout button to the bottom
        panel.add(logoutButton);

        return panel;
    }

    /**
     * Creates all the content panels for the CardLayout.
     * This method also performs the initial database query to populate the dashboard.
     *
     * @param username The user_id of the faculty member.
     */
    private void createContentCards(String username) {

        String instructorQuery = "SELECT instructor_id FROM users.instructors WHERE user_id = ?";
        String coursesQuery = "SELECT COUNT(*) AS course_count FROM users.sections WHERE instructor_id = ?";

        Connector connector = new Connector();

        try (Connection conn = connector.connect()) {

            // Step 1: Get the facultyID using the username (user_id)
            try (PreparedStatement psInstructors = conn.prepareStatement(instructorQuery)) {
                psInstructors.setString(1, username);
                try (ResultSet rsInstructors = psInstructors.executeQuery()) {
                    if (rsInstructors.next()) {
                        facultyID = rsInstructors.getString("instructor_id");
                    } else {
                        // Handle case where no faculty ID is found for this user
                        JOptionPane.showMessageDialog(this,
                                "Could not find faculty details for user: " + username,
                                "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            // Step 2: Use the retrieved facultyID to get the course count
            if (!facultyID.equals("N/A")) {
                try (PreparedStatement psCourses = conn.prepareStatement(coursesQuery)) {
                    // Use the facultyID, not the username
                    psCourses.setString(1, facultyID);
                    try (ResultSet rsCourses = psCourses.executeQuery()) {
                        if (rsCourses.next()) {
                            // Get the count from the alias
                            numCourses = rsCourses.getInt("course_count");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to the database. Please check your connection.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // --- 1. Home Panel ---
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(mainPanelColor);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor);

        JLabel welcomeLabel = new JLabel("Welcome, Faculty");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("Faculty name: " + username);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rollLabel = new JLabel("Faculty ID: " + facultyID);
        rollLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rollLabel.setForeground(textColor);
        rollLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(nameLabel);
        titlePanel.add(rollLabel);
        homePanel.add(titlePanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        infoPanel.setBackground(mainPanelColor);

        JPanel courseBox = createStatBox("No. of courses Offered", String.valueOf(numCourses));
        infoPanel.add(courseBox);
        homePanel.add(infoPanel, BorderLayout.WEST);

        // --- 2. Courses Panel ---
        JPanel CoursePanel = new JPanel(new BorderLayout(10, 10));
        CoursePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        CoursePanel.setBackground(mainPanelColor);

        JLabel gradesTitle = new JLabel("Your Courses");
        gradesTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gradesTitle.setForeground(textColor);
        CoursePanel.add(gradesTitle, BorderLayout.NORTH);

        // --- 3. Stats Panel ---
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(mainPanelColor);
        statsPanel.add(new JLabel("Select the course to see stats") {
            {
                setFont(new Font("Segoe UI", Font.PLAIN, 24));
                setForeground(textColor);
            }
        });

        // --- 4. TA Panel ---
        JPanel TAPanel = new JPanel();
        TAPanel.setBackground(mainPanelColor);
        TAPanel.add(new JLabel("Click on the course to see TA's Assigned") {
            {
                setFont(new Font("Segoe UI", Font.PLAIN, 24));
                setForeground(textColor);
            }
        });

        // --- Add all cards to the main panel ---
        mainContentPanel.add(homePanel, "HOME");
        mainContentPanel.add(CoursePanel, "COURSES");
        mainContentPanel.add(statsPanel, "Stats");
        mainContentPanel.add(TAPanel, "TA");
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
     * A helper method to create a styled statistics box.
     *
     * @param title The text for the top of the box.
     * @param value The text for the center of the box.
     * @return A styled JPanel.
     */
    private JPanel createStatBox(String title, String value) {
        JPanel boxPanel = new JPanel(new BorderLayout());
        boxPanel.setBackground(sideMenuColor);
        boxPanel.setPreferredSize(new Dimension(250, 200));
        boxPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonColor, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textColor);
        boxPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 52));
        valueLabel.setForeground(textColor);
        boxPanel.add(valueLabel, BorderLayout.CENTER);

        return boxPanel;
    }
}