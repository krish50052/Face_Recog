package src;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TSCDTest {
    @Test
    void startsUntrainedWithoutEigenvectorsOrAverageFace() {
        TSCD classifier = new TSCD();

        assertFalse(classifier.isTrained());
        assertEquals(0, classifier.getNumEigenVectors());
        assertNull(classifier.getAverageFace());
    }
}
