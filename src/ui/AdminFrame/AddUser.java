package ui.AdminFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel for adding new users (Students, Faculty, Admin).
 * Designed to match the AdminDashboard and StudentDashboard aesthetics.
 */
public class AddUser extends JPanel {

    // --- UI Color Palette (Copied from AdminDashboard) ---
    private Color bgColor = new Color(42, 48, 60);            // --background
    private Color sideMenuColor = new Color(48, 54, 70);      // --sidebar-background
    private Color mainPanelColor = new Color(42, 48, 60);       // --background
    private Color cardColor = new Color(54, 59, 74);          // --card
    private Color popoverColor = new Color(46, 52, 66);       // --popover
    private Color borderColor = new Color(64, 69, 89);        // --border
    private Color buttonColor = new Color(52, 159, 148);      // --primary / --accent
    private Color buttonColorGlow = new Color(79, 196, 184);  // --primary-glow
    private Color textColor = new Color(255, 255, 255);       // --foreground
    private Color textSecondaryColor = new Color(179, 179, 179);

    // --- Colors for Back Button (Copied from StudentRegCourses) ---
    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);

    private JLayeredPane mainLayeredPane;
    private JPanel cardHolderPanel;
    private CardLayout cardLayout;

    // List to manage the active state of side-menu buttons
    private List<RoundedButton> menuButtons;

    /**
     * Constructor for the AddUser panel.
     */
    public AddUser() {
        super();
        this.menuButtons = new ArrayList<>();

        // Set up the main panel itself
        setLayout(new BorderLayout());
        setBackground(bgColor);
        // Set a preferred size for the JDialog to pack()
        setPreferredSize(new Dimension(2160, 1080));

        // --- 1. Side Menu (WEST) ---
        JPanel sideMenuPanel = createSideMenuPanel();
        add(sideMenuPanel, BorderLayout.WEST);

        // --- 2. Content Panel (CENTER) ---
        cardLayout = new CardLayout();
        cardHolderPanel = new JPanel(cardLayout);
        cardHolderPanel.setBackground(mainPanelColor);
        cardHolderPanel.setOpaque(true);

        // Add the different forms to the CardLayout
        cardHolderPanel.add(createWelcomePanel(), "WELCOME");
        cardHolderPanel.add(createAddStudentPanel(), "ADD_STUDENT");
        cardHolderPanel.add(createAddFacultyPanel(), "ADD_FACULTY");
        cardHolderPanel.add(createAddAdminPanel(), "ADD_ADMIN");

        add(cardHolderPanel, BorderLayout.CENTER);

        // Show the welcome panel by default
        cardLayout.show(cardHolderPanel, "WELCOME");
    }

    /**
     * Creates the side navigation panel.
     */
    private JPanel createSideMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor);
        panel.setPreferredSize(new Dimension(300, 0)); // Match StudentDashboard width
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // --- MODIFIED ---
        // Changed createHeaderButton to createSideMenuButton for same size
        RoundedButton backButton = CreateBackbutton("← Back to Dashboard");
        // Added this line to center the text
        backButton.setHorizontalAlignment(SwingConstants.CENTER);
        // --- END MODIFICATION ---

        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> {
            // Get the top-level window (the JDialog) and dispose of it
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) {
                w.dispose();
            }
        });

        // --- Navigation Buttons (Styled like StudentDashboard) ---
        RoundedButton addStudentBtn = createSideMenuButton("Add Student");
        RoundedButton addFacultyBtn = createSideMenuButton("Add Faculty");
        RoundedButton addAdminBtn = createSideMenuButton("Add Admin");

        // Add to list for state management
        menuButtons.add(addStudentBtn);
        menuButtons.add(addFacultyBtn);
        menuButtons.add(addAdminBtn);

        // --- Action Listeners ---
        addStudentBtn.addActionListener(e -> {
            cardLayout.show(cardHolderPanel, "ADD_STUDENT");
            setActiveButton(addStudentBtn);
        });

        addFacultyBtn.addActionListener(e -> {
            cardLayout.show(cardHolderPanel, "ADD_FACULTY");
            setActiveButton(addFacultyBtn);
        });

        addAdminBtn.addActionListener(e -> {
            cardLayout.show(cardHolderPanel, "ADD_ADMIN");
            setActiveButton(addAdminBtn);
        });

        // --- Layout Panel ---
        panel.add(backButton);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JSeparator navSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        navSeparator.setForeground(borderColor);
        navSeparator.setBackground(sideMenuColor);
        navSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));

        // Wrap separator for correct padding
        Box separatorWrapper = Box.createHorizontalBox();
        separatorWrapper.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15)); // Match button padding
        separatorWrapper.add(navSeparator);
        separatorWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separatorWrapper);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(addStudentBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addFacultyBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addAdminBtn);

        panel.add(Box.createVerticalGlue()); // Pushes all content up

        return panel;
    }

    /**
     * Creates a welcome/prompt panel for the center area.
     */
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(mainPanelColor);
        panel.setOpaque(true);

        JLabel promptLabel = new JLabel("Select an action from the menu to begin.", SwingConstants.CENTER);
        promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        promptLabel.setForeground(textSecondaryColor);

        panel.add(promptLabel);
        return panel;
    }

    // --- FORM CREATION METHODS ---

    /**
     * Creates the "Add Student" form panel.
     */
    private JPanel createAddStudentPanel() {
        String[] depts = {"Select Department", "CSE", "ECE", "CSD", "CSB", "CSAI", "HSS"};

        // Use a generic creator method
        return createGenericFormPanel(
                "Add New Student",
                "Student ID (Roll No.):",
                depts,
                "Add Student"
        );
    }

    /**
     * Creates the "Add Faculty" form panel.
     */
    private JPanel createAddFacultyPanel() {
        String[] depts = {"Select Department", "CSE", "ECE", "CSD", "CSB", "CSAI", "HSS", "Mathematics", "Science"};

        // Use a generic creator method
        return createGenericFormPanel(
                "Add New Faculty",
                "Faculty ID:",
                depts,
                "Add Faculty"
        );
    }

    /**
     * Creates the "Add Admin" form panel.
     */
    private JPanel createAddAdminPanel() {
        String[] depts = {"Select Role", "Registrar", "Accounts", "IT Support", "HR"};

        // Use a generic creator method
        return createGenericFormPanel(
                "Add New Admin Staff",
                "Admin ID:",
                depts,
                "Add Admin"
        );
    }

    /**
     * A generic method to create a user form to avoid code duplication.
     */
    private JPanel createGenericFormPanel(String title, String idLabelText, String[] departmentOptions, String buttonText) {
        // This outer panel uses GridBagLayout to center the form panel
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(mainPanelColor);
        outerPanel.setOpaque(true);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // The form itself is a RoundedPanel
        RoundedPanel formPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        JLabel titleLabel = createFormTitle(title);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(titleLabel, gbc);

        // --- Separator ---
        JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        titleSeparator.setForeground(borderColor);
        titleSeparator.setBackground(cardColor);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0); // Less vertical padding
        formPanel.add(titleSeparator, gbc);

        // --- Reset Insets and Gridwidth ---
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;

        // --- Full Name ---
        JLabel nameLabel = createFormLabel("Full Name:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(nameLabel, gbc);

        JTextField nameField = createFormField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0; // Make field take available space
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);

        // --- ID Field ---
        JLabel idLabel = createFormLabel(idLabelText);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(idLabel, gbc);

        JTextField idField = createFormField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // --- MODIFIED ---
        gbc.weightx = 1.0; // Added this to make the field stretch
        // --- END MODIFICATION ---
        formPanel.add(idField, gbc);

        // --- Department/Role ---
        JLabel deptLabel = createFormLabel("Department/Role:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(deptLabel, gbc);

        JComboBox<String> deptDropdown = createFormComboBox(departmentOptions);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // --- MODIFIED ---
        gbc.weightx = 1.0; // Added this to make the field stretch
        // --- END MODIFICATION ---
        formPanel.add(deptDropdown, gbc);

        // --- Submit Button ---
        RoundedButton submitButton = createActionButton(buttonText);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST; // Align button to the right
        gbc.insets = new Insets(20, 10, 0, 10); // Add top margin
        formPanel.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            // TODO: Add database logic here
            System.out.println("--- Submitting New User ---");
            System.out.println("Type: " + buttonText);
            System.out.println("Name: " + nameField.getText());
            System.out.println("ID: " + idField.getText());
            System.out.println("Dept: " + deptDropdown.getSelectedItem());
            JOptionPane.showMessageDialog(this,
                    buttonText + " operation simulated.\nCheck console for details.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Clear fields after submission
            nameField.setText("");
            idField.setText("");
            deptDropdown.setSelectedIndex(0);
        });

        // --- MODIFIED ---
        // Changed how the formPanel is added to outerPanel
        // This makes the form stretch horizontally (with padding) and stay at the top
        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.anchor = GridBagConstraints.NORTH; // Pin to top
        outerGbc.fill = GridBagConstraints.HORIZONTAL; // Stretch horizontally
        outerGbc.weightx = 1.0; // Allow horizontal stretch
        outerGbc.weighty = 1.0; // Use remaining vertical space to push to top
        outerGbc.insets = new Insets(0, 150, 0, 150); // Add 150px padding on left/right
        outerPanel.add(formPanel, outerGbc);
        // --- END MODIFICATION ---

        return outerPanel;
    }

    // --- STYLING HELPER METHODS (Copied from other classes) ---

    /**
     * Creates a styled button for the side menu.
     * (Copied from StudentDashboard)
     */
    private RoundedButton createSideMenuButton(String text) {
        RoundedButton button = new RoundedButton(
                text,
                sideMenuColor,        // Normal
                borderColor,          // Hover
                buttonColor.darker(), // Pressed
                buttonColor,          // Active
                8                     // Arc
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setForeground(textSecondaryColor);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private RoundedButton CreateBackbutton(String text) {
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
     * Creates a styled header button (for "Back").
     * (Copied from StudentRegCourses)
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

        // --- Set explicit size to match StudentRegCourses ---
        Dimension size = new Dimension(180, 40);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        return button;
    }

    /**
     * Creates a styled action button (gradient background).
     * (Copied from StudentRegCourses)
     */
    private RoundedButton createActionButton(String text) {
        RoundedButton button = new RoundedButton(
                text,
                buttonColor,      // Gradient Start (--primary)
                buttonColorGlow,  // Gradient End (--primary-glow)
                8                 // Arc radius
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // A bit smaller
        button.setPreferredSize(null); // Let it size itself
        return button;
    }

    /**
     * Sets the active state for side menu buttons.
     * (Copied from StudentDashboard)
     */
    private void setActiveButton(RoundedButton activeButton) {
        for (RoundedButton button : menuButtons) {
            button.setActive(false);
            button.setForeground(textSecondaryColor);
        }
        activeButton.setActive(true);
        activeButton.setForeground(textColor);
    }

    // --- FORM COMPONENT STYLING ---

    /**
     * Creates a styled title label for a form.
     */
    private JLabel createFormTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(textColor);
        return label;
    }

    /**
     * Creates a styled label for a form field.
     */
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(textSecondaryColor);
        return label;
    }

    /**
     * Creates a styled text field for a form.
     */
    private JTextField createFormField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(bgColor); // Use darkest bg
        field.setForeground(textColor);
        field.setCaretColor(buttonColor); // Accent caret
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding
        ));

        // --- MODIFIED ---
        // Removed fixed size to allow horizontal stretching
        // Dimension size = new Dimension(300, 40);
        // field.setPreferredSize(size);
        // field.setMinimumSize(size);
        // --- END MODIFICATION ---
        return field;
    }

    /**
     * Creates and styles a JComboBox for a form.
     */
    private JComboBox<String> createFormComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        styleComboBox(comboBox); // Apply the complex styling

        // --- MODIFIED ---
        // Removed fixed size to allow horizontal stretching
        // Dimension size = new Dimension(300, 40);
        // comboBox.setPreferredSize(size);
        // comboBox.setMinimumSize(size);
        // --- END MODIFICATION ---
        return comboBox;
    }

    /**
     * Applies modern styling to a JComboBox.
     * (Copied from StudentRegCourses)
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

    /**
     * Inner class for a custom styled scrollbar.
     * (Copied from StudentRegCourses)
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