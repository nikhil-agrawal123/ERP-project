package ui.FacultyFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// --- Your Custom Components ---
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

// --- Middleware & Data ---
import middleware.gradingService;
import middleware.facultyService;
import ui.FacultyFrame.GradingComponent;
import dbClasses.EnrolledStudent;

public class UpdateScoresFrame extends JFrame {

    // --- UI Color Palette ---
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);

    private DefaultTableModel tableModel;
    private JTable scoresTable;
    private String courseCode;
    private String instructorId;
    private String semester;

    private gradingService gradingService;
    private facultyService facultyService;
    private List<GradingComponent> policyComponents;

    /**
     * @param courseCode   e.g. "CS101"
     * @param instructorId e.g. "inst1" (needed to find the correct policy)
     * @param semester     e.g. "Monsoon 2025"
     */
    public UpdateScoresFrame(String courseCode, String instructorId, String semester) {
        super("Update Scores for " + courseCode);
        this.courseCode = courseCode;
        this.instructorId = instructorId;
        this.semester = semester;

        this.gradingService = new gradingService();
        this.facultyService = new facultyService();

        // --- 1. Fetch Policy ---
        this.policyComponents = gradingService.getPolicy(courseCode, instructorId, semester);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        try {
            ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
            setIconImage(image.getImage());
        } catch (Exception ignored) {}

        // --- Main Panel ---
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(mainPanelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setOpaque(false);

        RoundedButton backButton = new RoundedButton("← Back", buttonColor, buttonColorGlow, 10);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backButton.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        backButton.addActionListener(e -> dispose());

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel pageTitle = new JLabel("Update Scores");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        pageTitle.setForeground(textColor);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSubtitle = new JLabel("Modify scores for " + courseCode + " (" + semester + ")");
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pageSubtitle.setForeground(textSecondaryColor);
        pageSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(pageTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(pageSubtitle);

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- 2. CHECK POLICY EXISTENCE ---
        if (policyComponents == null || policyComponents.isEmpty()) {
            // --- Show "No Policy" State ---
            RoundedPanel emptyStatePanel = new RoundedPanel(15, cardColor, cardColor, 0);
            emptyStatePanel.setLayout(new GridBagLayout());

            JLabel warningLabel = new JLabel("<html><center>No grading policy has been set for this course.<br>Please set a policy in the 'Grading Policy' section first.</center></html>");
            warningLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            warningLabel.setForeground(new Color(220, 80, 80)); // Danger color

            emptyStatePanel.add(warningLabel);
            mainPanel.add(emptyStatePanel, BorderLayout.CENTER);
        } else {
            // --- Show Dynamic Table ---
            JPanel tablePanel = createDynamicTablePanel();
            mainPanel.add(tablePanel, BorderLayout.CENTER);

            // --- Bottom Buttons ---
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            buttonPanel.setOpaque(false);

            RoundedButton exportButton = new RoundedButton("Export as CSV", borderColor, borderColor.brighter(), 10);
            exportButton.setForeground(Color.WHITE);
            exportButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            exportButton.setPreferredSize(new Dimension(180, 45));
            exportButton.addActionListener(e -> exportTableToCSV());

            RoundedButton saveButton = new RoundedButton("Save Changes", buttonColor, buttonColorGlow, 10);
            saveButton.setForeground(Color.WHITE);
            saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            saveButton.setPreferredSize(new Dimension(180, 45));
            saveButton.addActionListener(e -> saveScores());

            buttonPanel.add(exportButton);
            buttonPanel.add(saveButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        add(mainPanel);
    }

    private JPanel createDynamicTablePanel() {
        // 1. Define Columns dynamically
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Roll Number");
        columnNames.add("Student Name");

        // Add columns from Policy
        for (GradingComponent comp : policyComponents) {
            // Header format: "Quiz (10%)"
            columnNames.add(comp.getName() + " (" + comp.getPercentage() + "%)");
        }
        columnNames.add("Total (100%)");

        // 2. Fetch Students
        List<EnrolledStudent> students = facultyService.getClassList(courseCode, semester);

        // 3. Create Data Vector
        Vector<Vector<Object>> data = new Vector<>();
        for (EnrolledStudent student : students) {
            Vector<Object> row = new Vector<>();
            row.add(student.getRollNumber());
            row.add(student.getStudentName());

            // Initialize scores to 0 (or fetch from DB if we had a method for that)
            for (int i = 0; i < policyComponents.size(); i++) {
                row.add(0.0); // Default score
            }
            row.add(0.0); // Default Total
            data.add(row);
        }

        // 4. Create Model
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only score columns are editable (indices 2 to size+1)
                return column >= 2 && column < (2 + policyComponents.size());
            }

            @Override
            public void setValueAt(Object aValue, int row, int col) {
                double doubleVal = 0.0;
                try {
                    doubleVal = Double.parseDouble(aValue.toString());
                    if (doubleVal < 0) doubleVal = 0.0;

                    // Validate against max percentage?
                    // Optional: get max for this column
                    int policyIndex = col - 2;
                    if (policyIndex >= 0 && policyIndex < policyComponents.size()) {
                        double max = policyComponents.get(policyIndex).getPercentage();
                        if (doubleVal > max) doubleVal = max;
                    }

                } catch (NumberFormatException e) {
                    // ignore invalid input
                }

                super.setValueAt(doubleVal, row, col);

                // Auto-calculate Total
                calculateTotal(row);
            }
        };

        scoresTable = new JTable(tableModel);
        styleTable(scoresTable);

        JScrollPane scrollPane = new JScrollPane(scoresTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardColor);
        scrollPane.setBackground(cardColor);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

        RoundedPanel panel = new RoundedPanel(15, cardColor, cardColor, 0);
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void calculateTotal(int row) {
        double total = 0;
        int numComponents = policyComponents.size();

        // Score columns start at index 2
        for (int i = 0; i < numComponents; i++) {
            Object val = tableModel.getValueAt(row, 2 + i);
            if (val instanceof Number) {
                total += ((Number) val).doubleValue();
            } else {
                try {
                    total += Double.parseDouble(val.toString());
                } catch (Exception ignored) {}
            }
        }
        // Update Total column (last column)
        tableModel.setValueAt(total, row, tableModel.getColumnCount() - 1);
    }

    private void saveScores() {
        if (scoresTable.isEditing()) {
            scoresTable.getCellEditor().stopCellEditing();
        }

        // Iterate through table and save...
        // This part requires a 'saveScores' service method that does a batch update
        // to the 'grades' table.

        JOptionPane.showMessageDialog(this,
                "Scores saved locally (Database implementation pending).",
                "Save Successful",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void styleTable(JTable table) {
        table.setBackground(cardColor);
        table.setForeground(textColor);
        table.setGridColor(borderColor);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(buttonColorGlow);
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JTableHeader header = table.getTableHeader();
        header.setBackground(cardColor);
        header.setForeground(textSecondaryColor);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(100, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, borderColor));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(cardColor);
        centerRenderer.setForeground(textColor);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void exportTableToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");
        fileChooser.setSelectedFile(new File(this.courseCode + "_Scores.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (FileWriter fw = new FileWriter(fileToSave);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    bw.write("\"" + tableModel.getColumnName(i) + "\"");
                    if (i < tableModel.getColumnCount() - 1) bw.write(",");
                }
                bw.newLine();

                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        bw.write("\"" + String.valueOf(value) + "\"");
                        if (col < tableModel.getColumnCount() - 1) bw.write(",");
                    }
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(this, "Data exported successfully!", "Export Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = borderColor;
            this.trackColor = cardColor;
        }
        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fill(new RoundRectangle2D.Float(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10));
            g2.dispose();
        }
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            g2.fill(trackBounds);
            g2.dispose();
        }
    }
}