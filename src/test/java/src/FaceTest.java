package src;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FaceTest {
    @TempDir
    File temporaryDirectory;

    @Test
    void initializesFromImageAndStoresMetadata() throws IOException {
        Face face = faceFromImage(Color.RED);

        face.setClassification("person");
        face.setDescription("training image");

        assertEquals("person", face.getClassification());
        assertEquals("training image", face.getDescription());
        assertNotNull(face.getPicture());
        assertEquals(48, face.getPicture().getImage().getWidth());
        assertEquals(64, face.getPicture().getImage().getHeight());
        assertEquals(face.getFile(), face.getFile());
    }

    @Test
    void extractsColorChannelsAndAppliesMedianFilter() throws IOException {
        Face face = faceFromImage(Color.WHITE);

        assertEquals(Color.RED, face.getColor(Color.RED.getRGB()));

        face.getPicture().setSize(48, 64);
        int width = face.getPicture().getWidth();
        double[] pixels = new double[width * face.getPicture().getHeight()];
        int center = 10 + 10 * width;
        pixels[center] = 100;
        pixels[center - width - 1] = 1;
        pixels[center - width] = 2;
        pixels[center - width + 1] = 3;
        pixels[center - 1] = 4;
        pixels[center + 1] = 6;
        pixels[center + width - 1] = 7;
        pixels[center + width] = 8;
        pixels[center + width + 1] = 9;
        face.medianFilter(pixels, 3);

        assertEquals(3, pixels[center]);
    }

    private Face faceFromImage(Color color) throws IOException {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, color.getRGB());
            }
        }
        File file = new File(temporaryDirectory, "face.png");
        ImageIO.write(image, "png", file);
        return new Face(file);
    }
}
