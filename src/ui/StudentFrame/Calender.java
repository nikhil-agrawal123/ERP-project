package ui.StudentFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Calender extends JFrame {

    // --- COLOR PALETTE ---
    private Color bgColor = new Color(42, 48, 60);            // Main Background
    private Color cardColor = new Color(54, 59, 74);          // Card/Row Background
    private Color accentColor = new Color(52, 159, 148);      // Teal Accent
    private Color textColor = new Color(255, 255, 255);       // White
    private Color textSecondary = new Color(179, 179, 179);   // Gray
    private Color borderColor = new Color(64, 69, 89);
    private Color todayHighlight = new Color(60, 70, 90);

    // --- DATA ---
    private Map<LocalDate, String[]> eventMap;
    private YearMonth currentMonth;
    private LocalDate selectedDate;

    // --- UI COMPONENTS ---
    private JPanel agendaContainer;
    private JScrollPane agendaScrollPane;
    private JPanel calendarGridPanel;
    private JLabel monthLabel;

    // Map to store reference to day panels for auto-scrolling
    private Map<LocalDate, JPanel> agendaPanelMap = new HashMap<>();

    public Calender() {
        super("Academic Calendar");

        // 1. Set Full Screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1280, 800); // Fallback size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        currentMonth = YearMonth.now();
        selectedDate = LocalDate.now();
        initMockData();

        // 2. Header
        add(createMainHeader(), BorderLayout.NORTH);

        // 3. Main Content Wrapper
        // Added generous padding (Top, Left, BOTTOM, Right) to fix congestion
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        contentPanel.setBackground(bgColor);
        contentPanel.setBorder(new EmptyBorder(20, 50, 100, 50)); // 100px Bottom space

        // --- LEFT SIDE: AGENDA ---
        contentPanel.add(createAgendaView());

        // --- RIGHT SIDE: MONTH CALENDAR ---
        contentPanel.add(createMonthView());

        add(contentPanel, BorderLayout.CENTER);

        // Auto-scroll to today
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                scrollToDate(LocalDate.now());
            }
        });
    }

    private void initMockData() {
        eventMap = new HashMap<>();
        LocalDate today = LocalDate.now();
        eventMap.put(today.minusDays(2), new String[]{"CSE101 Assignment Due", "11:59 PM"});
        eventMap.put(today, new String[]{"Software Eng. Lecture", "10:00 AM", "Project Sync Up", "02:00 PM"});
        eventMap.put(today.plusDays(1), new String[]{"Math Tutorial", "09:00 AM"});
        eventMap.put(today.plusDays(3), new String[]{"Guest Speaker", "04:00 PM"});
        eventMap.put(today.plusDays(5), new String[]{"Mid-Sem Break", "All Day"});
        eventMap.put(today.plusDays(10), new String[]{"DB Lab Eval", "02:00 PM"});
        eventMap.put(today.plusDays(15), new String[]{"Online Quiz", "06:00 PM"});
    }

    // =================================================================================
    // HEADER
    // =================================================================================
    private JPanel createMainHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(bgColor);
        header.setBorder(new EmptyBorder(30, 50, 10, 50)); // More padding

        JLabel title = new JLabel("Academic Schedule");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32)); // Slightly larger
        title.setForeground(textColor);

        JButton closeBtn = new JButton("CLOSE");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setForeground(textSecondary);
        closeBtn.setBackground(cardColor);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());

        header.add(title, BorderLayout.WEST);
        header.add(closeBtn, BorderLayout.EAST);
        return header;
    }

    // =================================================================================
    // LEFT SIDE: AGENDA
    // =================================================================================
    private JComponent createAgendaView() {
        agendaContainer = new JPanel();
        agendaContainer.setLayout(new BoxLayout(agendaContainer, BoxLayout.Y_AXIS));
        agendaContainer.setBackground(bgColor);
        agendaContainer.setBorder(new EmptyBorder(10, 10, 10, 20));

        populateAgenda();

        agendaScrollPane = new JScrollPane(agendaContainer);
        agendaScrollPane.setBorder(BorderFactory.createMatteBorder(0,0,0,1, borderColor));
        agendaScrollPane.setBackground(bgColor);
        agendaScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        agendaScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        styleScrollBar(agendaScrollPane);

        return agendaScrollPane;
    }

    private void populateAgenda() {
        LocalDate start = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate end = LocalDate.now().plusMonths(2).withDayOfMonth(1);

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            JPanel dayPanel = createAgendaDayPanel(date);
            agendaContainer.add(dayPanel);
            agendaContainer.add(Box.createRigidArea(new Dimension(0, 15)));
            agendaPanelMap.put(date, dayPanel);
        }
    }

    private JPanel createAgendaDayPanel(LocalDate date) {
        boolean isToday = date.equals(LocalDate.now());

        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(bgColor);
        row.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Date Bubble
        JPanel dateBubble = new RoundedPanel(15, isToday ? accentColor : cardColor);
        dateBubble.setPreferredSize(new Dimension(60, 60));
        dateBubble.setLayout(new GridBagLayout());

        JLabel dayNum = new JLabel(String.valueOf(date.getDayOfMonth()));
        dayNum.setFont(new Font("Segoe UI", Font.BOLD, 20));
        dayNum.setForeground(textColor);

        JLabel dayName = new JLabel(date.format(DateTimeFormatter.ofPattern("EEE")).toUpperCase());
        dayName.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dayName.setForeground(isToday ? textColor : textSecondary);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0; dateBubble.add(dayNum, gbc);
        gbc.gridy=1; dateBubble.add(dayName, gbc);

        row.add(dateBubble, BorderLayout.WEST);

        // Events List
        JPanel eventsBox = new JPanel();
        eventsBox.setLayout(new BoxLayout(eventsBox, BoxLayout.Y_AXIS));
        eventsBox.setOpaque(false);

        String[] events = eventMap.get(date);
        if (events != null) {
            for (int i = 0; i < events.length; i+=2) {
                String title = events[i];
                String time = (i+1 < events.length) ? events[i+1] : "";

                JPanel card = new RoundedPanel(10, isToday ? todayHighlight : cardColor);
                card.setLayout(new BorderLayout());
                card.setBorder(new EmptyBorder(10, 15, 10, 15));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                JLabel tLbl = new JLabel(title);
                tLbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                tLbl.setForeground(textColor);

                JLabel timeLbl = new JLabel(time);
                timeLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                timeLbl.setForeground(accentColor);

                card.add(tLbl, BorderLayout.CENTER);
                card.add(timeLbl, BorderLayout.EAST);
                eventsBox.add(card);
                eventsBox.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        } else {
            // invisible spacer to keep alignment
            eventsBox.add(Box.createRigidArea(new Dimension(1, 40)));
        }
        row.add(eventsBox, BorderLayout.CENTER);
        return row;
    }

    // =================================================================================
    // RIGHT SIDE: MONTH CALENDAR (FIXED)
    // =================================================================================
    private JPanel createMonthView() {
        // Main wrapper for right side
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);
        // Added internal padding to make the calendar look centered and not huge
        rightWrapper.setBorder(new EmptyBorder(20, 40, 0, 40));

        // -- Month Navigation Header --
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        monthLabel.setForeground(textColor);

        JButton prevBtn = createNavButton("<");
        JButton nextBtn = createNavButton(">");

        prevBtn.addActionListener(e -> changeMonth(-1));
        nextBtn.addActionListener(e -> changeMonth(1));

        navPanel.add(prevBtn, BorderLayout.WEST);
        navPanel.add(monthLabel, BorderLayout.CENTER);
        navPanel.add(nextBtn, BorderLayout.EAST);

        // -- The Grid --
        // Using GridLayout for the cells
        calendarGridPanel = new JPanel(new GridLayout(0, 7, 15, 15)); // Increased gap
        calendarGridPanel.setOpaque(false);

        // IMPORTANT: We wrap the grid in a NORTH panel so it doesn't stretch
        // to fill the entire vertical screen space.
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(calendarGridPanel, BorderLayout.NORTH);

        rightWrapper.add(navPanel, BorderLayout.NORTH);
        rightWrapper.add(gridWrapper, BorderLayout.CENTER);

        refreshCalendarGrid();

        return rightWrapper;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setForeground(accentColor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void changeMonth(int amount) {
        currentMonth = currentMonth.plusMonths(amount);
        refreshCalendarGrid();
    }

    private void refreshCalendarGrid() {
        calendarGridPanel.removeAll();
        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        // 1. Headers
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(textSecondary);
            calendarGridPanel.add(lbl);
        }

        // 2. Spacers
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeekVal = firstOfMonth.getDayOfWeek().getValue();
        for (int i = 1; i < dayOfWeekVal; i++) calendarGridPanel.add(new JLabel(""));

        // 3. Days
        int daysInMonth = currentMonth.lengthOfMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = currentMonth.atDay(i);
            calendarGridPanel.add(createDateCell(date));
        }

        calendarGridPanel.revalidate();
        calendarGridPanel.repaint();
    }

    private JPanel createDateCell(LocalDate date) {
        boolean isToday = date.equals(LocalDate.now());
        boolean isSelected = date.equals(selectedDate);
        boolean hasEvents = eventMap.containsKey(date);

        // Fixed size for cells so they don't stretch too much
        RoundedPanel cell = new RoundedPanel(12, isSelected ? accentColor : cardColor);
        if (isToday && !isSelected) cell.backgroundColor = borderColor;

        // Force a square-ish shape preference
        cell.setPreferredSize(new Dimension(60, 60));

        cell.setLayout(new GridBagLayout());
        cell.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedDate = date;
                scrollToDate(date);
                refreshCalendarGrid();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0;

        JLabel numLbl = new JLabel(String.valueOf(date.getDayOfMonth()));
        numLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        numLbl.setForeground(textColor);
        cell.add(numLbl, gbc);

        if (hasEvents) {
            gbc.gridy=1;
            JLabel dot = new JLabel("•");
            dot.setFont(new Font("Segoe UI", Font.BOLD, 24));
            dot.setForeground(isSelected ? Color.WHITE : accentColor);
            gbc.insets = new Insets(-12,0,0,0);
            cell.add(dot, gbc);
        }
        return cell;
    }

    // =================================================================================
    // HELPERS
    // =================================================================================
    private void scrollToDate(LocalDate date) {
        if (!agendaPanelMap.containsKey(date)) return;
        JPanel targetPanel = agendaPanelMap.get(date);
        SwingUtilities.invokeLater(() -> {
            Rectangle bounds = targetPanel.getBounds();
            Point p = SwingUtilities.convertPoint(targetPanel.getParent(), bounds.getLocation(), agendaScrollPane.getViewport());
            agendaScrollPane.getViewport().setViewPosition(new Point(0, targetPanel.getY()));
        });
    }

    private void styleScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = borderColor;
                this.trackColor = bgColor;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton j = new JButton();
                j.setPreferredSize(new Dimension(0, 0));
                return j;
            }
        });
    }

    class RoundedPanel extends JPanel {
        private int radius;
        public Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}