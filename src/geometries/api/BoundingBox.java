package geometries.api;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * מחלקת עזר לייצוג תיבה חוסמת שצלעותיה מקבילות לצירים (AABB - Axis-Aligned Bounding Box).
 */
public class BoundingBox {
    public final double minX, maxX;
    public final double minY, maxY;
    public final double minZ, maxZ;

    /**
     * בנאי לקביעת גבולות התיבה החוסמת
     */
    public BoundingBox(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    /**
     * בדיקה מהירה: האם הקרן פוגעת בתיבה החוסמת?
     * @param ray הקרן הנבדקת
     * @return true אם הקרן עוברת דרך התיבה, false אחרת
     */
    public boolean intersect(Ray ray) {
        Point p0 = ray.origin();
        Vector dir = ray.direction();

        // חילוץ קואורדינטות ראשית הקרן (p0) על ידי מכפלה סקלרית של הווקטור מנקודת האפס
        Vector p0Vec = p0.equals(Point.ZERO) ? null : p0.subtract(Point.ZERO);
        double p0X = p0Vec == null ? 0 : p0Vec.dotProduct(Vector.AXIS_X);
        double p0Y = p0Vec == null ? 0 : p0Vec.dotProduct(Vector.AXIS_Y);
        double p0Z = p0Vec == null ? 0 : p0Vec.dotProduct(Vector.AXIS_Z);

        // חילוץ קואורדינטות וקטור הכיוון (dir) על ידי מכפלה סקלרית מול וקטורי הצירים
        double dirX = dir.dotProduct(Vector.AXIS_X);
        double dirY = dir.dotProduct(Vector.AXIS_Y);
        double dirZ = dir.dotProduct(Vector.AXIS_Z);

        // חישוב חיתוך ציר X
        double tXmin = (minX - p0X) / dirX;
        double tXmax = (maxX - p0X) / dirX;
        if (tXmin > tXmax) { double temp = tXmin; tXmin = tXmax; tXmax = temp; }

        // חישוב חיתוך ציר Y
        double tYmin = (minY - p0Y) / dirY;
        double tYmax = (maxY - p0Y) / dirY;
        if (tYmin > tYmax) { double temp = tYmin; tYmin = tYmax; tYmax = temp; }

        // בדיקת חפיפה ראשונית
        if ((tXmin > tYmax) || (tYmin > tXmax)) return false;

        double tMin = Math.max(tXmin, tYmin);
        double tMax = Math.min(tXmax, tYmax);

        // חישוב חיתוך ציר Z
        double tZmin = (minZ - p0Z) / dirZ;
        double tZmax = (maxZ - p0Z) / dirZ;
        if (tZmin > tZmax) { double temp = tZmin; tZmin = tZmax; tZmax = temp; }

        // בדיקת חפיפה סופית
        if ((tMin > tZmax) || (tZmin > tMax)) return false;

        tMax = Math.min(tMax, tZmax);
        return tMax >= 0;
    }
}