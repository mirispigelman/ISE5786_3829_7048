package geometries.impl;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents an infinite tube in 3D space.
 * * @author Naama Shafer
 * @author Miri Shpigelman
 */
public class Tube extends RadialGeometry {
    protected final Ray _axis;

    /**
     * Constructor for Tube.
     *
     * @param radius The radius
     * @param axis   The axis ray
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this._axis = axis;
    }

    @Override
    public Vector getNormal(Point point) {
        return null;
    }

    @Override
    public String toString() {
        return "Tube: axis=" + _axis + ", radius=" + _radius;
    }
}