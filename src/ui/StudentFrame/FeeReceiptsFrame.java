package ui.StudentFrame;

import ui.components.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class FeeReceiptsFrame extends JFrame {

    private String rollNumber;
    private String username;

    // --- UI COLOR PALETTE FROM StudentDashboard ---
    private Color bgColor = new Color(42, 48, 60);            // --background
    private Color sideMenuColor = new Color(48, 54, 70);      // --sidebar-background
    private Color mainPanelColor = new Color(42, 48, 60);       // --background
    private Color cardColor = new Color(54, 59, 74);          // --card
    private Color borderColor = new Color(64, 69, 89);        // --border
    private Color buttonColor = new Color(52, 159, 148);      // --primary / --accent
    private Color buttonColorGlow = new Color(79, 196, 184);  // --primary-glow
    private Color textColor = new Color(255, 255, 255);       // --foreground
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color downloadHoverColor = new Color(38, 44, 58);

    private HeaderButton headerButton;

    public FeeReceiptsFrame(String rollNumber, String username) {
        super("Fee Receipts - " + username);
        this.rollNumber = rollNumber;
        this.username = username;
        this.headerButton = new HeaderButton();

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        // --- Main Panel (like dashboard's 'homePanel') ---
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(mainPanelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 40, 40));
        add(mainPanel, BorderLayout.CENTER);

        // --- 1. TOP: Title Panel (with Back Button) ---
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // --- Create Back Button ---
        RoundedButton backButton = headerButton.createHeaderButton("← Back");
        backButton.addActionListener(e -> {
            dispose(); // Close this window
        });

        // --- Title Label ---
        JLabel titleLabel = new JLabel("My Fee Receipts");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // --- Header Content Panel (to hold button and title) ---
        JPanel headerContentPanel = new JPanel(new BorderLayout());
        headerContentPanel.setOpaque(false);
        headerContentPanel.add(backButton, BorderLayout.WEST);
        headerContentPanel.add(titleLabel, BorderLayout.CENTER);

        // --- Add components to titlePanel ---
        titlePanel.add(headerContentPanel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        titleSeparator.setForeground(borderColor);
        titleSeparator.setBackground(mainPanelColor);
        titleSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        titlePanel.add(titleSeparator);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // --- 2. CENTER: Table Card ---
        RoundedPanel tableCard = new RoundedPanel(15, cardColor, borderColor, 1);
        tableCard.setLayout(new BorderLayout(0, 20));
        tableCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        tableCard.setOpaque(false);

        JLabel cardTitle = new JLabel("Receipt History");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cardTitle.setForeground(textColor);
        tableCard.add(cardTitle, BorderLayout.NORTH);

        // --- Create the table for receipts ---
        String[] columnNames = {"Receipt ID", "Date", "Description", "Amount (₹)", "Status", "Action"};

        Object[][] data = {
                {"FR2025001", "2025-08-01", "Tuition Fee - Sem 1", "225,000.00", "Paid", "Download"},
                {"FR2025002", "2025-08-01", "Hostel Fee - Sem 1", "80,000.00", "Paid", "Download"},
                {"FR2026001", "2026-01-05", "Tuition Fee - Sem 2", "225,000.00", "Pending", "Pay Now"},
                {"FR2026002", "2026-01-05", "Hostel Fee - Sem 2", "80,000.00", "Pending", "Pay Now"},
        };

        JTable receiptsTable = createStyledTable(data, columnNames);

        // --- Set renderers and editors for the action column ---
        receiptsTable.getColumn("Action").setCellRenderer(new TableButtonRenderer());
        receiptsTable.getColumn("Action").setCellEditor(new TableButtonEditor());

        JScrollPane scrollPane = createStyledScrollPane(receiptsTable);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tableCard, BorderLayout.CENTER);
    }

    // --- Table Styling Method ---
