package geometries.api;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import primitives.Material;
import lighting.LightSource;
import geometries.api.BoundingBox; // <--- Clean import at the top of the file

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

        public Vector normal;
        public Vector v;
        public double vNormal;
        public LightSource light;
        public Vector l;
        public double lNormal;

        /**
         * Constructor for Intersection
         * @param geometry the geometry
         * @param point the point
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
     * This method is final and cannot be overridden.
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
     * This method is final and cannot be overridden.
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

    // --- BVH Acceleration Fields ---
    protected BoundingBox box; // Clean usage thanks to the import above
    protected boolean bvhEnabled = false;

    /**
     * Public getter to access the bounding box of this intersectable object.
     * @return the bounding box of the geometry
     */
    public BoundingBox getBox() {
        return this.box;
    }

    /**
     * Method to enable or disable the BVH mechanism from the unit tests.
     */
    public Intersectable setBVH(boolean enable) {
        this.bvhEnabled = enable;
        return this;
    }

    /**
     * Abstract method that every geometric body must implement to compute its own box.
     */
    public abstract void getOrCreateBox();
}