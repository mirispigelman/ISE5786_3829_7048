package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Unit tests for ImageWriter class.
 * This stage focuses on learning how to use the ImageWriter to generate images.
 */
class ImageWriterTests {

    /** Horizontal resolution */
    private static final int NX = 800;
    /** Vertical resolution */
    private static final int NY = 500;
    /** Grid square size in pixels */
    private static final int GRID_STEP = 50;

    /** Background color of the image */
    private static final Color BACKGROUND_COLOR = new Color(0, 100, 0); // Dark Green
    /** Grid lines color */
    private static final Color GRID_COLOR = new Color(255, 255, 255); // White

    /**
     * Test method for creating a simple grid image.
     * The image will have NX x NY resolution and a grid of GRID_STEP size.
     */
    @Test
    void testImageWriter() {
        ImageWriter imageWriter = new ImageWriter(NX, NY);

        // Loop over all pixels
        for (int i = 0; i < NX; i++) {
            for (int j = 0; j < NY; j++) {
                // Check if the current pixel is on the grid (every GRID_STEP pixels)
                // Use ternary operator to avoid code duplication
                imageWriter.writePixel(i, j,
                        (i % GRID_STEP == 0 || j % GRID_STEP == 0) ? GRID_COLOR : BACKGROUND_COLOR);
            }
        }

        // Finalize the image file
        imageWriter.writeToImage("basic_grid_test");
    }
}