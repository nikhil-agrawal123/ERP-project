package ui.dashboard;

import ui.landing.LandingFrame;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;
    private String adminID;

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public AdminDashboard(String adminID, String username) {
        super("Admin Dashboard - " + username);
        this.adminID = adminID;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(mainPanelColor);

        createMainContent(username);

        add(mainContentPanel, BorderLayout.CENTER);
        cardLayout.show(mainContentPanel, "HOME");
    }

    private void createMainContent(String username) {
        JPanel homePanel = new JPanel(new BorderLayout(0, 0));
        homePanel.setBackground(mainPanelColor);

        JPanel rightSidePanel = createRightSidePanel();
        homePanel.add(rightSidePanel, BorderLayout.EAST);

        JPanel centerAndLeftContainer = new JPanel(new BorderLayout(0, 0));
        centerAndLeftContainer.setBackground(mainPanelColor);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(welcomeLabel);

        centerAndLeftContainer.add(titlePanel, BorderLayout.NORTH);

        JPanel profilePanel = createAdminProfilePanel(username);
        centerAndLeftContainer.add(profilePanel, BorderLayout.WEST);

        JPanel functionPanel = createFunctionPanel();
        centerAndLeftContainer.add(functionPanel, BorderLayout.CENTER);

        homePanel.add(centerAndLeftContainer, BorderLayout.CENTER);

        mainContentPanel.add(homePanel, "HOME");
    }

    private JPanel createAdminProfilePanel(String username) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(mainPanelColor);
        panel.setPreferredSize(new Dimension(300, 0));

        Border lineBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY);
        Border paddingBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        panel.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        JPanel photoPanel = new JPanel();
        photoPanel.setBackground(Color.YELLOW);
        Dimension photoSize = new Dimension(150, 150);
        photoPanel.setPreferredSize(photoSize);
        photoPanel.setMaximumSize(photoSize);
        photoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Name: " + username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("ID: " + this.adminID);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setForeground(textColor);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(photoPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(idLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createFunctionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton addUserBox = createFunctionBox("Add Users");
        addUserBox.addActionListener(e -> createPlaceholderFrame("Add Users"));

        JButton manageUsersBox = createFunctionBox("Manage Users");
        manageUsersBox.addActionListener(e -> createPlaceholderFrame("Manage Users"));

        JButton manageScoresBox = createFunctionBox("Manage Scores");
        manageScoresBox.addActionListener(e -> createPlaceholderFrame("Manage Scores"));

        JButton maintenanceBox = createFunctionBox("Maintenance");
        maintenanceBox.addActionListener(e -> createPlaceholderFrame("Maintenance"));

        JButton logoutBox = createFunctionBox("Logout");
        logoutBox.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logout Successful");
            new LandingFrame().setVisible(true);
            dispose();
        });

        panel.add(addUserBox);
        panel.add(manageUsersBox);
        panel.add(manageScoresBox);
        panel.add(maintenanceBox);
        panel.add(logoutBox);

        return panel;
    }

    private JPanel createRightSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(mainPanelColor);
        panel.setPreferredSize(new Dimension(220, 0));

        Border lineBorder = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY);
        Border paddingBorder = BorderFactory.createEmptyBorder(0, 20, 20, 20);
        panel.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        JButton b1 = createStyledButton("Button 1");
        JButton b2 = createStyledButton("Button 2");
        JButton b3 = createStyledButton("Button 3");

        b1.addActionListener(e -> System.out.println("Button 1 pressed"));
        b2.addActionListener(e -> System.out.println("Button 2 pressed"));
        b3.addActionListener(e -> System.out.println("Button 3 pressed"));

        panel.add(Box.createRigidArea(new Dimension(0, 105)));

        panel.add(b1);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(b2);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(b3);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JButton createFunctionBox(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 180));
        button.setBackground(sideMenuColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(buttonColor, 1));

        return button;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Dimension buttonSize = new Dimension(200, 60);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);

        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }

    private void createPlaceholderFrame(String title) {
        JFrame placeholderFrame = new JFrame(title);
        placeholderFrame.setSize(800, 600);
        placeholderFrame.setLocationRelativeTo(this);
        placeholderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(mainPanelColor);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        label.setForeground(textColor);

        panel.add(label);

        placeholderFrame.add(panel);
        placeholderFrame.setVisible(true);
    }
}