package geometries.impl;

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
    public List<Point> findIntersections(Ray ray) {

        // שלב 1: מציאת חיתוך עם המישור של המשולש
        // הערה: מכיוון ש-Triangle יורש מ-Polygon, אפשר להשתמש במישור שלו
        var intersections = _vertices.get(0).add(_vertices.get(1).subtract(_vertices.get(0))).equals(_vertices.get(2)) ? null :
                new Plane(_vertices.get(0), _vertices.get(1), _vertices.get(2)).findIntersections(ray);

        // דרך פשוטה יותר אם כבר יש לך שדה plane ב-Polygon:
        // var intersections = plane.findIntersections(ray);

        if (intersections == null) return null;

        Point p0 = ray.origin();
        Vector v = ray.direction();

        Vector v1 = _vertices.get(0).subtract(p0);
        Vector v2 = _vertices.get(1).subtract(p0);
        Vector v3 = _vertices.get(2).subtract(p0);

        Vector n1 = v1.crossProduct(v2).normalize();
        Vector n2 = v2.crossProduct(v3).normalize();
        Vector n3 = v3.crossProduct(v1).normalize();

        double d1 = v.dotProduct(n1);
        double d2 = v.dotProduct(n2);
        double d3 = v.dotProduct(n3);

        // אם כל המכפלות הסקלריות הן באותו סימן (כולן חיוביות או כולן שליליות) - הנקודה בפנים
        if ((d1 > 0 && d2 > 0 && d3 > 0) || (d1 < 0 && d2 < 0 && d3 < 0)) {
            return intersections;
        }
        return null;
    }
}
