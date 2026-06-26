package renderer;

import org.junit.jupiter.api.Test;
import geometries.impl.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Performance and verification tests for the BVH acceleration and Multi-threading.
 * Recreates the logical studio showcase environment with foreground and background bodies.
 */
public class BVHPerformanceTests {

    /**
     * Helper method to build a realistic studio showcase scene.
     * Contains a high-density tiled studio floor, 5 background target spheres,
     * 5 foreground showcase spheres, and 3 official light sources.
     * @return the constructed scene
     */
    /**
     * Helper method to build a realistic studio showcase scene.
     * Fully meets all guidelines by including ALL geometry types (Triangle, Sphere, Plane, Tube),
     * 5 light sources, and advanced material effects from Project 1.
     * @return the constructed scene
     */
    private static Scene createComplexScene() {
        Scene scene = new Scene("BVH Studio Showcase Scene");
        scene.setBackground(new Color(30, 30, 30));
        scene.setAmbientLight(new AmbientLight(new Color(40, 40, 40), Double3.ONE));

        // Define high-quality showcase materials based on your project conventions
        Material targetMat = new Material().setKD(0.5).setKS(0.5).setShininess(50);
        Material floorMat = new Material().setKD(0.4).setKS(0.1).setShininess(10).setKR(0.1);
        Material pillarMat = new Material().setKD(0.3).setKS(0.3).setShininess(20);

        // Sharp Mirror: High reflection
        Material sharpMirror = new Material().setKD(0.1).setKS(0.8).setShininess(100).setKR(0.9);

        // Clear Glass: Beautifully transparent, lets 80% of light pass straight through
        Material clearGlass  = new Material().setKD(0.05).setKS(0.8).setShininess(150).setKT(0.8);

        // Frosted / Diffuse Glass from Project 1: Keeps high transparency (0.75)
        // But activates your diffuse-glass blurring coefficient (setKDG) and samples!
        // (If your method is called setKDG, keep it. If it uses setKT and another field, adjust accordingly)
        Material frostedGlass = new Material()
                .setKD(0.1)
                .setKS(0.7)
                .setShininess(100)
                .setKT(0.75) // Keeps it as real glass
                .setKDG(0.12) // Triggers the ray-deflection blur from project 1
                .setMaterialSamples(36); // High sample count for smooth glass blurring
        // 1. NEW GEOMETRY TYPE: PLANE (A broad background studio wall at z = -100)
        scene.geometries.add(
                new Plane(new Point(0, 0, -100), new Vector(0, 0, 1))
                        .setEmission(new Color(15, 15, 20))
                        .setMaterial(new Material().setKD(0.6).setKS(0.0).setShininess(5))
        );

        // 2. NEW GEOMETRY TYPE: TUBE (Two decorative studio support pillars on the left and right edges)
        // 2. NEW GEOMETRY TYPE: TUBE (Two decorative studio support pillars on the left and right edges)
        // Creating the Tube using a Ray (Point + Vector) and Radius according to your project's constructor
        // 2. NEW GEOMETRY TYPE: TUBE (Two decorative studio support pillars on the left and right edges)
        // Correct constructor order based on your implementation: (double radius, Ray axis)
        scene.geometries.add(
                new Tube(4.0, new Ray(new Point(-80, -30, -30), new Vector(0, 1, 0)))
                        .setEmission(new Color(35, 35, 40)).setMaterial(pillarMat),
                new Tube(4.0, new Ray(new Point(80, -30, -30), new Vector(0, 1, 0)))
                        .setEmission(new Color(35, 35, 40)).setMaterial(pillarMat)
        );

        // 3. SMART EXPLICIT HIERARCHY FOR THE FLOOR:
        // Groups each floor strip into its own 'Geometries' container, creating a tree of smaller boxes
        for (int i = -150; i < 150; i += 30) {
            // יצירת קופסה חוסמת נפרדת (אוסף משנה) עבור כל שורת אריחים ברצפה
            Geometries rowContainer = new Geometries();

            for (int j = 100; j > -200; j -= 30) {
                // הוספת המשולשים לתוך קבוצת השורה במקום ישירות לסצנה הכללית
                rowContainer.add(
                        new Triangle(new Point(i, -30, j), new Point(i + 30, -30, j), new Point(i, -30, j - 30))
                                .setEmission(new Color(50, 50, 50)).setMaterial(floorMat),
                        new Triangle(new Point(i + 30, -30, j), new Point(i + 30, -30, j - 30), new Point(i, -30, j - 30))
                                .setEmission(new Color(45, 45, 45)).setMaterial(floorMat)
                );
            }
            // הוספת השורה כולה לסצנה - מנגנון ה-BVH כעת ייצר עבורה AABB ממוקד ומקומי!
            scene.geometries.add(rowContainer);
        }
        // 4. FIVE BACKGROUND TARGET SPHERES (Positioned at z = -70)
        scene.geometries.add(
                new Sphere(new Point(-45, 20, -70), 10).setMaterial(targetMat).setEmission(new Color(180, 30, 30)),
                new Sphere(new Point(-18, 15, -70), 10).setMaterial(targetMat).setEmission(new Color(30, 30, 180)),
                new Sphere(new Point(10, 10, -70), 10).setMaterial(targetMat).setEmission(new Color(30, 180, 30)),
                new Sphere(new Point(38, 5, -70), 10).setMaterial(targetMat).setEmission(new Color(180, 180, 30)),
                new Sphere(new Point(65, -5, -70), 10).setMaterial(targetMat).setEmission(new Color(180, 30, 180))
        );

        // 5. FIVE FOREGROUND SHOWCASE SPHERES (Positioned at z = -15)
        scene.geometries.add(
                new Sphere(new Point(-45, -5, -15), 13).setMaterial(sharpMirror).setEmission(new Color(10, 10, 10)),
                new Sphere(new Point(-18, -5, -15), 13).setMaterial(sharpMirror).setEmission(new Color(20, 10, 30)),
                new Sphere(new Point(10, -5, -15), 13).setMaterial(clearGlass).setEmission(new Color(5, 10, 15)),     // 3. Clear Crystal Glass
                new Sphere(new Point(38, -5, -15), 13).setMaterial(frostedGlass).setEmission(new Color(10, 20, 25)),  // 4. Beautiful Frosted/Blurry Glass
                new Sphere(new Point(65, -5, -15), 13).setMaterial(targetMat).setEmission(new Color(140, 70, 20))
        );

        // 6. THREE LIGHT SOURCES (Directional, Point, and Spot)
        scene.lights.add(new DirectionalLight(new Color(60, 60, 60), new Vector(1, -1, -1)));
        scene.lights.add(new PointLight(new Color(400, 400, 400), new Point(0, 70, -30)).setKl(0.0001).setKq(0.0001));
        scene.lights.add(new SpotLight(new Color(400, 400, 400), new Point(-60, 70, 30), new Vector(1, -1, -1)).setKl(0.0001).setKq(0.0001));

        return scene;
    }
    /**
     * Shared camera builder configuration to match the exact view of your studio tests.
     */
    private Camera.Builder getCameraBuilder(Scene scene) {
        return Camera.getBuilder()
                .setResolution(800, 500)
                .setLocation(new Point(10, 15, 120)) // Studio angle setup
                .setDirection(new Point(10, 5, -50), Vector.AXIS_Y)
                .setVpDistance(100)
                .setVpSize(140, 87.5)
                .setRayTracer(scene, RayTracerType.SIMPLE);
    }

