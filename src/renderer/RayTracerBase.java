package renderer;

import primitives.Color;
import primitives.Ray;
import geometries.api.Intersectable.Intersection; // Required to access the static nested Intersection class
import lighting.LightSource; // Required for the LightSource interface
import scene.Scene;
import primitives.Vector; 
/**
 * Base class for all ray tracers.
 * This class is immutable.
 */
abstract class RayTracerBase {
    /** The scene to be traced */
    protected Scene _scene;

    /**
     * Constructor receiving the scene.
     * @param scene the scene
     */
    RayTracerBase(Scene scene) {
        _scene = scene;
    }

    /**
     * Trace a ray and return the color.
     * @param ray the ray
     * @return the calculated color
     */
    abstract Color traceRay(Ray ray);
    /**
     * Preprocesses geometric fields of the intersection that do not depend on a specific light source.
     * @param intersection the intersection point context
     * @param v the direction vector of the ray
     * @return false if the ray is tangent to the surface (vNormal == 0), true otherwise
     */
    protected boolean preprocessIntersection(Intersection intersection, Vector v) {
        // Calculate and cache the normal vector at the intersection point
        intersection.normal = intersection.geometry.getNormal(intersection.point);

        // Cache the incoming ray direction vector
        intersection.v = v;

        // Calculate and cache the dot product v * n, aligned to zero to handle numeric noise
        intersection.vNormal = primitives.Util.alignZero(v.dotProduct(intersection.normal));

        // If vNormal is 0, the ray is tangent to the surface, making local light calculations irrelevant
        return intersection.vNormal != 0;
    }

    /**
     * Preprocesses fields of the intersection that depend on a specific light source.
     * @param intersection the intersection point context
     * @param light the light source being evaluated
     * @return true if the light source and the viewer are on the same side of the surface, false otherwise
     */
    protected boolean preprocessLightSource(Intersection intersection, LightSource light) {
        // Cache the active light source being evaluated
        intersection.light = light;

        // Calculate and cache the light direction vector from the source to the point
        intersection.l = light.getL(intersection.point);

        // Calculate and cache the dot product l * n
        intersection.lNormal = primitives.Util.alignZero(intersection.l.dotProduct(intersection.normal));

        // Check side consistency: both light and viewer must be on the same side of the surface
        return intersection.lNormal * intersection.vNormal > 0;
    }
}