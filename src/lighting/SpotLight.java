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
        this._direction = direction.normalize(); // נרמול וקטור הכיוון לפני שמירתו [cite: 490]
    }

    // דריסת מתודות העדכון המשרשרות של מחלקת האב (סעיף ה') [cite: 502]
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

    // ==========================================
    // מימוש מתודות הממשק LightSource (סעיף ו')
    // ==========================================

    @Override
    public Vector getL(Point p) {
        // מפעיל את getL של מחלקת האב PointLight שמחשבת ומחזירה
        // וקטור מנורמל ממיקום מקור האור אל הנקודה p
        return super.getL(p);
    }

    @Override
    public Color getIntensity(Point p) {
        Vector l = getL(p); // וקטור כיוון האור לנקודה p [cite: 486]
        double cosFactor = _direction.dotProduct(l); // המכפלה הסקלרית בין כיוון הזרקור לכיוון האור לנקודה [cite: 656]

        // אם הזווית גדולה מ-90 מעלות, הנקודה נמצאת מאחורי או מחוץ לאלומת הזרקור [cite: 656, 899, 900]
        if (cosFactor <= 0) {
            return Color.BLACK;
        }

        // חישוב עוצמת האור הנקודתית (כולל דעיכת מרחק) והכפלתה ברכיב הזוויתי של הספוט [cite: 656]
        return super.getIntensity(p).scale(cosFactor);
    }
}