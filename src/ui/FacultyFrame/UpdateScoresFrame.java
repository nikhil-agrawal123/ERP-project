package ui.FacultyFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap; // <-- 1. IMPORTED
import java.util.Map;   // <-- 1. IMPORTED

public class UpdateScoresFrame extends JFrame {

    // --- Style Colors ---
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color tableBgColor = new Color(60, 60, 60);
    private Color headerBgColor = new Color(75, 75, 75);
    private Color gridColor = new Color(90, 90, 90);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;

    // --- Mock Data Store ---
    // 2. This static map will hold the table models for all course codes.
    // It will persist as long as the application is running.
    private static Map<String, DefaultTableModel> courseDataStore = new HashMap<>();

    private DefaultTableModel tableModel;

    public UpdateScoresFrame(String code) {
        super("Update Scores for " + code);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1080, 768);
        setLocationRelativeTo(null);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        // --- Main Panel with BorderLayout ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(mainPanelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Title Label ---
        JLabel titleLabel = new JLabel("Updating Scores for " + code);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Table Setup ---
        // 1. Define Column Names (Added "Total")
        String[] columnNames = {
                "Student Name",
                "Component 1 (10)",
                "Component 2 (10)",
                "Component 3 (10)",
                "Total"
        };

        // 3. Check Data Store or Create New Table Model
        if (courseDataStore.containsKey(code)) {
            // --- Data already exists, just retrieve it ---
            tableModel = courseDataStore.get(code);

        } else {
            // --- First time opening this code, create new data ---
            // 2. Define Hardcoded Data (Added calculated total)
            Object[][] data = {
                    {"Student 1", 8, 7, 9, 24},  // 8 + 7 + 9 = 24
                    {"Student 2", 10, 5, 8, 23}, // 10 + 5 + 8 = 23
                    {"Student 3", 7, 9, 10, 26}  // 7 + 9 + 10 = 26
            };

            // 3. Create the Table Model
            tableModel = new DefaultTableModel(data, columnNames) {

                // Make only component columns editable
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column > 0 && column < 4;
                }

                // Define column types
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0) {
                        return String.class;
                    } else {
                        return Integer.class;
                    }
                }

                // This method is called when a cell value is changed
                @Override
                public void setValueAt(Object aValue, int row, int col) {
                    int intValue;
                    try {
                        intValue = Integer.parseInt(aValue.toString());
                    } catch (NumberFormatException e) {
                        intValue = 0;
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

            // --- !! IMPORTANT: Add the new model to our store !! ---
            courseDataStore.put(code, tableModel);
        }


        // 4. Create the JTable
        JTable scoresTable = new JTable(tableModel);

        // 5. Style the JTable
        scoresTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scoresTable.setBackground(tableBgColor);
        scoresTable.setForeground(textColor);
        scoresTable.setGridColor(gridColor);
        scoresTable.setRowHeight(30);
        scoresTable.setSelectionBackground(buttonColor);
        scoresTable.setSelectionForeground(textColor);

        // 6. Style the Table Header
        JTableHeader header = scoresTable.getTableHeader();
        header.setBackground(headerBgColor);
        header.setForeground(textColor);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setReorderingAllowed(false);

        // 7. Create the Scroll Pane
        JScrollPane scrollPane = new JScrollPane(scoresTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(gridColor));
        scrollPane.getViewport().setBackground(tableBgColor);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Save Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(mainPanelColor);

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(buttonColor);
        saveButton.setForeground(textColor);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // --- Button Action ---
        // 4. Cleaned up save button listener
        saveButton.addActionListener(e -> {
            // Stop cell editing to ensure the last edited value is saved
            if (scoresTable.isEditing()) {
                scoresTable.getCellEditor().stopCellEditing();
            }

            // The data is already "saved" because it was updated
            // in the tableModel, which lives in our static map.
            // When you're ready for a database, this is where you would
            // loop through the tableModel rows and send them to the DB.

            JOptionPane.showMessageDialog(this,
                    "Scores updated successfully.",
                    "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose(); // Closes this (UpdateScoresFrame) window
        });

        // Add the main panel to the frame
        add(mainPanel);
    }
}