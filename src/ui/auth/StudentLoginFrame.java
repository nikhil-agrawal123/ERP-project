package ui.auth;

import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import dependancy.org.mindrot.jbcrypt.BCrypt;
import ui.landing.LandingFrame;
import databaseConfig.Connector;
import ui.dashboard.StudentDashboard;
import java.time.*;

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
    private JButton ForgetButton;
    Color backgroundColor = new Color(45, 45, 45);
    Color buttonColor = new Color(57, 174, 168);
    Color textColor = Color.WHITE;
    Color borderColor = new Color(150, 150, 150);
    private Color textFieldBgColor = new Color(60, 60, 60);

    private int numTry = 3;

    public StudentLoginFrame() {
        super("Student Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 1080);
        // This centers the window on the screen.
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(backgroundColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        initComponents();
        layoutComponents();
    }

    /**
     * Initializes all the UI components (labels, fields, buttons).
     */
    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JTextField[] textFields = {usernameField, passwordField};

        for (JTextField field : textFields) {
            field.setBackground(textFieldBgColor);
            field.setForeground(textColor);
            field.setCaretColor(textColor);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
        loginButton = new JButton("Login");
        BackButton = new JButton("Back");
        ForgetButton = new JButton("Forget");
        JButton[] buttons = {loginButton, BackButton, ForgetButton};
        for (JButton button : buttons) {
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        }
        // Add a listener to the login button to handle clicks.
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(numTry > 0){
                    handleLoginAttempt();
                }else {
                    handleLock();
                }
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

        ForgetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ForgetPassword newPassword = new ForgetPassword("student");
                newPassword.setVisible(true);
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
        JLabel titleLabel = new JLabel("Welcome to Student Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Make the title span two columns.
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        // --- Username Row ---
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(textColor); // MODIFIED: Set text color
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(userLabel, gbc);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(usernameField, gbc);

        // --- Password Row ---
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(textColor); // MODIFIED: Set text color
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passLabel, gbc);

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

        gbc.gridx = 1;
        gbc.gridy = 4;
        add(ForgetButton, gbc);
    }

    /**
     * This method is called when the login button is clicked.
     */
    private void handleLoginAttempt() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT sa.studentPass, s.student_roll_no " +
                "FROM auth.studentAuth sa " +
                "JOIN users.students s ON sa.studentId = s.user_id " +
                "WHERE sa.studentId = ?";

        Connector dbConnector = new Connector();

        try (Connection conn = dbConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            // Check if a user with that username was found
            if (rs.next()) {
                // Read both columns from the single result set
                String storedHash = rs.getString("studentPass");
                String storedRollNumber = rs.getString("student_roll_no");

                if (BCrypt.checkpw(password, storedHash)) {
                    // Login successful, open the dashboard
                    StudentDashboard dashboard = new StudentDashboard(storedRollNumber, username);
                    dashboard.setVisible(true);
                    dispose();
                } else {
                    // Password does NOT match the hash
                    JOptionPane.showMessageDialog(this,
                            "Incorrect username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                    numTry -= 1;
                }
            } else {
                // No user found with that username
                JOptionPane.showMessageDialog(this,
                        "Incorrect username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "An error occurred with the database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleLock() {
        loginButton.setEnabled(false);
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);

        JOptionPane.showMessageDialog(this,
                "Too many failed attempts. Account locked for 30 seconds.",
                "Auth Error",
                JOptionPane.ERROR_MESSAGE);

        Timer lockoutTimer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e ) {
                numTry = 3;
                loginButton.setEnabled(true);
                usernameField.setEnabled(true);
                passwordField.setEnabled(true);
                setTitle("Student Login");
            }
        });

        lockoutTimer.setRepeats(false);
        lockoutTimer.start();
    }
}