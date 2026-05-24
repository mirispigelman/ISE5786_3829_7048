package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import lighting.PointLight;
import primitives.*;

/**
 * Unit tests for PointLight class.
 */
class PointLightTests {

    @Test
    void testGetL() {
        Point lightPos = new Point(0, 0, 0);
        PointLight light = new PointLight(new Color(255, 255, 255), lightPos);

        // EP: Normal point evaluation
        Point p = new Point(3, 0, 0);
        assertEquals(new Vector(1, 0, 0), light.getL(p), "getL() for PointLight is incorrect");

        // BV: Point coincides with light position -> vector creation should fail
        assertThrows(IllegalArgumentException.class, () -> light.getL(lightPos),
                "getL() exactly at the light position should throw an exception");
    }

    @Test
    void testGetIntensity() {
        Point lightPos = new Point(0, 0, 0);
        PointLight light = new PointLight(new Color(100, 100, 100), lightPos);

        Point p = new Point(10, 0, 0);
        assertEquals(new Color(100, 100, 100), light.getIntensity(p), "getIntensity() for PointLight is incorrect");
    }
}