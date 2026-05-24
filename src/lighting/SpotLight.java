package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Spot Light source represents a directional point light source,
 * emitting light in a specific direction with angular attenuation.
 */
public class SpotLight extends PointLight {

    private final Vector _direction;

    /**
     * Constructor for SpotLight.
     * @param intensity the original light intensity
     * @param position the position point of the light source in space
     * @param direction the direction vector of the spotlight beam (will be normalized)
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this._direction = direction.normalize();
    }


    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }



    @Override
    public Vector getL(Point p) {

        return super.getL(p);
    }

    @Override
    public Color getIntensity(Point p) {
        Vector l = getL(p);
        double cosFactor = _direction.dotProduct(l);

        if (cosFactor <= 0) {
            return Color.BLACK;
        }

        return super.getIntensity(p).scale(cosFactor);
    }
}