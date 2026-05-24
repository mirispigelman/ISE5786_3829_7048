package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import lighting.SpotLight;
import primitives.*;

/**
 * Unit tests for SpotLight class.
 */
class SpotLightTests {

    @Test
    void testGetL() {
        Point lightPos = new Point(0, 0, 0);
        SpotLight light = new SpotLight(new Color(255, 255, 255), lightPos, new Vector(0, 0, -1));
        Point p = new Point(0, 0, -5);

        // EP: Normal vector direction calculation
        assertEquals(new Vector(0, 0, -1), light.getL(p), "getL() for SpotLight is incorrect");
    }

    @Test
    void testGetIntensity() {
        Point lightPos = new Point(0, 0, 0);
        // זרקור שמכוון לכיוון Z שלילי
        SpotLight light = new SpotLight(new Color(100, 100, 100), lightPos, new Vector(0, 0, -1));

        // EP: Point is directly in front of the spotlight beam
        Point inFront = new Point(0, 0, -5);
        assertNotNull(light.getIntensity(inFront), "getIntensity() returned null");

        // EP: Point is behind the spotlight beam (angle > 90 degrees)
        Point behind = new Point(0, 0, 5);
        assertEquals(Color.BLACK, light.getIntensity(behind), "Point behind spotlight should receive NO intensity");
    }
}