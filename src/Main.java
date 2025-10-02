import ui.landing.LandingFrame;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/**
 * The main entry point for the University ERP application.
 */
public class Main {

    public static void main(String[] args) {
        // Swing applications should be run on the Event Dispatch Thread (EDT)
        // for thread safety. SwingUtilities.invokeLater ensures this.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // The application now starts with the landing page.
                LandingFrame landingFrame = new LandingFrame();

                landingFrame.setVisible(true);

//                IIITD LOGO USAGE
                ImageIcon image = new ImageIcon(Main.class.getResource("/logo.jpg"));
                landingFrame.setIconImage(image.getImage());
            }
        });
    }
}

