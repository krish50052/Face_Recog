package src;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Application entry point for the 3D face recognition system.
 *
 * <p>The application starts directly in the face-recognition interface. No
 * login, registration, database, or test credentials are required.</p>
 */
public final class FrontEnd {
    private static final int WINDOW_WIDTH = 1366;
    private static final int WINDOW_HEIGHT = 730;
    public static final JFrame frame = new JFrame(
        "3D Face Recognition under Expressions, Occlusions and Pose Variations"
    );

    private FrontEnd() {
    }

    /**
     * Launches the face-recognition interface directly.
     *
     * @param args command line arguments, not used
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FrontEnd::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JApplet applet = new Main();
        frame.add(applet);
        frame.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                int reply = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to exit?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
                );
                if (reply == JOptionPane.YES_OPTION) {
                    frame.dispose();
                    System.exit(0);
                }
            }
        });
        frame.setVisible(true);
        applet.init();
    }
}
