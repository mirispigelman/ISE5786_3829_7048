package geometries.api;

import primitives.Point;
import primitives.Ray;
import geometries.api.Geometry;
import primitives.Material;
import java.util.List;
import java.util.Objects;

/**
 * Interface for all geometries that can be intersected by a ray
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
public abstract class Intersectable {

    /**
     * Helper class - Intersection (PDS)
     * Stores a geometry and its intersection point.
     */
    public static final class Intersection {
        /** The geometry that was intersected */
        public final Geometry geometry;
        /** The point of intersection */
        public final Point point;
        public final Material material;

        /**
         * Constructor for Intersection
         * @param geometry the geometry [cite: 28]
         * @param point the point [cite: 28]
         */
        public Intersection(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
            this.material = (geometry != null) ? geometry.getMaterial() : new Material();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Intersection that = (Intersection) o;
            return this.geometry == that.geometry && Objects.equals(this.point, that.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(geometry, point);
        }

        @Override
        public String toString() {
            return "Intersection: geometry=" + geometry + ", point=" + point;
        }
    }

    /**
     * Public NVI method to find intersections as points.
     * This method is final and cannot be overridden[cite: 40].
     * @param ray the ray
     * @return list of intersection points
     */
    public final List<Point> findIntersections(Ray ray) {
        var intersections = calcIntersections(ray);
        return intersections == null ? null
                : intersections.stream()
                .map(intersection -> intersection.point)
                .toList();
    }

    /**
     * Public NVI method to find intersections with their geometries.
     * This method is final and cannot be overridden[cite: 37].
     * @param ray the ray
     * @return list of intersections
     */
    public final List<Intersection> calcIntersections(Ray ray) {
        return calcIntersectionsHelper(ray);
    }

    /**
     * Abstract helper method for calculating intersections.
     * To be implemented by each geometry.
     * @param ray the ray
     * @return list of intersections
     */
    protected abstract List<Intersection> calcIntersectionsHelper(Ray ray);
}