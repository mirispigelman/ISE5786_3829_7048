package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Point Light source represents an omnidirectional point light source
 * radiating light equally in all directions from a single point in space.
 */
public class PointLight extends Light implements LightSource {

    private final Point _position;

    private double _kC = 1;
    private double _kL = 0;
    private double _kQ = 0;

    /**
     * Constructor for PointLight.
     * @param intensity the original light intensity
     * @param position the position point of the light source in space
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this._position = position;
    }

    public PointLight setKc(double kC) {
        this._kC = kC;
        return this;
    }

    public PointLight setKl(double kL) {
        this._kL = kL;
        return this;
    }

    public PointLight setKq(double kQ) {
        this._kQ = kQ;
        return this;
    }

    @Override
    public Vector getL(Point p) {

        return p.subtract(_position).normalize();
    }


    @Override
    public Color getIntensity(Point p) {
        double d = _position.distance(p);
        double factor = _kC + _kL * d + _kQ * d * d;

        return _intensity.scale(1.0 / factor);
    }
    @Override
    public double getDistance(Point point) {
        // Calculate geometric distance from the light source position to the given point
        return _position.distance(point);
    }
}