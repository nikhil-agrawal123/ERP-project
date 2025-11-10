package ui.FacultyFrame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;
import java.util.ArrayList; // Used for placeholder data

/**
 * A new frame to display TA information, organized by course.
 * Uses a JSplitPane for a master-detail view.
 */
public class TAStats extends JFrame {

    // --- Style Colors (copied from your Dashboard) ---
    private Color bgColor = new Color(45, 45, 45);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;
    private Border taCardBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(buttonColor, 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
    );

    // --- Components ---
    private JList<Course> courseList;
    private DefaultListModel<Course> courseListModel;
    private JPanel taDetailPanel; // This panel will hold the TA cards
    private JScrollPane detailScrollPane;
    private JLabel initialDetailLabel;

    // --- Services ---
    private TAService taService; // A placeholder service for fetching data

    private String facultyID;

    public TAStats(String facultyID, String facultyName) {
        super("TA Management for " + facultyName);
        this.facultyID = facultyID;
        this.taService = new TAService(); // Initialize your service

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose, don't exit
        setSize(1000, 700);
        setLocationRelativeTo(null); // Center on screen
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());
        setLayout(new BorderLayout());

        // --- Create Master-Detail Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300); // Width of the course list
        splitPane.setLeftComponent(createCourseListPanel());
        splitPane.setRightComponent(createDetailPanel());
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splitPane.setOpaque(false);
        splitPane.setDividerSize(8);

        add(splitPane, BorderLayout.CENTER);

        // --- Load Initial Data ---
        loadFacultyCourses();
    }

    /**
     * Creates the left-side panel containing the list of courses.
     */
    private Component createCourseListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(sideMenuColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("My Courses", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(textColor);
        panel.add(title, BorderLayout.NORTH);

        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setBackground(sideMenuColor);
        courseList.setForeground(textColor);
        courseList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        courseList.setSelectionBackground(buttonColor);
        courseList.setSelectionForeground(Color.BLACK);

        // Use a custom renderer to show Course names nicely
        courseList.setCellRenderer(new CourseListRenderer());

        // --- Add the List Selection Listener ---
        // This is where the magic happens.
        courseList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    Course selectedCourse = courseList.getSelectedValue();
                    if (selectedCourse != null) {
                        updateTADetails(selectedCourse);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setBorder(BorderFactory.createLineBorder(bgColor));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the right-side panel that will display TA details.
     */
    private Component createDetailPanel() {
        // This outer panel uses BorderLayout to center the initial message
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(mainPanelColor);

        // taDetailPanel is the one we will add/remove cards from
        // We use BoxLayout to stack TA cards vertically
        taDetailPanel = new JPanel();
        taDetailPanel.setLayout(new BoxLayout(taDetailPanel, BoxLayout.Y_AXIS));
        taDetailPanel.setBackground(mainPanelColor);
        taDetailPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Initial message
        initialDetailLabel = new JLabel("Select a course to view TA information", SwingConstants.CENTER);
        initialDetailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        initialDetailLabel.setForeground(textColor.darker());
        wrapperPanel.add(initialDetailLabel, BorderLayout.CENTER);

        // We put the taDetailPanel inside a JScrollPane
        // This ensures we can scroll if there are many TAs
        detailScrollPane = new JScrollPane(taDetailPanel);
        detailScrollPane.setBorder(BorderFactory.createEmptyBorder());
        detailScrollPane.getViewport().setBackground(mainPanelColor);

        // We will swap the label for the scroll pane when a course is selected
        return wrapperPanel;
    }

    /**
     * Loads the faculty's courses into the JList.
     */
    private void loadFacultyCourses() {
        // --- This is where you call your real service ---
        List<Course> courses = taService.getCoursesForFaculty(this.facultyID);

        courseListModel.clear();
        for (Course course : courses) {
            courseListModel.addElement(course);
        }
    }

    /**
     * Fetches TAs for the selected course and displays them as cards.
     */
    private void updateTADetails(Course course) {
        // 1. Swap view from "Select..." label to the (empty) scroll pane
        if (initialDetailLabel.isShowing()) {
            JPanel wrapper = (JPanel) initialDetailLabel.getParent();
            wrapper.remove(initialDetailLabel);
            wrapper.add(detailScrollPane, BorderLayout.CENTER);

            // --- ADD THESE TWO LINES ---
            wrapper.revalidate();
            wrapper.repaint();
            // ---------------------------
        }

        // 2. Clear old TA cards
        taDetailPanel.removeAll();

        // 3. Add a title for the selected course
        JLabel courseTitle = new JLabel("TAs for " + course.getCourseName());
        courseTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        courseTitle.setForeground(textColor);
        courseTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        courseTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        taDetailPanel.add(courseTitle);

        // 4. --- This is where you call your real service ---
        List<TA> tas = taService.getTAsForCourse(course.getCourseId());

        // 5. Create and add cards for each TA
        if (tas.isEmpty()) {
            JLabel noTaLabel = new JLabel("No TAs are assigned to this course.");
            noTaLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            noTaLabel.setForeground(textColor.darker());
            noTaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            taDetailPanel.add(noTaLabel);
        } else {
            for (TA ta : tas) {
                JPanel taCard = createTACard(ta);
                taCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                taDetailPanel.add(taCard);
                taDetailPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacer
            }
        }

        // 6. Refresh the panel
        taDetailPanel.revalidate();
        taDetailPanel.repaint();
        // Scroll to top
        SwingUtilities.invokeLater(() -> detailScrollPane.getVerticalScrollBar().setValue(0));
    }

    /**
     * Helper method to create a single "TA Card" panel.
     * This is what creates the "nice looking UI".
     */
    private JPanel createTACard(TA ta) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(sideMenuColor);
        card.setBorder(taCardBorder);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); // Constrain height

        // --- Top: TA Name ---
        JLabel nameLabel = new JLabel(ta.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(buttonColor);
        card.add(nameLabel, BorderLayout.NORTH);

        // --- Center: Details Grid ---
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 15, 5));
        detailsPanel.setOpaque(false); // Transparent background

        detailsPanel.add(createDetailLabel("TA ID:"));
        detailsPanel.add(createValueLabel(ta.getTaId()));

        detailsPanel.add(createDetailLabel("Email:"));
        detailsPanel.add(createValueLabel(ta.getEmail()));

        detailsPanel.add(createDetailLabel("Office Hours:"));
        detailsPanel.add(createValueLabel(ta.getOfficeHours()));

        card.add(detailsPanel, BorderLayout.CENTER);

        // --- Bottom: Responsibilities ---
        JTextArea respArea = new JTextArea("Responsibilities: " + ta.getResponsibilities());
        respArea.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        respArea.setForeground(textColor.lightGray);
        respArea.setOpaque(false);
        respArea.setEditable(false);
        respArea.setLineWrap(true);
        respArea.setWrapStyleWord(true);
        card.add(respArea, BorderLayout.SOUTH);

        return card;
    }

    // Helper methods for styling labels in the card
    private JLabel createDetailLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(textColor);
        return lbl;
    }

    private JLabel createValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(textColor.lightGray);
        return lbl;
    }

    /**
     * Custom ListCellRenderer to display Course objects nicely in the JList.
     */
    class CourseListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            // Get the default renderer component
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // value is the Course object. We cast it and get its name.
            if (value instanceof Course) {
                Course course = (Course) value;
                label.setText(course.getCourseCode() + " - " + course.getCourseName());
//                label.setIcon(new ImageIcon(getClass().getResource("/course_icon.png")));
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }
            return label;
        }
    }
}


