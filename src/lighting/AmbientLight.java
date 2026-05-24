package lighting;

import primitives.Color;
import primitives.Double3;

/**
 * AmbientLight class for uniform background lighting in the scene.
 */
public class AmbientLight extends Light {

    /** Static constant for no ambient light (Black) */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructor for AmbientLight with direct color intensity.
     * @param intensity the intensity of the ambient light.
     */
    public AmbientLight(Color intensity) {
        super(intensity);
    }

    /**
     * Constructor that calculates the final ambient light intensity (Ia * kA).
     * @param iA the basic intensity of the ambient light
     * @param kA the attenuation coefficient of the ambient light
     */
    public AmbientLight(Color iA, Double3 kA) {
        super(iA.scale(kA));
    }

    /**
     * Overloaded constructor accepting a double coefficient for convenience.
     * @param iA the basic intensity of the ambient light
     * @param kA the attenuation coefficient as a double
     */
    public AmbientLight(Color iA, double kA) {
        super(iA.scale(kA));
    }
}