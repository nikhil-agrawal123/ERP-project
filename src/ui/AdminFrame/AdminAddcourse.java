package ui.AdminFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class AdminAddcourse extends JFrame {

    // --- Consistent Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color inputBgColor = new Color(48, 54, 70); // Slightly darker for inputs
    private Color errorColor = new Color(190, 60, 60);
    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);

    private String adminID;
    private String username;
    private JFrame parentFrame;

    // Form Components
    private JTextField txtCourseName, txtCourseId, txtInstructor, txtPrerequisites, txtCapacity;
    private JComboBox<String> comboDepartment, comboType, comboSemester, comboCredits;
    private JTextArea txtDescription;

    public AdminAddcourse(String adminID, String username, JFrame parentFrame) {
        super("Add New Course");
        this.adminID = adminID;
        this.username = username;
        this.parentFrame = parentFrame;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1080, 1080);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);

        // Layout
        setLayout(new BorderLayout());

        // 1. Header
        add(createHeader(), BorderLayout.NORTH);

        // 2. Main Scrollable Content
        JScrollPane scrollPane = createMainScrollPane(createFormContent());
        add(scrollPane, BorderLayout.CENTER);

        // Handle window closing to reshow parent
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBack();
            }
        });
    }

    private void goBack() {
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        }
        dispose();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bgColor);
        header.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Add New Course");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(textColor);
        header.add(title, BorderLayout.WEST);

        RoundedButton backBtn = new RoundedButton("Back", Buttonback, Buttonhover, borderColor.darker(), borderColor, 10);
        backBtn.setForeground(textSecondaryColor);
        backBtn.setPreferredSize(new Dimension(100, 40));
        backBtn.addActionListener(e -> goBack());
        header.add(backBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel createFormContent() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(bgColor);
        container.setBorder(new EmptyBorder(20, 20, 20, 20));

        // The Card holding the form
        RoundedPanel formCard = new RoundedPanel(20, cardColor, borderColor, 1);
        formCard.setLayout(new GridBagLayout());
        // Limit width of the form card for aesthetics
        formCard.setPreferredSize(new Dimension(900, 750));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 5, 20); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Row 1: Course Name & ID ---
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.7;
        formCard.add(createLabel("Course Name"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formCard.add(createLabel("Course ID"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        txtCourseName = createStyledTextField();
        formCard.add(txtCourseName, gbc);

        gbc.gridx = 1;
        txtCourseId = createStyledTextField();
        formCard.add(txtCourseId, gbc);

        // --- Row 2: Department & Instructor ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        formCard.add(createLabel("Department"), gbc);
        gbc.gridx = 1;
        formCard.add(createLabel("Instructor Name"), gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        String[] depts = {"Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil", "Business"};
        comboDepartment = createStyledComboBox(depts);
        formCard.add(comboDepartment, gbc);

        gbc.gridx = 1;
        txtInstructor = createStyledTextField();
        formCard.add(txtInstructor, gbc);

        // --- Row 3: Type, Credits, Semester ---
        gbc.gridy = 4;
        gbc.gridx = 0; gbc.gridwidth = 2; // Span full width to add a sub-panel
        JPanel row3 = new JPanel(new GridLayout(1, 3, 20, 0));
        row3.setOpaque(false);

        // Col 1
        JPanel p1 = new JPanel(new BorderLayout(0, 5)); p1.setOpaque(false);
        p1.add(createLabel("Course Type"), BorderLayout.NORTH);
        comboType = createStyledComboBox(new String[]{"Mandatory (Core)", "Elective", "Lab", "Seminar"});
        p1.add(comboType, BorderLayout.CENTER);
        row3.add(p1);

        // Col 2
        JPanel p2 = new JPanel(new BorderLayout(0, 5)); p2.setOpaque(false);
        p2.add(createLabel("Credits"), BorderLayout.NORTH);
        comboCredits = createStyledComboBox(new String[]{"1", "2", "3", "4", "5", "6"});
        comboCredits.setSelectedItem("3");
        p2.add(comboCredits, BorderLayout.CENTER);
        row3.add(p2);

        // Col 3
        JPanel p3 = new JPanel(new BorderLayout(0, 5)); p3.setOpaque(false);
        p3.add(createLabel("Semester"), BorderLayout.NORTH);
        comboSemester = createStyledComboBox(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
        p3.add(comboSemester, BorderLayout.CENTER);
        row3.add(p3);

        formCard.add(row3, gbc);
        gbc.gridwidth = 1; // Reset

        // --- Row 4: Capacity & Prerequisites ---
        gbc.gridy = 5;
        gbc.gridx = 0; gbc.weightx = 0.3;
        formCard.add(createLabel("Class Capacity"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formCard.add(createLabel("Prerequisites (Optional)"), gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        txtCapacity = createStyledTextField();
        txtCapacity.setText("60");
        formCard.add(txtCapacity, gbc);

        gbc.gridx = 1;
        txtPrerequisites = createStyledTextField();
        txtPrerequisites.setToolTipText("e.g. CS101, MATH200");
        formCard.add(txtPrerequisites, gbc);

        // --- Row 5: Description ---
        gbc.gridy = 7;
        gbc.gridx = 0; gbc.gridwidth = 2;
        formCard.add(createLabel("Course Description"), gbc);

        gbc.gridy = 8;
        txtDescription = new JTextArea(5, 20);
        styleTextArea(txtDescription);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        styleScrollPane(scrollDesc);
        scrollDesc.setBorder(BorderFactory.createLineBorder(borderColor));
        formCard.add(scrollDesc, gbc);

        // --- Row 6: Buttons ---
        gbc.gridy = 9;
        gbc.insets = new Insets(40, 20, 20, 20); // More top margin

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        RoundedButton btnCancel = new RoundedButton("Cancel", cardColor, borderColor, errorColor, borderColor, 10);
        btnCancel.setPreferredSize(new Dimension(120, 45));
        btnCancel.setForeground(textColor);
        btnCancel.addActionListener(e -> goBack());

        RoundedButton btnSave = new RoundedButton("Create Course", buttonColor, buttonColorGlow, buttonColorGlow, buttonColor, 10);
        btnSave.setPreferredSize(new Dimension(180, 45));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // SAVE ACTION
        btnSave.addActionListener(e -> {
            // TODO: Implement Database Logic here
            String name = txtCourseName.getText();
            String id = txtCourseId.getText();

            if(name.isEmpty() || id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Course '" + name + "' created successfully!");
                goBack();
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        btnPanel.add(btnSave);

        formCard.add(btnPanel, gbc);

        // Add Form Card to the Main Container
        container.add(formCard);
        return container;
    }

    // --- UI Helper Methods for Consistency ---

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(textSecondaryColor);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(textColor);
        field.setBackground(inputBgColor);
        field.setCaretColor(buttonColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, buttonColor),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });
        return field;
    }

    // --- KEY FIX FOR DROPDOWN (JCOMBOBOX) ---
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        box.setForeground(textColor);
        box.setBackground(inputBgColor);

        // 1. Custom Renderer for the Dropdown List Items
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                // Set colors for the popup list
                if (isSelected) {
                    setBackground(buttonColor);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(inputBgColor);
                    setForeground(textColor);
                }
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });

        // 2. Custom UI to handle the Main Box display and the Popup
        box.setUI(new BasicComboBoxUI() {

            // Fix 1: Paint the background of the main box manually to avoid the "White" glitch
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(inputBgColor);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }

            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("\u25BC"); // Down arrow symbol
                btn.setBackground(inputBgColor);
                btn.setForeground(textSecondaryColor);
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setFocusable(false);
                btn.setContentAreaFilled(false); // Remove button default fill
                return btn;
            }

            // Fix 2: Style the Popup (The actual list that drops down)
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = super.createScroller();
                        // Make the scrollbar match the rest of the app
                        scroller.getVerticalScrollBar().setUI(new StyledScrollBarUI());
                        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        return scroller;
                    }
                };
                popup.setBorder(BorderFactory.createLineBorder(borderColor));
                return popup;
            }
        });

        box.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
        return box;
    }

    private void styleTextArea(JTextArea area) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        area.setForeground(textColor);
        area.setBackground(inputBgColor);
        area.setCaretColor(buttonColor);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
    }

    private JScrollPane createMainScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        styleScrollPane(scrollPane);
        return scrollPane;
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(bgColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI());
    }

    // Custom ScrollBar UI (Inner Class)
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = buttonColor;
            this.trackColor = bgColor;
        }
        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            return btn;
        }
    }
}