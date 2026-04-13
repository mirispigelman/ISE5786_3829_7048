package geometries.impl;

import geometries.impl.impl.Plane;
import java.util.List;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link geometries.impl.impl.Plane}
 *
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

    @Test
    void testFindIntersections() {
        Plane pl = new Plane(new Point(0, 0, 1), new Vector(0, 0, 1)); // מישור Z=1

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects the plane (1 point)
        List<Point> result = pl.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1)));
        assertEquals(1, result.size(), "Must be 1 intersection point");
        assertEquals(new Point(1, 1, 1), result.get(0), "Wrong intersection point");

        // EP02: Ray does not intersect the plane (0 points)
        assertNull(pl.findIntersections(new Ray(new Point(0, 0, 2), new Vector(1, 1, 1))),
                "Ray starts above and goes away from plane");

        // =============== Boundary Values Tests ==================

        // **** Group: Ray is parallel to the plane
        // BV01: Ray included in the plane (0 points according to instructions)
        assertNull(pl.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 0, 0))),
                "Ray is included in the plane");

        // BV02: Ray parallel to the plane, not included (0 points)
        assertNull(pl.findIntersections(new Ray(new Point(1, 1, 2), new Vector(1, 0, 0))),
                "Ray is parallel to the plane");

        // **** Group: Ray is orthogonal to the plane
        // BV03: Ray starts before the plane (1 point)
        assertEquals(1, pl.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1))).size(),
                "Orthogonal ray starts before plane");

        // BV04: Ray starts at the plane (0 points)
        assertNull(pl.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, 1))),
                "Orthogonal ray starts at plane");

        // BV05: Ray starts after the plane (0 points)
        assertNull(pl.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 0, 1))),
                "Orthogonal ray starts after plane");

        // **** Group: Special cases
        // BV06: Ray starts at the plane, but not orthogonal or parallel (0 points)
        assertNull(pl.findIntersections(new Ray(new Point(1, 1, 1), new Vector(1, 1, 1))),
                "Ray starts at the plane point");

        // BV07: Ray starts at the reference point q0 (0 points)
        assertNull(pl.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 1, 1))),
                "Ray starts at the plane's reference point");
    }
}