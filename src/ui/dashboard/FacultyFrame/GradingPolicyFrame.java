package ui.dashboard.FacultyFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
  NOTE:
  - This file DOES NOT declare a GradingComponent class. It uses the existing
    ui.dashboard.FacultyFrame.GradingComponent model (the one you posted earlier).
  - Keep GradingPolicyService in this file (or remove it if you already have it elsewhere).
*/

public class GradingPolicyFrame extends JFrame {

    // --- Style Colors ---
    private Color bgColor = new Color(45, 45, 45);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color sideMenuColor = new Color(60, 60, 60);

    // Button Colors
    private Color buttonColor = new Color(57, 174, 168);
    private Color buttonHoverColor = new Color(67, 204, 198);
    private Color buttonPressedColor = new Color(47, 144, 138);

    private Color dangerColor = new Color(220, 80, 80);
    private Color dangerHoverColor = new Color(240, 100, 100);
    private Color dangerPressedColor = new Color(200, 60, 60);

    private Color textColor = Color.WHITE;
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 16);
    private Font tableHeaderFont = new Font("Segoe UI", Font.BOLD, 18);

    private String courseCode;
    private List<GradingComponent> policyComponents;

    private CardLayout cardLayout;
    private JPanel mainCardPanel;

    // --- Components for VIEW card ---
    private JList<GradingComponent> viewList;
    private DefaultListModel<GradingComponent> viewListModel;
    private JLabel totalLabelView;

    // --- Components for EDIT card ---
    private JList<GradingComponent> editList;
    private DefaultListModel<GradingComponent> editListModel;
    private JLabel totalLabelEdit;

    public GradingPolicyFrame(String courseCode) {
        super("Grading Policy for " + courseCode);
        this.courseCode = courseCode;

        // Load data from our mock service
        this.policyComponents = GradingPolicyService.getPolicy(courseCode);

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 700); // Adjusted size
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(10, 10));

        // --- Title ---
        JLabel title = new JLabel("Grading Policy Management", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(textColor);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // --- Main Card Panel ---
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setOpaque(false);
        mainCardPanel.add(createViewPanel(), "VIEW");
        mainCardPanel.add(createEditPanel(), "EDIT");

        add(mainCardPanel, BorderLayout.CENTER);

        // Start in View Mode
        cardLayout.show(mainCardPanel, "VIEW");
    }

    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // --- List (replaces Table) ---
        viewListModel = new DefaultListModel<>();
        viewList = new JList<>(viewListModel);
        viewList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        viewList.setBackground(mainPanelColor); // Make background blend in
        viewList.setCellRenderer(new GradingComponentRenderer());
        viewList.setFixedCellHeight(60);

        // Make read-only selection
        viewList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) { /* no-op */ }
            @Override
            public void addSelectionInterval(int index0, int index1) { /* no-op */ }
        });
        viewList.setFocusable(false);

        JScrollPane scrollPane = new JScrollPane(viewList);
        scrollPane.setBorder(BorderFactory.createLineBorder(sideMenuColor, 2));
        scrollPane.getViewport().setBackground(mainPanelColor);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.add(createHeaderPanel());
        listPanel.add(scrollPane);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        totalLabelView = new JLabel();
        totalLabelView.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bottomPanel.add(totalLabelView, BorderLayout.WEST);

        JButton editButton = new JButton("Edit Policy");
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        editButton.setPreferredSize(new Dimension(180, 50));
        editButton.addActionListener(e -> {
            loadDataIntoEditList();
            cardLayout.show(mainCardPanel, "EDIT");
        });

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(editButton);
        bottomPanel.add(buttonWrapper, BorderLayout.EAST);

        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadDataIntoViewList();
        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        editListModel = new DefaultListModel<>();
        editList = new JList<>(editListModel);
        editList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        editList.setBackground(mainPanelColor);
        editList.setForeground(textColor);
        editList.setSelectionBackground(buttonColor);
        editList.setSelectionForeground(Color.BLACK);
        editList.setCellRenderer(new GradingComponentRenderer());
        editList.setFixedCellHeight(60);

        JScrollPane scrollPane = new JScrollPane(editList);
        scrollPane.setBorder(BorderFactory.createLineBorder(sideMenuColor, 2));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.add(createHeaderPanel());
        listPanel.add(scrollPane);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        totalLabelEdit = new JLabel();
        totalLabelEdit.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bottomPanel.add(totalLabelEdit, BorderLayout.WEST);

        JPanel saveCancelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        saveCancelPanel.setOpaque(false);

        JButton saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saveButton.setPreferredSize(new Dimension(180, 50));
        saveButton.addActionListener(e -> savePolicy());
        saveCancelPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cancelButton.setPreferredSize(new Dimension(130, 50));
        cancelButton.addActionListener(e -> cardLayout.show(mainCardPanel, "VIEW"));
        saveCancelPanel.add(cancelButton);

        bottomPanel.add(saveCancelPanel, BorderLayout.EAST);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlsPanel.setOpaque(false);

        JButton addButton = new JButton("Add Component");
        addButton.setPreferredSize(new Dimension(180, 45));
        addButton.addActionListener(e -> addComponent());
        controlsPanel.add(addButton);

        JButton editButton = new JButton("Edit Selected");
        editButton.setPreferredSize(new Dimension(180, 45));
        editButton.addActionListener(e -> editComponent());
        controlsPanel.add(editButton);

        JButton removeButton = new JButton("Remove Selected");
        removeButton.setPreferredSize(new Dimension(180, 45));
        removeButton.addActionListener(e -> removeComponent());
        controlsPanel.add(removeButton);

        panel.add(controlsPanel, BorderLayout.NORTH);
        panel.add(listPanel, BorderLayout.CENTER);
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
        JSpinner percentSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));

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
        JSpinner percentSpinner = new JSpinner(new SpinnerNumberModel(selected.getPercentage(), 0, 100, 1));

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
        editListModel.removeElement(selected);
        updateTotalLabel(totalLabelEdit);
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
        GradingPolicyService.savePolicy(courseCode, this.policyComponents);

        loadDataIntoViewList();
        cardLayout.show(mainCardPanel, "VIEW");

        JOptionPane.showMessageDialog(this, "Policy saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private int getTotalFromEditList() {
        int total = 0;
        for (int i = 0; i < editListModel.getSize(); i++) {
            total += editListModel.getElementAt(i).getPercentage();
        }
        return total;
    }

    private int getTotalFromTable() {
        int total = 0;
        for (GradingComponent comp : policyComponents) {
            total += comp.getPercentage();
        }
        return total;
    }

    private void updateTotalLabel(JLabel label) {
        int total;
        if (label == totalLabelView) {
            total = getTotalFromTable();
        } else {
            total = getTotalFromEditList();
        }
        label.setText("TOTAL: " + total + "%");
        if (total == 100) {
            label.setForeground(buttonColor);
        } else {
            label.setForeground(dangerColor);
        }
    }

    class GradingComponentRenderer extends JPanel implements ListCellRenderer<GradingComponent> {
        private JLabel nameLabel;
        private JLabel percentLabel;

        public GradingComponentRenderer() {
            setLayout(new BorderLayout(10, 0));
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            nameLabel = new JLabel();
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

            percentLabel = new JLabel();
            percentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));

            add(nameLabel, BorderLayout.WEST);
            add(percentLabel, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends GradingComponent> list,
                                                      GradingComponent value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {

            nameLabel.setText(value.getName());
            percentLabel.setText(value.getPercentage() + "%");

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                nameLabel.setForeground(list.getSelectionForeground());
                percentLabel.setForeground(list.getSelectionForeground());
            } else {
                setBackground(mainPanelColor);
                nameLabel.setForeground(textColor);
                percentLabel.setForeground(textColor.brighter());
            }

            return this;
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(mainPanelColor);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JLabel nameHeader = new JLabel("Component Name");
        nameHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameHeader.setForeground(buttonColor);

        JLabel percentHeader = new JLabel("Percentage");
        percentHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        percentHeader.setForeground(buttonColor);
        percentHeader.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(nameHeader, BorderLayout.WEST);
        header.add(percentHeader, BorderLayout.EAST);

        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        return header;
    }

    // Mock service — keep or remove if you already have this class elsewhere
    static class GradingPolicyService {
        private static java.util.Map<String, List<GradingComponent>> policies = new java.util.HashMap<>();
        static {
            List<GradingComponent> policy = new ArrayList<>();
            policy.add(new GradingComponent("Midterm Exam", 30));
            policy.add(new GradingComponent("Final Exam", 40));
            policy.add(new GradingComponent("Assignments (x5)", 20));
            policy.add(new GradingComponent("Quizzes (x10)", 10));
            policies.put("CS101", policy);
        }
        public static List<GradingComponent> getPolicy(String courseCode) {
            return new ArrayList<>(policies.getOrDefault(courseCode, new ArrayList<>()));
        }
        public static void savePolicy(String courseCode, List<GradingComponent> policy) {
            policies.put(courseCode, new ArrayList<>(policy));
        }
    }

    // Main for testing
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            GradingPolicyFrame frame = new GradingPolicyFrame("CS101");
            frame.setVisible(true);
        });
    }
}
