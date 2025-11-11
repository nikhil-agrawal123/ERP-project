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
import java.util.HashMap;
import java.util.Map;

// --- Your Custom Components ---
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

public class UpdateScoresFrame extends JFrame {

    // --- UI Color Palette (Copied from FacultyDashboard) ---
    private Color bgColor = new Color(42, 48, 60);
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);

    // --- Mock Data Store ---
    private static Map<String, DefaultTableModel> courseDataStore = new HashMap<>();

    private DefaultTableModel tableModel;
    private JTable scoresTable;
    private String courseCode; // --- NEW --- Store course code for export

    public UpdateScoresFrame(String code) {
        super("Update Scores for " + code);
        this.courseCode = code; // --- NEW ---
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        // --- Main Panel with BorderLayout ---
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(mainPanelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // --- NEW: Back Button (MODIFIED TO GRADIENT) ---
        RoundedButton backButton = new RoundedButton(
                "← Back to Course", // Updated text
                buttonColor,      // gradStart
                buttonColorGlow,  // gradEnd
                10                // arc
        );
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Match style
        backButton.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22)); // Match style
        backButton.addActionListener(e -> dispose()); // Action: close this frame
        // --- END OF MODIFICATION ---

        // --- Title Panel ---
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel pageTitle = new JLabel("Update Scores");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        pageTitle.setForeground(textColor);
        pageTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pageSubtitle = new JLabel("Modify and save scores for " + code);
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pageSubtitle.setForeground(textSecondaryColor);
        pageSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(pageTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(pageSubtitle);

        // --- NEW: Header Panel (to hold Back Button and Title Panel) ---
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0)); // 15px h-gap
        headerPanel.setOpaque(false);
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        // --- MODIFIED --- Use the new headerPanel
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Table Data Model Setup ---
        String[] columnNames = {
                "Student Name",
                "Component 1 (10)",
                "Component 2 (10)",
                "Component 3 (10)",
                "Total"
        };

        if (courseDataStore.containsKey(code)) {
            tableModel = courseDataStore.get(code);
        } else {
            Object[][] data = {
                    {"Student 1", 8, 7, 9, 24},
                    {"Student 2", 10, 5, 8, 23},
                    {"Student 3", 7, 9, 10, 26},
                    {"Student 4", 5, 5, 5, 15},
                    {"Student 5", 9, 9, 8, 26},
                    {"Student 6", 10, 10, 10, 30}
            };

            tableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Only columns 1, 2, 3 are editable
                    return column > 0 && column < 4;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) return String.class;
                    else return Integer.class;
                }

                @Override
                public void setValueAt(Object aValue, int row, int col) {
                    int intValue;
                    try {
                        intValue = Integer.parseInt(aValue.toString());
                        if (intValue < 0) intValue = 0;
                        // Components are max 10
                        if (col > 0 && col < 4 && intValue > 10) intValue = 10;
                    } catch (NumberFormatException e) {
                        intValue = 0; // Default to 0 if input is invalid
                    }

                    super.setValueAt(intValue, row, col);

                    if (col >= 1 && col <= 3) {
                        int comp1 = (Integer) getValueAt(row, 1);
                        int comp2 = (Integer) getValueAt(row, 2);
                        int comp3 = (Integer) getValueAt(row, 3);
                        int total = comp1 + comp2 + comp3;
                        super.setValueAt(total, row, 4);
                    }
                }
            };
            courseDataStore.put(code, tableModel);
        }

        // --- Table Creation and Styling ---
        scoresTable = new JTable(tableModel);
        scoresTable.setBackground(cardColor);
        scoresTable.setForeground(textColor);
        scoresTable.setGridColor(borderColor);
        scoresTable.setRowHeight(40);
        scoresTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scoresTable.setSelectionBackground(buttonColorGlow);
        scoresTable.setSelectionForeground(Color.WHITE);
        scoresTable.setFillsViewportHeight(true);
        scoresTable.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Style Header
        // Style Header
        JTableHeader header = scoresTable.getTableHeader();
        header.setBackground(cardColor);
        header.setForeground(textSecondaryColor);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(100, 40));

// --- ADD THIS LINE ---
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, borderColor));

        // --- Cell Renderers ---
        // --- Cell Renderers ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(cardColor);
        centerRenderer.setForeground(textColor);

// We no longer need a separate leftRenderer

// Apply the center renderer to ALL columns (starting loop from 0)
        for (int i = 0; i < scoresTable.getColumnCount(); i++) {
            scoresTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // --- ScrollPane ---
        JScrollPane scrollPane = new JScrollPane(scoresTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardColor);
        scrollPane.setBackground(cardColor);

        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

        // --- Center Table Panel (USING YOUR ROUNDEDPANEL) ---
        RoundedPanel tablePanel = new RoundedPanel(15, cardColor, cardColor, 0);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // --- Bottom Button Panel ---
        // --- MODIFIED: Added 15px horizontal gap ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // --- NEW: Export Button ---
        RoundedButton exportButton = new RoundedButton(
                "Export as CSV",
                borderColor,             // Neutral color
                borderColor.brighter(),  // hover
                borderColor.darker(),    // pressed
                10                       // arc
        );
        exportButton.setForeground(Color.WHITE);
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        exportButton.setPreferredSize(new Dimension(180, 45));
        exportButton.addActionListener(e -> exportTableToCSV()); // --- NEW Action ---


        // --- Save Button (USING YOUR ROUNDEDBUTTON) ---
        // --- MODIFIED --- Use the gradient constructor to match
        RoundedButton saveButton = new RoundedButton(
                "Save Changes",
                buttonColor,       // gradStart
                buttonColorGlow,   // gradEnd
                10                 // arc
        );
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveButton.setPreferredSize(new Dimension(180, 45));

        saveButton.addActionListener(e -> {
            if (scoresTable.isEditing()) {
                scoresTable.getCellEditor().stopCellEditing();
            }
            JOptionPane.showMessageDialog(
                    this,
                    "Scores for " + code + " have been saved.",
                    "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // --- MODIFIED: Add export button first ---
        buttonPanel.add(exportButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Finalize ---
        add(mainPanel);
        setVisible(true);
    }

    /**
     * --- NEW METHOD ---
     * Handles exporting the JTable data to a CSV file.
     */
    private void exportTableToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as CSV");
        // Suggest a filename
        fileChooser.setSelectedFile(new File(this.courseCode + "_Scores.csv"));
        // Filter for .csv files
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Automatically add .csv extension if missing
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (FileWriter fw = new FileWriter(fileToSave);
                 BufferedWriter bw = new BufferedWriter(fw)) {

                // 1. Write Header Row
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    bw.write("\"" + tableModel.getColumnName(i) + "\""); // Enclose in quotes
                    if (i < tableModel.getColumnCount() - 1) {
                        bw.write(","); // CSV separator
                    }
                }
                bw.newLine();

                // 2. Write Data Rows
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        bw.write("\"" + String.valueOf(value) + "\""); // Enclose in quotes
                        if (col < tableModel.getColumnCount() - 1) {
                            bw.write(",");
                        }
                    }
                    bw.newLine();
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Data exported successfully to " + fileToSave.getName(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error exporting data: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }


    /**
     * Inner class for a custom-styled scrollbar.
     */
    class CustomScrollBarUI extends BasicScrollBarUI {
        // ... (This class is unchanged) ...
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = borderColor; // Thumb color
            this.trackColor = cardColor;   // Track color
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
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
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


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new UpdateScoresFrame("CS101"));
    }
}