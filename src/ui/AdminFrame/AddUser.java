package ui.AdminFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
// NEW: Import for the action listener
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A JPanel designed to be placed inside a JDialog for adding new users.
 * It features a side navigation menu and a card layout for different user forms.
 */
public class AddUser extends JPanel {

    // --- UI Color Palette (Copied from Dashboards) ---
    private Color bgColor = new Color(42, 48, 60);
    private Color sideMenuColor = new Color(48, 54, 70);
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color textFieldBgColor = new Color(46, 52, 66); // --popover

    // --- MODIFIED: Added colors from Payfees.java for the back button ---
    private Color Buttonback = new Color(35, 42, 55);
    private Color Buttonhover = new Color(25, 30, 40);

    private JPanel cardHolderPanel;
    private CardLayout cardLayout;
    private List<RoundedButton> menuButtons;

    public AddUser() {
        // --- MODIFIED --- Set a more standard dialog size
        setPreferredSize(new Dimension(2160, 1080));
        setBackground(bgColor);
        setLayout(new BorderLayout());

        this.menuButtons = new ArrayList<>();

        // 2. Create and add the side menu
        JPanel sideMenuPanel = createSideMenuPanel();
        add(sideMenuPanel, BorderLayout.WEST);

        // 3. Create and add the main content panel (with CardLayout)
        cardLayout = new CardLayout();
        cardHolderPanel = new JPanel(cardLayout);
        cardHolderPanel.setOpaque(false);

        // 4. Create the individual form panels (the "cards")
        JPanel addStudentPanel = createFormPanel("Add New Student");
        JPanel addFacultyPanel = createFormPanel("Add New Faculty");
        JPanel addAdminPanel = createFormPanel("Add New Admin");

        // 5. Add the cards to the card holder
        cardHolderPanel.add(addStudentPanel, "ADD_STUDENT");
        cardHolderPanel.add(addFacultyPanel, "ADD_FACULTY");
        cardHolderPanel.add(addAdminPanel, "ADD_ADMIN");

        add(cardHolderPanel, BorderLayout.CENTER);

        // 6. Set the default view
        cardLayout.show(cardHolderPanel, "ADD_STUDENT");
        if (!menuButtons.isEmpty()) {
            setActiveButton(menuButtons.get(0)); // Set "Add Student" as active
        }
    }

