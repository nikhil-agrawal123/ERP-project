package ui.auth;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import dependancy.org.mindrot.jbcrypt.BCrypt;
import ui.landing.LandingFrame;

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
            String password = BCrypt.hashpw("Temporary for now", BCrypt.gensalt());
            System.out.println(password);
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
