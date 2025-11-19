package ui.FacultyFrame;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import middleware.gradingService;

public class GradingPolicyFrame extends JFrame {

    // --- NEW: Updated UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color dangerColor = new Color(220, 80, 80);
    private Color dangerHoverColor = new Color(240, 100, 100);
    private Color textColor = Color.WHITE;
    private Color textSecondaryColor = new Color(179, 179, 179);
    // --- End of Palette ---

    // Fields to store context
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

    /**
     * Updated Constructor to take explicit details instead of sectionId.
     * @param courseCode   e.g., "CS101"
     * @param courseName   e.g., "Intro to Programming"
     * @param instructorId e.g., "emp101"
     * @param semester     e.g., "Fall 2025"
     */
    public GradingPolicyFrame(String courseCode, String courseName, String instructorId, String semester) {
        super("Grading Policy - " + courseName);

        this.courseCode = courseCode;
        this.courseName = courseName;
        this.instructorId = instructorId;
        this.semester = semester;

        this.gradingService = new gradingService();

        // Load data using the new service method
        this.policyComponents = gradingService.getPolicy(courseCode, instructorId, semester);

        // --- Frame Setup ---
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

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 0, 5));

        totalLabelEdit = new JLabel();
        totalLabelEdit.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bottomPanel.add(totalLabelEdit, BorderLayout.WEST);

        JPanel saveCancelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        saveCancelPanel.setOpaque(false);

        RoundedButton saveButton = new RoundedButton(
                "Save Changes",
                buttonColor,
                buttonColorGlow,
                10
        );
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        saveButton.addActionListener(e -> savePolicy());
        saveCancelPanel.add(saveButton);

        RoundedButton cancelButton = new RoundedButton(
                "Cancel",
                borderColor,
                borderColor.brighter(),
                10
        );
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        cancelButton.addActionListener(e -> cardLayout.show(mainCardPanel, "VIEW"));
        saveCancelPanel.add(cancelButton);

        bottomPanel.add(saveCancelPanel, BorderLayout.EAST);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlsPanel.setOpaque(false);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        RoundedButton addButton = new RoundedButton(
                "Add Component",
                buttonColor,
                buttonColorGlow,
                10
        );
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setForeground(Color.WHITE);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addButton.addActionListener(e -> addComponent());
        controlsPanel.add(addButton);

        RoundedButton editButton = new RoundedButton(
                "Edit Selected",
                borderColor,
                borderColor.brighter(),
                10
        );
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editButton.setForeground(Color.WHITE);
        editButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        editButton.addActionListener(e -> editComponent());
        controlsPanel.add(editButton);

        RoundedButton removeButton = new RoundedButton(
                "Remove Selected",
                dangerColor,
                dangerHoverColor,
                10
        );
        removeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        removeButton.setForeground(Color.WHITE);
        removeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        removeButton.addActionListener(e -> removeComponent());
        controlsPanel.add(removeButton);

        panel.add(controlsPanel, BorderLayout.NORTH);
        panel.add(listContainer, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

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

        // --- SAVE TO DATABASE VIA SERVICE (UPDATED) ---
        // Now passes the direct parameters
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


    // Main for testing
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            // Updated sample usage for testing
            GradingPolicyFrame frame = new GradingPolicyFrame("CS101", "Intro to Programming", "inst1", "Fall 2025");
            frame.setVisible(true);
        });
    }
}