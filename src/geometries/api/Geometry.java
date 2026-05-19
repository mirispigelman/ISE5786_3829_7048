package geometries.api;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * Abstract class for all geometric objects in the scene.
 * All geometries must implement a method to calculate their normal.
 * @author Naama Shafer
 * @author Miri Shpigelman
 */
public abstract class Geometry extends Intersectable {
    private Color _emission = Color.BLACK;
    private Material _material = new Material(); // Default material

    /**
     * Getter for emission light
     * @return the emission color
     */
    public Color getEmission() {
        return _emission;
    }

    /**
     * Setter for emission light - chaining method
     * @param emission the emission color
     * @return this geometry object
     */
    public Geometry setEmission(Color emission) {
        this._emission = emission;
        return this;
    }

    /**
     * Getter for material
     * @return the material
     */
    public Material getMaterial() {
        return _material;
    }

    /**
     * Setter for material - chaining method
     * @param material the material to set
     * @return this geometry object
     */
    public Geometry setMaterial(Material material) {
        this._material = material;
        return this;
    }

    /**
     * Calculates the normal vector to the geometry at a given point
     * @param p the point on the geometry
     * @return the normal vector
     */
    public abstract Vector getNormal(Point p);
}