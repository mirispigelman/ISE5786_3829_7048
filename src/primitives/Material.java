package primitives;

/**
 * Material class represents the material properties of a geometry.
 * PDS (Plain Data Structure) class.
 */
public class Material {
    /** Diffuse attenuation coefficient for ambient light */
    public Double3 kA = Double3.ONE;

    /** Diffuse attenuation coefficient for Phong Model */
    public Double3 kD = Double3.ZERO;

    /** Specular attenuation coefficient for Phong Model */
    public Double3 kS = Double3.ZERO;

    /** Shininess exponent factor for specular concentration */
    public int nShininess = 0;

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

    /**
     * Setter for kD using Double3
     * @param kD attenuation coefficient
     * @return this material object for chaining
     */
    public Material setKD(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Setter for kD using double
     * @param kD attenuation coefficient as a double
     * @return this material object for chaining
     */
    public Material setKD(double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Setter for kS using Double3
     * @param kS attenuation coefficient
     * @return this material object for chaining
     */
    public Material setKS(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Setter for kS using double
     * @param kS attenuation coefficient as a double
     * @return this material object for chaining
     */
    public Material setKS(double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Setter for nShininess exponent factor
     * @param nShininess shininess factor
     * @return this material object for chaining
     */
    public Material setShininess(int nShininess) {
        this.nShininess = nShininess;
        return this;
    }
}

