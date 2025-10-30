package ui.auth;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import dependancy.org.mindrot.jbcrypt.BCrypt;
import ui.landing.LandingFrame;
import java.util.Random;
import middleware.*;

public class ForgetPassword extends JFrame{
    private JTextField userEmail;
    private JButton submitButton;
    private JButton backButton;
    Color backgroundColor = new Color(45, 45, 45);
    Color buttonColor = new Color(57, 174, 168);
    Color textColor = Color.WHITE;
    private final Color textFieldBgColor = new Color(60, 60, 60);
    private boolean success = true;
    private String user;

    private services passreset;

    public ForgetPassword(String userType){
        super("Forget Password");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.user =  userType;
        setSize(1080, 1080);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(backgroundColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        initComponents(userType);

        this.passreset = new services();
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
