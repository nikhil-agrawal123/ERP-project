package ui.landing;

import com.sun.tools.javac.Main;
import ui.auth.AdminLoginFrame;
import ui.auth.FacultyLoginFrame;
import ui.auth.ParentLoginFrame;
import ui.auth.StudentLoginFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * A welcome screen that serves as the initial landing page for the application.
 * It provides a button to proceed to the login screen.
 */
public class LandingFrame extends JFrame {

    public LandingFrame() {
        super("Welcome - University ERP");
        Color backgroundColor = new Color(45, 45, 45);
        Color buttonColor = new Color(57, 174, 168);
        Color textColor = Color.WHITE;
        Color borderColor = new Color(150, 150, 150);
        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 1080);
        setLocationRelativeTo(null);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());
        // enter the window
        setResizable(false);
        // --- UI Components ---
        JLabel welcomeLabel = new JLabel("Welcome to IIITD ERP");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setForeground(textColor);
        JLabel infoLabel = new JLabel("Click the button below to proceed to the login portal.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setForeground(textColor);



        JButton StudentButton = new JButton("Student Login");
        JButton FacultyButton = new JButton("Faculty Login");
        JButton AdminButton = new JButton("Admin Login");
        JButton ParentButton = new JButton("Parent Login");

        Border buttonBorder = BorderFactory.createLineBorder(borderColor, 1);

        JButton[] buttons = {StudentButton, FacultyButton, AdminButton, ParentButton};
        for (JButton button : buttons) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setBackground(buttonColor);
            button.setForeground(textColor);
            button.setFocusPainted(false);
//            button.setBorderPainted(false); // For a flatter, modern look
            button.setOpaque(true);
            button.setBorder(buttonBorder);// Necessary for background color to show on some systems
            Dimension buttonSize = new Dimension(160, 44);
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
        }


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(backgroundColor);
        // vertical glue before content to push it to vertical center
        centerPanel.add(Box.createVerticalGlue());

        ImageIcon originalIcon = new ImageIcon("resources/logo.jpg"); // your path
        int logoWidth = 300;
        int logoHeight = 150;
        Image scaledImage = originalIcon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

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