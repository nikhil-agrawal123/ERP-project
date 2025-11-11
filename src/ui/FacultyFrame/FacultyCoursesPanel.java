package ui.FacultyFrame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap; // Use LinkedHashMap to maintain semester order

import middleware.facultyService;

/**
 * A JPanel that displays courses taught by a faculty member, styled similarly
 * to the StudentCoursesPanel.
 *
 * THIS VERSION IS SELF-CONTAINED. It defines its own colors and service.
 * It only requires a 'username' in its constructor.
 */
public class FacultyCoursesPanel extends JPanel {

    // --- Services & Data ---
    private facultyService facultySvc; // Will be initialized in constructor
    private String facultyUsername;
    private Map<String, List<facultyCourseClass>> semesterCourses; // Hardcoded data map

    // --- UI Components ---
    private CardLayout mainCardLayout;
    private JPanel mainContentPanel; // The panel holding the CardLayout

    // --- UI Color Palette (DEFINED INTERNALLY) ---
    private Color bgColor;
    private Color sideMenuColor;
    private Color mainPanelColor;
    private Color cardColor;
    private Color popoverColor;
    private Color borderColor;
    private Color buttonColor;
    private Color buttonColorGlow;
    private Color textColor;
    private Color textSecondaryColor;

    /**
     * Constructor for the Faculty Courses Panel.
     * This version only accepts a username and defines its own colors.
     *
     * @param username The faculty member's username.
     */
    public FacultyCoursesPanel(String username) {
        super();

        // --- 1. Assign constructor parameters ---
        this.facultyUsername = username;

        // --- 2. Initialize Service (INTERNALLY) ---
        this.facultySvc = new facultyService();

        // --- 3. Initialize Colors (INTERNALLY) ---
        // **** THIS BLOCK IS UPDATED TO MATCH FACULTYDASHBOARD ****
        bgColor = new Color(42, 48, 60);            // --background
        sideMenuColor = new Color(48, 54, 70);      // --sidebar-background
        mainPanelColor = new Color(42, 48, 60);       // --background
        cardColor = new Color(54, 59, 74);          // --card
        popoverColor = new Color(46, 52, 66);       // --popover
        borderColor = new Color(64, 69, 89);        // --border
        buttonColor = new Color(52, 159, 148);      // --primary / --accent
        buttonColorGlow = new Color(79, 196, 184);  // --primary-glow
        textColor = new Color(255, 255, 255);       // --foreground
        textSecondaryColor = new Color(179, 179, 179); // --muted-foreground
        // **** END OF COLOR UPDATE ****


        // --- Hardcode Data (as requested) ---
        // This replaces a call like facultySvc.getAllCourses(username)
        this.semesterCourses = new LinkedHashMap<>();

        List<facultyCourseClass> fall2025 = new ArrayList<>();
        fall2025.add(new facultyCourseClass("CS-301", "Operating Systems", 4, "CSE", 75));
        fall2025.add(new facultyCourseClass("MATH-201", "Linear Algebra", 4, "MATH", 120));

        List<facultyCourseClass> spring2025 = new ArrayList<>();
        spring2025.add(new facultyCourseClass("CS-101", "Intro to Programming", 4, "CSE", 150));

        List<facultyCourseClass> fall2024 = new ArrayList<>();
        fall2024.add(new facultyCourseClass("CS-450", "Database Systems", 4, "CSE", 60));

        semesterCourses.put("Fall 2025", fall2025);
        semesterCourses.put("Spring 2025", spring2025);
        semesterCourses.put("Fall 2024", fall2024);
        // --- End Hardcoded Data ---

        // --- Configure this JPanel ---
        // This panel itself is just a holder for the CardLayout
        setLayout(new BorderLayout());
        setBackground(mainPanelColor);

        // --- Main Content Panel (with CardLayout) ---
        mainCardLayout = new CardLayout();
        mainContentPanel = new JPanel(mainCardLayout);
        mainContentPanel.setOpaque(false);
        add(mainContentPanel, BorderLayout.CENTER);

        // --- Create and add the two main views ---

        // 1. The Course List View (with tabs)
        JPanel courseListPage = createCourseListPage();
        mainContentPanel.add(courseListPage, "COURSE_LIST");

        // 2. The Detail Views (one for each course)
        for (String semester : semesterCourses.keySet()) {
            for (facultyCourseClass course : semesterCourses.get(semester)) {
                JPanel detailPanel = createCourseDetailPanel(course);
                mainContentPanel.add(detailPanel, "DETAIL_" + course.getCourseCode());
            }
        }

        // Show the list page by default
        mainCardLayout.show(mainContentPanel, "COURSE_LIST");
    }

