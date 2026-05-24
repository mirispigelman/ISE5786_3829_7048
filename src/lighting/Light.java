package lighting;

import primitives.Color;

/**
 * Abstract class representing a general light source.
 * Stores only the original intensity of the light.
 */
abstract class Light {

    protected final Color _intensity;

    /**
     * Protected constructor to initialize the light intensity.
     * @param intensity the original intensity of the light source
     */
    protected Light(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Regular getter for the original light intensity, independent of any point.
     * @return the original light intensity
     */
    public Color getIntensity() {
        return _intensity;
    }
}