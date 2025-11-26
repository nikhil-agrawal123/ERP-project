package ui.AdminFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import middleware.adminService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class ChangeSemesterFrame extends JFrame {

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

    // --- New Components for Dates ---
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;

    private adminService adminService;

    public ChangeSemesterFrame() {
        super("Academic Settings");
        this.adminService = new adminService();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 850);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);

        // Main Content inside a scroll pane to handle height
        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bgColor);
        header.setBorder(new EmptyBorder(25, 30, 10, 30));
        JLabel title = new JLabel("Academic Settings");
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

        // --- SECTION 1: CURRENT STATUS ---
        RoundedPanel currentStatusCard = new RoundedPanel(15, cardColor, borderColor, 1);
        currentStatusCard.setLayout(new BorderLayout());
        currentStatusCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        currentStatusCard.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblCurrent = new JLabel("Current System Semester");
        lblCurrent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCurrent.setForeground(textSecondaryColor);

        String currentSemText = adminService.getCurrentSemesterLabel();
        JLabel lblVal = new JLabel(currentSemText);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblVal.setForeground(buttonColorGlow);

        currentStatusCard.add(lblCurrent, BorderLayout.NORTH);
        currentStatusCard.add(lblVal, BorderLayout.CENTER);

        mainPanel.add(currentStatusCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- SECTION 2: CHANGE SEMESTER ---
        mainPanel.add(createSectionHeader("Change Semester"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createSemesterChangeCard());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- SECTION 3: REGISTRATION PERIOD (NEW) ---
        mainPanel.add(createSectionHeader("Course Registration Period"));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createRegistrationDateCard());

        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }

    private JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(textSecondaryColor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private RoundedPanel createSemesterChangeCard() {
        RoundedPanel formCard = new RoundedPanel(15, cardColor, borderColor, 1);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0;
        formCard.add(createLabel("Select New Semester"), gbc);

        gbc.gridy = 1;
        String[] sems = {"Winter", "Summer", "Monsoon"};
        semesterDropdown = createStyledComboBox(sems);
        formCard.add(semesterDropdown, gbc);

        gbc.gridy = 2;
        formCard.add(createLabel("Academic Year"), gbc);

        gbc.gridy = 3;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear + 1, 2020, 2035, 1));
        styleSpinner(yearSpinner);
        formCard.add(yearSpinner, gbc);

        // Dynamic Info Panel
        gbc.gridy = 4;
        infoPanel = new RoundedPanel(10, inputBgColor, borderColor, 1);
        infoPanel.setLayout(new BorderLayout());
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        infoLabel = new JLabel();
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateInfoText("Winter");
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        formCard.add(infoPanel, gbc);

        semesterDropdown.addActionListener(e -> updateInfoText((String) semesterDropdown.getSelectedItem()));

        // Button
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 0, 0, 0);
        RoundedButton updateBtn = new RoundedButton("Update System Semester", buttonColor, buttonColorGlow, 10);
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        updateBtn.setPreferredSize(new Dimension(0, 45));

        updateBtn.addActionListener(e -> {
            String newSem = (String) semesterDropdown.getSelectedItem();
            int newYear = (Integer) yearSpinner.getValue();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Move system to " + newSem + " " + newYear + "?\nThis affects all student records.",
                    "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm == JOptionPane.YES_OPTION) {
                boolean success = adminService.changeSystemSemester(newSem, newYear);
                if (success) {
                    JOptionPane.showMessageDialog(this, "System updated to " + newSem + " " + newYear);
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        formCard.add(updateBtn, gbc);

        return formCard;
    }

    // --- NEW PANEL FOR DATES ---
    private RoundedPanel createRegistrationDateCard() {
        RoundedPanel card = new RoundedPanel(15, cardColor, borderColor, 1);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Start Date
        gbc.gridy = 0;
        card.add(createLabel("Registration Start Date"), gbc);

        gbc.gridy = 1;
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        styleDateSpinner(startDateSpinner);
        card.add(startDateSpinner, gbc);

        // End Date
        gbc.gridy = 2;
        card.add(createLabel("Registration End Date"), gbc);

        gbc.gridy = 3;
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        styleDateSpinner(endDateSpinner);
        card.add(endDateSpinner, gbc);

        // Button
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 0, 0);
        RoundedButton saveDatesBtn = new RoundedButton("Set Registration Window", buttonColor, buttonColorGlow, 10);
        saveDatesBtn.setForeground(Color.WHITE);
        saveDatesBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveDatesBtn.setPreferredSize(new Dimension(0, 45));

        saveDatesBtn.addActionListener(e -> {
            Date start = (Date) startDateSpinner.getValue();
            Date end = (Date) endDateSpinner.getValue();

            boolean success = adminService.setRegistrationPeriod(start, end);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration period updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error: Start Date must be before End Date.", "Date Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        card.add(saveDatesBtn, gbc);

        return card;
    }

    private void updateInfoText(String semester) {
        if ("Summer".equals(semester)) {
            infoLabel.setText("<html><b style='color:#FFC107'>Note:</b> Summer semester is optional. Student semester counts <b>will NOT</b> be incremented.</html>");
            infoLabel.setForeground(Color.LIGHT_GRAY);
        } else {
            infoLabel.setText("<html><b style='color:#529f94'>Standard Term:</b> Active students will be promoted to the next semester (e.g. Sem 1 -> Sem 2).</html>");
            infoLabel.setForeground(textColor);
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(textColor);
        return lbl;
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

    private void styleDateSpinner(JSpinner spinner) {
        styleSpinner(spinner);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(dateEditor);
        // Style the text field inside the editor again as setEditor might reset it
        JTextField tf = dateEditor.getTextField();
        tf.setBackground(inputBgColor);
        tf.setForeground(textColor);
        tf.setCaretColor(buttonColor);
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