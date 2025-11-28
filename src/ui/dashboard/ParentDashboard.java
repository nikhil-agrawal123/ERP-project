// File: ui/dashboard/ParentDashboard.java

package ui.dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ParentDashboard updated to match the Modern UI design system.
 */
public class ParentDashboard extends JFrame {

    // --- NEW UI COLOR PALETTE (From StudentLoginFrame) ---
    private final Color bgColor = new Color(41, 47, 61);         // Dark Background
    private final Color cardColor = new Color(54, 59, 74);       // Lighter Panel/Card
    private final Color primaryColor = new Color(52, 159, 148);  // Teal Accent
    private final Color textColor = Color.WHITE;                 // White Text
    private final Color mutedColor = new Color(179, 179, 179);   // Grey Text
    private final Color hoverColor = new Color(64, 69, 89);      // Hover state

    // --- Layout Components ---
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private String studentName;

    public ParentDashboard(String username) {
        super("Parent Dashboard - " + username);
        this.studentName = username;

        // --- Basic Frame Setup ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);

        // Icon (Optional, handling if missing)
        try {
            ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
            setIconImage(image.getImage());
        } catch (Exception e) {
            // Logo not found, ignore
        }

        setLayout(new BorderLayout());

        // --- Side Menu ---
        JPanel sideMenu = createSideMenu();
        add(sideMenu, BorderLayout.WEST);

        // --- Main Content Area ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(bgColor);
        mainContentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Initialize Views
        initContentCards();

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createSideMenu() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(cardColor); // Use the lighter card color for sidebar
        menuPanel.setPreferredSize(new Dimension(240, 0));
        menuPanel.setBorder(new EmptyBorder(30, 20, 30, 20));

        // Dashboard Title
        JLabel title = new JLabel("ERP SYSTEM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(primaryColor);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Parent Portal");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(mutedColor);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(title);
        menuPanel.add(subtitle);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        // Navigation Buttons
        menuPanel.add(createNavButton("Dashboard", "HOME"));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(createNavButton("Grades", "GRADES"));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(createNavButton("Courses", "COURSES"));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuPanel.add(createNavButton("Fee Receipts", "RECEIPTS"));

        // Logout at bottom
        menuPanel.add(Box.createVerticalGlue());
        JButton logoutBtn = createNavButton("Logout", "LOGOUT");
        // Remove default action and add specific logout behavior
        for(java.awt.event.ActionListener al : logoutBtn.getActionListeners()) {
            logoutBtn.removeActionListener(al);
        }
        logoutBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Logout is disabled in this view."));
        menuPanel.add(logoutBtn);

        return menuPanel;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new ModernButton(text);
        btn.addActionListener(e -> {
            if (!cardName.equals("LOGOUT")) {
                cardLayout.show(mainContentPanel, cardName);
            }
        });
        return btn;
    }

    private void initContentCards() {
        // 1. Home Panel
        JPanel homePanel = new JPanel(new BorderLayout(0, 30));
        homePanel.setBackground(bgColor);

        // Header Section
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(bgColor);

        JLabel welcomeLabel = new JLabel("Welcome back, Parent");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeLabel.setForeground(textColor);

        JLabel infoLabel = new JLabel("Viewing data for Student: " + studentName + " | Roll No: 12345");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        infoLabel.setForeground(mutedColor);

        headerPanel.add(welcomeLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(infoLabel);
        homePanel.add(headerPanel, BorderLayout.NORTH);

        // Stats Grid
        JPanel statsContainer = new JPanel(new GridLayout(1, 2, 30, 0));
        statsContainer.setBackground(bgColor);
        statsContainer.setPreferredSize(new Dimension(0, 200)); // Fixed height for cards

        // --- HARDCODED DATA HERE ---
        statsContainer.add(new InfoCard("Current CGPA", "8.55", primaryColor));
        statsContainer.add(new InfoCard("Credits Earned", "4", new Color(79, 196, 184)));

        // Wrapper to keep stats at the top of center
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(bgColor);
        centerWrapper.add(statsContainer, BorderLayout.NORTH);

        homePanel.add(centerWrapper, BorderLayout.CENTER);

        // 2. Placeholder Panels for other tabs
        JPanel gradesPanel = createPlaceholderPanel("Academic Grades");
        JPanel coursesPanel = createPlaceholderPanel("Enrolled Courses");
        JPanel receiptsPanel = createPlaceholderPanel("Fee Receipts");

        // Add to CardLayout
        mainContentPanel.add(homePanel, "HOME");
        mainContentPanel.add(gradesPanel, "GRADES");
        mainContentPanel.add(coursesPanel, "COURSES");
        mainContentPanel.add(receiptsPanel, "RECEIPTS");
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        JLabel lbl = new JLabel(title + " - No Data Available", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(mutedColor);
        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    // =================================================================================
    // INTERNAL CUSTOM COMPONENTS (To ensure it works without external files)
    // =================================================================================

    /**
     * A Modern, Flat button with hover effects matching the theme.
     */
    private class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(textColor);
            setBackground(cardColor);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false); // Custom painting
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { setBackground(hoverColor); repaint(); }
                public void mouseExited(MouseEvent e) { setBackground(cardColor); repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
            super.paintComponent(g);
            g2.dispose();
        }
    }

    /**
     * A Card styled panel to display statistics.
     */
    private class InfoCard extends JPanel {
        public InfoCard(String title, String value, Color accent) {
            setLayout(new BorderLayout());
            setBackground(cardColor);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel titleLbl = new JLabel(title);
            titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            titleLbl.setForeground(mutedColor);

            JLabel valueLbl = new JLabel(value);
            valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 60));
            valueLbl.setForeground(accent);

            add(titleLbl, BorderLayout.NORTH);
            add(valueLbl, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            // Draw rounded rectangle
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            g2.dispose();
        }
    }

    // Main method for testing isolation
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ParentDashboard("John Doe").setVisible(true));
    }
}