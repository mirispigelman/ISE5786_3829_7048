package renderer;
import geometries.api.Intersectable.Intersection;
import primitives.Color;
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
        // 1. Get all intersections (using the new NVI method)
        var intersections = _scene.geometries.calcIntersections(ray);

        // 2. If no intersections, return background color
        if (intersections == null) {
            return _scene.background;
        }

        // 3. Find the closest intersection point and its geometry
        var closestIntersection = ray.findClosestIntersection(intersections);

        // 4. Calculate the color at this intersection
        return calcColor(closestIntersection);
    }

    /**
     * Computes the color at a specific point.
     * At this stage, only returns the ambient light intensity.[cite: 3]
     * @param intersection the point on a geometry
     * @return the color at the point
     */
    private Color calcColor(Intersection intersection) {
        // Formula: Ambient Light Intensity + Geometry Emission Light
        return _scene.ambientLight.getIntensity()
                .scale(intersection.material.kA)
                .add(intersection.geometry.getEmission());
    }
}