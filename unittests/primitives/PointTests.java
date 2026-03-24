package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link primitives.Point}
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
class PointTests {
    private static final Point P1 = new Point(1, 2, 3);
    private static final double DELTA = 1e-6;

    /** Test method for {@link primitives.Point#subtract(primitives.Point)}. */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple subtraction
        assertEquals(new Vector(1, 1, 1), new Point(2, 3, 4).subtract(P1),
                "ERROR: Point subtract Point does not work correctly");

        // =============== Boundary Values Tests ==================
        // BV01: Subtract point from itself (result is zero vector)
        assertThrows(IllegalArgumentException.class, () -> P1.subtract(P1),
                "ERROR: Subtract point from itself should throw exception");
    }

    /** Test method for {@link primitives.Point#add(primitives.Vector)}. */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple addition
        assertEquals(new Point(2, 3, 4), P1.add(new Vector(1, 1, 1)),
                "ERROR: Point add Vector does not work correctly");
    }

    /** Test method for {@link primitives.Point#distanceSquared(primitives.Point)}. */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple distance squared
        assertEquals(9d, P1.distanceSquared(new Point(1, 2, 6)), DELTA,
                "ERROR: distanceSquared() wrong result");
    }
}