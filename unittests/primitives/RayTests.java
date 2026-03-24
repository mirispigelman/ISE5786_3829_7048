package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link primitives.Ray}
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
class RayTests {

    /** Test method for {@link primitives.Ray#Ray(Point, Vector)}. */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple ray construction
        assertDoesNotThrow(() -> new Ray(new Point(1, 2, 3), new Vector(1, 0, 0)),
                "ERROR: Ray construction failed");

        // =============== Boundary Values Tests ==================
        // אין כרגע מקרי קצה מיוחדים ל-Ray חוץ מוקטור האפס,
        // אבל וקטור האפס כבר נבדק בבנאי של Vector עצמו.
    }
}