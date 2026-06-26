package geometries.impl;

import geometries.api.Geometry;
import java.util.List;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;


import static primitives.Util.isZero;

/**
 * Represents a convex polygon in a 3D Cartesian coordinate system.
 * <p>
 * The polygon is defined by an ordered sequence of vertices.
 * All vertices must lie in the same plane and be arranged along the
 * polygon edge path.
 * </p>
 * <p>
 * The polygon must be convex.
 * </p>
 *
 * @author Dan Zilberstein
 */
public class Polygon extends Geometry {
    /**
     * Ordered list of polygon vertices
     */
    protected final List<Point> _vertices;
    /**
     * Plane containing the polygon
     */
    protected final Plane _plane;
    /**
     * Number of vertices
     */
    private final int _size;

    /**
     * Constructs a convex polygon from ordered vertices.
     * <p>
     * The vertices must:
     * </p>
     * <ul>
     * <li>Contain at least three points</li>
     * <li>Be ordered along the polygon edge path</li>
     * <li>Lie in the same plane</li>
     * <li>Form a convex polygon</li>
     * </ul>
     *
     * @param vertices polygon vertices in edge order
     * @throws IllegalArgumentException if the vertices do not form a valid convex
     *                                  polygon
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3)
            throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
        _vertices = List.of(vertices);
        _size = vertices.length;

        // Create the supporting plane using the first three vertices.
        // The plane stores the constant normal of the polygon.
        _plane = new Plane(vertices[0], vertices[1], vertices[2]);
        if (_size == 3) return; // no need for more tests for a Triangle

        Vector n = _plane.getNormal(vertices[0]);
        // Subtracting identical vertices would create a zero vector (illegal)
        Vector edge1 = vertices[_size - 1].subtract(vertices[_size - 2]);
        Vector edge2 = vertices[0].subtract(vertices[_size - 1]);

        // Cross product of consecutive edges determines orientation.
        // All edge pairs must produce the same sign relative to the normal,
        // otherwise the polygon is concave or vertices are unordered.
        boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
        for (var i = 1; i < _size; ++i) {
            // Test that the point is in the same plane as calculated originally
            if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
                throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
            // Test the consequent edges have
            edge1 = edge2;
            edge2 = vertices[i].subtract(vertices[i - 1]);
            if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
                throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
        }
    }

    @Override
    public Vector getNormal(Point point) {
        return _plane.getNormal(point);
    }


    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // Step 1: Find intersections with the supporting plane
        // We call the public findIntersections of the plane as per instructions
        var planeIntersections = _plane.findIntersections(ray);
        if (planeIntersections == null) return null;

        Point p0 = ray.origin();
        Vector v = ray.direction();

        // Step 2: Check if the intersection point is inside the polygon
        // Vector from ray origin to the first vertex
        Vector v1 = _vertices.get(_size - 1).subtract(p0);
        Vector v2 = _vertices.get(0).subtract(p0);

        // Calculate the first normal to determine the side
        Vector n = v1.crossProduct(v2);
        double s1 = v.dotProduct(n);
        if (isZero(s1)) return null;

        boolean positive = s1 > 0;

        // Iterate through all edges and verify they all produce the same sign
        for (int i = 1; i < _size; i++) {
            v1 = v2;
            v2 = _vertices.get(i).subtract(p0);
            n = v1.crossProduct(v2);
            double s = v.dotProduct(n);

            if (isZero(s) || (s > 0 != positive)) return null;
        }

        // Step 3: Return the intersection wrapped with the polygon (this)
        // Even though we used the plane for calculation, the geometry must be the polygon
        return List.of(new Intersection(this, planeIntersections.get(0)));
    }
    @Override
    public void getOrCreateBox() {
        // If the bounding box is already calculated, do not recalculate
        if (this.box != null) return;

        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        // Iterate through all vertices of the polygon to find the absolute bounding bounds
        for (primitives.Point p : this._vertices) {
            primitives.Vector v = p.equals(primitives.Point.ZERO) ? null : p.subtract(primitives.Point.ZERO);
            double px = v == null ? 0 : v.dotProduct(primitives.Vector.AXIS_X);
            double py = v == null ? 0 : v.dotProduct(primitives.Vector.AXIS_Y);
            double pz = v == null ? 0 : v.dotProduct(primitives.Vector.AXIS_Z);

            if (px < minX) minX = px;
            if (px > maxX) maxX = px;
            if (py < minY) minY = py;
            if (py > maxY) maxY = py;
            if (pz < minZ) minZ = pz;
            if (pz > maxZ) maxZ = pz;
        }

        // Create the bounding box and save it in the protected field
        this.box = new geometries.api.BoundingBox(minX, maxX, minY, maxY, minZ, maxZ);
    }
}
