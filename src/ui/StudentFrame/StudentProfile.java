package ui.StudentFrame;

import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.dashboard.ChangePasswordDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class StudentProfile extends JFrame {

    private String rollNumber;
    private String username;

    // --- UNIFIED COLOR PALETTE (Matches StudentDashboard) ---
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
    private List<RoundedButton> tabButtons; // To manage active state

    // Card Names
    private final String PERSONAL_CARD = "Personal Details";
    private final String ACADEMIC_CARD = "Academic";
    private final String FAMILY_CARD = "Family";
    private final String CONTACT_CARD = "Contact Details";

    public StudentProfile(String rollNumber, String username) {
        super("Student Profile - " + username);
        this.rollNumber = rollNumber;
        this.username = username;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full Screen
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new GridBagLayout()); // Use GridBag for the main layout

        this.tabButtons = new ArrayList<>();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 30, 30, 30); // Outer padding
        gbc.fill = GridBagConstraints.BOTH;

        // 1. Left Sidebar (Profile Photo, Basic Info, Actions)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.25; // 25% width
        gbc.weighty = 1.0;
        add(createLeftSidebar(), gbc);

        // 2. Right Content Area (Tabs + Details)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.75; // 75% width
        gbc.weighty = 1.0;
        add(createRightContentPanel(), gbc);
    }

    /**
     * Creates the left sidebar styled as a card.
     */
    private JPanel createLeftSidebar() {
        RoundedPanel panel = new RoundedPanel(20, cardColor, borderColor, 1);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        // --- Profile Picture Placeholder ---
        // Creating a circular panel simulation
        JPanel photoContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoContainer.setOpaque(false);

        // High radius RoundedPanel to look like a circle
        RoundedPanel photoCircle = new RoundedPanel(100, buttonColor, buttonColorGlow);
        photoCircle.setPreferredSize(new Dimension(150, 150));
        photoCircle.setLayout(new GridBagLayout()); // To center the text

        JLabel initialLabel = new JLabel(username.substring(0, 1).toUpperCase());
        initialLabel.setFont(new Font("Segoe UI", Font.BOLD, 64));
        initialLabel.setForeground(textColor);
        photoCircle.add(initialLabel);

        photoContainer.add(photoCircle);
        panel.add(photoContainer);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- Basic Info ---
        panel.add(createSidebarLabel("Roll Number", rollNumber));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(createSidebarLabel("Username", username));

        panel.add(Box.createVerticalGlue()); // Push buttons to bottom

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
            ChangePasswordDialog dialog = new ChangePasswordDialog(StudentProfile.this, username);
            dialog.setVisible(true);
        });

        panel.add(changePassBtn);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        RoundedButton closeBtn = new RoundedButton(
                "Close Profile",
                cardColor,
                borderColor, // hover
                borderColor.darker(), // pressed
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

    /**
     * Creates the right side: Tabs on top, Card content below.
     */
    private JPanel createRightContentPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 20));
        container.setOpaque(false);

        // 1. Navigation Tabs
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        tabsPanel.setOpaque(false);

        // Create the tabs
        tabsPanel.add(createTabButton(PERSONAL_CARD, true)); // Active by default
        tabsPanel.add(createTabButton(ACADEMIC_CARD, false));
        tabsPanel.add(createTabButton(FAMILY_CARD, false));
        tabsPanel.add(createTabButton(CONTACT_CARD, false));

        container.add(tabsPanel, BorderLayout.NORTH);

        // 2. Card Content Area
        cardLayout = new CardLayout();
        cardContentPanel = new JPanel(cardLayout);
        cardContentPanel.setOpaque(false); // Transparent so we see rounded panels inside

        cardContentPanel.add(createDetailsCard(PERSONAL_CARD), PERSONAL_CARD);
        cardContentPanel.add(createDetailsCard(ACADEMIC_CARD), ACADEMIC_CARD);
        cardContentPanel.add(createDetailsCard(FAMILY_CARD), FAMILY_CARD);
        cardContentPanel.add(createDetailsCard(CONTACT_CARD), CONTACT_CARD);

        container.add(cardContentPanel, BorderLayout.CENTER);

        return container;
    }

    /**
     * Creates a styled tab button using RoundedButton.
     */
    private RoundedButton createTabButton(String text, boolean isActive) {
        // Constructor: text, normal, hover, pressed, activeStart, activeEnd, arc
        RoundedButton btn = new RoundedButton(
                text,
                cardColor,          // Normal Background
                cardColor.brighter(), // Hover
                cardColor.darker(),   // Pressed
                buttonColor,          // Active Gradient Start
                buttonColorGlow,      // Active Gradient End
                15                    // Arc
        );

        btn.setPreferredSize(new Dimension(180, 45));
        btn.setActive(isActive);

        if(isActive) {
            btn.setForeground(textColor);
        } else {
            btn.setForeground(textSecondaryColor);
        }

        btn.addActionListener(e -> {
            // 1. Switch Card
            cardLayout.show(cardContentPanel, text);

            // 2. Update Visual State
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

    /**
     * Creates the actual content panel for a specific category.
     */
    private JPanel createDetailsCard(String category) {
        RoundedPanel card = new RoundedPanel(20, cardColor, borderColor, 1);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Content Wrapper
        Box contentBox = Box.createVerticalBox();

        // Title
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

        // Add fields based on category
        switch (category) {
            case PERSONAL_CARD:
                contentBox.add(createDataRow("Date of Birth", "NA"));
                contentBox.add(createDataRow("Contact Number", "NA"));
                contentBox.add(createDataRow("Unique ID", "NA"));
                contentBox.add(Box.createRigidArea(new Dimension(0, 20)));
                JLabel subTitle = new JLabel("Additional Details");
                subTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
                subTitle.setForeground(buttonColor); // Accent color
                subTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentBox.add(subTitle);
                contentBox.add(Box.createRigidArea(new Dimension(0, 15)));
                contentBox.add(createDataRow("Gender", "NA"));
                contentBox.add(createDataRow("Blood Group", "NA"));
                contentBox.add(createDataRow("Nationality", "NA"));
                break;
            case ACADEMIC_CARD:
                contentBox.add(createDataRow("Program", "B.Tech (CSE)"));
                contentBox.add(createDataRow("Current Semester", "Semester 3"));
                contentBox.add(createDataRow("Batch", "2024"));
                contentBox.add(createDataRow("Enrollment No", rollNumber));
                break;
            case FAMILY_CARD:
                contentBox.add(createDataRow("Father's Name", "NA"));
                contentBox.add(createDataRow("Mother's Name", "NA"));
                contentBox.add(createDataRow("Guardian Contact", "NA"));
                break;
            case CONTACT_CARD:
                contentBox.add(createDataRow("Official Email", username + "@iiitd.ac.in"));
                contentBox.add(createDataRow("Personal Email", "NA"));
                contentBox.add(createDataRow("Permanent Address", "NA"));
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

    /**
     * Helper to create a styled row: "Label ......... Value"
     */
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

        // Add a bottom border line for separation
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(row);

        JSeparator line = new JSeparator();
        line.setForeground(new Color(255,255,255, 30)); // Very subtle divider
        line.setBackground(cardColor);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        wrapper.add(line);

        return wrapper;
    }
}