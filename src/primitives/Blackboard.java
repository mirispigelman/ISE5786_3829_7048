package primitives;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Blackboard class responsible for generating a list of 2D sample points
 * within a defined target area shape and layout pattern.
 */
public class Blackboard {
    private double size = 0.0;
    private int samplesCount = 1;
    private SampleShape shape = SampleShape.RECTANGLE;
    private SamplePattern pattern = SamplePattern.GRID;
    private final Random random = new Random();

    /** Default constructor */
    public Blackboard() {}

    // ================== Fluent API Setters ==================

    public Blackboard setSize(double size) {
        if (size < 0) throw new IllegalArgumentException("Size cannot be negative");
        this.size = size;
        return this;
    }

    public Blackboard setSamplesCount(int samplesCount) {
        if (samplesCount <= 0) throw new IllegalArgumentException("Samples count must be greater than 0");
        this.samplesCount = samplesCount;
        return this;
    }

    public Blackboard setShape(SampleShape shape) {
        this.shape = shape;
        return this;
    }

    public Blackboard setPattern(SamplePattern pattern) {
        this.pattern = pattern;
        return this;
    }

    /**
     * Generates a list of 2D sample points based on the current configuration.
     * @return list of Point2D objects representing local offsets from the center (0,0)
     */
    public List<Point2D> generate2DSamples() {
        List<Point2D> samples = new ArrayList<>();

        if (Util.isZero(size) || samplesCount <= 1) {
            samples.add(new Point2D(0, 0));
            return samples;
        }

        int n = (int) Math.sqrt(samplesCount);
        if (n < 1) n = 1;

        double halfSize = size / 2.0;
        double step = size / n;

        switch (pattern) {
            case GRID:
                generateGridPoints(samples, n, step, halfSize, false);
                break;

            case JITTERED:
                generateGridPoints(samples, n, step, halfSize, true);
                break;

            case STOCHASTIC:
                generateStochasticPoints(samples, halfSize);
                break;
        }

        if (shape == SampleShape.CIRCLE) {
            double radius = halfSize;
            samples.removeIf(p -> (p.getX() * p.getX() + p.getY() * p.getY()) > radius * radius);
        }

        return samples;
    }

    /**
     * Generates points using a regular Grid or Jittered Grid layout.
     */
    private void generateGridPoints(List<Point2D> samples, int n, double step, double halfSize, boolean useJitter) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double x = -halfSize + (i + 0.5) * step;
                double y = -halfSize + (j + 0.5) * step;

                if (useJitter) {
                    double jitterX = (random.nextDouble() - 0.5) * step;
                    double jitterY = (random.nextDouble() - 0.5) * step;
                    x += jitterX;
                    y += jitterY;
                }

                samples.add(new Point2D(x, y));
            }
        }
    }

    /**
     * Generates purely random (stochastic) points across the target area bounding box.
     */
    private void generateStochasticPoints(List<Point2D> samples, double halfSize) {
        int targetCount = samplesCount;
        if (shape == SampleShape.CIRCLE) {
            targetCount = (int) (samplesCount * 1.28);
        }

        for (int i = 0; i < targetCount; i++) {
            double x = (random.nextDouble() - 0.5) * size;
            double y = (random.nextDouble() - 0.5) * size;
            samples.add(new Point2D(x, y));
        }
    }
}