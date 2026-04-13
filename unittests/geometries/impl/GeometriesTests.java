package geometries; // Assuming this file is in unittests/geometries

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

// These imports must match your new project structure:
import geometries.impl.Geometries;
import geometries.impl.Sphere;
import geometries.impl.Plane;
import geometries.impl.Triangle;
import primitives.*;
import java.util.List;

/**
 * Unit tests for Geometries class
 */
class GeometriesTests {

    @Test
    void testFindIntersections() {
        // Sphere at (0,0,10) with radius 1: Intersects at (0,0,9) and (0,0,11)
        Sphere sphere = new Sphere(new Point(0, 0, 10), 1d);
        // Plane at Z=5: Intersects at (0,0,5)
        Plane plane = new Plane(new Point(0, 0, 5), new Vector(0, 0, 1));
        // Triangle at Z=2: Intersects at (0,0,2)
        Triangle triangle = new Triangle(
                new Point(1, 1, 2),
                new Point(-1, 1, 2),
                new Point(0, -1, 2));

        Geometries geometries = new Geometries(sphere, plane, triangle);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Some shapes are intersected (Sphere and Plane, but not Triangle)
        // Ray starts at (0,0,4) and goes up: hits Plane (at 5) and Sphere (at 9 and 11)
        var resultEP = geometries.findIntersections(new Ray(new Point(0, 0, 4), new Vector(0, 0, 1)));
        assertNotNull(resultEP, "Some shapes should be intersected");
        assertEquals(3, resultEP.size(), "Sphere (2) + Plane (1) = 3 points");

        // =============== Boundary Values Tests ==================

        // BV01: Empty collection
        assertNull(new Geometries().findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, 1))),
                "Empty collection should return null");

        // BV02: No shape is intersected
        // Ray starts at (0,0,20) and goes up: nothing there
        assertNull(geometries.findIntersections(new Ray(new Point(0, 0, 20), new Vector(0, 0, 1))),
                "No shape should be intersected");

        // BV03: Only one shape is intersected (Plane)
        // Ray starts at (5, 5, 4) and goes up: only hits the plane (infinite)
        var resultBV3 = geometries.findIntersections(new Ray(new Point(5, 5, 4), new Vector(0, 0, 1)));
        assertNotNull(resultBV3, "Only Plane should be intersected");
        assertEquals(1, resultBV3.size(), "Only one point");

        // BV04: All shapes are intersected
        // Ray starts at (0,0,1) and goes up: hits Triangle (at 2), Plane (at 5), Sphere (at 9 and 11)
        var resultAll = geometries.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, 1)));
        assertNotNull(resultAll, "All shapes should be intersected");
        assertEquals(4, resultAll.size(), "Triangle(1) + Plane(1) + Sphere(2) = 4 points");
    }
}