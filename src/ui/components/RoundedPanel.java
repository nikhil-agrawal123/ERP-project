package ui.components; // Also in the new package

import javax.swing.*;
import java.awt.*;
import java.awt.GradientPaint;
import java.awt.geom.RoundRectangle2D;

/**
 * A JPanel with rounded corners and a border.
 * Supports both solid and gradient backgrounds.
 */
public class RoundedPanel extends JPanel { // Changed to public class
    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;
    private int borderThickness;

    private boolean useGradient = false;
    private Color gradientStartColor;
    private Color gradientEndColor;

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
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (useGradient) {
            // 1. Fill with Gradient
            GradientPaint gp = new GradientPaint(
                    0, getHeight(), gradientStartColor,
                    getWidth(), 0, gradientEndColor
            );
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        } else {
            // 2. Fill with Solid Color
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            // 3. Draw Border (only if solid and borderThickness > 0)
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