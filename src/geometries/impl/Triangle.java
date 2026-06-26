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

}
