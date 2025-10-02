package ui.landing;

import ui.auth.StudentLoginFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A welcome screen that serves as the initial landing page for the application.
 * It provides a button to proceed to the login screen.
 */
public class LandingFrame extends JFrame {

    public LandingFrame() {
        super("Welcome - University ERP");

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null); // C
        // enter the window
        setResizable(false);
        // --- UI Components ---
        JLabel welcomeLabel = new JLabel("Welcome to the University Management System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel infoLabel = new JLabel("Click the button below to proceed to the login portal.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton proceedButton = new JButton("Proceed to Login");
        proceedButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // --- Layout ---
        // Use a BorderLayout for overall structure and a JPanel with GridBagLayout for centering
        setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);

        centerPanel.add(welcomeLabel, gbc);
        centerPanel.add(infoLabel, gbc);
        centerPanel.add(proceedButton, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // --- Action Listener ---
        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the LoginFrame
                StudentLoginFrame studentLoginFrame = new StudentLoginFrame();
                studentLoginFrame.setVisible(true);

                // Close this landing frame
                dispose();
            }
        });
    }
}
