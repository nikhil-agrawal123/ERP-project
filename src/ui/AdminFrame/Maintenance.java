package ui.AdminFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.dashboard.AdminDashboard;
import middleware.maintenanceService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Maintenance extends JFrame {

    // --- Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondary = new Color(179, 179, 179);
    private Color accentColor = new Color(52, 159, 148);
    private Color accentGlow = new Color(79, 196, 184);
    private Color dangerColor = new Color(220, 53, 69);
    private Color toggleOffColor = new Color(80, 85, 100);
    private Color borderColor = new Color(64, 69, 89);
    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);

    // --- Components ---
    private ModernToggle masterSwitch;
    private JLabel statusLabel;
    private JLabel masterToggleLabel;
    private JPanel optionsContainer;
    private JTextArea messageArea;
    private List<ModernToggle> subToggles;

    // --- Service ---
    private maintenanceService maintenanceService; // --- NEW ---

    public Maintenance() {
        setTitle("System Maintenance Control");
        this.maintenanceService = new maintenanceService(); // --- INIT ---

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        subToggles = new ArrayList<>();

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        // --- LOAD INITIAL STATE FROM DB ---
        boolean currentStatus = maintenanceService.isMaintenanceActive();
        masterSwitch.setSelected(currentStatus);
        updateVisuals(currentStatus);
    }

    // ... (createHeader is unchanged) ...
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bgColor);
        header.setBorder(new EmptyBorder(25, 30, 10, 30));

        JLabel title = new JLabel("Maintenance Configuration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor);

        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JScrollPane createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 100, 40, 100));

        // --- Section A: Master Control ---
        RoundedPanel masterPanel = new RoundedPanel(20, cardColor, borderColor, 1);
        masterPanel.setLayout(new BorderLayout());
        masterPanel.setMaximumSize(new Dimension(1000, 100));
        masterPanel.setPreferredSize(new Dimension(1000, 100));
        masterPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        JLabel masterLabel = new JLabel("Maintenance Mode");
        masterLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        masterLabel.setForeground(textColor);

        statusLabel = new JLabel("System is LIVE");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(accentColor);

        textPanel.add(masterLabel);
        textPanel.add(statusLabel);

        JPanel switchWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        switchWrapper.setOpaque(false);

        masterToggleLabel = new JLabel("Maintenance: OFF");
        masterToggleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        masterToggleLabel.setForeground(textSecondary);

        masterSwitch = new ModernToggle();
        masterSwitch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (masterSwitch.isEnabled()) {
                    // Perform the toggle logic
                    boolean newState = !masterSwitch.isSelected();
                    toggleMasterState(newState); // Update DB and UI
                }
            }
        });

        switchWrapper.add(masterToggleLabel);
        switchWrapper.add(masterSwitch);

        masterPanel.add(textPanel, BorderLayout.CENTER);
        masterPanel.add(switchWrapper, BorderLayout.EAST);

        // ... (Options Container code is unchanged) ...
        optionsContainer = new JPanel();
        optionsContainer.setLayout(new BoxLayout(optionsContainer, BoxLayout.Y_AXIS));
        optionsContainer.setOpaque(false);

        optionsContainer.add(createOptionRow("Student Login Portal", "Allow students to log in via mobile/web."));
        optionsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        optionsContainer.add(createOptionRow("Faculty Grading System", "Enable grade entry and modification."));
        optionsContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // ... (Message Area code is unchanged) ...
        JPanel msgPanelContainer = new JPanel(new BorderLayout());
        msgPanelContainer.setOpaque(false);
        msgPanelContainer.setMaximumSize(new Dimension(1000, 150));

        JLabel msgLabel = new JLabel("Maintenance Message");
        msgLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        msgLabel.setForeground(textColor);
        msgLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        messageArea = new JTextArea("The system is currently undergoing scheduled maintenance. Please try again later.");
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setForeground(textSecondary);
        messageArea.setBackground(cardColor.darker());
        messageArea.setCaretColor(textColor);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        msgPanelContainer.add(msgLabel, BorderLayout.NORTH);
        msgPanelContainer.add(messageArea, BorderLayout.CENTER);

        // Assemble
        mainPanel.add(masterPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Header for options
        JLabel settingsHeader = new JLabel("Access Control Settings");
        settingsHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        settingsHeader.setForeground(textSecondary);
        settingsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setOpaque(false);
        headerWrapper.setMaximumSize(new Dimension(1000, 30));
        headerWrapper.add(settingsHeader, BorderLayout.WEST);

        mainPanel.add(headerWrapper);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(optionsContainer);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(msgPanelContainer);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scrollPane;
    }

    /**
     * Updates the DB and the UI based on the new state.
     */
    private void toggleMasterState(boolean newState) {
        // 1. Update Database
        boolean success = maintenanceService.setSystemMaintenance(newState);

        if (success) {
            // 2. Update Toggle Visuals
            masterSwitch.setSelected(newState);
            // 3. Update Labels
            updateVisuals(newState);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update system settings. Database Error.");
        }
    }

    private void updateVisuals(boolean isMaintenanceOn) {
        if (isMaintenanceOn) {
            statusLabel.setText("System is UNDER MAINTENANCE");
            statusLabel.setForeground(dangerColor);
            masterToggleLabel.setText("Maintenance: ON");
            masterToggleLabel.setForeground(dangerColor); // changed to red for visibility
            toggleOptions(true);
        } else {
            statusLabel.setText("System is LIVE");
            statusLabel.setForeground(accentColor);
            masterToggleLabel.setText("Maintenance: OFF");
            masterToggleLabel.setForeground(textSecondary);
            toggleOptions(false);
        }
    }

    // ... (createOptionRow, createButtonRow, createFooter, toggleOptions, setPanelEnabled, ModernToggle inner class are unchanged) ...
    // ... (Assuming createButtonRow was added in the previous step, if not, paste it here) ...

    private JPanel createOptionRow(String title, String subtitle) {
        RoundedPanel row = new RoundedPanel(15, cardColor, borderColor, 1);
        row.setLayout(new BorderLayout());
        row.setMaximumSize(new Dimension(1000, 75));
        row.setPreferredSize(new Dimension(1000, 75));
        row.setBorder(new EmptyBorder(10, 25, 10, 25));

        // Left side: Text
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel tLabel = new JLabel(title);
        tLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tLabel.setForeground(textColor);

        JLabel sLabel = new JLabel(subtitle);
        sLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sLabel.setForeground(textSecondary);

        textPanel.add(tLabel);
        textPanel.add(sLabel);

        // Right side: State Label + Toggle
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        controlsPanel.setOpaque(false);

        JLabel stateText = new JLabel("Allowed");
        stateText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        stateText.setForeground(accentColor);

        ModernToggle subToggle = new ModernToggle();
        subToggle.setSelected(true); // Default to allowed
        subToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(subToggle.isEnabled()) {
                    boolean isAllowed = subToggle.isSelected();
                    if(isAllowed) {
                        stateText.setText("Allowed");
                        stateText.setForeground(accentColor); // Teal
                    } else {
                        stateText.setText("Restricted");
                        stateText.setForeground(dangerColor); // RED
                    }
                }
            }
        });

        subToggles.add(subToggle);

        controlsPanel.add(stateText);
        controlsPanel.add(subToggle);

        row.add(textPanel, BorderLayout.CENTER);
        row.add(controlsPanel, BorderLayout.EAST);

        return row;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 25));
        footer.setBackground(bgColor);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor));

        RoundedButton backBtn = new RoundedButton("Back", Buttonback,
                Buttonhover,
                borderColor.darker(), bgColor, 10);
        backBtn.setForeground(textColor);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backBtn.setPreferredSize(new Dimension(150, 50));
        backBtn.addActionListener(e -> {
            new AdminDashboard("ADMIN01", "Admin").setVisible(true);
            this.dispose();
        });

        RoundedButton saveBtn = new RoundedButton("Save Changes", accentColor, accentGlow, accentColor.darker(), accentColor, 10);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveBtn.setPreferredSize(new Dimension(200, 50));
        saveBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Settings Saved"));

        footer.add(backBtn);
        footer.add(saveBtn);
        return footer;
    }

    private void toggleOptions(boolean enable) {
        for (Component comp : optionsContainer.getComponents()) {
            if (comp instanceof RoundedPanel) {
                setPanelEnabled((RoundedPanel) comp, enable);
            }
        }
        optionsContainer.repaint();
    }

    private void setPanelEnabled(JPanel panel, boolean isEnabled) {
        for (Component c : panel.getComponents()) {
            c.setEnabled(isEnabled);
            if (c instanceof JPanel) setPanelEnabled((JPanel) c, isEnabled);
        }
    }

    // --- Inner Class ModernToggle ---
    class ModernToggle extends JComponent {
        private boolean selected = false;
        private Timer timer;
        private float animationProgress = 0f;

        public ModernToggle() {
            setPreferredSize(new Dimension(50, 26));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Note: MouseListener is handled by parent to control state changes

            timer = new Timer(10, e -> {
                if (selected && animationProgress < 1.0f) {
                    animationProgress += 0.15f;
                    if (animationProgress > 1.0f) animationProgress = 1.0f;
                    repaint();
                } else if (!selected && animationProgress > 0.0f) {
                    animationProgress -= 0.15f;
                    if (animationProgress < 0.0f) animationProgress = 0.0f;
                    repaint();
                } else {
                    ((Timer)e.getSource()).stop();
                }
            });
        }

        public boolean isSelected() { return selected; }
        public void setSelected(boolean s) {
            this.selected = s;
            this.animationProgress = s ? 1.0f : 0.0f;
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (isEnabled()) {
                g2.setColor(interpolateColor(toggleOffColor, accentColor, animationProgress));
            } else {
                g2.setColor(borderColor);
            }

            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));

            int padding = 3;
            int knobSize = h - (padding * 2);
            double knobX = padding + (w - knobSize - (padding * 2)) * animationProgress;

            g2.setColor(isEnabled() ? Color.WHITE : textSecondary);
            g2.fill(new Ellipse2D.Double(knobX, padding, knobSize, knobSize));
        }

        private Color interpolateColor(Color c1, Color c2, float fraction) {
            int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * fraction);
            int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * fraction);
            int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * fraction);
            return new Color(r, g, b);
        }
    }
}