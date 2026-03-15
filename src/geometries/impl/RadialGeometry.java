package geometries.impl;

import geometries.api.Geometry;

/**
 * Abstract class for all geometries with a radius.
 * * @author Naama Shafer
 * @author Miri Shpigelman
 */
public abstract class RadialGeometry extends Geometry {
    protected final double _radius;
    protected final double _radiusSquared;

    /**
     * Constructor for RadialGeometry.
     *
     * @param radius The radius of the geometry
     */
    public RadialGeometry(double radius) {
        this._radius = radius;
        this._radiusSquared = radius * radius;
    }
}