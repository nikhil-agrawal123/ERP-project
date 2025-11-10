package ui.StudentFrame;

import javax.swing.*;
import javax.swing.border.Border; // Import the Border class
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class FeeReceiptsFrame extends JFrame {

    private String rollNumber;
    private String username;

    // Use consistent colors from the dashboard
    private Color bgColor = new Color(45, 45, 45);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;

    public FeeReceiptsFrame(String rollNumber, String username) {
        super("Fee Receipts - " + username);
        this.rollNumber = rollNumber;
        this.username = username;

        // --- IMPORTANT ---
        // Use DISPOSE_ON_CLOSE so it only closes this window,
        // not the entire application.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1080, 1080); // You had 800x600 before, I kept your new size
        setLocationRelativeTo(null); // Center the window
        getContentPane().setBackground(bgColor);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(mainPanelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Fee Receipts");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Create the table for receipts ---
        String[] columnNames = {"Receipt ID", "Date", "Description", "Amount", "Status", "Action"};

        // TODO: Replace this dummy data with a call to a FeeService
        Object[][] data = {
                {"FR2025001", "2025-08-01", "Tuition Fee - Sem 1", "225,000.00", "Paid", "Download"},
                {"FR2025002", "2025-08-01", "Hostel Fee - Sem 1", "80,000.00", "Paid", "Download"},
                {"FR2026001", "2026-01-05", "Tuition Fee - Sem 2", "225,000.00", "Pending", "Pay Now"},
        };

        JTable receiptsTable = createStyledTable(data, columnNames);

        // --- This is the magic part: add a button to the "Action" column ---
        receiptsTable.getColumn("Action").setCellRenderer(new ButtonRenderer(buttonColor));
        receiptsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), buttonColor));

        JScrollPane scrollPane = createStyledScrollPane(receiptsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    // --- Copied styling methods from StudentDashboard for consistency ---

    private JTable createStyledTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the "Action" column (column 5) is editable (to click the button)
                return column == 5;
            }
        };
        JTable table = new JTable(model);
        table.setBackground(mainPanelColor);
        table.setForeground(textColor);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(sideMenuColor.brighter());
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(buttonColor.darker());
        table.setSelectionForeground(textColor);
        table.getTableHeader().setBackground(sideMenuColor);
        table.getTableHeader().setForeground(buttonColor);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(sideMenuColor));
        table.getTableHeader().setReorderingAllowed(false);

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        // Center align all except description
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 2) { // Description column
                table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        return table;
    }

    private JScrollPane createStyledScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createLineBorder(sideMenuColor));
        scrollPane.getViewport().setBackground(mainPanelColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }
}

// --- Helper class for rendering a JTable cell as a button ---
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer(Color color) {
        setOpaque(true);
        setBackground(color);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- CHANGE HERE ---
        // Create a 1px border using a darker shade of the button color
        Border line = BorderFactory.createLineBorder(color.darker(), 1);
        // Keep the original 4px top/bottom, 10px left/right padding
        Border padding = BorderFactory.createEmptyBorder(4, 10, 4, 10);
        // Combine the line border and the padding
        setBorder(BorderFactory.createCompoundBorder(line, padding));
        // --- END OF CHANGE ---
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

// --- Helper class for editing a JTable cell as a button (to make it clickable) ---
class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;

    public ButtonEditor(JCheckBox checkBox, Color color) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- CHANGE HERE ---
        // Create a 1px border using a darker shade of the button color
        Border line = BorderFactory.createLineBorder(color.darker(), 1);
        // Keep the original 4px top/bottom, 10px left/right padding
        Border padding = BorderFactory.createEmptyBorder(4, 10, 4, 10);
        // Combine the line border and the padding
        button.setBorder(BorderFactory.createCompoundBorder(line, padding));
        // --- END OF CHANGE ---

        button.addActionListener(e -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            // --- THIS IS WHERE THE BUTTON CLICK LOGIC GOES ---
            String action = label;
            if (action.equals("Download")) {
                JOptionPane.showMessageDialog(button, "Downloading receipt...");
            } else if (action.equals("Pay Now")) {
                JOptionPane.showMessageDialog(button, "Redirecting to payment gateway...");
            }
        }
        isPushed = false;
        return label;
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}