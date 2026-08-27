package src;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FeatureSpaceTest {
    @TempDir
    File temporaryDirectory;

    @Test
    void findsNearestFaceAndOrdersDistances() throws IOException {
        FeatureSpace space = new FeatureSpace();
        Face near = face("near", Color.RED);
        Face far = face("far", Color.BLUE);
        space.insertIntoDatabase(near, new double[] {1, 1, 1});
        space.insertIntoDatabase(far, new double[] {5, 5, 5});

        FeatureVector probe = vector(new double[] {1, 2, 1});

        assertEquals("near", space.closestFeature(FeatureSpace.EUCLIDEAN_DISTANCE, probe));
        assertEquals("near", space.knn(FeatureSpace.EUCLIDEAN_DISTANCE, probe, 1));
        FeatureSpace.FaceDistancePair[] ordered = space.orderByDistance(FeatureSpace.EUCLIDEAN_DISTANCE, probe);
        assertEquals(2, ordered.length);
        assertEquals("near", ordered[0].getFace().getClassification());
        assertEquals(Math.sqrt(41), ordered[1].getDist(), 0.000001);
    }

    @Test
    void returnsNullForAnEmptySpace() {
        FeatureSpace space = new FeatureSpace();
        FeatureVector probe = vector(new double[] {1, 2, 3});

        assertNull(space.closestFeature(FeatureSpace.EUCLIDEAN_DISTANCE, probe));
        assertNull(space.knn(FeatureSpace.EUCLIDEAN_DISTANCE, probe, 3));
        assertEquals(0, space.orderByDistance(FeatureSpace.EUCLIDEAN_DISTANCE, probe).length);
    }

    @Test
    void exposesThreeDimensionalNormalizedPoints() throws IOException {
        FeatureSpace space = new FeatureSpace();
        space.insertIntoDatabase(face("first", Color.RED), new double[] {0, 10, 20});
        space.insertIntoDatabase(face("second", Color.BLUE), new double[] {10, 20, 30});

        double[][] points = space.get3dFeatureSpace();

        assertEquals(54, points.length);
        assertArrayEquals(new double[] {0, 0, 0}, points[0]);
        assertArrayEquals(new double[] {100, 100, 100}, points[18]);
    }

    private FeatureVector vector(double[] features) {
        FeatureVector vector = new FeatureVector();
        vector.setFeatureVector(features);
        return vector;
    }

    private Face face(String classification, Color color) throws IOException {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, color.getRGB());
            }
        }
        File file = new File(temporaryDirectory, classification + ".png");
        ImageIO.write(image, "png", file);
        Face face = new Face(file);
        face.setClassification(classification);
        return face;
    }
}