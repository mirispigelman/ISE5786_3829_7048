package primitives;

/**
 * Material class represents the material properties of a geometry.
 * PDS (Plain Data Structure) class.
 */
public class Material {
    /** Diffuse attenuation coefficient for ambient light */
    public Double3 kA = Double3.ONE;

    /** Diffuse attenuation coefficient for Phong Model */
    public Double3 kD = Double3.ZERO; // סעיף א'+ב': הוספת שדה ואתחולו ל-Double3.ZERO

    /** Specular attenuation coefficient for Phong Model */
    public Double3 kS = Double3.ZERO; // סעיף א'+ב': הוספת שדה ואתחולו ל-Double3.ZERO

    /** Shininess exponent factor for specular concentration */
    public int nShininess = 0;        // סעיף א'+ב': הוספת שדה ואתחולו ל-0

    /**
     * Setter for kA using Double3
     * @param kA attenuation coefficient
     * @return this material object for chaining
     */
    public Material setKA(Double3 kA) {
        this.kA = kA;
        return this;
    }

    /**
     * Setter for kA using double
     * @param kA attenuation coefficient
     * @return this material object for chaining
     */
    public Material setKA(double kA) {
        this.kA = new Double3(kA);
        return this;
    }

    /**
     * Setter for kD using Double3
     * @param kD attenuation coefficient
     * @return this material object for chaining
     */
    public Material setKD(Double3 kD) { // סעיף ד': מתודה מעדכנת משתרשרת (Double3) מותאמת לטסט
        this.kD = kD;
        return this;
    }

    /**
     * Setter for kD using double
     * @param kD attenuation coefficient as a double
     * @return this material object for chaining
     */
    public Material setKD(double kD) { // סעיף ד': מתודה מעדכנת משתרשרת (double) מותאמת לטסט
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Setter for kS using Double3
     * @param kS attenuation coefficient
     * @return this material object for chaining
     */
    public Material setKS(Double3 kS) { // סעיף ד': מתודה מעדכנת משתרשרת (Double3) מותאמת לטסט
        this.kS = kS;
        return this;
    }

    /**
     * Setter for kS using double
     * @param kS attenuation coefficient as a double
     * @return this material object for chaining
     */
    public Material setKS(double kS) { // סעיף ד': מתודה מעדכנת משתרשרת (double) מותאמת לטסט
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Setter for nShininess exponent factor
     * @param nShininess shininess factor
     * @return this material object for chaining
     */
    public Material setShininess(int nShininess) { // סעיף ה': מתודה מעדכנת משתרשרת עבור nShininess
        this.nShininess = nShininess;
        return this;
    }
}