package renderer;

import java.util.MissingResourceException;
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
     * Renders the image by iterating over all pixels and casting rays[cite: 3].
     * @return the camera object
     */
    public Camera renderImage() {
        if (imageWriter == null) throw new MissingResourceException("Missing image writer", "Camera", "imageWriter");
        if (rayTracer == null) throw new MissingResourceException("Missing ray tracer", "Camera", "rayTracer");

        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                castRay(nX, nY, j, i);
            }
        }
        return this;
    }

    /**
     * Casts a ray through a single pixel and writes its color to the image[cite: 3].
     */
    private void castRay(int nX, int nY, int j, int i) {
        Ray ray = constructRay(nX, nY, j, i);
        Color color = rayTracer.traceRay(ray);
        imageWriter.writePixel(j, i, color);
    }

    /**
     * Prints a grid on the image for testing purposes[cite: 3].
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
     * Delegates the image writing to ImageWriter[cite: 3].
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

        /**
         * Configures the ray tracer for the camera[cite: 3].
         * @param scene the scene to render
         * @param type ray tracer strategy
         * @return builder object
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                _camera.rayTracer = new SimpleRayTracer(scene); // Here is the line
            } else {
                throw new IllegalArgumentException("Unsupported ray tracer type");
            }
            return this;
        }

        private void checkResolution() {
            if (_camera.nX <= 0 || _camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
            // Initialize imageWriter after resolution is confirmed[cite: 3]
            _camera.imageWriter = new ImageWriter(_camera.nX, _camera.nY);
        }

        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();

            // Default ray tracer initialization if missing[cite: 3]
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