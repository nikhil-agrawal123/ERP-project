package ui.dashboard;

import org.json.JSONObject;
import ui.landing.LandingFrame;
import javax.swing.*;
import java.sql.*;
import databaseConfig.Connector;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A dashboard window for a student, featuring a side navigation menu
 * and a main content area that switches between different panels.
 */
public class StudentDashboard extends JFrame {

    // --- Style Colors ---
    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;

    private double cg = 0;
    private int credits = 0;
    private int rollNumber = 0;

    // --- Main Layout Components ---
    private JPanel mainContentPanel;
    private CardLayout cardLayout;


    public StudentDashboard(String username) {
        super("Student Dashboard - " + username);

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
     *
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
        JButton gradesButton = createMenuButton("My Grades");
        JButton coursesButton = createMenuButton("My Courses");
        JButton receiptButton = createMenuButton("Fee Receipts");
        JButton logoutButton = createMenuButton("Logout");

        // --- Add Action Listeners to buttons ---
        homeButton.addActionListener(e -> cardLayout.show(mainContentPanel, "HOME"));
        gradesButton.addActionListener(e -> cardLayout.show(mainContentPanel, "GRADES"));
        coursesButton.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSES"));
        receiptButton.addActionListener(e -> cardLayout.show(mainContentPanel, "RECEIPTS"));

        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logout Successful");
            LandingFrame landingFrame = new LandingFrame();
            landingFrame.setVisible(true);
            dispose();
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
        Connector connector = new Connector();
        String sql = "SELECT currentCGPA,currentCredits,studentRollNumber FROM users.student WHERE studentName = ?";
        try (Connection connection = connector.connect()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Double currentCGPA = rs.getDouble("currentCGPA");
                int currentCredits = rs.getInt("currentCredits");
                int rollNu = rs.getInt("studentRollNumber");
                cg = currentCGPA;
                credits = currentCredits;
                rollNumber = rollNu;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }

        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(mainPanelColor);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Add padding

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor); // Match the background

        // Welcome label at the top
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Suggestion: Use a slightly smaller font for the details
        JLabel nameLabel = new JLabel("Student name: " + username);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Changed font
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rollLabel = new JLabel("Student Roll no.: " + rollNumber); // Example roll no.
        rollLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Changed font
        rollLabel.setForeground(textColor);
        rollLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Add labels to the new title panel ---
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add a small vertical space
        titlePanel.add(nameLabel);
        titlePanel.add(rollLabel);

        homePanel.add(titlePanel, BorderLayout.NORTH);


        // --- Center Content Section ---
        JPanel centerContentPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        centerContentPanel.setBackground(mainPanelColor);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.X_AXIS));
        statsPanel.setBackground(mainPanelColor);

        JPanel cgpaBox = createStatBox("Current CGPA", "" + cg);
        JPanel creditsBox = createStatBox("Credits Earned", "" + credits);

        statsPanel.add(Box.createHorizontalGlue());
        statsPanel.add(cgpaBox);
        statsPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        statsPanel.add(creditsBox);
        statsPanel.add(Box.createHorizontalGlue());

        // --- MODIFIED SECTION: New container to align the stats panel to the top ---
        JPanel statsContainer = new JPanel(new BorderLayout());
        statsContainer.setBackground(mainPanelColor);
        statsContainer.add(statsPanel, BorderLayout.NORTH);
        // --- END OF MODIFIED SECTION ---

        JPanel coursesListPanel = createCoursesPanel(rollNumber);

        // Add the new container (which holds the stats panel) to the grid
        centerContentPanel.add(statsContainer);
        centerContentPanel.add(coursesListPanel);

        homePanel.add(centerContentPanel, BorderLayout.CENTER);
// --- 2. Grades Panel ---
// MODIFIED SECTION
        JPanel gradesPanel = new JPanel(new BorderLayout(10, 10)); // Use BorderLayout
        gradesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        gradesPanel.setBackground(mainPanelColor);

