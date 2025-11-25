package ui.StudentFrame;

import dbClasses.StudentRegisteredCourse;
import middleware.studentService;
import ui.components.*;
import ui.service.PdfExportService;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.io.File;

public class StudentCoursesPanel extends JPanel {

    // --- Fields ---
    private studentService enrollmentService;
    private String username;

    // --- UI Color Palette ---
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

    // --- Components ---
    private HeaderButton headerButton;
    private PdfExportService pdfExportService;
    private Map<Integer, List<StudentRegisteredCourse>> semesterData;

    // --- Layout Components (Promoted to Fields for Refresh) ---
    private RoundedPanel tabBarContainer;
    private JPanel semesterCardPanel;
    private CardLayout semesterCardLayout;

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

        // --- Initialize Services & Helpers ---
        this.headerButton = new HeaderButton();
        this.pdfExportService = new PdfExportService();

        // Initial Fetch
        // this.semesterData = enrollmentService.getSemesterData(username); // Moved to refreshPanel

        // --- Configure this JPanel ---
        setLayout(new BorderLayout(0, 15));
        setBackground(mainPanelColor);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // --- 1. Top Header Container ---
        JPanel topHeaderContainer = new JPanel(new BorderLayout());
        topHeaderContainer.setOpaque(false);

        // 1a. Title and Subtitle
        JPanel coursesTitlePanel = new JPanel();
        coursesTitlePanel.setLayout(new BoxLayout(coursesTitlePanel, BoxLayout.Y_AXIS));
        coursesTitlePanel.setOpaque(false);

        JLabel pageTitle = new JLabel("My Registered Courses");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        pageTitle.setForeground(textColor);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSubtitle = new JLabel("View all your courses organized by semester");
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pageSubtitle.setForeground(textSecondaryColor);
        pageSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        coursesTitlePanel.add(pageTitle);
        coursesTitlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        coursesTitlePanel.add(pageSubtitle);

        topHeaderContainer.add(coursesTitlePanel, BorderLayout.CENTER);

        // 1b. Export Button
        RoundedButton exportPdf = headerButton.createHeaderButton("Export to PDF");
        exportPdf.addActionListener(e -> handleExportPdf());

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(exportPdf);

        topHeaderContainer.add(buttonWrapper, BorderLayout.EAST);

        add(topHeaderContainer, BorderLayout.NORTH);

        // --- 2. Main Content Area ---
        JPanel mainCoursesContentPanel = new JPanel(new BorderLayout(0, 15));
        mainCoursesContentPanel.setOpaque(false);
        add(mainCoursesContentPanel, BorderLayout.CENTER);

        // --- 3. Tab Bar Container ---
        this.tabBarContainer = new RoundedPanel(8, cardColor, cardColor, 0);
        this.tabBarContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        this.tabBarContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainCoursesContentPanel.add(tabBarContainer, BorderLayout.NORTH);

        // --- 4. Semester Card Panel ---
        this.semesterCardLayout = new CardLayout();
        this.semesterCardPanel = new JPanel(semesterCardLayout);
        this.semesterCardPanel.setOpaque(false);
        mainCoursesContentPanel.add(semesterCardPanel, BorderLayout.CENTER);

