package primitives;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link primitives.Vector}
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
class VectorTests {
    private static final Vector V1 = new Vector(1, 2, 3);
    private static final double DELTA = 1e-6;

    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple addition
        assertEquals(new Vector(2, 3, 4), V1.add(new Vector(1, 1, 1)), "ERROR: Vector add Vector wrong");

        // =============== Boundary Values Tests ==================
        // BV01: Add opposite vector
        assertThrows(IllegalArgumentException.class, () -> V1.add(new Vector(-1, -2, -3)),
                "ERROR: Vector add opposite should throw exception");
    }

    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple dot product
        assertEquals(-28d, V1.dotProduct(new Vector(-2, -4, -6)), DELTA, "ERROR: dotProduct() wrong");

        // =============== Boundary Values Tests ==================
        // BV01: Orthogonal vectors
        assertEquals(0d, V1.dotProduct(new Vector(0, 3, -2)), DELTA, "ERROR: dotProduct() for orthogonal should be 0");
    }

    @Test
    void testCrossProduct() {
        Vector v2 = new Vector(0, 3, -2);
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple cross product
        Vector vr = V1.crossProduct(v2);
        assertEquals(V1.length() * v2.length(), vr.length(), DELTA, "ERROR: crossProduct() length wrong");
        assertEquals(0d, vr.dotProduct(V1), DELTA, "ERROR: crossProduct() result not orthogonal to v1");
        assertEquals(0d, vr.dotProduct(v2), DELTA, "ERROR: crossProduct() result not orthogonal to v2");

        // =============== Boundary Values Tests ==================
        // BV01: Parallel vectors
        assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(new Vector(2, 4, 6)),
                "ERROR: crossProduct() for parallel vectors should throw exception");
    }
}