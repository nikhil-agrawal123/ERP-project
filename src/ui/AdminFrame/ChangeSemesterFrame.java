package ui.AdminFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import middleware.adminService; // Import Service

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.util.Calendar;

public class ChangeSemesterFrame extends JFrame {

    // --- UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color inputBgColor = new Color(48, 54, 70);

    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);

    private JComboBox<String> semesterDropdown;
    private JSpinner yearSpinner;
    private JLabel infoLabel;
    private RoundedPanel infoPanel;

    private adminService adminService; // Service

    public ChangeSemesterFrame() {
        super("Change System Semester");
        this.adminService = new adminService(); // Init

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    // ... (createHeader is unchanged) ...
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bgColor);
        header.setBorder(new EmptyBorder(25, 30, 10, 30));
        JLabel title = new JLabel("Change Semester");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor);
        RoundedButton backBtn = new RoundedButton("Close", Buttonback, Buttonhover, borderColor.darker(), borderColor, 10);
        backBtn.setForeground(textSecondaryColor);
        backBtn.setPreferredSize(new Dimension(80, 35));
        backBtn.addActionListener(e -> dispose());
        header.add(title, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 40, 40, 40));

        // --- Current State Card ---
        RoundedPanel currentStatusCard = new RoundedPanel(15, cardColor, borderColor, 1);
        currentStatusCard.setLayout(new BorderLayout());
        currentStatusCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        currentStatusCard.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblCurrent = new JLabel("Current System Semester");
        lblCurrent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCurrent.setForeground(textSecondaryColor);

        // --- FETCH LIVE DATA ---
        String currentSemText = adminService.getCurrentSemesterLabel();
        JLabel lblVal = new JLabel(currentSemText);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblVal.setForeground(buttonColorGlow);

        currentStatusCard.add(lblCurrent, BorderLayout.NORTH);
        currentStatusCard.add(lblVal, BorderLayout.CENTER);

        // --- Form Card ---
        RoundedPanel formCard = new RoundedPanel(15, cardColor, borderColor, 1);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Label: New Semester
        gbc.gridy = 0;
        JLabel semLabel = new JLabel("Select New Semester");
        semLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        semLabel.setForeground(textColor);
        formCard.add(semLabel, gbc);

        // Dropdown
        gbc.gridy = 1;
        String[] sems = {"Winter", "Summer", "Monsoon"};
        semesterDropdown = createStyledComboBox(sems);
        formCard.add(semesterDropdown, gbc);

        // Label: Year
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        JLabel yearLabel = new JLabel("Academic Year");
        yearLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        yearLabel.setForeground(textColor);
        formCard.add(yearLabel, gbc);

        // Spinner
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 2020, 2035, 1)); // Default to current
        styleSpinner(yearSpinner);
        formCard.add(yearSpinner, gbc);

        // --- Dynamic Info Panel ---
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 0, 20, 0);

        infoPanel = new RoundedPanel(10, inputBgColor, borderColor, 1);
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        infoLabel = new JLabel();
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateInfoText("Winter"); // Default

        infoPanel.add(infoLabel, BorderLayout.CENTER);
        formCard.add(infoPanel, gbc);

        // Action Listener
        semesterDropdown.addActionListener(e -> {
            String selected = (String) semesterDropdown.getSelectedItem();
            updateInfoText(selected);
        });

        // --- Update Button ---
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        RoundedButton updateBtn = new RoundedButton("Update System Semester", buttonColor, buttonColorGlow, 10);
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        updateBtn.setPreferredSize(new Dimension(0, 50));

        updateBtn.addActionListener(e -> {
            String newSem = (String) semesterDropdown.getSelectedItem();
            int newYear = (Integer) yearSpinner.getValue();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to move the system to " + newSem + " " + newYear + "?\nThis will update system settings and handle student promotions.",
                    "Confirm Update", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if(confirm == JOptionPane.YES_OPTION) {
                // --- CALL SERVICE ---
                boolean success = adminService.changeSystemSemester(newSem, newYear);

                if (success) {
                    JOptionPane.showMessageDialog(this, "System updated successfully to " + newSem + " " + newYear);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed. Check database connection.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        formCard.add(updateBtn, gbc);

        mainPanel.add(currentStatusCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(formCard);
        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }

    // ... (updateInfoText, styling helpers, inner classes unchanged) ...
    private void updateInfoText(String semester) {
        if ("Summer".equals(semester)) {
            infoLabel.setText("<html><b style='color:#FFC107'>Note:</b> Summer semester is optional. <br>Student semester counts <b>will NOT</b> be incremented.</html>");
            infoLabel.setForeground(Color.LIGHT_GRAY);
        } else {
            infoLabel.setText("<html><b style='color:#529f94'>Standard Term:</b> <br>All active students will be promoted to the next semester count (e.g. Sem 1 -> Sem 2).</html>");
            infoLabel.setForeground(textColor);
        }
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(inputBgColor);
            tf.setForeground(textColor);
            tf.setCaretColor(buttonColor);
        }
        spinner.setBorder(BorderFactory.createLineBorder(borderColor, 1));
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        box.setForeground(textColor);
        box.setBackground(inputBgColor);

        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    setBackground(buttonColor);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(inputBgColor);
                    setForeground(textColor);
                }
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });

        box.setUI(new BasicComboBoxUI() {
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(inputBgColor);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("\u25BC");
                btn.setBackground(inputBgColor);
                btn.setForeground(textSecondaryColor);
                btn.setBorder(BorderFactory.createEmptyBorder());
                btn.setFocusable(false);
                btn.setContentAreaFilled(false);
                return btn;
            }
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    @Override
                    protected JScrollPane createScroller() {
                        JScrollPane scroller = super.createScroller();
                        scroller.getVerticalScrollBar().setUI(new StyledScrollBarUI());
                        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        return scroller;
                    }
                };
                popup.setBorder(BorderFactory.createLineBorder(borderColor));
                return popup;
            }
        });
        box.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
        return box;
    }

    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = buttonColor;
            this.trackColor = bgColor;
        }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            return btn;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChangeSemesterFrame().setVisible(true));
    }
}