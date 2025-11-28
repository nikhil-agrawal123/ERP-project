package ui.AdminFrame;

import ui.dashboard.AdminDashboard;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import middleware.adminService;
import dbClasses.CourseDTO;
import dbClasses.AddCourse; // Matches your Backend/Service

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class AdminManageCourses extends JFrame {

    private String adminID;
    private String username;

    // --- Service ---
    private adminService adminService;
    private AdminDashboard dashboardInstance;

    // --- UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);
    private Color dangerColor = new Color(220, 80, 80);

    // Manage Button Colors
    private Color manageButtonColor = new Color(66, 133, 244);
    private Color manageButtonColorGlow = new Color(100, 160, 155);

    // --- Components ---
    private DefaultTableModel tableModel;
    private JTable courseTable;
    private JTextField searchField;
    private int hoveredRow = -1;
    private List<CourseDTO> courseList;

    public AdminManageCourses(String adminID, String username, AdminDashboard dashboardInstance) {
        super("Manage Courses - Admin");
        this.adminID = adminID;
        this.username = username;
        this.dashboardInstance = dashboardInstance;

        // Initialize Service
        this.adminService = new adminService();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);

        JPanel mainContainer = new JPanel(new BorderLayout(0, 20));
        mainContainer.setBackground(mainPanelColor);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        add(mainContainer);

        // --- 1. Header Section ---
        mainContainer.add(createHeader(), BorderLayout.NORTH);

        // --- 2. Content Section (Table) ---
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);

        // Table Panel
        RoundedPanel tableCard = new RoundedPanel(15, cardColor, cardColor, 0);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createTable(); // Initialize Table
        JScrollPane scrollPane = createScrollPane(courseTable);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(tableCard, BorderLayout.CENTER);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        // --- Load Real Data ---
        loadData("");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Left: Back Button & Title
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeader.setOpaque(false);

        RoundedButton backBtn = new RoundedButton(
                "← Dashboard", Buttonback, Buttonhover, borderColor.darker(), 8
        );
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setForeground(textColor);
        backBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backBtn.setPreferredSize(new Dimension(150, 45));

        backBtn.addActionListener(e -> {
            if (dashboardInstance != null) dashboardInstance.setVisible(true);
            dispose();
        });

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);

        JLabel titleLabel = new JLabel("Course Catalog");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textColor);

        JLabel subtitleLabel = new JLabel("View and manage the master list of courses.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(textSecondaryColor);

        titleGroup.add(titleLabel);
        titleGroup.add(Box.createRigidArea(new Dimension(0, 5)));
        titleGroup.add(subtitleLabel);

        leftHeader.add(backBtn);
        leftHeader.add(Box.createRigidArea(new Dimension(20, 0)));
        leftHeader.add(titleGroup);

        // Right: Search + Add
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);

        searchField = new JTextField(15);
        styleTextField(searchField);
        searchField.putClientProperty("JTextField.placeholderText", "Search Course...");

        RoundedButton searchBtn = new RoundedButton("Search", borderColor, borderColor.brighter(), 10);
        searchBtn.setForeground(textColor);
        searchBtn.setPreferredSize(new Dimension(90, 40));
        searchBtn.addActionListener(e -> loadData(searchField.getText()));

        rightHeader.add(searchField);
        rightHeader.add(searchBtn);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);

        return header;
    }

    private void createTable() {
        // Columns for CATALOG
        String[] columns = {"Code", "Course Name", "Department", "Credits","Offered By", "Semester", "Current Cap", "Action"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Action column
            }
        };

        courseTable = new JTable(tableModel);
        styleTable(courseTable);

        // Hover Logic
        courseTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = courseTable.rowAtPoint(e.getPoint());
                int col = courseTable.columnAtPoint(e.getPoint());
                int modelCol = (col >= 0) ? courseTable.convertColumnIndexToModel(col) : -1;

                int newHoveredRow = -1;
                if (row >= 0 && modelCol == 6) {
                    newHoveredRow = courseTable.convertRowIndexToModel(row);
                }

                if (newHoveredRow != hoveredRow) {
                    hoveredRow = newHoveredRow;
                    courseTable.repaint();
                }
            }
        });

        courseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                courseTable.repaint();
            }
        });

        // Button Renderer/Editor
        courseTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        courseTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Widths
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Code
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(400); // Name
        courseTable.getColumnModel().getColumn(7).setPreferredWidth(140); // Action
    }

    private void loadData(String query) {
        tableModel.setRowCount(0);

        if (query == null || query.isEmpty()) {
            courseList = adminService.getCourseCatalog();
        } else {
            courseList = adminService.searchCourses(query);
        }

        for (CourseDTO c : courseList) {
            tableModel.addRow(new Object[]{
                    c.getCourseCode(),
                    c.getCourseName(),
                    c.getDepartment(),
                    c.getCredits(),
                    c.getInstructorId(),
                    c.getSemester(),
                    c.getCapacity(),
                    "Manage"
            });
        }
    }

    // --- MANAGE DIALOG ---
    private void showManageDialog(CourseDTO course) {
        JDialog d = new JDialog(this, "Manage: " + course.getCourseCode(), true);
        d.setSize(600, 900); // Increased height to fit new fields
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(mainPanelColor);
        d.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // Title
        JLabel headerLabel = new JLabel("Course Details");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(textColor);
        d.add(headerLabel, gbc);

        JLabel codeLabel = new JLabel(course.getCourseCode());
        codeLabel.setFont(new Font("Segoe UI Mono", Font.PLAIN, 16));
        codeLabel.setForeground(buttonColorGlow);
        gbc.gridy++;
        d.add(codeLabel, gbc);

        // --- Editable Fields ---
        JTextField nameField = addField(d, "Course Name:", gbc);
        nameField.setText(course.getCourseName());

        JTextField deptField = addField(d, "Department:", gbc);
        deptField.setText(course.getDepartment());

        JTextField credField = addField(d, "Credits:", gbc);
        credField.setText(String.valueOf(course.getCredits()));

        JTextField instField = addField(d, "Instructor ID:", gbc);
        instField.setText(course.getInstructorId());

        JTextField capField = addField(d, "Capacity:", gbc);
        capField.setText(String.valueOf(course.getCapacity()));

        // --- Read-Only Fields (Info) ---
        JTextField semField = addField(d, "Semester:", gbc);
        semField.setText(course.getSemester());
        semField.setEditable(false);
        semField.setForeground(textSecondaryColor);

        JTextField enrollField = addField(d, "Enrolled Students:", gbc);
        enrollField.setText(String.valueOf(course.getEnrolled()));
        enrollField.setEditable(false);
        enrollField.setForeground(textSecondaryColor);

        // Update Button
        gbc.gridy++;
        gbc.insets = new Insets(25, 15, 5, 15);
        RoundedButton upBtn = new RoundedButton("Update Course", buttonColor, buttonColorGlow, 10);
        upBtn.setForeground(Color.WHITE);
        upBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        upBtn.setPreferredSize(new Dimension(0, 40));

        upBtn.addActionListener(e -> {
            try {
                boolean success = adminService.updateCourseOffering(
                        course.getId(),
                        course.getCourseCode(),
                        instField.getText(),
                        Integer.parseInt(capField.getText()),
                        Integer.parseInt(credField.getText())
                );

                if(success) {
                    JOptionPane.showMessageDialog(d, "Course Updated Successfully!");
                    d.dispose();
                    loadData(""); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(d, "Update Failed.", "Some data is faulty", JOptionPane.ERROR_MESSAGE);
                }
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "Credits and Capacity must be numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        d.add(upBtn, gbc);

        // Delete Button
        gbc.gridy++;
        gbc.insets = new Insets(10, 15, 15, 15);
        RoundedButton delBtn = new RoundedButton("Delete Course Offering", dangerColor, new Color(240, 100, 100), 10);
        delBtn.setForeground(Color.WHITE);
        delBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        delBtn.setPreferredSize(new Dimension(0, 40));

        delBtn.addActionListener(e -> {
            // Check enrollment count before deleting
            if(course.getEnrolled() > 0){
                JOptionPane.showMessageDialog(d,
                        "Cannot delete course.\nThere are " + course.getEnrolled() + " students enrolled.",
                        "Delete Restriction", JOptionPane.WARNING_MESSAGE);
            } else {
                int confirm = JOptionPane.showConfirmDialog(d,
                        "Delete offering for " + course.getCourseCode() + "?\nThis cannot be undone.",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if(confirm == JOptionPane.YES_OPTION) {
                    boolean success = adminService.deleteCourseOffering(course.getId());
                    if (success) {
                        JOptionPane.showMessageDialog(d, "Course Offering Deleted.");
                        d.dispose();
                        loadData(""); // Refresh table
                    } else {
                        JOptionPane.showMessageDialog(d, "Delete Failed.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        d.add(delBtn, gbc);

        d.setVisible(true);
    }

    // --- HELPERS (Styling) ---
    private JTextField addField(JDialog d, String lbl, GridBagConstraints gbc) {
        gbc.gridy++;
        JLabel l = new JLabel(lbl); l.setForeground(textSecondaryColor); d.add(l, gbc);
        gbc.gridy++;
        JTextField f = new JTextField(15); styleTextField(f); d.add(f, gbc);
        return f;
    }
    private void styleTextField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(cardColor); f.setForeground(textColor); f.setCaretColor(textColor);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor), BorderFactory.createEmptyBorder(5,10,5,10)));
    }
    private void styleTable(JTable t) {
        t.setBackground(cardColor); t.setForeground(textColor); t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setRowHeight(55); t.setGridColor(borderColor); t.setFillsViewportHeight(true);
        t.setSelectionBackground(Buttonhover); t.setSelectionForeground(textColor);
        t.getTableHeader().setDefaultRenderer(new LeftAlignedHeaderRenderer());
        t.getTableHeader().setPreferredSize(new Dimension(0, 50));
        t.getTableHeader().setReorderingAllowed(false);
        LeftAlignedCellRenderer textRenderer = new LeftAlignedCellRenderer();
        for (int i = 0; i < t.getColumnCount() - 1; i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(textRenderer);
        }
    }
    private JScrollPane createScrollPane(Component v) {
        JScrollPane s = new JScrollPane(v);
        s.setBorder(BorderFactory.createEmptyBorder()); s.getViewport().setBackground(cardColor);
        s.setOpaque(false); s.getViewport().setOpaque(false);
        s.getVerticalScrollBar().setUnitIncrement(16);
        s.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        return s;
    }
    private JButton createZeroButton() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b; }

    private void styleComboBox(JComboBox<String> box) {
        box.setFont(new Font("Segoe UI", Font.BOLD, 14));
        box.setForeground(textColor);
        box.setBackground(cardColor);
        box.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        box.setUI(new BasicComboBoxUI() {
            protected JButton createArrowButton() {
                JButton b = new JButton("▼");
                b.setBackground(cardColor); b.setForeground(textSecondaryColor); b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }
        });
    }

    // --- Inner Classes ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(false); setFont(new Font("Segoe UI", Font.BOLD, 12)); setForeground(Color.BLUE); setBorder(new EmptyBorder(5,10,5,10)); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { setText("Manage"); return this; }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hoveredRow == ((JTable)SwingUtilities.getAncestorOfClass(JTable.class, this)).convertRowIndexToModel(((JTable)SwingUtilities.getAncestorOfClass(JTable.class, this)).getEditingRow() == -1 ? 0 : ((JTable)SwingUtilities.getAncestorOfClass(JTable.class, this)).getEditingRow()) ? manageButtonColorGlow : manageButtonColor);
            g2.setColor(manageButtonColor);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8); super.paintComponent(g); g2.dispose();
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton b;
        private int r;
        public ButtonEditor(JCheckBox c) {
            super(c);
            b = new JButton();
            b.setOpaque(false);
            b.addActionListener(e -> fireEditingStopped());
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int row, int col) {
            r = row;
            b.setText("Manage");
            return b;
        }
        public Object getCellEditorValue() {
            // FIX: Robust row checking
            if (courseList != null && r >= 0 && r < courseList.size()) {
                // Ensure the table is not editing before showing dialog to prevent focus issues
                SwingUtilities.invokeLater(() -> {
                    CourseDTO course = courseList.get(r);
                    showManageDialog(course);
                });
            }
            return "Manage";
        }
    }

    class LeftAlignedHeaderRenderer extends DefaultTableCellRenderer {
        public LeftAlignedHeaderRenderer() { setHorizontalAlignment(JLabel.LEFT); setBackground(cardColor); setForeground(textSecondaryColor); setFont(new Font("Segoe UI", Font.BOLD, 14)); setBorder(BorderFactory.createEmptyBorder(0,10,0,10)); }
    }
    class LeftAlignedCellRenderer extends DefaultTableCellRenderer {
        public LeftAlignedCellRenderer() { setHorizontalAlignment(JLabel.LEFT); setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            Component cmp = super.getTableCellRendererComponent(t, v, s, f, r, c);
            cmp.setBackground(s ? Buttonhover : cardColor); cmp.setForeground(textColor); return cmp;
        }
    }
    class StyledScrollBarUI extends BasicScrollBarUI {
        protected void configureScrollBarColors() { this.thumbColor = buttonColor; this.trackColor = cardColor; }
        protected JButton createDecreaseButton(int o) { return createZeroButton(); }
        protected JButton createIncreaseButton(int o) { return createZeroButton(); }
    }
}