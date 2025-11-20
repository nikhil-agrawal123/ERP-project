package ui.FacultyFrame;

import dbClasses.EnrolledStudent;
import middleware.facultyService;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ShowStudentsFrame extends JFrame {

    // --- UI Color Palette (Matched to FacultyCoursesPanel) ---
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);

    private facultyService facultyService;
    private List<EnrolledStudent> studentList;

    public ShowStudentsFrame(String courseCode, String courseName, String semester) {
        super("Enrolled Students - " + courseCode);

        // --- Data Initialization ---
        this.facultyService = new facultyService();
        this.studentList = facultyService.getClassList(courseCode, semester);

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(mainPanelColor);
        setLayout(new BorderLayout());

        // --- 1. Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(mainPanelColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Back Button
        RoundedButton backButton = new RoundedButton(
                "← Back",
                buttonColor,      // gradStart
                buttonColorGlow,  // gradEnd
                10                // arc
        );
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.addActionListener(e -> dispose());

        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backButtonPanel.setOpaque(false);
        backButtonPanel.add(backButton);

        // Title Block
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel titleLabel = new JLabel("Enrolled Students");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(courseCode + ": " + courseName + " (" + semester + ")");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(textSecondaryColor);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBlock.add(titleLabel);
        titleBlock.add(Box.createRigidArea(new Dimension(0, 5)));
        titleBlock.add(subtitleLabel);

        headerPanel.add(backButtonPanel, BorderLayout.WEST);
        headerPanel.add(titleBlock, BorderLayout.CENTER);

        // Add some spacing on the right to balance the title if needed
        headerPanel.add(Box.createRigidArea(new Dimension(100, 0)), BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Main Content Panel ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        // The Card Container (RoundedPanel)
        RoundedPanel tableCard = new RoundedPanel(15, cardColor, cardColor, 0);
        tableCard.setLayout(new BorderLayout());
        // Add padding inside the card
        tableCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (studentList.isEmpty()) {
            JLabel noDataLabel = new JLabel("No students found for this course.", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            noDataLabel.setForeground(textSecondaryColor);
            tableCard.add(noDataLabel, BorderLayout.CENTER);
        } else {
            // Create Table
            JTable table = createStyledTable();
            JScrollPane scrollPane = createStyledScrollPane(table);
            tableCard.add(scrollPane, BorderLayout.CENTER);

            // Footer count
            JLabel countLabel = new JLabel("Total Students: " + studentList.size());
            countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            countLabel.setForeground(buttonColor);
            countLabel.setBorder(BorderFactory.createEmptyBorder(15, 5, 0, 0));
            tableCard.add(countLabel, BorderLayout.SOUTH);
        }

        contentPanel.add(tableCard, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    // --- Helper Methods ---

    private JTable createStyledTable() {
        String[] columnNames = {"Roll Number", "Student Name", "Email Address"};

        // Populate Data
        Object[][] data = new Object[studentList.size()][3];
        for (int i = 0; i < studentList.size(); i++) {
            EnrolledStudent s = studentList.get(i);
            data[i][0] = s.getRollNumber();
            data[i][1] = s.getStudentName();
            data[i][2] = s.getEmail();
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setBackground(cardColor);
        table.setForeground(textColor);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(45);
        table.setGridColor(borderColor);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(buttonColor.darker());
        table.setSelectionForeground(textColor);

        // Header Style
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(cardColor);
                setForeground(textSecondaryColor);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, borderColor),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                return this;
            }
        });

        // Cell Style
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    setBackground(buttonColor.darker());
                } else {
                    setBackground(cardColor);
                }
                setForeground(textColor);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        return table;
    }

    private JScrollPane createStyledScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardColor);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI());

        return scrollPane;
    }

    // --- Inner Class: StyledScrollBarUI (Matched to FacultyCoursesPanel) ---
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = borderColor.brighter(); // Using border color for thumb
            this.trackColor = cardColor;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }

        private JButton createZeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0,0));
            b.setMinimumSize(new Dimension(0,0));
            b.setMaximumSize(new Dimension(0,0));
            return b;
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