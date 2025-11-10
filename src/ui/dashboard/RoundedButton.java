package ui.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A custom "smooth" button with rounded corners and hover effects.
 */
public class RoundedButton extends JButton {

    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private int arc = 20; // The roundness of the corners

    private boolean isHovered = false;

    public RoundedButton(String text, Color normal, Color hover, Color pressed) {
        super(text);
        this.normalColor = normal;
        this.hoverColor = hover;
        this.pressedColor = pressed;

        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(Color.WHITE);

        // Make the button transparent so we can paint our own shape
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);

        // Add mouse listener for hover and press effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }
        });
    }

    // Setters for colors and arc
    public void setNormalColor(Color normalColor) { this.normalColor = normalColor; }
    public void setHoverColor(Color hoverColor) { this.hoverColor = hoverColor; }
    public void setPressedColor(Color pressedColor) { this.pressedColor = pressedColor; }
    public void setArc(int arc) { this.arc = arc; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine which color to use
        Color color;
        if (getModel().isPressed()) {
            color = pressedColor;
        } else if (isHovered) {
            color = hoverColor;
        } else {
            color = normalColor;
        }

        g2.setColor(color);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        // Let the default painter draw the text
        super.paintComponent(g2);
        g2.dispose();
    }
}