package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.GradientPaint;
import java.awt.geom.RoundRectangle2D;
// --- NEW IMPORTS ---
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A JPanel with rounded corners and a border.
 * Supports both solid and gradient backgrounds, now with hover state.
 */
public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private int borderThickness;

    private boolean useGradient = false;
    private Color gradientStartColor;
    private Color gradientEndColor;

    // --- NEW FIELDS ---
    private boolean isHovered = false;
    private boolean hoverUsesGradient = false;
    private Color hoverGradientStart;
    private Color hoverGradientEnd;

    /**
     * Constructor for SOLID color panels (with border)
     */
    public RoundedPanel(int radius, Color bgColor, Color borderColor, int borderThickness) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        this.borderThickness = borderThickness;
        this.useGradient = false;
        setOpaque(false);
        addHoverListener(); // --- NEW CALL ---
    }

    /**
     * Constructor for GRADIENT color panels (no border)
     */
    public RoundedPanel(int radius, Color gradStart, Color gradEnd) {
        super();
        this.cornerRadius = radius;
        this.gradientStartColor = gradStart;
        this.gradientEndColor = gradEnd;
        this.useGradient = true;
        this.borderThickness = 0; // Gradients don't have a border in this design
        this.borderColor = gradStart; // Fallback
        this.backgroundColor = gradStart; // Fallback
        setOpaque(false);
        addHoverListener(); // --- NEW CALL ---
    }

    // --- NEW METHOD ---
    /**
     * Adds the internal mouse listener to track hover state.
     */
    private void addHoverListener() {
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

    // --- NEW METHOD ---
    /**
     * Sets the panel to fill with a gradient when hovered.
     *
     * @param gradStart The starting color of the gradient.
     * @param gradEnd   The ending color of the gradient.
     */
    public void setHoverGradient(Color gradStart, Color gradEnd) {
        this.hoverUsesGradient = true;
        this.hoverGradientStart = gradStart;
        this.hoverGradientEnd = gradEnd;
    }


    // --- MODIFIED METHOD ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isHovered && hoverUsesGradient) {
            // 1. Hover State (is Gradient)
            GradientPaint gp = new GradientPaint(
                    0, getHeight(), hoverGradientStart,
                    getWidth(), 0, hoverGradientEnd
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            // No border on hover, as requested

        } else if (useGradient) {
            // 2. Normal State (is Gradient)
            GradientPaint gp = new GradientPaint(
                    0, getHeight(), gradientStartColor,
                    getWidth(), 0, gradientEndColor
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        } else {
            // 3. Normal State (is Solid)
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            // 4. Draw Border (only if solid and borderThickness > 0)
            if (borderThickness > 0) {
                g2.setColor(this.borderColor);
                g2.setStroke(new BasicStroke(this.borderThickness));
                float halfStroke = this.borderThickness / 2.0f;
                g2.draw(new RoundRectangle2D.Float(
                        halfStroke,
                        halfStroke,
                        getWidth() - this.borderThickness,
                        getHeight() - this.borderThickness,
                        cornerRadius,
                        cornerRadius
                ));
            }
        }
        g2.dispose();
    }
}