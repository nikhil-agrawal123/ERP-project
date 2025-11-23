package ui.AdminFrame;

import dbClasses.AddFaculty;
import dbClasses.NewStudent;
import dbClasses.AddAdmin;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import middleware.adminService;
import middleware.loggerService;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.sql.SQLException;

/**
 * A panel for adding new users (Students, Faculty, Admin).
 */
public class AddUser extends JPanel {

    // --- UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color sideMenuColor = new Color(48, 54, 70);
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);

    // --- Colors for Back Button ---
    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);

    private JPanel cardHolderPanel;
    private CardLayout cardLayout;
    private List<RoundedButton> menuButtons;

    // --- Service ---
    private adminService adminService;
    private loggerService loggerService;

    public AddUser() {
        super();
        this.menuButtons = new ArrayList<>();
        this.adminService = new adminService();
        this.loggerService = new loggerService();

        setLayout(new BorderLayout());
        setBackground(bgColor);
        setPreferredSize(new Dimension(1280, 800));

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

        cardLayout.show(cardHolderPanel, "WELCOME");
    }

    /**
     * Creates the side navigation panel.
     */
    private JPanel createSideMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        RoundedButton backButton = CreateBackbutton("← Back to Dashboard");
        backButton.setHorizontalAlignment(SwingConstants.CENTER);
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.dispose();
        });

        RoundedButton addStudentBtn = createSideMenuButton("Add Student");
        RoundedButton addFacultyBtn = createSideMenuButton("Add Faculty");
        RoundedButton addAdminBtn = createSideMenuButton("Add Admin");

        menuButtons.add(addStudentBtn);
        menuButtons.add(addFacultyBtn);
        menuButtons.add(addAdminBtn);

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

        panel.add(backButton);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JSeparator navSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        navSeparator.setForeground(borderColor);
        navSeparator.setBackground(sideMenuColor);
        navSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));

        Box separatorWrapper = Box.createHorizontalBox();
        separatorWrapper.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        separatorWrapper.add(navSeparator);
        separatorWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separatorWrapper);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(addStudentBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addFacultyBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(addAdminBtn);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

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


    /**
     * Creates the SPECIFIC "Add Student" form panel with extra fields.
     * CORRECTED LAYOUT INDICES
     */
    private JPanel createAddStudentPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(mainPanelColor);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        RoundedPanel formPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Header
        JLabel titleLabel = createFormTitle("Add New Student");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // Row 1: Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(borderColor);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(sep, gbc);

        // Reset for fields
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Row 2: Full Name
        addLabel(formPanel, "Full Name:", 0, 2, gbc);
        JTextField nameField = createFormField();
        addField(formPanel, nameField, 1, 2, gbc);

        // Row 3: Roll No
        addLabel(formPanel, "Student Roll No:", 0, 3, gbc);
        JTextField idField = createFormField();
        addField(formPanel, idField, 1, 3, gbc);

        // Row 4: Email
        addLabel(formPanel, "Student Email:", 0, 4, gbc);
        JTextField emailField = createFormField();
        addField(formPanel, emailField, 1, 4, gbc);

        // Row 5: Student ID
        addLabel(formPanel, "Student Id:", 0, 5, gbc);
        JTextField studentIdField = createFormField();
        addField(formPanel, studentIdField, 1, 5, gbc);

        // Row 6: Program
        addLabel(formPanel, "Program:", 0, 6, gbc);
        String[] programs = {
                "Select Program",
                "B.Tech in Computer Science & Engineering",
                "B.Tech in Electronics & Communication",
                "B.Tech in Computational Biology",
                "B.Tech in CS & Artificial Intelligence",
                "B.Tech in CS & Applied Mathematics",
                "B.Tech in CS & Design",
                "B.Tech in CS & Social Sciences",
                "M.Tech in Computer Science",
                "Ph.D. in Computer Science"
        };
        JComboBox<String> progDropdown = createFormComboBox(programs);
        addField(formPanel, progDropdown, 1, 6, gbc); // FIXED: Index is 6

        // Row 7: Years
        addLabel(formPanel, "Enrollment Year:", 0, 7, gbc);

        JPanel yearPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        yearPanel.setOpaque(false);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        JSpinner enrollSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 2000, 2100, 1));
        styleSpinner(enrollSpinner);
        yearPanel.add(enrollSpinner);

        JLabel currYearLabel = createFormLabel("Current Year:");
        currYearLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        yearPanel.add(currYearLabel);

        JSpinner currYearSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        styleSpinner(currYearSpinner);
        yearPanel.add(currYearSpinner);

        addField(formPanel, yearPanel, 1, 7, gbc); // FIXED: Index is 7

        // Row 8: Submit Button
        RoundedButton submitButton = createActionButton("Add Student");
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; // FIXED: Index is 8
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(30, 10, 0, 10);
        formPanel.add(submitButton, gbc);

        // Row 9: Spacer
        gbc.gridy = 9;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        formPanel.add(Box.createVerticalGlue(), gbc);

        // --- SUBMIT ACTION ---
        submitButton.addActionListener(e -> {
            if (nameField.getText().isEmpty() || idField.getText().isEmpty() || studentIdField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Roll Number, and User ID are required.", "Error", JOptionPane.ERROR_MESSAGE);
                loggerService.log("Admin", "Add Student", "Admin Tried adding a student but failed");
                return;
            }

            // Create Data Object (Using updated AddStudent constructor)
            NewStudent newStudent = new NewStudent(
                    nameField.getText(),
                    idField.getText(),
                    (String) progDropdown.getSelectedItem(),
                    emailField.getText(),
                    (Integer) enrollSpinner.getValue(),
                    (Integer) currYearSpinner.getValue(),
                    studentIdField.getText()
            );

            boolean success = false;
            try {
                success = adminService.addStudent(newStudent);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loggerService.log("Admin", "Add Student", "Admin Tried adding a student- successfully");
                // Reset fields
                nameField.setText("");
                idField.setText("");
                emailField.setText("");
                studentIdField.setText("");
                progDropdown.setSelectedIndex(0);
                loggerService.log("Admin", "New Student" , "New user has been added");

            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student. User ID or Roll No might be duplicate.", "Error", JOptionPane.ERROR_MESSAGE);
                loggerService.log("Admin", "New Student" , "New user creation failed");
            }
        });

        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.fill = GridBagConstraints.BOTH;
        outerGbc.weightx = 1.0;
        outerGbc.weighty = 1.0;
        outerGbc.insets = new Insets(0, 100, 0, 100);
        outerPanel.add(formPanel, outerGbc);

        return outerPanel;
    }

    /**
     * Creates the "Add Faculty" form panel (Generic).
     */
    private JPanel createAddFacultyPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(mainPanelColor);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        RoundedPanel formPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Header
        JLabel titleLabel = createFormTitle("Add New Faculty");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(borderColor);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(sep, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Row 2: Full Name
        addLabel(formPanel, "Full Name:", 0, 2, gbc);
        JTextField nameField = createFormField();
        addField(formPanel, nameField, 1, 2, gbc);

        // Row 3: Instructor ID
        addLabel(formPanel, "Instructor ID:", 0, 3, gbc);
        JTextField instIdField = createFormField();
        addField(formPanel, instIdField, 1, 3, gbc);

        // Row 4: User ID (Login)
        addLabel(formPanel, "User ID (Login):", 0, 4, gbc);
        JTextField userIdField = createFormField();
        addField(formPanel, userIdField, 1, 4, gbc);

        // Row 5: Email
        addLabel(formPanel, "Email:", 0, 5, gbc);
        JTextField emailField = createFormField();
        addField(formPanel, emailField, 1, 5, gbc);

        // Row 6: Department
        addLabel(formPanel, "Department:", 0, 6, gbc);
        String[] depts = {
                "Select Department", "Computer Science (CSE)", "Electronics (ECE)",
                "Computational Biology (CB)", "Mathematics", "Social Sciences", "Design", "Physics"
        };
        JComboBox<String> deptDropdown = createFormComboBox(depts);
        addField(formPanel, deptDropdown, 1, 6, gbc);

        // Row 7: Submit
        RoundedButton submitButton = createActionButton("Add Faculty");
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(30, 10, 0, 10);
        formPanel.add(submitButton, gbc);

        gbc.gridy = 8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        formPanel.add(Box.createVerticalGlue(), gbc);

        // --- SUBMIT ACTION ---
        submitButton.addActionListener(e -> {
            if (nameField.getText().isEmpty() || instIdField.getText().isEmpty() || userIdField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Instructor ID, and User ID are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AddFaculty newFaculty = new AddFaculty(
                    nameField.getText(),
                    instIdField.getText(),
                    userIdField.getText(),
                    emailField.getText(),
                    (String) deptDropdown.getSelectedItem()
            );

            boolean success = false;
            try {
                success = adminService.addInstructor(newFaculty);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Faculty added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loggerService.log("Admin", "Add Faculty", "Admin Tried adding a instructor successfully");
                nameField.setText("");
                instIdField.setText("");
                userIdField.setText("");
                emailField.setText("");
                deptDropdown.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add Faculty. ID might be duplicate.", "Error", JOptionPane.ERROR_MESSAGE);
                loggerService.log("Admin" ,"Failed to add Faculty", "Failed to add Faculty");
            }
        });

        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.fill = GridBagConstraints.BOTH;
        outerGbc.weightx = 1.0;
        outerGbc.weighty = 1.0;
        outerGbc.insets = new Insets(0, 100, 0, 100);
        outerPanel.add(formPanel, outerGbc);

        return outerPanel;
    }

    /**
     * Creates the "Add Admin" form panel (Generic).
     */
    private JPanel createAddAdminPanel() {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(mainPanelColor);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        RoundedPanel formPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Header
        JLabel titleLabel = createFormTitle("Add New Admin Staff");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(borderColor);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(sep, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Row 2: Full Name
        addLabel(formPanel, "Full Name:", 0, 2, gbc);
        JTextField nameField = createFormField();
        addField(formPanel, nameField, 1, 2, gbc);

        // Row 3: Admin ID
        addLabel(formPanel, "Admin ID:", 0, 3, gbc);
        JTextField adminIdField = createFormField();
        addField(formPanel, adminIdField, 1, 3, gbc);

        // Row 5: Email
        addLabel(formPanel, "Email:", 0, 5, gbc);
        JTextField emailField = createFormField();
        addField(formPanel, emailField, 1, 5, gbc);

        // Row 6: Role
        addLabel(formPanel, "Role:", 0, 6, gbc);
        String[] roles = {"Select Role", "Registrar", "Accounts", "IT Support", "HR", "Student Affairs", "System Admin"};
        JComboBox<String> roleDropdown = createFormComboBox(roles);
        addField(formPanel, roleDropdown, 1, 6, gbc);

        // Row 7: Submit
        RoundedButton submitButton = createActionButton("Add Admin");
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(30, 10, 0, 10);
        formPanel.add(submitButton, gbc);

        gbc.gridy = 8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        formPanel.add(Box.createVerticalGlue(), gbc);

        // --- SUBMIT ACTION ---
        submitButton.addActionListener(e -> {
            if (nameField.getText().isEmpty() || adminIdField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, Admin ID, and User ID are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AddAdmin newAdmin = new AddAdmin(
                    nameField.getText(),
                    adminIdField.getText(),
                    emailField.getText(),
                    (String) roleDropdown.getSelectedItem()
            );

            boolean success = adminService.registerAdmin(newAdmin);

            if (success) {
                JOptionPane.showMessageDialog(this, "Admin added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                adminIdField.setText("");
                emailField.setText("");
                roleDropdown.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add Admin. ID might be duplicate.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.fill = GridBagConstraints.BOTH;
        outerGbc.weightx = 1.0;
        outerGbc.weighty = 1.0;
        outerGbc.insets = new Insets(0, 100, 0, 100);
        outerPanel.add(formPanel, outerGbc);

        return outerPanel;
    }

    /**
     * Generic form builder for simple cases (Faculty/Admin).
     */
        private JPanel createGenericFormPanel(String title, String idLabelText, String[] departmentOptions, String buttonText) {
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(mainPanelColor);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        RoundedPanel formPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = createFormTitle(title);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(borderColor);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(sep, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        addLabel(formPanel, "Full Name:", 0, 2, gbc);
        JTextField nameField = createFormField();
        addField(formPanel, nameField, 1, 2, gbc);

        addLabel(formPanel, idLabelText, 0, 3, gbc);
        JTextField idField = createFormField();
        addField(formPanel, idField, 1, 3, gbc);

        addLabel(formPanel, "Department/Role:", 0, 4, gbc);
        JComboBox<String> deptDropdown = createFormComboBox(departmentOptions);
        addField(formPanel, deptDropdown, 1, 4, gbc);

        RoundedButton submitButton = createActionButton(buttonText);
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(30, 10, 0, 10);
        formPanel.add(submitButton, gbc);

        gbc.gridy = 6; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.VERTICAL;
        formPanel.add(Box.createVerticalGlue(), gbc);

        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.fill = GridBagConstraints.BOTH;
        outerGbc.weightx = 1.0;
        outerGbc.weighty = 1.0;
        outerGbc.insets = new Insets(0, 150, 0, 150);
        outerPanel.add(formPanel, outerGbc);

        return outerPanel;
    }

    // --- Helper Methods to reduce code duplication ---

    private void addLabel(JPanel panel, String text, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y; gbc.weightx = 0.0;
        panel.add(createFormLabel(text), gbc);
    }

    private void addField(JPanel panel, JComponent field, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y; gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private RoundedButton createSideMenuButton(String text) {
        RoundedButton button = new RoundedButton(text, sideMenuColor, borderColor, buttonColor.darker(), buttonColor, 8);
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
        RoundedButton button = new RoundedButton(text, Buttonback, Buttonhover, borderColor.darker(), 8);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setPreferredSize(null);
        return button;
    }

    private RoundedButton createActionButton(String text) {
        RoundedButton button = new RoundedButton(text, buttonColor, buttonColorGlow, 8);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setPreferredSize(null);
        return button;
    }

    private void setActiveButton(RoundedButton activeButton) {
        for (RoundedButton button : menuButtons) {
            button.setActive(false);
            button.setForeground(textSecondaryColor);
        }
        activeButton.setActive(true);
        activeButton.setForeground(textColor);
    }

    private JLabel createFormTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(textColor);
        return label;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(textSecondaryColor);
        return label;
    }

    private JTextField createFormField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(bgColor);
        field.setForeground(textColor);
        field.setCaretColor(buttonColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return field;
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(bgColor);
            tf.setForeground(textColor);
            tf.setCaretColor(buttonColor);
        }
        spinner.setBorder(BorderFactory.createLineBorder(borderColor, 1));
    }

    private JComboBox<String> createFormComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.BOLD, 16));
        comboBox.setForeground(textColor);
        comboBox.setBackground(bgColor); // Darker bg
        comboBox.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                RoundedButton arrowButton = new RoundedButton("▼", buttonColor, buttonColorGlow, buttonColor.darker(), 8);
                arrowButton.setForeground(textColor);
                arrowButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
                arrowButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return arrowButton;
            }
            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                g2.setColor(textColor);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                String text = (String) comboBox.getSelectedItem();
                if (text != null) {
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(text, bounds.x + 10, bounds.y + fm.getAscent() + (bounds.height - fm.getHeight())/2);
                }
                g2.dispose();
            }
        });
        return comboBox;
    }
}