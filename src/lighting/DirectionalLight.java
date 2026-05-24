package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Directional Light source represents a light source that is infinitely far away
 * (like the sun), so its direction is constant and light lines are parallel.
 */
public class DirectionalLight extends Light implements LightSource {

    private final Vector _direction;

    /**
     * Constructor for DirectionalLight.
     * @param intensity the original light intensity
     * @param direction the direction vector of the light (will be normalized)
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this._direction = direction.normalize();
    }

    @Override
    public Vector getL(Point p) {
        return _direction;
    }

    @Override
    public Color getIntensity(Point p) {
        return _intensity;
    }
}