package ui.dashboard;

import ui.landing.LandingFrame;
import javax.swing.*;
import java.sql.*;
import databaseConfig.Connector;
import java.awt.*;

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
        try (Connection connection = connector.connect()){
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                Double currentCGPA = rs.getDouble("currentCGPA");
                int currentCredits = rs.getInt("currentCredits");
                int rollNu = rs.getInt("studentRollNumber");
                cg = currentCGPA;
                credits = currentCredits;
                rollNumber = rollNu;
            }

        }catch (SQLException e){
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

        // Panel to hold the stats boxes in the center
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        statsPanel.setBackground(mainPanelColor);

        // Create the two stat boxes using the helper method
        JPanel cgpaBox = createStatBox("Current CGPA", ""+cg);
        JPanel creditsBox = createStatBox("Credits Earned", ""+credits);

        // Add boxes to the stats panel
        statsPanel.add(cgpaBox);
        statsPanel.add(creditsBox);

        // Add the stats panel to the main home panel
        homePanel.add(statsPanel, BorderLayout.CENTER);
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
}