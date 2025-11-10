package ui.dashboard;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

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
    private JList<GradingComponent> viewList; // Replaced JTable
    private DefaultListModel<GradingComponent> viewListModel; // Replaced TableModel
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
        setSize(1080, 1080); // As requested
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
        mainCardPanel.add(createViewPanel(), "VIEW");
        mainCardPanel.add(createEditPanel(), "EDIT");

        add(mainCardPanel, BorderLayout.CENTER);

        // Start in View Mode
        cardLayout.show(mainCardPanel, "VIEW");
    }

    /**
     * Creates the "View Mode" panel with the non-editable table.
     */
    /**
     * Creates the "View Mode" panel with the non-editable table.
     */
    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // --- Table ---
        // --- List (replaces Table) ---
        viewListModel = new DefaultListModel<>();
        viewList = new JList<>(viewListModel);
        viewList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        viewList.setBackground(mainPanelColor); // Make background blend in

        // Apply the same aesthetic renderer as the edit panel
        viewList.setCellRenderer(new GradingComponentRenderer());
        viewList.setFixedCellHeight(60);

        // --- Make the list "Read-Only" ---
        viewList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                // Do nothing to prevent selection
            }
            @Override
            public void addSelectionInterval(int index0, int index1) {
                // Do nothing
            }
        });
        viewList.setFocusable(false); // Don't allow keyboard focus

        JScrollPane scrollPane = new JScrollPane(viewList);
        scrollPane.setBorder(BorderFactory.createLineBorder(sideMenuColor, 2));
        scrollPane.getViewport().setBackground(mainPanelColor);

        // --- [START] --- New Wrapper Logic ---

        // This wrapper panel will control the table's height
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false); // Make it transparent
        tableWrapper.add(scrollPane, BorderLayout.NORTH);

        // This wrapper will push everything up
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);

        // --- Create the bottom panel FIRST ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        // Add some padding above the buttons
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        totalLabelView = new JLabel();
        totalLabelView.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bottomPanel.add(totalLabelView, BorderLayout.WEST);

        // --- Edit Button ---
        RoundedButton editButton = new RoundedButton("Edit Policy", buttonColor, buttonHoverColor, buttonPressedColor);
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        editButton.setPreferredSize(new Dimension(180, 50));
        editButton.addActionListener(e -> {
            // Load fresh data into the edit list before switching
            loadDataIntoEditList();
            cardLayout.show(mainCardPanel, "EDIT");
        });

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // Align button right
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(editButton);
        bottomPanel.add(buttonWrapper, BorderLayout.EAST);

        // --- Create a vertical stacking panel ---
        JPanel verticalStackPanel = new JPanel();
        verticalStackPanel.setLayout(new BoxLayout(verticalStackPanel, BoxLayout.Y_AXIS));
        verticalStackPanel.setOpaque(false);

        verticalStackPanel.add(createHeaderPanel());

        // Add tableWrapper (table)
        tableWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        verticalStackPanel.add(tableWrapper);

        // Add bottomPanel (button)
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        verticalStackPanel.add(bottomPanel);

        // Add this stack to the NORTH of the contentWrapper
        contentWrapper.add(verticalStackPanel, BorderLayout.NORTH);

        // Add a spacer to fill all remaining space below the buttons
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        contentWrapper.add(spacer, BorderLayout.CENTER);

        // Add this new wrapper to the main panel's center
        panel.add(contentWrapper, BorderLayout.CENTER);

        // --- [END] --- New Wrapper Logic ---

        // We no longer add bottomPanel to panel.SOUTH

        // Load data into the table
        loadDataIntoViewList();
        return panel;
    }

    /**
     * Creates the "Edit Mode" panel with the list and controls.
     */
    /**
     * Creates the "Edit Mode" panel with the list and controls.
     */
    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // --- Main Edit List ---
        editListModel = new DefaultListModel<>();
        editList = new JList<>(editListModel);
        editList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        editList.setBackground(mainPanelColor);
        editList.setForeground(textColor);
        editList.setSelectionBackground(buttonColor);
        editList.setSelectionForeground(Color.BLACK);
        editList.setCellRenderer(new GradingComponentRenderer()); // Custom renderer
        editList.setFixedCellHeight(60);

        JScrollPane scrollPane = new JScrollPane(editList);
        scrollPane.setBorder(BorderFactory.createLineBorder(sideMenuColor, 2));

        // --- [START] --- New Wrapper Logic ---

        // This panel will wrap the list and buttons, pushing them to the top
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);

        // --- Create the bottom panel FIRST ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        // Add some padding above the buttons
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        totalLabelEdit = new JLabel();
        totalLabelEdit.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bottomPanel.add(totalLabelEdit, BorderLayout.WEST);

        // Button wrapper for Save/Cancel
        JPanel saveCancelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        saveCancelPanel.setOpaque(false);

        RoundedButton saveButton = new RoundedButton("Save Changes", buttonColor, buttonHoverColor, buttonPressedColor);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saveButton.setPreferredSize(new Dimension(180, 50));
        saveButton.addActionListener(e -> savePolicy());
        saveCancelPanel.add(saveButton);

        RoundedButton cancelButton = new RoundedButton("Cancel", sideMenuColor, sideMenuColor.brighter(), sideMenuColor.darker());
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cancelButton.setPreferredSize(new Dimension(130, 50));
        cancelButton.addActionListener(e -> cardLayout.show(mainCardPanel, "VIEW")); // Just switch back
        saveCancelPanel.add(cancelButton);

        bottomPanel.add(saveCancelPanel, BorderLayout.EAST);

        // --- Create a vertical stacking panel ---
        // This holds the list and the buttons together
        JPanel verticalStackPanel = new JPanel();
        verticalStackPanel.setLayout(new BoxLayout(verticalStackPanel, BoxLayout.Y_AXIS));
        verticalStackPanel.setOpaque(false);

        verticalStackPanel.add(createHeaderPanel());

        // Add scrollPane (list)
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        verticalStackPanel.add(scrollPane);

        // Add bottomPanel (buttons)
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        verticalStackPanel.add(bottomPanel);

        // Add this stack to the NORTH of the contentWrapper
        contentWrapper.add(verticalStackPanel, BorderLayout.NORTH);

        // Add a spacer to fill all remaining space below the buttons
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        contentWrapper.add(spacer, BorderLayout.CENTER);

        // Add this new wrapper to the main panel's center
        panel.add(contentWrapper, BorderLayout.CENTER);

        // --- [END] --- New Wrapper Logic ---


        // --- Top Button Panel (Add, Edit, Remove) ---
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlsPanel.setOpaque(false);

        RoundedButton addButton = new RoundedButton("Add Component", buttonColor, buttonHoverColor, buttonPressedColor);
        addButton.setPreferredSize(new Dimension(180, 45));
        addButton.addActionListener(e -> addComponent());
        controlsPanel.add(addButton);

        RoundedButton editButton = new RoundedButton("Edit Selected", buttonColor, buttonHoverColor, buttonPressedColor);
        editButton.setPreferredSize(new Dimension(180, 45));
        editButton.addActionListener(e -> editComponent());
        controlsPanel.add(editButton);

        RoundedButton removeButton = new RoundedButton("Remove Selected", dangerColor, dangerHoverColor, dangerPressedColor);
        removeButton.setPreferredSize(new Dimension(180, 45));
        removeButton.addActionListener(e -> removeComponent());
        controlsPanel.add(removeButton);

        panel.add(controlsPanel, BorderLayout.NORTH);

        // We no longer add bottomPanel to panel.SOUTH

        return panel;
    }

    // --- Data and UI Logic ---

    /**
     * Clears and loads data from 'policyComponents' list into the VIEW table.
     */
    private void loadDataIntoViewList() {
        viewListModel.clear(); // Clear existing items
        for (GradingComponent comp : policyComponents) {
            viewListModel.addElement(comp);
        }
        updateTotalLabel(totalLabelView);
    }

    /**
     * Clears and loads data from 'policyComponents' list into the EDIT list.
     */
    private void loadDataIntoEditList() {
        editListModel.clear();
        for (GradingComponent comp : policyComponents) {
            editListModel.addElement(new GradingComponent(comp.getName(), comp.getPercentage())); // Add copies
        }
        updateTotalLabel(totalLabelEdit);
    }

    /**
     * Handles adding a new component in Edit Mode.
     */
    private void addComponent() {
        // Use a custom panel for the dialog
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

    /**
     * Handles editing a selected component in Edit Mode.
     */
    private void editComponent() {
        GradingComponent selected = editList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a component to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Use a custom panel for the dialog, pre-filled with values
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
            // Update the selected item
            selected.setName(name);
            selected.setPercentage(percent);
            editList.repaint(); // Tell the list to redraw
            updateTotalLabel(totalLabelEdit);
        }
    }

    /**
     * Handles removing a selected component in Edit Mode.
     */
    private void removeComponent() {
        GradingComponent selected = editList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a component to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        editListModel.removeElement(selected);
        updateTotalLabel(totalLabelEdit);
    }

    /**
     * Saves the new policy from the Edit List.
     */
    private void savePolicy() {
        int total = getTotalFromEditList();
        if (total != 100) {
            JOptionPane.showMessageDialog(
                    this, "Total percentage must be 100%. Current total is " + total + "%.", "Save Error", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Create a new list from the editListModel
        List<GradingComponent> newPolicy = new ArrayList<>();
        for (int i = 0; i < editListModel.getSize(); i++) {
            newPolicy.add(editListModel.getElementAt(i));
        }

        // "Save" to our main list and the mock service
        this.policyComponents = newPolicy;
        GradingPolicyService.savePolicy(courseCode, this.policyComponents);

        // Refresh the read-only table
        loadDataIntoViewList();

        // Switch back to View Mode
        cardLayout.show(mainCardPanel, "VIEW");

        JOptionPane.showMessageDialog(this, "Policy saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Helper to get the total from the EDIT list.
     */
    private int getTotalFromEditList() {
        int total = 0;
        for (int i = 0; i < editListModel.getSize(); i++) {
            total += editListModel.getElementAt(i).getPercentage();
        }
        return total;
    }

    /**
     * Helper to get the total from the VIEW table.
     */
    private int getTotalFromTable() {
        int total = 0;
        for (GradingComponent comp : policyComponents) {
            total += comp.getPercentage();
        }
        return total;
    }

    /**
     * Updates the specified total label (for either view or edit).
     */
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

    /**
     * Styles the JTable with the app's dark theme.
     */
    private void styleTable(JTable table) {
        table.setBackground(mainPanelColor);
        table.setForeground(textColor);
        table.setFont(tableFont);
        table.setGridColor(sideMenuColor);
        table.setRowHeight(40);

        // --- Style Header ---
        JTableHeader header = table.getTableHeader();
        header.setBackground(sideMenuColor);
        header.setForeground(textColor);
        header.setFont(tableHeaderFont);
        header.setPreferredSize(new Dimension(100, 50));

        // --- Style Cells (Center alignment) ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(mainPanelColor);
        centerRenderer.setForeground(textColor);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBackground(mainPanelColor);
        leftRenderer.setForeground(textColor);
        leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // Add padding

        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // --- Style Selection ---
        table.setSelectionBackground(buttonColor);
        table.setSelectionForeground(Color.BLACK);
    }

    /**
     * Custom renderer for the JList in Edit Mode.
     */
    class GradingComponentRenderer extends JPanel implements ListCellRenderer<GradingComponent> {
        private JLabel nameLabel;
        private JLabel percentLabel;

        public GradingComponentRenderer() {
            setLayout(new BorderLayout(10, 0));
            // Add padding for the whole row
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            nameLabel = new JLabel();
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Main text

            percentLabel = new JLabel();
            percentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Percentage

            add(nameLabel, BorderLayout.WEST);
            add(percentLabel, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends GradingComponent> list,
                                                      GradingComponent value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {

            // Set the text for this list item
            nameLabel.setText(value.getName());
            percentLabel.setText(value.getPercentage() + "%");

            // Handle selection colors
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                nameLabel.setForeground(list.getSelectionForeground());
                percentLabel.setForeground(list.getSelectionForeground());
            } else {
                setBackground(mainPanelColor);
                nameLabel.setForeground(textColor);
                percentLabel.setForeground(textColor.brighter()); // Make percentage slightly different
            }

            return this;
        }
        /**
         * Creates a styled header row for the component lists.
         */

    }
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(mainPanelColor); // Use main panel color to blend in
        // Match the horizontal padding (15px) and add vertical padding
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JLabel nameHeader = new JLabel("Component Name");
        nameHeader.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Larger font
        nameHeader.setForeground(buttonColor); // Highlight color

        JLabel percentHeader = new JLabel("Percentage");
        percentHeader.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Larger font
        percentHeader.setForeground(buttonColor); // Highlight color
        percentHeader.setHorizontalAlignment(SwingConstants.RIGHT); // Align right

        header.add(nameHeader, BorderLayout.WEST);
        header.add(percentHeader, BorderLayout.EAST);

        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Set max height to ensure it doesn't stretch
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        return header;
    }
}