// -------------------------------------------------------------------
// --- PLACEHOLDER CLASSES (Replace with your own) ---
// -------------------------------------------------------------------

/**
 * A placeholder "Service" class.
 * Replace this with your actual database/middleware calls.
 */
class TAService {

    // Placeholder method
    public List<Course> getCoursesForFaculty(String facultyID) {
        // --- YOUR DATABASE LOGIC HERE ---
        // e.g., "SELECT * FROM courses WHERE faculty_id = ?"
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("CS101", "Intro to Programming", facultyID));
        courses.add(new Course("CS340", "Operating Systems", facultyID));
        courses.add(new Course("MATH210", "Discrete Mathematics", facultyID));
        return courses;
    }

    // Placeholder method
    public List<TA> getTAsForCourse(String courseId) {
        // --- YOUR DATABASE LOGIC HERE ---
        // e.g., "SELECT * FROM tas WHERE course_id = ?"
        List<TA> tas = new ArrayList<>();

        // Mock data based on course
        if ("CS101".equals(courseId)) {
            tas.add(new TA("Alice Smith", "ta_001", "alice@school.edu", "Mon 10-12am", "Grading Homeworks 1-5, Lab 1"));
            tas.add(new TA("Bob Johnson", "ta_002", "bob@school.edu", "Wed 2-4pm", "Grading Homeworks 6-10, Lab 2"));
        } else if ("CS340".equals(courseId)) {
            tas.add(new TA("Charlie Brown", "ta_003", "charlie@school.edu", "Fri 1-3pm", "Project grading, Office Hours"));
        }
        else if ("MATH210".equals(courseId)) {
            tas.add(new TA("David Lee", "ta_004", "david@school.edu", "Tue 1-2pm", "Proof review sessions"));
            tas.add(new TA("Eva Green", "ta_005", "eva@school.edu", "Thu 3-4pm", "Grading induction assignments"));
        }

        return tas;
    }
}

/**
 * A placeholder "Model" class for a Course.
 */

/**
 * A placeholder "Model" class for a TA.
 */
class TA {
    private String name;
    private String taId;
    private String email;
    private String officeHours;
    private String responsibilities;

    public TA(String name, String taId, String email, String officeHours, String responsibilities) {
        this.name = name;
        this.taId = taId;
        this.email = email;
        this.officeHours = officeHours;
        this.responsibilities = responsibilities;
    }

    public String getName() { return name; }
    public String getTaId() { return taId; }
    public String getEmail() { return email; }
    public String getOfficeHours() { return officeHours; }
    public String getResponsibilities() { return responsibilities; }
}