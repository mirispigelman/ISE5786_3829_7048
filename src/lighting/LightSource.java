package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface representing an external light source that propagates through space.
 */
public interface LightSource {

    /**
     * Calculates the normalized direction vector from the light source to an illuminated point.
     * @param p the illuminated point
     * @return the normalized Vector pointing to the point
     */
    Vector getL(Point p);

    /**
     * Calculates the light intensity reaching a specific point from the light source.
     * @param p the illuminated point
     * @return the Color representing the light intensity at that point
     */
    Color getIntensity(Point p);
}