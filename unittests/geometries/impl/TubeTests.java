package geometries.impl;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for {@link Tube}
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
class TubeTests {
    @Test
    void testGetNormal() {
        Ray ray = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Tube tube = new Tube(1.0,ray);

        // ============ Equivalence Partitions Tests ==============
        // EP01: Simple point on the tube
        assertEquals(new Vector(1, 0, 0), tube.getNormal(new Point(1, 0, 2)), "ERROR: Tube getNormal() wrong");

        // =============== Boundary Values Tests ==================
        // BV01: Point orthogonal to ray head
        assertEquals(new Vector(1, 0, 0), tube.getNormal(new Point(1, 0, 0)), "ERROR: Tube getNormal() at head wrong");

        // BV02: Testing when (P - P0) is orthogonal to the axis direction
// In this case, the dot product is 0, and o should be the ray origin.
        Ray ray2 = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
        Tube tube2 = new Tube(1.0, ray2);
        assertEquals(new Vector(0, 1, 0), tube2.getNormal(new Point(0, 1, 0)),
                "ERROR: getNormal() failed when point is orthogonal to ray origin");
    }
}