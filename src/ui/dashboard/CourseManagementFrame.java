package ui.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import dbClasses.facultyCourseClass;
import middleware.facultyService;

public class CourseManagementFrame extends JFrame {

    // --- Style Colors ---
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color linkColor = new Color(100, 180, 255);
    private Color textColor = Color.WHITE;
    
    private facultyService faculty = new facultyService();

    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public CourseManagementFrame(String userId) {
        super("Course Management");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null); // Center on screen
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(mainPanelColor);

        createCoursePages(userId);

        add(mainContentPanel);
        cardLayout.show(mainContentPanel, "COURSE_LIST");
    }

    private void createCoursePages(String userId) {
        JPanel courseListPanel = new JPanel();
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
        courseListPanel.setBackground(mainPanelColor);
        courseListPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel scoresTitle = new JLabel("Select a Course You Teach");
        scoresTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        scoresTitle.setForeground(textColor);
        scoresTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        courseListPanel.add(scoresTitle);
        courseListPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        List<facultyCourseClass> courses = faculty.getAllCourses("INST-CS-501");

        if (courses.isEmpty()) {
            JLabel noCoursesLabel = new JLabel("You are not assigned to any courses for the current term.");
            noCoursesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            noCoursesLabel.setForeground(textColor);
            courseListPanel.add(noCoursesLabel);
        }

        for (facultyCourseClass course : courses) {
            JLabel courseLink = new JLabel(course.getCourseName() + " (" + course.getCourseCode() + ")");
            courseLink.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            courseLink.setForeground(linkColor);
            courseLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
            courseLink.setAlignmentX(Component.LEFT_ALIGNMENT);

            courseLink.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cardLayout.show(mainContentPanel, "DETAIL_" + course.getCourseCode());
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    courseLink.setText("<html><u>" + course.getCourseName() + " (" + course.getCourseCode() + ")</u></html>");
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    courseLink.setText(course.getCourseName() + " (" + course.getCourseCode() + ")");
                }
            });

            courseListPanel.add(courseLink);
            courseListPanel.add(Box.createRigidArea(new Dimension(0, 15)));

            JPanel detailPanel = createCourseDetailPanel(course);
            mainContentPanel.add(detailPanel, "DETAIL_" + course.getCourseCode());
        }

        mainContentPanel.add(courseListPanel, "COURSE_LIST");
    }

    private JPanel createCourseDetailPanel(facultyCourseClass course) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(mainPanelColor);

        JButton backButton = createMenuButton("← Back");
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE_LIST"));
        headerPanel.add(backButton, BorderLayout.WEST);

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
        viewStudentsButton.setBackground(buttonColor);
        viewStudentsButton.setForeground(textColor);
        viewStudentsButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewStudentsButton.setFocusPainted(false);
        viewStudentsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewStudentsButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        viewStudentsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, "Opening student list for " + course.getCourseCode());
        });

        JButton updateScoresButton = new JButton("Update Scores");
        updateScoresButton.setBackground(buttonColor);
        updateScoresButton.setForeground(textColor);
        updateScoresButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateScoresButton.setFocusPainted(false);
        updateScoresButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateScoresButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        updateScoresButton.addActionListener(e -> new UpdateScoresFrame(course.getCourseCode()).setVisible(true));

        // --- Button 3: "Set Grading Policy" (New) ---
        JButton setGradingPolicyButton = new JButton("Set Grading Policy");
        setGradingPolicyButton.setBackground(buttonColor);
        setGradingPolicyButton.setForeground(textColor);
        setGradingPolicyButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        setGradingPolicyButton.setFocusPainted(false);
        setGradingPolicyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setGradingPolicyButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
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
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }
}
