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
    }

    /**
     * Test method for {@link primitives.Ray#getPoint(double)}.
     */
    @Test
    void testGetPoint() {
        Ray ray = new Ray(new Point(1, 0, 0), new Vector(1, 0, 0));

        // ============ Equivalence Partitions Tests ==============

        // EP01: t > 0 - The point should be in the direction of the ray
        assertEquals(new Point(2, 0, 0), ray.getPoint(1),
                "getPoint(t) with positive t failed");

        // EP02: t < 0 - The point should be "behind" the ray head
        assertEquals(new Point(0, 0, 0), ray.getPoint(-1),
                "getPoint(t) with negative t failed");

        // =============== Boundary Values Tests ==================

        // BV01: t = 0 - The point should be exactly the ray's origin
        assertEquals(new Point(1, 0, 0), ray.getPoint(0),
                "getPoint(t) with zero t failed");
    }
}