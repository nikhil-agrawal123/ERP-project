package ui.AdminFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Position;
import javax.swing.plaf.TextUI;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.JTextComponent;

/**
 * A JPanel that provides a UI for adding Students, Faculty, or Admins.
 * This panel is intended to be placed inside a JFrame or JDialog.
 */
public class AddUser extends JPanel {

    // --- UI Color Palette (from AdminDashboard) ---
    private Color bgColor = new Color(42, 48, 60);
    private Color sideMenuColor = new Color(48, 54, 70);
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color caretColor = new Color(52, 159, 148); // Accent color for caret
    private Color formFieldColor = new Color(42, 48, 60); // Darker background for fields

    private JPanel cardHolderPanel;
    private CardLayout cardLayout;
    private List<RoundedButton> menuButtons;

    public AddUser() {
        // Set up the main panel
        setLayout(new BorderLayout(0, 0));
        setBackground(mainPanelColor);
        this.menuButtons = new ArrayList<>();
        setPreferredSize(new Dimension(2160, 1080));

        // 1. Create the Left Menu Panel
        JPanel leftMenuPanel = createLeftMenu();

        // 2. Create the Right Content Panel
        JPanel rightContentPanel = createRightContentPanel();

        // 3. Add panels to the main layout
        add(leftMenuPanel, BorderLayout.WEST);
        add(rightContentPanel, BorderLayout.CENTER);

        // 4. Set the first button as active by default
        if (!menuButtons.isEmpty()) {
            setActiveButton(menuButtons.get(0));
        }
    }

