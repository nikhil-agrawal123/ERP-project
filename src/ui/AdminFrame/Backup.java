package ui.AdminFrame;

import middleware.adminService;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Backup extends JFrame {

    // --- UI Color Palette ---
    private final Color bgColor = new Color(42, 48, 60);
    private final Color cardColor = new Color(54, 59, 74);
    private final Color borderColor = new Color(64, 69, 89);
    private final Color buttonColor = new Color(52, 159, 148);
    private final Color dangerColor = new Color(220, 80, 80);
    private final Color textColor = Color.WHITE;
    private final Color textSecondaryColor = new Color(179, 179, 179);

    private final adminService adminService;

    public Backup() {
        super("System Maintenance - Backup & Restore");
        this.adminService = new adminService();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new GridBagLayout()); // Use GridBag for centering

        try {
            ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
            setIconImage(image.getImage());
        } catch (Exception ignored) {}

        // --- Main Card ---
        RoundedPanel mainCard = new RoundedPanel(20, cardColor, borderColor, 1);
        mainCard.setLayout(new BorderLayout(0, 30));
        mainCard.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainCard.setPreferredSize(new Dimension(800, 450));

        // 1. Header
        JPanel headerPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Database Maintenance", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textColor);

        JLabel subLabel = new JLabel("Create backups or restore the system to a previous state.", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subLabel.setForeground(textSecondaryColor);

        headerPanel.add(titleLabel);
        headerPanel.add(subLabel);
        mainCard.add(headerPanel, BorderLayout.NORTH);

        // 2. Actions Panel (Split Layout)
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // --- Left Side: Backup ---
        JPanel backupPanel = createActionOption(
                "Create Backup",
                "Save a complete snapshot of the database to a .sql file.",
                "Backup Now",
                buttonColor,
                true
        );

        // --- Right Side: Restore ---
        JPanel restorePanel = createActionOption(
                "Restore Data",
                "<html><center>Revert database to a previous state.<br><b style='color:#ff6b6b'>Warning: This overwrites current data.</b></center></html>",
                "Select File to Restore",
                dangerColor,
                false
        );

        actionPanel.add(backupPanel);
        actionPanel.add(restorePanel);
        mainCard.add(actionPanel, BorderLayout.CENTER);

        // 3. Footer (Close Button)
        RoundedButton closeButton = new RoundedButton("Close", borderColor, borderColor.brighter(), 10);
        closeButton.setForeground(textColor);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setPreferredSize(new Dimension(100, 40));
        closeButton.addActionListener(e -> dispose());

        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.add(closeButton);
        mainCard.add(footerPanel, BorderLayout.SOUTH);

        add(mainCard);
    }

    private JPanel createActionOption(String title, String desc, String btnText, Color btnColor, boolean isBackup) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(textColor);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><center>" + desc + "</center></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(textSecondaryColor);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton actionBtn = new RoundedButton(btnText, btnColor, btnColor.brighter(), 10);
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        actionBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add Action Logic
        actionBtn.addActionListener(e -> {
            if (isBackup) handleBackup();
            else handleRestore();
        });

        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(lblDesc);
        panel.add(Box.createVerticalGlue());
        panel.add(actionBtn);

        return panel;
    }

    private void handleBackup() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Backup");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        fileChooser.setSelectedFile(new File("UniversityDB_Backup_" + timeStamp + ".sql"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".sql")) {
                file = new File(file.getAbsolutePath() + ".sql");
            }

            boolean success = adminService.performSystemBackup(file);
            if (success) {
                JOptionPane.showMessageDialog(this, "Backup created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Backup failed. Ensure MySQL tools are in system PATH.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRestore() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Restoring will overwrite all current data.\nAre you sure you want to proceed?",
                "Confirm Restore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup File to Restore");
        fileChooser.setFileFilter(new FileNameExtensionFilter("SQL Files", "sql"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            boolean success = adminService.performSystemRestore(file);

            setCursor(Cursor.getDefaultCursor());

            if (success) {
                JOptionPane.showMessageDialog(this, "System restored successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Restore failed. Check log files.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}