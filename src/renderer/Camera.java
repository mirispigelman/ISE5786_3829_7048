package renderer;

import java.util.MissingResourceException;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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

    /**
     * Static method to get a new Builder instance
     *
     * @return a new Builder object
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through the center of a pixel [j,i]
     *
     * @param nX number of pixels in a row (x-axis)
     * @param nY number of pixels in a column (y-axis)
     * @param j  pixel column index
     * @param i  pixel row index
     * @return constructed Ray from camera through pixel center
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        // Pixel center calculation according to formulas in the slides
        // P[i,j] = Pc + (j - (Nx-1)/2)*Rx*vRight - (i - (Ny-1)/2)*Ry*vUp

        double rX = width / nX;
        double rY = height / nY;

        double xj = (j - (nX - 1) / 2.0) * rX;
        double yi = -(i - (nY - 1) / 2.0) * rY;

        Point pIJ = vpCenter;

        // Use if(!isZero) to avoid Vector Zero exception when scaling by 0
        if (!isZero(xj)) {
            pIJ = pIJ.add(vRight.scale(xj));
        }
        if (!isZero(yi)) {
            pIJ = pIJ.add(vUp.scale(yi));
        }

        return new Ray(p0, pIJ.subtract(p0));
    }

    /**
     * Helper method to support the test module signature (constructRay(int, int))
     */
    public Ray constructRay(int j, int i) {
        return constructRay(this.nX, this.nY, j, i);
    }

    // --- Nested Builder Class ---

    public static class Builder {
        private final Camera _camera = new Camera();

        // Auxiliary fields for intermediate data
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
         * Validates and builds the final Camera object
         */
        public Camera build() {
            // 1. Check Resolution [cite: 66, 74]
            if (_camera.nX <= 0 || _camera.nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");

            // 2. Check and Set Location/Orientation [cite: 66, 78]
            checkLocationAndDirection();

            // 3. Check and Set View Plane [cite: 67, 79]
            checkViewPlane();

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

            // Compute vRight (vTo x generalUp) [cite: 78, 181]
            try {
                _camera.vRight = _camera.vTo.crossProduct(generalUp).normalize();
                // Compute final orthogonal vUp [cite: 78, 226]
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