import databaseConfig.Connector;
import ui.landing.LandingFrame;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import dependancy.org.mindrot.jbcrypt.BCrypt;

/**
 * The main entry point for the University ERP application.
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LandingFrame landingFrame = new LandingFrame();

                landingFrame.setVisible(true);

                ImageIcon image = new ImageIcon(Main.class.getResource("/logo.jpg"));
                landingFrame.setIconImage(image.getImage());
            }
        });
        Connector connector = new Connector();
        connector.connect();
    }
}

