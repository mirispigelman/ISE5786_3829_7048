package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import lighting.DirectionalLight;
import primitives.*;

/**
 * Unit tests for DirectionalLight class.
 */
class DirectionalLightTests {

    @Test
    void testGetL() {
        Vector dir = new Vector(1, 1, -1);
        DirectionalLight light = new DirectionalLight(new Color(255, 255, 255), dir);
        Point p = new Point(10, 20, 30);

        // EP: getL must always return the constant normalized light direction vector
        assertEquals(dir.normalize(), light.getL(p), "getL() for DirectionalLight is incorrect");
    }

    @Test
    void testGetIntensity() {
        DirectionalLight light = new DirectionalLight(new Color(200, 100, 50), new Vector(0, 0, -1));
        Point p = new Point(5, 5, 5);

        // EP: getIntensity must return the source's original intensity everywhere
        assertEquals(new Color(200, 100, 50), light.getIntensity(p), "getIntensity() for DirectionalLight is incorrect");
    }
}