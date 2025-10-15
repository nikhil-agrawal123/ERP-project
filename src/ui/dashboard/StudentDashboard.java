package ui.dashboard;

import org.json.JSONObject;
import ui.landing.LandingFrame;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.geom.RoundRectangle2D;
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


        centerContentPanel.add(statsContainer);
        JPanel rightSideContainer = new JPanel();
        rightSideContainer.setLayout(new BoxLayout(rightSideContainer, BoxLayout.Y_AXIS));
        rightSideContainer.setBackground(mainPanelColor);

        // Create the individual panels
        JPanel coursesListPanel = createCoursesPanel();
        JPanel appointmentsPanel = createAppointmentsPanel(); // The new panel

        // Add the panels to the right-side container
        rightSideContainer.add(coursesListPanel);
        rightSideContainer.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer for separation
        rightSideContainer.add(appointmentsPanel);
        rightSideContainer.add(Box.createVerticalGlue()); // Pushes panels to the top

        // Add the right-side container to the main grid layout
        centerContentPanel.add(rightSideContainer);

        homePanel.add(centerContentPanel, BorderLayout.CENTER);
// --- 2. Grades Panel ---




        JPanel gradesPanel = new JPanel(new BorderLayout(10, 10));
        gradesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gradesPanel.setBackground(mainPanelColor);

// Add a title label to the top
        JLabel gradesTitle = new JLabel("Your Academic Grades");
        gradesTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gradesTitle.setForeground(textColor);
        gradesPanel.add(gradesTitle, BorderLayout.NORTH);







        // --- 3. Courses Panel ---
        JPanel coursesPanel = new JPanel(new BorderLayout()); // Use BorderLayout
        coursesPanel.setBackground(mainPanelColor);
        coursesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

// Add a main title for the page
        JLabel pageTitle = new JLabel("My Registered Courses");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        pageTitle.setForeground(textColor);
        pageTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Bottom margin
        coursesPanel.add(pageTitle, BorderLayout.NORTH);

// Panel to hold the stack of course cards
        JPanel cardStackPanel = new JPanel();
        cardStackPanel.setLayout(new BoxLayout(cardStackPanel, BoxLayout.Y_AXIS));
        cardStackPanel.setBackground(mainPanelColor);

// --- Add your course cards here ---
        cardStackPanel.add(createCourseCard("CSE121", "Discrete Mathematics"));
        cardStackPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        cardStackPanel.add(createCourseCard("CSE201", "Advanced Programming"));
        cardStackPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        cardStackPanel.add(createCourseCard("CSE231", "Operating Systems"));
// Add more cards as needed...

// Crucial: Add the card stack to a Scroll Pane
        JScrollPane scrollPane = new JScrollPane(cardStackPanel);
        scrollPane.setBorder(null); // Remove the default border of the scroll pane
        scrollPane.getViewport().setBackground(mainPanelColor);
// Adjust scroll speed
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        coursesPanel.add(scrollPane, BorderLayout.CENTER);

