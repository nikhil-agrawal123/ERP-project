package ui.landing;

import ui.auth.AdminLoginFrame;
import ui.auth.FacultyLoginFrame;
import ui.auth.ParentLoginFrame;
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
        setSize(2160, 1080);
        setLocationRelativeTo(null); // C
        // enter the window
        setResizable(false);
        // --- UI Components ---
        JLabel welcomeLabel = new JLabel("Welcome to the University Management System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoLabel = new JLabel("Click the button below to proceed to the login portal.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        JButton StudentButton = new JButton("Student Login");
        StudentButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton FacultyButton = new JButton("Faculty Login");
        FacultyButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton AdminButton = new JButton("Admin Login");
        AdminButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton ParentButton = new JButton("Parent Login");
        ParentButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Dimension buttonSize = new Dimension(160, 44);
        StudentButton.setPreferredSize(buttonSize);
        FacultyButton.setPreferredSize(buttonSize);
        AdminButton.setPreferredSize(buttonSize);
        ParentButton.setPreferredSize(buttonSize);
        StudentButton.setMaximumSize(buttonSize);
        FacultyButton.setMaximumSize(buttonSize);
        AdminButton.setMaximumSize(buttonSize);
        ParentButton.setMaximumSize(buttonSize);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // vertical glue before content to push it to vertical center
        centerPanel.add(Box.createVerticalGlue());

        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        centerPanel.add(infoLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 24)));

        // --- Button row: use FlowLayout to ensure proper horizontal centering ---
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)); // gap = 16 px
        rowPanel.setOpaque(false); // let centerPanel background show through if needed

        rowPanel.add(StudentButton);
        rowPanel.add(FacultyButton);
        rowPanel.add(AdminButton);
        rowPanel.add(ParentButton);

        // Important: center the rowPanel inside the centerPanel
        rowPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(rowPanel);

        // vertical glue after content to complete centering
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);

        // --- Action Listener ---
        StudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentLoginFrame studentLoginFrame = new StudentLoginFrame();
                studentLoginFrame.setVisible(true);
                dispose();
            }
        });

        FacultyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FacultyLoginFrame facultyLoginForm = new FacultyLoginFrame();
                facultyLoginForm.setVisible(true);
                dispose();
            }
        });

        AdminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminLoginFrame adminLoginFrame = new AdminLoginFrame();
                adminLoginFrame.setVisible(true);
                dispose();
            }
        });

        ParentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ParentLoginFrame parentLoginFrame = new ParentLoginFrame();
                parentLoginFrame.setVisible(true);
                dispose();
            }
        });
    }

    // quick main to test the frame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LandingFrame lf = new LandingFrame();
            lf.setVisible(true);
        });
    }
}