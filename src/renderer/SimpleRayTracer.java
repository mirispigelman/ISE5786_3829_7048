package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

/**
 * Basic implementation of a ray tracer.
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructor.[cite: 3]
     * @param scene the scene
     */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        // Find all intersections of the ray with the scene geometries[cite: 3]
        var intersections = _scene.geometries.findIntersections(ray);

        // If there are no intersections, return the background color[cite: 3]
        if (intersections == null)
            return _scene.background;

        // Find the closest point to the ray head[cite: 3]
        Point closestPoint = ray.findClosestPoint(intersections);

        // Return the color computed at that point[cite: 3]
        return calcColor(closestPoint);
    }

    /**
     * Computes the color at a specific point.
     * At this stage, only returns the ambient light intensity.[cite: 3]
     * @param intersection the point on a geometry
     * @return the color at the point
     */
    private Color calcColor(Point intersection) {
        return _scene.ambientLight.getIntensity();
    }
}