package ui.auth;

import javax.swing.*;
import javax.swing.border.Border;
import java.sql.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import ui.landing.LandingFrame;
import ui.dashboard.StudentDashboard;

import middleware.services;
import middleware.loggerService;
import middleware.maintenanceService;

/**
 * The initial login window for the application.
 * This class builds the UI for the login screen using the new design.
 */
public class StudentLoginFrame extends JFrame {

    private services studentService;
    private maintenanceService maintenance;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private RoundedButton loginButton;
    private RoundedButton backButton;
    private RoundedButton forgetButton;

    // --- NEW UI COLOR PALETTE ---
    private Color bgColor = new Color(41, 47, 61);        // --background: 220 18% 20%
    private Color fgColor = new Color(255, 255, 255);     // --foreground: 0 0% 100%
    private Color cardColor = new Color(54, 59, 74);      // --card: 220 15% 25%
    private Color mutedFgColor = new Color(179, 179, 179);  // --muted-foreground: 0 0% 70%
    private Color primaryColor = new Color(52, 159, 148);   // --primary: 177 51% 42%
    private Color primaryGlowColor = new Color(79, 196, 184); // --primary-glow: 177 51% 52%
    private Color secondaryColor = new Color(64, 69, 89);   // --secondary / --border: 220 15% 30%
    private Color inputBgColor = new Color(41, 47, 61);     // --background (for contrast)
    private loggerService logger;

    private int numTry = 3;

    public StudentLoginFrame() {
        super("Student Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(2160, 1080); // A more modern window size
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        this.studentService = new services();
        this.logger = new loggerService();
        this.maintenance = new maintenanceService();

        initComponents();
        layoutComponents();
    }

    /**
     * Initializes all the UI components (labels, fields, buttons).
     */
    private void initComponents() {
        // --- Text Fields Styling ---
        int fieldArc = 8;
        int fieldPadding = 12;
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

        Border roundedBorder = new RoundedBorder(fieldArc, 1, secondaryColor);
        Border paddingBorder = BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding);

        usernameField = new JTextField(20);
        usernameField.setBackground(inputBgColor);
        usernameField.setForeground(fgColor);
        usernameField.setCaretColor(fgColor);
        usernameField.setFont(fieldFont);
        usernameField.setBorder(BorderFactory.createCompoundBorder(roundedBorder, paddingBorder));

        passwordField = new JPasswordField(20);
        passwordField.setBackground(inputBgColor);
        passwordField.setForeground(fgColor);
        passwordField.setCaretColor(fgColor);
        passwordField.setFont(fieldFont);
        passwordField.setBorder(BorderFactory.createCompoundBorder(roundedBorder, paddingBorder));


        // --- Button Styling (using RoundedButton) ---

        // "Login" button (Gradient)
        loginButton = new RoundedButton(
                "Login",
                primaryColor.darker(),       // Gradient Start
                primaryGlowColor,   // Gradient End
                8                   // Arc
        );
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setForeground(fgColor);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // "Back" button (Solid)
        backButton = new RoundedButton(
                "Back",
                secondaryColor,         // Normal
                secondaryColor.brighter(), // Hover
                secondaryColor.darker(),  // Pressed
                8                         // Arc
        );
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setForeground(fgColor);
        backButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        forgetButton = new RoundedButton(
                "Forget Password",
                primaryColor.darker(),
                primaryGlowColor,
                8
        );

        // --- Action Listeners ---
        loginButton.addActionListener(e -> {
            if(maintenance.isMaintenanceActive()){
                JOptionPane.showMessageDialog(this,
                        "Maintenance is active. Please try later",
                        "Maintenance",
                        JOptionPane.ERROR_MESSAGE);
            }else{
                if (numTry > 0) {
                    try {
                        handleLoginAttempt();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    handleLock();
                }
            }
        });

        backButton.addActionListener(e -> {
            LandingFrame frame = new LandingFrame();
            frame.setVisible(true);
            dispose();
        });

        forgetButton.addActionListener(e -> {
            if(maintenance.isMaintenanceActive()){
                JOptionPane.showMessageDialog(this,
                        "Maintenance is active. Please try later",
                        "Maintenance",
                        JOptionPane.ERROR_MESSAGE);
            }else{
                ForgetPassword frame = new ForgetPassword("student");
                frame.setVisible(true);
                dispose();
            }
        });
    }

    /**
     * Sets the layout manager and adds all components to the frame.
     */
    private void layoutComponents() {
        // Use GridBagLayout on the JFrame to center the card panel
        setLayout(new GridBagLayout());

        // --- The Main Card Panel (using RoundedPanel) ---
        RoundedPanel cardPanel = new RoundedPanel(15, cardColor, cardColor, 0);
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // This single constraint centers the cardPanel in the JFrame
        add(cardPanel, new GridBagConstraints());

        // --- Layout INSIDE the cardPanel ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Make all components span 2 columns

        // 1. Title: "Student Login"
        JLabel titleLabel = new JLabel("Student Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(fgColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0); // Bottom padding
        cardPanel.add(titleLabel, gbc);

        // 2. Subtitle: "Enter your credentials..."
        JLabel subtitleLabel = new JLabel("Enter your credentials to access the ERP system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(mutedFgColor);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0); // Large bottom padding
        cardPanel.add(subtitleLabel, gbc);

        // 3. "Username" Label
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(fgColor);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0); // Bottom padding
        cardPanel.add(userLabel, gbc);

        // 4. Username Field
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0); // Bottom padding
        cardPanel.add(usernameField, gbc);

        // 5. "Password" Label
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passLabel.setForeground(fgColor);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0); // Bottom padding
        cardPanel.add(passLabel, gbc);

        // 6. Password Field
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 30, 0); // Large bottom padding
        cardPanel.add(passwordField, gbc);

        // 7. Button Panel (for side-by-side buttons)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 row, 2 cols, 10px h-gap
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 25, 0); // Bottom padding
        cardPanel.add(buttonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        JPanel buttonPanel1 = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel1.setOpaque(false);
        buttonPanel1.add(forgetButton);
        cardPanel.add(buttonPanel1, gbc);
    }


    private void handleLoginAttempt() throws SQLException {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(username,"Login Attempt" ,"User attempted with invalid credentials");
            return;
        }

        String rollNumber = studentService.loginStudent(username, password);
        if (rollNumber != null) {
            StudentDashboard dashboard = new StudentDashboard(rollNumber, username);
            dashboard.setVisible(true);
            dispose();
            logger.log(username,"Login Attempt" ,"User logged in");

        } else {
            JOptionPane.showMessageDialog(this, "Incorrect username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            numTry -= 1;
            logger.log(username,"Login Attempt" ,"User attempted with invalid credentials");
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
        logger.log("Student","Login Attempt" ,"User attempted with invalid credentials locked login attempt");


        Timer lockoutTimer = new Timer(30000, e -> {
            numTry = 3;
            loginButton.setEnabled(true);
            usernameField.setEnabled(true);
            passwordField.setEnabled(true);
            setTitle("Student Login");
        });

        lockoutTimer.setRepeats(false);
        lockoutTimer.start();
    }


    private static class RoundedBorder implements Border {
        private int radius;
        private int thickness;
        private Color color;

        public RoundedBorder(int radius, int thickness, Color color) {
            this.radius = radius;
            this.thickness = thickness;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            // Draw the rounded rectangle
            g2.draw(new RoundRectangle2D.Float(
                    x + thickness / 2.0f,
                    y + thickness / 2.0f,
                    width - thickness,
                    height - thickness,
                    radius, radius
            ));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            // This border itself doesn't add padding, we use a CompoundBorder for that
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }
}