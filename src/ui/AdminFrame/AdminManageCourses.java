package ui.AdminFrame;

import ui.dashboard.AdminDashboard;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class AdminManageCourses extends JFrame {

    private String adminID;
    private String username;

    // --- UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);      // Teal
    private Color buttonColorGlow = new Color(79, 196, 184);  // Brighter Teal (Hover)
    private Color buttonColorPressed = new Color(35, 110, 100); // Darker Teal (Pressed)
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);

    // --- Back Button Colors ---
    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private List<TabButton> tabButtons = new ArrayList<>();

    // --- TRACK HOVER ROW FOR DETAILS BUTTON EFFECT ---
    private int hoveredRow = -1;
    private AdminDashboard dashboardInstance;

    public AdminManageCourses(String adminID, String username, AdminDashboard dashboardInstance) {
        super("Manage Courses - Admin");
        this.adminID = adminID;
        this.username = username;
        this.dashboardInstance = dashboardInstance;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);

        // Main Container with padding
        JPanel mainContainer = new JPanel(new BorderLayout(0, 20));
        mainContainer.setBackground(mainPanelColor);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        add(mainContainer);

        // --- 1. Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        RoundedButton backBtn = new RoundedButton(
                "← Back to Dashboard",
                Buttonback, Buttonhover, borderColor.darker(), 8
        );
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setForeground(textColor);
        backBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backBtn.setPreferredSize(new Dimension(200, 45));

        backBtn.addActionListener(e -> {
            if (dashboardInstance != null) {
                dashboardInstance.setVisible(true); // Re-show the original window
            }
            dispose();
        });

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);

        JLabel titleLabel = new JLabel("Course Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textColor);

        JLabel subtitleLabel = new JLabel("Select a semester to manage course details.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(textSecondaryColor);

        titleGroup.add(titleLabel);
        titleGroup.add(Box.createRigidArea(new Dimension(0, 5)));
        titleGroup.add(subtitleLabel);

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeader.setOpaque(false);
        leftHeader.add(backBtn);
        leftHeader.add(Box.createRigidArea(new Dimension(20, 0)));
        leftHeader.add(titleGroup);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // --- 2. Content Section ---
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        // Tab Bar
        RoundedPanel tabBar = new RoundedPanel(10, cardColor, cardColor, 0);
        tabBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        contentPanel.add(tabBar, BorderLayout.NORTH);

        // Card Layout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        contentPanel.add(cardPanel, BorderLayout.CENTER);

        // --- 3. Generate Semesters ---
        for (int i = 1; i <= 8; i++) {
            String semName = "Semester " + i;
            String semId = "SEM" + i;

            TabButton tab = new TabButton(semName);
            tabButtons.add(tab);
            tab.addActionListener(e -> switchTab(semId, tab));
            tabBar.add(tab);

            cardPanel.add(createSemesterPanel(i), semId);
        }

        if (!tabButtons.isEmpty()) {
            switchTab("SEM1", tabButtons.get(0));
        }
    }

    private void switchTab(String cardName, TabButton activeBtn) {
        cardLayout.show(cardPanel, cardName);
        for (TabButton btn : tabButtons) {
            btn.setActive(false);
        }
        activeBtn.setActive(true);
    }

    private JPanel createSemesterPanel(int semester) {
        RoundedPanel panel = new RoundedPanel(15, cardColor, cardColor, 0);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Code", "Course Name", "Department", "Credits", "Action"};
        Object[][] data = getMockData(semester);

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only Action column
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        // --- HOVER LOGIC ---
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                int modelCol = (col >= 0) ? table.convertColumnIndexToModel(col) : -1;

                int newHoveredRow = -1;
                // If mouse is over "Action" column (index 4), track that row
                if (row >= 0 && modelCol == 4) {
                    newHoveredRow = table.convertRowIndexToModel(row);
                }

                if (newHoveredRow != hoveredRow) {
                    hoveredRow = newHoveredRow;
                    table.repaint(); // Force update of button colors
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoveredRow != -1) {
                    hoveredRow = -1;
                    table.repaint();
                }
            }
        });

        // Assign Renderer and Editor
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Column Widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);

        JScrollPane scrollPane = new JScrollPane(table);
        styleScrollPane(scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setBackground(cardColor);
        table.setForeground(textColor);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(55);
        table.setGridColor(borderColor);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(Buttonhover);
        table.setSelectionForeground(textColor);

        table.getTableHeader().setDefaultRenderer(new LeftAlignedHeaderRenderer());
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setReorderingAllowed(false);

        LeftAlignedCellRenderer textRenderer = new LeftAlignedCellRenderer();
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        }
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardColor);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = buttonColor;
                this.trackColor = cardColor;
            }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        });
    }

    private JButton createZeroButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        return b;
    }

    // --- INNER CLASSES FOR BUTTON RENDERING ---

    /**
     * ButtonRenderer
     * Matches RemoveUser design. Uses 'hoveredRow' to switch colors.
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(textColor);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFocusPainted(false);
            setContentAreaFilled(false); // We paint manually
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "Details" : value.toString());

            int modelRow = table.convertRowIndexToModel(row);

            // --- HOVER EFFECT LOGIC ---
            // If this row is the one tracked by MouseMotionListener: GLOW
            // Otherwise: NORMAL (Teal)
            if (modelRow == hoveredRow || isSelected) {
                setBackground(buttonColorGlow);
            } else {
                setBackground(buttonColor);
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Use the background color set in getTableCellRendererComponent
            g2.setColor(getBackground());

            // Paint rounded rect
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            super.paintComponent(g);
            g2.dispose();
        }
    }

    /**
     * ButtonEditor
     * Handles the click interaction (Press down, Release).
     */
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;
        private JTable currentTable;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);

            // --- FIX: Define anonymous class to handle painting on the button itself ---
            button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Use the color set by mouse listeners
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    super.paintComponent(g);
                    g2.dispose();
                }
            };

            button.setOpaque(false); // Required so we can draw round shape
            button.setContentAreaFilled(false); // Disable default rectangle
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(textColor);
            button.setBorder(new EmptyBorder(10, 15, 10, 15));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Handle Mouse Events within the editor to show press effects
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    button.setBackground(buttonColorPressed);
                    button.repaint();
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    button.setBackground(buttonColorGlow); // Return to hover state
                    button.repaint();
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(buttonColorGlow);
                    button.repaint();
                }
            });

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Details" : value.toString();
            button.setText(label);

            // Initial state when clicking is Hover/Glow
            button.setBackground(buttonColorGlow);

            isPushed = true;
            currentRow = row;
            currentTable = table;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                String courseCode = (String) currentTable.getValueAt(currentRow, 0);
                String courseName = (String) currentTable.getValueAt(currentRow, 1);

                // Action
                JOptionPane.showMessageDialog(AdminManageCourses.this,
                        "Opening details for:\n" + courseCode + " : " + courseName,
                        "Details", JOptionPane.INFORMATION_MESSAGE);
            }
            isPushed = false;
            return label;
        }
    }

    // --- MOCK DATA ---
    private Object[][] getMockData(int semester) {
        if (semester % 2 != 0) {
            return new Object[][]{
                    {"CS" + semester + "01", "Advanced Data Structures", "CSE", "4", "View Details"},
                    {"CS" + semester + "02", "Digital Logic Design", "ECE", "3", "View Details"},
                    {"MA" + semester + "01", "Discrete Mathematics", "MATH", "4", "View Details"},
                    {"HU" + semester + "01", "Engineering Ethics", "HSS", "2", "View Details"}
            };
        } else {
            return new Object[][]{
                    {"CS" + semester + "01", "Operating Systems", "CSE", "4", "View Details"},
                    {"CS" + semester + "02", "Database Management Systems", "CSE", "4", "View Details"},
                    {"CS" + semester + "03", "Computer Networks", "CSE", "3", "View Details"},
                    {"PR" + semester + "01", "Project Phase " + (semester / 2), "CORE", "2", "View Details"}
            };
        }
    }

    // --- HELPERS ---
    class LeftAlignedHeaderRenderer extends DefaultTableCellRenderer {
        public LeftAlignedHeaderRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setBackground(cardColor);
            setForeground(textSecondaryColor);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, textColor),
                    BorderFactory.createEmptyBorder(0, 20, 0, 20)
            ));
        }
    }

    class LeftAlignedCellRenderer extends DefaultTableCellRenderer {
        public LeftAlignedCellRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) c.setBackground(Buttonhover);
            else c.setBackground(cardColor);
            c.setForeground(textColor);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor),
                    BorderFactory.createEmptyBorder(0, 20, 0, 20)
            ));
            return c;
        }
    }

    class TabButton extends JButton {
        private boolean isActive = false;
        public TabButton(String text) {
            super(text);
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setForeground(textSecondaryColor);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if (!isActive) setForeground(textColor); }
                public void mouseExited(MouseEvent e) { if (!isActive) setForeground(textSecondaryColor); }
            });
        }
        public void setActive(boolean b) {
            this.isActive = b;
            setForeground(b ? textColor : textSecondaryColor);
            repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isActive) { g2.setColor(buttonColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); }
            else { g2.setColor(cardColor); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); }
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}