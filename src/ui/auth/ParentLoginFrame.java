package ui.auth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import middleware.*;
import ui.dashboard.ParentDashboard;
import ui.landing.LandingFrame;
// --- NEW IMPORT ---


public class ParentLoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton BackButton;
    private JButton ForgetButton;
    Color backgroundColor = new Color(45, 45, 45);
    Color buttonColor = new Color(57, 174, 168);
    Color textColor = Color.WHITE;
    private Color textFieldBgColor = new Color(60, 60, 60);
    private int numTry = 3;

    private services parentLogin;

    public ParentLoginFrame() {
        super("Parent Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 1080);
        // This centers the window on the screen.
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(backgroundColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        this.parentLogin = new services();

        initComponents();
        layoutComponents();

    }

    private void initComponents() {
        // ... (initComponents code is unchanged) ...
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
        ForgetButton = new JButton("Forget Password");
        JButton[] buttons = {loginButton, BackButton, ForgetButton};
        for (JButton button : buttons) {
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        }

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(numTry > 0){
                    handleLoginAttempt();
                }else{
                    handleLock();
                }
            }
        });

        BackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LandingFrame landingFrame = new LandingFrame();
                landingFrame.setVisible(true);
                dispose();
            }
        });

        ForgetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ForgetPassword pane = new ForgetPassword("parent");
                pane.setVisible(true);
                dispose();
            }
        });
    }


    /**
     * Sets the layout manager and adds all components to the frame.
     */
    private void layoutComponents() {
        // ... (layoutComponents code is unchanged) ...
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Insets add padding around components.
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("Welcome to Parent Login");
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

        // --- MODIFIED LINE: Hardcoded login check ---
        boolean loginSuccess = username.equals("a") && password.equals("a");
        // --- END OF MODIFICATION ---


        // --- This block is the same as before ---
        if (loginSuccess) {
            System.out.println("Password correct for " + username + ". Opening face verification...");

            // 1. Define what to do on *successful verification*
            // This is a "lambda expression"
            Runnable onVerificationSuccess = () -> {
                ParentDashboard dashboard = new ParentDashboard(username);
                dashboard.setVisible(true);
            };

        } else {
            JOptionPane.showMessageDialog(this,
                    "Incorrect username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            numTry -= 1;
        }
        // --- END OF MODIFIED BLOCK ---
    }

    public void handleLock() {
        // ... (handleLock code is unchanged) ...
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