package ui.landing;

import ui.auth.AdminLoginFrame;
import ui.auth.FacultyLoginFrame;
import ui.auth.ParentLoginFrame;
import ui.auth.StudentLoginFrame;
import ui.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

/**
 * A welcome screen that serves as the initial landing page for the application.
 * It provides a button to proceed to the login screen.
 */
public class LandingFrame extends JFrame {

    // --- UI COLOR PALETTE ---
    private Color bgColor = new Color(42, 48, 60);            // --background
    private Color sideMenuColor = new Color(48, 54, 70);      // --sidebar-background
    private Color cardColor = new Color(54, 59, 74);          // --card
    private Color borderColor = new Color(64, 69, 89);        // --border
    private Color buttonColor = new Color(52, 159, 148);      // --primary / --accent
    private Color buttonColorGlow = new Color(79, 196, 184);  // --primary-glow
    private Color textColor = new Color(255, 255, 255);       // --foreground
    private Color textSecondaryColor = new Color(179, 179, 179); // --muted-foreground

    public LandingFrame() {
        super("Welcome - Student ERP System");

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(2160, 1080); // Adjusted size for a landing modal
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor); // Set background color
        setResizable(false);

        // --- Set Frame Icon ---
        try {
            ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
            setIconImage(image.getImage());
        } catch (Exception e) {
            System.err.println("Error loading logo.jpg: " + e.getMessage());
        }

        // --- UI Components ---

        // Main content panel with vertical layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(bgColor); // Use the main background color

        // Add flexible space before content to push it to the center
        centerPanel.add(Box.createVerticalGlue());

        // --- Logo ---
        JLabel logoLabel = new JLabel();
        try {
            URL logoUrl = getClass().getResource("/logo.jpg");
            if (logoUrl != null) {
                ImageIcon originalIcon = new ImageIcon(logoUrl);
                int logoWidth = 400;
                int logoHeight = 200;
                Image scaledImage = originalIcon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                logoLabel.setText("Logo"); // Fallback text
            }
        } catch (Exception e) {
            logoLabel.setText("Logo");
            System.err.println("Error loading logo.jpg: " + e.getMessage());
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Title Label ---
        JLabel welcomeLabel = new JLabel("Student ERP System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36)); // Larger font
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setForeground(textColor);
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // --- Subtitle Label ---
        JLabel infoLabel = new JLabel("Manage your academic journey with our comprehensive ERP platform");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setForeground(textSecondaryColor); // Muted color
        centerPanel.add(infoLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- Create Buttons ---
        RoundedButton StudentButton = createPrimaryButton("Student Login");
        RoundedButton FacultyButton = createPrimaryButton("Faculty Login");
        RoundedButton AdminButton = createPrimaryButton("Admin Login");
        RoundedButton ParentButton = createPrimaryButton("Parent Login");

        // --- Button Row Panel ---
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0)); // 16px horizontal gap
        rowPanel.setOpaque(false); // Make transparent
        rowPanel.add(StudentButton);
        rowPanel.add(FacultyButton);
        rowPanel.add(AdminButton);
        rowPanel.add(ParentButton);

        // Set max size to prevent stretching in wider windows (though frame is fixed)
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        centerPanel.add(rowPanel);

        // Add flexible space after content
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);

        // --- Action Listeners ---
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

    /**
     * Creates a primary (gradient) button.
     */
    private RoundedButton createPrimaryButton(String text) {
        RoundedButton button = new RoundedButton(
                text,
                buttonColor.darker(),      // Gradient Start
                buttonColor,  // Gradient End
                8                 // Arc
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        Dimension buttonSize = new Dimension(160, 60);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        return button;
    }


    // quick main to test the frame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LandingFrame lf = new LandingFrame();
            lf.setVisible(true);
        });
    }

}