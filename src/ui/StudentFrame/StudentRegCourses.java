package ui.StudentFrame;

import dbClasses.studentAvailableCourses;
import ui.dashboard.StudentDashboard;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import middleware.studentService;
import java.util.List;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI; // For styled scrollbar
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.awt.Desktop;

/**
 * A refactored class for course registration, matching the StudentDashboard UI.
 */
public class StudentRegCourses extends JFrame {

    // --- UI COLOR PALETTE FROM StudentDashboard ---
    private Color bgColor = new Color(42, 48, 60);
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);
    private studentService student;
// --muted-foreground

    // --- Icons for our custom checkbox (will be styled with new colors) ---
    private ImageIcon uncheckedIcon;
    private ImageIcon checkedIcon;

    private String rollNumber;
    private String username;

    private JPanel centerContentPanel;

    public StudentRegCourses(String rollNumber, String username) {
        super("Course Registration - " + username);

        this.rollNumber = rollNumber;
        this.username = username;

        this.uncheckedIcon = createCheckBoxIcon(false);
        this.checkedIcon = createCheckBoxIcon(true);
        this.student = new studentService();

        // --- Standard Frame Setup (using new colors) ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800); // Match dashboard size
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Match dashboard state
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout()); // Simplified to just BorderLayout

        try {
            ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
            setIconImage(image.getImage());
        } catch (Exception e) {
            System.out.println("Logo not found: " + e.getMessage());
        }

        // --- Main Content Panel (replaces old 'mainPanel') ---
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(mainPanelColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 40, 40)); // Added more padding


        // --- Top Panel for Controls ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false); // Use parent's background
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));


        // --- Header Panel for Back Button and Title ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // --- Top-Left Back Button (using RoundedButton) ---
        RoundedButton backButton = createHeaderButton("← Back to Dashboard");
        backButton.addActionListener(e -> {
            new StudentDashboard(rollNumber, username).setVisible(true);
            dispose();
        });

        // --- Title (matching dashboard style) ---
        JLabel titleLabel = new JLabel("Available Courses for Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36)); // Larger font
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Dropdown (styled to match) ---
        String[] semesters = {"Select Term", "Monsoon 2025"};
        JComboBox<String> termDropdown = new JComboBox<>(semesters);
        styleComboBox(termDropdown); // Apply custom styling

        Dimension dropSize = new Dimension(300, 45); // Larger, fixed size
        termDropdown.setPreferredSize(dropSize);
        termDropdown.setMaximumSize(dropSize);

        // --- Wrapper for dropdown to align it left ---
        JPanel dropdownWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dropdownWrapper.setOpaque(false);
        dropdownWrapper.add(termDropdown);
        dropdownWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        topPanel.add(headerPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        topPanel.add(dropdownWrapper);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- Separator ---
        JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        titleSeparator.setForeground(borderColor);
        titleSeparator.setBackground(mainPanelColor);
        titleSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        titleSeparator.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(titleSeparator);


        // --- Main Content Area (Scrollable panel) ---
        centerContentPanel = new JPanel();
        centerContentPanel.setLayout(new BoxLayout(centerContentPanel, BoxLayout.Y_AXIS));
        centerContentPanel.setBackground(mainPanelColor);
        // Add padding on the right to make room for the scrollbar without content overlap
        centerContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        JScrollPane scrollPane = new JScrollPane(centerContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(mainPanelColor);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // Apply custom scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());


        // --- Dropdown Action Listener ---
        termDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) termDropdown.getSelectedItem();
                centerContentPanel.removeAll();
                if (!selectedItem.equals("Select Term")) {
                    System.out.println("User selected: " + selectedItem);
                    loadCourses(selectedItem); // Load placeholder courses
                } else {
                    showPromptCard();
                }
                centerContentPanel.revalidate();
                centerContentPanel.repaint();
            }
        });

        // --- Bottom Panel for Buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        RoundedButton registerButton = createActionButton("Register Selected Courses");
        bottomPanel.add(registerButton);

        registerButton.addActionListener(e -> {
            System.out.println("Register button clicked. Registering selected courses:");
            Component[] components = centerContentPanel.getComponents();
            int selectedCount = 0;
            java.util.List<String> selectedCourses = new java.util.ArrayList<>();

            for (Component comp : components) {
                if (comp instanceof RoundedPanel) {
                    RoundedPanel tilePanel = (RoundedPanel) comp;
                    for (Component tileComp : tilePanel.getComponents()) {
                        if (tileComp instanceof JLabel) {
                            JLabel checkBoxLabel = (JLabel) tileComp;
                            Object selectedProp = checkBoxLabel.getClientProperty("selected");

                            if (selectedProp != null && (boolean) selectedProp) {
                                String courseCode = (String) checkBoxLabel.getClientProperty("courseCode");
                                selectedCourses.add(courseCode);
                                selectedCount++;
                                break;
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
                // ---
                // TODO: Add your registration logic here
                // Example: studentService.registerCourses(rollNumber, selectedCourses);
                // ---
                String courseList = String.join(", ", selectedCourses);
                System.out.println("Registering: " + courseList);

                JOptionPane.showMessageDialog(this,
                        "Successfully registered " + selectedCount + " course(s):\n" + courseList,
                        "Registration Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // --- Add all panels to the main content panel ---
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add the main content panel to the frame
        add(contentPanel, BorderLayout.CENTER);

        // Initial state
        showPromptCard();
    }

    /**
     * Shows a prompt card in the center panel.
     */
    private void showPromptCard() {
        centerContentPanel.removeAll();
        RoundedPanel promptPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        promptPanel.setLayout(new BorderLayout());
        promptPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        promptPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); // Constrain height

        JLabel promptLabel = new JLabel("Please select a term from the dropdown above to view available courses.", SwingConstants.CENTER);
        promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        promptLabel.setForeground(textSecondaryColor);

        promptPanel.add(promptLabel, BorderLayout.CENTER);

        centerContentPanel.add(promptPanel);
        centerContentPanel.revalidate();
        centerContentPanel.repaint();
    }


    /**
     * Populates the centerContentPanel with styled course tiles.
     */
    private void loadCourses(String semester) {

        List<studentAvailableCourses> courses = student.AllCourses(semester);

        courses.forEach(course -> {
            JPanel coursePanel = createCourseTilePanel(course.getCourse_code(),course.getCourse_name(),String.valueOf(course.getCourse_credits()) ,course.getOfferedBY());
            centerContentPanel.add(coursePanel);
            centerContentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        });
    }


    /**
     * Creates a styled course tile using RoundedPanel.
     */
    private JPanel createCourseTilePanel(String code, String name, String credits, String instructor) {
        // Use the new RoundedPanel as the base
        RoundedPanel tilePanel = new RoundedPanel(15, cardColor, borderColor, 1);
        tilePanel.setLayout(new BorderLayout(15, 10)); // Gaps
        tilePanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25)); // Padding
        tilePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160)); // Fixed height
        tilePanel.setMinimumSize(new Dimension(800, 160)); // Min size


        // --- Top Panel (Code + Name + Details Link) ---
        JPanel topInfoPanel = new JPanel();
        topInfoPanel.setLayout(new BoxLayout(topInfoPanel, BoxLayout.X_AXIS));
        topInfoPanel.setOpaque(false);

        JLabel codeLabel = new JLabel(code);
        codeLabel.setFont(new Font("Segoe UI Mono", Font.BOLD, 16)); // Monospaced font
        codeLabel.setForeground(Color.WHITE);
        codeLabel.setBackground(buttonColor); // Use theme accent
        codeLabel.setOpaque(true);
        codeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Larger font
        nameLabel.setForeground(textColor);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15)); // Spacing

        topInfoPanel.add(codeLabel);
        topInfoPanel.add(nameLabel);
        topInfoPanel.add(Box.createHorizontalGlue()); // Pushes link to the right

        // --- Use the new createClickableLink helper ---
        JLabel detailsLink = createClickableLink(
                "Check course details ↗",
                "https://techtree.iiitd.edu.in/viewDescription/filename?=" + code
        );
        topInfoPanel.add(detailsLink);


        // --- Bottom Panel (Details) ---
        JPanel bottomInfoPanel = new JPanel();
        bottomInfoPanel.setLayout(new BoxLayout(bottomInfoPanel, BoxLayout.X_AXIS));
        bottomInfoPanel.setOpaque(false);
        bottomInfoPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0)); // Top margin

        JPanel creditsPanel = createDetailPanel("Credits", credits);
        JPanel instructorPanel = createDetailPanel("Instructor", instructor);

        bottomInfoPanel.add(creditsPanel);
        bottomInfoPanel.add(Box.createRigidArea(new Dimension(40, 0)));
        bottomInfoPanel.add(instructorPanel);
        bottomInfoPanel.add(Box.createHorizontalGlue()); // Push all details left


        // --- Custom Checkbox (JLabel) ---
        JLabel checkBoxLabel = new JLabel();
        checkBoxLabel.setIcon(uncheckedIcon); // Set default state
        checkBoxLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkBoxLabel.putClientProperty("selected", false);
        checkBoxLabel.putClientProperty("courseCode", code);
        // Add padding to make it a larger click target
        checkBoxLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));

        checkBoxLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean isSelected = (boolean) checkBoxLabel.getClientProperty("selected");
                isSelected = !isSelected;
                checkBoxLabel.setIcon(isSelected ? checkedIcon : uncheckedIcon);
                checkBoxLabel.putClientProperty("selected", isSelected);
            }
        });

        // --- Add components to tile ---
        tilePanel.add(topInfoPanel, BorderLayout.NORTH);
        tilePanel.add(bottomInfoPanel, BorderLayout.CENTER);
        tilePanel.add(checkBoxLabel, BorderLayout.EAST);

        return tilePanel;
    }


    /**
     * Programmatically draws an icon for our custom checkbox, using the new theme.
     */
    private ImageIcon createCheckBoxIcon(boolean isChecked) {
        int width = 24; // Slightly larger
        int height = 24;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Draw the Box ---
        g2.setColor(textSecondaryColor); // Use muted color for the box
        g2.setStroke(new BasicStroke(2));
        g2.draw(new RoundRectangle2D.Float(1, 1, width - 3, height - 3, 8, 8)); // More rounded

        if (isChecked) {
            // --- Fill the Box ---
            g2.setColor(buttonColor); // Use theme's accent color
            g2.fill(new RoundRectangle2D.Float(1, 1, width - 3, height - 3, 8, 8));

            // --- Draw the Checkmark ---
            g2.setColor(textColor); // White check
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(7, 12, 11, 17); // Left part of V
            g2.drawLine(11, 17, 18, 8); // Right part of V
        }

        g2.dispose();
        return new ImageIcon(image);
    }

    /**
     * Helper to create a small "detail" block (e.g., "Credits" and "4").
     * Styled to match the new theme.
     */
    private JPanel createDetailPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(textSecondaryColor); // Muted foreground
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(textColor); // Main foreground
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(valueLabel);

        return panel;
    }

    // --- HELPER METHODS COPIED FROM StudentDashboard ---

    /**
     * Creates a styled header button (solid, dark background).
     */
    private RoundedButton createHeaderButton(String text) {
        RoundedButton button = new RoundedButton(
                text,
                Buttonback, // normal
                Buttonhover,   // hover
                borderColor.darker(), // pressed
                8
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setPreferredSize(null);
        return button;
    }

    /**
     * Creates a styled action button (gradient background).
     */
    private RoundedButton createActionButton(String text) {
        RoundedButton button = new RoundedButton(
                text,
                buttonColor,      // Gradient Start (--primary)
                buttonColorGlow,  // Gradient End (--primary-glow)
                8                 // Arc radius
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Slightly larger
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25)); // More padding
        button.setPreferredSize(null);
        return button;
    }

    /**
     * Creates a clickable hyperlink-style JLabel.
     */
    private JLabel createClickableLink(String text, String url) {
        JLabel linkLabel = new JLabel("<html><u>" + text + "</u></html>"); // Underlined
        linkLabel.setForeground(buttonColor); // Use accent color
        linkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        linkLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(url));
                    } else {
                        showError("Cannot open link. OS does not support Desktop.browse.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Could not open link: " + e.getMessage());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                linkLabel.setForeground(buttonColorGlow); // Brighter on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                linkLabel.setForeground(buttonColor);
            }
        });

        return linkLabel;
    }

    /**
     * Helper to show a formatted error message.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Applies modern styling to a JComboBox.
     */
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.BOLD, 16));
        comboBox.setForeground(textColor);
        comboBox.setBackground(cardColor); // Dark card color
        comboBox.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        comboBox.setFocusable(false);

        // --- Custom Renderer ---
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value.toString());
                setBackground(isSelected ? buttonColor : cardColor);
                setForeground(isSelected ? textColor : textSecondaryColor);
                setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                return this;
            }
        });

        // --- Custom UI (to style arrow) ---
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                // Create a button with the new theme
                RoundedButton arrowButton = new RoundedButton("▼",
                        buttonColor, buttonColor.brighter(), buttonColor.darker(), 8);
                arrowButton.setForeground(textColor);
                arrowButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
                arrowButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return arrowButton;
            }

            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                // Custom painting to get padding
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(cardColor);
                g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

                String text = (String) comboBox.getSelectedItem();
                FontMetrics fm = g2.getFontMetrics();

                g2.setColor(textColor);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.drawString(text, bounds.x + 15, bounds.y + fm.getAscent() + (bounds.height - fm.getHeight()) / 2);

                g2.dispose();
            }
        });
    }

    // --- INNER CLASS ---
    /**
     * Inner class for a custom styled scrollbar.
     * Copied directly from StudentDashboard.
     */
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = buttonColor;      // Accent color for the thumb
            this.trackColor = cardColor;      // Dark card color for the track
            this.thumbDarkShadowColor = buttonColor;
            this.thumbHighlightColor = buttonColor;
            this.thumbLightShadowColor = buttonColor;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            // Draw a rounded rectangle for the thumb
            g2.fill(new RoundRectangle2D.Float(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 10, 10));
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            g2.fill(trackBounds);
            g2.dispose();
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            jbutton.setMinimumSize(new Dimension(0, 0));
            jbutton.setMaximumSize(new Dimension(0, 0));
            return jbutton;
        }
    }
}