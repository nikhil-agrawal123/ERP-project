package ui.dashboard;

import middleware.services;
import middleware.loggerService;
import ui.StudentFrame.StudentProfile;
import ui.FacultyFrame.FacultyProfile;

import javax.swing.*;
import java.awt.*;

/**
 * A modal dialog window for changing a user's password.
 * Supports both Student and Faculty profiles.
 */
public class ChangePasswordDialog extends JDialog {

    // --- User ID (Roll No or Faculty ID) ---
    private String userId;

    // --- Role Detection ---
    private boolean isFaculty = false;
    private String roleName = "Student"; // Default

    // --- Services ---
    private services authService;
    private loggerService logger;

    // --- UI Components ---
    private JPasswordField currentPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    private JButton submitButton;
    private JButton cancelButton;

    // --- Styling Colors ---
    private Color bgColor = new Color(45, 45, 45);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color textColor = Color.WHITE;
    private Color buttonColor = new Color(57, 174, 168);
    private Color textFieldBgColor = new Color(60, 60, 60);

    /**
     * Creates the dialog.
     * @param parent The parent frame (StudentProfile or FacultyProfile).
     * @param userId The 'username', 'rollNumber', or 'facultyId'.
     */
    public ChangePasswordDialog(Frame parent, String userId) {
        super(parent, "Change Password", true);
        this.userId = userId;
        this.authService = new services();
        this.logger = new loggerService();

        // --- 1. Detect Role based on Parent Window ---
        if (parent instanceof FacultyProfile) {
            this.isFaculty = true;
            this.roleName = "Faculty";
            setTitle("Change Faculty Password");
        } else if (parent instanceof StudentProfile) {
            this.isFaculty = false;
            this.roleName = "Student";
            setTitle("Change Student Password");
        }

        // --- Dialog Properties ---
        setSize(450, 320);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(mainPanelColor);
        setLayout(new BorderLayout());

        // --- Main Panel ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Info Label ---
        JLabel userLabel = createLabel("User: " + userId + " (" + roleName + ")");
        userLabel.setForeground(new Color(179, 179, 179));
        userLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        // --- Current Password Row ---
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(createLabel("Current Password:"), gbc);

        currentPassField = createPasswordField();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(currentPassField, gbc);

        // --- New Password Row ---
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(createLabel("New Password:"), gbc);

        newPassField = createPasswordField();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(newPassField, gbc);

        // --- Confirm Password Row ---
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(createLabel("Confirm Password:"), gbc);

        confirmPassField = createPasswordField();
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(confirmPassField, gbc);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        cancelButton = createButton("Cancel");
        submitButton = createButton("Submit");

        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets.top = 20;
        panel.add(buttonPanel, gbc);

        // --- Action Listeners ---
        submitButton.addActionListener(e -> handleSubmit());
        cancelButton.addActionListener(e -> dispose());
    }

    /**
     * Handles the submit logic with role-based branching.
     */
    private void handleSubmit() {
        String currentPass = new String(currentPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        // 1. Validate Empty
        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Validate Match
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean verificationSuccess;
        boolean updateSuccess;

        // 3. Branching Logic (Faculty vs Student)
        if (isFaculty) {
            // --- FACULTY LOGIC ---
            verificationSuccess = authService.verifyFacultyPassword(userId, currentPass);
            if (!verificationSuccess) {
                JOptionPane.showMessageDialog(this, "Incorrect current password.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
                logger.log(userId, "Password Reset", "Faculty failed auth check", "Admin");
                return;
            }
            updateSuccess = authService.updateFacultyPassword(userId, newPass);
        } else {
            // --- STUDENT LOGIC ---
            verificationSuccess = authService.verifyCurrentPassword(userId, currentPass);
            if (!verificationSuccess) {
                JOptionPane.showMessageDialog(this, "Incorrect current password.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
                logger.log(userId, "Password Reset", "Student failed auth check", "Admin");
                return;
            }
            updateSuccess = authService.updatePassword(userId, newPass);
        }

        // 4. Final Result
        if (updateSuccess) {
            JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            logger.log(userId, "Password Reset", roleName + " password updated", "Admin");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Database error. Could not update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Helpers ---
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField(15);
        pf.setBackground(textFieldBgColor);
        pf.setForeground(textColor);
        pf.setCaretColor(textColor);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.brighter()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return pf;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }
}