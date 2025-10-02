package ui.auth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ui.landing.LandingFrame;

/**
 * The initial login window for the application.
 * This class builds the UI for the login screen. The backend logic will be
 * connected later.
 */
public class StudentLoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton BackButton;

    public StudentLoginFrame() {
        super("University ERP - Login");

        // --- 1. Frame Setup ---
        // This ensures the application exits when the window is closed.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(2100, 1080);
        // This centers the window on the screen.
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();

        layoutComponents();
    }

    /**
     * Initializes all the UI components (labels, fields, buttons).
     */
    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        BackButton = new JButton("Back");

        // Add a listener to the login button to handle clicks.
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLoginAttempt();
            }
        });

        BackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LandingFrame frame = new LandingFrame();
                frame.setVisible(true);
                dispose();
            }
        });
    }

    /**
     * Sets the layout manager and adds all components to the frame.
     */
    private void layoutComponents() {
        // We use a GridBagLayout for a clean, form-like structure.
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Insets add padding around components.
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("University ERP Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Make the title span two columns.
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // --- Username Row ---
        gbc.gridwidth = 1; // Reset to one column for subsequent components.
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(usernameField, gbc);

        // --- Password Row ---
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(passwordField, gbc);

        // --- Login Button ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Span two columns.
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // --- Back Button ----
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(BackButton, gbc);
    }

    /**
     * This method is called when the login button is clicked.
     * For now, it's a placeholder to show the UI is interactive.
     */
    private void handleLoginAttempt() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Display the captured input in a dialog box.
        // This is a placeholder for the real authentication logic.
        String testUserName = "nikhil";
        String testPassword = "nikhil";
        if(password.equals(testPassword) &&  username.equals(testUserName)) {
            String message = "Login attempt with:\nUsername: " + username + "\nPassword: " + password;
            JOptionPane.showMessageDialog(this,
                    message,
                    "Login Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }else {
            String message = "Incorrect username or password";
            JOptionPane.showMessageDialog(this,
                    message, "login information", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}