package ui.dashboard;

import dbClasses.StudentCgCredits;
import ui.landing.LandingFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.sql.*;
import databaseConfig.Connector;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.net.URI;
import java.awt.Desktop;
import java.util.Map;

import middleware.studentService;
import dbClasses.StudentRegisteredCourse;

public class StudentDashboard extends JFrame {

    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;

    // --- USE THE CORRECT SERVICE CLASS ---
    private studentService enrollmentService;

    private double cg = 0;
    private int credits = 0;
    private String rollNumber;

    private JLayeredPane mainContentPanel;
    private JPanel cardHolderPanel;
    private CardLayout cardLayout;
    private JPopupMenu profileMenu;

    public StudentDashboard(String rollNum, String username) {
        super("Student Dashboard - " + username);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        setLayout(new BorderLayout());

        // --- INITIALIZE THE SERVICE ---
        this.enrollmentService = new studentService();

        this.rollNumber = rollNum;
        JPanel sideMenuPanel = createSideMenuPanel();
        add(sideMenuPanel, BorderLayout.WEST);

        mainContentPanel = new JLayeredPane();
        mainContentPanel.setBackground(mainPanelColor);
        add(mainContentPanel, BorderLayout.CENTER);

        cardLayout = new CardLayout();
        cardHolderPanel = new JPanel(cardLayout);
        cardHolderPanel.setOpaque(false);

        mainContentPanel.add(cardHolderPanel, JLayeredPane.DEFAULT_LAYER);

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

        mainContentPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = e.getComponent().getSize();
                cardHolderPanel.setBounds(0, 0, size.width, size.height);

                Dimension btnSize = profileButton.getPreferredSize();
                int padding = 20;
                profileButton.setBounds(size.width - btnSize.width - padding, padding, btnSize.width, btnSize.height);
            }
        });

        // This method now calls the service
        createContentCards(cardHolderPanel, this.rollNumber, username);

        cardLayout.show(cardHolderPanel, "HOME");
    }

    private JPanel createSideMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor);
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel menuTitle = new JLabel("Navigation");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        menuTitle.setForeground(textColor);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JButton homeButton = createMenuButton("Dashboard Home");
        JButton registerForCourses = createMenuButton("Register For Courses");
        JButton coursesButton = createMenuButton("My Courses");
        JButton receiptButton = createMenuButton("Fee Receipts");
        JButton logoutButton = createMenuButton("Logout");

        homeButton.addActionListener(e -> cardLayout.show(cardHolderPanel, "HOME"));
        registerForCourses.addActionListener(e -> cardLayout.show(cardHolderPanel, "REGISTER"));
        coursesButton.addActionListener(e -> cardLayout.show(cardHolderPanel, "COURSES"));
        receiptButton.addActionListener(e -> cardLayout.show(cardHolderPanel, "RECEIPTS"));

        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logout Successful");
            LandingFrame landingFrame = new LandingFrame();
            landingFrame.setVisible(true);
            dispose();
        });

        panel.add(menuTitle);
        panel.add(homeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(coursesButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(registerForCourses);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(receiptButton);

        panel.add(Box.createVerticalGlue());
        panel.add(logoutButton);

        return panel;
    }

    // --- 1. MODIFIED createContentCards METHOD ---
    // This method now contains the new 2-row (top/bottom) layout
// --- 1. MODIFIED createContentCards METHOD ---
    // This method now contains the new 2-row (top/bottom) layout
    private void createContentCards(JPanel cardHolder, String rollNum, String username) {

        StudentCgCredits dashboardData = enrollmentService.getCgData(rollNum);

        if (dashboardData != null) {
            this.credits = dashboardData.getCredits();
            this.cg = dashboardData.getCg()/dashboardData.getCredits();
        } else {
            // Handle error case
            this.credits = 0;
            this.cg = 0.0;
            JOptionPane.showMessageDialog(this, "Could not fetch student CGPA data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // --- HOME PANEL ---
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(mainPanelColor);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor);
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel nameLabel = new JLabel("Student name: " + username);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel rollLabel = new JLabel("Student Roll no.: " + this.rollNumber);
        rollLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rollLabel.setForeground(textColor);
        rollLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(nameLabel);
        titlePanel.add(rollLabel);
        homePanel.add(titlePanel, BorderLayout.NORTH);

        // 1. Main content area now uses BorderLayout to stack top and bottom panels
        // --- GAP INCREASED from 20 to 40 ---
        JPanel centerContentPanel = new JPanel(new BorderLayout(0, 40)); // 40px vertical gap
        centerContentPanel.setBackground(mainPanelColor);

        // 2. Create TOP panel for Stats and Appointments
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 40, 0)); // 1 row, 2 cols, 40px h-gap
        topPanel.setBackground(mainPanelColor);

        // 3. Create Stats Panel (for the left side of topPanel)
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.X_AXIS));
        statsPanel.setBackground(mainPanelColor);
        JPanel cgpaBox = createStatBox("Current CGPA", "" + this.cg);
        JPanel creditsBox = createStatBox("Credits Earned", "" + this.credits);
        statsPanel.add(Box.createHorizontalGlue());
        statsPanel.add(cgpaBox);
        statsPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        statsPanel.add(creditsBox);
        statsPanel.add(Box.createHorizontalGlue());

        // 4. Create Appointments Panel (for the right side of topPanel)
        JPanel appointmentsPanel = createAppointmentsPanel();

        // 5. Add Stats and Appointments to the TOP panel
        topPanel.add(statsPanel);
        topPanel.add(appointmentsPanel);

        // 6. Create BOTTOM panel for the two Link lists
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 40, 0)); // 1 row, 2 cols, 40px h-gap
        bottomPanel.setBackground(mainPanelColor);

        // 7. Create "Quick Links" Panel (for the left side of bottomPanel)
        JPanel linksPanel = new JPanel();
        linksPanel.setLayout(new BoxLayout(linksPanel, BoxLayout.Y_AXIS));
        linksPanel.setBackground(mainPanelColor);
        linksPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20)); // Removed top padding
        JLabel linksTitle = new JLabel("Quick Links");
        // --- FONT SIZE INCREASED from 20 to 22 ---
        linksTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        linksTitle.setForeground(textColor);
        linksTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        linksTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        linksPanel.add(linksTitle);
        linksPanel.add(createClickableLink("IIITD Website", "https://iiitd.ac.in/"));
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(createClickableLink("Academic Dishonesty Policy", "https://www.iiitd.ac.in/academics/resources/academic-dishonesty"));
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(createClickableLink("Library Website", "https://library.iiitd.edu.in/"));
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(Box.createVerticalGlue());

        // 8. Create "More Resources" Panel (for the right side of bottomPanel)
        JPanel moreLinksPanel = new JPanel();
        moreLinksPanel.setLayout(new BoxLayout(moreLinksPanel, BoxLayout.Y_AXIS));
        moreLinksPanel.setBackground(mainPanelColor);
        moreLinksPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25)); // Removed top padding
        JLabel moreLinksTitle = new JLabel("Minor Programs Offered");
        // --- FONT SIZE INCREASED from 20 to 22 ---
        moreLinksTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        moreLinksTitle.setForeground(textColor);
        moreLinksTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        moreLinksTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        moreLinksPanel.add(moreLinksTitle);
        moreLinksPanel.add(createClickableLink("Minor in Economics", "https://iiitd.ac.in/sites/default/files/docs/education/2018-July-Regulation-for-Minor-in-Economics.pdf"));
        moreLinksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        moreLinksPanel.add(createClickableLink("Minor in Quantum Technologies", "https://iiitd.ac.in/sites/default/files/docs/education/2022/Minor%20in%20Quantum%20Technologies%20Aug%202022.pdf"));
        moreLinksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        moreLinksPanel.add(createClickableLink("Minor in Computational Biology", "https://iiitd.ac.in/sites/default/files/docs/education/2018%20July%20Regulation%20for%20Minor%20in%20Computational%20Biology%20(CB).pdf"));
        moreLinksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        moreLinksPanel.add(createClickableLink("Minor in Entrepreneurship", "https://iiitd.ac.in/sites/default/files/docs/education/2024/2024-July-Regulations%20for%20Minor%20in%20Entrepreneurship.pdf"));
        moreLinksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        moreLinksPanel.add(createClickableLink("Minor in Human Centered Design", "https://iiitd.ac.in/sites/default/files/docs/education/2025/2025-January-Minor%20in%20Human%20Centered%20Design.pdf"));
        moreLinksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        moreLinksPanel.add(Box.createVerticalGlue());

        // 9. Add the two link panels to the BOTTOM panel
        bottomPanel.add(linksPanel);
        bottomPanel.add(moreLinksPanel);

        // 10. Add the top and bottom panels to the main center panel
        centerContentPanel.add(topPanel, BorderLayout.NORTH);
        centerContentPanel.add(bottomPanel, BorderLayout.CENTER);

        // 11. Add the main center panel to the home panel
        homePanel.add(centerContentPanel, BorderLayout.CENTER);


        // --- COURSES PANEL ---
        JPanel coursesPanel = new JPanel(new BorderLayout());
        coursesPanel.setBackground(mainPanelColor);
        coursesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel pageTitle = new JLabel("My Registered Courses");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        pageTitle.setForeground(textColor);
        pageTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        coursesPanel.add(pageTitle, BorderLayout.NORTH);

        Map<Integer, List<StudentRegisteredCourse>> semesterData = enrollmentService.getSemesterData(username);

        // --- Corrected Typo: JTabbedPane ---
        JTabbedPane semesterTabs = new JTabbedPane();
        semesterTabs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        semesterTabs.setBackground(mainPanelColor);
        semesterTabs.setForeground(textColor);
        semesterTabs.setFocusable(false);

        String[] columnNames = {"Course Code", "Course Name", "Credits", "Offered By", "Grade Point"};

        // 3. Loop through semesters and build tabs
        for (int i = 1; i < 9; i++) {
            if (semesterData.containsKey(i)) {

                // 4. Get the clean list of data objects
                List<StudentRegisteredCourse> coursesForThisSem = semesterData.get(i);

                // 5. Convert the List of objects into an Object[][] for the JTable
                Object[][] data = new Object[coursesForThisSem.size()][5]; // 5 columns

                for (int j = 0; j < coursesForThisSem.size(); j++) {
                    StudentRegisteredCourse course = coursesForThisSem.get(j);

                    data[j][0] = course.getCourseCode();
                    data[j][1] = course.getCourseName();
                    data[j][2] = course.getCourseCredits();
                    data[j][3] = course.getOfferedBy();

                    if (course.getGradePoint() == 0.0) {
                        data[j][4] = "Yet to be declared";
                    } else {
                        data[j][4] = course.getGradePoint();
                    }
                }

                // 6. Create the JTable and add it to a scroll pane
                JTable semTable = createStyledTable(data, columnNames);
                JScrollPane scrollPane = createStyledScrollPane(semTable);

                // 7. Add the scroll pane (with the table) as a new tab
                semesterTabs.addTab("Semester " + i, scrollPane);
            }
        }

        coursesPanel.add(semesterTabs, BorderLayout.CENTER);


        // --- OTHER PANELS (Unchanged) ---
        JPanel registerPanel = new JPanel();
        registerPanel.setBackground(mainPanelColor);
        registerPanel.add(new JLabel("All courses here") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 24));
            setForeground(textColor);
        }});

        JPanel receiptPanel = new JPanel();
        receiptPanel.setBackground(mainPanelColor);
        receiptPanel.add(new JLabel("Fee updates are here") {{
            setFont(new Font("Segoe UI", Font.PLAIN, 24));
            setForeground(textColor);
        }});


        cardHolder.add(homePanel, "HOME");
        cardHolder.add(registerPanel, "REGISTER");
        cardHolder.add(coursesPanel, "COURSES");
        cardHolder.add(receiptPanel, "RECEIPTS");
    }
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }

    private JPanel createStatBox(String title, String value) {
        JPanel boxPanel = new JPanel(new BorderLayout());
        boxPanel.setBackground(sideMenuColor);
        boxPanel.setPreferredSize(new Dimension(250, 200));
        boxPanel.setMaximumSize(boxPanel.getPreferredSize());
        boxPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonColor, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textColor);
        boxPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 56));
        valueLabel.setForeground(textColor);
        boxPanel.add(valueLabel, BorderLayout.CENTER);

        return boxPanel;
    }


    // --- 2. MODIFIED createAppointmentsPanel METHOD ---
    // Font size was increased
    private JPanel createAppointmentsPanel() {
        JPanel appointmentsPanel = new JPanel();
        appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));
        appointmentsPanel.setBackground(mainPanelColor);
        appointmentsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Faculty Appointments", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
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
            // --- FONT SIZE INCREASED ---
            noAppointmentsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            noAppointmentsLabel.setForeground(textColor);
            noAppointmentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            appointmentsPanel.add(noAppointmentsLabel);
        } else {
            for (String appointment : appointmentDetails) {
                JLabel appointmentLabel = new JLabel("\u2022 " + appointment);
                // --- FONT SIZE INCREASED ---
                appointmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                appointmentLabel.setForeground(textColor);
                appointmentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                appointmentLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
                appointmentsPanel.add(appointmentLabel);
            }
        }

        appointmentsPanel.add(Box.createVerticalGlue());
        return appointmentsPanel;
    }

    // --- 3. MODIFIED createClickableLink METHOD ---
    // Removed underline (HTML) and set color/font size directly
    private JLabel createClickableLink(String text, String url) {
        // No HTML needed. Just create a normal JLabel.
        JLabel linkLabel = new JLabel(text);

        // Set the text color to your 'green' buttonColor
        linkLabel.setForeground(buttonColor);
        // --- FONT SIZE INCREASED ---
        linkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        linkLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(url));
                    } else {
                        JOptionPane.showMessageDialog(StudentDashboard.this,
                                "Cannot open link. OS does not support Desktop.browse.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDashboard.this,
                            "Could not open link: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // On hover, set the color to a darker shade of green
                linkLabel.setForeground(buttonColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // When mouse leaves, set it back to the original green
                linkLabel.setForeground(buttonColor);
            }
        });

        return linkLabel;
    }

    private JTable createStyledTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setBackground(mainPanelColor);
        table.setForeground(textColor);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(sideMenuColor.brighter());
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(buttonColor.darker());
        table.setSelectionForeground(textColor);
        table.getTableHeader().setBackground(sideMenuColor);
        table.getTableHeader().setForeground(buttonColor);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(sideMenuColor));
        table.getTableHeader().setReorderingAllowed(false);

        // Center align header text
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        // Center align text in all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for(int i=0; i < table.getColumnCount(); i++){
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return table;
    }

    private JScrollPane createStyledScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createLineBorder(sideMenuColor));
        scrollPane.getViewport().setBackground(mainPanelColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private void createProfileMenu() {
        profileMenu = new JPopupMenu();
        profileMenu.setBackground(new Color(70, 70, 70));
        profileMenu.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        profileMenu.add(createMenuItem("Manage Account"));
        profileMenu.add(createMenuItem("Notifications"));
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setBackground(new Color(70, 70, 70));
        item.setForeground(textColor);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.addActionListener(e -> {
            System.out.println("Clicked: " + text);
            // You can add functionality here, e.g., open a new dialog
            // if (text.equals("Manage Account")) { ... }
        });

        return item;
    }

    private class CircularButton extends JButton {

        public CircularButton(String text) {
            super(text);

            Dimension size = new Dimension(40, 40);
            setPreferredSize(size);
            setMaximumSize(size);
            setMinimumSize(size);

            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
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

            super.paintComponent(g);

            g2.dispose();
        }



        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.dispose();
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