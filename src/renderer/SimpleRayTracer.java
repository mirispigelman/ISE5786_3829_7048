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
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;
    private static final double TARGET_PLANE_DISTANCE = 100.0;
    /**
     * Checks if a point is unshaded (has light visibility) from a specific light source.
     * @param intersection the intersection point context
     * @return true if unshaded, false if shaded
     */
    private boolean unshaded(Intersection intersection, lighting.LightSource lightSource) {
        // 1. Calculate shadow ray direction (opposite to light vector l)
        Vector pointToLight = intersection.l.scale(-1);
        // 2. Shift the ray head along the normal to avoid self-shadowing
        Vector deltaVector = intersection.normal.scale(intersection.lNormal > 0 ? -DELTA : DELTA);
        Point rayHead = intersection.point.add(deltaVector);

        // 3. Construct the shadow ray
        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal);

        // 4. Find all intersections along the shadow ray (using calcIntersections to get geometries)
        var shadowIntersections = _scene.geometries.calcIntersections(shadowRay);

        // If no intersections are found, the point is completely unshaded
        if (shadowIntersections == null) {
            return true;
        }

        // 5. Get the distance from the intersection point to the light source
        double lightDistance = lightSource.getDistance(intersection.point);

        // 6. Loop over shadow intersections and filter by distance and transparency
        for (var geoIntersection : shadowIntersections) {
            double distance = rayHead.distance(geoIntersection.point);

            // If the obstacle is closer than the light source, check its transparency
            if (distance < lightDistance) {
                // Temporary transparency logic: only objects with kT lower than MIN_CALC_COLOR_K cause shadows
                if (geoIntersection.geometry.getMaterial().kT.isLowerThan(MIN_CALC_COLOR_K)) {
                    return false; // Opaque object blocks the light
                }
            }
        }

        // If all obstacles are further away or transparent enough, it's unshaded
        return true;
    }
    /**
     * Calculates the aggregated transparency/attenuation factor between
     * the intersection point and a light source.
     * @param intersection the intersection point context
     * @param lightSource the light source to check against
     * @return the cumulative attenuation factor as a Double3
     */
    private Double3 transparency(Intersection intersection, lighting.LightSource lightSource) {
        // 1. Calculate shadow ray direction (opposite to light vector l)
        Vector pointToLight = intersection.l.scale(-1);

        // 2 & 3. Construct the shadow ray using the constructor that handles DELTA shifting automatically
        Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal);

        // 4. Find all intersections along the shadow ray
        var shadowIntersections = _scene.geometries.calcIntersections(shadowRay);

        // If no intersections are found, the light is completely unattenuated (fully bright)
        if (shadowIntersections == null) {
            return Double3.ONE;
        }

        // 5. Get the distance from the intersection point to the light source
        double lightDistance = lightSource.getDistance(intersection.point);

        // This will store our accumulated transparency factor, starting at full light (1,1,1)
        Double3 kstr = Double3.ONE;

        // 6. Loop over shadow intersections and accumulate transparency
        for (var geoIntersection : shadowIntersections) {
            double distance = shadowRay.origin().distance(geoIntersection.point);

            // If the obstacle is closer than the light source, it attenuates the light
            if (distance < lightDistance) {
                // Multiply the cumulative factor by the object's transparency coefficient (kT)
                kstr = kstr.product(geoIntersection.geometry.getMaterial().kT);

                // If the light becomes too weak to matter, stop checking further obstacles
                if (kstr.isLowerThan(MIN_CALC_COLOR_K)) {
                    return Double3.ZERO;
                }
            }
        }

        return kstr;
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
        Intersection closestIntersection = findClosestIntersection(ray);

        if (closestIntersection == null) {
            return _scene.background;
        }

        if (!preprocessIntersection(closestIntersection, ray.direction())) {
            return _scene.ambientLight.getIntensity();
        }

        return calcColor(closestIntersection);
    }

    /**
     * Calculates the color at a given intersection point using Phong model.
     * Matches the required single-parameter signature according to the stage guidelines.
     * @param intersection the intersection point
     * @return the calculated color
     */



    public Color calcColor(Intersection intersection) {
        // 1. Calculate base local and global color effects using the recursive method
        Color baseColor = calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K);

        // 2. Get the light's raw ambient intensity
        Color ambientLight = _scene.ambientLight.getIntensity();

        // 3. Securely fetch the Ka factor using your project's Material getters
        var material = intersection.geometry.getMaterial();
        Double3 kA = (material == null) ? Double3.ONE : material.kA;

        // 4. Return the combined color according to the physics formula
        return baseColor.add(ambientLight.scale(kA));
    }

    /**
     * Recursive color calculation including global effects.
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        Color color = calcLocalEffects(intersection);

        if (level > 1) {
            color = color.add(calcGlobalEffects(intersection, level, k));
        }

        return color;
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
            if (preprocessLightSource(intersection, lightSource)) {
                // Calculate transparency factor (Double3)
                Double3 kstr = transparency(intersection, lightSource);

                // Only add diffusive and specular effects if light isn't completely blocked
                if (kstr.isGreaterThan(MIN_CALC_COLOR_K)) {
                    Color lightIntensity = lightSource.getIntensity(intersection.point).scale(kstr);

                    color = color.add(lightIntensity.scale(
                            calcDiffuse(intersection).add(calcSpecular(intersection))
                    ));
                }
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
    /**
     * Constructs a reflection ray from the intersection point.
     * @param intersection the intersection point context
     * @return the reflection ray
     */
    /**
     * Constructs a reflection ray from the intersection point.
     * @param intersection the intersection point context
     * @return the reflection ray
     */
    private Ray constructReflectedRay(Intersection intersection) {
        Vector v = (Vector) intersection.v;
        Vector n = (Vector) intersection.normal;
        double vn = intersection.vNormal;

        // Formula for reflection vector: r = v - 2 * (v * n) * n
        Vector r = v.subtract(n.scale(2 * vn)).normalize();

        // Using the new Ray constructor that handles the DELTA shifting automatically
        return new Ray(intersection.point, r, n);
    }

    /**
     * Constructs a refraction (transparency) ray from the intersection point.
     * @param intersection the intersection point context
     * @return the refracted ray
     */
    /**
     * Constructs a refraction (transparency) ray from the intersection point.
     * @param intersection the intersection point context
     * @return the refracted ray
     */
    private Ray constructRefractedRay(Intersection intersection) {
        Vector v = (Vector) intersection.v;
        Vector n = (Vector) intersection.normal;

        // In our model, refraction direction is exactly the same as the incoming ray direction (v)
        Vector t = v.normalize();

        // Using the new Ray constructor that handles the DELTA shifting automatically
        return new Ray(intersection.point, t, n);
    }
    /**
     * Calculates a single global lighting effect (reflection or refraction).
     * @param ray the secondary ray (reflected or refracted)
     * @param level recursion depth level
     * @param k cumulative attenuation factor
     * @param kx the attenuation factor of the current geometry (kT or kR)
     * @return the calculated color component for this global effect
     */
    /**
     * Calculates global lighting effects (reflection/refraction) supporting
     * super-sampling beams for glossy surfaces and diffuse glass.
     */
    private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx, Double3 blurFactor, int samplesCount) {
        // 1. Calculate the cumulative attenuation factor for the next step
        Double3 kkx = k.product(kx);

        // 2. Check the recursion stop conditions
        if (level == 0 || kkx.isLowerThan(MIN_CALC_COLOR_K)) {
            return Color.BLACK;
        }

        // 3. Feature Off Optimization: If no blur or single sample, trace exactly 1 ray
        if (blurFactor.equals(Double3.ZERO) || samplesCount <= 1) {
            var intersections = _scene.geometries.calcIntersections(ray);
            if (intersections == null) {
                return _scene.background.scale(kx);
            }
            var closestIntersection = ray.findClosestIntersection(intersections);
            if (!preprocessIntersection(closestIntersection, ray.direction())) {
                return Color.BLACK;
            }
            return calcColor(closestIntersection, level - 1, kkx).scale(kx);
        }

        // 4. Super-Sampling Beam Logic: Configure the blackboard for blurry effects
        double sizeRadius = blurFactor._d1() * TARGET_PLANE_DISTANCE;
        primitives.Blackboard blackboard = new primitives.Blackboard()
                .setSize(sizeRadius)
                .setSamplesCount(samplesCount)
                .setShape(primitives.SampleShape.CIRCLE) // Circular target area prevents directional distortions
                .setPattern(primitives.SamplePattern.JITTERED); // Jittered layout yields realistic smooth blur

        // Generate the 3D ray beam from our generic core infrastructure
        java.util.List<Ray> beam = ray.generateBeam(blackboard, TARGET_PLANE_DISTANCE);
        Color colorSum = Color.BLACK;

        // 5. Trace each recursive ray in the beam individually
        for (Ray beamRay : beam) {
            var intersections = _scene.geometries.calcIntersections(beamRay);
            if (intersections == null) {
                colorSum = colorSum.add(_scene.background);
            } else {
                var closestIntersection = beamRay.findClosestIntersection(intersections);
                if (preprocessIntersection(closestIntersection, beamRay.direction())) {
                    colorSum = colorSum.add(calcColor(closestIntersection, level - 1, kkx));
                }
            }
        }

        // 6. Average the accumulated beam light and scale by the geometry's attenuation factor
        return colorSum.reduce(beam.size()).scale(kx);
    }
    /**
     * Combines both global effects (reflection and refraction) for an intersection point.
     * @param intersection the intersection point context
     * @param level recursion depth level
     * @param k cumulative attenuation factor
     * @return the total accumulated color from both global effects
     */
    /**
     * Combines both global effects (reflection and refraction) supporting glossy and diffuse glass beams.
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        Color color = Color.BLACK;
        var material = intersection.material;

        // 1. Calculate Reflected Light Component (Glossy Mirror effect)
        Ray reflectedRay = constructReflectedRay(intersection);
        color = color.add(calcGlobalEffect(reflectedRay, level, k, material.kR, material.kG, material.materialSamples));

        // 2. Calculate Refracted Light Component (Diffuse Blurry Glass effect)
        Ray refractedRay = constructRefractedRay(intersection);
        color = color.add(calcGlobalEffect(refractedRay, level, k, material.kT, material.kDG, material.materialSamples));

        return color;
    }
    /**
     * Finds the closest intersection point for a given ray.
     * @param ray the ray being traced
     * @return the closest intersection, or null if there are no intersections
     */
    private Intersection findClosestIntersection(Ray ray) {
        var intersections = _scene.geometries.calcIntersections(ray);
        if (intersections == null) {
            return null;
        }
        return ray.findClosestIntersection(intersections);
    }
}