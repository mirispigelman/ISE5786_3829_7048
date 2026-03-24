package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;

import geometries.impl.impl.Plane;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for {@link geometries.Plane}
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
class PlaneTests {
    private static final double DELTA = 1e-6;

    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple correct plane
        assertDoesNotThrow(() -> new Plane(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0)),
                "Failed to construct a proper plane");

        // =============== Boundary Values Tests ==================
        // BV01: Identical points
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(1, 1, 1), new Point(1, 1, 1), new Point(0, 1, 0)),
                "ERROR: Identical points should throw exception");

        // BV02: Collinear points
        assertThrows(IllegalArgumentException.class, () ->
                        new Plane(new Point(1, 1, 1), new Point(2, 2, 2), new Point(3, 3, 3)),
                "ERROR: Collinear points should throw exception");
    }

    @Test
    void testGetNormal() {
        Plane pl = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1)); // תיקון: נקודה לפני וקטור        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple getNormal test
        assertEquals(new Vector(0, 0, 1), pl.getNormal(new Point(1, 2, 1)), "ERROR: Plane getNormal() wrong");

        // =============== Boundary Values Tests ==================
        // BV01: Point is the reference point q0
        assertEquals(new Vector(0, 0, 1), pl.getNormal(new Point(0, 0, 1)), "ERROR: getNormal() at q0 wrong");

        // Check if the normal is normalized
        assertEquals(1, pl.getNormal(new Point(0, 0, 1)).length(), DELTA, "Plane normal must be a unit vector");
    }
}