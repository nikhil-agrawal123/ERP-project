package ui.StudentFrame;

import dbClasses.StudentRegisteredCourse;
import middleware.studentService;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentCoursesPanel extends JPanel {

    // --- Fields ---
    private studentService enrollmentService;
    private String username;

    // --- UI Color Palette (Passed from StudentDashboard) ---
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

    public StudentCoursesPanel(studentService enrollmentService, String username,
                          Color bgColor, Color sideMenuColor, Color mainPanelColor, Color cardColor,
                          Color popoverColor, Color borderColor, Color buttonColor,
                          Color buttonColorGlow, Color textColor, Color textSecondaryColor) {

        // Assign fields
        this.enrollmentService = enrollmentService;
        this.username = username;
        this.bgColor = bgColor;
        this.sideMenuColor = sideMenuColor;
        this.mainPanelColor = mainPanelColor;
        this.cardColor = cardColor;
        this.popoverColor = popoverColor;
        this.borderColor = borderColor;
        this.buttonColor = buttonColor;
        this.buttonColorGlow = buttonColorGlow;
        this.textColor = textColor;
        this.textSecondaryColor = textSecondaryColor;

        // --- Configure this JPanel ---
        setLayout(new BorderLayout(0, 15)); // 15px v-gap
        setBackground(mainPanelColor);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40)); // Added more horizontal padding

        // 1. Title and Subtitle Panel
        JPanel coursesTitlePanel = new JPanel();
        coursesTitlePanel.setLayout(new BoxLayout(coursesTitlePanel, BoxLayout.Y_AXIS));
        coursesTitlePanel.setOpaque(false);

        JLabel pageTitle = new JLabel("My Registered Courses");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 32)); // Increased font size
        pageTitle.setForeground(textColor);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSubtitle = new JLabel("View all your courses organized by semester");
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pageSubtitle.setForeground(textSecondaryColor);
        pageSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        coursesTitlePanel.add(pageTitle);
        coursesTitlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        coursesTitlePanel.add(pageSubtitle);

        add(coursesTitlePanel, BorderLayout.NORTH);

        // 2. Main Content Area (Tabs + Table Cards)
        JPanel mainCoursesContentPanel = new JPanel(new BorderLayout(0, 15)); // 15px gap between tabs and table
        mainCoursesContentPanel.setOpaque(false);
        add(mainCoursesContentPanel, BorderLayout.CENTER);

        // 3. Tab Bar Container
        RoundedPanel tabBarContainer = new RoundedPanel(8, cardColor, cardColor, 0);
        tabBarContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        tabBarContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding inside the tab bar
        mainCoursesContentPanel.add(tabBarContainer, BorderLayout.NORTH);

        // 4. Semester Card Panel (for tables)
        CardLayout semesterCardLayout = new CardLayout();
        JPanel semesterCardPanel = new JPanel(semesterCardLayout);
        semesterCardPanel.setOpaque(false);
        mainCoursesContentPanel.add(semesterCardPanel, BorderLayout.CENTER);

        List<TabButton> semesterTabButtons = new ArrayList<>();
        Map<Integer, List<StudentRegisteredCourse>> semesterData = enrollmentService.getSemesterData(username);
        String[] columnNames = {"Course Code", "Course Name", "Credits", "Offered By", "Grade Point"};
        String firstAvailableSem = "";

        for (int i = 1; i < 9; i++) {
            if (semesterData.containsKey(i)) {
                String tabName = "Sem " + i;
                if (firstAvailableSem.isEmpty()) {
                    firstAvailableSem = tabName;
                }

                // --- Create the NEW Tab Button ---
                TabButton tabButton = new TabButton(tabName);
                semesterTabButtons.add(tabButton);
                tabBarContainer.add(tabButton);

                // --- Create the Table Content Card ---
                List<StudentRegisteredCourse> coursesForThisSem = semesterData.get(i);
                Object[][] data = new Object[coursesForThisSem.size()][5];

                for (int j = 0; j < coursesForThisSem.size(); j++) {
                    StudentRegisteredCourse course = coursesForThisSem.get(j);
                    data[j][0] = course.getCourseCode();
                    data[j][1] = course.getCourseName();
                    data[j][2] = course.getCourseCredits();
                    data[j][3] = course.getOfferedBy();
                    data[j][4] = (course.getGradePoint() == 0.0) ? "In Progress" : course.getGradePoint();
                }

                JTable semTable = createStyledTable(data, columnNames);
                JScrollPane scrollPane = createStyledTableScrollPane(semTable);

                // --- Wrap ScrollPane in a RoundedPanel ---
                RoundedPanel tableCard = new RoundedPanel(15, cardColor, cardColor, 0); // No border
                tableCard.setLayout(new BorderLayout());
                tableCard.add(scrollPane, BorderLayout.CENTER);

                semesterCardPanel.add(tableCard, tabName);

                // --- Add ActionListener ---
                tabButton.addActionListener(e -> {
                    semesterCardLayout.show(semesterCardPanel, tabName);
                    setActiveSemesterTab(tabButton, semesterTabButtons);
                });
            }
        }

        // 5. Set the default active tab
        if (!semesterTabButtons.isEmpty()) {
            setActiveSemesterTab(semesterTabButtons.get(0), semesterTabButtons);
            semesterCardLayout.show(semesterCardPanel, firstAvailableSem);
        }
    }

    // --- Helper Methods (Moved from StudentDashboard) ---

    /**
     * Sets the active state for the custom semester tabs.
     */
    private void setActiveSemesterTab(TabButton activeButton, List<TabButton> allTabs) {
        for (TabButton button : allTabs) {
            button.setActive(false);
        }
        activeButton.setActive(true);
    }

    private JTable createStyledTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setBackground(cardColor);
        table.setForeground(textColor);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40); // Increased row height for more spacing
        table.setGridColor(borderColor);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(buttonColor.darker());
        table.setSelectionForeground(textColor);

        // --- NEW --- Use custom header renderer for left-alignment and padding
        table.getTableHeader().setDefaultRenderer(new LeftAlignedHeaderRenderer());
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 50)); // Increased header height

        // --- NEW --- Use custom cell renderer for left-alignment and padding
        LeftAlignedCellRenderer cellRenderer = new LeftAlignedCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        return table;
    }

    private JScrollPane createStyledTableScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default border
        scrollPane.getViewport().setBackground(cardColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Make scroll pane seamless with card
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBackground(cardColor);

        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI());

        return scrollPane;
    }

    // --- Inner Classes (Moved from StudentDashboard or Duplicated) ---

    /**
     * A custom button for the semester tabs.
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
            // Add padding to the button
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            // Set initial text color
            setForeground(textSecondaryColor);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    if (!isActive) {
                        setForeground(textColor); // Brighten text on hover
                    }
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    if (!isActive) {
                        setForeground(textSecondaryColor); // Dim text on exit
                    }
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
                g2.setColor(buttonColorGlow); // Darker active background
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            } else if (isHovered) {
                g2.setColor(borderColor); // Subtle hover
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            } else {
                g2.setColor(cardColor); // Transparent (same as parent)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            }

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    /**
     * Custom renderer for table headers to ensure left-alignment and padding.
     */
    private class LeftAlignedHeaderRenderer extends DefaultTableCellRenderer {
        public LeftAlignedHeaderRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setBackground(cardColor);
            setForeground(textSecondaryColor); // Muted gray text
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Padding
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            // Ensure background and border are set correctly
            setBackground(cardColor);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, borderColor), // Bottom line
                    BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding
            ));
            return this;
        }
    }

    /**
     * Custom renderer for table cells to ensure left-alignment and padding.
     */
    private class LeftAlignedCellRenderer extends DefaultTableCellRenderer {
        public LeftAlignedCellRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setBackground(cardColor);
            setForeground(textColor); // White text
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Padding
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(buttonColor.darker());
                setForeground(textColor);
            } else {
                setBackground(cardColor);
                setForeground(textColor);
            }
            setText(value != null ? value.toString() : "");
            return this;
        }
    }

    /**
     * A JPanel with rounded corners and a border. (Duplicated from StudentDashboard)
     */
    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;
        private Color borderColor;
        private int borderThickness;
        private boolean useGradient = false;
        private Color gradientStartColor;
        private Color gradientEndColor;

        /**
         * Constructor for SOLID color panels (with border)
         */
        public RoundedPanel(int radius, Color bgColor, Color borderColor, int borderThickness) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            this.borderColor = borderColor;
            this.borderThickness = borderThickness;
            this.useGradient = false;
            setOpaque(false);
        }

        /**
         * Constructor for GRADIENT color panels (no border)
         */
        public RoundedPanel(int radius, Color gradStart, Color gradEnd) {
            super();
            this.cornerRadius = radius;
            this.gradientStartColor = gradStart;
            this.gradientEndColor = gradEnd;
            this.useGradient = true;
            this.borderThickness = 0;
            this.borderColor = gradStart;
            this.backgroundColor = gradStart;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (useGradient) {
                GradientPaint gp = new GradientPaint(
                        0, getHeight(), gradientStartColor,
                        getWidth(), 0, gradientEndColor
                );
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            } else {
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
            }
            g2.dispose();
        }
    }

    /**
     * Inner class for a custom styled scrollbar. (Duplicated from StudentDashboard)
     */
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = buttonColor;
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
    }
}