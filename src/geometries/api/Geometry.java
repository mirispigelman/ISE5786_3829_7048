package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Interface for all geometric objects in the scene.
 */
public abstract class Geometry {
    /**
     * Calculates the unit normal vector to the geometry at a given point.
     *
     * @param point Point on the surface of the geometry
     * @return The normal vector
     */
    public abstract Vector getNormal(Point point);
}