// --- Table Styling Method ---
    private JTable createStyledTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the "Action" column (column 5) is editable
                return column == 5;
            }
        };
        JTable table = new JTable(model);
        table.setBackground(cardColor);
        table.setForeground(textColor);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(65); // <-- 1. INCREASED ROW HEIGHT
        table.setGridColor(borderColor);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(buttonColor.darker());
        table.setSelectionForeground(textColor);

        // --- Table Header ---
        table.getTableHeader().setBackground(sideMenuColor);
        table.getTableHeader().setForeground(textColor);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(borderColor));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // --- Cell Renderers ---
        // --- 2. CENTER ALIGN ALL TEXT COLUMNS ---
        StyledCellRenderer centerRenderer = new StyledCellRenderer(JLabel.CENTER);

        // Loop through all columns *except* the last one (the button column)
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        // --- END OF CHANGE ---

        return table;
    }
    // --- ScrollPane Styling Method ---
    private JScrollPane createStyledScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        scrollPane.getViewport().setBackground(cardColor);
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI());
        return scrollPane;
    }

    private RoundedButton createActionButton(String text) {
        RoundedButton button = new RoundedButton(
                text, buttonColor, buttonColorGlow, 8);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Smaller padding for table
        button.setPreferredSize(null);
        return button;
    }

    // --- INNER CLASSES ---

    /**
     * Custom renderer for text cells.
     */
    private class StyledCellRenderer extends DefaultTableCellRenderer {
        public StyledCellRenderer(int horizontalAlignment) {
            setHorizontalAlignment(horizontalAlignment);
            setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                c.setBackground(buttonColor.darker());
                c.setForeground(textColor);
            } else {
                c.setBackground(cardColor);
                c.setForeground(textSecondaryColor);
                if (column == 3 || column == 4) { // Amount & Status
                    c.setForeground(textColor);
                }
            }
            return c;
        }
    }

    /**
     * Renders a JTable cell as a styled button (using RoundedButton).
     */
    /**
     * Renders a JTable cell as a styled button (using RoundedButton).
     */
    /**
     * Renders a JTable cell as a styled button (using RoundedButton).
     */
    private class TableButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String text = (value == null) ? "" : value.toString();
            RoundedButton button;

            if (text.equals("Pay Now")) {
                button = createActionButton(text);
            } else {
                // --- FIX: Create a custom "Download" button for the renderer ---
                // This now matches the logic in the TableButtonEditor
                button = new RoundedButton(
                        text,
                        sideMenuColor,       // Normal
                        downloadHoverColor,  // Hover
                        downloadHoverColor,  // Pressed (same as hover, per your request)
                        8
                );
                button.removeGradient();

                // Copy styling from createHeaderButton (which was used before)
                button.setForeground(textColor);
                button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                button.setPreferredSize(null);
                // --- END OF FIX ---
            }
            button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set table-specific font size

            // --- 3. FIX BUTTON ALIGNMENT ---
            // We use GridBagLayout to perfectly center the button in the tall cell
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(isSelected ? table.getSelectionBackground() : cardColor);
            panel.add(button, new GridBagConstraints()); // Add button with default (center) constraints
            return panel;
        }
    }

    /**
     * Manages the editing (clicking) of the button in the JTable.
     */
    /**
     * Manages the editing (clicking) of the button in the JTable.
     */
    /**
     * Manages the editing (clicking) of the button in the JTable.
     */
    private class TableButtonEditor extends DefaultCellEditor {
        protected RoundedButton button;
        private String label;
        private boolean isPushed;
        private JPanel panel;

        public TableButtonEditor() {
            super(new JCheckBox()); // Required by DefaultCellCellEditor

            // --- 3. FIX BUTTON ALIGNMENT ---
            // Use GridBagLayout to match the renderer
            panel = new JPanel(new GridBagLayout());
            panel.setOpaque(true);

            // Create the button instance
            button = createActionButton("Default"); // Placeholder
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.addActionListener(e -> fireEditingStopped());

            panel.add(button, new GridBagConstraints()); // Add button with default (center) constraints
            // --- END OF FIX ---
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;

            // Re-style the single button instance based on the cell value
            if (label.equals("Pay Now")) {
                // "Pay Now" uses gradient action button style
                // The hover effect (brighter gradient) is already built into RoundedButton
                button.setColors(buttonColor, buttonColor.brighter(), buttonColor.darker(), buttonColor);
                button.setGradient(buttonColor, buttonColorGlow);
            } else {
                // --- THIS BLOCK IS CORRECT ---
                // "Download" uses the header button style, with user's custom hover/pressed color
                button.setColors(sideMenuColor,       // Normal
                        downloadHoverColor,  // Hover
                        downloadHoverColor,  // Pressed (same as hover)
                        sideMenuColor);      // Active (not used)
                button.removeGradient();
            }

            // Set background of the panel to match selection
            panel.setBackground(table.getSelectionBackground());

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // --- THIS IS WHERE THE BUTTON CLICK LOGIC GOES ---
                if (label.equals("Download")) {
                    JOptionPane.showMessageDialog(button, "Downloading receipt...");
                } else if (label.equals("Pay Now")) {
                    JOptionPane.showMessageDialog(button, "Redirecting to payment gateway...");
                    // You could also open the Payfees frame here if needed
                    // Payfees feeFrame = new Payfees(rollNumber, username);
                    // feeFrame.setVisible(true);
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    /**
     * Inner class for a custom styled scrollbar.
     */
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = buttonColor;
            this.trackColor = cardColor;
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