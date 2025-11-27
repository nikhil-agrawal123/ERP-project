package ui.FacultyFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;

// --- Your Custom Components ---
import dbClasses.GradingComponent;
import dbClasses.GradeRange;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

// --- Middleware & Data ---
import middleware.gradingService;
import middleware.facultyService;
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
    private List<GradeRange> gradeCutoffs;

    // --- FIX 1: Use Integer for Enrollment IDs to match Database/Service ---
    private Map<Integer, Integer> rowToEnrollmentIdMap;

    public UpdateScoresFrame(String courseCode, String instructorId, String semester) {
        super("Update Scores for " + courseCode);
        this.courseCode = courseCode;
        this.instructorId = instructorId;
        this.semester = semester;

        this.gradingService = new gradingService();
        this.facultyService = new facultyService();
        this.rowToEnrollmentIdMap = new HashMap<>();

        // --- 1. Fetch Policy & Cutoffs ---
        this.policyComponents = gradingService.getPolicy(courseCode, instructorId, semester);
        this.gradeCutoffs = gradingService.getGradeCutoffs(courseCode, instructorId, semester);

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
            warningLabel.setForeground(new Color(220, 80, 80));

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
        // 1. Define Columns
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Roll Number");
        columnNames.add("Student Name");

        for (GradingComponent comp : policyComponents) {
            columnNames.add(comp.getName() + " (" + comp.getPercentage() + "%)");
        }
        columnNames.add("Total (100%)");
        columnNames.add("CG");
        columnNames.add("Letter Grade");

        // 2. Fetch Students & EXISTING SCORES
        List<EnrolledStudent> students = facultyService.getClassList(courseCode, semester);

        // --- FIX 2: Expect Integer Keys from Service ---
        Map<Integer, Map<String, Double>> existingScores = facultyService.getExistingScores(students);

        // 3. Create Data Vector
        Vector<Vector<Object>> data = new Vector<>();
        int rowIndex = 0;

        for (EnrolledStudent student : students) {
            // --- FIX 3: Use Integer ID ---
            int enrollmentId = student.getEnrollmentId();
            rowToEnrollmentIdMap.put(rowIndex++, enrollmentId);

            Vector<Object> row = new Vector<>();
            row.add(student.getRollNumber());
            row.add(student.getStudentName());

            // --- FIX 4: Lookup using Integer Key ---
            Map<String, Double> myScores = existingScores.getOrDefault(enrollmentId, new HashMap<>());

            // Populate scores columns
            for (GradingComponent comp : policyComponents) {
                Double savedVal = myScores.getOrDefault(comp.getName(), 0.0);
                row.add(savedVal);
            }
            row.add(0.0); // Total (will recalculate)
            row.add("-"); // Letter
            row.add("0"); // CG
            data.add(row);
        }

        // 4. Create Model
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2 && column < (2 + policyComponents.size());
            }

            @Override
            public void setValueAt(Object aValue, int row, int col) {
                // Infinite Recursion Prevention
                int firstCalculatedColumnIndex = 2 + policyComponents.size();
                if (col >= firstCalculatedColumnIndex) {
                    super.setValueAt(aValue, row, col);
                    return;
                }

                double doubleVal = 0.0;
                try {
                    doubleVal = Double.parseDouble(aValue.toString());
                    if (doubleVal < 0) doubleVal = 0.0;

                    int policyIndex = col - 2;
                    if (policyIndex >= 0 && policyIndex < policyComponents.size()) {
                        double max = policyComponents.get(policyIndex).getPercentage();
                        if (doubleVal > max) doubleVal = max;
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }

                super.setValueAt(doubleVal, row, col);
                calculateRowMetrics(row);
            }
        };

        scoresTable = new JTable(tableModel);
        styleTable(scoresTable);

        // Initial Calculation (updates Total column based on loaded data)
        for(int i=0; i<tableModel.getRowCount(); i++) {
            calculateRowMetrics(i);
        }

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

    private void calculateRowMetrics(int row) {
        double total = 0;
        int numComponents = policyComponents.size();

        for (int i = 0; i < numComponents; i++) {
            Object val = tableModel.getValueAt(row, 2 + i);
            double scorePart = 0;
            if (val instanceof Number) {
                scorePart = ((Number) val).doubleValue();
            } else {
                try { scorePart = Double.parseDouble(val.toString()); } catch (Exception ignored) {}
            }
            total += scorePart;
        }

        int totalColIdx = 2 + numComponents;
        tableModel.setValueAt(Math.round(total * 100.0) / 100.0, row, totalColIdx);

        String letter = "F";
        String cg = "0";

        if (gradeCutoffs != null && !gradeCutoffs.isEmpty()) {
            for (GradeRange range : gradeCutoffs) {
                if (total >= range.getMinScore()) {
                    cg = range.getGradeLetter();
                    letter = getLetterForCG(cg);
                    break;
                }
            }
        }

        tableModel.setValueAt(letter, row, totalColIdx + 1);
        tableModel.setValueAt(cg, row, totalColIdx + 2);
    }

    private String getLetterForCG(String cg) {
        switch (cg) {
            case "A+": return "10";
            case "A": return "10";
            case "A-": return "9";
            case "B": return "8";
            case "B-": return "7";
            case "C": return "6";
            case "D": return "5";
            case "X" : return "-";
            default: return "F";
        }
    }

    private void saveScores() {
        if (scoresTable.isEditing()) {
            scoresTable.getCellEditor().stopCellEditing();
        }

        // --- FIX 5: Map uses Integer Keys ---
        Map<Integer, Map<String, Double>> dataToSave = new HashMap<>();

        int numComponents = policyComponents.size();

        for (int row = 0; row < tableModel.getRowCount(); row++) {
            int enrollmentId = rowToEnrollmentIdMap.get(row);
            Map<String, Double> studentScores = new HashMap<>();

            for (int i = 0; i < numComponents; i++) {
                String compName = policyComponents.get(i).getName();
                Object val = tableModel.getValueAt(row, 2 + i);
                double score = 0.0;
                try { score = Double.parseDouble(val.toString()); } catch (Exception e) {}

                studentScores.put(compName, score);
            }
            dataToSave.put(enrollmentId, studentScores);
        }

        boolean success = facultyService.saveBatchScores(dataToSave);

        if(success) {
            JOptionPane.showMessageDialog(this, "Scores saved to database.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error saving scores.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
            g2.dispose();
        }
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }
}