package ui.AdminFrame;

import middleware.ResultProcessingService;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Calendar;

public class AdminPublishResult extends JFrame {

    // --- UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color dangerColor = new Color(220, 80, 80);
    private Color dangerHoverColor = new Color(240, 100, 100);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color inputBgColor = new Color(48, 54, 70);

    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);

    private JComboBox<String> semesterDropdown;
    private JSpinner yearSpinner;
    private ResultProcessingService resultService;

    public AdminPublishResult() {
        super("Publish Results");
        this.resultService = new ResultProcessingService();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bgColor);
        header.setBorder(new EmptyBorder(25, 30, 10, 30));

        JLabel title = new JLabel("Publish Results");
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

        // --- Warning Card ---
        RoundedPanel warningCard = new RoundedPanel(15, cardColor, dangerColor, 1);
        warningCard.setLayout(new BorderLayout());
        warningCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        warningCard.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblWarning = new JLabel("<html><center><b>Warning:</b> This action will calculate final grades for all students in the selected semester and mark courses as 'Completed'.<br>This cannot be easily undone.</center></html>");
        lblWarning.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWarning.setForeground(new Color(255, 150, 150)); // Light red text
        lblWarning.setHorizontalAlignment(SwingConstants.CENTER);

        warningCard.add(lblWarning, BorderLayout.CENTER);

        // --- Selection Form Card ---
        RoundedPanel formCard = new RoundedPanel(15, cardColor, borderColor, 1);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Select Semester
        gbc.gridy = 0;
        JLabel semLabel = new JLabel("Select Semester");
        semLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        semLabel.setForeground(textColor);
        formCard.add(semLabel, gbc);

        gbc.gridy = 1;
        String[] sems = {"Monsoon", "Winter", "Summer"};
        semesterDropdown = createStyledComboBox(sems);
        formCard.add(semesterDropdown, gbc);

        // Select Year
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        JLabel yearLabel = new JLabel("Academic Year");
        yearLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        yearLabel.setForeground(textColor);
        formCard.add(yearLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 2020, 2035, 1));
        styleSpinner(yearSpinner);
        formCard.add(yearSpinner, gbc);

        // --- Publish Button ---
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 0, 0, 0);

        // Using Danger Color to indicate critical action
        RoundedButton publishBtn = new RoundedButton("Calculate & Publish", dangerColor, dangerHoverColor, 10);
        publishBtn.setForeground(Color.WHITE);
        publishBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        publishBtn.setPreferredSize(new Dimension(0, 50));

        publishBtn.addActionListener(e -> handlePublish());

        formCard.add(publishBtn, gbc);

        // Add cards to main panel
        mainPanel.add(warningCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(formCard);
        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }

    private void handlePublish() {
        String sem = (String) semesterDropdown.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        String fullSemester = sem + " " + year;

        // Confirm Action
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to publish results for " + fullSemester + "?\n\n" +
                        "- CGPA will be recalculated.\n" +
                        "- Transcripts will be updated.\n" +
                        "- Emails may be sent to students.",
                "Confirm Publication", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if(confirm == JOptionPane.YES_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            String resultMsg = resultService.publishResults(fullSemester);

            setCursor(Cursor.getDefaultCursor());

            // Show Result
            if (resultMsg.startsWith("Error")) {
                JOptionPane.showMessageDialog(this, resultMsg, "Publication Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, resultMsg, "Publication Successful", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        }
    }

    // --- Styling Helpers (Reused from other frames) ---

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
        @Override protected void configureScrollBarColors() { this.thumbColor = buttonColor; this.trackColor = bgColor; }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            g.setColor(thumbColor); ((Graphics2D)g).fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, 10, 10));
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor); g.fillRect(r.x, r.y, r.width, r.height);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPublishResult().setVisible(true));
    }
}