    /**
     * Creates the main "Course List" page.
     * This page has a title, semester tabs, and a CardLayout to show
     * the courses for the selected semester.
     */
    private JPanel createCourseListPage() {
        // --- Main Panel Setup ---
        JPanel courseListPage = new JPanel(new BorderLayout(0, 15)); // 15px v-gap
        courseListPage.setBackground(mainPanelColor);
        courseListPage.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // 1. Title and Subtitle Panel
        JPanel coursesTitlePanel = new JPanel();
        coursesTitlePanel.setLayout(new BoxLayout(coursesTitlePanel, BoxLayout.Y_AXIS));
        coursesTitlePanel.setOpaque(false);

        JLabel pageTitle = new JLabel("My Courses");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        pageTitle.setForeground(textColor);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSubtitle = new JLabel("View courses you are teaching, organized by semester");
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pageSubtitle.setForeground(textSecondaryColor);
        pageSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        coursesTitlePanel.add(pageTitle);
        coursesTitlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        coursesTitlePanel.add(pageSubtitle);

        courseListPage.add(coursesTitlePanel, BorderLayout.NORTH);

        // 2. Main Content Area (Tabs + Course Cards)
        JPanel mainCoursesContentPanel = new JPanel(new BorderLayout(0, 15)); // 15px gap
        mainCoursesContentPanel.setOpaque(false);
        courseListPage.add(mainCoursesContentPanel, BorderLayout.CENTER);

        // 3. Tab Bar Container
        RoundedPanel tabBarContainer = new RoundedPanel(8, cardColor, cardColor, 0);
        tabBarContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        tabBarContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainCoursesContentPanel.add(tabBarContainer, BorderLayout.NORTH);

        // 4. Semester Card Panel (for course lists)
        CardLayout semesterCardLayout = new CardLayout();
        JPanel semesterCardPanel = new JPanel(semesterCardLayout);
        semesterCardPanel.setOpaque(false);
        mainCoursesContentPanel.add(semesterCardPanel, BorderLayout.CENTER);

        // --- Populate Tabs and Cards ---
        List<TabButton> semesterTabButtons = new ArrayList<>();
        String firstAvailableSem = "";

        for (String semesterName : semesterCourses.keySet()) {
            if (firstAvailableSem.isEmpty()) {
                firstAvailableSem = semesterName;
            }

            // --- Create the Tab Button ---
            TabButton tabButton = new TabButton(semesterName);
            semesterTabButtons.add(tabButton);
            tabBarContainer.add(tabButton);

            // --- Create the Content Card for this semester ---
            List<facultyCourseClass> coursesForThisSem = semesterCourses.get(semesterName);
            JPanel semesterContentPanel = createSemesterContentPanel(coursesForThisSem);

            // Add the panel to the CardLayout
            semesterCardPanel.add(semesterContentPanel, semesterName);

            // --- Add ActionListener ---
            tabButton.addActionListener(e -> {
                semesterCardLayout.show(semesterCardPanel, semesterName);
                setActiveSemesterTab(tabButton, semesterTabButtons);
            });
        }

        // 5. Set the default active tab
        if (!semesterTabButtons.isEmpty()) {
            setActiveSemesterTab(semesterTabButtons.get(0), semesterTabButtons);
            semesterCardLayout.show(semesterCardPanel, firstAvailableSem);
        }

        return courseListPage;
    }

