package ui.StudentFrame;

import dbClasses.studentAvailableCourses;
import ui.dashboard.StudentDashboard;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;
import middleware.studentService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Added for filtering

public class StudentRegCourses extends JFrame {

    // --- UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color mainPanelColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);
    private Color inputBgColor = new Color(48, 54, 70); // Added for search field
    private Color Buttonback = new Color(38, 44, 58);
    private Color Buttonhover = new Color(25, 30, 40);
    private Color sideMenuColor = new Color(60, 60, 60);

    // Notification colors
    private Color notifBgColor = new Color(255, 193, 7, 40);
    private Color notifBorderColor = new Color(255, 193, 7);
    private Color notifTextColor = new Color(255, 213, 79);

    private studentService student;
    private ImageIcon uncheckedIcon;
    private ImageIcon checkedIcon;

    private String rollNumber;
    private String username;
    private String currentSystemSem;

    private JPanel centerContentPanel;
    private JComboBox<String> termDropdown;
    private JTextField searchField; // --- NEW: Search Field ---

    public StudentRegCourses(String rollNumber, String username) {
        super("Course Registration - " + username);

        this.rollNumber = rollNumber;
        this.username = username;

        this.uncheckedIcon = createCheckBoxIcon(false);
        this.checkedIcon = createCheckBoxIcon(true);
        this.student = new studentService();

        this.currentSystemSem = student.getCurrentSystemSemester();
        Map<String, String> dates = student.getRegistrationSchedule();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        try {
            ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
            setIconImage(image.getImage());
        } catch (Exception ignored) {}

        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(mainPanelColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 35, 40, 40));

        // --- Top Panel ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        RoundedButton backButton = createHeaderButton("← Back to Dashboard");
        backButton.addActionListener(e -> {
            new StudentDashboard(rollNumber, username).setVisible(true);
            dispose();
        });

