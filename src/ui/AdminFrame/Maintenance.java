package ui.AdminFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.dashboard.AdminDashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Maintenance extends JFrame {

    // --- Color Palette (Matched to AdminDashboard) ---
    private Color bgColor = new Color(42, 48, 60);            // Background
    private Color cardColor = new Color(54, 59, 74);          // Card/Panel
    private Color textColor = new Color(255, 255, 255);       // Text
    private Color textSecondary = new Color(179, 179, 179);   // Subtext
    private Color accentColor = new Color(52, 159, 148);      // Teal/Primary
    private Color accentGlow = new Color(79, 196, 184);       // Lighter Teal
    private Color dangerColor = new Color(190, 60, 60);       // Red for "Offline"
    private Color borderColor = new Color(64, 69, 89);

    // --- Components ---
    private ModernToggle masterSwitch;
    private JLabel statusLabel;
    private JPanel optionsContainer;
    private JTextArea messageArea;
    private List<ModernToggle> subToggles;

    public Maintenance() {
        setTitle("System Maintenance Control");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full Screen
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Closes this frame, not app
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        subToggles = new ArrayList<>();

        // 1. Header Section
        add(createHeader(), BorderLayout.NORTH);

        // 2. Main Content (Scrollable)
        JScrollPane scrollPane = createMainContent();
        add(scrollPane, BorderLayout.CENTER);

        // 3. Footer (Action Buttons)
        add(createFooter(), BorderLayout.SOUTH);
    }

    /**
     * Creates the top header with the Back button.
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bgColor);
        header.setBorder(new EmptyBorder(25, 30, 10, 30));

        // Title
        JLabel title = new JLabel("Maintenance Configuration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor);

        header.add(title, BorderLayout.WEST);

        return header;
    }

    /**
     * Creates the central scrollable content area.
     */
    private JScrollPane createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(20, 100, 40, 100)); // High padding for centered look

        // --- Section A: Master Control ---
        RoundedPanel masterPanel = new RoundedPanel(20, cardColor, borderColor, 1);
        masterPanel.setLayout(new BorderLayout());
        masterPanel.setMaximumSize(new Dimension(1000, 120));
        masterPanel.setPreferredSize(new Dimension(1000, 120));
        masterPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Text Info
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        JLabel masterLabel = new JLabel("Maintenance Mode");
        masterLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        masterLabel.setForeground(textColor);

        statusLabel = new JLabel("System is currently LIVE (Online)");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(accentColor); // Starts green/teal

        textPanel.add(masterLabel);
        textPanel.add(statusLabel);

        // Master Switch
        masterSwitch = new ModernToggle();
        masterSwitch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleMasterState(masterSwitch.isSelected());
            }
        });

        masterPanel.add(textPanel, BorderLayout.CENTER);
        masterPanel.add(masterSwitch, BorderLayout.EAST);

        // --- Section B: Configuration Options ---
        optionsContainer = new JPanel();
        optionsContainer.setLayout(new BoxLayout(optionsContainer, BoxLayout.Y_AXIS));
        optionsContainer.setOpaque(false);

        // Add specific options (Hardcoded as requested, but realistic)
        optionsContainer.add(createOptionRow("Option 1: Student Login Portal", "Allow students to log in."));
        optionsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        optionsContainer.add(createOptionRow("Option 2: Faculty Grading System", "Allow faculty to enter marks."));
        optionsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        optionsContainer.add(createOptionRow("Option 3: Course Registration", "Open/Close course enrollment."));
        optionsContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        optionsContainer.add(createOptionRow("Option 4: Library Database", "Access to digital library assets."));

        // --- Section C: Custom Message ---
        JPanel msgPanelContainer = new JPanel(new BorderLayout());
        msgPanelContainer.setOpaque(false);
        msgPanelContainer.setMaximumSize(new Dimension(1000, 150));

        JLabel msgLabel = new JLabel("Maintenance Message (Visible to Users)");
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

        // Add all to main panel
        mainPanel.add(masterPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        JLabel settingsHeader = new JLabel("Access Control Settings");
        settingsHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        settingsHeader.setForeground(textSecondary);
        settingsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerWrapper = new JPanel(new BorderLayout()); // Wrapper to help alignment
        headerWrapper.setOpaque(false);
        headerWrapper.setMaximumSize(new Dimension(1000, 30));
        headerWrapper.add(settingsHeader, BorderLayout.WEST);

        mainPanel.add(headerWrapper);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(optionsContainer);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(msgPanelContainer);

        // Initial State: Disable options because master is OFF (Live)
        toggleOptions(false);

        // Scroll Pane Wrapper
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Make scrollbar aesthetic if desired (reusing the one from dashboard or default)

        return scrollPane;
    }

    /**
     * Helper to create a single option row (Option Name + Toggle).
     */
    private JPanel createOptionRow(String title, String subtitle) {
        RoundedPanel row = new RoundedPanel(15, cardColor, borderColor, 1);
        row.setLayout(new BorderLayout());
        row.setMaximumSize(new Dimension(1000, 80));
        row.setPreferredSize(new Dimension(1000, 80));
        row.setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel tLabel = new JLabel(title);
        tLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tLabel.setForeground(textColor);

        JLabel sLabel = new JLabel(subtitle);
        sLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sLabel.setForeground(textSecondary);

        textPanel.add(tLabel);
        textPanel.add(sLabel);

        ModernToggle subToggle = new ModernToggle();
        // Add labels to the toggle for clarity
        subToggle.setLabels("Allow", "Disallow");
        subToggles.add(subToggle); // Add to list for bulk management

        row.add(textPanel, BorderLayout.CENTER);
        row.add(subToggle, BorderLayout.EAST);

        return row;
    }

    /**
     * Creates the footer with Save/Back buttons.
     */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footer.setBackground(bgColor);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor));

        RoundedButton backBtn = new RoundedButton("Back to Dashboard", bgColor, borderColor, cardColor, bgColor, 10);
        backBtn.setForeground(textColor);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setPreferredSize(new Dimension(180, 45));
        backBtn.addActionListener(e -> {
            // Assuming AdminDashboard expects to be shown again
            new AdminDashboard("ADMIN01", "Admin").setVisible(true);
            this.dispose();
        });

        RoundedButton saveBtn = new RoundedButton("Apply Changes", accentColor, accentGlow, accentColor.darker(), accentColor, 10);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(180, 45));
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Maintenance settings updated successfully.");
        });

        footer.add(backBtn);
        footer.add(saveBtn);

        return footer;
    }

    /**
     * Logic to handle what happens when Master Switch is toggled.
     */
    private void toggleMasterState(boolean isMaintenanceOn) {
        if (isMaintenanceOn) {
            statusLabel.setText("System is UNDER MAINTENANCE (Offline)");
            statusLabel.setForeground(dangerColor);
            toggleOptions(true); // Enable editing options
        } else {
            statusLabel.setText("System is currently LIVE (Online)");
            statusLabel.setForeground(accentColor);
            toggleOptions(false); // Disable/Hide options
        }
    }

    /**
     * Enables or Disables the visual state of the sub-options.
     */
    private void toggleOptions(boolean enable) {
        for (Component comp : optionsContainer.getComponents()) {
            if (comp instanceof RoundedPanel) {
                setPanelEnabled((RoundedPanel) comp, enable);
            }
        }
        optionsContainer.repaint();
    }

    private void setPanelEnabled(JPanel panel, boolean isEnabled) {
        // Dim the panel color slightly if disabled
        // Iterate recursively to disable components
        for (Component c : panel.getComponents()) {
            c.setEnabled(isEnabled);
            if (c instanceof JPanel) {
                setPanelEnabled((JPanel) c, isEnabled);
            }
        }
    }

    // =================================================================================
    // --- CUSTOM COMPONENT: Modern Toggle Switch ---
    // =================================================================================
    class ModernToggle extends JComponent {
        private boolean selected = false;
        private Timer timer;
        private float animationProgress = 0f; // 0.0 to 1.0
        private String labelOn = "ON";
        private String labelOff = "OFF";

        public ModernToggle() {
            setPreferredSize(new Dimension(100, 34));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) {
                        setSelected(!selected);
                    }
                }
            });

            // Simple animation timer
            timer = new Timer(10, e -> {
                if (selected && animationProgress < 1.0f) {
                    animationProgress += 0.1f;
                    if (animationProgress > 1.0f) animationProgress = 1.0f;
                    repaint();
                } else if (!selected && animationProgress > 0.0f) {
                    animationProgress -= 0.1f;
                    if (animationProgress < 0.0f) animationProgress = 0.0f;
                    repaint();
                } else {
                    ((Timer)e.getSource()).stop();
                }
            });
        }

        public void setLabels(String on, String off) {
            this.labelOn = on;
            this.labelOff = off;
        }

        public boolean isSelected() { return selected; }
        public void setSelected(boolean s) {
            this.selected = s;
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = h;

            // 1. Draw Track
            if (isEnabled()) {
                g2.setColor(selected ? accentColor : cardColor.darker());
            } else {
                g2.setColor(bgColor); // Dimmed if disabled
            }

            if (!isEnabled() && !selected) g2.setColor(borderColor); // Visible border when disabled off

            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));

            // 2. Draw Label Text inside track
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();

            // Draw "ON" text on the left
            if (animationProgress > 0.5) {
                g2.drawString(labelOn, 15, (h + fm.getAscent()) / 2 - 2);
            }
            // Draw "OFF" text on the right
            else {
                g2.drawString(labelOff, w - fm.stringWidth(labelOff) - 15, (h + fm.getAscent()) / 2 - 2);
            }

            // 3. Draw Knob
            int knobSize = h - 6;
            int padding = 3;
            // Calculate X position based on animation
            double knobX = padding + (w - knobSize - padding * 2) * animationProgress;

            g2.setColor(Color.WHITE);
            if (!isEnabled()) g2.setColor(textSecondary);

            g2.fill(new Ellipse2D.Double(knobX, padding, knobSize, knobSize));
        }
    }
}