    /**
     * Creates the left-side navigation panel.
     */
    /**
     * Creates the left-side navigation panel.
     */
    private JPanel createSideMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor);
        panel.setPreferredSize(new Dimension(280, 0)); // Slightly smaller than dashboard
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // --- MODIFIED: Replaced menuTitle JLabel with a Back Button ---

        // 1. Create the button using the helper
        RoundedButton backButton = createBackButton("← Back");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Add an action listener to close the dialog
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the top-level container (which should be the JDialog) and dispose it
                Window window = SwingUtilities.getWindowAncestor(AddUser.this);
                if (window instanceof JDialog) {
                    window.dispose();
                }
            }
        });

        // 3. Create a wrapper panel to hold the button and apply the
        //    same "outer" padding that the old menuTitle label had.
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonWrapper.setOpaque(false);
        // --- MODIFIED: Changed bottom padding from 10 to 0 ---
        buttonWrapper.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        buttonWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonWrapper.add(backButton);

        // --- End of Modification ---


        // Create navigation buttons
        RoundedButton addStudentButton = createSideMenuButton("Add Student");
        RoundedButton addFacultyButton = createSideMenuButton("Add Faculty");
        RoundedButton addAdminButton = createSideMenuButton("Add Admin");

        // Add buttons to the list for state management
        menuButtons.add(addStudentButton);
        menuButtons.add(addFacultyButton);
        menuButtons.add(addAdminButton);

        // Add Action Listeners
        addStudentButton.addActionListener(e -> {
            cardLayout.show(cardHolderPanel, "ADD_STUDENT");
            setActiveButton(addStudentButton);
        });

        addFacultyButton.addActionListener(e -> {
            cardLayout.show(cardHolderPanel, "ADD_FACULTY");
            setActiveButton(addFacultyButton);
        });

        addAdminButton.addActionListener(e -> {
            cardLayout.show(cardHolderPanel, "ADD_ADMIN");
            setActiveButton(addAdminButton);
        });

        // --- MODIFIED: Add the buttonWrapper instead of menuTitle ---
        panel.add(buttonWrapper);

         panel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Add Separator
        JSeparator navSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        navSeparator.setForeground(borderColor);
        navSeparator.setBackground(sideMenuColor);
        navSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        Box separatorWrapper = Box.createHorizontalBox();
        separatorWrapper.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        separatorWrapper.add(navSeparator);
        separatorWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separatorWrapper);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));


        panel.add(addStudentButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addFacultyButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addAdminButton);

        panel.add(Box.createVerticalGlue()); // Pushes buttons up

        return panel;
    }
    /**
     * A generic factory method to create a form panel (a "card").
     *
     * @param title The title to display at the top of the form.
     */
    private JPanel createFormPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(mainPanelColor);

        // --- MODIFIED --- Removed top padding here (was 10)
        panel.setBorder(BorderFactory.createEmptyBorder(0, 35, 40, 40));

        // --- 1. Title Panel (North) ---
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor);

        // --- MODIFIED --- Added top padding here (was 0)
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel formTitleLabel = new JLabel(title);
        formTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        formTitleLabel.setForeground(textColor);
        formTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(formTitleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        titleSeparator.setForeground(borderColor);
        titleSeparator.setBackground(mainPanelColor);
        titleSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        titlePanel.add(titleSeparator);

        panel.add(titlePanel, BorderLayout.NORTH);

        // --- 2. Form Fields Panel (Center) ---
        // We use GridBagLayout for a clean form alignment
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);

        // --- MODIFIED --- Reduced top padding to pull fields up (was 20)
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Full Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(createStyledLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        fieldsPanel.add(createStyledTextField(), gbc);

        // Row 1: ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        fieldsPanel.add(createStyledLabel("User ID:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        fieldsPanel.add(createStyledTextField(), gbc);

        // Row 2: Department
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        fieldsPanel.add(createStyledLabel("Department:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        fieldsPanel.add(createStyledTextField(), gbc);

        // Row 3: Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        fieldsPanel.add(createStyledLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        fieldsPanel.add(createStyledTextField(), gbc);

        // Row 4: Submit Button
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST; // Align button to the right
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(20, 10, 10, 10);
        RoundedButton submitButton = new RoundedButton(
                "Submit",
                buttonColor,      // Gradient Start
                buttonColorGlow,  // Gradient End
                8                 // Arc
        );
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitButton.setForeground(textColor);
        submitButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        fieldsPanel.add(submitButton, gbc);

        // Spacer to push everything to the top
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        fieldsPanel.add(new JLabel(), gbc);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Helper to create a styled form label.
     */
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(textSecondaryColor);
        return label;
    }

    /**
     * Helper to create a styled form text field.
     */
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setBackground(textFieldBgColor);
        textField.setForeground(textColor);
        textField.setCaretColor(textColor); // Set the cursor color

        // Set a nice border with padding
        Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border line = BorderFactory.createLineBorder(borderColor, 1);
        textField.setBorder(BorderFactory.createCompoundBorder(line, padding));

        return textField;
    }

    /**
     * --- MODIFIED HELPER METHOD ---
     * Creates a styled "Back" button, using the colors from Payfees.java
     */
    private RoundedButton createBackButton(String text) {
        RoundedButton button = new RoundedButton(
                text,
                Buttonback,           // Normal (from Payfees)
                Buttonhover,          // Hover (from Payfees)
                Buttonhover.darker(), // Pressed (from Payfees)
                8                     // Arc
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // from Payfees
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // from Payfees
        button.setPreferredSize(null); // Let it size itself
        return button;
    }

    /**
     * Helper to create a styled button for the side menu.
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

    /**
     * Sets the clicked button to active (gradient) and all others to inactive.
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
}