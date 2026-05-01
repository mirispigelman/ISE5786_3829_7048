package renderer;

import geometries.api.Intersectable;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
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
     * Helper method to count intersections and assert against expected value.
     * Performs Act and Assert stages for any given resolution. [cite: 103, 105, 107, 350]
     * * @param camera   The camera to construct rays from
     *
     * @param body     The geometric body (Intersectable)
     * @param nX       Horizontal resolution (number of pixels)
     * @param nY       Vertical resolution (number of pixels)
     * @param expected Expected total number of intersections
     * @param testName Name of the test for error messages
     */
    private void assertIntersectionsCount(Camera camera, Intersectable body, int nX, int nY, int expected, String testName) {
        int count = 0;

        // Act: Iterate through all pixels according to given resolution [cite: 105, 347]
        for (int i = 0; i < nY; ++i) {
            for (int j = 0; j < nX; ++j) {
                // Construct ray through pixel using generic resolution [cite: 107, 347]
                Ray ray = camera.constructRay(nX, nY, j, i);

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
        // View Plane configuration variables
        final int nX = 3;
        final int nY = 3;
        final double width = 3;
        final double height = 3;
        final double distance = 1;

        // Prepare common cameras using variables [cite: 99, 100]
        final Camera camera1 = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(width, height).setVpDistance(distance).build();

        final Camera camera2 = Camera.getBuilder()
                .setLocation(new Point(0, 0, 0.5))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(width, height).setVpDistance(distance).build();

        // Arrange: Create bodies
        Sphere sphere1 = new Sphere(new Point(0, 0, -3), 1);
        Sphere sphere2 = new Sphere(new Point(0, 0, -2.5), 2.5);
        Sphere sphere3 = new Sphere(new Point(0, 0, -2), 2);
        Sphere sphere4 = new Sphere(new Point(0, 0, -1), 4);
        Sphere sphere5 = new Sphere(new Point(0, 0, 1), 0.5);

        // Act/Assert using the generic helper method
        assertIntersectionsCount(camera1, sphere1, nX, nY, 2, "TC01");
        assertIntersectionsCount(camera2, sphere2, nX, nY, 18, "TC02");
        assertIntersectionsCount(camera2, sphere3, nX, nY, 10, "TC03");
        assertIntersectionsCount(camera2, sphere4, nX, nY, 9, "TC04");
        assertIntersectionsCount(camera1, sphere5, nX, nY, 0, "TC05");
    }

    @Test
    void testCameraRayPlaneIntegration() {
        final int nX = 3;
        final int nY = 3;

        final Camera camera = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3).setVpDistance(1).build();

        // Arrange & Act/Assert
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), nX, nY, 9, "TC01");
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -2), new Vector(0, 1, -2)), nX, nY, 9, "TC02");
        assertIntersectionsCount(camera, new Plane(new Point(0, 0, -2), new Vector(0, 1, -0.5)), nX, nY, 6, "TC03");
    }

    @Test
    void testCameraRayTriangleIntegration() {
        final int nX = 3;
        final int nY = 3;

        final Camera camera = Camera.getBuilder()
                .setLocation(Point.ZERO)
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpSize(3, 3).setVpDistance(1).build();

        // Arrange & Act/Assert
        assertIntersectionsCount(camera, new Triangle(new Point(0, 1, -2), new Point(1, -1, -2), new Point(-1, -1, -2)), nX, nY, 1, "TC01");
        assertIntersectionsCount(camera, new Triangle(new Point(0, 20, -2), new Point(10, -10, -2), new Point(-10, -10, -2)), nX, nY, 9, "TC02");
    }
}