package ui.dashboard;

import dbClasses.*;
import ui.StudentFrame.*;
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
import java.net.URI;
import java.awt.Desktop;

import middleware.studentService;

public class StudentDashboard extends JFrame {

    private String username;

    // --- UI COLOR PALETTE ---
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

    private studentService enrollmentService;

    private double cg = 0;
    private int credits = 0;
    private String rollNumber;
    private String studentProgram;

    // --- SLIDING MENU VARIABLES ---
    private JPanel sideMenuPanel;
    private boolean isMenuOpen = false; // Start closed
    private Timer menuTimer;
    // ADJUSTED WIDTH TO 250
    private final int MENU_TARGET_WIDTH = 250;
    private int currentMenuWidth = 0; // Start at 0 width (Hidden)

    private JLayeredPane mainContentPanel;
    private JPanel cardHolderPanel;
    private CardLayout cardLayout;
    private JPopupMenu profileMenu;
    private RoundedButton onlineLinkButton;
    private RoundedButton generateReportButton;
    private RoundedButton hamburgerButton;
    private RoundedButton closeMenuButton; // The arrow inside the menu
    private List<RoundedButton> menuButtons;


    public StudentDashboard(String rollNum, String username) {
        super("Student Dashboard - " + username);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        // Use BorderLayout to allow the "Push" effect
        setLayout(new BorderLayout());

        this.enrollmentService = new studentService();
        this.menuButtons = new ArrayList<>();
        this.rollNumber = rollNum;
        this.username = username;
        this.studentProgram = enrollmentService.getStudentProgram(rollNum);

        // 1. Create Side Menu (Initially Width 0)
        sideMenuPanel = createSideMenuPanel();
        sideMenuPanel.setPreferredSize(new Dimension(0, getHeight())); // Start hidden
        add(sideMenuPanel, BorderLayout.WEST);

        // 2. Initialize Main Content (Layered Pane to hold buttons over content)
        mainContentPanel = new JLayeredPane();
        mainContentPanel.setBackground(mainPanelColor);
        add(mainContentPanel, BorderLayout.CENTER);

        // 3. Setup Card Holder
        cardLayout = new CardLayout();
        cardHolderPanel = new JPanel(cardLayout);
        cardHolderPanel.setOpaque(false);
        mainContentPanel.add(cardHolderPanel, JLayeredPane.DEFAULT_LAYER);

        // 4. Create Header Buttons
        String initial = (username != null && !username.isEmpty()) ? username.substring(0, 1).toUpperCase() : "?";
        JButton profileButton = new CircularButton(initial);

        createProfileMenu();

        profileButton.addActionListener(e -> {
            int menuWidth = profileMenu.getPreferredSize().width;
            int x = profileButton.getWidth() - menuWidth;
            int y = profileButton.getHeight() + 5;
            profileMenu.show(profileButton, x, y);
        });

        // --- HAMBURGER BUTTON (Opens the menu) ---
        // CHANGED SYMBOL to ≣ and font size to 20
        hamburgerButton = createHeaderButton("");
        hamburgerButton.setIcon(new HamburgerIcon(textColor));
        hamburgerButton.addActionListener(e -> {
            if (!isMenuOpen) toggleMenu();
        });

        onlineLinkButton = createHeaderButton("Degree Requirements");
        onlineLinkButton.addActionListener(e -> {
            try {
                String url = getDegreeLink(this.studentProgram);
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        generateReportButton = createActionButton("Generate Report");
        generateReportButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(StudentDashboard.this, "Report generation logic not yet implemented.");
        });

        // Add buttons
        mainContentPanel.add(profileButton, JLayeredPane.PALETTE_LAYER);
        mainContentPanel.add(onlineLinkButton, JLayeredPane.PALETTE_LAYER);
        mainContentPanel.add(generateReportButton, JLayeredPane.PALETTE_LAYER);
        mainContentPanel.add(hamburgerButton, JLayeredPane.PALETTE_LAYER);

        // 5. Layout Logic
        mainContentPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = e.getComponent().getSize();

                cardHolderPanel.setBounds(0, 0, size.width, size.height);

                int padding = 20;
                int buttonSpacing = 20;

                // Position Hamburger (Visible when menu closed, or always visible)
                Dimension hamBtnSize = hamburgerButton.getPreferredSize();
                hamburgerButton.setBounds(padding, padding, hamBtnSize.width, hamBtnSize.height);

                Dimension profileBtnSize = profileButton.getPreferredSize();
                int profileX = size.width - profileBtnSize.width - padding;
                int profileY = padding;
                profileButton.setBounds(profileX, profileY, profileBtnSize.width, profileBtnSize.height);

                Dimension linkBtnSize = onlineLinkButton.getPreferredSize();
                onlineLinkButton.setBounds(
                        profileX - linkBtnSize.width - buttonSpacing,
                        profileY + (profileBtnSize.height - linkBtnSize.height) / 2,
                        linkBtnSize.width, linkBtnSize.height
                );

                Dimension reportBtnSize = generateReportButton.getPreferredSize();
                generateReportButton.setBounds(
                        size.width - reportBtnSize.width - padding - 30,
                        size.height - reportBtnSize.height - padding - 30,
                        reportBtnSize.width, reportBtnSize.height
                );
            }
        });

        createContentCards(cardHolderPanel, this.rollNumber, username);
        cardLayout.show(cardHolderPanel, "HOME");

        if (!menuButtons.isEmpty()) {
            setActiveButton(menuButtons.get(0));
        }
    }

    // --- PUSH ANIMATION LOGIC ---
    private void toggleMenu() {
        if (menuTimer != null && menuTimer.isRunning()) {
            menuTimer.stop();
        }

        isMenuOpen = !isMenuOpen;
        if (isMenuOpen) {
            hamburgerButton.setVisible(false);
        }
        int targetWidth = isMenuOpen ? MENU_TARGET_WIDTH : 0;
        int step = 40; // Speed of animation

        menuTimer = new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean finished = false;

                if (isMenuOpen) {
                    // EXPANDING
                    currentMenuWidth += step;
                    if (currentMenuWidth >= targetWidth) {
                        currentMenuWidth = targetWidth;
                        finished = true;
                    }
                } else {
                    // COLLAPSING
                    currentMenuWidth -= step;
                    if (currentMenuWidth <= targetWidth) {
                        currentMenuWidth = targetWidth;
                        finished = true;
                    }
                }

                // Apply the new width to the side panel
                sideMenuPanel.setPreferredSize(new Dimension(currentMenuWidth, getHeight()));
                sideMenuPanel.revalidate();
                revalidate();
                repaint();

                if (finished) {
                    ((Timer)e.getSource()).stop();
                    // 2. SHOW BUTTON ONLY AFTER CLOSING ANIMATION
                    if (!isMenuOpen) {
                        hamburgerButton.setVisible(true);
                    }
                }
            }
        });
        menuTimer.start();
    }

    private JPanel createSideMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // --- CUSTOM HEADER WITH CLOSE ARROW ---
        JPanel navHeader = new JPanel();
        navHeader.setLayout(new BoxLayout(navHeader, BoxLayout.X_AXIS));
        navHeader.setBackground(sideMenuColor);
        navHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        navHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        navHeader.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        JLabel menuTitle = new JLabel("Navigation");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Reduced size
        menuTitle.setForeground(textColor);

        // The Close Arrow Button
        closeMenuButton = createHeaderButton("\u00AB"); // Left guillemet (<<)
        closeMenuButton.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Reduced size
        closeMenuButton.setToolTipText("Close Menu");
        closeMenuButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeMenuButton.addActionListener(e -> {
            if(isMenuOpen) toggleMenu();
        });

        navHeader.add(menuTitle);
        navHeader.add(Box.createHorizontalGlue()); // Pushes arrow to the right
        navHeader.add(closeMenuButton);

        // ---------------------------------------

        RoundedButton homeButton = createSideMenuButton("Dashboard Home");
        RoundedButton coursesButton = createSideMenuButton("My Courses");
        RoundedButton registerForCourses = createSideMenuButton("Register For Courses");
        RoundedButton receiptButton = createSideMenuButton("Fee details");
        RoundedButton calenderButton = createSideMenuButton("Calender");

        Color logoutBg = bgColor;
        RoundedButton logoutButton = new RoundedButton(
                "\u21AA   Logout",
                logoutBg, logoutRedHover, logoutRedPressed, logoutBg, 8
        );

        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Reduced size
        logoutButton.setForeground(textColor);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45)); // Reduced height
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Reduced height
        logoutButton.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        menuButtons.add(homeButton);
        menuButtons.add(coursesButton);
        menuButtons.add(registerForCourses);
        menuButtons.add(receiptButton);
        menuButtons.add(calenderButton);

        homeButton.addActionListener(e -> {
            cardLayout.show(cardHolderPanel, "HOME");
            onlineLinkButton.setVisible(true);
            generateReportButton.setVisible(true);
            setActiveButton(homeButton);
        });

        coursesButton.addActionListener(e -> {
            cardLayout.show(cardHolderPanel, "COURSES");
            onlineLinkButton.setVisible(false);
            generateReportButton.setVisible(false);
            setActiveButton(coursesButton);
        });

        registerForCourses.addActionListener(e -> {
            StudentRegCourses courseListFrame = new StudentRegCourses(rollNumber, username);
            courseListFrame.setVisible(true);
            dispose();
        });

        receiptButton.addActionListener(e -> {
            Payfees feeFrame = new Payfees(rollNumber, username);
            feeFrame.setVisible(true);
            toggleMenu();
        });

        calenderButton.addActionListener(e -> {
            Calender calender = new Calender();
            calender.setVisible(true);
            toggleMenu();
        });

        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logout Successful");
            LandingFrame landingFrame = new LandingFrame();
            landingFrame.setVisible(true);
            dispose();
        });

        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(navHeader); // Add the Header with the arrow
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
        panel.add(coursesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(registerForCourses);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(receiptButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(calenderButton);
        panel.add(Box.createVerticalGlue());
        panel.add(logoutButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        return panel;
    }

    private RoundedButton createSideMenuButton(String text) {
        RoundedButton button = new RoundedButton(
                text, sideMenuColor, borderColor, buttonColor.darker(), buttonColor, 8
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Reduced font size
        button.setForeground(textSecondaryColor);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45)); // Reduced height
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); // Reduced height
        button.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private void setActiveButton(RoundedButton activeButton) {
        for (RoundedButton button : menuButtons) {
            button.setActive(false);
            button.setForeground(textSecondaryColor);
        }
        activeButton.setActive(true);
        activeButton.setForeground(textColor);
    }

    private RoundedButton createHeaderButton(String text) {
        RoundedButton button = new RoundedButton(
                text, sideMenuColor, borderColor, borderColor.darker(), 8
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setPreferredSize(null);
        return button;
    }

    private RoundedButton createActionButton(String text) {
        RoundedButton button = new RoundedButton(
                text, buttonColor, buttonColorGlow, 8
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setPreferredSize(null);
        return button;
    }

    private void createContentCards(JPanel cardHolder, String rollNum, String username) {
        StudentCgCredits dashboardData = enrollmentService.getCgData(rollNum);

        if (dashboardData != null) {
            this.credits = dashboardData.getCredits();
            this.cg = dashboardData.getCredits() > 0 ? (dashboardData.getCg() / dashboardData.getCredits()) : 0.0;
        } else {
            this.credits = 0;
            this.cg = 0.0;
        }

        // --- HOME PANEL ---
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(mainPanelColor);
        homePanel.setBorder(BorderFactory.createEmptyBorder(80, 35, 40, 40));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rollLabel = new JLabel("Student Roll no.: " + this.rollNumber);
        rollLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rollLabel.setForeground(textSecondaryColor);
        rollLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(rollLabel);

        titlePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        titleSeparator.setForeground(borderColor);
        titleSeparator.setBackground(mainPanelColor);
        titleSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        titlePanel.add(titleSeparator);

        homePanel.add(titlePanel, BorderLayout.NORTH);

        JPanel centerContentPanel = new JPanel(new GridBagLayout());
        centerContentPanel.setBackground(mainPanelColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // --- ROW 1: STATS BOXES ---
        JPanel cgpaBox = createStatBox("Current CGPA", String.format("%.2f", this.cg));
        JPanel creditsBox = createStatBox("Credits Earned", "" + this.credits);

        JPanel statBoxHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statBoxHolder.setOpaque(false);
        statBoxHolder.add(cgpaBox);
        statBoxHolder.add(Box.createRigidArea(new Dimension(25, 0)));
        statBoxHolder.add(creditsBox);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        centerContentPanel.add(statBoxHolder, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        centerContentPanel.add(spacer, gbc);

        // --- ROW 2: APPOINTMENTS BOX ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JPanel appointmentsPanel = createAppointmentsPanel();
        centerContentPanel.add(appointmentsPanel, gbc);

        // --- ROW 3: LINK BOXES ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel row3Panel = new JPanel(new GridLayout(1, 2, 30, 0));
        row3Panel.setOpaque(false);

        JPanel quickLinksPanel = createQuickLinksPanel();
        JPanel minorProgramsPanel = createMinorProgramsPanel();

        row3Panel.add(quickLinksPanel);
        row3Panel.add(minorProgramsPanel);

        centerContentPanel.add(row3Panel, gbc);

        JScrollPane mainScrollPane = createMainScrollPane(centerContentPanel);
        homePanel.add(mainScrollPane, BorderLayout.CENTER);

        cardHolder.add(homePanel, "HOME");

        StudentCoursesPanel coursesPanel = new StudentCoursesPanel(
                this.enrollmentService,
                this.username,
                bgColor, sideMenuColor, mainPanelColor, cardColor, popoverColor,
                borderColor, buttonColor, buttonColorGlow, textColor, textSecondaryColor
        );
        cardHolder.add(coursesPanel, "COURSES");
    }

    private JPanel createStatBox(String title, String value) {
        RoundedPanel boxPanel = new RoundedPanel(15, buttonColor, buttonColorGlow);
        boxPanel.setLayout(new BorderLayout(0, 10));
        // ADJUSTED DIMENSIONS
        boxPanel.setPreferredSize(new Dimension(300, 180));
        boxPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Reduced font size
        titleLabel.setForeground(textColor);
        boxPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 64)); // Reduced font size
        valueLabel.setForeground(textColor);
        boxPanel.add(valueLabel, BorderLayout.CENTER);

        return boxPanel;
    }

    private JPanel createAppointmentsPanel() {
        RoundedPanel appointmentsPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        // ADJUSTED DIMENSIONS
        appointmentsPanel.setPreferredSize(new Dimension(520, 200));
        appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));
        appointmentsPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel titleLabel = new JLabel("Faculty Appointments", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Reduced font size
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        appointmentsPanel.add(titleLabel);

        List<String> appointmentDetails = new ArrayList<>();
        appointmentDetails.add("Dr. Alan Turing - 2025-10-20 at 11:00 AM");
        appointmentDetails.add("Prof. Ada Lovelace - 2025-10-22 at 02:30 PM");
        appointmentDetails.add("Dr. Grace Hopper - 2025-10-25 at 09:00 AM");

        if (appointmentDetails.isEmpty()) {
            JLabel noAppointmentsLabel = new JLabel("No appointments scheduled.");
            noAppointmentsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noAppointmentsLabel.setForeground(textSecondaryColor);
            noAppointmentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            appointmentsPanel.add(noAppointmentsLabel);
        } else {
            for (String appointment : appointmentDetails) {
                JLabel appointmentLabel = new JLabel(appointment);
                appointmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // Reduced font size
                appointmentLabel.setForeground(textSecondaryColor);
                appointmentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                appointmentLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
                appointmentsPanel.add(appointmentLabel);
            }
        }
        appointmentsPanel.add(Box.createVerticalGlue());
        return appointmentsPanel;
    }

    private JPanel createQuickLinksPanel() {
        RoundedPanel linksPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        linksPanel.setLayout(new BoxLayout(linksPanel, BoxLayout.Y_AXIS));
        linksPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel linksTitle = new JLabel("Quick Links");
        linksTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Reduced font size
        linksTitle.setForeground(textColor);
        linksTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        linksTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        linksPanel.add(linksTitle);

        linksPanel.add(createClickableLink("IIITD Website", "https://iiitd.ac.in/"));
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(createClickableLink("Course Directory", "https://techtree.iiitd.edu.in/"));
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(createClickableLink("Academic Dishonesty Policy", "https://www.iiitd.ac.in/academics/resources/academic-dishonesty"));
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(createClickableLink("Library Website", "https://library.iiitd.ac.in/"));
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(createClickableLink("Fee Structure", "https://www.iiitd.ac.in/admission/fees"));
        linksPanel.add(Box.createVerticalGlue());

        return linksPanel;
    }

    private JPanel createMinorProgramsPanel() {
        RoundedPanel minorLinksPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        minorLinksPanel.setLayout(new BoxLayout(minorLinksPanel, BoxLayout.Y_AXIS));
        minorLinksPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel moreLinksTitle = new JLabel("Minor Programs Offered");
        moreLinksTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Reduced font size
        moreLinksTitle.setForeground(textColor);
        moreLinksTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        moreLinksTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        minorLinksPanel.add(moreLinksTitle);

        minorLinksPanel.add(createClickableLink("Minor in Economics", "https://iiitd.ac.in/sites/default/files/docs/education/2018-July-Regulation-for-Minor-in-Economics.pdf"));
        minorLinksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        minorLinksPanel.add(createClickableLink("Minor in Quantum Technologies", "https://iiitd.ac.in/sites/default/files/docs/education/2022/Minor%20in%20Quantum%20Technologies%20Aug%202022.pdf"));
        minorLinksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        minorLinksPanel.add(createClickableLink("Minor in Computational Biology", "https://iiitd.ac.in/sites/default/files/docs/education/2018%20July%20Regulation%20for%20Minor%20in%20Computational%20Biology%20(CB).pdf"));
        minorLinksPanel.add(Box.createVerticalGlue());
        minorLinksPanel.add(createClickableLink("Minor in Entrepreneurship", "https://iiitd.ac.in/sites/default/files/docs/education/2024/2024-July-Regulations%20for%20Minor%20in%20Entrepreneurship.pdf"));
        minorLinksPanel.add(Box.createVerticalGlue());//
        minorLinksPanel.add(createClickableLink("Minor in Human Centered Design", "https://iiitd.ac.in/sites/default/files/docs/education/2025/2025-January-Minor%20in%20Human%20Centered%20Design.pdf"));
        minorLinksPanel.add(Box.createVerticalGlue());
        return minorLinksPanel;
    }

    private String getDegreeLink(String program) {
        if (program == null) return "https://iiitd.ac.in/academics/regulations";
        String p = program.toLowerCase();

        if (p.contains("applied mathematics")) {
            return "https://iiitd.ac.in/sites/default/files/docs/education/2019/2019-July-BTech(CSAM)-Regulations.pdf";
        } else if (p.contains("computer science") && (p.contains("engineering") || p.contains("&"))) {
            return "https://iiitd.ac.in/sites/default/files/docs/education/2024/2024-May-BTech(CSE)-Regulations.pdf";
        } else if (p.contains("electronics")) {
            return "https://iiitd.ac.in/sites/default/files/docs/education/2022/2022-July-BTech(ECE)-Regulations.pdf";
        } else if (p.contains("design")) {
            return "https://iiitd.ac.in/sites/default/files/docs/education/2022/2022-July-BTech(CSD)-Regulations.pdf";
        } else if (p.contains("artificial")) {
            return "https://iiitd.ac.in/sites/default/files/docs/education/2024/2024-July-BTech(CSAI)-Regulations.pdf";
        } else if (p.contains("biology")) {
            return "https://iiitd.ac.in/sites/default/files/docs/education/2019/2019-July-BTech(CSB)-Regulations.pdf";
        } else if (p.contains("social")) {
            return "https://iiitd.ac.in/sites/default/files/docs/education/2022/2022-July-BTech(CSSS)-Regulations.pdf";
        }
        return "https://iiitd.ac.in/academics/regulations";
    }


    private JLabel createClickableLink(String text, String url) {
        JLabel linkLabel = new JLabel(text);
        linkLabel.setForeground(buttonColor);
        linkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // Reduced font size
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        linkLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(url));
                    }
                } catch (Exception e) {
                    // Log
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                linkLabel.setForeground(buttonColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                linkLabel.setForeground(buttonColor);
            }
        });

        return linkLabel;
    }

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

    private void createProfileMenu() {
        profileMenu = new JPopupMenu();
        profileMenu.setBackground(popoverColor);
        profileMenu.setBorder(BorderFactory.createLineBorder(bgColor));
        profileMenu.add(createMenuItem("Manage Account"));
        profileMenu.add(createMenuItem("Notifications"));
    }

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
                SwingUtilities.invokeLater(() -> {
                    StudentProfile profileFrame = new StudentProfile(rollNumber, username);
                    profileFrame.setVisible(true);
                });

            } else if (text.equals("Notifications")) {
                SwingUtilities.invokeLater(() -> {
                    Notifications notifFrame = new Notifications(rollNumber, username);
                    notifFrame.setVisible(true);
                });
            }
        });
        return item;
    }
    private class HamburgerIcon implements Icon {
        private final int width = 16;
        private final int height = 16;
        private final Color color;

        public HamburgerIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            int barHeight = 2;
            int gap = 3;
            int startY = y + 3;

            // Draw 3 Rounded Bars
            g2.fillRoundRect(x, startY, width, barHeight, 2, 2);
            g2.fillRoundRect(x, startY + barHeight + gap, width, barHeight, 2, 2);
            g2.fillRoundRect(x, startY + (barHeight + gap) * 2, width, barHeight, 2, 2);
            g2.dispose();
        }

        @Override public int getIconWidth() { return width; }
        @Override public int getIconHeight() { return height; }
    }

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