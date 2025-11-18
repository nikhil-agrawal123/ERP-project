package ui.dashboard;

import ui.AdminFrame.AddUser;
import ui.AdminFrame.AdminManageCourses;
import ui.AdminFrame.RemoveUser;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import ui.landing.LandingFrame;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {

    // --- New UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);            // --background
    private Color sideMenuColor = new Color(48, 54, 70);      // --sidebar-background
    private Color mainPanelColor = new Color(42, 48, 60);       // --background
    private Color cardColor = new Color(54, 59, 74);          // --card
    private Color popoverColor = new Color(46, 52, 66);       // --popover
    private Color borderColor = new Color(64, 69, 89);        // --border
    private Color buttonColor = new Color(52, 159, 148);      // --primary / --accent
    private Color buttonColorGlow = new Color(79, 196, 184);  // --primary-glow
    private Color textColor = new Color(255, 255, 255);       // --foreground
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color logoutRedHover = new Color(190, 60, 60);
    private Color logoutRedPressed = new Color(160, 40, 40);

    private String adminID;
    private String username;

    private JLayeredPane mainLayeredPane;
    private JPanel cardHolderPanel;
    private CardLayout cardLayout;

    // --- NEW ---
    // List to manage the active state of right-panel buttons
    private List<RoundedButton> rightPanelButtons;

    public AdminDashboard(String adminID, String username) {
        super("Admin Dashboard - " + username);
        this.adminID = adminID;
        this.username = username;

        // --- NEW ---
        this.rightPanelButtons = new ArrayList<>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full screen
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
        setIconImage(image.getImage());

        setLayout(new BorderLayout());

        // --- Main Content Area ---
        mainLayeredPane = new JLayeredPane();
        mainLayeredPane.setBackground(mainPanelColor);
        add(mainLayeredPane, BorderLayout.CENTER);

        cardLayout = new CardLayout();
        cardHolderPanel = new JPanel(cardLayout);
        cardHolderPanel.setOpaque(false);
        mainLayeredPane.add(cardHolderPanel, JLayeredPane.DEFAULT_LAYER);

        // --- Profile Button REMOVED ---

        // --- Component Resizing Listener ---
        mainLayeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = e.getComponent().getSize();
                cardHolderPanel.setBounds(0, 0, size.width, size.height);
                // Profile button resizing logic removed
            }
        });

        // --- Create Content Cards ---
        createContentCards(cardHolderPanel, username);
        cardLayout.show(cardHolderPanel, "HOME");

        // --- MODIFIED --- Set the first button as active by default
        if (!rightPanelButtons.isEmpty()) {
            setActiveRightButton(rightPanelButtons.get(0));
        }
    }

    /**
     * Creates the main "HOME" panel and adds it to the card holder.
     */
    private void createContentCards(JPanel cardHolder, String username) {
        JPanel homePanel = new JPanel(new BorderLayout(0, 0));
        homePanel.setBackground(mainPanelColor);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 40, 40));

        // --- 1. Title Panel (North) ---
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(mainPanelColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(textColor);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idLabel = new JLabel("Admin ID: " + this.adminID);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setForeground(textSecondaryColor);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(idLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        titleSeparator.setForeground(borderColor);
        titleSeparator.setBackground(mainPanelColor);
        titleSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        titlePanel.add(titleSeparator);

        homePanel.add(titlePanel, BorderLayout.NORTH);

        // --- 2. Main Content Panel (Center) ---
        // This panel holds the original layout (Profile West, Functions Center, Buttons East)
        JPanel mainContentContainer = new JPanel(new BorderLayout(0, 0));
        mainContentContainer.setOpaque(false);

        // --- 2a. Right Side Panel (East) ---
        JPanel rightSidePanel = createRightSidePanel();
        mainContentContainer.add(rightSidePanel, BorderLayout.EAST);

        // --- 2b. Center and Left Container ---
        JPanel centerAndLeftContainer = new JPanel(new BorderLayout(0, 0));
        centerAndLeftContainer.setOpaque(false);

        JPanel profilePanel = createAdminProfilePanel(username);
        centerAndLeftContainer.add(profilePanel, BorderLayout.WEST);

        JPanel functionPanel = createFunctionPanel();
        centerAndLeftContainer.add(functionPanel, BorderLayout.CENTER);

        mainContentContainer.add(centerAndLeftContainer, BorderLayout.CENTER);

        // --- 3. Add to Scroll Pane ---
        // Wrap the main content in a scroll pane (with hidden bars)
        JScrollPane mainScrollPane = createMainScrollPane(mainContentContainer);
        homePanel.add(mainScrollPane, BorderLayout.CENTER);

        cardHolder.add(homePanel, "HOME");

        // Add other panels here if needed (e.g., "MANAGE_USERS")
        // cardHolder.add(createManageUsersPanel(), "MANAGE_USERS");
    }

    /**
     * Creates the Admin Profile panel (Original WEST position).
     */
    private JPanel createAdminProfilePanel(String username) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor); // Use side menu color for contrast
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, borderColor),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // --- MODIFIED: Passport photo panel with gradient border ---
        JPanel passportPhotoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Call super.paintComponent to handle JPanel's default painting,
                // but we will draw over it, so setting Opaque to false for the panel itself might be better
                // super.paintComponent(g); // Removed or set setOpaque(false) for the panel
                Graphics2D g2d = (Graphics2D) g.create(); // Create a copy to not affect original Graphics object
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int borderWidth = 3; // Reduced border width

                // Draw gradient border
                GradientPaint gp = new GradientPaint(
                        0, 0, buttonColor,
                        getWidth(), getHeight(), buttonColorGlow);
                g2d.setPaint(gp);
                // Draw the outer rounded rectangle (border)
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));

                // Draw inner background, ensuring it doesn't leave white corners
                // This draws a slightly smaller rounded rectangle inside the border
                g2d.setColor(sideMenuColor); // Use sideMenuColor for the inner background
                g2d.fill(new RoundRectangle2D.Double(borderWidth, borderWidth,
                        getWidth() - 2 * borderWidth, getHeight() - 2 * borderWidth, 8, 8)); // Slightly smaller arc for inner

                g2d.dispose(); // Release Graphics resources
            }
        };
        passportPhotoPanel.setLayout(new BorderLayout());
        passportPhotoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passportPhotoPanel.setPreferredSize(new Dimension(180, 220)); // Passport photo aspect ratio
        passportPhotoPanel.setMaximumSize(new Dimension(180, 220));
        passportPhotoPanel.setOpaque(false); // Make the panel transparent so its default background doesn't show

        // Load and scale the image for the passport photo
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/logo.jpg"));
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(
                passportPhotoPanel.getPreferredSize().width - 10, // Some padding
                passportPhotoPanel.getPreferredSize().height - 10,
                Image.SCALE_SMOOTH);
        JLabel photoLabel = new JLabel(new ImageIcon(scaledImage));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        passportPhotoPanel.add(photoLabel, BorderLayout.CENTER);
        // --- END MODIFIED ---

        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("ID: " + this.adminID);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setForeground(textSecondaryColor);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(passportPhotoPanel); // Add the new photo panel
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(idLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }


    /**
     * Creates the Function panel with clickable cards (Original CENTER position).
     */
    private JPanel createFunctionPanel() {
        // Use GridBagLayout to anchor the grid of cards to the top-left
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // This panel holds the cards in a 3-column grid
        JPanel cardGridPanel = new JPanel(new GridLayout(0, 3, 25, 25)); // 0 rows, 3 cols, 25px gaps
        cardGridPanel.setOpaque(false); // Make it transparent

        // Create styled function cards
        JPanel addUserCard = createFunctionCard("Add Users");
        addUserCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                AddUser addUserPanel = new AddUser();
                JDialog addUserDialog = new JDialog(AdminDashboard.this, "Add New User", true); // 'true' makes it modal

                addUserDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                addUserDialog.getContentPane().add(addUserPanel); // Add your panel

                // Now that AddUser has a size, pack() will work!
                addUserDialog.pack();
                addUserDialog.setLocationRelativeTo(AdminDashboard.this);
                addUserDialog.setVisible(true);
            }
        });

        JPanel removeUsersCard = createFunctionCard("Remove Users");
        removeUsersCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    RemoveUser removeUserFrame = new RemoveUser(adminID, username);
                    removeUserFrame.setVisible(true);
                });

            }
        });

        JPanel manageCoursesCard = createFunctionCard("Manage Courses");
        manageCoursesCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    AdminManageCourses managecourseFrame = new AdminManageCourses(adminID, username, AdminDashboard.this);
                    managecourseFrame.setVisible(true);
                    AdminDashboard.this.setVisible(false);
                });
            }
        });

        JPanel maintenanceCard = createFunctionCard("Maintenance");
        maintenanceCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                createPlaceholderFrame("Maintenance");
            }
        });

        // --- REMOVED ---
        // Logout card has been removed

        // Add cards to the new grid panel
        cardGridPanel.add(addUserCard);
        cardGridPanel.add(removeUsersCard);
        cardGridPanel.add(manageCoursesCard);
        cardGridPanel.add(maintenanceCard);
        // cardGridPanel.add(logoutCard); // Removed

        // Add the grid panel to the main panel, anchored to the top-left
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Pin to top-left
        gbc.weightx = 1.0; // Allow panel to use horizontal space
        gbc.weighty = 1.0; // Allow panel to use vertical space

        panel.add(cardGridPanel, gbc);

        return panel;
    }

    /**
     * Creates the Right Side panel with buttons (Original EAST position).
     * --- MODIFIED TO MATCH STUDENTDASHBOARD ---
     */
    private JPanel createRightSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor); // Use side menu color
        // --- MODIFIED --- (Increased width, changed padding, removed MatteBorder)
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));


        // --- MODIFIED ---
        // Use the new button creation method that supports an active state
        RoundedButton b1 = createRightPanelButton("Home");
        RoundedButton b2 = createRightPanelButton("View Logs");
        RoundedButton b3 = createRightPanelButton("Settings");
        RoundedButton b4 = createRightPanelButton("Reports");

        b1.addActionListener(e -> {
            setActiveRightButton(b1);
            System.out.println("Home pressed");
            // cardLayout.show(cardHolderPanel, "HOME"); // Example
        });
        b2.addActionListener(e -> {
            setActiveRightButton(b2);
            System.out.println("View Logs pressed");
        });
        b3.addActionListener(e -> {
            setActiveRightButton(b3);
            System.out.println("Settings pressed");
        });
        b4.addActionListener(e -> {
            setActiveRightButton(b4);
            System.out.println("Reports pressed");
        });


        // --- MODIFIED --- (Spacing changed from 15 to 10)
        panel.add(b1);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(b2);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(b3);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(b4);
        panel.add(Box.createVerticalGlue());

        Color logoutBg = bgColor;

        // This button is already styled correctly, no changes needed.
        RoundedButton logoutButton = new RoundedButton(
                "\u21AA   Logout",
                logoutBg,       // Normal background
                logoutRedHover,       // Hover background (same as normal)
                logoutRedPressed,       // Pressed background (same as normal)
                logoutBg,       // Active background (same as normal)
                8               // Arc
        );

        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logoutButton.setForeground(textColor);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logout Successful");
            new LandingFrame().setVisible(true);
            dispose();
        });

        panel.add(logoutButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // 20px bottom margin

        return panel;
    }

    /**
     * Helper to create a clickable card for the function panel.
     */
    private JPanel createFunctionCard(String text) {
        // Use RoundedPanel as a base
        RoundedPanel card = new RoundedPanel(15, cardColor, borderColor, 1);
        card.setPreferredSize(new Dimension(220, 180));
        card.setLayout(new GridBagLayout()); // Use GridBagLayout to center text
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Set the panel to use the gradient on hover
        // This is the *default* hover for all cards.
        card.setHoverGradient(buttonColor, buttonColorGlow);


        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(textColor);

        card.add(label);

        return card;
    }

    // --- REMOVED ---
    // createHeaderButton(String text) is no longer needed

    // --- NEW METHOD ---
    /**
     * Helper to create styled buttons for the right panel that support an
     * active (gradient) state.
     * --- MODIFIED TO MATCH STUDENTDASHBOARD ---
     */
    private RoundedButton createRightPanelButton(String text) {
        // --- MODIFIED --- (Uses 4-color constructor, matches student's active style)
        RoundedButton button = new RoundedButton(
                text,
                sideMenuColor,      // Normal
                borderColor,        // Hover
                buttonColor.darker(), // Pressed
                buttonColor,        // Active
                8                   // Arc
        );
        // --- MODIFIED --- (Font size 17)
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        // --- MODIFIED --- (Default text color is secondary)
        button.setForeground(textSecondaryColor);
        // --- MODIFIED --- (Border padding matches student's)
        button.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        button.setHorizontalAlignment(SwingConstants.LEFT);

        // --- ADDED --- (Size and alignment matches student's)
        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);


        rightPanelButtons.add(button); // Add to list for state management
        return button;
    }

    // --- NEW METHOD ---
    /**
     * Sets the clicked button to active (gradient) and all others to inactive.
     * --- MODIFIED TO MATCH STUDENTDASHBOARD ---
     */
    private void setActiveRightButton(RoundedButton activeButton) {
        for (RoundedButton button : rightPanelButtons) {
            button.setActive(false);
            // --- ADDED --- (Set inactive text color)
            button.setForeground(textSecondaryColor);
        }
        activeButton.setActive(true);
        // --- ADDED --- (Set active text color)
        activeButton.setForeground(textColor);
    }


    /**
     * Creates the placeholder frame with the new styling.
     */
    private void createPlaceholderFrame(String title) {
        JFrame placeholderFrame = new JFrame(title);
        placeholderFrame.setSize(800, 600);
        placeholderFrame.setLocationRelativeTo(this);
        placeholderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        placeholderFrame.getContentPane().setBackground(mainPanelColor);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(mainPanelColor);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        label.setForeground(textColor);

        panel.add(label);

        placeholderFrame.add(panel);
        placeholderFrame.setVisible(true);
    }

    /**
     * Creates a custom scroll pane with hidden bars.
     * (Copied from StudentDashboard)
     */
    private JScrollPane createMainScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(mainPanelColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(mainPanelColor);

        // Hide both scrollbars permanently
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI());

        return scrollPane;
    }

    // --- INNER CLASSES ---

    /**
     * Inner class for a custom styled scrollbar.
     */
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = buttonColor;
            this.trackColor = cardColor;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }


        private JButton createZeroButton() {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            jbutton.setMinimumSize(new Dimension(0, 0));
            jbutton.setMaximumSize(new Dimension(0, 0));
            return jbutton;
        }
    }

    /**
     * Inner class for the circular profile button.
     * REMOVED - not used anymore
     */
    // private class CircularButton extends JButton { ... }
}