        // Load Data and Build UI
        refreshPanel();
    }

    /**
     * Fetches fresh data from the database and rebuilds the tabs and tables.
     */
    private void refreshPanel() {
        // 1. Clear existing UI components
        tabBarContainer.removeAll();
        semesterCardPanel.removeAll();

        // 2. Fetch fresh data
        this.semesterData = enrollmentService.getSemesterData(username);

        List<TabButton> semesterTabButtons = new ArrayList<>();
        String firstAvailableSem = "";

        // --- Dynamic Tab Generation ---
        List<Integer> availableSemesters = new ArrayList<>(semesterData.keySet());
        Collections.sort(availableSemesters);

        if (availableSemesters.isEmpty()) {
            JLabel noDataLabel = new JLabel("No registered courses found.", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            noDataLabel.setForeground(textSecondaryColor);
            semesterCardPanel.add(noDataLabel, "EMPTY");
            semesterCardLayout.show(semesterCardPanel, "EMPTY");
        } else {
            for (Integer i : availableSemesters) {
                String tabName;
                if (i == 0) {
                    tabName = "Current / General";
                } else {
                    tabName = "Sem " + i;
                }

                if (firstAvailableSem.isEmpty()) {
                    firstAvailableSem = tabName;
                }

                // Create Tab Button
                TabButton tabButton = new TabButton(tabName);
                semesterTabButtons.add(tabButton);
                tabBarContainer.add(tabButton);

                // Prepare Data
                List<StudentRegisteredCourse> coursesForThisSem = semesterData.get(i);
                String[] columnNames = {"Course Code", "Course Name", "Credits", "Offered By", "Grade Point", "Action"};
                Object[][] data = new Object[coursesForThisSem.size()][6];

                for (int j = 0; j < coursesForThisSem.size(); j++) {
                    StudentRegisteredCourse course = coursesForThisSem.get(j);
                    data[j][0] = course.getCourseCode();
                    data[j][1] = course.getCourseName();
                    data[j][2] = course.getCourseCredits();
                    data[j][3] = course.getOfferedBy();

                    // Determine Grade Display
                    String gradeDisplay;
                    if (course.getGradeLetter() != null && course.getGradeLetter().equals("X")) {
                        gradeDisplay = "Withdrawn (X)";
                    } else if (course.getGradePoint() == 0.0) {
                        gradeDisplay = "In Progress";
                    } else {
                        gradeDisplay = String.valueOf(course.getGradePoint());
                    }
                    data[j][4] = gradeDisplay;
                    data[j][5] = "Drop";
                }

                // Create Table
                JTable semTable = createStyledTable(data, columnNames);

                // Configure Button Column
                semTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
                // Pass the UPDATED map to the editor
                semTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), semesterData, i));

                // Width adjustments
                semTable.getColumnModel().getColumn(0).setPreferredWidth(80);
                semTable.getColumnModel().getColumn(1).setPreferredWidth(250);
                semTable.getColumnModel().getColumn(5).setPreferredWidth(80);

                JScrollPane scrollPane = createStyledTableScrollPane(semTable);

                RoundedPanel tableCard = new RoundedPanel(15, cardColor, cardColor, 0);
                tableCard.setLayout(new BorderLayout());
                tableCard.add(scrollPane, BorderLayout.CENTER);

                semesterCardPanel.add(tableCard, tabName);

                // Tab Action
                tabButton.addActionListener(e -> {
                    semesterCardLayout.show(semesterCardPanel, tabName);
                    setActiveSemesterTab(tabButton, semesterTabButtons);
                });
            }

            // Set default active tab
            if (!semesterTabButtons.isEmpty()) {
                setActiveSemesterTab(semesterTabButtons.get(0), semesterTabButtons);
                semesterCardLayout.show(semesterCardPanel, firstAvailableSem);
            }
        }

        // Refresh visuals
        tabBarContainer.revalidate();
        tabBarContainer.repaint();
        semesterCardPanel.revalidate();
        semesterCardPanel.repaint();
    }

    // --- Helper Methods ---

    private void handleExportPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report as PDF");
        fileChooser.setSelectedFile(new File(username + "_Course_Report.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            boolean success = pdfExportService.exportStudentReport(this.semesterData, this.username, fileToSave);

            if (success) {
                JOptionPane.showMessageDialog(this, "Report exported successfully!", "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to export report.", "Export Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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
                // Only editable if Action column AND Grade is 'In Progress'
                if (column == 5) {
                    String gradeStatus = (String) getValueAt(row, 4);
                    return "In Progress".equals(gradeStatus);
                }
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setBackground(cardColor);
        table.setForeground(textColor);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(45);
        table.setGridColor(borderColor);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(cardColor);
        table.setSelectionForeground(textColor);

        table.getTableHeader().setDefaultRenderer(new LeftAlignedHeaderRenderer());
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));

        LeftAlignedCellRenderer cellRenderer = new LeftAlignedCellRenderer();
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        return table;
    }

    private JScrollPane createStyledTableScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardColor);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBackground(cardColor);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI());

        return scrollPane;
    }

    // --- Inner Classes ---

    class ButtonRenderer extends RoundedButton implements TableCellRenderer {
        public ButtonRenderer() {
            super("Drop", new Color(220, 80, 80), new Color(240, 100, 100), 8);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String gradeStatus = (String) table.getValueAt(row, 4);

            if ("In Progress".equals(gradeStatus)) {
                return this;
            } else {
                JLabel lbl = new JLabel("-");
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lbl.setForeground(textSecondaryColor);
                lbl.setBackground(cardColor);
                lbl.setOpaque(true);
                return lbl;
            }
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private RoundedButton button;
        private int currentSemIndex;
        private Map<Integer, List<StudentRegisteredCourse>> dataMap;
        private int currentRow;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox, Map<Integer, List<StudentRegisteredCourse>> map, int semIndex) {
            super(checkBox);
            this.dataMap = map;
            this.currentSemIndex = semIndex;

            button = new RoundedButton("Drop", new Color(220, 80, 80), new Color(240, 100, 100), 8);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentRow = row;
            this.isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                if (dataMap != null && dataMap.containsKey(currentSemIndex)) {
                    List<StudentRegisteredCourse> courses = dataMap.get(currentSemIndex);
                    if (currentRow >= 0 && currentRow < courses.size()) {
                        StudentRegisteredCourse course = courses.get(currentRow);

                        int confirm = JOptionPane.showConfirmDialog(button,
                                "Are you sure you want to drop " + course.getCourseCode() + "?\n(Action depends on current deadline dates)",
                                "Confirm Drop", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            String result = null;
                            try {
                                result = enrollmentService.dropCourse(username, course.getSectionId(),course.getCourseCode());
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            JOptionPane.showMessageDialog(button, result);

                            SwingUtilities.invokeLater(() -> refreshPanel());
                        }
                    }
                }
            }
            isPushed = false;
            return "Drop";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    // --- Styling Inner Classes ---

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
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    if (!isActive) setForeground(textColor);
                    repaint();
                }
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

    private class LeftAlignedHeaderRenderer extends DefaultTableCellRenderer {
        public LeftAlignedHeaderRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setBackground(cardColor);
            setForeground(textSecondaryColor);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(cardColor);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, borderColor),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            return this;
        }
    }

    private class LeftAlignedCellRenderer extends DefaultTableCellRenderer {
        public LeftAlignedCellRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setBackground(cardColor);
            setForeground(textColor);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        }
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

    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() { this.thumbColor = buttonColor; this.trackColor = cardColor; }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            g.setColor(thumbColor); ((Graphics2D)g).fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, 10, 10));
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor); ((Graphics2D)g).fill(r);
        }
    }
}