    /**
     * Creates a scrollable panel containing a list of course cards.
     */
    private JPanel createSemesterContentPanel(List<facultyCourseClass> courses) {
        // Use a standard JPanel as the base, so the JScrollPane can be added
        // to the CardLayout. The RoundedPanel effect is achieved by the
        // JScrollPane's viewport and the cards within.
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        // This panel holds the cards in a vertical list
        JPanel cardHolder = new JPanel();
        cardHolder.setLayout(new BoxLayout(cardHolder, BoxLayout.Y_AXIS));
        cardHolder.setBackground(mainPanelColor); // Match main background
        cardHolder.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Padding

        if (courses.isEmpty()) {
            JLabel noCoursesLabel = new JLabel("You are not assigned to any courses for this term.");
            noCoursesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            noCoursesLabel.setForeground(textSecondaryColor);
            noCoursesLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noCoursesLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
            cardHolder.add(noCoursesLabel);
        } else {
            for (facultyCourseClass course : courses) {
                JPanel courseCard = createCourseCard(course);
                courseCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                cardHolder.add(courseCard);
                cardHolder.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
            }
        }

        // --- Create the Scroll Pane ---
        JScrollPane scrollPane = new JScrollPane(cardHolder);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBackground(mainPanelColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI());

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        return contentPanel;
    }

