package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;

import geometries.impl.impl.Triangle;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for {@link geometries.Triangle}
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
}