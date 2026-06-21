package geometries.impl;

import geometries.api.BoundingBox;
import java.util.List;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;


/**
 * Class Triangle represents a two-dimensional triangle in 3D space.
 * Inherits from Polygon.
 *
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
public final class Triangle extends Polygon {

    /**
     * Constructor for Triangle receiving three points.
     * Passes the points to the Polygon constructor.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    public Vector getNormal(Point point) {
        return super.getNormal(point);
    }

    @Override
    public String toString() {
        return "Triangle: " + super.toString();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // Step 1: Find intersections with the plane containing the triangle
        // We call the public findIntersections of the supporting plane [cite: 56]
        var intersections = _plane.findIntersections(ray);
        if (intersections == null) return null;

        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Step 2: Calculate vectors from ray origin to triangle vertices
        Vector v1 = _vertices.get(0).subtract(p0);
        Vector v2 = _vertices.get(1).subtract(p0);
        Vector v3 = _vertices.get(2).subtract(p0);

        // Step 3: Calculate normals for each edge-plane
        Vector n1 = v1.crossProduct(v2).normalize();
        Vector n2 = v2.crossProduct(v3).normalize();
        Vector n3 = v3.crossProduct(v1).normalize();

        // Step 4: Check if the ray direction is on the same side of all edge-planes
        double d1 = v.dotProduct(n1);
        double d2 = v.dotProduct(n2);
        double d3 = v.dotProduct(n3);

        // The point is inside the triangle if all dot products have the same sign (and none are zero)
        if ((d1 > 0 && d2 > 0 && d3 > 0) || (d1 < 0 && d2 < 0 && d3 < 0)) {
            // Return the point wrapped in an Intersection object with 'this' triangle [cite: 328, 333-334]
            return List.of(new Intersection(this, intersections.get(0)));
        }

        return null;
    }
    @Override
    public void getOrCreateBox() {
        // If the bounding box is already calculated, do not recalculate
        if (this.box != null) return;

        // Extract coordinates for vertex 1
        Point p1 = _vertices.get(0);
        primitives.Vector v1 = p1.equals(primitives.Point.ZERO) ? null : p1.subtract(primitives.Point.ZERO);
        double p1x = v1 == null ? 0 : v1.dotProduct(primitives.Vector.AXIS_X);
        double p1y = v1 == null ? 0 : v1.dotProduct(primitives.Vector.AXIS_Y);
        double p1z = v1 == null ? 0 : v1.dotProduct(primitives.Vector.AXIS_Z);

        // Extract coordinates for vertex 2
        Point p2 = _vertices.get(1);
        primitives.Vector v2 = p2.equals(primitives.Point.ZERO) ? null : p2.subtract(primitives.Point.ZERO);
        double p2x = v2 == null ? 0 : v2.dotProduct(primitives.Vector.AXIS_X);
        double p2y = v2 == null ? 0 : v2.dotProduct(primitives.Vector.AXIS_Y);
        double p2z = v2 == null ? 0 : v2.dotProduct(primitives.Vector.AXIS_Z);

        // Extract coordinates for vertex 3
        Point p3 = _vertices.get(2);
        primitives.Vector v3 = p3.equals(primitives.Point.ZERO) ? null : p3.subtract(primitives.Point.ZERO);
        double p3x = v3 == null ? 0 : v3.dotProduct(primitives.Vector.AXIS_X);
        double p3y = v3 == null ? 0 : v3.dotProduct(primitives.Vector.AXIS_Y);
        double p3z = v3 == null ? 0 : v3.dotProduct(primitives.Vector.AXIS_Z);

        // Find min and max values for the X axis
        double minX = Math.min(p1x, Math.min(p2x, p3x));
        double maxX = Math.max(p1x, Math.max(p2x, p3x));

        // Find min and max values for the Y axis
        double minY = Math.min(p1y, Math.min(p2y, p3y));
        double maxY = Math.max(p1y, Math.max(p2y, p3y));

        // Find min and max values for the Z axis
        double minZ = Math.min(p1z, Math.min(p2z, p3z));
        double maxZ = Math.max(p1z, Math.max(p2z, p3z));

        // Create the bounding box and save it in the protected field inherited from Intersectable
        this.box = new BoundingBox(minX, maxX, minY, maxY, minZ, maxZ);
    }
}
