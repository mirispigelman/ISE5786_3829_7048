package geometries.api;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract class for all geometric objects in the scene.
 * All geometries must implement a method to calculate their normal.
 * * @author Naama Shafer
 *
 * @author Miri Shpigelman
 */
public abstract class Geometry extends Intersectable {
    /**
     * Calculates the unit normal vector to the geometry at a given point.
     *
     * @param point Point on the surface of the geometry
     * @return The normal vector (normalized)
     */
    public abstract Vector getNormal(Point point);
}