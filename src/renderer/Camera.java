package renderer;

import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.stream.IntStream;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Camera class representing a viewpoint in 3D space.
 * Follows the Builder design pattern and implements Cloneable.
 */
public class Camera implements Cloneable {
    // Primary fields
    private Point p0;
    private Vector vTo;
    private Vector vUp;
    private Vector vRight;

    // View Plane fields
    private double width = 0;
    private double height = 0;
    private double distance = 0;

    // Resolution fields (default 1)
    private int nX = 1;
    private int nY = 1;

    // Computed auxiliary fields
    private Point vpCenter;
    private double pixelWidth;
    private double pixelHeight;

    // Stage 5 fields
    private ImageWriter imageWriter;
    private RayTracerBase rayTracer;

    // --- שדות חדשים עבור ריבוי תהליכונים והדפסות התקדמות ---
    private int threadsCount = 0;
    private static final int SPARE_THREADS = 2;
    private double printInterval = 0;
    private PixelManager pixelManager;

    /**
     * Static method to get a new Builder instance
     * @return a new Builder object
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through the center of a pixel [j,i]
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        double rX = width / nX;
        double rY = height / nY;

        double xj = (j - (nX - 1) / 2.0) * rX;
        double yi = -(i - (nY - 1) / 2.0) * rY;

        Point pIJ = vpCenter;

        if (!isZero(xj)) {
            pIJ = pIJ.add(vRight.scale(xj));
        }
        if (!isZero(yi)) {
            pIJ = pIJ.add(vUp.scale(yi));
        }

        return new Ray(p0, pIJ.subtract(p0));
    }

    /**
     * Helper method to support the test module signature
     */
    public Ray constructRay(int j, int i) {
        return constructRay(this.nX, this.nY, j, i);
    }

    /**
     * Renders the image by dynamically choosing the optimal threading strategy.
     * @return the camera object
     */
    public Camera renderImage() {
        if (imageWriter == null) throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        if (rayTracer == null) throw new MissingResourceException("Missing ray tracer", "Camera", "rayTracer");

        // אתחול מנהל הפיקסלים
        pixelManager = new PixelManager(nY, nX, printInterval);

        // בחירת אסטרטגיית הריצה בהתאם לערך השדה threadsCount
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * רינדור ללא ריבוי תהליכונים
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < nY; ++i) {
            for (int j = 0; j < nX; ++j) {
                castRay(nX, nY, j, i);
            }
        }
        return this;
    }

    /**
     * רינדור באמצעות הזרמה מקבילית Parallel Stream
     */
    private Camera renderImageStream() {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> castRay(nX, nY, j, i)));
        return this;
    }

    /**
     * רינדור באמצעות יצירה והרצה של תהליכונים גולמיים ומנהל הפיקסלים
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        int currentThreads = threadsCount;
        while (currentThreads-- > 0) {
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null) {
                    castRay(nX, nY, pixel.col(), pixel.row());
                }
            }));
        }

        for (var thread : threads) thread.start();

        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {}

        return this;
    }

    /**
     * Casts a ray through a single pixel and writes its color to the image.
     */
    private void castRay(int nX, int nY, int j, int i) {
        Ray ray = constructRay(nX, nY, j, i);
        Color color = rayTracer.traceRay(ray);
        imageWriter.writePixel(j, i, color);

        if (pixelManager != null) {
            pixelManager.pixelDone();
        }
    }

    /**
     * Prints a grid on the image for testing purposes.
     * @param interval grid square size
     * @param color grid line color
     * @return the camera object
     */
    public Camera printGrid(int interval, Color color) {
        if (imageWriter == null) throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");

        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    /**
     * Delegates the image writing to ImageWriter.
     * @param fileName output file name
     */
    public void writeToImage(String fileName) {
        if (imageWriter == null) throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        imageWriter.writeToImage(fileName);
    }

    // --- Nested Builder Class ---

    public static class Builder {
        private final Camera _camera = new Camera();
        private Vector generalUp = Vector.AXIS_Y;
        private Point target = null;
        private Vector directionVec = null;

        public Builder setLocation(Point location) {
            _camera.p0 = location;
            return this;
        }

        public Builder setDirection(Vector to, Vector up) {
            this.directionVec = to;
            this.generalUp = up;
            return this;
        }

        public Builder setDirection(Point target, Vector up) {
            this.target = target;
            this.generalUp = up;
            return this;
        }

        public Builder setDirection(Point target) {
            this.target = target;
            this.generalUp = Vector.AXIS_Y;
            return this;
        }

        public Builder setVpSize(double width, double height) {
            _camera.width = width;
            _camera.height = height;
            return this;
        }

        public Builder setVpDistance(double distance) {
            _camera.distance = distance;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            _camera.nX = nX;
            _camera.nY = nY;
            return this;
        }

        public Builder setMultithreading(int threads) {
            if (threads < -3)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == 2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                _camera.threadsCount = cores <= 2 ? 1 : cores;
            } else {
                _camera.threadsCount = threads;
            }
            return this;
        }

        public Builder setDebugPrint(double interval) {
            if (interval < 0) throw new IllegalArgumentException("interval parameter must be non-negative");
            _camera.printInterval = interval;
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                _camera.rayTracer = new SimpleRayTracer(scene);
            } else {
                throw new IllegalArgumentException("Unsupported ray tracer type");
            }
            return this;
        }

        private void checkResolution() {
            if (_camera.nX <= 0 || _camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
            _camera.imageWriter = new ImageWriter(_camera.nX, _camera.nY);
        }

        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            if (_camera.rayTracer == null) {
                setRayTracer(new Scene("test"), RayTracerType.SIMPLE);
            }

            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException _) {
                return null;
            }
        }

        private void checkLocationAndDirection() {
            if (_camera.p0 == null) throw new MissingResourceException("Missing location", "Camera", "p0");
            if (directionVec == null && target == null)
                throw new MissingResourceException("Missing direction", "Camera", "vTo");

            if (directionVec == null) {
                _camera.vTo = target.subtract(_camera.p0).normalize();
            } else {
                _camera.vTo = directionVec.normalize();
            }

            try {
                _camera.vRight = _camera.vTo.crossProduct(generalUp).normalize();
                _camera.vUp = _camera.vRight.crossProduct(_camera.vTo).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Direction and Up vectors are parallel");
            }
        }

        private void checkViewPlane() {
            if (alignZero(_camera.width) <= 0 || alignZero(_camera.height) <= 0)
                throw new IllegalArgumentException("View Plane size must be positive");
            if (alignZero(_camera.distance) <= 0)
                throw new IllegalArgumentException("View Plane distance must be positive");

            _camera.vpCenter = _camera.p0.add(_camera.vTo.scale(_camera.distance));
            _camera.pixelWidth = _camera.width / _camera.nX;
            _camera.pixelHeight = _camera.height / _camera.nY;
        }
    }
}