package ui.StudentFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Payfees extends JFrame {

    private String rollNumber;
    private String username;

    // --- UI COLOR PALETTE FROM StudentDashboard ---
    private Color bgColor = new Color(42, 48, 60);            // --background
    private Color sideMenuColor = new Color(48, 54, 70);      // --sidebar-background
    private Color mainPanelColor = new Color(42, 48, 60);       // --background
    private Color cardColor = new Color(54, 59, 74);          // --card
    private Color popoverColor = new Color(46, 52, 66);       // --popover
    private Color borderColor = new Color(64, 69, 89);        // --border
    private Color buttonColor = new Color(52, 159, 148);      // --primary / --accent
    private Color buttonColorGlow = new Color(79, 196, 184);  // --primary-glow
    private Color textColor = new Color(255, 255, 255);       // --foreground
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color Buttonback = new Color(35, 42, 55);
    private Color Buttonhover = new Color(25, 30, 40);

    public Payfees(String rollNumber, String username) {
        super("Fee Payment - " + username);
        this.rollNumber = rollNumber;
        this.username = username;

        // --- Frame Setup (Matching Dashboard) ---
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor); // Use new theme color
        setLayout(new BorderLayout());

        // --- Main Panel (like dashboard's 'homePanel') ---
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(mainPanelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 40, 40));
        add(mainPanel, BorderLayout.CENTER);

        // --- 1. TOP: Title Panel (MODIFIED) ---
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // --- NEW: Create Back Button ---
        RoundedButton backButton = createHeaderButton("← Back"); // <-- CHANGED HERE
        backButton.addActionListener(e -> {
            dispose(); // Just close this window, dashboard is underneath
        });

        // --- MODIFIED: Title Label ---
        JLabel titleLabel = new JLabel("Fee Payment Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36)); // Match dashboard
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the title

        // --- NEW: Header Content Panel (to hold button and title) ---
        JPanel headerContentPanel = new JPanel(new BorderLayout());
        headerContentPanel.setOpaque(false);
        headerContentPanel.add(backButton, BorderLayout.WEST);
        headerContentPanel.add(titleLabel, BorderLayout.CENTER);

        // --- MODIFIED: Add new header panel to titlePanel ---
        titlePanel.add(headerContentPanel); // Add the panel with button + title
        titlePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        titleSeparator.setForeground(borderColor);
        titleSeparator.setBackground(mainPanelColor);
        titleSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        titlePanel.add(titleSeparator);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        // --- END OF MODIFICATIONS ---

        // --- 2. CENTER: Main Content Panel (using 50/50 split) ---
        //    We use a wrapper panel with GridLayout(1, 2) to force a 50/50 split
        //    This matches the layout of the "Quick Links" and "Minors" panels
        JPanel centerWrapper = new JPanel(new BorderLayout(30, 0)); // Use BorderLayout with 30px gap
        centerWrapper.setOpaque(false); // Transparent
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0)); // Add top padding

        centerWrapper.add(createStudentInfoPanel(), BorderLayout.WEST);   // Add panel to the WEST
        centerWrapper.add(createCurrentFeePanel(), BorderLayout.CENTER);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // --- 3. BOTTOM: Button Panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        RoundedButton checkReceiptsButton = createActionButton("Check Fee Receipts"); // <-- CHANGED HERE
        checkReceiptsButton.addActionListener(e -> {
            // Assuming FeeReceiptsFrame is also refactored or works
            FeeReceiptsFrame feeframe = new FeeReceiptsFrame(rollNumber, username);
            feeframe.setVisible(true);
        });
        bottomPanel.add(checkReceiptsButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    /**
     * Creates the "Student Info" panel (Card) on the left.
     */
    private RoundedPanel createStudentInfoPanel() {
        RoundedPanel panel = new RoundedPanel(15, cardColor, borderColor, 1);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        panel.setPreferredSize(new Dimension(650, 0));
        // --- Header ---
        JLabel title = new JLabel("Student Info");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(textColor);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Space

        // --- Info Rows ---
        panel.add(createDetailRow("Roll No:", rollNumber));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createDetailRow("Student Name:", username));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createDetailRow("Admission Year:", "July 2024"));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createDetailRow("Term:", "July 2024/BTECH/CSE-IIITD/Semester 3"));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createDetailRow("Fee Pattern:", "Fee Pattern of July 2024/BTECH/CSE"));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createDetailRow("All Amounts In:", "₹ Currency"));

        panel.add(Box.createVerticalGlue()); // Push content to top

        return panel;
    }

    /**
     * Helper to create a key-value detail row for the info panel.
     */
    private JPanel createDetailRow(String label, String value) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.Y_AXIS));
        rowPanel.setOpaque(false);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(textSecondaryColor); // Muted label
        labelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        valueComponent.setForeground(textColor); // Bright value
        valueComponent.setAlignmentX(Component.LEFT_ALIGNMENT);

        rowPanel.add(labelComponent);
        rowPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rowPanel.add(valueComponent);
        return rowPanel;
    }

    /**
     * Creates the "Student Fee Details" panel (Card) on the right.
     */
    private RoundedPanel createCurrentFeePanel() {
        RoundedPanel panel = new RoundedPanel(15, cardColor, borderColor, 1);
        panel.setLayout(new BorderLayout(0, 20)); // Vertical gap
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // --- Header ---
        JLabel title = new JLabel("Student Fee Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(textColor);
        panel.add(title, BorderLayout.NORTH);

        // --- Table ---
        String[] columnNames = {"Description", "Amount (₹)"};
        Object[][] data = {
                {"Tuition Fee - Sem 3", "225,000.00"},
                {"Hostel Fee - Sem 3", "80,000.00"},
                {"Fine", "0.00"},
                // Add more rows to test scrolling
                {"Library Fee", "5,000.00"},
                {"Sports Facility", "3,000.00"},
                {"Lab Charges", "7,500.00"},
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable feeTable = createStyledTable(model);
        JScrollPane scrollPane = createStyledScrollPane(feeTable);
        // Set a preferred height for the table's scroll pane
        scrollPane.setPreferredSize(new Dimension(0, 250));
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- SOUTH PANEL for Total and Button ---
        JPanel southPanel = new JPanel(new BorderLayout(10, 0));
        southPanel.setOpaque(false);
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Top padding

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        totalPanel.setOpaque(false);
        JLabel totalLabel = new JLabel("Total Due:");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(textSecondaryColor);

        JLabel amountLabel = new JLabel("₹ 305,000.00");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        amountLabel.setForeground(buttonColor); // Teal accent
        totalPanel.add(totalLabel);
        totalPanel.add(amountLabel);

        // Use the gradient "ActionButton"
        RoundedButton payButton = createActionButton("Pay Now");
        payButton.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Make font larger
        payButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Redirecting to payment gateway...");
        });

        // Wrapper to keep the button at its preferred size on the right
        JPanel payPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        payPanel.setOpaque(false);
        payPanel.add(payButton);

        southPanel.add(totalPanel, BorderLayout.CENTER);
        southPanel.add(payPanel, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }


    /**
     * Helper to create a styled table.
     */
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(cardColor);
        table.setForeground(textColor);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(40); // Taller rows
        table.setGridColor(borderColor); // Use border color for grid
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(buttonColor); // Use accent for selection
        table.setSelectionForeground(textColor);

        // --- Table Header ---
        table.getTableHeader().setBackground(sideMenuColor); // Dark header
        table.getTableHeader().setForeground(textColor);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(borderColor));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45)); // Header height

        // --- Cell Renderer ---
        StyledCellRenderer leftRenderer = new StyledCellRenderer(JLabel.LEFT);
        StyledCellRenderer rightRenderer = new StyledCellRenderer(JLabel.RIGHT);
        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        return table;
    }

    /**
     * Helper to create a styled scroll pane (with custom scrollbar).
     */
    private JScrollPane createStyledScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor)); // Use border color
        scrollPane.getViewport().setBackground(cardColor); // Match table bg
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI());
        return scrollPane;
    }

    /**
     * Custom renderer to apply new theme colors to table cells.
     */
    private class StyledCellRenderer extends DefaultTableCellRenderer {
        public StyledCellRenderer(int horizontalAlignment) {
            setHorizontalAlignment(horizontalAlignment);
            // Add padding
            setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                c.setBackground(buttonColor);
                c.setForeground(textColor);
            } else {
                // Use the card color, not alternating
                c.setBackground(cardColor);
                // Make description text muted, amount text primary
                c.setForeground(column == 0 ? textSecondaryColor : textColor);
            }
            return c;
        }
    }


    // --- HELPER METHODS (from StudentDashboard) ---

    /**
     * Creates a styled header button (solid, dark background).
     */
    private RoundedButton createHeaderButton(String text) {
        RoundedButton button = new RoundedButton(
                text,
                Buttonback, // normal
                Buttonhover,   // hover
                Buttonhover.darker(), // pressed
                8
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setPreferredSize(null);
        return button;
    }

    /**
     * Creates a styled action button (gradient background).
     */
    private RoundedButton createActionButton(String text) {
        RoundedButton button = new RoundedButton(
                text,
                buttonColor,      // Gradient Start (--primary)
                buttonColorGlow,  // Gradient End (--primary-glow)
                8                 // Arc radius
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setPreferredSize(null);
        return button;
    }

    /**
     * Inner class for a custom styled scrollbar.
     */
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = buttonColor;      // Accent color
            this.trackColor = cardColor;      // Background of card
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