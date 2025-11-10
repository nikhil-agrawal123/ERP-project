package ui.StudentFrame;

import ui.dashboard.ChangePasswordDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentProfile extends JFrame {

    // These fields store the data passed from the dashboard
    private String rollNumber; // e.g., "2024380"
    private String username;   // e.g., "nikhil24380" (this is the auth ID)

    // Use consistent colors from the dashboard
    private Color bgColor = new Color(45, 45, 45);
    private Color mainPanelColor = new Color(50, 50, 50);
    private Color textColor = Color.WHITE;
    private Color accentColor = new Color(100, 100, 100);
    private Color buttonColor = new Color(57, 174, 168);

    // Components for the card layout
    private JPanel cardContentPanel;
    private CardLayout cardLayout;

    // Card Names
    private final String PERSONAL_CARD = "Personal Details";
    private final String ACADEMIC_CARD = "Academic";
    private final String FAMILY_CARD = "Family";
    private final String CONTACT_CARD = "Contact Details";


    public StudentProfile(String rollNumber, String username) {
        super("Student Profile - " + username);
        this.rollNumber = rollNumber;
        this.username = username;

        // Set properties for the new frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1080, 720);
        setLocationRelativeTo(null); // Center
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        // Main panel that will hold the new GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(mainPanelColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();

        // 1. Left Panel (Photo + Details)
        JPanel leftPanel = createLeftPanel();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.3; // Give 30% width to left panel
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 10); // Padding to the right
        mainPanel.add(leftPanel, c);

        // 2. Vertical Separator
        JPanel separatorPanel = new JPanel();
        separatorPanel.setBackground(Color.WHITE);
        separatorPanel.setPreferredSize(new Dimension(2, 0)); // Width of 2px, height will stretch
        c.gridx = 1;
        c.weightx = 0; // No extra width
        c.fill = GridBagConstraints.VERTICAL;
        c.insets = new Insets(0, 0, 0, 10); // Padding to the right
        mainPanel.add(separatorPanel, c);

        // 3. Right Panel (Nav Tabs + Content)
        JPanel rightPanel = createRightPanel();
        c.gridx = 2;
        c.weightx = 0.7; // Give 70% width to right panel
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0); // No padding
        mainPanel.add(rightPanel, c);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(mainPanelColor);
        panel.setOpaque(false); // Let main panel color show through

        // 1. Passport Photo Placeholder
        JPanel photoPanel = new JPanel();
        photoPanel.setBackground(accentColor);
        photoPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        photoPanel.setPreferredSize(new Dimension(180, 220)); // Passport-like-ish size

        JLabel photoLabel = new JLabel("Photo Placeholder");
        photoLabel.setForeground(textColor);
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoPanel.add(photoLabel);

        // Use a container to keep the photo panel from stretching
        JPanel photoContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoContainer.setOpaque(false);
        photoContainer.add(photoPanel);
        panel.add(photoContainer, BorderLayout.NORTH);

        // 2. Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setBackground(mainPanelColor);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        JLabel rollLabel = new JLabel("Roll Number:");
        rollLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rollLabel.setForeground(textColor);
        rollLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // This is the display roll number (e.g., "2024380")
        JLabel rollValue = new JLabel(rollNumber);
        rollValue.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rollValue.setForeground(textColor);
        rollValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        rollValue.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Padding below

        JLabel userLabel = new JLabel("Username / Auth ID:"); // Clarified label
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(textColor);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // This is the auth username (e.g., "nikhil24380")
        JLabel userValue = new JLabel(username);
        userValue.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userValue.setForeground(textColor);
        userValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        userValue.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); // Added more padding below

        detailsPanel.add(rollLabel);
        detailsPanel.add(rollValue);
        detailsPanel.add(userLabel);
        detailsPanel.add(userValue);

        // Add "Change Password" button
        JButton changePassButton = createMenuButton("Change Password");
        changePassButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        changePassButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ---
        // --- THIS IS THE MODIFIED ACTION LISTENER ---
        // ---
        changePassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create and show the new dialog
                // 'StudentProfile.this' is the parent frame
                // 'username' is the auth ID (e.g., "nikhil24380") needed by the service
                ChangePasswordDialog dialog = new ChangePasswordDialog(StudentProfile.this, username);
                dialog.setVisible(true);
            }
        });

        detailsPanel.add(changePassButton);

        // Add glue to push button to the top
        detailsPanel.add(Box.createVerticalGlue());

        panel.add(detailsPanel, BorderLayout.CENTER);

        return panel;
    }

    // --- (Rest of your StudentProfile.java class is unchanged) ---

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // 1. Nav Tabs Panel
        JPanel navTabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navTabsPanel.setBackground(mainPanelColor);

        // Use a ButtonGroup to make buttons act like tabs (only one selected)
        ButtonGroup tabGroup = new ButtonGroup();
        ActionListener tabListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the button's text, which is the card name
                cardLayout.show(cardContentPanel, e.getActionCommand());
            }
        };

        // Create buttons
        JToggleButton personalBtn = createNavButton(PERSONAL_CARD, tabListener);
        JToggleButton academicBtn = createNavButton(ACADEMIC_CARD, tabListener);
        JToggleButton familyBtn = createNavButton(FAMILY_CARD, tabListener);
        JToggleButton contactBtn = createNavButton(CONTACT_CARD, tabListener);

        // Add to group and panel
        tabGroup.add(personalBtn);
        tabGroup.add(academicBtn);
        tabGroup.add(familyBtn);
        tabGroup.add(contactBtn);

        navTabsPanel.add(personalBtn);
        navTabsPanel.add(academicBtn);
        navTabsPanel.add(familyBtn);
        navTabsPanel.add(contactBtn);

        // Select the first tab by default
        personalBtn.setSelected(true);

        panel.add(navTabsPanel, BorderLayout.NORTH);

        // 2. Card Content Panel (where content changes)
        cardLayout = new CardLayout();
        cardContentPanel = new JPanel(cardLayout);
        cardContentPanel.setOpaque(false);
        cardContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Add the different content cards
        cardContentPanel.add(createDetailsPanel(PERSONAL_CARD), PERSONAL_CARD);
        cardContentPanel.add(createDetailsPanel(ACADEMIC_CARD), ACADEMIC_CARD);
        cardContentPanel.add(createDetailsPanel(FAMILY_CARD), FAMILY_CARD);
        cardContentPanel.add(createDetailsPanel(CONTACT_CARD), CONTACT_CARD);

        panel.add(cardContentPanel, BorderLayout.CENTER);

        // Show the default card
        cardLayout.show(cardContentPanel, PERSONAL_CARD);

        return panel;
    }

    private JToggleButton createNavButton(String text, ActionListener listener) {
        JToggleButton button = new JToggleButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBackground(accentColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setActionCommand(text); // Set command to the card name
        button.addActionListener(listener);

        // Simple styling for selected state
        button.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                button.setBackground(new Color(80, 80, 80)); // Darker when selected
            } else {
                button.setBackground(accentColor); // Reset when deselected
            }
        });

        return button;
    }

    // Helper method to create placeholder "NA" panels
    private JPanel createDetailsPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(mainPanelColor);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel);

        // Add "NA" fields based on the title
        switch (title) {
            case PERSONAL_CARD:
                panel.add(createNAField("E-mail:"));
                panel.add(createNAField("Date of Birth:"));
                panel.add(createNAField("Contact Number:"));
                panel.add(createNAField("Unique Identification Number:"));

                // Add vertical space before the new section
                panel.add(Box.createRigidArea(new Dimension(0, 20)));

                // Add "Additional Details" sub-title
                JLabel additionalDetailsLabel = new JLabel("Additional Details");
                additionalDetailsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Slightly smaller than main title
                additionalDetailsLabel.setForeground(textColor);
                additionalDetailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                additionalDetailsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // Padding below
                panel.add(additionalDetailsLabel);

                // Add fields for the new section
                panel.add(createNAField("Marital Status:"));
                panel.add(createNAField("Blood Group:"));
                panel.add(createNAField("Nationality:"));
                panel.add(createNAField("Gender:")); // Added new "Gender" field

                break;
            case ACADEMIC_CARD:
                panel.add(createNAField("Program:"));
                panel.add(createNAField("Batch:"));
                panel.add(createNAField("Enrollment No:"));
                break;
            case FAMILY_CARD:
                panel.add(createNAField("Father's Name:"));
                panel.add(createNAField("Mother's Name:"));
                panel.add(createNAField("Guardian's Contact:"));
                break;
            case CONTACT_CARD:
                panel.add(createNAField("Email:"));
                panel.add(createNAField("Contact No:"));
                panel.add(createNAField("Permanent Address:"));
                break;
            default:
                panel.add(new JLabel("Content for " + title));
        }

        panel.add(Box.createVerticalGlue()); // Pushes content to the top

        // Wrap in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(mainPanelColor);

        // Need a wrapper panel to return a JPanel, not a JScrollPane
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel createNAField(String label) {
        JLabel field = new JLabel(label + "\t NA");
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(textColor);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return field;
    }
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }
}