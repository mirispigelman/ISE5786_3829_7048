package geometries.impl;

import java.util.List;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Sphere}
 *
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
class SphereTests {
    /**
     * Test method for {@link Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple getNormal testS
        Sphere sp = new Sphere(new Point(0, 0, 0), 1);
        Vector n = sp.getNormal(new Point(1, 0, 0));
        assertEquals(new Vector(1, 0, 0), n, "ERROR: Sphere getNormal() wrong result");
        assertEquals(1d, n.length(), 1e-6, "ERROR: Sphere normal should be unit length");
    }

    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray's line is outside the sphere (0 points) [cite: 123, 373]
        assertNull(sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 1, 0))),
                "Ray's line out of sphere");

        // EP02: Ray starts before and crosses the sphere (2 points) [cite: 124, 375]
        Point p1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        Point p2 = new Point(1.53484692283495, 0.844948974278318, 0);
        List<Point> result = sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(3, 1, 0)));

        assertEquals(2, result.size(), "Wrong number of points");

        assertTrue(result.contains(p1) && result.contains(p2),
                "Ray crosses sphere twice but the points are not as expected");

        // EP03: Ray starts inside the sphere (1 point) [cite: 127, 381]
        assertEquals(1, sphere.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(1, 0, 0))).size(),
                "Ray starts inside sphere");

        // EP04: Ray starts after the sphere (0 points) [cite: 128, 382]
        assertNull(sphere.findIntersections(new Ray(new Point(2.5, 0, 0), new Vector(1, 0, 0))),
                "Ray starts after sphere");
        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere (but not the center)
        // BV01: Ray starts at sphere and goes inside (1 points)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, -1, 0))).size(),
                "Ray starts at sphere and goes inside");

        // BV02: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 1, 0))),
                "Ray starts at sphere and goes outside");

        // **** Group 2: Ray's line goes through the center
        // BV03: Ray starts before the sphere (2 points)
        List<Point> resultBV03 = sphere.findIntersections(new Ray(new Point(1, -2, 0), new Vector(0, 1, 0)));
        assertEquals(2, resultBV03.size(), "Ray through center should have 2 points");

        // BV04: Ray starts at sphere and goes inside (1 points)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, -1, 0), new Vector(0, 1, 0))).size(),
                "Ray starts at sphere through center");

        // BV05: Ray starts inside (1 points)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, 0.5, 0), new Vector(0, 1, 0))).size(),
                "Ray starts inside sphere through center");

        // BV06: Ray starts at the center (1 points)
        assertEquals(1, sphere.findIntersections(new Ray(new Point(1, 0, 0), new Vector(0, 1, 0))).size(),
                "Ray starts at sphere center");

        // BV07: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 1, 0))),
                "Ray starts at sphere through center going outside");

        // BV08: Ray starts after sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(1, 2, 0), new Vector(0, 1, 0))),
                "Ray starts after sphere through center");

        // **** Group 3: Ray's line is tangent to the sphere (all tests 0 points)
        // BV09: Ray starts before the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 0, 0))),
                "Tangent ray starts before");

        // BV10: Ray starts at the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))),
                "Tangent ray starts at point");

        // BV11: Ray starts after the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(2, 1, 0), new Vector(1, 0, 0))),
                "Tangent ray starts after");

        // **** Group 4: Special cases
        // BV12: Ray's line is outside sphere, ray is orthogonal to ray start to sphere's center line
        assertNull(sphere.findIntersections(new Ray(new Point(1, 2, 0), new Vector(1, 0, 0))),
                "Special case: ray orthogonal to center line");
    }

}