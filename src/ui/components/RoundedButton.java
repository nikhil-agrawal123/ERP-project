package ui.components;

// --- All Required Imports ---
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GradientPaint;

/**
 * A custom "smooth" button with rounded corners and multiple states.
 * This class supports solid colors, gradients, and active/inactive states.
 */
public class RoundedButton extends JButton {

    // Colors for solid-fill buttons
    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color activeColor;

    // Colors for gradient-fill buttons
    private Color gradientStartColor;
    private Color gradientEndColor;

    private int arc;
    private boolean useGradient = false;
    private boolean isHovered = false;
    private boolean isActive = false;
    private boolean activeIsGradient = false;

    /**
     * Constructor for SOLID color buttons (like the side menu).
     */
    public RoundedButton(String text, Color normal, Color hover, Color pressed, Color active, int arc) {
        super(text);
        this.normalColor = normal;
        this.hoverColor = hover;
        this.pressedColor = pressed;
        this.activeColor = active;
        this.arc = arc;
        this.useGradient = false;
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setProperties();
    }

    /**
     * Constructor for SOLID color buttons (like the header button).
     */
    public RoundedButton(String text, Color normal, Color hover, Color pressed, int arc) {
        this(text, normal, hover, pressed, normal, arc);
        this.useGradient = false;
    }

    /**
     * Constructor for GRADIENT color buttons (for createActionButton).
     */
    public RoundedButton(String text, Color gradStart, Color gradEnd, int arc) {
        super(text);
        this.gradientStartColor = gradStart;
        this.gradientEndColor = gradEnd;
        this.pressedColor = gradStart.darker();
        this.normalColor = gradStart; // Fallback
        this.hoverColor = gradStart; // Fallback
        this.activeColor = gradStart; // Fallback
        this.arc = arc;
        this.useGradient = true; // This is a gradient button
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(Color.WHITE);
        setProperties();
    }

    /**
     * Constructor for TAB-STYLE buttons (Solid when normal, Gradient when active).
     */
    public RoundedButton(String text, Color normal, Color hover, Color pressed, Color gradStart, Color gradEnd, int arc) {
        this(text, normal, hover, pressed, normal, arc); // Call solid constructor
        this.gradientStartColor = gradStart;
        this.gradientEndColor = gradEnd;
        this.activeIsGradient = true; // Set the flag
    }


    private void setProperties() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        if (getBorder() != null) {
            Insets insets = getBorder().getBorderInsets(this);
            size.width += insets.left + insets.right;
            size.height += insets.top + insets.bottom;
        }
        if (isPreferredSizeSet()) {
            size = super.getPreferredSize();
        }
        return size;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            // 1. Pressed State (always solid)
            g2.setColor(pressedColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        } else if (isActive) {
            // 2. Active State
            if (activeIsGradient) {
                // Draw gradient for active state (semester tabs)
                GradientPaint gp = new GradientPaint(
                        0, getHeight(), gradientStartColor,
                        getWidth(), 0, gradientEndColor
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            } else {
                // Draw solid color for active state (side menu)
                g2.setColor(activeColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            }

        } else if (useGradient) {
            // 3. Gradient Button (Normal or Hover)
            Color start = gradientStartColor;
            Color end = gradientEndColor;

            if (isHovered) {
                // A subtle glow/darken on hover for gradients
                start = gradientStartColor.brighter();
                end = gradientEndColor;
            }

            GradientPaint gp = new GradientPaint(
                    0, getHeight(), start,
                    getWidth(), 0, end
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        } else {
            // 4. Solid Button (Normal or Hover)
            Color color = isHovered ? hoverColor : normalColor;
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        }

        super.paintComponent(g2);
        g2.dispose();
    }
}