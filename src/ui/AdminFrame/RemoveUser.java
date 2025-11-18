package ui.AdminFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RemoveUserFrame
 * <p>
 * Full-screen (2160x1080) admin UI to select role, search, view table of users
 * and delete individual rows. Matches the colour/theme/aesthetics used across
 * your dashboards (RoundedPanel / RoundedButton).
 * <p>
 * Drop this file into ui/AdminFrame and open with new RemoveUserFrame(adminID, username)
 * It depends on ui.components.RoundedPanel and ui.components.RoundedButton which
 * you already have in the project.
 */
public class RemoveUser extends JFrame {

    // --- Theme colours (copied from dashboards) ---
    private final Color bgColor = new Color(42, 48, 60);
    private final Color sideMenuColor = new Color(48, 54, 70);
    private final Color mainPanelColor = new Color(42, 48, 60);
    private final Color cardColor = new Color(54, 59, 74);
    private final Color popoverColor = new Color(46, 52, 66);
    private final Color borderColor = new Color(64, 69, 89);
    private final Color buttonColor = new Color(52, 159, 148);
    private final Color buttonColorGlow = new Color(79, 196, 184);
    private final Color textColor = new Color(255, 255, 255);
    private final Color textSecondaryColor = new Color(179, 179, 179);

    // --- Delete Colors (matched to AdminDashboard logout) ---
    private final Color dangerRed = new Color(190, 60, 60);
    private final Color dangerRedHover = new Color(220, 70, 70);    // Brighter hover
    private final Color dangerRedPressed = new Color(160, 40, 40);  // Pressed

    // --- Icons for custom checkbox (from StudentRegCourses) ---
    private final ImageIcon uncheckedIcon;
    private final ImageIcon checkedIcon;

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final List<UserRow> allRows = new ArrayList<>();

    private final JComboBox<String> roleDropdown;
    private final JTextField searchField;

    // --- MODIFIED --- Tracks row for table button hover effect
    private int hoveredRow = -1;

