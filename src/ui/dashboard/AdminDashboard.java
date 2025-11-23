package ui.dashboard;

import ui.AdminFrame.*;
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

    // List to manage the active state of right-panel buttons
    private List<RoundedButton> rightPanelButtons;

    public AdminDashboard(String adminID, String username) {
        super("Admin Dashboard - " + username);
        this.adminID = adminID;
        this.username = username;

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

        // --- Component Resizing Listener ---
        mainLayeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = e.getComponent().getSize();
                cardHolderPanel.setBounds(0, 0, size.width, size.height);
            }
        });

        // --- Create Content Cards ---
        createContentCards(cardHolderPanel, username);
        cardLayout.show(cardHolderPanel, "HOME");

        // Set the first button as active by default
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
        JScrollPane mainScrollPane = createMainScrollPane(mainContentContainer);
        homePanel.add(mainScrollPane, BorderLayout.CENTER);

        cardHolder.add(homePanel, "HOME");
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

        // --- Passport photo panel with gradient border ---
        JPanel passportPhotoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int borderWidth = 3;

                // Draw gradient border
                GradientPaint gp = new GradientPaint(
                        0, 0, buttonColor,
                        getWidth(), getHeight(), buttonColorGlow);
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));

                // Draw inner background
                g2d.setColor(sideMenuColor);
                g2d.fill(new RoundRectangle2D.Double(borderWidth, borderWidth,
                        getWidth() - 2 * borderWidth, getHeight() - 2 * borderWidth, 8, 8));

                g2d.dispose();
            }
        };
        passportPhotoPanel.setLayout(new BorderLayout());
        passportPhotoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passportPhotoPanel.setPreferredSize(new Dimension(180, 220)); // Passport photo aspect ratio
        passportPhotoPanel.setMaximumSize(new Dimension(180, 220));
        passportPhotoPanel.setOpaque(false);

        // Load and scale the image for the passport photo
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/logo.jpg"));
            Image originalImage = originalIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(
                    170, // width - padding
                    210, // height - padding
                    Image.SCALE_SMOOTH);
            JLabel photoLabel = new JLabel(new ImageIcon(scaledImage));
            photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            photoLabel.setVerticalAlignment(SwingConstants.CENTER);
            passportPhotoPanel.add(photoLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            System.out.println("Profile image not found.");
        }

        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(textColor);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel idLabel = new JLabel("ID: " + this.adminID);
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        idLabel.setForeground(textSecondaryColor);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(passportPhotoPanel);
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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(mainPanelColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // This panel holds the cards in a 3-column grid
        JPanel cardGridPanel = new JPanel(new GridLayout(0, 3, 25, 25));
        cardGridPanel.setOpaque(false);

        // Create styled function cards
        JPanel addUserCard = createFunctionCard("Add Users");
        addUserCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                AddUser addUserPanel = new AddUser();
                JDialog addUserDialog = new JDialog(AdminDashboard.this, "Add New User", true);

                addUserDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                addUserDialog.getContentPane().add(addUserPanel);

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

        JPanel addCoursesCard = createFunctionCard("Add Course");
        addCoursesCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    AdminAddcourse addcourseFrame = new AdminAddcourse(adminID, username, AdminDashboard.this);
                    addcourseFrame.setVisible(true);
                    AdminDashboard.this.setVisible(false);
                });
            }
        });

        JPanel maintenanceCard = createFunctionCard("Maintenance");
        maintenanceCard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Maintenance maintenance = new Maintenance();
                maintenance.setVisible(true);
                AdminDashboard.this.setVisible(false);
            }
        });

        // Add cards to the new grid panel
        cardGridPanel.add(addUserCard);
        cardGridPanel.add(removeUsersCard);
        cardGridPanel.add(addCoursesCard);
        cardGridPanel.add(manageCoursesCard);
        cardGridPanel.add(maintenanceCard);

        // Add the grid panel to the main panel, anchored to the top-left
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        panel.add(cardGridPanel, gbc);

        return panel;
    }

    /**
     * Creates the Right Side panel with buttons (Original EAST position).
     */
    private JPanel createRightSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(sideMenuColor);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));


        // --- Create Buttons ---
        RoundedButton b1 = createRightPanelButton("Home");
        RoundedButton b2 = createRightPanelButton("View Logs");
        RoundedButton b4 = createRightPanelButton("Reports");
        // --- ADDED B5 ---
        RoundedButton b5 = createRightPanelButton("Backup & Restore");

        // --- Action Listeners ---
        b1.addActionListener(e -> {
            setActiveRightButton(b1);
            // cardLayout.show(cardHolderPanel, "HOME"); // Example
        });

        b2.addActionListener(e -> {
            setActiveRightButton(b2);
            SwingUtilities.invokeLater(() -> new ViewLogsFrame().setVisible(true));
        });


        b4.addActionListener(e -> {
            setActiveRightButton(b4);
            System.out.println("Reports pressed");
        });

        // --- ADDED B5 LISTENER ---
        b5.addActionListener(e -> {
            setActiveRightButton(b5);
            SwingUtilities.invokeLater(() -> new Backup().setVisible(true));
        });


        // --- Add Buttons to Panel ---
        panel.add(b1);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(b2);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(b4);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(b5);

        panel.add(Box.createVerticalGlue());

        Color logoutBg = bgColor;

        RoundedButton logoutButton = new RoundedButton(
                "\u21AA   Logout",
                logoutBg,
                logoutRedHover,
                logoutRedPressed,
                logoutBg,
                8
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
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        return panel;
    }

    /**
     * Helper to create a clickable card for the function panel.
     */
    private JPanel createFunctionCard(String text) {
        // Use RoundedPanel as a base
        RoundedPanel card = new RoundedPanel(15, cardColor, borderColor, 1);
        card.setPreferredSize(new Dimension(220, 180));
        card.setLayout(new GridBagLayout());
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.setHoverGradient(buttonColor, buttonColorGlow);

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(textColor);

        card.add(label);

        return card;
    }

    /**
     * Helper to create styled buttons for the right panel that support an
     * active (gradient) state.
     */
    private RoundedButton createRightPanelButton(String text) {
        RoundedButton button = new RoundedButton(
                text,
                sideMenuColor,
                borderColor,
                buttonColor.darker(),
                buttonColor,
                8
        );
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setForeground(textSecondaryColor);
        button.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        button.setHorizontalAlignment(SwingConstants.LEFT);

        button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);


        rightPanelButtons.add(button);
        return button;
    }

    /**
     * Sets the clicked button to active (gradient) and all others to inactive.
     */
    private void setActiveRightButton(RoundedButton activeButton) {
        for (RoundedButton button : rightPanelButtons) {
            button.setActive(false);
            button.setForeground(textSecondaryColor);
        }
        activeButton.setActive(true);
        activeButton.setForeground(textColor);
    }

    /**
     * Creates a custom scroll pane with hidden bars.
     */
    private JScrollPane createMainScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(mainPanelColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(mainPanelColor);

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
}