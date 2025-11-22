package ui.dashboard;

import middleware.services;

import javax.swing.*;
import java.awt.*;
import middleware.loggerService;

/**
 * A modal dialog window for changing a student's password.
 */
public class ChangePasswordDialog extends JDialog {

    // --- Student Auth ID ---
    private String studentId;

    // --- Service for auth logic ---
    private services authService;
    private loggerService logger;

    // --- UI Components ---
    private JPasswordField currentPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    private JButton submitButton;
    private JButton cancelButton;

    // --- Styling Colors (from your other files) ---
    private Color bgColor = new Color(45, 45, 45);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color textColor = Color.WHITE;
    private Color buttonColor = new Color(57, 174, 168);
    private Color textFieldBgColor = new Color(60, 60, 60);

    /**
     * Creates the dialog.
     * @param parent    The parent frame (StudentProfile), so this dialog is modal.
     * @param studentId The 'username' or 'studentId' used for authentication.
     */
    public ChangePasswordDialog(Frame parent, String studentId) {
        super(parent, "Change Password", true);
        this.studentId = studentId;
        this.authService = new services();
        this.logger = new loggerService();

        // --- Dialog Properties ---
        setSize(450, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(mainPanelColor);
        setLayout(new BorderLayout());

        // --- Main Panel with GridBagLayout ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Current Password Row ---
        JLabel currentPassLabel = createLabel("Current Password:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(currentPassLabel, gbc);

        currentPassField = createPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(currentPassField, gbc);

        // --- New Password Row ---
        JLabel newPassLabel = createLabel("New Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(newPassLabel, gbc);

        newPassField = createPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(newPassField, gbc);

        // --- Confirm Password Row ---
        JLabel confirmPassLabel = createLabel("Confirm New Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(confirmPassLabel, gbc);

        confirmPassField = createPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
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
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets.top = 20; // Add space above buttons
        panel.add(buttonPanel, gbc);

        // --- Action Listeners ---
        submitButton.addActionListener(e -> handleSubmit(studentId));
        cancelButton.addActionListener(e -> dispose());
    }

    /**
     * Handles the logic when the "Submit" button is clicked.
     */
    private void handleSubmit(String username) {
        String currentPass = new String(currentPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        // 1. Validate: Check for empty fields
        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(username,"Password Reset" ,"Student Tried resetting password");
            return;
        }

        // 2. Validate: Check if new passwords match
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this,
                    "New passwords do not match.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(username,"Password Reset" ,"Student Tried resetting password but failed");

            return;
        }

        // 3. Logic: Verify the current password
        if (!authService.verifyCurrentPassword(studentId, currentPass)) {
            JOptionPane.showMessageDialog(this,
                    "Incorrect current password.",
                    "Authentication Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(username,"Password Reset" ,"Student Tried resetting - Current Password Error");

            return;
        }

        // 4. Logic: Update the password
        if (authService.updatePassword(studentId, newPass)) {
            JOptionPane.showMessageDialog(this,
                    "Password updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            logger.log(username,"Password Reset" ,"Student Tried resetting successfully");

        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update password. A database error occurred.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(username,"Password Reset" ,"Student Tried resetting password but failed");

        }
    }

    // --- Helper methods for styling ---

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