// Add a title label to the top
        JLabel gradesTitle = new JLabel("Your Academic Grades");
        gradesTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gradesTitle.setForeground(textColor);
        gradesPanel.add(gradesTitle, BorderLayout.NORTH);


        // --- 3. Courses Panel ---
        JPanel coursesPanel = new JPanel();
        coursesPanel.setBackground(mainPanelColor);
        coursesPanel.add(new JLabel("Your Courses Will Be Displayed Here") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 24));
            setForeground(textColor);
        }});

        JPanel receiptPanel = new JPanel();
        receiptPanel.setBackground(mainPanelColor);
        receiptPanel.add(new JLabel("Your Courses Will Be Displayed Here") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 24));
            setForeground(textColor);
        }});

        // --- Add the panels to the CardLayout container ---
        // The String is a unique key to identify each card
        mainContentPanel.add(homePanel, "HOME");
        mainContentPanel.add(gradesPanel, "GRADES");
        mainContentPanel.add(coursesPanel, "COURSES");
        mainContentPanel.add(receiptPanel, "RECEIPTS");
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

        // Make buttons fill the width of the side menu
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }

    private JPanel createStatBox(String title, String value) {
        JPanel boxPanel = new JPanel(new BorderLayout());
        boxPanel.setBackground(sideMenuColor); // Use a contrasting background
        boxPanel.setPreferredSize(new Dimension(200, 200));
        boxPanel.setMaximumSize(boxPanel.getPreferredSize());
        // Combine a colored line border with internal padding
        boxPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonColor, 2, true), // Rounded corners
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Title label for the top of the box
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textColor);
        boxPanel.add(titleLabel, BorderLayout.NORTH);

        // Value label for the center of the box
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 52));
        valueLabel.setForeground(textColor);
        boxPanel.add(valueLabel, BorderLayout.CENTER);

        return boxPanel;
    }

// No new imports needed for this method specifically

    private JPanel createCoursesPanel(int studentRollNumber) {
        JPanel coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(sideMenuColor);
        coursesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonColor, 2, true),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel titleLabel = new JLabel("Registered Courses", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        coursesPanel.add(titleLabel);

        String sql = "SELECT registerCourses FROM users.student WHERE studentRollNumber = ?";
        List<String> courseNames = new ArrayList<>();

        try (Connection conn = new Connector().connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentRollNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String jsonString = rs.getString("registerCourses");
                if (jsonString != null && !jsonString.trim().isEmpty() && !jsonString.equals("{}")) {
                    JSONObject registeredCourses = new JSONObject(jsonString);
                    Iterator<String> keys = registeredCourses.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject courseObject = registeredCourses.getJSONObject(key);
                        courseNames.add(courseObject.getString("course_name"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // ... error handling code remains the same
        }

        if (courseNames.isEmpty()) {
            JLabel noCoursesLabel = new JLabel("No courses registered for this semester.");
            noCoursesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noCoursesLabel.setForeground(textColor);
            noCoursesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            coursesPanel.add(noCoursesLabel);
        } else {
            for (String courseName : courseNames) {
                // ✅ CHANGED FROM JLABEL TO JBUTTON
                JButton courseButton = new JButton("• " + courseName);
                styleCourseButton(courseButton); // Apply custom styling

                // ✅ ADD ACTION LISTENER TO HANDLE CLICKS
                courseButton.addActionListener(e -> {
                    // This will create and switch to the new panel
                    showCourseDetailPanel(courseName);
                });

                coursesPanel.add(courseButton);
                coursesPanel.add(Box.createRigidArea(new Dimension(0, 5))); // A little space between buttons
            }
        }

        coursesPanel.add(Box.createVerticalGlue());
        return coursesPanel;
    }
    // This helper method styles our course buttons to look like links
    private void styleCourseButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setForeground(textColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Make the button look like a label
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setHorizontalAlignment(SwingConstants.LEFT); // Align text to the left
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add a hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(buttonColor); // Change color on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(textColor); // Change back
            }
        });
    }

    // This method creates the new panel for a specific course
    private JPanel createCourseDetailPanel(String courseName) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // --- Top section with Title and Back Button ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false); // Make it transparent

        // Back button to return to the dashboard
        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setForeground(textColor);
        styleCourseButton(backButton); // Use the same styling for consistency
        backButton.addActionListener(e -> cardLayout.show(mainContentPanel, "HOME"));
        topPanel.add(backButton, BorderLayout.WEST);

        // Course Title
        JLabel titleLabel = new JLabel(courseName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textColor);
        topPanel.add(titleLabel, BorderLayout.NORTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // --- Main Content Area ---
        // You can add database queries here to fetch and display more details
        JTextArea courseDetailsArea = new JTextArea("Details for " + courseName + " would go here.\n\n" +
                "You could display information like:\n" +
                "- Instructor Name\n" +
                "- Course Code\n" +
                "- Credits\n");
        courseDetailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        courseDetailsArea.setForeground(textColor);
        courseDetailsArea.setBackground(sideMenuColor); // Use a slightly different color
        courseDetailsArea.setEditable(false);
        courseDetailsArea.setLineWrap(true);
        courseDetailsArea.setWrapStyleWord(true);
        courseDetailsArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(new JScrollPane(courseDetailsArea), BorderLayout.CENTER);

        return panel;
    }

    // This method handles adding the new panel and switching the view
    private void showCourseDetailPanel(String courseName) {
        String panelId = "COURSE_" + courseName; // Create a unique ID for the panel
        JPanel coursePanel = createCourseDetailPanel(courseName);
        mainContentPanel.add(coursePanel, panelId);
        cardLayout.show(mainContentPanel, panelId);
    }

}