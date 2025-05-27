package Location;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
public class PerlinNoiseGenerator {

    private static final double[][] GRADIENTS = {
        {1, 0}, {0.7071067811865475, 0.7071067811865475},
        {0, 1}, {-0.7071067811865475, 0.7071067811865475},
        {-1, 0}, {-0.7071067811865475, -0.7071067811865475},
        {0, -1}, {0.7071067811865475, -0.7071067811865475}
    };

    public static double noise(int seed,double x, double y ) {
        int xi = (int) Math.floor(x);
        int yi = (int) Math.floor(y);

        double x0 = x - xi;
        double y0 = y - yi;
        double x1 = x0 - 1;
        double y1 = y0 - 1;

        double n00 = dotGradient(xi, yi, x0, y0, seed);
        double n10 = dotGradient(xi + 1, yi, x1, y0, seed);
        double n01 = dotGradient(xi, yi + 1, x0, y1, seed);
        double n11 = dotGradient(xi + 1, yi + 1, x1, y1, seed);

        double u = fade(x0);
        double v = fade(y0);

        double lerpX0 = lerp(n00, n10, u);
        double lerpX1 = lerp(n01, n11, u);
        double result = lerp(lerpX0, lerpX1, v);

        // 归一化到[-1, 1]范围
        return result / Math.sqrt(2);
    }

    private static double dotGradient(int ix, int iy, double dx, double dy, int seed) {
        int hash = hash(ix, iy, seed);
        int index = Math.abs(hash % 8);
        double[] grad = GRADIENTS[index];
        return grad[0] * dx + grad[1] * dy;
    }

    private static int hash(int x, int y, int seed) {
        int hash = seed;
        hash = (hash ^ x) * 0x6A5D39EA;
        hash = (hash ^ y) * 0x4E3D8C7B;
        hash ^= hash >>> 16;
        hash *= 0x5E9B3D19;
        hash ^= hash >>> 15;
        hash *= 0x4F6E2A5D;
        hash ^= hash >>> 17;
        return hash;
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }
}