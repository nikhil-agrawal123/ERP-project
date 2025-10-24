package ui.dashboard;

import javax.swing.*;
import ui.landing.LandingFrame;
import java.awt.*;
import databaseConfig.Connector;
import java.sql.*;

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
    private int numCourses = 0;
    private String facultyID = "";


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
        JButton scoresButton = createMenuButton("Enter Scores");
        JButton receiptButton = createMenuButton("Stats");
        JButton TAButton = createMenuButton("Stats");
        JButton logoutButton = createMenuButton("Logout");

        // --- Add Action Listeners to buttons ---
        homeButton.addActionListener(e -> cardLayout.show(mainContentPanel, "HOME"));
        gradesButton.addActionListener(e -> cardLayout.show(mainContentPanel, "Sections"));
        scoresButton.addActionListener(e -> {
            // 1. Create an instance of the new frame
            CourseManagementFrame courseFrame = new CourseManagementFrame(facultyID);

            // 2. Make that new frame visible
            courseFrame.setVisible(true);
        });        receiptButton.addActionListener(e -> cardLayout.show(mainContentPanel, "Stats"));
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
        panel.add(gradesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
        panel.add(scoresButton);
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
        String newSQl = "SELECT * FROM users.sections WHERE instructor_id = ?";
        String sql = "SELECT * FROM users.instructors WHERE user_id = ?";
        Connector connector = new Connector();

        try (Connection conn = connector.connect()){
            PreparedStatement preparedStatement = conn.prepareStatement(newSQl);
            PreparedStatement preparedStatement1 = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement1.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSet rs1 = preparedStatement1.executeQuery();

            while (rs.next()) {
                numCourses +=1;
            }

            while(rs1.next()) {
                facultyID = rs1.getString("instructor_id");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }



        // --- 1. Home Panel ---
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(mainPanelColor);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Add padding


        JPanel titlePanel = new JPanel();

        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor); // Match the background

        JLabel welcomeLabel = new JLabel("Welcome, Faculty");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("Faculty name: " + username);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rollLabel = new JLabel("Faculty ID: "  + facultyID);
        rollLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rollLabel.setForeground(textColor);
        rollLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add a small vertical space
        titlePanel.add(nameLabel);
        titlePanel.add(rollLabel);
        homePanel.add(titlePanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        infoPanel.setBackground(mainPanelColor);


        JPanel coueseBox = createStatBox("No. of courses Offered", String.valueOf(numCourses));
        infoPanel.add(coueseBox);
        homePanel.add(infoPanel, BorderLayout.WEST);

// --- 2. Sections Panel ---
        JPanel sectionPanel = new JPanel(new BorderLayout(10, 10)); // Use BorderLayout
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        sectionPanel.setBackground(mainPanelColor);

        JLabel gradesTitle = new JLabel("Your Courses");
        gradesTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        gradesTitle.setForeground(textColor);
        sectionPanel.add(gradesTitle, BorderLayout.WEST);

// --- 2. stats Panel ---
        JPanel statsPanel = new JPanel();
        statsPanel.setBackground(mainPanelColor);
        statsPanel.add(new JLabel("Select the course to see stats") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 24));
            setForeground(textColor);
        }});

        // --- 2. TA Panel ---

        JPanel TAPanel = new JPanel();
        TAPanel.setBackground(mainPanelColor);
        TAPanel.add(new JLabel("Click on the course to see TA's Assigned") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 24));
            setForeground(textColor);
        }});


        mainContentPanel.add(homePanel, "HOME");
        mainContentPanel.add(sectionPanel, "Sections");
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