package ui.auth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import ui.landing.LandingFrame;


public class FacultyLoginForm extends JFrame {

    private JTextField FacultyName;
    private JTextField FacultyCode;
    private JTextField username;
    private JPasswordField password;
    private JButton login;
    private JButton backButton;

    public FacultyLoginForm() {
        super("Faculty ERP Login");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(2100, 1080);
        setLocationRelativeTo(null);
        setResizable(false);

    }

    private void initComponents() {
        username = new JTextField();
        password = new JPasswordField();
        FacultyCode = new JTextField();
        FacultyName = new JTextField();
        login = new JButton("Login");
        backButton = new JButton("Back");

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLoginAttempt();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LandingFrame landingFrame = new LandingFrame();
                landingFrame.setVisible(true);
                dispose();
            }
        });
    }

    private void handleLoginAttempt() {
        String message = "Incorrect username or password";
        JOptionPane.showMessageDialog(this,
                message, "login information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
    }

}
