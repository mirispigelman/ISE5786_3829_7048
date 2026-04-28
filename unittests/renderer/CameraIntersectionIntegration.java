package renderer;

import geometries.impl.Sphere;
import geometries.impl.api.Intersectable;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for Camera Ray construction and Geometric bodies intersections.
 * [cite: 94, 95]
 */
class CameraIntersectionIntegration {

    /**
     * Constant for 3x3 resolution used in all integration tests [cite: 100, 346, 352]
     */
    private static final int RES_3 = 3;

    /**
     * Helper method to count intersections and assert against expected value.
     * Performs Act and Assert stages. [cite: 103, 105, 107, 350]
     * * @param camera   The camera to construct rays from
     *
     * @param body     The geometric body (Intersectable) [cite: 105]
     * @param expected Expected total number of intersections [cite: 105]
     * @param testName Name of the test for error messages [cite: 105]
     */
    private void assertIntersectionsCount(Camera camera, Intersectable body, int expected, String testName) {
        int count = 0;

        // Act: Iterate through all pixels in 3x3 resolution [cite: 105, 347]
        for (int i = 0; i < RES_3; ++i) {
            for (int j = 0; j < RES_3; ++j) {
                // Construct ray through pixel [cite: 107, 347]
                Ray ray = camera.constructRay(RES_3, RES_3, j, i);

                // Calculate intersections with body [cite: 107, 348]
                var intersections = body.findIntersections(ray);

                // Sum up the amount of intersections [cite: 107, 348]
                if (intersections != null) {
                    count += intersections.size();
                }
            }
        }

        // Assert: Compare total count to expected [cite: 107, 349]
        assertEquals(expected, count, testName + ": Wrong amount of intersections");
    }

    @Test
    void testCameraRaySphereIntegration() {
        // Prepare common cameras [cite: 99, 100]
        Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3).setVpDistance(1).build();

        Camera camera2 = Camera.getBuilder()
                .setLocation(new Point(0, 0, 0.5))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3).setVpDistance(1).build();

        // Arrange & Act/Assert
        // TC01: Sphere r=1 (2 intersections)
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, -3), 1), 2, "TC01");

        // TC02: Sphere r=2.5 (18 intersections)
        assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -2.5), 2.5), 18, "TC02");

        // TC03: Sphere r=2 (10 intersections)
        assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -2), 2), 10, "TC03");

        // TC04: Sphere r=4 (9 intersections)
        assertIntersectionsCount(camera2, new Sphere(new Point(0, 0, -1), 4), 9, "TC04");

        // TC05: Sphere r=0.5 behind camera (0 intersections)
        assertIntersectionsCount(camera1, new Sphere(new Point(0, 0, 1), 0.5), 0, "TC05");
    }

    @Test
    void testCameraRayPlaneIntegration() {
        Camera camera = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3).setVpDistance(1).build();

        // TC01: Plane parallel to View Plane (9 intersections)
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), 9, "TC01");

        // TC02: Plane with acute angle to View Plane (9 intersections)
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -2), new Vector(0, 1, -2)), 9, "TC02");

        // TC03: Plane with obtuse angle (6 intersections)
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -2), new Vector(0, 1, -0.5)), 6, "TC03");
    }

    @Test
    void testCameraRayTriangleIntegration() {
        Camera camera = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3).setVpDistance(1).build();

        // TC01: Small triangle in front of center (1 intersection)
        assertIntersectionsCount(camera, new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 1, "TC01");

        // TC02: Medium triangle (2 intersections)
        assertIntersectionsCount(camera, new Triangle(new Point(0, 20, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), 2, "TC02");
    }
}