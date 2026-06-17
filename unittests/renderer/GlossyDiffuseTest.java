package renderer;

import org.junit.jupiter.api.Test;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.*;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Official normal test replicating the presentation specification for Diffuse Glass and Glossy Mirrors.
 * Renders a red target object behind 5 glass panels, standing on a split sharp/glossy mirror floor.
 */
public class GlossyDiffuseTest {

    private void runPresentationScene(boolean enableBlur, String fileName) {
        // 1. Scene setup with a bright light-blue sky background
        Scene scene = new Scene("Presentation Glass Scene")
                .setBackground(new Color(135, 206, 235))
                .setAmbientLight(new AmbientLight(new Color(50, 50, 50), Double3.ONE));

        // 2. Define Materials
        Material targetMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(60);

        // 36 samples when blur is ON, 1 sample when OFF
        int samples = enableBlur ? 36 : 1;

        // Floor Mirror Materials (Left = Sharp, Right = Glossy/Blurry Mirror)
        Material sharpFloorMirror = new Material().setKD(0.2).setKS(0.4).setKR(0.7).setKG(0.0);
        Material glossyFloorMirror = new Material().setKD(0.2).setKS(0.4).setKR(0.7).setKG(enableBlur ? 0.10 : 0.0).setMaterialSamples(samples);

        // 5 Glass materials from Left to Right (Sharp -> Medium -> Heavy Blur)
        Material g1 = new Material().setKT(0.85).setKDG(0.0);
        Material g2 = new Material().setKT(0.85).setKDG(enableBlur ? 0.04 : 0.0).setMaterialSamples(samples);
        Material g3 = new Material().setKT(0.85).setKDG(enableBlur ? 0.09 : 0.0).setMaterialSamples(samples);
        Material g4 = new Material().setKT(0.85).setKDG(enableBlur ? 0.15 : 0.0).setMaterialSamples(samples);
        Material g5 = new Material().setKT(0.85).setKDG(enableBlur ? 0.25 : 0.0).setMaterialSamples(samples);

        // 3. Add Geometries (1 Target Sphere + 2 Floor Mirrors + 5 Glass Panels = 13 Geometries total)
        scene.geometries.add(
                // החצי השמאלי של הרצפה - מראה חדה לחלוטין (נבנה מ-2 משולשים)
                new Triangle(new Point(-300, -35, 50), new Point(0, -35, 50), new Point(-300, -35, -250)).setMaterial(sharpFloorMirror).setEmission(new Color(180, 180, 180)),
                new Triangle(new Point(0, -35, 50), new Point(0, -35, -250), new Point(-300, -35, -250)).setMaterial(sharpFloorMirror).setEmission(new Color(180, 180, 180)),

                // החצי הימני של הרצפה - מראה מטושטשת/מאט (נבנה מ-2 משולשים)
                new Triangle(new Point(0, -35, 50), new Point(300, -35, 50), new Point(0, -35, -250)).setMaterial(glossyFloorMirror).setEmission(new Color(180, 180, 180)),
                new Triangle(new Point(300, -35, 50), new Point(300, -35, -250), new Point(0, -35, -250)).setMaterial(glossyFloorMirror).setEmission(new Color(180, 180, 180)),

                // The main RED target sphere standing BEHIND the windows (Z = -50)
                new Sphere(new Point(0, -10, -80), 22).setMaterial(targetMaterial).setEmission(new Color(200, 20, 20)),

                // Window 1: Far Left (Perfect Clear Glass)
                new Triangle(new Point(-50, -35, 10), new Point(-32, -35, 10), new Point(-50, 40, 10)).setMaterial(g1).setEmission(new Color(40, 50, 60)),
                new Triangle(new Point(-32, -35, 10), new Point(-32, 40, 10), new Point(-50, 40, 10)).setMaterial(g1).setEmission(new Color(40, 50, 60)),

                // Window 2: Mid-Left (Slightly Blurry)
                new Triangle(new Point(-28, -35, 10), new Point(-10, -35, 10), new Point(-28, 40, 10)).setMaterial(g2).setEmission(new Color(40, 50, 60)),
                new Triangle(new Point(-10, -35, 10), new Point(-10, 40, 10), new Point(-28, 40, 10)).setMaterial(g2).setEmission(new Color(40, 50, 60)),

                // Window 3: Center (Medium Blurry)
                new Triangle(new Point(-6, -35, 10), new Point(13, -35, 10), new Point(-6, 40, 10)).setMaterial(g3).setEmission(new Color(40, 50, 60)),
                new Triangle(new Point(13, -35, 10), new Point(13, 40, 10), new Point(-6, 40, 10)).setMaterial(g3).setEmission(new Color(40, 50, 60)),

                // Window 4: Mid-Right (Strong Blurry)
                new Triangle(new Point(16, -35, 10), new Point(34, -35, 10), new Point(16, 40, 10)).setMaterial(g4).setEmission(new Color(40, 50, 60)),
                new Triangle(new Point(34, -35, 10), new Point(34, 40, 10), new Point(16, 40, 10)).setMaterial(g4).setEmission(new Color(40, 50, 60)),

                // Window 5: Far Right (Heavily Blurry)
                new Triangle(new Point(38, -35, 10), new Point(56, -35, 10), new Point(38, 40, 10)).setMaterial(g5).setEmission(new Color(40, 50, 60)),
                new Triangle(new Point(56, -35, 10), new Point(56, 40, 10), new Point(38, 40, 10)).setMaterial(g5).setEmission(new Color(40, 50, 60))
        );

        // 4. Exactly 3 Light Sources (Directional, Point, and Spot)
        scene.lights.add(new DirectionalLight(new Color(80, 80, 80), new Vector(1, -1, -1)));
        scene.lights.add(new PointLight(new Color(500, 500, 500), new Point(0, 100, -50)).setKl(0.0001).setKq(0.0001));
        scene.lights.add(new SpotLight(new Color(300, 300, 300), new Point(-80, 80, 50), new Vector(1, -1, -2)).setKl(0.0001).setKq(0.0001));

        // 5. Performance Timer & Render using your standard Camera Builder
        long startTime = System.currentTimeMillis();

        Camera.getBuilder()
                .setResolution(700, 500)
                .setLocation(new Point(0, 10, 130)) // הגבהנו טיפה את המצלמה כדי שנראה את ההשתקפויות על הרצפה
                .setDirection(new Point(0, -5, -1), Vector.AXIS_Y)
                .setVpDistance(100)
                .setVpSize(140, 100)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build()
                .renderImage()
                .writeToImage(fileName);

        long endTime = System.currentTimeMillis();
        System.out.println("Render time for [" + fileName + "]: " + (endTime - startTime) + " ms");
    }

    @Test
    public void generatePresentationImages() {
        // Run 1: Base image without blur (Saves as Glass_Base_OFF)
        runPresentationScene(false, "Glass_Base_OFF");

        // Run 2: Finished image with blur (Saves as Glass_Improvement_ON)
        runPresentationScene(true, "Glass_Improvement_ON");
    }
}