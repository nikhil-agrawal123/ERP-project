package ui.FacultyFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.dashboard.ChangePasswordDialog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyProfile extends JFrame {

    private String facultyID;
    private String username;

    // --- UNIFIED COLOR PALETTE ---
    private Color bgColor = new Color(42, 48, 60);            // --background
    private Color cardColor = new Color(54, 59, 74);          // --card
    private Color borderColor = new Color(64, 69, 89);        // --border
    private Color buttonColor = new Color(52, 159, 148);      // --primary
    private Color buttonColorGlow = new Color(79, 196, 184);  // --primary-glow
    private Color textColor = new Color(255, 255, 255);       // --foreground
    private Color textSecondaryColor = new Color(179, 179, 179); // --muted-foreground

    // Components
    private JPanel cardContentPanel;
    private CardLayout cardLayout;
    private List<RoundedButton> tabButtons;

    // Card Names
    private final String PERSONAL_CARD = "Personal Details";
    private final String PROFESSIONAL_CARD = "Professional";
    private final String CONTACT_CARD = "Contact Details";

    public FacultyProfile(String facultyID, String username) {
        super("Faculty Profile - " + username);
        this.facultyID = facultyID;
        this.username = username;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new GridBagLayout());

        this.tabButtons = new ArrayList<>();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 30, 30, 30);
        gbc.fill = GridBagConstraints.BOTH;

        // 1. Left Sidebar
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.25;
        gbc.weighty = 1.0;
        add(createLeftSidebar(), gbc);

        // 2. Right Content Area
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.75;
        gbc.weighty = 1.0;
        add(createRightContentPanel(), gbc);
    }

    private JPanel createLeftSidebar() {
        RoundedPanel panel = new RoundedPanel(20, cardColor, borderColor, 1);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        // --- Profile Picture Placeholder ---
        JPanel photoContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoContainer.setOpaque(false);

        RoundedPanel photoCircle = new RoundedPanel(100, buttonColor, buttonColorGlow);
        photoCircle.setPreferredSize(new Dimension(150, 150));
        photoCircle.setLayout(new GridBagLayout());

        JLabel initialLabel = new JLabel(username.substring(0, 1).toUpperCase());
        initialLabel.setFont(new Font("Segoe UI", Font.BOLD, 64));
        initialLabel.setForeground(textColor);
        photoCircle.add(initialLabel);

        photoContainer.add(photoCircle);
        panel.add(photoContainer);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- Basic Info ---
        panel.add(createSidebarLabel("Faculty ID", facultyID)); // Changed label
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(createSidebarLabel("Username", username));

        panel.add(Box.createVerticalGlue());

        // --- Action Buttons ---
        RoundedButton changePassBtn = new RoundedButton(
                "Change Password",
                buttonColor,
                buttonColorGlow,
                10
        );
        changePassBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        changePassBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        changePassBtn.addActionListener(e -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(FacultyProfile.this, username);
            dialog.setVisible(true);
        });

        panel.add(changePassBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        RoundedButton closeBtn = new RoundedButton(
                "Close Profile",
                cardColor,
                borderColor,
                borderColor.darker(),
                10
        );
        closeBtn.setForeground(textSecondaryColor);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        closeBtn.addActionListener(e -> dispose());

        panel.add(closeBtn);

        return panel;
    }

    private JPanel createSidebarLabel(String title, String value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(textSecondaryColor);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 20));
        v.setForeground(textColor);
        v.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(t);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(v);
        return p;
    }

    private JPanel createRightContentPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 20));
        container.setOpaque(false);

        // 1. Navigation Tabs
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        tabsPanel.setOpaque(false);

        tabsPanel.add(createTabButton(PERSONAL_CARD, true));
        tabsPanel.add(createTabButton(PROFESSIONAL_CARD, false));
        tabsPanel.add(createTabButton(CONTACT_CARD, false));

        container.add(tabsPanel, BorderLayout.NORTH);

        // 2. Card Content Area
        cardLayout = new CardLayout();
        cardContentPanel = new JPanel(cardLayout);
        cardContentPanel.setOpaque(false);

        cardContentPanel.add(createDetailsCard(PERSONAL_CARD), PERSONAL_CARD);
        cardContentPanel.add(createDetailsCard(PROFESSIONAL_CARD), PROFESSIONAL_CARD);
        cardContentPanel.add(createDetailsCard(CONTACT_CARD), CONTACT_CARD);

        container.add(cardContentPanel, BorderLayout.CENTER);

        return container;
    }

    private RoundedButton createTabButton(String text, boolean isActive) {
        RoundedButton btn = new RoundedButton(
                text,
                cardColor,
                cardColor.brighter(),
                cardColor.darker(),
                buttonColor,
                buttonColorGlow,
                15
        );

        btn.setPreferredSize(new Dimension(180, 45));
        btn.setActive(isActive);
        btn.setForeground(isActive ? textColor : textSecondaryColor);

        btn.addActionListener(e -> {
            cardLayout.show(cardContentPanel, text);
            for (RoundedButton b : tabButtons) {
                b.setActive(false);
                b.setForeground(textSecondaryColor);
            }
            btn.setActive(true);
            btn.setForeground(textColor);
        });

        tabButtons.add(btn);
        return btn;
    }

    private JPanel createDetailsCard(String category) {
        RoundedPanel card = new RoundedPanel(20, cardColor, borderColor, 1);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        Box contentBox = Box.createVerticalBox();

        JLabel title = new JLabel(category);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentBox.add(title);
        contentBox.add(Box.createRigidArea(new Dimension(0, 10)));

        JSeparator sep = new JSeparator();
        sep.setForeground(borderColor);
        sep.setBackground(cardColor);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentBox.add(sep);
        contentBox.add(Box.createRigidArea(new Dimension(0, 25)));

        // --- Custom Fields for Faculty ---
        switch (category) {
            case PERSONAL_CARD:
                contentBox.add(createDataRow("Full Name", username));
                contentBox.add(createDataRow("Date of Birth", "NA"));
                contentBox.add(createDataRow("Gender", "NA"));
                contentBox.add(createDataRow("Nationality", "Indian"));
                break;
            case PROFESSIONAL_CARD:
                contentBox.add(createDataRow("Designation", "Assistant Professor"));
                contentBox.add(createDataRow("Department", "Computer Science (CSE)"));
                contentBox.add(createDataRow("Faculty ID", facultyID));
                contentBox.add(createDataRow("Joining Date", "NA"));
                break;
            case CONTACT_CARD:
                contentBox.add(createDataRow("Official Email", username + "@iiitd.ac.in"));
                contentBox.add(createDataRow("Office Location", "R&D Block, Room 204"));
                contentBox.add(createDataRow("Office Extension", "NA"));
                break;
        }

        contentBox.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentBox);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createDataRow(String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(textSecondaryColor);

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        value.setForeground(textColor);

        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(row);

        JSeparator line = new JSeparator();
        line.setForeground(new Color(255,255,255, 30));
        line.setBackground(cardColor);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        wrapper.add(line);

        return wrapper;
    }
}