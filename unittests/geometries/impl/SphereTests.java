package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;

import geometries.impl.impl.Sphere;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for {@link geometries.impl.Sphere}
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
class SphereTests {
    /** Test method for {@link geometries.impl.Sphere#getNormal(primitives.Point)}. */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple getNormal test
        Sphere sp = new Sphere(new Point(0, 0, 0), 1);
        Vector n = sp.getNormal(new Point(1, 0, 0));
        assertEquals(new Vector(1, 0, 0), n, "ERROR: Sphere getNormal() wrong result");
        assertEquals(1d, n.length(), 1e-6, "ERROR: Sphere normal should be unit length");
    }
}