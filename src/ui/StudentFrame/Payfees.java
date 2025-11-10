package ui.StudentFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Payfees extends JFrame {

    private String rollNumber;
    private String username;

    // --- DARK THEME COLORS (RESTORED) ---
    private Color bgColor = new Color(45, 45, 45);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color sideMenuColor = new Color(60, 60, 60);
    private Color buttonColor = new Color(57, 174, 168); // Teal accent
    private Color textColor = Color.WHITE;
    private Color headerColor = new Color(40, 55, 100); // Dark blue for headers

    // --- NEW: Dark theme colors for alternating rows ---
    private Color rowLight = new Color(60, 60, 60);  // Slightly lighter dark
    private Color rowDark = new Color(50, 50, 50);   // Same as main panel
    // --- END OF THEME CHANGE ---


    public Payfees(String rollNumber, String username) {
        super("Fee Payment - " + username);
        this.rollNumber = rollNumber;
        this.username = username;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor); // Dark background
        setLayout(new BorderLayout(20, 20));

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(bgColor); // Dark background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        // 1. TOP: Title
        JLabel titleLabel = new JLabel("Fee Payment Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textColor); // White text
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 2. CENTER: Main content panel
        JPanel topSplitPanel = new JPanel(new BorderLayout(20, 0));
        topSplitPanel.setOpaque(false); // Transparent background

        topSplitPanel.add(createStudentInfoPanel(), BorderLayout.WEST);
        topSplitPanel.add(createCurrentFeePanel(), BorderLayout.CENTER);

        mainPanel.add(topSplitPanel, BorderLayout.CENTER);

        // 3. BOTTOM: Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        JButton checkReceiptsButton = createStyledButton("Check Fee Receipts");
        checkReceiptsButton.addActionListener(e -> {
            FeeReceiptsFrame feeframe = new FeeReceiptsFrame(rollNumber, username);
            feeframe.setVisible(true);
        });
        buttonPanel.add(checkReceiptsButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the "Student Info" panel on the left.
     */
    private JPanel createStudentInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(450, 0));
        // Use the dark blue header
        panel.add(createSectionHeader("Student Info"), BorderLayout.NORTH);

        // Panel for the data rows, using GridLayout
        JPanel detailsPanel = new JPanel(new GridLayout(6, 1));
        detailsPanel.setBackground(mainPanelColor); // Dark background
        detailsPanel.setBorder(BorderFactory.createLineBorder(sideMenuColor));

        // --- THEME CHANGE APPLIED ---
        // createDataRow now uses dark colors
        detailsPanel.add(createDataRow("Roll No :", rollNumber, false));
        detailsPanel.add(createDataRow("Student Name :", username, true));
        detailsPanel.add(createDataRow("Admission Year :", "July 2024", false));
        detailsPanel.add(createDataRow("Term :", "July 2024/BTECH/CSE-IIITD/Semester 3", true));
        detailsPanel.add(createDataRow("Fee Pattern :", "Fee Pattern of July 2024/BTECH/CSE", false));
        detailsPanel.add(createDataRow("All Amounts In :", "₹ Currency", true));

        panel.add(detailsPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the "Student Fee Details" panel on the right.
     */
    private JPanel createCurrentFeePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        // Use the dark blue header
        panel.add(createSectionHeader("Student Fee Details"), BorderLayout.NORTH);

        String[] columnNames = {"Description", "Amount (₹)"};
        Object[][] data = {
                {"Tuition Fee - Sem 3", "225,000.00"},
                {"Hostel Fee - Sem 3", "80,000.00"},
                {"Fine", "0.00"},
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable feeTable = createStyledTable(model);
        feeTable.setRowHeight(30);

        // --- THEME CHANGE APPLIED ---
        // The renderer now correctly applies a dark theme
        feeTable.getColumnModel().getColumn(0).setCellRenderer(new DarkThemeCellRenderer(JLabel.LEFT));
        feeTable.getColumnModel().getColumn(1).setCellRenderer(new DarkThemeCellRenderer(JLabel.RIGHT));
        // --- END OF THEME CHANGE ---

        JScrollPane scrollPane = createStyledScrollPane(feeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- SOUTH PANEL for Total and Button ---
        JPanel southPanel = new JPanel(new BorderLayout(10, 0));
        southPanel.setOpaque(false);
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        totalPanel.setOpaque(false);
        JLabel totalLabel = new JLabel("Total Due:");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(textColor); // White text

        JLabel amountLabel = new JLabel("₹ 305,000.00");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        amountLabel.setForeground(buttonColor); // Teal accent
        totalPanel.add(totalLabel);
        totalPanel.add(amountLabel);

        JPanel payPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        payPanel.setOpaque(false);
        JButton payButton = createStyledButton("Pay Now");
        payButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        payButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        payButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Redirecting to payment gateway...");
        });
        payPanel.add(payButton);

        southPanel.add(totalPanel, BorderLayout.CENTER);
        southPanel.add(payPanel, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Helper to create the dark blue header bar for a section.
     */
    private JPanel createSectionHeader(String title) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // --- THEME CHANGE ---
        headerPanel.setBackground(headerColor); // Use dark blue header
        // --- END OF THEME CHANGE ---
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(textColor); // White text
        headerPanel.add(headerLabel);
        return headerPanel;
    }

    /**
     * Helper to create a single row for the Student Info panel.
     * --- THIS IS NOW A DARK THEME ROW ---
     */
    private JPanel createDataRow(String label, String value, boolean isDark) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        // --- THEME CHANGE ---
        // Use the new DARK row colors
        rowPanel.setBackground(isDark ? rowDark : rowLight);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(textColor); // Use WHITE text
        rowPanel.add(labelComponent);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComponent.setForeground(textColor); // Use WHITE text
        rowPanel.add(valueComponent);
        // --- END OF THEME CHANGE ---

        return rowPanel;
    }

    /**
     * Helper to create a styled button.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor); // Teal accent
        button.setForeground(textColor); // White text
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    /**
     * Helper to create a styled table.
     */
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        // Base dark theme
        table.setBackground(mainPanelColor);
        table.setForeground(textColor);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(sideMenuColor.brighter());
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(buttonColor.darker()); // Teal selection
        table.setSelectionForeground(textColor);

        // --- TABLE HEADER THEME CHANGE ---
        table.getTableHeader().setBackground(buttonColor); // Dark blue header
        table.getTableHeader().setForeground(textColor); // White text
        // --- END OF CHANGE ---

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(sideMenuColor));
        table.getTableHeader().setReorderingAllowed(false);

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        return table;
    }

    /**
     * Helper to create a styled scroll pane.
     */
    private JScrollPane createStyledScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createLineBorder(sideMenuColor));
        scrollPane.getViewport().setBackground(mainPanelColor); // Dark background
        return scrollPane;
    }

    /**
     * Custom renderer to apply dark theme colors to table cells.
     * --- THIS IS NOW A DARK THEME RENDERER ---
     */
    private class DarkThemeCellRenderer extends DefaultTableCellRenderer {
        public DarkThemeCellRenderer(int horizontalAlignment) {
            setHorizontalAlignment(horizontalAlignment);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                // Use the theme's selection color
                c.setBackground(buttonColor.darker());
                c.setForeground(textColor);
            } else {
                // --- APPLY DARK THEME ---
                // Set alternating dark row color
                c.setBackground(row % 2 == 0 ? rowLight : rowDark);
                // Set text color to white
                c.setForeground(textColor);
                // --- END OF CHANGE ---
            }
            return c;
        }
    }
}



