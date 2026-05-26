package renderer;

import geometries.api.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import scene.Scene;
import primitives.Vector;
import primitives.Point;
import primitives.Ray;

/**
 * Basic implementation of a ray tracer.
 */
class SimpleRayTracer extends RayTracerBase {
    private static final double DELTA = 0.1;

    /**
     * Checks if a point is unshaded (has light visibility) from a specific light source.
     * @param intersection the intersection point context
     * @return true if unshaded, false if shaded
     */
    private boolean unshaded(Intersection intersection, lighting.LightSource lightSource) {
        // 1. Calculate shadow ray direction (opposite to light vector l)
        Vector pointToLight = intersection.l.scale(-1);

        // 2. Shift the ray head along the normal to avoid self-shadowing
        double sign = intersection.lNormal > 0 ? DELTA : -DELTA;
        Vector deltaVector = intersection.normal.scale(intersection.lNormal > 0 ? -DELTA : DELTA);
        Point rayHead = intersection.point.add(deltaVector);

        // 3. Construct the shadow ray
        Ray shadowRay = new Ray(rayHead, pointToLight);

        // 4. Find all intersections along the shadow ray
        var shadowIntersections = _scene.geometries.findIntersections(shadowRay);

        // If no intersections are found, the point is completely unshaded
        if (shadowIntersections == null) {
            return true;
        }

        // 5. Get the distance from the intersection point to the light source using the parameter
        double lightDistance = lightSource.getDistance(intersection.point);

        // 6. Loop over shadow intersections and filter by distance
        for (Point geoIntersection : shadowIntersections) {
            // Since geoIntersection is already a Point object, we pass it directly
            double distance = rayHead.distance(geoIntersection);

            // If the obstacle is closer than the light source, it blocks the light
            if (distance < lightDistance) {
                return false;
            }
        }

        // If all obstacles are further away than the light source, it's unshaded
        return true;
    }
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
        if (!preprocessIntersection(closestIntersection, ray.direction())) {
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
        return  _scene.ambientLight.getIntensity().scale(intersection.material.kA) //
                .add(calcLocalEffects(intersection)); //
    }

    /**
     * Calculates the local lighting effects (Diffuse + Specular) from all light sources in the scene.
     * @param intersection the intersection point context
     * @return the total accumulated color from local light sources
     */
    private Color calcLocalEffects(Intersection intersection) {
        // Start with the emission color of the geometry
        Color color = intersection.geometry.getEmission();

        // Loop through all the light sources in the scene
        for (LightSource lightSource : _scene.lights) {
            // 1.ג + 1.ד: Add the unshaded check to verify there's no obstacle between the point and the light
            if (preprocessLightSource(intersection, lightSource) && unshaded(intersection, lightSource)) {
                Color lightIntensity = lightSource.getIntensity(intersection.point);

                // Add diffusive and specular light effects only if unshaded
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