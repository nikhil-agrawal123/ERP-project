package ui.FacultyFrame;

import dbClasses.GradeRange;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import middleware.gradingService;
import middleware.maintenanceService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class DefineGradeRangesFrame extends JFrame {

    // --- UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color accentColor = new Color(52, 159, 148);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color inputBgColor = new Color(40, 44, 55);

    private String courseCode;
    private String courseName;
    private String instructorId;
    private String semester;

    private List<GradeRange> gradeRanges;
    private List<JTextField> scoreFields;
    private gradingService gradingService;
    private maintenanceService maintenanceService;

    /**
     * Updated constructor to accept section details.
     */
    public DefineGradeRangesFrame(String courseCode, String courseName, String instructorId, String semester) {
        super("Grade Thresholds - " + courseName);
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.instructorId = instructorId;
        this.semester = semester;

        this.scoreFields = new ArrayList<>();
        this.gradingService = new gradingService();
        this.maintenanceService = new maintenanceService();

        // --- Load Data ---
        List<GradeRange> savedRanges = gradingService.getGradeCutoffs(courseCode, instructorId, semester);
        if (savedRanges != null && !savedRanges.isEmpty()) {
            this.gradeRanges = savedRanges;
        } else {
            this.gradeRanges = loadDefaultRanges(); // Use defaults if no DB entry
        }

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(550, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        JScrollPane scrollPane = createMainContent();
        add(scrollPane, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    // ... (createHeader, createMainContent, addGradeRow, createFooter methods remain mostly the same) ...

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bgColor);
        header.setBorder(new EmptyBorder(25, 30, 10, 30));
        JLabel title = new JLabel("Grade Cutoffs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor);
        RoundedButton backBtn = new RoundedButton("Close", borderColor, borderColor.brighter(), borderColor.darker(), 8);
        backBtn.setForeground(textColor);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setPreferredSize(new Dimension(80, 35));
        backBtn.addActionListener(e -> dispose());
        header.add(title, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);
        return header;
    }

    private JScrollPane createMainContent() {
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(bgColor);
        mainContainer.setBorder(new EmptyBorder(10, 30, 10, 30));

        JLabel infoLabel = new JLabel("<html>Define the <b>minimum percentage</b> required for each grade.<br>Values must be in descending order (e.g., A > B).</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(textSecondaryColor);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel infoWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoWrapper.setOpaque(false);
        infoWrapper.add(infoLabel);
        mainContainer.add(infoWrapper);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        RoundedPanel card = new RoundedPanel(15, cardColor, borderColor, 1);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(8, 0, 8, 0);

        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.5;
        JLabel h1 = new JLabel("Grade");
        h1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h1.setForeground(textSecondaryColor);
        card.add(h1, gbc);

        gbc.gridx = 1; gbc.weightx = 0.5;
        JLabel h2 = new JLabel("Min Score (%)");
        h2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h2.setForeground(textSecondaryColor);
        h2.setHorizontalAlignment(SwingConstants.RIGHT);
        card.add(h2, gbc);

        gbc.gridy++;
        gbc.gridx = 0; gbc.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(borderColor);
        sep.setBackground(cardColor);
        card.add(sep, gbc);
        gbc.gridwidth = 1;

        for (GradeRange range : gradeRanges) {
            gbc.gridy++;
            addGradeRow(card, gbc, range);
        }

        mainContainer.add(card);
        mainContainer.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        return scrollPane;
    }

    private void addGradeRow(JPanel panel, GridBagConstraints gbc, GradeRange range) {
        gbc.gridx = 0;
        JLabel lblGrade = new JLabel(range.getGradeLetter());
        lblGrade.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblGrade.setForeground(textColor);
        panel.add(lblGrade, gbc);

        gbc.gridx = 1;
        JTextField scoreField = new JTextField(String.valueOf(range.getMinScore()));
        styleTextField(scoreField);

        if (range.getGradeLetter().equals("F")) {
            scoreField.setText("0");
            scoreField.setEnabled(false);
            scoreField.setBackground(cardColor);
            scoreField.setBorder(BorderFactory.createLineBorder(borderColor));
            scoreField.setForeground(textSecondaryColor);
        }

        scoreField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
                if (scoreField.getText().length() >= 3 && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        scoreFields.add(scoreField);
        JPanel fieldWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        fieldWrapper.setOpaque(false);
        fieldWrapper.add(scoreField);
        panel.add(fieldWrapper, gbc);
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 20));
        footer.setBackground(bgColor);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor));

        RoundedButton saveBtn = new RoundedButton("Save Cutoffs", buttonColor, buttonColorGlow, 10);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveBtn.setPreferredSize(new Dimension(150, 45));
        saveBtn.addActionListener(e -> {
            if(!maintenanceService.isMaintenanceActive()){
                saveRanges();
            }else {
                JOptionPane.showMessageDialog(footer, "System Under Maintenance");
            }
        });
        footer.add(saveBtn);
        return footer;
    }

    // --- SAVE LOGIC ---
    private void saveRanges() {
        List<GradeRange> newRanges = new ArrayList<>();
        for (int i = 0; i < gradeRanges.size(); i++) {
            String letter = gradeRanges.get(i).getGradeLetter();
            String textVal = scoreFields.get(i).getText();
            int val = 0;
            try {
                val = Integer.parseInt(textVal);
                if (val > 100) {
                    JOptionPane.showMessageDialog(this, "Score cannot exceed 100 (Grade " + letter + ")", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number for Grade " + letter, "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            newRanges.add(new GradeRange(letter, val));
        }

        // Validation
        for (int i = 0; i < newRanges.size() - 1; i++) {
            int current = newRanges.get(i).getMinScore();
            int next = newRanges.get(i+1).getMinScore();
            if (current <= next) {
                String msg = "<html>Logic Error:<br><b>" + newRanges.get(i).getGradeLetter() + "</b> (" + current + "%) must be greater than <b>" +
                        newRanges.get(i+1).getGradeLetter() + "</b> (" + next + "%).</html>";
                JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // --- CALL SERVICE ---
        boolean success = gradingService.saveGradeCutoffs(courseCode, courseName, instructorId, semester, newRanges);

        if (success) {
            JOptionPane.showMessageDialog(this, "Grade thresholds saved to database!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save. Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<GradeRange> loadDefaultRanges() {
        List<GradeRange> list = new ArrayList<>();
        list.add(new GradeRange("A+", 97));
        list.add(new GradeRange("A", 93));
        list.add(new GradeRange("A-", 90));
        list.add(new GradeRange("B+", 87));
        list.add(new GradeRange("B", 83));
        list.add(new GradeRange("B-", 80));
        list.add(new GradeRange("C+", 77));
        list.add(new GradeRange("C", 73));
        list.add(new GradeRange("C-", 70));
        list.add(new GradeRange("D", 60));
        list.add(new GradeRange("F", 0));
        return list;
    }

    // ... (styleTextField, StyledScrollBarUI unchanged) ...
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.BOLD, 18));
        field.setPreferredSize(new Dimension(80, 40));
        field.setBackground(inputBgColor);
        field.setForeground(textColor);
        field.setCaretColor(accentColor);
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setBorder(BorderFactory.createLineBorder(borderColor, 1));
    }

    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() { this.thumbColor = buttonColor; this.trackColor = bgColor; }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() { JButton btn = new JButton(); btn.setPreferredSize(new Dimension(0, 0)); return btn; }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            g.setColor(thumbColor); ((Graphics2D)g).fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, 10, 10));
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor); g.fillRect(r.x, r.y, r.width, r.height);
        }
    }

    public static void main(String[] args) {
        // Test Mode
        SwingUtilities.invokeLater(() -> new DefineGradeRangesFrame("CS101", "Intro to Programming", "inst1", "Monsoon 2025").setVisible(true));
    }
}