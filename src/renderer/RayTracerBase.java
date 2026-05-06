package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

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
}