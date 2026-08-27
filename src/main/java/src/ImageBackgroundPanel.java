package src;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * A JPanel that displays a background image.
 * The image is scaled to fit the panel's dimensions.
 * Provides a customizable background for the application interface.
 *
 * @author Aman Rahangdale
 * @version 1.0
 * @since 1.0
 */
public class ImageBackgroundPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String BACKGROUND_IMAGE_PATH = "/bkd.png";
    
    private BufferedImage backgroundImage;

    /**
     * Creates a new ImageBackgroundPanel with the default background image.
     * If the image cannot be loaded, a blank panel will be displayed.
     */
    public ImageBackgroundPanel() {
        loadBackgroundImage();
    }

    /**
     * Loads the background image from the specified path.
     * If the image cannot be loaded, a blank panel will be displayed.
     */
    private void loadBackgroundImage() {
        try (InputStream imageStream = ImageBackgroundPanel.class.getResourceAsStream(BACKGROUND_IMAGE_PATH)) {
            if (imageStream == null) {
                System.err.println("Background image not found: " + BACKGROUND_IMAGE_PATH);
                return;
            }
            backgroundImage = ImageIO.read(imageStream);
        } catch (IOException e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
