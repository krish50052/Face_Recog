package src;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class FeatureVectorTest {
    @Test
    void storesFeatureDataAndMetadata() {
        FeatureVector vector = new FeatureVector();
        double[] features = {1.5, -2.0};
        Face face = null;

        vector.setFeatureVector(features);
        vector.setClassification(4);
        vector.setFace(face);

        assertArrayEquals(features, vector.getFeatureVector());
        assertEquals(4, vector.getClassification());
        assertSame(face, vector.getFace());
    }
}