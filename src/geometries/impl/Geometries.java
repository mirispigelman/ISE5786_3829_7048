package geometries.impl;

import geometries.api.BoundingBox;
import geometries.api.Intersectable;

import primitives.Ray;


import java.util.ArrayList;
import java.util.List;

/**
 * Composite class for all geometric objects in the scene.
 * Holds a list of geometries and finds intersections with all of them.
 * * @author Naama Shafer
 * @author Miri Shpigelman
 */
public class Geometries extends Intersectable {
    /**
     * List of intersectable geometries, initialized upon definition.
     */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Default empty constructor.
     */
    public Geometries() {}

    /**
     * Constructor with initial geometries.
     * Uses the add method to maintain the DRY principle.
     * * @param geometries List of geometries to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds geometries to the collection using Java's built-in methods.
     * * @param geometries List of geometries to add
     */
    public void add(Intersectable... geometries) {
        if (geometries != null) {
            _geometries.addAll(List.of(geometries));
        }
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        // BVH Acceleration Optimization:
        // If BVH is enabled and a bounding box exists, check for a box intersection first.
        // If the ray misses the bounding box, skip checking all individual geometries inside.
        if (this.bvhEnabled && this.getBox() != null) {
            if (!this.getBox().intersect(ray)) {
                return null; // Ray missed the bounding box, skip the loop entirely
            }
        }

        List<Intersection> result = null;

        // Iterate through all geometries using a foreach loop (KIS principle)
        for (Intersectable item : _geometries) {
            // We MUST call the public calcIntersections method, not the helper
            var itemIntersections = item.calcIntersections(ray);

            if (itemIntersections != null) {
                // Lazy initialization: create the list only when the first intersection is found
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(itemIntersections);
            }
        }

        // Return the list of intersections, or null if no intersections were found
        return result;
    }
    @Override
    public void getOrCreateBox() {
        // Use getBox() to check if the bounding box is already calculated
        if (this.getBox() != null) return;

        // Ensure all internal geometries calculate or retrieve their bounding boxes first
        for (Intersectable geo : this._geometries) {
            geo.getOrCreateBox();
        }

        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        boolean hasValidBox = false;

        // Find the absolute min and max bounds that encompass all geometries inside
        for (Intersectable geo : this._geometries) {
            geometries.api.BoundingBox geoBox = geo.getBox(); // Retrieve using the public getter
            if (geoBox != null) {
                hasValidBox = true;
                if (geoBox.minX < minX) minX = geoBox.minX;
                if (geoBox.maxX > maxX) maxX = geoBox.maxX;
                if (geoBox.minY < minY) minY = geoBox.minY;
                if (geoBox.maxY > maxY) maxY = geoBox.maxY;
                if (geoBox.minZ < minZ) minZ = geoBox.minZ;
                if (geoBox.maxZ > maxZ) maxZ = geoBox.maxZ;
            }
        }

        // If at least one inner geometry has a valid box, assign the combined box to the parent class field
        if (hasValidBox) {
            this.box = new geometries.api.BoundingBox(minX, maxX, minY, maxY, minZ, maxZ);
        }
    }
    @Override
    public Geometries setBVH(boolean enable) {
        super.setBVH(enable);
        // Pass the BVH flag down to all underlying geometries recursively
        for (Intersectable geo : this._geometries) {
            geo.setBVH(enable);
        }
        return this;
    }
}