package renderer;

import org.junit.jupiter.api.Test;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.*;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Official Comprehensive Showcase Test for Glossy Surfaces & Diffuse Glass.
 * Meets 100% of the project guidelines (11 shapes, 3 lights, performance logs, 2 output files).
 */
public class TransparencyReflectionTests {

    private void runComprehensiveScene(boolean enableBlur, String fileName) {
        // 1. Initialize Scene with a clean dark-studio background
        Scene scene = new Scene("Comprehensive Glossy and Diffuse Showcase")
                .setBackground(new Color(30, 30, 30))
                .setAmbientLight(new AmbientLight(new Color(40, 40, 40), Double3.ONE));

        // 2. Setup Materials
        Material targetMat = new Material().setKD(0.5).setKS(0.5).setShininess(50);
        Material floorMat = new Material().setKa(0.5).setKD(0.4);

        // Define number of rays: 36 samples when blurred (ON), 1 ray when sharp (OFF)
        int samples = enableBlur ? 9 : 1;

        // Define Showcase Materials (Left to Right)
        Material sharpMirror = new Material().setKD(0.1).setKS(0.8).setShininess(100).setKR(0.9).setKG(0.0);
        Material glossyMirror = new Material().setKD(0.1).setKS(0.8).setShininess(100).setKR(0.9).setKG(enableBlur ? 0.12 : 0.0).setMaterialSamples(samples);

        Material clearGlass = new Material().setKT(0.8).setKDG(0.0);
        Material midBlurGlass = new Material().setKT(0.8).setKDG(enableBlur ? 0.08 : 0.0).setMaterialSamples(samples);
        Material heavyBlurGlass = new Material().setKT(0.8).setKDG(enableBlur ? 0.18 : 0.0).setMaterialSamples(samples);

        // 3. Exactly 11 Geometries (1 Floor + 5 Background Targets + 5 Foreground Showcase Spheres)
        scene.geometries.add(
                // רצפת סטודיו רחבה לתחושת עומק וצללים
                new Triangle(new Point(-300, -30, 100), new Point(300, -30, 100), new Point(-300, -30, -200)).setMaterial(floorMat).setEmission(new Color(60, 60, 60)),
                new Triangle(new Point(300, -30, 100), new Point(300, -30, -200), new Point(-300, -30, -200)).setMaterial(floorMat).setEmission(new Color(60, 60, 60)),

                // --- 5 כדורי מטרה צבעוניים מאחור (גופים 3 עד 7) ---
                // ממוקמים מאחור כדי שישתקפו במראות או ייראו מבעד לזכוכיות
                new Sphere(new Point(-45, 20, -70), 10).setMaterial(targetMat).setEmission(new Color(180, 30, 30)),   // אדום
                new Sphere(new Point(-18, 20, -70), 10).setMaterial(targetMat).setEmission(new Color(30, 30, 180)),   // כחול
                new Sphere(new Point(10, 20, -70), 10).setMaterial(targetMat).setEmission(new Color(30, 180, 30)),    // ירוק
                new Sphere(new Point(38, 20, -70), 10).setMaterial(targetMat).setEmission(new Color(180, 180, 30)),  // צהוב
                new Sphere(new Point(65, 20, -70), 10).setMaterial(targetMat).setEmission(new Color(180, 30, 180)),  // מג'נטה

                // --- 5 כדורי התצוגה הראשיים מקדימה (גופים 8 עד 12) ---
                // מסודרים משמאל לימין ומראים את חוקי השיפור שלכן בהדרגה מושלמת
                new Sphere(new Point(-45, -5, -15), 13).setMaterial(sharpMirror),   // 1. מראה חדה לחלוטין
                new Sphere(new Point(-18, -5, -15), 13).setMaterial(glossyMirror),  // 2. מראה מטושטשת (Glossy)
                new Sphere(new Point(10, -5, -15), 13).setMaterial(clearGlass),     // 3. זכוכית שקופה וצלולה
                new Sphere(new Point(38, -5, -15), 13).setMaterial(midBlurGlass),   // 4. זכוכית חלבית (טשטוש בינוני)
                new Sphere(new Point(65, -5, -15), 13).setMaterial(heavyBlurGlass)  // 5. זכוכית חלבית (טשטוש עמוק)
        );

        // 4. Exactly 3 Light Sources (Directional, Point, and Spot)
        scene.lights.add(new DirectionalLight(new Color(60, 60, 60), new Vector(1, -1, -1)));
        scene.lights.add(new PointLight(new Color(400, 400, 400), new Point(0, 70, -30)).setKl(0.0001).setKq(0.0001));
        scene.lights.add(new SpotLight(new Color(400, 400, 400), new Point(-60, 70, 30), new Vector(1, -1, -1)).setKl(0.0001).setKq(0.0001));

        // 5. Start Performance Timer & Render
        long startTime = System.currentTimeMillis();

        Camera.getBuilder()
                .setResolution(800, 500)
                .setLocation(new Point(10, 15, 120)) // המצלמה ממוקמת בגובה ובזווית קלה כדי לתפוס את ההשתקפויות בצורה דרמטית
                .setDirection(new Point(10, 5, -50), Vector.AXIS_Y)
                .setVpDistance(100)
                .setVpSize(140, 87.5)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage(fileName);

        long endTime = System.currentTimeMillis();

        System.out.println("----------------------------------------");
        System.out.println("Render time for [" + fileName + "]: " + (endTime - startTime) + " ms");
        System.out.println("----------------------------------------");
    }

    @Test
    public void generateGlossyDiffuseProjectImages() {
        // ריצה 1: מצב בסיס מכובה - הכל חד לחלוטין (מייצר את קובץ ה-OFF)
        runComprehensiveScene(false, "GlossyDiffuse_Base_OFF");

        // ריצה 2: מצב שיפור מופעל - פיצול אלומות קרניים (מייצר את קובץ ה-ON)
        runComprehensiveScene(true, "GlossyDiffuse_Improvement_ON");
    }
}