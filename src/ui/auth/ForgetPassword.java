package ui.auth;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import databaseConfig.Connector;
import dependancy.org.mindrot.jbcrypt.BCrypt;
import ui.landing.LandingFrame;
import java.util.Random;

public class ForgetPassword extends JFrame{
    private JTextField userEmail;
    private JButton submitButton;
    private JButton backButton;
    Color backgroundColor = new Color(45, 45, 45);
    Color buttonColor = new Color(57, 174, 168);
    Color textColor = Color.WHITE;
    private Color textFieldBgColor = new Color(60, 60, 60);

    public ForgetPassword(){
        super("Forget Password");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 1080);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(backgroundColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        initComponents();
        layoutComponents();
    }

    private void updateDatabase(String newHash) {
        String getUserIdSQL = "SELECT studentId FROM users.student WHERE studentEmail = ?";
        String updatePassSQL = "UPDATE auth.studentAuth SET studentPass = ? WHERE studentId = ?";

        Connector dbConnector = new Connector();

        try (Connection conn = dbConnector.connect()) {
            // Prepare and execute the query to find the user
            PreparedStatement preparedStatement = conn.prepareStatement(getUserIdSQL);
            preparedStatement.setString(1, userEmail.getText());
            ResultSet rs = preparedStatement.executeQuery();

            // FIX: Check if a result was found AND move the cursor to the first row.
            if (rs.next()) {
                // Now that the cursor is on a valid row, we can get the studentId.
                String studentId = rs.getString("studentId");
                System.out.println("Found student ID: " + studentId);

                // Proceed with the update
                PreparedStatement ps = conn.prepareStatement(updatePassSQL);
                ps.setString(1, newHash);
                ps.setString(2, studentId);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Password reset successful");
                } else {
                    System.out.println("Update failed, no rows were changed.");
                }

            } else {
                // Handle the case where the email was not found in the database.
                System.out.println("No user found with the email: " + userEmail.getText());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String passwordGen(){
        StringBuilder password = new StringBuilder();
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        for(int i = 0; i < 5; i++){
            int index = rand.nextInt(alphanumeric.length());
            password.append(alphanumeric.charAt(index));
        }
        return password.toString();
    }

    private void initComponents(){
        userEmail = new JTextField(20);
        userEmail.setBackground(textFieldBgColor);
        userEmail.setForeground(textColor);
        userEmail.setCaretColor(textColor);
        userEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        submitButton = new JButton("Submit");
        backButton = new JButton("Back");

        JButton[] buttons = {submitButton,backButton};

        for (JButton button : buttons) {
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        }

        submitButton.addActionListener(actionEvent -> {
            String email = userEmail.getText();
            String temp = passwordGen();
            String password = BCrypt.hashpw(temp, BCrypt.gensalt());
            System.out.println("new pass:" + temp);
            updateDatabase(password);
            if(email.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(null, "Please fill the field");
            }
        });

        backButton.addActionListener(actionEvent -> {
            LandingFrame  frame = new LandingFrame();
            frame.setVisible(true);
            dispose();
        });
    }

    private void layoutComponents(){
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Label userLabel = new Label("User Email:");
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
        gbc.gridwidth =2;
        add(userEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Span two columns.
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(backButton, gbc);
    }
}
