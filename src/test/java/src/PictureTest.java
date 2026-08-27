package src;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

class PictureTest {
    @Test
    void extractsGrayscalePixels() {
        BufferedImage image = new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.RED.getRGB());
        image.setRGB(1, 0, Color.WHITE.getRGB());

        Picture picture = new Picture(image);

        assertArrayEquals(new double[] {85.0, 255.0}, picture.getImagePixels());
    }

    @Test
    void displaysAndCropsPixels() {
        Picture picture = new Picture(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));
        int[] pixels = {Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), Color.WHITE.getRGB()};

        picture.display(pixels, 2, 2);
        assertEquals(2, picture.getImage().getWidth());
        assertEquals(2, picture.getImage().getHeight());

        picture.cropAndDisplay(pixels, 2, 2, 0, 1, 0, 1);
        assertEquals(1, picture.getImage().getWidth());
        assertEquals(1, picture.getImage().getHeight());
    }
}