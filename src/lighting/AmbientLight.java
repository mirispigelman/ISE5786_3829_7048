package lighting;

import primitives.Color;

/**
 * AmbientLight class for uniform background lighting in the scene.
 * This class is immutable.
 */
public class AmbientLight {

    /** Light intensity */
    private final Color _intensity;

    /** Static constant for no ambient light (Black) */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructor for AmbientLight.
     * @param intensity the intensity of the ambient light.
     */
    public AmbientLight(Color intensity) {
        this._intensity = intensity;
    }

    /**
     * Returns the intensity of the light.
     * @return Color object representing the intensity.
     */
    public Color getIntensity() {
        return _intensity;
    }
}