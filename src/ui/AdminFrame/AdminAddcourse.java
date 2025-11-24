package ui.AdminFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import middleware.adminService;
import dbClasses.AddCourse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI; // --- FIXED: Added Missing Import ---
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Calendar;

public class AdminAddcourse extends JFrame {

    // --- Consistent Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color inputBgColor = new Color(48, 54, 70);
    private Color errorColor = new Color(190, 60, 60);
    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);

    private String adminID;
    private String username;
    private JFrame parentFrame;

    // --- SERVICE ---
    private adminService adminService;

    // Form Components
    private JTextField txtCourseName, txtCourseId, txtInstructor, txtPrerequisites, txtCapacity;
    private JComboBox<String> comboDepartment, comboType, comboSemester, comboCredits;
    private JSpinner yearSpinner;
    private JTextArea txtDescription;

    public AdminAddcourse(String adminID, String username, JFrame parentFrame) {
        super("Add New Course");
        this.adminID = adminID;
        this.username = username;
        this.parentFrame = parentFrame;

        this.adminService = new adminService();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1080, 1080);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);

        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        JScrollPane scrollPane = createMainScrollPane(createFormContent());
        add(scrollPane, BorderLayout.CENTER);

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

        RoundedPanel formCard = new RoundedPanel(20, cardColor, borderColor, 1);
        formCard.setLayout(new GridBagLayout());
        formCard.setPreferredSize(new Dimension(900, 800));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Row 1 ---
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.7;
        formCard.add(createLabel("Course Name"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3;
        formCard.add(createLabel("Course Code"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        txtCourseName = createStyledTextField();
        formCard.add(txtCourseName, gbc);

        gbc.gridx = 1;
        txtCourseId = createStyledTextField();
        formCard.add(txtCourseId, gbc);

        // --- Row 2 ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        formCard.add(createLabel("Department"), gbc);
        gbc.gridx = 1;
        formCard.add(createLabel("Instructor ID"), gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        String[] depts = {"Computer Science", "Information Technology", "Electronics", "Mechanical", "Civil", "Business", "Physics", "Mathematics"};
        comboDepartment = createStyledComboBox(depts);
        formCard.add(comboDepartment, gbc);

        gbc.gridx = 1;
        txtInstructor = createStyledTextField();
        txtInstructor.setToolTipText("Enter Instructor User ID (e.g. alok)");
        formCard.add(txtInstructor, gbc);

        // --- Row 3 ---
        gbc.gridy = 4;
        gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel row3 = new JPanel(new GridLayout(1, 4, 20, 0));
        row3.setOpaque(false);

        JPanel p1 = new JPanel(new BorderLayout(0, 5)); p1.setOpaque(false);
        p1.add(createLabel("Course Type"), BorderLayout.NORTH);
        comboType = createStyledComboBox(new String[]{"Mandatory (Core)", "Elective", "Lab", "Seminar"});
        p1.add(comboType, BorderLayout.CENTER);
        row3.add(p1);

        JPanel p2 = new JPanel(new BorderLayout(0, 5)); p2.setOpaque(false);
        p2.add(createLabel("Credits"), BorderLayout.NORTH);
        comboCredits = createStyledComboBox(new String[]{"1", "2", "3", "4", "5", "6"});
        comboCredits.setSelectedItem("4");
        p2.add(comboCredits, BorderLayout.CENTER);
        row3.add(p2);

        JPanel p3 = new JPanel(new BorderLayout(0, 5)); p3.setOpaque(false);
        p3.add(createLabel("Semester"), BorderLayout.NORTH);
        comboSemester = createStyledComboBox(new String[]{"Monsoon", "Winter", "Summer"});
        p3.add(comboSemester, BorderLayout.CENTER);
        row3.add(p3);

        JPanel p4 = new JPanel(new BorderLayout(0, 5)); p4.setOpaque(false);
        p4.add(createLabel("Year"), BorderLayout.NORTH);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 2020, 2030, 1));
        styleSpinner(yearSpinner);
        p4.add(yearSpinner, BorderLayout.CENTER);
        row3.add(p4);

        formCard.add(row3, gbc);
        gbc.gridwidth = 1;

        // --- Row 4 ---
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
        formCard.add(txtPrerequisites, gbc);

        // --- Row 5 ---
        gbc.gridy = 7;
        gbc.gridx = 0; gbc.gridwidth = 2;
        formCard.add(createLabel("Course Description (Optional)"), gbc);

        gbc.gridy = 8;
        txtDescription = new JTextArea(5, 20);
        styleTextArea(txtDescription);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        styleScrollPane(scrollDesc);
        scrollDesc.setBorder(BorderFactory.createLineBorder(borderColor));
        formCard.add(scrollDesc, gbc);

        // --- Row 6: Buttons ---
        gbc.gridy = 9;
        gbc.insets = new Insets(40, 20, 20, 20);

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

        btnSave.addActionListener(e -> {
            String name = txtCourseName.getText();
            String code = txtCourseId.getText();
            String instId = txtInstructor.getText();
            String capStr = txtCapacity.getText();

            if(name.isEmpty() || code.isEmpty() || instId.isEmpty() || capStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int credits = Integer.parseInt((String) comboCredits.getSelectedItem());
                int capacity = Integer.parseInt(capStr);
                int year = (Integer) yearSpinner.getValue();

                AddCourse data = new AddCourse(
                        code,
                        name,
                        credits,
                        (String) comboDepartment.getSelectedItem(),
                        instId,
                        (String) comboSemester.getSelectedItem(),
                        year,
                        capacity
                );

                boolean success = adminService.createCourseOffering(data);

                if(success) {
                    JOptionPane.showMessageDialog(this, "Course '" + name + "' created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    goBack();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create course.\nCheck if Instructor ID exists and is valid.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Capacity must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        btnPanel.add(btnSave);

        formCard.add(btnPanel, gbc);
        container.add(formCard);
        return container;
    }

    // --- Helpers ---

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
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, buttonColor),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            }
        });
        return field;
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(inputBgColor);
            tf.setForeground(textColor);
            tf.setCaretColor(buttonColor);
        }
        spinner.setBorder(BorderFactory.createLineBorder(borderColor, 1));
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        box.setForeground(textColor);
        box.setBackground(inputBgColor);

        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
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

        box.setUI(new BasicComboBoxUI() {
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(inputBgColor);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("\u25BC");
                btn.setBackground(inputBgColor);
                btn.setForeground(textSecondaryColor);
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setFocusable(false);
                btn.setContentAreaFilled(false);
                return btn;
            }
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = super.createScroller();
                        // --- FIX: Use static nested class with passed colors ---
                        scroller.getVerticalScrollBar().setUI(new StyledScrollBarUI(buttonColor, bgColor));
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
        // --- FIX: Use static nested class with passed colors ---
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI(buttonColor, bgColor));
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI(buttonColor, bgColor));
    }

    // --- FIX: Made static and accept colors in constructor ---
    private static class StyledScrollBarUI extends BasicScrollBarUI {
        private Color thumbColor;
        private Color trackColor;

        public StyledScrollBarUI(Color thumbColor, Color trackColor) {
            this.thumbColor = thumbColor;
            this.trackColor = trackColor;
        }

        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = thumbColor;
            this.trackColor = trackColor;
        }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            return btn;
        }
    }
}