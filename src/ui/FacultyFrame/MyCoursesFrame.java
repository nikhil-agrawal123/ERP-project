package ui.FacultyFrame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * A new frame to display Courses, organized by semester.
 * Uses a JSplitPane for a master-detail view.
 */
public class MyCoursesFrame extends JFrame {

    // --- Style Colors (copied from your Dashboard) ---
    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;
    private Border courseCardBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(buttonColor.darker(), 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
    );

    // --- Components ---
    private JList<String> semesterList;
    private DefaultListModel<String> semesterListModel;
    private JPanel courseDetailPanel; // This panel will hold the course cards
    private JScrollPane detailScrollPane;
    private JLabel initialDetailLabel;

    // --- Services ---
    private CourseService courseService; // A placeholder service for fetching data

    private String facultyID;

    public MyCoursesFrame(String facultyID, String facultyName) {
        super("Course Management for " + facultyName);
        this.facultyID = facultyID;
        this.courseService = new CourseService(); // Initialize your service

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose, don't exit
        setSize(1100, 750);
        setLocationRelativeTo(null); // Center on screen
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());
        setLayout(new BorderLayout());

        // --- Create Master-Detail Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250); // Width of the semester list
        splitPane.setLeftComponent(createSemesterListPanel());
        splitPane.setRightComponent(createDetailPanel());
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splitPane.setOpaque(false);
        splitPane.setDividerSize(8);

        add(splitPane, BorderLayout.CENTER);

        // --- Load Initial Data ---
        loadSemesters();
    }

    /**
     * Creates the left-side panel containing the list of semesters.
     */
    private Component createSemesterListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(sideMenuColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Semesters", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(textColor);
        panel.add(title, BorderLayout.NORTH);

        semesterListModel = new DefaultListModel<>();
        semesterList = new JList<>(semesterListModel);
        semesterList.setBackground(sideMenuColor);
        semesterList.setForeground(textColor);
        semesterList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        semesterList.setSelectionBackground(buttonColor);
        semesterList.setSelectionForeground(Color.BLACK);
        semesterList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Add padding
                return label;
            }
        });


        // --- Add the List Selection Listener ---
        semesterList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedSemester = semesterList.getSelectedValue();
                    if (selectedSemester != null) {
                        updateCourseDetails(selectedSemester);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(semesterList);
        scrollPane.setBorder(BorderFactory.createLineBorder(bgColor));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the right-side panel that will display course details.
     */
    private Component createDetailPanel() {
        // This outer panel uses BorderLayout to center the initial message
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(mainPanelColor);

        // courseDetailPanel is the one we will add/remove cards from
        courseDetailPanel = new JPanel();
        courseDetailPanel.setLayout(new BoxLayout(courseDetailPanel, BoxLayout.Y_AXIS));
        courseDetailPanel.setBackground(mainPanelColor);
        courseDetailPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Initial message
        initialDetailLabel = new JLabel("Select a semester to view courses", SwingConstants.CENTER);
        initialDetailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        initialDetailLabel.setForeground(textColor.darker());
        wrapperPanel.add(initialDetailLabel, BorderLayout.CENTER);

        // We put the courseDetailPanel inside a JScrollPane
        detailScrollPane = new JScrollPane(courseDetailPanel);
        detailScrollPane.setBorder(BorderFactory.createEmptyBorder());
        detailScrollPane.getViewport().setBackground(mainPanelColor);

        return wrapperPanel;
    }

    /**
     * Loads the hardcoded semesters into the JList.
     */
    private void loadSemesters() {
        // --- Hardcoded Data ---
        List<String> semesters = courseService.getSemesters(this.facultyID);
        semesterListModel.clear();
        for (String semester : semesters) {
            semesterListModel.addElement(semester);
        }
    }

    /**
     * Fetches courses for the selected semester and displays them as cards.
     */
    private void updateCourseDetails(String semester) {
        // 1. Swap view from "Select..." label to the (empty) scroll pane
        if (initialDetailLabel.isShowing()) {
            JPanel wrapper = (JPanel) initialDetailLabel.getParent();
            wrapper.remove(initialDetailLabel);
            wrapper.add(detailScrollPane, BorderLayout.CENTER);
            wrapper.revalidate();
            wrapper.repaint();
        }

        // 2. Clear old course cards
        courseDetailPanel.removeAll();

        // 3. Add a title for the selected semester
        JLabel courseTitle = new JLabel("Courses for " + semester);
        courseTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        courseTitle.setForeground(textColor);
        courseTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        courseTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        courseDetailPanel.add(courseTitle);

        // 4. --- Get hardcoded course data ---
        List<Course> courses = courseService.getCoursesForSemester(semester, this.facultyID);

        // 5. Create and add cards for each course
        if (courses.isEmpty()) {
            JLabel noCourseLabel = new JLabel("No courses found for this semester.");
            noCourseLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noCourseLabel.setForeground(textColor.darker());
            noCourseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            courseDetailPanel.add(noCourseLabel);
        } else {
            for (Course course : courses) {
                JPanel courseCard = createCourseCard(course);
                courseCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                courseDetailPanel.add(courseCard);
                courseDetailPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
            }
        }

        // 6. Refresh the panel
        courseDetailPanel.revalidate();
        courseDetailPanel.repaint();
        // Scroll to top
        SwingUtilities.invokeLater(() -> detailScrollPane.getVerticalScrollBar().setValue(0));
    }

    /**
     * Helper method to create a single "Course Card" panel.
     */
    private JPanel createCourseCard(Course course) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(sideMenuColor);
        card.setBorder(courseCardBorder);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140)); // Constrain height

        // --- Top: Course Code and Name ---
        JLabel nameLabel = new JLabel(course.getCourseCode() + ": " + course.getCourseName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(buttonColor);
        card.add(nameLabel, BorderLayout.NORTH);

        // --- Center: Details ---
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false); // Transparent background
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(createDetailLabel("Program: " + course.getProgram()));
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(createDetailLabel("Enrolled Students: " + course.getStudentCount()));

        card.add(detailsPanel, BorderLayout.CENTER);

        // --- Right: "View Details" Button ---
        JButton viewButton = new JButton("View Details");
        viewButton.setBackground(buttonColor.darker());
        viewButton.setForeground(textColor);
        viewButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewButton.setFocusPainted(false);
        viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This is where you would open a new, more detailed frame
                JOptionPane.showMessageDialog(
                        MyCoursesFrame.this,
                        "Showing details for " + course.getCourseName(),
                        "Course Details",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // Add button to a small panel for padding
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 5));
        buttonPanel.add(viewButton, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    // Helper method for styling labels in the card
    private JLabel createDetailLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(textColor.lightGray);
        return lbl;
    }
}