    /**
     * Creates the left-side menu panel with navigation buttons.
     */
    private JPanel createLeftMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor);
        panel.setPreferredSize(new Dimension(260, 0));
        // Add a subtle border on the right to act as a separator
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, borderColor),
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
        ));

        // --- Menu Buttons ---
        RoundedButton b1 = createMenuButton("Add Student");
        RoundedButton b2 = createMenuButton("Add Faculty");
        RoundedButton b3 = createMenuButton("Add Admin");

        b1.addActionListener(e -> {
            setActiveButton(b1);
            cardLayout.show(cardHolderPanel, "STUDENT");
        });
        b2.addActionListener(e -> {
            setActiveButton(b2);
            cardLayout.show(cardHolderPanel, "FACULTY");
        });
        b3.addActionListener(e -> {
            setActiveButton(b3);
            cardLayout.show(cardHolderPanel, "ADMIN");
        });

        // Add components to the panel
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Top padding
        panel.add(b1);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(b2);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(b3);
        panel.add(Box.createVerticalGlue()); // Pushes buttons to the top

        return panel;
    }

    /**
     * Creates the right-side panel that holds the different forms (Student, Faculty, Admin).
     */
    private JPanel createRightContentPanel() {
        cardLayout = new CardLayout();
        cardHolderPanel = new JPanel(cardLayout);
        cardHolderPanel.setOpaque(false); // Transparent background

        // Create the individual form panels
        JPanel studentForm = createFormWrapperPanel(createStudentForm(), "Add New Student");
        JPanel facultyForm = createFormWrapperPanel(createFacultyForm(), "Add New Faculty");
        JPanel adminForm = createFormWrapperPanel(createAdminForm(), "Add New Admin");

        // Add forms to the CardLayout
        cardHolderPanel.add(studentForm, "STUDENT");
        cardHolderPanel.add(facultyForm, "FACULTY");
        cardHolderPanel.add(adminForm, "ADMIN");

        // Show the first form by default
        cardLayout.show(cardHolderPanel, "STUDENT");

        return cardHolderPanel;
    }

    /**
     * Creates a wrapper for a form. This wrapper centers the form vertically and
     * horizontally and provides padding.
     *
     * @param formPanel The actual form content.
     * @param title     The title to display above the form.
     * @return A JPanel ready to be added to the CardLayout.
     */
    private JPanel createFormWrapperPanel(JPanel formPanel, String title) {
        // This panel uses GridBagLayout to center the form
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false); // Transparent background
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Container for title and form
        RoundedPanel contentContainer = new RoundedPanel(15, cardColor, cardColor, 0);
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        contentContainer.setPreferredSize(new Dimension(500, 0)); // Set a preferred width
        contentContainer.setMaximumSize(new Dimension(500, 900)); // Set max width

        // --- Title ---
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        contentContainer.add(titleLabel);
        contentContainer.add(formPanel);

        // Add the content container to the wrapper (which centers it)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH; // Anchor to the top
        gbc.weighty = 1.0;                     // Allow it to be pushed to the top
        wrapperPanel.add(contentContainer, gbc);

        return wrapperPanel;
    }

    // --- Form Creation Methods ---

    /**
     * Creates the specific form fields for adding a Student.
     */
    private JPanel createStudentForm() {
        String[] departments = {"Computer Science", "Electrical Engineering", "Mechanical Engineering", "Civil Engineering", "Biotechnology"};
        return createBaseForm(
                new String[]{"Full Name", "Student ID", "Email", "Password", "Department", "Semester"},
                new JComponent[]{
                        new StyledTextField(),
                        new StyledTextField(),
                        new StyledTextField(),
                        new StyledPasswordField(),
                        new StyledComboBox<>(departments),
                        new StyledComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"})
                },
                "Add Student"
        );
    }

    /**
     * Creates the specific form fields for adding a Faculty.
     */
    private JPanel createFacultyForm() {
        String[] departments = {"Computer Science", "Electrical Engineering", "Mechanical Engineering", "Civil Engineering", "Biotechnology"};
        String[] designations = {"Professor", "Associate Professor", "Assistant Professor", "Lecturer"};
        return createBaseForm(
                new String[]{"Full Name", "Faculty ID", "Email", "Password", "Department", "Designation"},
                new JComponent[]{
                        new StyledTextField(),
                        new StyledTextField(),
                        new StyledTextField(),
                        new StyledPasswordField(),
                        new StyledComboBox<>(departments),
                        new StyledComboBox<>(designations)
                },
                "Add Faculty"
        );
    }

    /**
     * Creates the specific form fields for adding an Admin.
     */
    private JPanel createAdminForm() {
        return createBaseForm(
                new String[]{"Full Name", "Admin ID", "Email", "Password"},
                new JComponent[]{
                        new StyledTextField(),
                        new StyledTextField(),
                        new StyledTextField(),
                        new StyledPasswordField()
                },
                "Add Admin"
        );
    }

    /**
     * A generic method to create a form panel with labels and fields.
     */
    private JPanel createBaseForm(String[] labelTexts, JComponent[] fields, String submitButtonText) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false); // Transparent
        panel.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Spacing for all components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Add form fields
        for (int i = 0; i < labelTexts.length; i++) {
            // Label
            JLabel label = new JLabel(labelTexts[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(textSecondaryColor);
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.LINE_START;
            panel.add(label, gbc);

            // Field
            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.LINE_END;
            panel.add(fields[i], gbc);
        }

        // --- Submit Button ---
        RoundedButton submitButton = new RoundedButton(
                submitButtonText,
                buttonColor,
                buttonColorGlow,
                buttonColor.darker(),
                buttonColor,
                8
        );
        submitButton.setForeground(textColor);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitButton.setPreferredSize(new Dimension(0, 45)); // Taller button
        submitButton.addActionListener(e -> {
            // Handle form submission here
            System.out.println(submitButtonText + " button clicked.");
            // You would normally retrieve text from fields[] here
            JOptionPane.showMessageDialog(this,
                    "User added successfully (simulation).\nCheck console for details.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        gbc.gridx = 0;
        gbc.gridy = labelTexts.length;
        gbc.gridwidth = 3; // Span all 3 columns
        gbc.insets = new Insets(20, 8, 8, 8); // Extra top margin
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(submitButton, gbc);

        return panel;
    }


    // --- Helper Methods & Inner Classes ---

    /**
     * Helper to create styled buttons for the left menu.
     */
    private RoundedButton createMenuButton(String text) {
        RoundedButton button = new RoundedButton(
                text,
                sideMenuColor,      // Normal
                borderColor,        // Hover
                buttonColor.darker(), // Pressed
                buttonColor,        // Active
                8                   // Arc
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setForeground(textSecondaryColor);
        button.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuButtons.add(button); // Add to list for state management
        return button;
    }

    /**
     * Sets the clicked button to active (gradient) and all others to inactive.
     */
    private void setActiveButton(RoundedButton activeButton) {
        for (RoundedButton button : menuButtons) {
            button.setActive(false);
            button.setForeground(textSecondaryColor);
        }
        activeButton.setActive(true);
        activeButton.setForeground(textColor);
    }

    /**
     * A custom Border for text fields with padding and a rounded effect.
     */
    private class StyledFieldBorder implements Border {
        private int cornerRadius = 8;
        private Color focusColor = buttonColor;
        private Color defaultColor = borderColor;
        private int thickness = 1;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (c.hasFocus()) {
                g2.setColor(focusColor);
                thickness = 2; // Thicker border on focus
            } else {
                g2.setColor(defaultColor);
                thickness = 1;
            }

            // Draw the rounded border
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(x + thickness / 2.0, y + thickness / 2.0,
                    width - thickness, height - thickness,
                    cornerRadius, cornerRadius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            // Top, Left, Bottom, Right padding
            return new Insets(8, 12, 8, 12);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    /**
     * A custom Caret for text fields.
     */
    private class StyledCaret extends DefaultCaret {
        public StyledCaret() {
            setBlinkRate(500);
        }

        @Override
        protected synchronized void damage(Rectangle r) {
            if (r == null) return;
            // Repaint the component
            JComponent c = getComponent();
            if (c != null) {
                c.repaint(r.x, r.y, r.width, r.height);
            }
        }

        @Override
        public void paint(Graphics g) {
            JComponent c = getComponent();
            if (c == null || !isVisible()) return;

            Rectangle r = c.getVisibleRect();
            try {
                // Cast the UI to TextUI to get the correct modelToView method
                TextUI ui = (TextUI) c.getUI();
                Rectangle magicCaret = ui.modelToView((JTextComponent) c, getDot(), getMarkBias());

                if (magicCaret == null || (magicCaret.width == 0 && magicCaret.height == 0)) {
                    return;
                }

                if ((r != null && r.intersects(magicCaret)) || (r == null)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(caretColor); // Use our custom caret color
                    g2.setStroke(new BasicStroke(2)); // Make it thicker
                    g2.drawLine(magicCaret.x, magicCaret.y, magicCaret.x, magicCaret.y + magicCaret.height - 1);
                    g2.dispose();
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    /**
     * A JTextField styled to match the UI theme.
     */
    private class StyledTextField extends JTextField {
        public StyledTextField() {
            super();
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
            setBackground(formFieldColor);
            setForeground(textColor);
            setOpaque(true);
            setBorder(new StyledFieldBorder());
            setCaret(new StyledCaret());
            // Set padding
            setMargin(new Insets(5, 10, 5, 10));
        }
    }

    /**
     * A JPasswordField styled to match the UI theme.
     */
    private class StyledPasswordField extends JPasswordField {
        public StyledPasswordField() {
            super();
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
            setBackground(formFieldColor);
            setForeground(textColor);
            setOpaque(true);
            setBorder(new StyledFieldBorder());
            setCaret(new StyledCaret());
            setMargin(new Insets(5, 10, 5, 10));
        }
    }

    /**
     * A JComboBox styled to match the UI theme.
     */
    private class StyledComboBox<E> extends JComboBox<E> {
        public StyledComboBox(E[] items) {
            super(items);
            setBackground(formFieldColor);
            setForeground(textColor);
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
            setUI(new CustomComboBoxUI());
            setBorder(new StyledFieldBorder());
        }

        private class CustomComboBoxUI extends BasicComboBoxUI {
            @Override
            protected JButton createArrowButton() {
                // Create a custom arrow button
                BasicArrowButton arrowButton = new BasicArrowButton(
                        BasicArrowButton.SOUTH,
                        formFieldColor, // background
                        formFieldColor, // shadow
                        buttonColor,    // dark shadow (arrow color)
                        formFieldColor  // highlight
                );
                arrowButton.setBorder(BorderFactory.createEmptyBorder());
                arrowButton.setOpaque(true);
                arrowButton.setBackground(formFieldColor);
                return arrowButton;
            }

             @Override
             // --- THIS IS THE FULLY CORRECTED METHOD ---
             public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                 // 1. Get the generic-aware renderer from the JComboBox
                 ListCellRenderer<? super E> renderer = comboBox.getRenderer();

                 // 2. Get the component, casting listBox to raw JList to bridge generic gap
                 Component c = renderer.getListCellRendererComponent(
                         (JList) listBox, // <-- This cast is the fix
                         (E) comboBox.getSelectedItem(),
                         -1,
                         false,
                         false
                 );

                 c.setBackground(formFieldColor);
                 c.setForeground(textColor);
                 c.setFont(comboBox.getFont());
                 c.setComponentOrientation(comboBox.getComponentOrientation());

                 // Set bounds for painting
                 bounds.x += 5;
                 bounds.width -= 5;

                 // Paint the component
                 SwingUtilities.paintComponent(g, c, comboBox, bounds);
             }

            @Override
            protected ListCellRenderer<Object> createRenderer() {
                return new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        setBackground(isSelected ? buttonColor : formFieldColor);
                        setForeground(isSelected ? textColor : textSecondaryColor);
                        setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                        return this;
                    }
                };
            }

            @Override
            protected ComboPopup createPopup() {
                ComboPopup popup = super.createPopup();
                popup.getList().setBackground(formFieldColor);
                popup.getList().setSelectionBackground(buttonColor);
                popup.getList().setSelectionForeground(textColor);
                popup.getList().setBorder(BorderFactory.createLineBorder(borderColor, 1));
                return popup;
            }

            @Override
            public void paint(Graphics g, JComponent c) {
                // Override paint to ensure the background is set correctly
                c.setBackground(formFieldColor);
                super.paint(g, c);
            }
        }
    }
}