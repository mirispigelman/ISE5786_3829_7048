package primitives;

/**
 * Material class represents the material properties of a geometry.
 * PDS (Plain Data Structure) class.
 */
public class Material {
    /** Diffuse attenuation coefficient for ambient light */
    public Double3 kA = Double3.ONE;

    /**
     * Setter for kA using Double3
     * @param kA attenuation coefficient
     * @return this material object for chaining
     */
    public Material setKa(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Setter for kA using double
     * @param kA attenuation coefficient
     * @return this material object for chaining
     */
    public Material setKa(double kA) {
        this.kA = new Double3(kA);
        return this;
    }
}