package ui.dashboard;

import ui.FacultyFrame.FacultyCoursesPanel;
import ui.FacultyFrame.FacultyProfile;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.landing.LandingFrame;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;

import ui.FacultyFrame.TAStats;
import middleware.facultyService;

public class FacultyDashboard extends JFrame {

    private String username;
    private String facultyID;

    // --- UI COLOR PALETTE FROM STUDENT DASHBOARD ---
    private Color bgColor = new Color(42, 48, 60);
    private Color sideMenuColor = new Color(48, 54, 70);
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color popoverColor = new Color(46, 52, 66);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    Color logoutRedHover = new Color(190, 60, 60);
    Color logoutRedPressed = new Color(160, 40, 40);

    private facultyService faculty;

    // --- UI Components from StudentDashboard ---
    private JLayeredPane mainContentPanel;
    private JPanel cardHolderPanel;
    private CardLayout cardLayout;
    private JPopupMenu profileMenu;
    private List<RoundedButton> menuButtons;

    private int numCourses = 0;

    public FacultyDashboard(String username) {
        super("Faculty Dashboard - " + username);

        this.username = username;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        setLayout(new BorderLayout());
        this.faculty = new facultyService();
        this.facultyID = faculty.facultyId(username);
        this.menuButtons = new ArrayList<>();

        // --- Create and add the side menu ---
        JPanel sideMenuPanel = createSideMenuPanel(username);
        add(sideMenuPanel, BorderLayout.WEST);

        // --- Main Content Area (Using JLayeredPane) ---
        mainContentPanel = new JLayeredPane();
        mainContentPanel.setBackground(mainPanelColor);
        add(mainContentPanel, BorderLayout.CENTER);

        // --- CardHolder Panel (sits on default layer) ---
        cardLayout = new CardLayout();
        cardHolderPanel = new JPanel(cardLayout);
        cardHolderPanel.setOpaque(false);
        mainContentPanel.add(cardHolderPanel, JLayeredPane.DEFAULT_LAYER);

        // --- Profile Button (sits on palette layer) ---
        String initial = (username != null && !username.isEmpty()) ? username.substring(0, 1).toUpperCase() : "?";
        JButton profileButton = new CircularButton(initial);

        createProfileMenu();

        profileButton.addActionListener(e -> {
            int menuWidth = profileMenu.getPreferredSize().width;
            int x = profileButton.getWidth() - menuWidth;
            int y = profileButton.getHeight() + 5;
            profileMenu.show(profileButton, x, y);
        });

        mainContentPanel.add(profileButton, JLayeredPane.PALETTE_LAYER);

        // --- Component Resizer (for positioning) ---
        mainContentPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = e.getComponent().getSize();
                cardHolderPanel.setBounds(0, 0, size.width, size.height);

                int padding = 20;
                Dimension profileBtnSize = profileButton.getPreferredSize();
                int profileX = size.width - profileBtnSize.width - padding;
                int profileY = padding;
                profileButton.setBounds(profileX, profileY, profileBtnSize.width, profileBtnSize.height);
            }
        });

        // --- Populate Content ---
        createContentCards(cardHolderPanel, username);

        // Show the initial "home" card
        cardLayout.show(cardHolderPanel, "HOME");
        if (!menuButtons.isEmpty()) {
            setActiveButton(menuButtons.get(0));
        }
    }

    /**
     * Creates the side navigation panel (Refactored StudentDashboard style).
     */
    private JPanel createSideMenuPanel(String username) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel menuTitle = new JLabel("Navigation");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        menuTitle.setForeground(textColor);
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuTitle.setBorder(BorderFactory.createEmptyBorder(0, 25, 10, 25));

        // Create navigation buttons using the new style
        RoundedButton homeButton = createSideMenuButton("Dashboard Home");
        RoundedButton scoresButton = createSideMenuButton("My Courses");
        RoundedButton TAButton = createSideMenuButton("TA Info");

        // Special logout button
        RoundedButton logoutButton = new RoundedButton(
                "\u21AA   Logout",
                bgColor, logoutRedHover, logoutRedPressed, bgColor, 8
        );
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logoutButton.setForeground(textColor);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add to button list for active state management
        menuButtons.add(homeButton);
        menuButtons.add(scoresButton);

        // --- Add Action Listeners (Original Faculty Logic) ---
        homeButton.addActionListener(e -> {
            cardLayout.show(cardHolderPanel, "HOME");
            setActiveButton(homeButton);
        });

        scoresButton.addActionListener(e -> {
            cardLayout.show(cardHolderPanel, "COURSES");
            setActiveButton(scoresButton);
        });

        TAButton.addActionListener(e -> {
            TAStats taFrame = new TAStats(facultyID, username);
            taFrame.setVisible(true);
        });

        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logout successful");
            for (Window w : Window.getWindows()) {
                if (w instanceof JFrame) {
                    w.dispose();
                }
            }
            SwingUtilities.invokeLater(() -> {
                LandingFrame landingFrame = new LandingFrame();
                landingFrame.setVisible(true);
            });
        });

        // --- Add components to the panel (StudentDashboard layout) ---
        panel.add(menuTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        JSeparator navSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        navSeparator.setForeground(borderColor);
        navSeparator.setBackground(sideMenuColor);
        navSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));

        Box separatorWrapper = Box.createHorizontalBox();
        separatorWrapper.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        separatorWrapper.add(navSeparator);
        separatorWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separatorWrapper);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(homeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(scoresButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(TAButton);

        panel.add(Box.createVerticalGlue());
        panel.add(logoutButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        return panel;
    }

    /**
     * Creates all the content panels for the CardLayout.
     */
    private void createContentCards(JPanel cardHolder, String username) {
        if (facultyID == null) {
            JOptionPane.showMessageDialog(this,
                    "Could not find faculty details for user: " + username,
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        if (facultyID != null) {
            numCourses = faculty.getFacultyCourse(facultyID);
        }

        // --- 1. Home Panel (Refactored StudentDashboard style) ---
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(mainPanelColor);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 40, 40));

        // --- Title Panel ---
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idLabel = new JLabel("Faculty ID: " + (this.facultyID != null ? this.facultyID : "N/A"));
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setForeground(textSecondaryColor);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(idLabel);

        titlePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        titleSeparator.setForeground(borderColor);
        titleSeparator.setBackground(mainPanelColor);
        titleSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        titlePanel.add(titleSeparator);

        homePanel.add(titlePanel, BorderLayout.NORTH);

        // --- Center Content Panel (GridBagLayout) ---
        JPanel centerContentPanel = new JPanel(new GridBagLayout());
        centerContentPanel.setBackground(mainPanelColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // --- ROW 0: STATS BOXES ---
        JPanel courseBox = createStatBox("Courses Taught", String.valueOf(numCourses));

        JPanel statBoxHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statBoxHolder.setOpaque(false);
        statBoxHolder.add(courseBox);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        centerContentPanel.add(statBoxHolder, gbc);

        // Horizontal spacer
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        centerContentPanel.add(spacer, gbc);

        // --- ROW 1: APPOINTMENTS BOX (As requested) ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JPanel appointmentsPanel = createAppointmentsPanel();
        centerContentPanel.add(appointmentsPanel, gbc);

        // --- ROW 2: VERTICAL SPACER (Pushes content up) ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel verticalSpacer = new JPanel();
        verticalSpacer.setOpaque(false);
        centerContentPanel.add(verticalSpacer, gbc);

        // --- Add ScrollPane (for consistent feel and custom scrollbar) ---
        JScrollPane mainScrollPane = createMainScrollPane(centerContentPanel);
        homePanel.add(mainScrollPane, BorderLayout.CENTER);

        // --- Add all cards to the main panel ---
        cardHolder.add(homePanel, "HOME");
        FacultyCoursesPanel coursesPanel = new FacultyCoursesPanel(facultyID);
        cardHolder.add(coursesPanel, "COURSES");
    }

    /**
     * Creates a styled side menu button.
     */
    private RoundedButton createSideMenuButton(String text) {
        // Use the new public RoundedButton class
        RoundedButton button = new RoundedButton(
                text,
                sideMenuColor,      // normal
                borderColor,      // hover
                buttonColor.darker(), // pressed
                buttonColor,      // active
                8                 // arc
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setForeground(textSecondaryColor);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    /**
     * Sets the active state for the side menu.
     */
    private void setActiveButton(RoundedButton activeButton) {
        for (RoundedButton button : menuButtons) {
            button.setActive(false);
            button.setForeground(textSecondaryColor);
        }
        activeButton.setActive(true);
        activeButton.setForeground(textColor);
    }

    /**
     * Creates a styled statistic box for the dashboard.
     */
    private JPanel createStatBox(String title, String value) {
        // Use the new public RoundedPanel class
        RoundedPanel boxPanel = new RoundedPanel(15, buttonColor, buttonColorGlow);
        boxPanel.setLayout(new BorderLayout(0, 10));
        boxPanel.setPreferredSize(new Dimension(350, 200));
        boxPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        boxPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 72));
        valueLabel.setForeground(textColor);
        boxPanel.add(valueLabel, BorderLayout.CENTER);

        return boxPanel;
    }

    /**
     * Creates the styled "Faculty Appointments" panel.
     */
    private JPanel createAppointmentsPanel() {
        // Use the new public RoundedPanel class
        RoundedPanel appointmentsPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        appointmentsPanel.setPreferredSize(new Dimension(570, 220));
        appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));
        appointmentsPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel titleLabel = new JLabel("Faculty Appointments", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        appointmentsPanel.add(titleLabel);

        // Placeholder data
        List<String> appointmentDetails = new ArrayList<>();
        appointmentDetails.add("Student: John Doe - 2025-10-20 at 11:00 AM");
        appointmentDetails.add("Student: Jane Smith - 2025-10-22 at 02:30 PM");
        appointmentDetails.add("Student: Robert Brown - 2025-10-25 at 09:00 AM");

        if (appointmentDetails.isEmpty()) {
            JLabel noAppointmentsLabel = new JLabel("No appointments scheduled.");
            noAppointmentsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noAppointmentsLabel.setForeground(textSecondaryColor);
            noAppointmentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            appointmentsPanel.add(noAppointmentsLabel);
        } else {
            for (String appointment : appointmentDetails) {
                JLabel appointmentLabel = new JLabel(appointment);
                appointmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                appointmentLabel.setForeground(textSecondaryColor);
                appointmentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                appointmentLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
                appointmentsPanel.add(appointmentLabel);
            }
        }

        appointmentsPanel.add(Box.createVerticalGlue());
        return appointmentsPanel;
    }

    /**
     * Creates the main scroll pane with hidden, styled scrollbars.
     */
    private JScrollPane createMainScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(mainPanelColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(mainPanelColor);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI());
        return scrollPane;
    }

    /**
     * Creates the profile popup menu.
     */
    private void createProfileMenu() {
        profileMenu = new JPopupMenu();
        profileMenu.setBackground(popoverColor);
        profileMenu.setBorder(BorderFactory.createLineBorder(bgColor));
        profileMenu.add(createMenuItem("Manage Account"));
    }

    /**
     * Creates a styled menu item for the profile popup.
     */
    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setBackground(popoverColor);
        item.setForeground(textColor);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(buttonColor);
                item.setForeground(textColor);
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(popoverColor);
                item.setForeground(textColor);
            }
        });

        item.addActionListener(e -> {
            if (text.equals("Manage Account")) {
                // UPDATED: Open Faculty Profile
                SwingUtilities.invokeLater(() -> {
                    FacultyProfile profileFrame = new FacultyProfile(facultyID, username);
                    profileFrame.setVisible(true);
                });
            }
        });
        return item;
    }

    // ---
    // --- INNER CLASSES (Still needed)
    // ---

    /**
     * Inner class for a custom styled scrollbar.
     */
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = buttonColor;
            this.trackColor = cardColor;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            jbutton.setMinimumSize(new Dimension(0, 0));
            jbutton.setMaximumSize(new Dimension(0, 0));
            return jbutton;
        }
    }

    /**
     * Inner class for the circular profile button.
     */
    private static class CircularButton extends JButton {
        public CircularButton(String text) {
            super(text);
            Dimension size = new Dimension(40, 40);
            setPreferredSize(size);
            setMaximumSize(size);
            setMinimumSize(size);
            setBackground(new Color(52, 159, 148));
            setForeground(new Color(255, 255, 255));
            setFont(new Font("Segoe UI", Font.BOLD, 18));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isArmed()) {
                g2.setColor(getBackground().darker());
            } else {
                g2.setColor(getBackground());
            }
            g2.fillOval(0, 0, getWidth(), getHeight());
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // No border
        }

        @Override
        public boolean contains(int x, int y) {
            int radius = getWidth() / 2;
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            return (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2)) <= Math.pow(radius, 2);
        }
    }
}