package ui.FacultyFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import middleware.gradingService;

public class GradingPolicyFrame extends JFrame {

    // --- UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color dangerColor = new Color(220, 80, 80);
    private Color dangerHoverColor = new Color(240, 100, 100);
    private Color textColor = Color.WHITE;
    private Color textSecondaryColor = new Color(179, 179, 179);

    // Fields
    private String courseCode;
    private String courseName;
    private String instructorId;
    private String semester;

    private List<GradingComponent> policyComponents;
    private gradingService gradingService;

    private CardLayout cardLayout;
    private JPanel mainCardPanel;

    private JList<GradingComponent> viewList;
    private DefaultListModel<GradingComponent> viewListModel;
    private JLabel totalLabelView;

    private JList<GradingComponent> editList;
    private DefaultListModel<GradingComponent> editListModel;
    private JLabel totalLabelEdit;

    public GradingPolicyFrame(String courseCode, String courseName, String instructorId, String semester) {
        super("Grading Policy - " + courseName);

        this.courseCode = courseCode;
        this.courseName = courseName;
        this.instructorId = instructorId;
        this.semester = semester;

        this.gradingService = new gradingService();
        this.policyComponents = gradingService.getPolicy(courseCode, instructorId, semester);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(0, 0));

        // --- Title ---
        JLabel title = new JLabel("Grading Policy: " + courseName + " (" + semester + ")", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor);
        title.setOpaque(false);
        title.setBorder(BorderFactory.createEmptyBorder(20, 40, 25, 40));
        add(title, BorderLayout.NORTH);

