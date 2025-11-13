package ui.auth;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.*;

import dependancy.org.mindrot.jbcrypt.BCrypt;
import ui.landing.LandingFrame;
import java.util.Random;
import middleware.services;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import java.awt.geom.RoundRectangle2D;


public class ForgetPassword extends JFrame{
    private JTextField userEmail;
    // --- Use RoundedButton ---
    private RoundedButton submitButton;
    private RoundedButton backButton;

    // --- NEW UI COLOR PALETTE ---
    private Color bgColor = new Color(41, 47, 61);        // --background: 220 18% 20%
    private Color fgColor = new Color(255, 255, 255);     // --foreground: 0 0% 100%
    private Color cardColor = new Color(54, 59, 74);      // --card: 220 15% 25%
    private Color mutedFgColor = new Color(179, 179, 179);  // --muted-foreground: 0 0% 70%
    private Color primaryColor = new Color(52, 159, 148);   // --primary: 177 51% 42%
    private Color primaryGlowColor = new Color(79, 196, 184); // --primary-glow: 177 51% 52%
    private Color secondaryColor = new Color(64, 69, 89);   // --secondary / --border: 220 15% 30%
    private Color inputBgColor = new Color(41, 47, 61);     // --background (for contrast)

    private boolean success = true;
    private String user;

    private services passreset;

    public ForgetPassword(String userType){
        super("Forget Password");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.user =  userType;
        setSize(2160, 1080); // Match login frame size
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(bgColor); // Use new bg color
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        this.passreset = new services(); // Initialize service

        initComponents(userType);
        layoutComponents();
    }

    private void updateDatabase(String newHash, String userType) {
        boolean reset = passreset.forgetPass(userEmail.getText(),userType,newHash);
        if (reset) {
            JFrame frame = new JFrame();
            frame.setSize(300,200);
            frame.setLocationRelativeTo(null);

            Object[] option = {"Proceed to login"};
            int choice = JOptionPane.showOptionDialog(
                    frame,
                    "Password updated Succesfully",
                    "Success",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    option,
                    option[0]
            );

            if(choice == 0){
                LandingFrame landingFrame = new LandingFrame();
                landingFrame.setVisible(true);
                dispose();
            }else{
                LandingFrame landingFrame = new LandingFrame();
                landingFrame.setVisible(true);
                dispose();
            }
            frame.dispose();

            System.out.println("Password reset successful");
        } else {
            success = false;
            System.out.println("Update failed, no rows were changed.");
        }
    }

    private String passwordGen(){
        // This helper method is unchanged
        StringBuilder password = new StringBuilder();
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%&";
        Random rand = new Random();
        for(int i = 0; i < 8; i++){
            int index = rand.nextInt(alphanumeric.length());
            password.append(alphanumeric.charAt(index));
        }
        return password.toString();
    }

    private void initComponents(String userType){
        // --- Text Fields Styling ---
        int fieldArc = 8;
        int fieldPadding = 12;
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);

        Border roundedBorder = new RoundedBorder(fieldArc, 1, secondaryColor);
        Border paddingBorder = BorderFactory.createEmptyBorder(fieldPadding, fieldPadding, fieldPadding, fieldPadding);

        userEmail = new JTextField(20);
        userEmail.setBackground(inputBgColor);
        userEmail.setForeground(fgColor);
        userEmail.setCaretColor(fgColor);
        userEmail.setFont(fieldFont);
        userEmail.setBorder(BorderFactory.createCompoundBorder(roundedBorder, paddingBorder));

        // --- Button Styling (using RoundedButton) ---
        submitButton = new RoundedButton(
                "Submit",
                primaryColor.darker(),       // Gradient Start
                primaryGlowColor,   // Gradient End
                8                   // Arc
        );
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitButton.setForeground(fgColor);
        submitButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

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


        // --- Action Listeners ---
        submitButton.addActionListener(actionEvent -> {
            String email = userEmail.getText();
            if(email.isEmpty()){
                JOptionPane.showMessageDialog(null, "Please fill the field");
            }else{
                String temp = passwordGen();
                String password = BCrypt.hashpw(temp, BCrypt.gensalt());
                System.out.println("new pass:" + temp);
                updateDatabase(password,userType);
                if(!success){
                    JOptionPane.showMessageDialog(null, "Password reset failed enter a valid EmailId");
                }
            }
        });

        backButton.addActionListener(actionEvent -> {
            LandingFrame  frame = new LandingFrame();
            frame.setVisible(true);
            dispose();
        });
    }

    /**
     * Re-laout the components to match the FacultyLoginFrame style.
     */
    private void layoutComponents(){
        // Use GridBagLayout on the JFrame to center the card panel
        setLayout(new GridBagLayout());

        // --- The Main Card Panel (using RoundedPanel) ---
        RoundedPanel cardPanel = new RoundedPanel(15, cardColor, cardColor, 0);
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        add(cardPanel, new GridBagConstraints()); // Center the card

        // --- Layout INSIDE the cardPanel ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Make all components span 2 columns

        // 1. Title
        JLabel titleLabel = new JLabel("Reset Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(fgColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(titleLabel, gbc);

        // 2. Subtitle
        JLabel subtitleLabel = new JLabel("Enter your account email to reset your password");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(mutedFgColor);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        cardPanel.add(subtitleLabel, gbc);

        // 3. "User Email" Label
        JLabel userLabel = new JLabel("User Email");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(fgColor);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        cardPanel.add(userLabel, gbc);

        // 4. Email Field
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 30, 0); // More space after field
        cardPanel.add(userEmail, gbc);

        // 5. Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        gbc.gridy = 4; // Changed from 6
        gbc.insets = new Insets(0, 0, 0, 0); // No bottom padding after buttons
        cardPanel.add(buttonPanel, gbc);
    }

    /**
     * A custom Border class for rounded text fields.
     * Copied from FacultyLoginFrame.
     */
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
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }
}