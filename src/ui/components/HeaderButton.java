package ui.components;

import javax.swing.*;
import java.awt.*;

public class HeaderButton {
    private Color Buttonback = new Color(35, 42, 55);
    private Color Buttonhover = new Color(25, 30, 40);// --muted-foreground
    private Color textColor = new Color(255, 255, 255);       // --foreground


    public RoundedButton createHeaderButton(String text) {
        RoundedButton button = new RoundedButton(
                text, Buttonback, // normal
                Buttonhover,   // hover
                Buttonhover.darker(), 8);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setPreferredSize(null);
        return button;
    }
}