        JLabel titleLabel = new JLabel("Available Courses for Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Notification Banner
        JPanel notifPanel = createNotificationBanner(dates);
        notifPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 3. Controls Row (Dropdown + Search) - MODIFIED LAYOUT
        JPanel controlsRow = new JPanel(new BorderLayout(20, 0));
        controlsRow.setOpaque(false);
        controlsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        controlsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Dropdown ---
        String[] semesters = {"Select Term", currentSystemSem};
        termDropdown = new JComboBox<>(semesters);
        styleComboBox(termDropdown);
        if (currentSystemSem != null && !currentSystemSem.equals("Unknown 0000")) {
            termDropdown.setSelectedItem(currentSystemSem);
        }
        termDropdown.setPreferredSize(new Dimension(300, 45));

        // --- Search Bar Panel (NEW) ---
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        styleTextField(searchField);
        searchField.putClientProperty("JTextField.placeholderText", "Search by Course Name or Instructor...");
        searchField.setPreferredSize(new Dimension(300, 45));

        RoundedButton searchBtn = createHeaderButton("Search"); // Reuse header button style
        searchBtn.setPreferredSize(new Dimension(100, 45));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        // Add to controls row
        controlsRow.add(termDropdown, BorderLayout.WEST);
        controlsRow.add(searchPanel, BorderLayout.EAST); // Search on the right

        // Add to Top Panel
        topPanel.add(headerPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        topPanel.add(notifPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        topPanel.add(controlsRow); // Added controls row instead of just dropdown
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JSeparator titleSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        titleSeparator.setForeground(borderColor);
        titleSeparator.setBackground(mainPanelColor);
        titleSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        titleSeparator.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(titleSeparator);

        // --- Center Content ---
        centerContentPanel = new JPanel();
        centerContentPanel.setLayout(new BoxLayout(centerContentPanel, BoxLayout.Y_AXIS));
        centerContentPanel.setBackground(mainPanelColor);
        centerContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        JScrollPane scrollPane = new JScrollPane(centerContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(mainPanelColor);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());

        // --- Listeners ---

        // Dropdown Listener
        termDropdown.addActionListener(e -> {
            String selectedItem = (String) termDropdown.getSelectedItem();
            searchField.setText(""); // Clear search on term change
            reloadContent(selectedItem, "");
        });

        // Search Button Listener
        searchBtn.addActionListener(e -> {
            String selectedItem = (String) termDropdown.getSelectedItem();
            String query = searchField.getText().trim();
            reloadContent(selectedItem, query);
        });

        // Search Enter Key Listener
        searchField.addActionListener(e -> {
            String selectedItem = (String) termDropdown.getSelectedItem();
            String query = searchField.getText().trim();
            reloadContent(selectedItem, query);
        });

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        RoundedButton registerButton = createActionButton("Register Selected Courses");
        bottomPanel.add(registerButton);

        registerButton.addActionListener(e -> handleRegistration());

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // Initial Load
        if (currentSystemSem != null && !currentSystemSem.equals("Unknown 0000")) {
            loadCourses(currentSystemSem, "");
        } else {
            showPromptCard();
        }
    }

    // --- Helper to manage reloading ---
    private void reloadContent(String selectedItem, String query) {
        centerContentPanel.removeAll();
        if (selectedItem != null && selectedItem.equals(currentSystemSem)) {
            loadCourses(currentSystemSem, query);
        } else {
            showPromptCard();
        }
        centerContentPanel.revalidate();
        centerContentPanel.repaint();
    }

    // --- UPDATED: Load Courses with Filtering ---
    private void loadCourses(String semester, String searchQuery) {
        List<studentAvailableCourses> allCourses = student.AllCourses(semester);

        // --- Filtering Logic ---
        List<studentAvailableCourses> filteredCourses;
        if (searchQuery == null || searchQuery.isEmpty()) {
            filteredCourses = allCourses;
        } else {
            String q = searchQuery.toLowerCase();
            filteredCourses = allCourses.stream()
                    .filter(c -> c.getCourse_name().toLowerCase().contains(q) ||
                            c.getOfferedBY().toLowerCase().contains(q) ||
                            c.getCourse_code().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        if (filteredCourses.isEmpty()) {
            if (allCourses.isEmpty()) {
                showEmptyMessage(semester); // Truly empty
            } else {
                // Empty because of search result
                showNoSearchResultsMessage(searchQuery);
            }
        } else {
            filteredCourses.forEach(course -> {
                JPanel coursePanel = createCourseTilePanel(course);
                centerContentPanel.add(coursePanel);
                centerContentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            });
        }
    }

    private void showNoSearchResultsMessage(String query) {
        RoundedPanel promptPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        promptPanel.setLayout(new BorderLayout());
        promptPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        promptPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel promptLabel = new JLabel("No courses found matching \"" + query + "\".", SwingConstants.CENTER);
        promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        promptLabel.setForeground(textSecondaryColor);

        promptPanel.add(promptLabel, BorderLayout.CENTER);
        centerContentPanel.add(promptPanel);
    }

    // ... (createNotificationBanner, handleRegistration, showEmptyMessage, showPromptCard unchanged) ...
    private JPanel createNotificationBanner(Map<String, String> dates) {
        RoundedPanel banner = new RoundedPanel(10, notifBgColor, notifBorderColor, 1);
        banner.setLayout(new BorderLayout());
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        banner.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        String start = dates.getOrDefault("reg_start", "TBA");
        String end = dates.getOrDefault("reg_end", "TBA");
        JLabel label = new JLabel("Registration Window: " + start + " to " + end);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(notifTextColor);
        label.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
        banner.add(label, BorderLayout.CENTER);
        return banner;
    }

    private void handleRegistration() {
        Component[] components = centerContentPanel.getComponents();
        Map<String, String> dates = student.getRegistrationSchedule();
        String start = dates.get("reg_start");
        String end = dates.get("reg_end");

        int selectedCount = 0;
        List<studentAvailableCourses> selectedCourses = new ArrayList<>();

        for (Component comp : components) {
            if (comp instanceof RoundedPanel) {
                RoundedPanel tilePanel = (RoundedPanel) comp;
                for (Component tileComp : tilePanel.getComponents()) {
                    if (tileComp instanceof JLabel && ((JLabel) tileComp).getIcon() != null) {
                        JLabel checkBoxLabel = (JLabel) tileComp;
                        Object selectedProp = checkBoxLabel.getClientProperty("selected");
                        if (selectedProp != null && (boolean) selectedProp) {
                            studentAvailableCourses courseObject = (studentAvailableCourses) checkBoxLabel.getClientProperty("courseObject");
                            selectedCourses.add(courseObject);
                            selectedCount++;
                            break;
                        }
                    }
                }
            }
        }

        LocalDate startD = LocalDate.parse(start);
        LocalDate endD = LocalDate.parse(end);
        LocalDate today = LocalDate.now();

        if(today.isAfter(endD) || today.isBefore(startD)) {
            JOptionPane.showMessageDialog(this, "Registration failed registration has not begin.", "Error", JOptionPane.ERROR_MESSAGE);
        }else if(today.isAfter(endD)){
            JOptionPane.showMessageDialog(this, "Registration failed registration has ended.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else{
            if (selectedCount == 0) {
                JOptionPane.showMessageDialog(this, "You have not selected any courses.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            } else {
                boolean success = student.RegisterCourse(selectedCourses, username);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Successfully registered for " + selectedCount + " courses!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    termDropdown.setSelectedItem(termDropdown.getSelectedItem());
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed. Check capacity or duplicates.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showEmptyMessage(String sem) {
        RoundedPanel promptPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        promptPanel.setLayout(new BorderLayout());
        promptPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        promptPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        JLabel promptLabel = new JLabel("No courses available for " + sem, SwingConstants.CENTER);
        promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        promptLabel.setForeground(textSecondaryColor);
        promptPanel.add(promptLabel, BorderLayout.CENTER);
        centerContentPanel.add(promptPanel);
    }

    private void showPromptCard() {
        RoundedPanel promptPanel = new RoundedPanel(15, cardColor, borderColor, 1);
        promptPanel.setLayout(new BorderLayout());
        promptPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        promptPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        JLabel promptLabel = new JLabel("Please select a term to view courses.", SwingConstants.CENTER);
        promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        promptLabel.setForeground(textSecondaryColor);
        promptPanel.add(promptLabel, BorderLayout.CENTER);
        centerContentPanel.add(promptPanel);
    }

    // ... (createCourseTilePanel, createDetailPanel, Icons, createHeaderButton, createActionButton, styleComboBox unchanged) ...

    private JPanel createCourseTilePanel(studentAvailableCourses course) {
        RoundedPanel tilePanel = new RoundedPanel(15, cardColor, borderColor, 1);
        tilePanel.setLayout(new BorderLayout(15, 10));
        tilePanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        tilePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Top
        JPanel topInfoPanel = new JPanel();
        topInfoPanel.setLayout(new BoxLayout(topInfoPanel, BoxLayout.X_AXIS));
        topInfoPanel.setOpaque(false);
        JLabel codeLabel = new JLabel(course.getCourse_code());
        codeLabel.setFont(new Font("Segoe UI Mono", Font.BOLD, 16));
        codeLabel.setForeground(Color.WHITE);
        codeLabel.setBackground(buttonColor);
        codeLabel.setOpaque(true);
        codeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel nameLabel = new JLabel(course.getCourse_name());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(textColor);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        topInfoPanel.add(codeLabel);
        topInfoPanel.add(nameLabel);
        topInfoPanel.add(Box.createHorizontalGlue());
        JLabel detailsLink = createClickableLink("Check course details ↗", "https://techtree.iiitd.edu.in/viewDescription/filename?=" + course.getCourse_code());
        topInfoPanel.add(detailsLink);

        // Bottom
        JPanel bottomInfoPanel = new JPanel();
        bottomInfoPanel.setLayout(new BoxLayout(bottomInfoPanel, BoxLayout.X_AXIS));
        bottomInfoPanel.setOpaque(false);
        bottomInfoPanel.add(createDetailPanel("Credits", String.valueOf(course.getCourse_credits())));
        bottomInfoPanel.add(Box.createRigidArea(new Dimension(40, 0)));
        bottomInfoPanel.add(createDetailPanel("Instructor", course.getOfferedBY()));
        bottomInfoPanel.add(Box.createRigidArea(new Dimension(40, 0)));
        bottomInfoPanel.add(createDetailPanel("Seats", course.getEnrolledCount() + " / " + course.getCapacity()));
        bottomInfoPanel.add(Box.createHorizontalGlue());

        // Checkbox
        JLabel checkBoxLabel = new JLabel(uncheckedIcon);
        checkBoxLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkBoxLabel.putClientProperty("selected", false);
        checkBoxLabel.putClientProperty("courseObject", course);

        if (course.getEnrolledCount() >= course.getCapacity()) {
            checkBoxLabel.setEnabled(false);
            codeLabel.setBackground(sideMenuColor);
            nameLabel.setForeground(textSecondaryColor);
            detailsLink.setVisible(false);
        } else {
            checkBoxLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (checkBoxLabel.isEnabled()) {
                        boolean isSelected = (boolean) checkBoxLabel.getClientProperty("selected");
                        checkBoxLabel.setIcon(!isSelected ? checkedIcon : uncheckedIcon);
                        checkBoxLabel.putClientProperty("selected", !isSelected);
                    }
                }
            });
        }

        tilePanel.add(topInfoPanel, BorderLayout.NORTH);
        tilePanel.add(bottomInfoPanel, BorderLayout.CENTER);
        tilePanel.add(checkBoxLabel, BorderLayout.EAST);
        return tilePanel;
    }

    private JPanel createDetailPanel(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setForeground(textSecondaryColor);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 16));
        v.setForeground(textColor);
        panel.add(t);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(v);
        return panel;
    }

    // --- Styling Helpers ---

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(inputBgColor);
        field.setForeground(textColor);
        field.setCaretColor(buttonColor);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private ImageIcon createCheckBoxIcon(boolean isChecked) {
        int w = 24; int h = 24;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(textSecondaryColor);
        g2.setStroke(new BasicStroke(2));
        g2.draw(new RoundRectangle2D.Float(1, 1, w-3, h-3, 8, 8));
        if (isChecked) {
            g2.setColor(buttonColor);
            g2.fill(new RoundRectangle2D.Float(1, 1, w-3, h-3, 8, 8));
            g2.setColor(textColor);
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(7, 12, 11, 17); g2.drawLine(11, 17, 18, 8);
        }
        g2.dispose();
        return new ImageIcon(img);
    }

    private RoundedButton createHeaderButton(String text) {
        RoundedButton b = new RoundedButton(text, Buttonback, Buttonhover, borderColor.darker(), 8);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(textColor);
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        b.setPreferredSize(null);
        return b;
    }

    private RoundedButton createActionButton(String text) {
        RoundedButton b = new RoundedButton(text, buttonColor, buttonColorGlow, 8);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setForeground(textColor);
        b.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        b.setPreferredSize(null);
        return b;
    }

    private JLabel createClickableLink(String text, String url) {
        JLabel linkLabel = new JLabel("<html><u>" + text + "</u></html>");
        linkLabel.setForeground(buttonColor);
        linkLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                try { Desktop.getDesktop().browse(new URI(url)); } catch (Exception e) {}
            }
            public void mouseEntered(MouseEvent e) { linkLabel.setForeground(buttonColorGlow); }
            public void mouseExited(MouseEvent e) { linkLabel.setForeground(buttonColor); }
        });
        return linkLabel;
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setFont(new Font("Segoe UI", Font.BOLD, 16));
        box.setForeground(textColor);
        box.setBackground(cardColor);
        box.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        box.setFocusable(false);
        box.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? buttonColor : cardColor);
                setForeground(isSelected ? textColor : textSecondaryColor);
                setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                return this;
            }
        });
        box.setUI(new BasicComboBoxUI() {
            protected JButton createArrowButton() {
                RoundedButton b = new RoundedButton("▼", buttonColor, buttonColor.brighter(), buttonColor.darker(), 8);
                b.setForeground(textColor);
                b.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return b;
            }
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(cardColor);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                g.setColor(textColor);
                g.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g.drawString((String)box.getSelectedItem(), bounds.x + 15, bounds.y + 30);
            }
        });
    }

    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() { this.thumbColor = buttonColor; this.trackColor = cardColor; }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            g.setColor(thumbColor); ((Graphics2D)g).fill(new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, 10, 10));
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor); ((Graphics2D)g).fill(r);
        }
    }

    // --- Inner Class: RoundedPanel (Duplicated to ensure standalone) ---
    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor, borderColor;
        private int borderThickness;

        public RoundedPanel(int radius, Color bgColor, Color borderColor, int borderThickness) {
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            this.borderColor = borderColor;
            this.borderThickness = borderThickness;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            if (borderThickness > 0) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(borderThickness));
                g2.draw(new RoundRectangle2D.Float(borderThickness/2f, borderThickness/2f, getWidth()-borderThickness, getHeight()-borderThickness, cornerRadius, cornerRadius));
            }
            g2.dispose();
        }
    }
}