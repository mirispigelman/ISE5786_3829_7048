package geometries.impl;

import geometries.api.Intersectable;
import primitives.Point;
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
    public List<Point> findIntersections(Ray ray) {
        List<Point> result = null;

        // Iterate through all geometries using a foreach loop (KIS principle)
        for (Intersectable item : _geometries) {
            List<Point> itemPoints = item.findIntersections(ray);

            if (itemPoints != null) {
                // Lazy initialization: create the list only when the first intersection is found
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(itemPoints);
            }
        }

        // Return the list of points, or null if no intersections were found
        return result;
    }
}