        // --- Main Card Panel ---
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setOpaque(false);
        mainCardPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));
        mainCardPanel.add(createViewPanel(), "VIEW");
        mainCardPanel.add(createEditPanel(), "EDIT");

        add(mainCardPanel, BorderLayout.CENTER);

        cardLayout.show(mainCardPanel, "VIEW");
    }

    private JPanel createViewPanel() {
        RoundedPanel panel = new RoundedPanel(15, cardColor, cardColor, 0);
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        viewListModel = new DefaultListModel<>();
        viewList = new JList<>(viewListModel);
        viewList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        viewList.setBackground(cardColor);
        viewList.setCellRenderer(new GradingComponentRenderer());
        viewList.setFixedCellHeight(60);

        viewList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) { /* no-op */ }
            @Override
            public void addSelectionInterval(int index0, int index1) { /* no-op */ }
        });
        viewList.setFocusable(false);

        JScrollPane scrollPane = new JScrollPane(viewList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardColor);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

        RoundedPanel listContainer = new RoundedPanel(10, borderColor, borderColor, 1);
        listContainer.setLayout(new BorderLayout());
        listContainer.add(createHeaderPanel(), BorderLayout.NORTH);
        listContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 0, 5));

        totalLabelView = new JLabel();
        totalLabelView.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bottomPanel.add(totalLabelView, BorderLayout.WEST);

        RoundedButton editButton = new RoundedButton(
                "Edit Policy",
                buttonColor,
                buttonColorGlow,
                10
        );
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        editButton.setForeground(Color.WHITE);
        editButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        editButton.addActionListener(e -> {
            loadDataIntoEditList();
            cardLayout.show(mainCardPanel, "EDIT");
        });

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(editButton);
        bottomPanel.add(buttonWrapper, BorderLayout.EAST);

        panel.add(listContainer, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadDataIntoViewList();
        return panel;
    }

    private JPanel createEditPanel() {
        RoundedPanel panel = new RoundedPanel(15, cardColor, cardColor, 0);
        panel.setLayout(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        editListModel = new DefaultListModel<>();
        editList = new JList<>(editListModel);
        editList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        editList.setBackground(cardColor);
        editList.setForeground(textColor);
        editList.setSelectionBackground(buttonColorGlow);
        editList.setSelectionForeground(Color.WHITE);
        editList.setCellRenderer(new GradingComponentRenderer());
        editList.setFixedCellHeight(60);

        JScrollPane scrollPane = new JScrollPane(editList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardColor);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new CustomScrollBarUI());

        RoundedPanel listContainer = new RoundedPanel(10, borderColor, borderColor, 1);
        listContainer.setLayout(new BorderLayout());
        listContainer.add(createHeaderPanel(), BorderLayout.NORTH);
        listContainer.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel (Save/Cancel) ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 0, 5));

        totalLabelEdit = new JLabel();
        totalLabelEdit.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bottomPanel.add(totalLabelEdit, BorderLayout.WEST);

        JPanel saveCancelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        saveCancelPanel.setOpaque(false);

        RoundedButton saveButton = new RoundedButton("Save Changes", buttonColor, buttonColorGlow, 10);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        saveButton.addActionListener(e -> savePolicy());

        RoundedButton cancelButton = new RoundedButton("Cancel", borderColor, borderColor.brighter(), 10);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        cancelButton.addActionListener(e -> cardLayout.show(mainCardPanel, "VIEW"));

        saveCancelPanel.add(saveButton);
        saveCancelPanel.add(cancelButton);
        bottomPanel.add(saveCancelPanel, BorderLayout.EAST);

        // --- Top Controls Panel ---
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlsPanel.setOpaque(false);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Standard Action Buttons
        RoundedButton addButton = new RoundedButton("Add", buttonColor, buttonColorGlow, 10);
        styleControlButton(addButton);
        addButton.addActionListener(e -> addComponent());

        RoundedButton editButton = new RoundedButton("Edit", borderColor, borderColor.brighter(), 10);
        styleControlButton(editButton);
        editButton.addActionListener(e -> editComponent());

        RoundedButton removeButton = new RoundedButton("Remove", dangerColor, dangerHoverColor, 10);
        styleControlButton(removeButton);
        removeButton.addActionListener(e -> removeComponent());

        // --- NEW: Import/Export Buttons ---
        // We use a separator color or just the border color for "Tools"
        RoundedButton importButton = new RoundedButton("Import CSV", borderColor, borderColor.brighter(), 10);
        styleControlButton(importButton);
        importButton.addActionListener(e -> importPolicyFromCsv());

        RoundedButton exportButton = new RoundedButton("Export CSV", borderColor, borderColor.brighter(), 10);
        styleControlButton(exportButton);
        exportButton.addActionListener(e -> exportPolicyToCsv());

        controlsPanel.add(addButton);
        controlsPanel.add(editButton);
        controlsPanel.add(removeButton);
        controlsPanel.add(Box.createHorizontalStrut(20)); // Spacer
        controlsPanel.add(importButton);
        controlsPanel.add(exportButton);

        panel.add(controlsPanel, BorderLayout.NORTH);
        panel.add(listContainer, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Helper to style the small control buttons consistently
    private void styleControlButton(RoundedButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    // --- Data Loading Methods ---

    private void loadDataIntoViewList() {
        viewListModel.clear();
        for (GradingComponent comp : policyComponents) {
            viewListModel.addElement(comp);
        }
        updateTotalLabel(totalLabelView);
    }

    private void loadDataIntoEditList() {
        editListModel.clear();
        for (GradingComponent comp : policyComponents) {
            editListModel.addElement(new GradingComponent(comp.getName(), comp.getPercentage()));
        }
        updateTotalLabel(totalLabelEdit);
    }

    // --- CSV Import/Export Methods ---

    private void importPolicyFromCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Grading Policy (CSV)");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                List<GradingComponent> importedComponents = new ArrayList<>();

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String name = parts[0].trim();
                        String percentStr = parts[1].trim();

                        // Skip Header if present
                        if (name.equalsIgnoreCase("Component Name") || name.equalsIgnoreCase("Name")) {
                            continue;
                        }

                        try {
                            int percent = Integer.parseInt(percentStr);
                            importedComponents.add(new GradingComponent(name, percent));
                        } catch (NumberFormatException ex) {
                            System.err.println("Skipping invalid line: " + line);
                        }
                    }
                }

                if (!importedComponents.isEmpty()) {
                    editListModel.clear();
                    for (GradingComponent c : importedComponents) {
                        editListModel.addElement(c);
                    }
                    updateTotalLabel(totalLabelEdit);
                    JOptionPane.showMessageDialog(this, "Policy imported successfully!", "Import Complete", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No valid data found in the CSV file.", "Import Error", JOptionPane.WARNING_MESSAGE);
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportPolicyToCsv() {
        if (editListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The policy list is empty. Nothing to export.", "Export Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Grading Policy");
        fileChooser.setSelectedFile(new File(courseCode + "_grading_policy.csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // Ensure .csv extension
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getParentFile(), file.getName() + ".csv");
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write Header
                writer.println("Component Name,Percentage");

                // Write Data
                for (int i = 0; i < editListModel.size(); i++) {
                    GradingComponent comp = editListModel.getElementAt(i);
                    writer.println(comp.getName() + "," + comp.getPercentage());
                }

                JOptionPane.showMessageDialog(this, "Policy exported successfully!", "Export Complete", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error writing file: " + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- CRUD Methods ---

    private void addComponent() {
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner percentSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        percentSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel dialogPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        dialogPanel.add(new JLabel("Component Name:"));
        dialogPanel.add(nameField);
        dialogPanel.add(new JLabel("Percentage (%):"));
        dialogPanel.add(percentSpinner);

        int result = JOptionPane.showConfirmDialog(
                this, dialogPanel, "Add New Component", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            int percent = (Integer) percentSpinner.getValue();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            editListModel.addElement(new GradingComponent(name, percent));
            updateTotalLabel(totalLabelEdit);
        }
    }

    private void editComponent() {
        GradingComponent selected = editList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a component to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField(selected.getName());
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JSpinner percentSpinner = new JSpinner(new SpinnerNumberModel(selected.getPercentage(), 0, 100, 1));
        percentSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel dialogPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        dialogPanel.add(new JLabel("Component Name:"));
        dialogPanel.add(nameField);
        dialogPanel.add(new JLabel("Percentage (%):"));
        dialogPanel.add(percentSpinner);

        int result = JOptionPane.showConfirmDialog(
                this, dialogPanel, "Edit Component", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            int percent = (Integer) percentSpinner.getValue();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            selected.setName(name);
            selected.setPercentage(percent);
            editList.repaint();
            updateTotalLabel(totalLabelEdit);
        }
    }

    private void removeComponent() {
        GradingComponent selected = editList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a component to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove '" + selected.getName() + "'?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            editListModel.removeElement(selected);
            updateTotalLabel(totalLabelEdit);
        }
    }

    private void savePolicy() {
        int total = getTotalFromEditList();
        if (total != 100) {
            JOptionPane.showMessageDialog(
                    this, "Total percentage must be 100%. Current total is " + total + "%.", "Save Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        List<GradingComponent> newPolicy = new ArrayList<>();
        for (int i = 0; i < editListModel.getSize(); i++) {
            newPolicy.add(editListModel.getElementAt(i));
        }
        this.policyComponents = newPolicy;

        boolean success = gradingService.savePolicy(courseCode, courseName, instructorId, semester, this.policyComponents);

        if (success) {
            loadDataIntoViewList();
            cardLayout.show(mainCardPanel, "VIEW");
            JOptionPane.showMessageDialog(this, "Policy saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save policy to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getTotalFromEditList() {
        int total = 0;
        for (int i = 0; i < editListModel.getSize(); i++) {
            total += editListModel.getElementAt(i).getPercentage();
        }
        return total;
    }

    private int getTotalFromPolicyList() {
        int total = 0;
        for (GradingComponent comp : this.policyComponents) {
            total += comp.getPercentage();
        }
        return total;
    }

    private void updateTotalLabel(JLabel label) {
        int total;
        if (label == totalLabelView) {
            total = getTotalFromPolicyList();
        } else {
            total = getTotalFromEditList();
        }
        label.setText("TOTAL: " + total + "%");
        if (total == 100) {
            label.setForeground(buttonColorGlow);
        } else {
            label.setForeground(dangerColor);
        }
    }

    class GradingComponentRenderer extends JPanel implements ListCellRenderer<GradingComponent> {
        private JLabel nameLabel;
        private JLabel percentLabel;
        private JPanel contentPanel;

        public GradingComponentRenderer() {
            setLayout(new BorderLayout());
            setOpaque(true);

            contentPanel = new JPanel(new BorderLayout(10, 0));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

            nameLabel = new JLabel();
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));

            percentLabel = new JLabel();
            percentLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));

            contentPanel.add(nameLabel, BorderLayout.WEST);
            contentPanel.add(percentLabel, BorderLayout.EAST);
            add(contentPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends GradingComponent> list,
                                                      GradingComponent value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            nameLabel.setText(value.getName());
            percentLabel.setText(value.getPercentage() + "%");
            setBackground(cardColor);

            if (isSelected) {
                contentPanel.setBackground(list.getSelectionBackground());
                nameLabel.setForeground(list.getSelectionForeground());
                percentLabel.setForeground(list.getSelectionForeground().brighter());
            } else {
                contentPanel.setBackground(cardColor);
                nameLabel.setForeground(textColor);
                percentLabel.setForeground(textSecondaryColor);
            }
            return this;
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(cardColor);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, borderColor),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JLabel nameHeader = new JLabel("Component Name");
        nameHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameHeader.setForeground(textSecondaryColor);

        JLabel percentHeader = new JLabel("Percentage");
        percentHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        percentHeader.setForeground(textSecondaryColor);
        percentHeader.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(nameHeader, BorderLayout.WEST);
        header.add(percentHeader, BorderLayout.EAST);

        return header;
    }

    class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = borderColor;
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
        } catch (Exception ignored) {}
    }
}