    /**
     * Helper method to create a single "Course Card" panel using RoundedPanel.
     */
    private JPanel createCourseCard(facultyCourseClass course) {
        // Use RoundedPanel as the card base
        RoundedPanel card = new RoundedPanel(15, cardColor, cardColor, 0);
        card.setLayout(new BorderLayout(15, 10));
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25)); // Padding
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); // Constrain height

        // --- Top: Course Code and Name ---
        JLabel nameLabel = new JLabel(course.getCourseCode() + ": " + course.getCourseName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(textColor); // White title
        card.add(nameLabel, BorderLayout.NORTH);

        // --- Center: Details ---
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false); // Transparent background
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Styled detail labels
        JLabel enrolledLabel = new JLabel("Enrolled Students: " + course.getStudentCount());
        enrolledLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        enrolledLabel.setForeground(textSecondaryColor);
        detailsPanel.add(enrolledLabel);

        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel deptLabel = new JLabel("Department: " + course.getDepartment());
        deptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        deptLabel.setForeground(textSecondaryColor);
        detailsPanel.add(deptLabel);

        card.add(detailsPanel, BorderLayout.CENTER);

        // --- Right: "View Details" Button ---
        // Use the new StyledButton class for a consistent look
        StyledButton viewButton = new StyledButton("View Details");
        viewButton.addActionListener(e -> {
            mainCardLayout.show(mainContentPanel, "DETAIL_" + course.getCourseCode());
        });

        // Add button to a small panel to control its alignment
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewButton);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private JPanel createCourseDetailPanel(facultyCourseClass course) {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // --- 1. Header: Back Button and Title ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // "Back" Button
        StyledButton backButton = new StyledButton("← Back to Courses");
        backButton.addActionListener(e -> mainCardLayout.show(mainContentPanel, "COURSE_LIST"));

        // Wrap button in a FlowLayout to prevent stretching
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backButtonPanel.setOpaque(false);
        backButtonPanel.add(backButton);
        headerPanel.add(backButtonPanel, BorderLayout.WEST);

        // Title
        JLabel titleLabel = new JLabel(course.getCourseName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Add a spacer to the EAST to help center the title
        headerPanel.add(Box.createRigidArea(backButton.getPreferredSize()), BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // --- 2. Main Content Card ---
        RoundedPanel contentCard = new RoundedPanel(15, cardColor, cardColor, 0);
        contentCard.setLayout(new BorderLayout());
        contentCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Panel for info labels
        JPanel detailsGrid = new JPanel(new GridBagLayout());
        detailsGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Add detail rows
        addDetailRow(detailsGrid, gbc, 0, "Course Code:", course.getCourseCode());
        addDetailRow(detailsGrid, gbc, 1, "Department:", course.getDepartment());
        addDetailRow(detailsGrid, gbc, 2, "Credits:", String.valueOf(course.getCourseCredits()));
        addDetailRow(detailsGrid, gbc, 3, "Students Enrolled:", String.valueOf(course.getStudentCount()));

        contentCard.add(detailsGrid, BorderLayout.NORTH);

        // --- 3. Action Buttons Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0)); // Top padding

        StyledButton viewStudentsButton = new StyledButton("View Enrolled Students");
        viewStudentsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, "Opening student list for " + course.getCourseCode());
//            new ViewStudentsFrame(course.getCourseCode()).setVisible(true);
        });

        StyledButton updateScoresButton = new StyledButton("Update Scores");
        updateScoresButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, "Opening score update for " + course.getCourseCode());
            new UpdateScoresFrame(course.getCourseCode()).setVisible(true);
        });

        StyledButton setGradingPolicyButton = new StyledButton("Set Grading Policy");
        setGradingPolicyButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, "Opening grading policy for " + course.getCourseCode());
            new GradingPolicyFrame(course.getCourseCode()).setVisible(true);
        });

        buttonPanel.add(viewStudentsButton);
        buttonPanel.add(updateScoresButton);
        buttonPanel.add(setGradingPolicyButton);

        // Add button panel below the grid
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 5, 0, 5);
        detailsGrid.add(buttonPanel, gbc);

        // Add spacer to push content up
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        detailsGrid.add(new JLabel(""), gbc);

        panel.add(contentCard, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Helper to add a styled row (Label + Value) to the detail grid.
     */
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int y, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0; // Label column
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelComponent.setForeground(textSecondaryColor);
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Value column
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        valueComponent.setForeground(textColor);
        panel.add(valueComponent, gbc);
    }


    /**
     * Sets the active state for the custom semester tabs.
     * (Copied from StudentCoursesPanel)
     */
    private void setActiveSemesterTab(TabButton activeButton, List<TabButton> allTabs) {
        for (TabButton button : allTabs) {
            button.setActive(false);
        }
        activeButton.setActive(true);
    }

    // ---
    // --- INNER CLASSES (Copied from StudentCoursesPanel or newly created) ---
    // ---

    /**
     * A custom button for the semester tabs.
     * (Copied from StudentCoursesPanel)
     */
    private class TabButton extends JButton {
        private boolean isActive = false;
        private boolean isHovered = false;
        private int arc = 8;

        public TabButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setForeground(textSecondaryColor);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    if (!isActive) setForeground(textColor);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    if (!isActive) setForeground(textSecondaryColor);
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setForeground(isActive ? textColor : textSecondaryColor);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(buttonColorGlow);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            } else if (isHovered) {
                g2.setColor(borderColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            } else {
                g2.setColor(cardColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            }

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    /**
     * A new custom button for primary actions (View, Back, etc.)
     * Inspired by TabButton, but uses the primary button colors.
     */
    private class StyledButton extends JButton {
        private boolean isHovered = false;
        private int arc = 10;

        public StyledButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22)); // Padding
            setForeground(textColor); // Text is always white

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isHovered) {
                g2.setColor(buttonColorGlow); // Brighten on hover
            } else {
                g2.setColor(buttonColor); // Normal color
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            super.paintComponent(g2);
            g2.dispose();
        }
    }


    /**
     * A JPanel with rounded corners.
     * (Copied from StudentCoursesPanel)
     */
    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;
        private Color borderColor;
        private int borderThickness;

        public RoundedPanel(int radius, Color bgColor, Color borderColor, int borderThickness) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            this.borderColor = borderColor;
            this.borderThickness = borderThickness;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            if (borderThickness > 0) {
                g2.setColor(this.borderColor);
                g2.setStroke(new BasicStroke(this.borderThickness));
                float halfStroke = this.borderThickness / 2.0f;
                g2.draw(new RoundRectangle2D.Float(
                        halfStroke,
                        halfStroke,
                        getWidth() - this.borderThickness,
                        getHeight() - this.borderThickness,
                        cornerRadius,
                        cornerRadius
                ));
            }
            g2.dispose();
        }
    }

    /**
     * Inner class for a custom styled scrollbar.
     * (Copied from StudentCoursesPanel)
     */
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            // Use borderColor for the thumb and cardColor for the track
            // to be less intrusive
            this.thumbColor = borderColor.brighter();
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

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 10, 10);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
            g2.dispose();
        }
    }

    /**
     * LOCAL PLACEHOLDER CLASS
     * (Copied from original facultyCourseList)
     */
    private class facultyCourseClass {
        private String courseCode;
        private String courseName;
        private int courseCredits;
        private String department;
        private int studentCount;

        public facultyCourseClass(String code, String name, int credits, String dept, int count) {
            this.courseCode = code;
            this.courseName = name;
            this.courseCredits = credits;
            this.department = dept;
            this.studentCount = count;
        }

        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public int getCourseCredits() { return courseCredits; }
        public String getDepartment() { return department; }
        public int getStudentCount() { return studentCount; }
    }
}