// File: ui/dashboard/CourseManagementFrame.java

package ui.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A dedicated frame for managing courses, listing them,
 * and showing details for a selected course.
 */
public class CourseManagementFrame extends JFrame {

    // --- Style Colors ---
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color linkColor = new Color(100, 180, 255);
    private Color textColor = Color.WHITE;

    // --- Main Layout Components ---
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public CourseManagementFrame() {
        super("Course Management");

        // --- Basic Frame Setup ---
        // DISPOSE_ON_CLOSE only closes this window, not the whole app
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null); // Center on screen
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        // --- Create Content ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(mainPanelColor);

        createCoursePages(); // Helper to create the list and detail pages

        // Add the main panel to the frame
        add(mainContentPanel);

        // Show the initial "course list" card
        cardLayout.show(mainContentPanel, "COURSE_LIST");
    }

    /**
     * Creates the course list panel and a detail panel for each course.
     */
    private void createCoursePages() {
        // --- Course List Panel ---
        JPanel courseListPanel = new JPanel();
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
        courseListPanel.setBackground(mainPanelColor);
        courseListPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel scoresTitle = new JLabel("Select a Course");
        scoresTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        scoresTitle.setForeground(textColor);
        scoresTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        courseListPanel.add(scoresTitle);
        courseListPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- Hardcoded Course Data ---
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Data Structures", "CS201", 120, 4, "CSE"));
        courses.add(new Course("Algorithms", "CS301", 95, 4, "CSE"));
        courses.add(new Course("Operating Systems", "CS302", 88, 3, "CSE"));
        courses.add(new Course("Database Management", "CS305", 110, 3, "IT"));

        // Create clickable labels for each course and their corresponding detail panels
        for (Course course : courses) {
            JLabel courseLink = new JLabel(course.getName() + " (" + course.getCode() + ")");
            courseLink.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            courseLink.setForeground(buttonColor);
            courseLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
            courseLink.setAlignmentX(Component.LEFT_ALIGNMENT);

            courseLink.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cardLayout.show(mainContentPanel, "DETAIL_" + course.getCode());
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    courseLink.setText("<html><u>" + course.getName() + " (" + course.getCode() + ")</u></html>");
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    courseLink.setText(course.getName() + " (" + course.getCode() + ")");
                }
            });

            courseListPanel.add(courseLink);
            courseListPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            // Create a detail panel for this course and add it to the CardLayout
            JPanel detailPanel = createCourseDetailPanel(course);
            mainContentPanel.add(detailPanel, "DETAIL_" + course.getCode());
        }

        // Add the fully populated list panel to the main card layout
        mainContentPanel.add(courseListPanel, "COURSE_LIST");
    }

    /**
     * A helper method to create the generic course detail panel.
     */
    private JPanel createCourseDetailPanel(Course course) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // --- NEW: Header Panel to hold both the back button and the title ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(mainPanelColor);

        // --- Back Button (Now at top left) ---
        JButton backButton = createMenuButton("← Back");
        backButton.setBorderPainted(false); // No border for a cleaner look
        backButton.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE_LIST"));

        // Add button to the left of the header
        headerPanel.add(backButton, BorderLayout.WEST);

        // --- Title Label ---
        JLabel titleLabel = new JLabel("Details for " + course.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the title

        // Add title to the center of the header
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Add the entire header panel to the top of the main panel
        panel.add(headerPanel, BorderLayout.NORTH);


        // --- Details Grid (Unchanged) ---
        JPanel detailsGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        detailsGrid.setBackground(mainPanelColor);
        detailsGrid.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Add top padding
        detailsGrid.add(createDetailLabel("Course Name:"));
        detailsGrid.add(createValueLabel(course.getName()));
        detailsGrid.add(createDetailLabel("Course Code:"));
        detailsGrid.add(createValueLabel(course.getCode()));
        detailsGrid.add(createDetailLabel("Students Enrolled:"));
        detailsGrid.add(createValueLabel(String.valueOf(course.getStudentCount())));
        detailsGrid.add(createDetailLabel("Credits:"));
        detailsGrid.add(createValueLabel(String.valueOf(course.getCredits())));
        detailsGrid.add(createDetailLabel("Department:"));
        detailsGrid.add(createValueLabel(course.getDepartment()));
        panel.add(detailsGrid, BorderLayout.CENTER);

        // The old button panel at the SOUTH has been removed.

        return panel;
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

    /**
     * A private inner class to act as a simple data structure for a course.
     */
    private static class Course {
        private String name, code, department;
        private int studentCount, credits;

        public Course(String name, String code, int studentCount, int credits, String department) {
            this.name = name; this.code = code; this.studentCount = studentCount;
            this.credits = credits; this.department = department;
        }
        public String getName() { return name; }
        public String getCode() { return code; }
        public int getStudentCount() { return studentCount; }
        public int getCredits() { return credits; }
        public String getDepartment() { return department; }
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
}