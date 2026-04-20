package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link Triangle}
 *
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
class TriangleTests {
    @Test
    void testGetNormal() {
        Point p1 = new Point(0, 0, 1);
        Triangle tri = new Triangle(p1, new Point(1, 0, 0), new Point(0, 1, 0));
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple getNormal test
        Vector n = tri.getNormal(new Point(0.3, 0.3, 0.4));
        assertEquals(1, n.length(), 1e-6, "ERROR: Triangle normal should be unit length");
    }

    @Test
    void testFindIntersections() {
       
        // EP01: Inside triangle
        Triangle tr = new Triangle(new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1));
        var result = tr.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1)));
        assertNotNull(result, "Should be an intersection");
        assertEquals(1, result.size(), "Should be 1 point");

        // EP02: Outside against edge (0 points)
        assertNull(tr.findIntersections(new Ray(new Point(1, 1, -1), new Vector(0, 0, 1))),
                "Outside against edge");

        // EP03: Outside against vertex (0 points)
        assertNull(tr.findIntersections(new Ray(new Point(-1, -1, -1), new Vector(0, 0, 1))),
                "Outside against vertex");

        // =============== Boundary Values Tests ==================

        // BV01: On edge (0 points)
        assertNull(tr.findIntersections(new Ray(new Point(0.5, 0.5, 0), new Vector(0, 0, 1))),
                "On edge");

        // BV02: In vertex (0 points)
        assertNull(tr.findIntersections(new Ray(new Point(1, 0, -1), new Vector(0, 0, 1))),
                "In vertex");

        // BV03: On edge's continuation (0 points)
        assertNull(tr.findIntersections(new Ray(new Point(2, -1, -1), new Vector(0, 0, 1))),
                "On edge's continuation");
    }
}