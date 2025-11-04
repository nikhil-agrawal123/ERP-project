package ui.auth;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import ui.landing.LandingFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This frame opens after a successful password login to perform face verification.
 * It is reusable for any user type (student, parent, etc.).
 *
 * It takes a "uniqueId" (like roll number or username) to know *who* to verify,
 * and a "Runnable" to execute upon successful verification.
 */
public class FaceVerificationFrame extends JFrame {

    private Color bgColor = new Color(45, 45, 45);
    private Color buttonColor = new Color(57, 174, 168);
    private Color textColor = Color.WHITE;

    private Webcam webcam;
    private WebcamPanel webcamPanel;

    // This is the action that will be run on success.
    // e.g., () -> new StudentDashboard().setVisible(true)
    private Runnable onVerificationSuccess;

    /**
     * Creates a new Face Verification window.
     *
     * @param uniqueId The ID of the user to verify (e.g., rollNumber or parentUsername).
     * @param username The display name of the user (e.g., "John Doe").
     * @param onVerificationSuccess A lambda expression or Runnable to be executed on success.
     */
    public FaceVerificationFrame(String uniqueId, String username, Runnable onVerificationSuccess) {
        super("Face Verification - " + username);
        this.onVerificationSuccess = onVerificationSuccess;

        // 1. Find and set up the webcam
        try {
            System.out.println("Searching for webcams...");

            // Get a list of all webcams. This is a better test.
            java.util.List<Webcam> webcams = Webcam.getWebcams();

            if (webcams.isEmpty()) {
                throw new RuntimeException("No webcams found.");
            }

            System.out.println("Found webcams:");
            for (Webcam w : webcams) {
                System.out.println(" - " + w.getName());
            }

            webcam = Webcam.getDefault(); // Get the default one from the list

            if (webcam == null) {
                throw new RuntimeException("Could not get default webcam, even though list is not empty.");
            }

            System.out.println("Using default webcam: " + webcam.getName());

            webcam.setViewSize(WebcamResolution.VGA.getSize());

        } catch (Exception e) {
            System.err.println("Webcam initialization failed!");
            // --- NEW: Print the full error for debugging ---
            e.printStackTrace();
            // --- END NEW ---

            JOptionPane.showMessageDialog(this,
                    "Could not initialize webcam.\n" +
                            "Please ensure it is connected and NOT in use by another app (Zoom, Teams, etc.).\n" +
                            "Error: " + e.getMessage(),
                    "Webcam Error",
                    JOptionPane.ERROR_MESSAGE);
            new LandingFrame().setVisible(true); // Go back to safety
            dispose();
            return;
        }


        // 2. Create the panel from the library
        webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setMirrored(true);

        // 3. Set up the frame
        setSize(new Dimension(640, 560)); // A bit taller than the 640x480 video
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(bgColor);
        setLayout(new BorderLayout(0, 10));

        // 4. Create UI components
        JLabel infoLabel = new JLabel("Please look at the camera, " + username);
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoLabel.setForeground(textColor);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JButton verifyButton = createStyledButton("Verify Face");
        JButton cancelButton = createStyledButton("Cancel");
        buttonPanel.add(verifyButton);
        buttonPanel.add(cancelButton);

        // 5. Add components to frame
        add(infoLabel, BorderLayout.NORTH);
        add(webcamPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---

        verifyButton.addActionListener(e -> {
            // ---
            // TODO: Add your actual face recognition logic here
            // 1. Capture image: webcam.getImage()
            // 2. Load stored template for 'uniqueId' from database
            // 3. Compare them using a model (e.g., JavaCV's LBPHFaceRecognizer)
            // 4. If (confidence is high enough) { ... }
            // ---

            // For now, we will *simulate* a successful login
            System.out.println("Face verification successful (simulated) for " + uniqueId);
            JOptionPane.showMessageDialog(this,
                    "Face verification successful!",
                    "Login Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Run the success action we were given
            this.onVerificationSuccess.run();

            // Clean up and close
            closeWebcam();
            dispose();
        });

        cancelButton.addActionListener(e -> {
            // User cancelled. Go back to the landing page.
            closeWebcam();
            new LandingFrame().setVisible(true);
            dispose();
        });

        // Add a window listener to ensure the webcam is always closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWebcam();
            }
        });
    }

    /**
     * Safely closes the webcam if it's open.
     */
    private void closeWebcam() {
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
            System.out.println("Webcam closed.");
        }
    }

    /**
     * Helper method to create a styled button.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(buttonColor);
        button.setForeground(textColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}