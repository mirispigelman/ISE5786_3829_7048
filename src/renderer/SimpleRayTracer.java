package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import scene.Scene;

/**
 * Basic implementation of a ray tracer.
 */
class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructor.
     * @param scene the scene
     */
    SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        // 1. Get all intersections
        var intersections = _scene.geometries.calcIntersections(ray);

        // 2. If no intersections, return background color
        if (intersections == null) {
            return _scene.background;
        }

        // 3. Find the closest intersection point and its geometry
        var closestIntersection = ray.findClosestIntersection(intersections);

        // 4. Preprocess the geometric fields using the ray's public direction() method
        // Using explicit primitives.Vector to bypass package/import mismatches
        if (!preprocessIntersection(closestIntersection, (primitives.Vector) ray.direction())) {
            return _scene.ambientLight.getIntensity();
        }

        // 5. Calculate the color at this intersection using the single-parameter method
        return calcColor(closestIntersection);
    }

    /**
     * Calculates the color at a given intersection point using Phong model.
     * Matches the required single-parameter signature according to the stage guidelines.
     * @param intersection the intersection point
     * @return the calculated color
     */
    public Color calcColor(Intersection intersection) {
        // Sum up: Ambient Light + Emission Light + Local Effects (Diffuse + Specular)
        return _scene.ambientLight.getIntensity()
                .add(intersection.geometry.getEmission())
                .add(calcLocalEffects(intersection));
    }

    /**
     * Calculates the local lighting effects (Diffuse + Specular) from all light sources in the scene.
     * @param intersection the intersection point context
     * @return the total accumulated color from local light sources
     */
    private Color calcLocalEffects(Intersection intersection) {
        Color color = Color.BLACK;

        // Loop through all external light sources in the scene [cite: 57]
        for (LightSource lightSource : _scene.lights) {

            // Execute preprocessing for the current light source (checks side consistency and caches variables) [cite: 52]
            if (preprocessLightSource(intersection, lightSource)) {

                // Get the continuous light intensity at the intersection point (including distance attenuation)
                Color lightIntensity = lightSource.getIntensity(intersection.point);

                // Accumulate: color + I_L * (calcDiffuse + calcSpecular) [cite: 62]
                color = color.add(lightIntensity.scale(
                        calcDiffuse(intersection).add(calcSpecular(intersection))
                ));
            }
        }

        return color;
    }

    /**
     * Calculates the diffuse reflection component of the Phong model. [cite: 58]
     * @param intersection the intersection point context
     * @return the calculated diffuse component as a Double3
     */
    private Double3 calcDiffuse(Intersection intersection) {
        // kD * |lNormal| (using cached lNormal) [cite: 58]
        return intersection.material.kD.scale(Math.abs(intersection.lNormal));
    }

    /**
     * Calculates the specular reflection component of the Phong model. [cite: 59]
     * @param intersection the intersection point context
     * @return the calculated specular component as a Double3
     */
    private Double3 calcSpecular(Intersection intersection) {
        // Using explicit primitives.Vector to ensure type safety with the cache
        primitives.Vector l = (primitives.Vector) intersection.l;
        primitives.Vector n = (primitives.Vector) intersection.normal;
        double ln = intersection.lNormal;

        // Calculate the reflection vector: r = l - 2 * (l * n) * n [cite: 62]
        primitives.Vector r = l.subtract(n.scale(2 * ln));

        // Calculate the dot product between the viewer vector v and the reflection vector r
        double vr = ((primitives.Vector) intersection.v).dotProduct(r);

        // According to Phong model: max(0, -v * r) [cite: 62]
        double minusVr = primitives.Util.alignZero(-vr);
        if (minusVr <= 0) {
            return Double3.ZERO;
        }

        // kS * (max(0, -v * r))^nShininess [cite: 62]
        return intersection.material.kS.scale(Math.pow(minusVr, intersection.material.nShininess));
    }
}