    public RemoveUser(String adminId, String username) {
        super("Remove Users - " + username);

        // --- Create the custom checkbox icons (from StudentRegCourses) ---
        this.uncheckedIcon = createCheckBoxIcon(false);
        this.checkedIcon = createCheckBoxIcon(true);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Set an initial size for the frame, but allow it to be maximized/resized
        setSize(1200, 800); // Changed to a more standard initial size
        setLocationRelativeTo(null);
        setResizable(true);

        Container contentPane = getContentPane();
        contentPane.setBackground(bgColor);
        contentPane.setLayout(new BorderLayout(20, 20)); // Keep BorderLayout, add gaps
        ((JPanel) contentPane).setBorder(new EmptyBorder(20, 20, 20, 20));


        // --- Top header (title, role selector, search) ---
        JPanel topBar = new RoundedPanel(12, cardColor, borderColor, 1);
        topBar.setLayout(new BorderLayout(20, 12));
        topBar.setBorder(new EmptyBorder(18, 24, 18, 24));
        topBar.setOpaque(false);

        JLabel title = new JLabel("Remove Users");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(textColor);
        title.setVerticalAlignment(SwingConstants.CENTER);

        // Left: title
        topBar.add(title, BorderLayout.WEST);

        // Center: role selector and search (custom styled dropdown)
        JPanel centerControls = new JPanel();
        centerControls.setOpaque(false);
        centerControls.setLayout(new FlowLayout(FlowLayout.LEFT, 18, 0));

        roleDropdown = new JComboBox<>(new String[]{"All", "Student", "Faculty", "Admin"});
        styleComboBox(roleDropdown); // Apply custom styling
        Dimension dropSize = new Dimension(300, 45); // Larger, fixed size
        roleDropdown.setPreferredSize(dropSize);
        roleDropdown.setMaximumSize(dropSize);

        centerControls.add(roleDropdown);

        // Search field
        JPanel searchWrapper = new RoundedPanel(10, sideMenuColor, borderColor, 1);
        searchWrapper.setLayout(new BoxLayout(searchWrapper, BoxLayout.Y_AXIS));
        searchWrapper.setBorder(new EmptyBorder(8, 12, 8, 12));
        searchWrapper.setMaximumSize(new Dimension(700, 60)); // Allow it to grow horizontally

        JLabel searchLabel = new JLabel("Search (name / id / dept / role)");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchLabel.setForeground(textSecondaryColor);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBackground(sideMenuColor);
        searchField.setForeground(textColor);
        searchField.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        searchField.setMaximumSize(new Dimension(700, 36));

        // --- MODIFIED --- Add this line to make the cursor visible
        searchField.setCaretColor(textColor); // Set caret (typing cursor) to white

        searchWrapper.add(searchLabel);
        searchWrapper.add(Box.createRigidArea(new Dimension(0, 6)));
        searchWrapper.add(searchField);

        centerControls.add(searchWrapper);

        topBar.add(centerControls, BorderLayout.CENTER);

        // Right: action buttons
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightActions.setOpaque(false);

        RoundedButton refreshBtn = new RoundedButton("Refresh", buttonColor, buttonColorGlow, 8);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        refreshBtn.setForeground(textColor);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.setBorder(new EmptyBorder(12, 20, 12, 20)); // Added padding

        RoundedButton closeBtn = new RoundedButton("Close", sideMenuColor, borderColor, buttonColor.darker(), 8);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeBtn.setForeground(textColor);
        closeBtn.setBorder(new EmptyBorder(12, 20, 12, 20)); // Added padding

        rightActions.add(refreshBtn);
        rightActions.add(closeBtn);

        topBar.add(rightActions, BorderLayout.EAST);

        contentPane.add(topBar, BorderLayout.NORTH); // Add to contentPane

        // --- Center: table card ---
        RoundedPanel tableCard = new RoundedPanel(14, cardColor, borderColor, 1);
        tableCard.setBorder(new EmptyBorder(22, 22, 22, 22));
        tableCard.setLayout(new BorderLayout(12, 12));

        // header inside card
        JLabel cardTitle = new JLabel("Search & Delete Users");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cardTitle.setForeground(textColor);
        tableCard.add(cardTitle, BorderLayout.NORTH);

        // Table
        String[] cols = new String[]{"", "Name", "ID", "Department", "Role", "Action"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 5;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;
                }
                return super.getColumnClass(column);
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(48);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(mainPanelColor);
        table.setForeground(textColor);
        table.getTableHeader().setBackground(cardColor);
        table.getTableHeader().setForeground(textColor);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setSelectionBackground(sideMenuColor);
        table.setSelectionForeground(textColor);

        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.setDefaultRenderer(Boolean.class, new CustomBooleanRenderer());
        table.setDefaultRenderer(Object.class, new CustomCellRenderer()); // For other cells


        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        // --- MODIFIED --- Add listeners for table button hover
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                int modelCol = (col >= 0) ? table.convertColumnIndexToModel(col) : -1;

                int newHoveredRow = -1;
                // Check if mouse is over the "Action" column (index 5)
                if (row >= 0 && modelCol == 5) {
                    newHoveredRow = table.convertRowIndexToModel(row);
                }

                if (newHoveredRow != hoveredRow) {
                    hoveredRow = newHoveredRow;
                    // Repaint table to update button appearance
                    table.repaint();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // When mouse leaves table, no row is hovered
                if (hoveredRow != -1) {
                    hoveredRow = -1;
                    table.repaint();
                }
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(mainPanelColor);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getVerticalScrollBar().setUI(new StyledScrollBarUI());

        tableCard.add(sp, BorderLayout.CENTER);

        contentPane.add(tableCard, BorderLayout.CENTER); // Add to contentPane

        // --- Bottom panel for buttons ---
        contentPane.add(createBottomPanel(), BorderLayout.SOUTH); // Add to contentPane

        // --- Populate hardcoded 50 entries ---
        populateHardcodedEntries();
        loadTableData();

        // --- Listeners ---
        roleDropdown.addActionListener(e -> loadTableData());

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadTableData();
            }
        });

        refreshBtn.addActionListener(e -> {
            // Clear selections and reload
            searchField.setText("");
            roleDropdown.setSelectedIndex(0);
            loadTableData();
        });

        closeBtn.addActionListener(e -> {
            dispose();
        });

        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize after showing
        });
    }

    /**
     * Creates the bottom panel with Delete Selected and Save buttons.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setOpaque(false);

        // "Delete Selected" Button
        // --- MODIFIED ---
        // Changed normal color from bgColor to dangerRed.
        // Changed hover color from dangerRed to dangerRedHover (brighter).
        RoundedButton deleteSelectedBtn = new RoundedButton("Delete Selected",
                dangerRed,        // Normal (red)
                dangerRedHover,   // Hover (brighter red)
                dangerRedPressed, // Pressed (dark red)
                8);
        deleteSelectedBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deleteSelectedBtn.setForeground(textColor);
        deleteSelectedBtn.setBorder(new EmptyBorder(12, 25, 12, 25));
        deleteSelectedBtn.addActionListener(e -> handleDeleteSelected());
        bottomPanel.add(deleteSelectedBtn);

        // "Save Changes" Button
        RoundedButton saveBtn = new RoundedButton("Save Changes", buttonColor, buttonColorGlow, 8);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveBtn.setForeground(textColor);
        saveBtn.setBorder(new EmptyBorder(12, 25, 12, 25));
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "All changes have been saved. (Mock Action)",
                    "Changes Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        bottomPanel.add(saveBtn);

        return bottomPanel;
    }

    /**
     * Logic to handle batch deletion.
     */
    private void handleDeleteSelected() {
        List<String> idsToDelete = new ArrayList<>();
        List<String> namesToDelete = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
            if (isSelected != null && isSelected) {
                idsToDelete.add((String) tableModel.getValueAt(i, 2));
                namesToDelete.add((String) tableModel.getValueAt(i, 1));
            }
        }

        if (idsToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No users selected. Please check the boxes to delete.",
                    "No Selection",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String userList = String.join("\n- ", namesToDelete);
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete these " + idsToDelete.size() + " users?\n\n- " + userList,
                "Confirm Batch Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            allRows.removeIf(userRow -> idsToDelete.contains(userRow.id));
            loadTableData();
            System.out.println("Batch deleted " + idsToDelete.size() + " users.");
        }
    }


    private void populateHardcodedEntries() {
        String[] roles = {"Student", "Faculty", "Admin"};
        String[] depts = {"CSE", "ECE", "Math", "Physics", "Economics", "Design", "Biology", "HCD"};
        String[] first = {"Alex", "Maya", "Rohan", "Priya", "Kiran", "Aisha", "Vikram", "Sara", "Omar", "Nina", "John", "Jane", "Luca", "Meera", "Arjun", "Zara", "Noah", "Lina", "Ishaan", "Rekha"};
        String[] last = {"Sharma", "Singh", "Patel", "Gupta", "Kumar", "Verma", "Bose", "Roy", "Das", "Mehta"};

        int id = 1001;
        for (int i = 0; i < 50; ++i) {
            String name = first[i % first.length] + " " + last[i % last.length];
            String role = roles[i % roles.length];
            String dept = depts[i % depts.length];
            String uid = (role.equals("Student") ? "S" : role.equals("Faculty") ? "F" : "A") + id++;
            allRows.add(new UserRow(name, uid, dept, role));
        }
    }

    private void loadTableData() {
        String roleSel = (String) roleDropdown.getSelectedItem();
        String q = searchField.getText().trim().toLowerCase();

        tableModel.setRowCount(0);

        for (UserRow r : allRows) {
            if (!"All".equals(roleSel) && !r.role.equalsIgnoreCase(roleSel)) continue;
            if (!q.isEmpty()) {
                boolean matches = r.name.toLowerCase().contains(q) || r.id.toLowerCase().contains(q)
                        || r.department.toLowerCase().contains(q) || r.role.toLowerCase().contains(q);
                if (!matches) continue;
            }
            tableModel.addRow(new Object[]{Boolean.FALSE, r.name, r.id, r.department, r.role, "Delete"});
        }
    }

    private static class UserRow {
        String name, id, department, role;

        UserRow(String name, String id, String department, String role) {
            this.name = name;
            this.id = id;
            this.department = department;
            this.role = role;
        }
    }

    // ---
    // --- STYLING INNER CLASSES ---
    // ---

    /**
     * Custom renderer for all Object cells in the table to apply alternating row colors.
     */
    private class CustomCellRenderer extends DefaultTableCellRenderer {
        public CustomCellRenderer() {
            setOpaque(true); // Must be true for background to show
            setForeground(textColor); // Default text color
            // --- MODIFIED --- Changed alignment from LEFT to CENTER
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(0, 15, 0, 15)); // Add padding
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(mainPanelColor);
            }
            return this;
        }
    }

    /**
     * Custom renderer for the Boolean checkbox column.
     * Uses the icons from StudentRegCourses and **does not alternate background color**.
     */
    private class CustomBooleanRenderer extends JLabel implements TableCellRenderer {
        public CustomBooleanRenderer() {
            super();
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Set background based on selection, otherwise use table's default background for this column
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                // Keep the background consistent for the checkbox column, matching the table's general background
                setBackground(mainPanelColor); // Use mainPanelColor as a consistent background
            }

            if (value instanceof Boolean && (Boolean) value) {
                setIcon(checkedIcon);
            } else {
                setIcon(uncheckedIcon);
            }
            return this;
        }
    }


    // --- Button Renderer and Editor for table action column ---

    // --- MODIFIED ---
    /**
     * Renders the button in the table.
     * Normal state is now bgColor (dark grey).
     * Selected/Hover state is dangerRed.
     * Now checks the 'hoveredRow' variable set by the table's MouseMotionListener.
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(textColor);
            setBackground(bgColor); // Set NORMAL state to bgColor
            setBorder(new EmptyBorder(10, 15, 10, 15));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());

            // --- MODIFIED ---
            // Convert view row index to model row index to compare with hoveredRow
            int modelRow = table.convertRowIndexToModel(row);

            // Show hover color if:
            // 1. The mouse is hovering over this specific row's button (hoveredRow)
            // 2. The cell is selected (e.g., keyboard nav)
            // 3. The cell has focus
            if (modelRow == hoveredRow || isSelected || hasFocus) {
                setBackground(dangerRed); // Use dangerRed for selection/hover
            } else {
                setBackground(bgColor); // Use bgColor for normal
            }
            return this;
        }
    }

    // --- MODIFIED ---
    /**
     * Editor for the button (handles the click and hover/press effects).
     * Matches the AdminDashboard logout button behavior.
     */
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int editingRow = -1;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setForeground(textColor);
            button.setBackground(bgColor); // NORMAL state
            button.setBorder(new EmptyBorder(10, 15, 10, 15));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Add MouseListener for hover/press effects
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // --- MODIFIED --- Use brighter hover color
                    button.setBackground(dangerRedHover); // HOVER state
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(bgColor); // Back to NORMAL
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    button.setBackground(dangerRedPressed); // PRESSED state
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // When released, go back to hover color (if still inside)
                    if (button.contains(e.getPoint())) {
                        button.setBackground(dangerRedHover);
                    } else {
                        button.setBackground(bgColor);
                    }
                }
            });

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    // --- MODIFIED ---
                    // Check if editingRow is valid before accessing table model
                    if (editingRow >= 0 && editingRow < table.getRowCount()) {
                        int modelRow = table.convertRowIndexToModel(editingRow);
                        String name = (String) tableModel.getValueAt(modelRow, 1);
                        String id = (String) tableModel.getValueAt(modelRow, 2);

                        int confirm = JOptionPane.showConfirmDialog(RemoveUser.this,
                                "Delete user " + name + " (" + id + ")?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (confirm == JOptionPane.YES_OPTION) {
                            allRows.removeIf(r -> r.id.equals(id));
                            loadTableData();
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            // When editor is activated, mouse is already over it, so set to hover
            // --- MODIFIED --- Use brighter hover color
            button.setBackground(dangerRedHover);
            isPushed = true;
            editingRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    // --- Styled scrollbar copied from dashboards ---
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = buttonColor;
            this.trackColor = cardColor;
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

    // ---
    // --- METHODS COPIED FROM StudentRegCourses FOR STYLING ---
    // ---

    /**
     * Programmatically draws an icon for our custom checkbox.
     * (Copied from StudentRegCourses)
     */
    private ImageIcon createCheckBoxIcon(boolean isChecked) {
        int width = 24;
        int height = 24;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Draw the Box ---
        g2.setColor(textSecondaryColor);
        g2.setStroke(new BasicStroke(2));
        g2.draw(new RoundRectangle2D.Float(1, 1, width - 3, height - 3, 8, 8));

        if (isChecked) {
            // --- Fill the Box ---
            g2.setColor(buttonColor);
            g2.fill(new RoundRectangle2D.Float(1, 1, width - 3, height - 3, 8, 8));

            // --- Draw the Checkmark ---
            g2.setColor(textColor);
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(7, 12, 11, 17); // Left part of V
            g2.drawLine(11, 17, 18, 8); // Right part of V
        }

        g2.dispose();
        return new ImageIcon(image);
    }

    /**
     * Applies modern styling to a JComboBox.
     * (Copied from StudentRegCourses)
     */
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.BOLD, 16));
        comboBox.setForeground(textColor);
        comboBox.setBackground(cardColor);
        comboBox.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        comboBox.setFocusable(false);

        // --- Custom Renderer ---
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value.toString());
                setBackground(isSelected ? buttonColor : cardColor);
                setForeground(isSelected ? textColor : textSecondaryColor);
                setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                return this;
            }
        });

        // --- Custom UI (to style arrow) ---
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                RoundedButton arrowButton = new RoundedButton("▼",
                        buttonColor, buttonColor.brighter(), buttonColor.darker(), 8);
                arrowButton.setForeground(textColor);
                arrowButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
                arrowButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return arrowButton;
            }

            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2.setColor(cardColor);
                g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

                String text = (String) comboBox.getSelectedItem();
                FontMetrics fm = g2.getFontMetrics();

                g2.setColor(textColor);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.drawString(text, bounds.x + 15, bounds.y + fm.getAscent() + (bounds.height - fm.getHeight()) / 2);

                g2.dispose();
            }
        });
    }

}