// ... then add this coursesPanel to your main CardLayout
// mainContentPanel.add(coursesPanel, "COURSES");

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
        boxPanel.setPreferredSize(new Dimension(250, 200));
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
        valueLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 56));
        valueLabel.setForeground(textColor);
        boxPanel.add(valueLabel, BorderLayout.CENTER);

        return boxPanel;
    }

    private JPanel createCoursesPanel() {
        JPanel coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(mainPanelColor);
        coursesPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // --- Panel Title ---
        JLabel titleLabel = new JLabel("Registered Courses", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        coursesPanel.add(titleLabel);

        // --- Hardcoded course data ---
        List<String> courseNames = new ArrayList<>();
        courseNames.add("CS101 - Intro to Programming");
        courseNames.add("MA203 - Linear Algebra");
        courseNames.add("PHY105 - Classical Mechanics");
        courseNames.add("EE201 - Digital Circuits");

        // --- Display Courses as a Bulleted List ---
        if (courseNames.isEmpty()) {
            JLabel noCoursesLabel = new JLabel("No courses registered for this semester.");
            noCoursesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noCoursesLabel.setForeground(textColor);
            noCoursesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            coursesPanel.add(noCoursesLabel);
        } else {
            // Loop through the list and create a JLabel for each course
            for (String courseName : courseNames) {
                JLabel courseLabel = new JLabel("\u2022 " + courseName); // \u2022 is the bullet character
                courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                courseLabel.setForeground(textColor);
                courseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                courseLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // Add spacing below each item
                coursesPanel.add(courseLabel);
            }
        }

        coursesPanel.add(Box.createVerticalGlue());
        return coursesPanel;
    }



    private JPanel createAppointmentsPanel() {
        JPanel appointmentsPanel = new JPanel();
        appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));
        appointmentsPanel.setBackground(mainPanelColor);
        appointmentsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Faculty Appointments", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        appointmentsPanel.add(titleLabel);

        // --- Hardcoded appointment data ---
        List<String> appointmentDetails = new ArrayList<>();
        appointmentDetails.add("Dr. Alan Turing - 2025-10-20 at 11:00 AM");
        appointmentDetails.add("Prof. Ada Lovelace - 2025-10-22 at 02:30 PM");
        appointmentDetails.add("Dr. Grace Hopper - 2025-10-25 at 09:00 AM");

        if (appointmentDetails.isEmpty()) {
            JLabel noAppointmentsLabel = new JLabel("No appointments scheduled.");
            noAppointmentsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noAppointmentsLabel.setForeground(textColor);
            noAppointmentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            appointmentsPanel.add(noAppointmentsLabel);
        } else {
            // Loop through the list and create a JLabel for each appointment
            for (String appointment : appointmentDetails) {
                JLabel appointmentLabel = new JLabel("\u2022 " + appointment);
                appointmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                appointmentLabel.setForeground(textColor);
                appointmentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                appointmentLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // Add spacing
                appointmentsPanel.add(appointmentLabel);
            }
        }

        appointmentsPanel.add(Box.createVerticalGlue());
        return appointmentsPanel;
    }
    private JPanel createCourseCard(String code, String name) {
        // --- Main Card Panel ---
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setBackground(new Color(65, 65, 65)); // A slightly lighter gray for the card
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)), // Outline
                BorderFactory.createEmptyBorder(15, 20, 15, 20)      // Inner padding
        ));
        // Set a max height to prevent cards from stretching vertically
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));


        // --- 1. Top Section: Course Code and Name ---
        JLabel titleLabel = new JLabel("Code - " + code + "   •   Course - " + name);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(buttonColor); // Using your theme color for highlight
        cardPanel.add(titleLabel, BorderLayout.NORTH);


        // --- 2. Center Section: Details (Class Type, Credits, etc.) ---
        JPanel detailsPanel = new JPanel(new GridLayout(1, 3, 20, 0)); // 1 row, 3 columns
        detailsPanel.setOpaque(false); // Make it transparent to show card background

        // Helper to create a detail column
        JPanel classTypePanel = createDetailColumn("Class Type", "Lecture");
        JPanel creditsPanel = createDetailColumn("Credits", "4");
        JPanel regTypePanel = createDetailColumn("Registration Type", "Mandatory (Core)");

        detailsPanel.add(classTypePanel);
        detailsPanel.add(creditsPanel);
        detailsPanel.add(regTypePanel);
        cardPanel.add(detailsPanel, BorderLayout.CENTER);


        // --- 3. Bottom Section: Action Buttons ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        buttonsPanel.add(createStyledButton("Time Table"));
        buttonsPanel.add(createStyledButton("Attendance"));
        buttonsPanel.add(createStyledButton("Assignment"));
        buttonsPanel.add(createStyledButton("Lesson Plan"));
        buttonsPanel.add(createStyledButton("Course Feedback"));
        cardPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return cardPanel;
    }

    /**
     * Helper to create a vertical column for a single detail item.
     */
    private JPanel createDetailColumn(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.LIGHT_GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(textColor);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(valueLabel);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(textColor);
        button.setBackground(new Color(80, 80, 80));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }


}