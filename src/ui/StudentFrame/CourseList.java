package ui.StudentFrame;


import ui.dashboard.StudentDashboard;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
// --- NEW IMPORTS ---
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CourseList extends JFrame {

    // --- Re-using the same style from StudentDashboard ---
    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168); // This color is now used
    private Color textColor = Color.WHITE;
    private Color tileBgColor = new Color(60, 60, 60); // Background for the course tiles
    private Color codeBgColor = new Color(57, 128, 174); // Blue background for code label

    // --- NEW: Icons for our custom checkbox ---
    private ImageIcon uncheckedIcon;
    private ImageIcon checkedIcon;

    private String rollNumber;
    private String username;

    // --- Panel to hold course tiles, made a class member ---
    private JPanel centerContentPanel;

    public CourseList(String rollNumber, String username) {
        super("Course Registration - " + username);

        this.rollNumber = rollNumber;
        this.username = username;

        // --- NEW: Create the custom checkbox icons ---
        this.uncheckedIcon = createCheckBoxIcon(false);
        this.checkedIcon = createCheckBoxIcon(true);

        // --- Standard Frame Setup ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(20, 20));

        // Set the same logo
        try {
            ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
            setIconImage(image.getImage());
        } catch (Exception e) {
            System.out.println("Logo not found: " + e.getMessage());
        }

        // --- Main Content Panel ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(mainPanelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        // --- Top Panel for Controls ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Stack items vertically
        topPanel.setBackground(mainPanelColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Bottom padding only


        // --- Header Panel for Back Button and Title ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(mainPanelColor);

        // --- Top-Left Back Button ---
        JButton backButton = createMenuButton("← Back");
        backButton.addActionListener(e -> {
            new StudentDashboard(rollNumber, username).setVisible(true);
            dispose();
        });

        // --- Title (now centered) ---
        JLabel titleLabel = new JLabel("Available Courses for Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Dropdown ---
        String[] semesters = {"Select term", "sem 1", "sem 2"};
        JComboBox<String> termDropdown = new JComboBox<>(semesters);
        termDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        termDropdown.setBackground(sideMenuColor);
        termDropdown.setForeground(textColor);
        termDropdown.setFocusable(false);
        ((JLabel) termDropdown.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        Dimension dropSize = new Dimension(200, 40);
        termDropdown.setPreferredSize(dropSize);
        termDropdown.setMaximumSize(dropSize);
        termDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(headerPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        topPanel.add(termDropdown);

        // --- Main Content Area (Scrollable panel) ---
        centerContentPanel = new JPanel();
        centerContentPanel.setLayout(new BoxLayout(centerContentPanel, BoxLayout.Y_AXIS));
        centerContentPanel.setBackground(mainPanelColor);
        centerContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JScrollPane scrollPane = new JScrollPane(centerContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // --- Dropdown Action Listener ---
        termDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) termDropdown.getSelectedItem();
                centerContentPanel.removeAll();
                if (selectedItem.equals("sem 1") || selectedItem.equals("sem 2")) {
                    System.out.println("User selected: " + selectedItem);
                    loadCourses();
                } else {
                    System.out.println("Prompt selected, clearing panel.");
                }
                centerContentPanel.revalidate();
                centerContentPanel.repaint();
            }
        });


        // --- Bottom Panel for Buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        bottomPanel.setBackground(mainPanelColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton registerButton = createMenuButton("Register Selected Courses");
        bottomPanel.add(registerButton);

        // --- UPDATED Action Listener for Register Button ---
        registerButton.addActionListener(e -> {
            System.out.println("Register button clicked. Registering selected courses:");
            Component[] tiles = centerContentPanel.getComponents();
            int selectedCount = 0;

            for (Component tile : tiles) {
                if (tile instanceof JPanel) {
                    JPanel tilePanel = (JPanel) tile;
                    // Find our custom checkbox (JLabel)
                    for (Component comp : tilePanel.getComponents()) {
                        if (comp instanceof JLabel) {
                            JLabel checkBoxLabel = (JLabel) comp;
                            // Check for the "selected" property we stored
                            Object selectedProp = checkBoxLabel.getClientProperty("selected");

                            if (selectedProp != null && (boolean) selectedProp) {
                                // It's selected! Get the course code.
                                String courseCode = (String) checkBoxLabel.getClientProperty("courseCode");
                                System.out.println(" - Selected: " + courseCode);
                                // ---
                                // TODO: Add your registration logic here
                                // Example: studentService.registerCourse(rollNumber, courseCode);
                                // ---
                                selectedCount++;
                                break; // Found the checkbox for this tile
                            }
                        }
                    }
                }
            }

            if (selectedCount == 0) {
                JOptionPane.showMessageDialog(this,
                        "You have not selected any courses to register.",
                        "No Courses Selected",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Successfully registered " + selectedCount + " course(s).\n(Check console for details)",
                        "Registration Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });


        // --- Add all panels to the main panel ---
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Populates the centerContentPanel with hardcoded course tiles.
     */
    private void loadCourses() {
        // Hardcoded examples
        JPanel tile1 = createCourseTilePanel("CSE121", "Discrete Mathematics", "4", "Mandatory (Core)", "Dr. Alan Turing");
        JPanel tile2 = createCourseTilePanel("CSE201", "Advanced Programming", "4", "Mandatory (Core)", "Dr. Ada Lovelace");
        JPanel tile3 = createCourseTilePanel("CSE231", "Operating Systems", "4", "Mandatory (Core)", "Dr. Linus Torvalds");

        centerContentPanel.add(tile1);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerContentPanel.add(tile2);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerContentPanel.add(tile3);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    }


    /**
     * --- UPDATED METHOD ---
     * Creates a course tile, now using our custom-drawn JLabel checkbox
     * and a clickable "course details" link.
     */
    private JPanel createCourseTilePanel(String code, String name, String credits, String regType, String instructor) {
        // Use 'this' (the JFrame) to show potential error dialogs
        final Component parentFrame = this;

        JPanel tilePanel = new JPanel(new BorderLayout(15, 15));
        tilePanel.setBackground(tileBgColor);

        Border lineBorder = BorderFactory.createLineBorder(new Color(80, 80, 80), 1);
        Border paddingBorder = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        tilePanel.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));
        tilePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));


        // --- Top Panel (Code + Name + Details Link) ---
        JPanel topInfoPanel = new JPanel();
        topInfoPanel.setLayout(new BoxLayout(topInfoPanel, BoxLayout.X_AXIS));
        topInfoPanel.setOpaque(false);

        JLabel codeLabel = new JLabel("Code - " + code);
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        codeLabel.setForeground(Color.WHITE);
        codeLabel.setBackground(codeBgColor);
        codeLabel.setOpaque(true);
        codeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel nameLabel = new JLabel("• Course - " + name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(textColor);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // --- NEW: Clickable "Details" Link ---
        JLabel detailsLink = new JLabel("Check course details ↗");
        detailsLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailsLink.setForeground(codeBgColor); // Use theme's blue color
        detailsLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailsLink.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); // Left padding

        detailsLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        String url = "https://techtree.iiitd.edu.in/viewDescription/filename?=" + code;
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (IOException | URISyntaxException ex) {
                        System.err.println("Failed to open browser: " + ex.getMessage());
                        // Show an error message to the user
                        JOptionPane.showMessageDialog(parentFrame,
                                "Could not open the link. Please visit:\n" +
                                        "https://techtree.iiitd.edu.in/viewDescription/filename?=" + code,
                                "Browser Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        // --- End of New Link ---


        topInfoPanel.add(codeLabel);
        topInfoPanel.add(nameLabel);
        topInfoPanel.add(detailsLink); // Add the new link
        topInfoPanel.add(Box.createHorizontalGlue()); // Pushes everything to the left


        // --- Bottom Panel (Details) ---
        JPanel bottomInfoPanel = new JPanel();
        bottomInfoPanel.setLayout(new BoxLayout(bottomInfoPanel, BoxLayout.X_AXIS));
        bottomInfoPanel.setOpaque(false);
        bottomInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel creditsPanel = createDetailPanel("Credits", credits);
        JPanel regTypePanel = createDetailPanel("Registration Type", regType);
        JPanel instructorPanel = createDetailPanel("Instructor", instructor);

        bottomInfoPanel.add(creditsPanel);
        bottomInfoPanel.add(Box.createRigidArea(new Dimension(40, 0)));
        bottomInfoPanel.add(regTypePanel);
        bottomInfoPanel.add(Box.createRigidArea(new Dimension(40, 0)));
        bottomInfoPanel.add(instructorPanel);
        bottomInfoPanel.add(Box.createHorizontalGlue());


        // --- NEW: Custom Checkbox (JLabel) ---
        JLabel checkBoxLabel = new JLabel();
        checkBoxLabel.setIcon(uncheckedIcon); // Set default state
        checkBoxLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Store the state and course code *inside* the label component
        checkBoxLabel.putClientProperty("selected", false);
        checkBoxLabel.putClientProperty("courseCode", code);

        // Add padding to the right
        checkBoxLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        // Add the click listener to swap icons and state
        checkBoxLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 1. Get current state
                boolean isSelected = (boolean) checkBoxLabel.getClientProperty("selected");

                // 2. Flip state
                isSelected = !isSelected;

                // 3. Set new icon
                checkBoxLabel.setIcon(isSelected ? checkedIcon : uncheckedIcon);

                // 4. Store new state
                checkBoxLabel.putClientProperty("selected", isSelected);
            }
        });


        // --- Add components to tile ---
        tilePanel.add(topInfoPanel, BorderLayout.NORTH);
        tilePanel.add(bottomInfoPanel, BorderLayout.CENTER);
        tilePanel.add(checkBoxLabel, BorderLayout.EAST); // Add our custom label

        return tilePanel;
    }


    /**
     * --- NEW METHOD ---
     * Programmatically draws an icon for our custom checkbox.
     * This is pure Java - no external files or libraries needed.
     */
    private ImageIcon createCheckBoxIcon(boolean isChecked) {
        int width = 20;
        int height = 20;
        // Use ARGB for transparency
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Enable anti-aliasing for smooth lines
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Draw the Box ---
        g2.setColor(Color.WHITE);
        // Use a 2px stroke
        g2.setStroke(new BasicStroke(2));
        // Draw a rounded rectangle (5px arc)
        // Inset by 1px so the stroke doesn't get cut off
        g2.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, 5, 5));

        if (isChecked) {
            // --- Draw the Checkmark ---
            // Use the app's button color
            g2.setColor(buttonColor);
            // Use a thicker 3px stroke for the check
            g2.setStroke(new BasicStroke(3));

            // Draw the "V" shape for the checkmark
            // (Coordinates are tuned for a 20x20 box)
            g2.drawLine(6, 10, 9, 14); // Left part of V
            g2.drawLine(9, 14, 15, 7); // Right part of V
        }

        g2.dispose(); // Clean up graphics object
        return new ImageIcon(image);
    }

    /**
     * Helper to create a small "detail" block (e.g., "Credits" and "4").
     */
    private JPanel createDetailPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(textColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(valueLabel);

        return panel;
    }


    /**
     * Creates a styled menu button (using the buttonColor).
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        return button;
    }
}