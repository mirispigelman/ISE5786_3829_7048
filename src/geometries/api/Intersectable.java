package geometries.api;

import primitives.Point;
import primitives.Ray;
import java.util.List;

/**
 * Interface for all geometries that can be intersected by a ray
 */
public abstract class Intersectable {
    /**
     * Finds intersections between a ray and the geometry
     * @param ray the ray
     * @return list of intersection points, or null if there are none
     */
    public abstract List<Point> findIntersections(Ray ray);
}