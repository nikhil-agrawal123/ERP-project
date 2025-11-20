package ui.FacultyFrame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import dbClasses.facultyCourseClass;

import middleware.facultyService;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

public class FacultyCoursesPanel extends JPanel {

    // --- Services & Data ---
    private facultyService facultySvc;
    private String facultyUsername;
    private Map<String, List<facultyCourseClass>> semesterCourses;

    // --- UI Components ---
    private CardLayout mainCardLayout;
    private JPanel mainContentPanel;

    // --- UI Color Palette (DEFINED INTERNALLY) ---
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);

    public FacultyCoursesPanel(String username) {
        super();

        this.facultyUsername = username;
        this.facultySvc = new facultyService();

        // --- Hardcode Data ---
        this.semesterCourses = new LinkedHashMap<>();
        List<facultyCourseClass> fall2025 = new ArrayList<>();
        fall2025.add(new facultyCourseClass("Operating Systems", "CS301", 75, 4, "CSE"));
        fall2025.add(new facultyCourseClass("Linear Algebra","MTH201",  120,4, "MATH"));
        List<facultyCourseClass> spring2025 = new ArrayList<>();
        spring2025.add(new facultyCourseClass("Intro to Programming","CS101", 150 ,4, "CSE"));
        List<facultyCourseClass> fall2024 = new ArrayList<>();
        fall2024.add(new facultyCourseClass("Database Systems","CS450", 60, 4, "CSE"));
        semesterCourses.put("Monsoon 2025", fall2025);
        semesterCourses.put("Winter 2024", spring2025);
        semesterCourses.put("Monsoon 2024", fall2024);

        // --- Configure this JPanel ---
        setLayout(new BorderLayout());
        setBackground(mainPanelColor);

        // --- Main Content Panel (with CardLayout) ---
        mainCardLayout = new CardLayout();
        mainContentPanel = new JPanel(mainCardLayout);
        mainContentPanel.setOpaque(false);
        add(mainContentPanel, BorderLayout.CENTER);

        // --- Create and add the two main views ---
        JPanel courseListPage = createCourseListPage();
        mainContentPanel.add(courseListPage, "COURSE_LIST");

        for (String semester : semesterCourses.keySet()) {
            for (facultyCourseClass course : semesterCourses.get(semester)) {
                JPanel detailPanel = createCourseDetailPanel(course, semester);
                mainContentPanel.add(detailPanel, "DETAIL_" + course.getCourseCode());
            }
        }
        mainCardLayout.show(mainContentPanel, "COURSE_LIST");
    }

    /**
     * Creates the main "Course List" page.
     */
    private JPanel createCourseListPage() {
        JPanel courseListPage = new JPanel(new BorderLayout(0, 15));
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
        JPanel mainCoursesContentPanel = new JPanel(new BorderLayout(0, 15));
        mainCoursesContentPanel.setOpaque(false);
        courseListPage.add(mainCoursesContentPanel, BorderLayout.CENTER);

        // 3. Tab Bar Container (Using public RoundedPanel)
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
        List<RoundedButton> semesterTabButtons = new ArrayList<>(); // <-- Changed to RoundedButton
        String firstAvailableSem = "";

        for (String semesterName : semesterCourses.keySet()) {
            if (firstAvailableSem.isEmpty()) {
                firstAvailableSem = semesterName;
            }

            // --- Create the Tab Button (Using public RoundedButton) ---
            RoundedButton tabButton = new RoundedButton(
                    semesterName,
                    cardColor,        // normal
                    borderColor,      // hover
                    buttonColorGlow,  // pressed
                    buttonColor,      // active gradient start
                    buttonColorGlow,  // active gradient end
                    8                 // arc
            );
            // Manually set font, padding, and initial color
            tabButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            tabButton.setForeground(textSecondaryColor);
            tabButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            semesterTabButtons.add(tabButton);
            tabBarContainer.add(tabButton);

            // --- Create the Content Card for this semester ---
            List<facultyCourseClass> coursesForThisSem = semesterCourses.get(semesterName);
            JPanel semesterContentPanel = createSemesterContentPanel(coursesForThisSem);
            semesterCardPanel.add(semesterContentPanel, semesterName);

            // --- Add ActionListener ---
            tabButton.addActionListener(e -> {
                semesterCardLayout.show(semesterCardPanel, semesterName);
                setActiveSemesterTab(tabButton, semesterTabButtons);
            });
        }

        // 5. Set the default active tab
        if (!semesterTabButtons.isEmpty()) {
            setActiveSemesterTab(semesterTabButtons.getFirst(), semesterTabButtons);
            semesterCardLayout.show(semesterCardPanel, firstAvailableSem);
        }

        return courseListPage;
    }

    /**
     * Creates a scrollable panel containing a list of course cards.
     */
    private JPanel createSemesterContentPanel(List<facultyCourseClass> courses) {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        JPanel cardHolder = new JPanel();
        cardHolder.setLayout(new BoxLayout(cardHolder, BoxLayout.Y_AXIS));
        cardHolder.setBackground(mainPanelColor);
        cardHolder.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

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
                cardHolder.add(Box.createRigidArea(new Dimension(0, 15)));
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
     * Helper method to create a single "Course Card" panel.
     */
    private JPanel createCourseCard(facultyCourseClass course) {
        // Use RoundedPanel as the card base
        RoundedPanel card = new RoundedPanel(15, cardColor, cardColor, 0);
        card.setLayout(new BorderLayout(15, 10));
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel nameLabel = new JLabel(course.getCourseCode() + ": " + course.getCourseName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(textColor);
        card.add(nameLabel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));

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

        // --- MODIFIED ---
        // Use the public RoundedButton gradient constructor
        RoundedButton viewButton = new RoundedButton(
                "View Details",
                buttonColor,      // gradStart
                buttonColorGlow,  // gradEnd
                10                // arc
        );
        viewButton.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Match old style
        viewButton.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22)); // Match old style

        viewButton.addActionListener(e -> {
            mainCardLayout.show(mainContentPanel, "DETAIL_" + course.getCourseCode());
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewButton);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    /**
     * Creates the "Course Detail" panel for a specific course.
     */
    private JPanel createCourseDetailPanel(facultyCourseClass course, String semester) {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // --- 1. Header: Back Button and Title ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // --- MODIFIED ---
        // "Back" Button (Using public RoundedButton gradient constructor)
        RoundedButton backButton = new RoundedButton(
                "← Back to Courses",
                buttonColor,      // gradStart
                buttonColorGlow,  // gradEnd
                10                // arc
        );
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backButton.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        backButton.addActionListener(e -> mainCardLayout.show(mainContentPanel, "COURSE_LIST"));

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
        headerPanel.add(Box.createRigidArea(backButton.getPreferredSize()), BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // --- 2. Main Content Card (Using public RoundedPanel) ---
        RoundedPanel contentCard = new RoundedPanel(15, cardColor, cardColor, 0);
        contentCard.setLayout(new BorderLayout());
        contentCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel detailsGrid = new JPanel(new GridBagLayout());
        detailsGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        addDetailRow(detailsGrid, gbc, 0, "Course Code:", course.getCourseCode());
        addDetailRow(detailsGrid, gbc, 1, "Department:", course.getDepartment());
        addDetailRow(detailsGrid, gbc, 2, "Credits:", String.valueOf(course.getCourseCredits()));
        addDetailRow(detailsGrid, gbc, 3, "Students Enrolled:", String.valueOf(course.getStudentCount()));
        contentCard.add(detailsGrid, BorderLayout.NORTH);

        // --- 3. Action Buttons Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        // --- MODIFIED ---
        // Create buttons using the public RoundedButton gradient constructor
        RoundedButton viewStudentsButton = new RoundedButton("View Enrolled Students", buttonColor, buttonColorGlow, 10);
        RoundedButton updateScoresButton = new RoundedButton("Update Scores", buttonColor, buttonColorGlow, 10);
        RoundedButton setGradingPolicyButton = new RoundedButton("Set Grading Policy", buttonColor, buttonColorGlow, 10);

        // Style buttons (can be factorized)
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 15);
        Border buttonBorder = BorderFactory.createEmptyBorder(12, 22, 12, 22);

        viewStudentsButton.setFont(buttonFont);
        viewStudentsButton.setBorder(buttonBorder);
        viewStudentsButton.addActionListener(e -> {
            ShowStudentsFrame showStudentsFrame = new ShowStudentsFrame(course.getCourseCode(), course.getCourseName(), semester);
            showStudentsFrame.setVisible(true);
        });

        updateScoresButton.setFont(buttonFont);
        updateScoresButton.setBorder(buttonBorder);
        updateScoresButton.addActionListener(e ->
        {
            // --- MODIFIED --- Pass the correct course code
            UpdateScoresFrame updateScoresFrame = new UpdateScoresFrame(course.getCourseCode(), "INST-CS-501",  semester);
            updateScoresFrame.setVisible(true);
        });

        setGradingPolicyButton.setFont(buttonFont);
        setGradingPolicyButton.setBorder(buttonBorder);
        setGradingPolicyButton.addActionListener(e ->
        {
            GradingPolicyFrame gradingPolicyFrame = new GradingPolicyFrame(course.getCourseCode(), course.getCourseName(),"INST-CS-501", semester);
            gradingPolicyFrame.setVisible(true);
        });

        buttonPanel.add(viewStudentsButton);
        buttonPanel.add(updateScoresButton);
        buttonPanel.add(setGradingPolicyButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 5, 0, 5);
        detailsGrid.add(buttonPanel, gbc);

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
        gbc.weightx = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelComponent.setForeground(textSecondaryColor);
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        valueComponent.setForeground(textColor);
        panel.add(valueComponent, gbc);
    }


    /**
     * Sets the active state for the custom semester tabs.
     * UPDATED to use RoundedButton and manage text color.
     */
    private void setActiveSemesterTab(RoundedButton activeButton, List<RoundedButton> allTabs) {
        for (RoundedButton button : allTabs) {
            button.setActive(false);
            button.setForeground(textSecondaryColor); // Set inactive text color
        }
        activeButton.setActive(true);
        activeButton.setForeground(textColor); // Set active text color
    }

    /**
     * Inner class for a custom styled scrollbar.
     */
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
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
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
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
}