// -------------------------------------------------------------------
// --- PLACEHOLDER CLASSES (Replace with your own) ---
// -------------------------------------------------------------------

/**
 * A placeholder "Service" class.
 * Replace this with your actual database/middleware calls.
 */
class CourseService {

    // Hardcoded map of courses by semester
    private Map<String, List<Course>> semesterCourses;

    public CourseService() {
        semesterCourses = new HashMap<>();

        // --- Hardcoded Data ---
        List<Course> fall2025 = new ArrayList<>();
        fall2025.add(new Course("CS340", "Operating Systems", "Fall 2025", "B.Tech (CSE)", 75, "prof_dummy"));
        fall2025.add(new Course("MATH210", "Discrete Mathematics", "Fall 2025", "B.Tech (All)", 150, "prof_dummy"));

        List<Course> spring2025 = new ArrayList<>();
        spring2025.add(new Course("CS101", "Intro to Programming", "Spring 2025", "B.Tech (CSE)", 120, "prof_dummy"));

        List<Course> fall2024 = new ArrayList<>();
        fall2024.add(new Course("CS450", "Database Systems", "Fall 2024", "B.Tech (CSE)", 60, "prof_dummy"));
        fall2024.add(new Course("EE200", "Digital Logic Design", "Fall 2024", "B.Tech (ECE)", 80, "prof_dummy"));
// ...
        semesterCourses.put("Fall 2025", fall2025);
        semesterCourses.put("Spring 2025", spring2025);
        semesterCourses.put("Fall 2024", fall2024);
    }

    // Placeholder method
    public List<String> getSemesters(String facultyID) {
        // In a real app, you'd query:
        // "SELECT DISTINCT semester FROM courses WHERE faculty_id = ? ORDER BY year DESC, semester DESC"
        return new ArrayList<>(semesterCourses.keySet());
    }

    // Placeholder method
    public List<Course> getCoursesForSemester(String semester, String facultyID) {
        // In a real app, you'd query:
        // "SELECT * FROM courses WHERE semester = ? AND faculty_id = ?"
        return semesterCourses.getOrDefault(semester, new ArrayList<>());
    }
}

/**
 * A placeholder "Model" class for a Course.
 * This is defined separately from the one in TAStats for this example.
 */
