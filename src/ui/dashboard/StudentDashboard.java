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
            profileMenu.show(profileButton, 0, profileButton.getHeight() + 5);
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
        JPanel centerContentPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        centerContentPanel.setBackground(mainPanelColor);
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
        JPanel statsContainer = new JPanel(new BorderLayout());
        statsContainer.setBackground(mainPanelColor);
        statsContainer.add(statsPanel, BorderLayout.NORTH);
        JPanel linksPanel = new JPanel();
        linksPanel.setLayout(new BoxLayout(linksPanel, BoxLayout.Y_AXIS));
        linksPanel.setBackground(mainPanelColor);
        linksPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        JLabel linksTitle = new JLabel("Quick Links");
        linksTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        linksTitle.setForeground(textColor);
        linksTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        linksTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        linksPanel.add(linksTitle);
        linksPanel.add(createClickableLink("Link 1", "https://example.com/link1"));
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(createClickableLink("Link 2", "https://example.com/link2"));
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(createClickableLink("Link 3", "https://example.com/link3"));
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(createClickableLink("Link 4", "https://example.com/link4"));
        linksPanel.add(Box.createVerticalGlue());
        statsContainer.add(linksPanel, BorderLayout.CENTER);
        centerContentPanel.add(statsContainer);
        JPanel rightSideContainer = new JPanel();
        rightSideContainer.setLayout(new BoxLayout(rightSideContainer, BoxLayout.Y_AXIS));
        rightSideContainer.setBackground(mainPanelColor);
        JPanel appointmentsPanel = createAppointmentsPanel();
        rightSideContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        rightSideContainer.add(appointmentsPanel);
        rightSideContainer.add(Box.createVerticalGlue());
        centerContentPanel.add(rightSideContainer);
        homePanel.add(centerContentPanel, BorderLayout.CENTER);


        // --- OLD SQL BLOCK DELETED ---
        // The 'for' loop that ran 8 SQL queries is now gone.


        // --- MODIFIED COURSES PANEL ---
        // This panel is now clean and uses the EnrollmentService.
        JPanel coursesPanel = new JPanel(new BorderLayout());
        coursesPanel.setBackground(mainPanelColor);
        coursesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel pageTitle = new JLabel("My Registered Courses");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        pageTitle.setForeground(textColor);
        pageTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        coursesPanel.add(pageTitle, BorderLayout.NORTH);

        // 1. Get the data from the service ONCE.
        Map<Integer, List<StudentRegisteredCourse>> semesterData = enrollmentService.getSemesterData(username);

        // 2. Create the Tabbed Pane
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
            noAppointmentsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noAppointmentsLabel.setForeground(textColor);
            noAppointmentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            appointmentsPanel.add(noAppointmentsLabel);
        } else {
            for (String appointment : appointmentDetails) {
                JLabel appointmentLabel = new JLabel("\u2022 " + appointment);
                appointmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                appointmentLabel.setForeground(textColor);
                appointmentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                appointmentLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
                appointmentsPanel.add(appointmentLabel);
            }
        }

        appointmentsPanel.add(Box.createVerticalGlue());
        return appointmentsPanel;
    }

    private JLabel createClickableLink(String text, String url) {
        String hexColor = String.format("#%02x%02x%02x", buttonColor.getRed(), buttonColor.getGreen(), buttonColor.getBlue());
        String htmlText = "<html><u style='color:" + hexColor + "'>" + text + "</u></html>";
        JLabel linkLabel = new JLabel(htmlText);

        linkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
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
                String hoverHexColor = String.format("#%02x%02x%02x", buttonColor.darker().getRed(), buttonColor.darker().getGreen(), buttonColor.darker().getBlue());
                String hoverText = "<html><u style='color:" + hoverHexColor + "'>" + text + "</u></html>";
                linkLabel.setText(hoverText);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                linkLabel.setText(htmlText);
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

        profileMenu.add(createMenuItem("Option 1"));
        profileMenu.add(createMenuItem("Option 2"));
        profileMenu.add(new JPopupMenu.Separator());
        profileMenu.add(createMenuItem("Option 3"));
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

