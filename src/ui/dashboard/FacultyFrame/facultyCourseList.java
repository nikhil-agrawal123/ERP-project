package ui.dashboard.FacultyFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap; // Use LinkedHashMap to maintain semester order

import dbClasses.facultyCourseClass; // Assuming this class is available
import middleware.facultyService;

public class facultyCourseList extends JFrame {

    // --- Style Colors ---
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color cardColor = new Color(60, 60, 60);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;
    private Color selectionPanelColor = new Color(45, 45, 45); // Slightly darker for contrast

    private Border courseCardBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(buttonColor.darker(), 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
    );

    // --- Services ---
    private facultyService faculty = new facultyService(); // Kept, but not used for hardcoded data

    // --- UI Components ---
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JPanel courseCardHolderPanel; // This panel will be dynamically updated
    private Map<String, List<facultyCourseClass>> semesterCourses; // Hardcoded data map

    public facultyCourseList(String userId) {
        super("Course Management");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(mainPanelColor);

        // --- Initialize and create pages ---
        createCoursePages(userId);

        add(mainContentPanel);
        cardLayout.show(mainContentPanel, "COURSE_LIST");
    }

    /**
     * Creates all pages: the main list page and all detail pages.
     */
    private void createCoursePages(String userId) {
        // --- 1. Hardcode the Data ---
        // We use a Map to group courses by semester.
        // NOTE: This REPLACES the faculty.getAllCourses() call for this demo.
        // You would replace this with real data logic.
        this.semesterCourses = new LinkedHashMap<>();

        // --- Create dummy facultyCourseClass objects (assuming a constructor or setters)
        // Since I don't have the class definition, I'll assume it's a POJO
        // and create a local placeholder class at the bottom for this example.
        // If your facultyCourseClass is different, this logic will need to adapt.

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


        // --- 2. Create the main "Course List" page (which is a BorderLayout) ---
        JPanel courseListPage = new JPanel(new BorderLayout(0, 10)); // 10px v-gap
        courseListPage.setBackground(mainPanelColor);


        // --- 3. Create the Top Selection Panel ---
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        selectionPanel.setBackground(selectionPanelColor);
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25)); // Padding

        JLabel selectLabel = new JLabel("Select Semester:");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        selectLabel.setForeground(textColor);
        selectionPanel.add(selectLabel);

        // Create the ComboBox (dropdown)
        JComboBox<String> semesterComboBox = new JComboBox<>(
                semesterCourses.keySet().toArray(new String[0])
        );
        semesterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        semesterComboBox.setPreferredSize(new Dimension(250, 30));
        selectionPanel.add(semesterComboBox);

        courseListPage.add(selectionPanel, BorderLayout.NORTH); // Add to top of page


        // --- 4. Create the Center Panel (for the course cards) ---
        // This is the panel that will be cleared and updated
        courseCardHolderPanel = new JPanel();
        courseCardHolderPanel.setLayout(new BoxLayout(courseCardHolderPanel, BoxLayout.Y_AXIS));
        courseCardHolderPanel.setBackground(mainPanelColor);
        courseCardHolderPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        // Put the card holder in a Scroll Pane
        JScrollPane scrollPane = new JScrollPane(courseCardHolderPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(mainPanelColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        courseListPage.add(scrollPane, BorderLayout.CENTER); // Add to center of page


        // --- 5. Add the main list page to the CardLayout ---
        mainContentPanel.add(courseListPage, "COURSE_LIST");


        // --- 6. Create and add ALL Detail Panels (from all semesters) ---
        // This is crucial. The CardLayout needs all panels to exist upfront
        // so it can switch to them.
        for (String semester : semesterCourses.keySet()) {
            for (facultyCourseClass course : semesterCourses.get(semester)) {
                JPanel detailPanel = createCourseDetailPanel(course);
                mainContentPanel.add(detailPanel, "DETAIL_" + course.getCourseCode());
            }
        }


        // --- 7. Set up the ComboBox listener ---
        semesterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSemester = (String) semesterComboBox.getSelectedItem();
                if (selectedSemester != null) {
                    // Get the list of courses for that semester
                    List<facultyCourseList.facultyCourseClass> courses = semesterCourses.get(selectedSemester);
                    // Update the UI
                    updateCourseList(courses);
                }
            }
        });

        // --- 8. Trigger the initial load ---
        if (semesterComboBox.getItemCount() > 0) {
            semesterComboBox.setSelectedIndex(0);
        }
    }

    /**
     * Clears the courseCardHolderPanel and repopulates it with
     * cards from the provided list.
     */
    private void updateCourseList(List<facultyCourseClass> courses) {
        // 1. Clear the existing cards
        courseCardHolderPanel.removeAll();

        // 2. Add title
        JLabel scoresTitle = new JLabel("Courses You Teach");
        scoresTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        scoresTitle.setForeground(textColor);
        scoresTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        courseCardHolderPanel.add(scoresTitle);
        courseCardHolderPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // 3. Add new cards for the selected semester
        if (courses.isEmpty()) {
            JLabel noCoursesLabel = new JLabel("You are not assigned to any courses for this term.");
            noCoursesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            noCoursesLabel.setForeground(textColor);
            courseCardHolderPanel.add(noCoursesLabel);
        } else {
            for (facultyCourseClass course : courses) {
                JPanel courseCard = createCourseCard(course);
                courseCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                courseCardHolderPanel.add(courseCard);
                courseCardHolderPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
            }
        }

        // 4. Refresh the panel
        courseCardHolderPanel.revalidate();
        courseCardHolderPanel.repaint();
    }


    /**
     * Helper method to create a single "Course Card" panel,
     * inspired by MyCoursesFrame.
     */
    private JPanel createCourseCard(facultyCourseClass course) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(cardColor); // Use new card color
        card.setBorder(courseCardBorder);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140)); // Constrain height

        // --- Top: Course Code and Name ---
        JLabel nameLabel = new JLabel(course.getCourseCode() + ": " + course.getCourseName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(buttonColor); // Use theme color for title
        card.add(nameLabel, BorderLayout.NORTH);

        // --- Center: Details ---
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false); // Transparent background
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Use helper to create styled labels
        detailsPanel.add(createDetailLabel("Enrolled Students: " + course.getStudentCount()));
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(createDetailLabel("Department: " + course.getDepartment()));

        card.add(detailsPanel, BorderLayout.CENTER);

        // --- Right: "View Details" Button ---
        JButton viewButton = new JButton("View Details");
        viewButton.setBackground(buttonColor.darker());
        viewButton.setForeground(textColor);
        viewButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewButton.setFocusPainted(false);
        viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewButton.addActionListener(e -> {
            // This is the action the old label used to perform
            cardLayout.show(mainContentPanel, "DETAIL_" + course.getCourseCode());
        });

        // Add button to a small panel for padding
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 5));
        buttonPanel.add(viewButton, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }


    private JPanel createCourseDetailPanel(facultyCourseClass course) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(mainPanelColor);

        // --- MODIFIED: Use the createMenuButton for consistent styling ---
        JButton backButton = createMenuButton("← Back");
        backButton.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE_LIST"));

        // --- Wrap button in a FlowLayout to prevent stretching ---
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backButtonPanel.setOpaque(false);
        backButtonPanel.add(backButton);
        headerPanel.add(backButtonPanel, BorderLayout.WEST);


        JLabel titleLabel = new JLabel("Details for " + course.getCourseName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(mainPanelColor);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(createDetailLabel("Course Name:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createValueLabel(course.getCourseName()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(createDetailLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createValueLabel(course.getCourseCode()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(createDetailLabel("Students Enrolled:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createValueLabel(String.valueOf(course.getStudentCount())), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(createDetailLabel("Credits:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createValueLabel(String.valueOf(course.getCourseCredits())), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        detailsPanel.add(createDetailLabel("Department:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createValueLabel(course.getDepartment()), gbc);

        // --- Button 1: "View Enrolled Students" (Renamed) ---
        JButton viewStudentsButton = new JButton("View Enrolled Students");
        styleActionButton(viewStudentsButton); // Apply common style
        viewStudentsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, "Opening student list for " + course.getCourseCode());
        });

        JButton updateScoresButton = new JButton("Update Scores");
        styleActionButton(updateScoresButton); // Apply common style
        updateScoresButton.addActionListener(e -> new UpdateScoresFrame(course.getCourseCode()).setVisible(true));

        // --- Button 3: "Set Grading Policy" (New) ---
        JButton setGradingPolicyButton = new JButton("Set Grading Policy");
        styleActionButton(setGradingPolicyButton); // Apply common style
        setGradingPolicyButton.addActionListener(e -> {
            new GradingPolicyFrame(course.getCourseCode()).setVisible(true);
        });

        // --- Panel to hold all three buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0)); // 15px horizontal gap
        buttonPanel.setBackground(mainPanelColor);
        buttonPanel.add(viewStudentsButton);
        buttonPanel.add(updateScoresButton);
        buttonPanel.add(setGradingPolicyButton); // Added the new button

        // --- Add the button panel to the GridBagLayout ---
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        detailsPanel.add(buttonPanel, gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0; // Pushes content up
        detailsPanel.add(new JLabel(""), gbc); // Empty spacer

        JPanel containerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        containerPanel.setBackground(mainPanelColor);
        containerPanel.add(detailsPanel);
        panel.add(containerPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Helper method to style the main action buttons
     */
    private void styleActionButton(JButton button) {
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(textColor);
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(textColor);
        return label;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // --- MODIFIED: Removed MaxSize and Alignment, set padding ---
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    // ---
    // --- LOCAL PLACEHOLDER CLASS ---
    // ---
    // This is a placeholder to make the hardcoded example work.
    // You should REMOVE this and use your REAL dbClasses.facultyCourseClass.
    // Make sure your real class has a constructor that can be used.
    // ---
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