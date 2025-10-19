package ui.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CourseManagementFrame extends JFrame {

    // --- Style Colors ---
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color linkColor = new Color(100, 180, 255);
    private Color textColor = Color.WHITE;

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public CourseManagementFrame() {
        super("Course Management");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null); // Center on screen
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(mainPanelColor);

        createCoursePages();

        add(mainContentPanel);

        cardLayout.show(mainContentPanel, "COURSE_LIST");
    }

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

        for (Course course : courses) {
            JLabel courseLink = new JLabel(course.getName() + " (" + course.getCode() + ")");
            courseLink.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            courseLink.setForeground(linkColor);
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

            JPanel detailPanel = createCourseDetailPanel(course);
            mainContentPanel.add(detailPanel, "DETAIL_" + course.getCode());
        }

        mainContentPanel.add(courseListPanel, "COURSE_LIST");
    }

    private JPanel createCourseDetailPanel(Course course) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(mainPanelColor);

        JButton backButton = createMenuButton("← Back");
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE_LIST"));
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Details for " + course.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        // --- Details Panel with GridBagLayout ---
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(mainPanelColor);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Add detail rows...
        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(createDetailLabel("Course Name:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createValueLabel(course.getName()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        detailsPanel.add(createDetailLabel("Course Code:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createValueLabel(course.getCode()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        detailsPanel.add(createDetailLabel("Students Enrolled:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createValueLabel(String.valueOf(course.getStudentCount())), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        detailsPanel.add(createDetailLabel("Credits:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createValueLabel(String.valueOf(course.getCredits())), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        detailsPanel.add(createDetailLabel("Department:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(createValueLabel(course.getDepartment()), gbc);

        // --- NEW POSITION: Update Scores Button is now part of the GridBagLayout ---
        JButton updateScoresButton = new JButton("Update Scores");
        updateScoresButton.setBackground(buttonColor);
        updateScoresButton.setForeground(textColor);
        updateScoresButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateScoresButton.setFocusPainted(false);
        updateScoresButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateScoresButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        updateScoresButton.addActionListener(e -> new UpdateScoresFrame().setVisible(true));

        // Add button to the next row, aligned left
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span two columns
        gbc.insets = new Insets(20, 5, 8, 5); // Add top margin
        detailsPanel.add(updateScoresButton, gbc);

        // Filler to push content to the top
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        detailsPanel.add(new JLabel(""), gbc);

        // Wrapper panel to prevent horizontal stretching
        JPanel containerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        containerPanel.setBackground(mainPanelColor);
        containerPanel.add(detailsPanel);
        panel.add(containerPanel, BorderLayout.CENTER);

        // The button panel at the SOUTH has been removed.

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