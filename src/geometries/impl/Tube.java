package geometries.impl;

import java.util.List;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents an infinite tube in 3D space.
 * * @author Naama Shafer
 *
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
    public Vector getNormal(Point p) {
        // Finding the projection of the point on the tube's axis
        // t = dir * (p - p0)
        double t = _axis.direction().dotProduct(p.subtract(_axis.origin()));

        // The point on the axis that is exactly "opposite" to point P
        Point o = _axis.origin();

        // If t is not 0, the projection is not the ray head
        if (t != 0) {
            o = _axis.origin().add(_axis.direction().scale(t));
        }

        // The normal is the vector from the point on the axis (o) to the point on the surface (p)
        return p.subtract(o).normalize();
    }

    @Override
    public String toString() {
        return "Tube: axis=" + _axis + ", radius=" + _radius;
    }


    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // Currently not implemented, returning null as in the original code
        return null;
    }
    @Override
    public void getOrCreateBox() {
        // A tube is infinite along its axis, so it cannot be enclosed in a finite bounding box.
        // We leave this.box as null so the BVH optimization safely skips bounding box checks for tubes.
        this.box = null;
    }
}