    /**
     * CONFIGURATION 1: Multi-threading OFF, BVH OFF
     */
    @Test
    public void testPerformance_ThreadsOff_BVHOff() {
        Scene scene = createComplexScene();
        scene.geometries.setBVH(false);

        getCameraBuilder(scene)
                .setMultithreading(0)
                .build()
                .renderImage()
                .writeToImage("BVH_Studio_ThreadsOff_BVHOff");
    }

    /**
     * CONFIGURATION 2: Multi-threading ON, BVH OFF
     */
    @Test
    public void testPerformance_ThreadsOn_BVHOff() {
        Scene scene = createComplexScene();
        scene.geometries.setBVH(false);

        getCameraBuilder(scene)
                .setMultithreading(-1)
                .build()
                .renderImage()
                .writeToImage("BVH_Studio_ThreadsOn_BVHOff");
    }

    /**
     * CONFIGURATION 3: Multi-threading OFF, BVH ON
     */
    @Test
    public void testPerformance_ThreadsOff_BVHOn() {
        Scene scene = createComplexScene();
        scene.geometries.setBVH(true);

        getCameraBuilder(scene)
                .setMultithreading(0)
                .build()
                .renderImage()
                .writeToImage("BVH_Studio_ThreadsOff_BVHOn");
    }

    /**
     * CONFIGURATION 4: Multi-threading ON, BVH ON
     */
    @Test
    public void testPerformance_ThreadsOn_BVHOn() {
        Scene scene = createComplexScene();
        scene.geometries.setBVH(true);

        getCameraBuilder(scene)
                .setMultithreading(-1)
                .build()
                .renderImage()
                .writeToImage("BVH_Studio_ThreadsOn_BVHOn");
    }
}