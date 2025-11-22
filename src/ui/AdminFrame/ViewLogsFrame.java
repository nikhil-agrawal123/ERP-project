package ui.AdminFrame;

import dbClasses.logClass;
import middleware.loggerService;
import ui.components.RoundedButton;
import ui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * A standalone frame to view System Audit Logs.
 * Styled to match the Admin Dashboard.
 */
public class ViewLogsFrame extends JFrame {

    // --- UI Color Palette ---
    private Color bgColor = new Color(42, 48, 60);
    private Color cardColor = new Color(54, 59, 74);
    private Color borderColor = new Color(64, 69, 89);
    private Color buttonColor = new Color(52, 159, 148);
    private Color buttonColorGlow = new Color(79, 196, 184);
    private Color textColor = new Color(255, 255, 255);
    private Color textSecondaryColor = new Color(179, 179, 179);

    private loggerService loggerService;
    private JTable logsTable;
    private DefaultTableModel tableModel;

    public ViewLogsFrame() {
        super("System Audit Logs");

        this.loggerService = new loggerService();

        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout());

        try {
            ImageIcon image = new ImageIcon(getClass().getResource("/logo.jpg"));
            setIconImage(image.getImage());
        } catch (Exception ignored) {}

        // --- 1. Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Title Block
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel titleLabel = new JLabel("System Logs");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Track all system activities and changes");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(textSecondaryColor);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBlock.add(titleLabel);
        titleBlock.add(Box.createRigidArea(new Dimension(0, 5)));
        titleBlock.add(subtitleLabel);

        // Header Buttons (Refresh & Close)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        RoundedButton refreshButton = new RoundedButton(
                "Refresh",
                borderColor,
                borderColor.brighter(),
                10
        );
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.setForeground(textColor);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshButton.addActionListener(e -> loadLogs());

        RoundedButton closeButton = new RoundedButton(
                "Close",
                buttonColor,
                buttonColorGlow,
                10
        );
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setForeground(textColor);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Table Panel ---
        RoundedPanel tableCard = new RoundedPanel(15, cardColor, cardColor, 0);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Wrapper for margins
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));
        contentWrapper.add(tableCard, BorderLayout.CENTER);

        add(contentWrapper, BorderLayout.CENTER);

        // Initialize Table
        String[] columns = {"ID", "Timestamp", "User", "Action", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logsTable = new JTable(tableModel);
        styleTable(logsTable);

        // Column Width Adjustments
        logsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        logsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Time
        logsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // User
        logsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Action
        logsTable.getColumnModel().getColumn(4).setPreferredWidth(400); // Description

        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardColor);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Apply custom scrollbar
        scrollPane.getVerticalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new StyledScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Load Initial Data
        loadLogs();
    }

    private void loadLogs() {
        tableModel.setRowCount(0); // Clear existing
        List<logClass> logs = loggerService.getSystemLogs();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int itr = 1;
        for (logClass log : logs) {
            Object[] row = {
                    itr,
                    sdf.format(log.getDate()),
                    log.getUserId(),
                    log.getActionType(),
                    log.getDescription()
            };
            itr++;
            tableModel.addRow(row);
        }
    }

    // --- Styling Helper Methods ---

    private void styleTable(JTable table) {
        table.setBackground(cardColor);
        table.setForeground(textColor);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setGridColor(borderColor);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(buttonColor.darker());
        table.setSelectionForeground(textColor);

        // Header Style
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(cardColor);
                setForeground(textSecondaryColor);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, borderColor),
                        BorderFactory.createEmptyBorder(10, 5, 10, 5)
                ));
                return this;
            }
        });

        // Cell Style
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    setBackground(buttonColor.darker());
                } else {
                    setBackground(cardColor);
                }
                setForeground(textColor);
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

                // Center align everything except Description
                if (column == 4) {
                    setHorizontalAlignment(JLabel.LEFT);
                } else {
                    setHorizontalAlignment(JLabel.CENTER);
                }

                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    // --- Custom Scrollbar (Matching other frames) ---
    private class StyledScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = borderColor.brighter();
            this.trackColor = cardColor;
        }
        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }

        private JButton createZeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0,0));
            return b;
        }
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 10, 10);
            g2.dispose();
        }
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new ViewLogsFrame().setVisible(true));
    }
}