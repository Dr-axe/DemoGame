package Location;

public class OptimizedPerlinNoise {
    private static final double[][] GRADIENTS = {
        {1, 0}, {0.7071067811865475, 0.7071067811865475},
        {0, 1}, {-0.7071067811865475, 0.7071067811865475},
        {-1, 0}, {-0.7071067811865475, -0.7071067811865475},
        {0, -1}, {0.7071067811865475, -0.7071067811865475}
    };
    public static double[][] generateFractalNoiseMap(
    double minx, double miny, 
    double maxx, double maxy, 
    int seed, int octaves, 
    double persistence, double lacunarity
) {
    final int SIZE = 64;
    double[][] fractalNoise = new double[SIZE][SIZE];
    
    double amplitude = 1.0;
    double frequency = 1.0;
    double totalAmplitude = 0.0;

    for (int octave = 0; octave < octaves; octave++) {
        // 计算当前octave的坐标范围
        double currentMinX = minx * frequency;
        double currentMinY = miny * frequency;
        double currentMaxX = maxx * frequency;
        double currentMaxY = maxy * frequency;

        // 生成当前octave的噪声图
        double[][] noise = generateNoiseMap(currentMinX, currentMinY, currentMaxX, currentMaxY, seed);

        // 叠加到总噪声图并累加振幅
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                fractalNoise[y][x] += noise[y][x] * amplitude;
            }
        }
        totalAmplitude += amplitude;
        amplitude *= persistence;
        frequency *= lacunarity;
    }

    // 归一化到[-1, 1]范围
    if (totalAmplitude != 0) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                fractalNoise[y][x] /= totalAmplitude;
            }
        }
    }

    return fractalNoise;
}
    // 批量生成噪声图
    public static double[][] generateNoiseMap(
        double minx, double miny, 
        double maxx, double maxy, 
        int seed
    ) {
        final int SIZE = 64;
        double[][] noiseMap = new double[SIZE][SIZE];
        
        // 计算步长（均匀分布）
        double stepX = (maxx - minx) / (SIZE - 1);
        double stepY = (maxy - miny) / (SIZE - 1);
        
        // 预计算哈希网格（覆盖所有可能访问的网格坐标）
        int ixMin = (int) Math.floor(minx);
        int ixMax = (int) Math.floor(maxx) + 1;
        int iyMin = (int) Math.floor(miny);
        int iyMax = (int) Math.floor(maxy) + 1;
        int[][] hashGrid = precomputeHashGrid(ixMin, ixMax, iyMin, iyMax, seed);

        // 按行处理以优化缓存
        for (int yIdx = 0; yIdx < SIZE; yIdx++) {
            double y = miny + yIdx * stepY;
            int yi = (int) Math.floor(y);
            double yFrac = y - yi;
            double yFade = fade(yFrac);
            
            int rowInHashGrid = yi - iyMin; // 哈希网格中的行
            for (int xIdx = 0; xIdx < SIZE; xIdx++) {
                double x = minx + xIdx * stepX;
                int xi = (int) Math.floor(x);
                double xFrac = x - xi;
                double xFade = fade(xFrac);
                
                int colInHashGrid = xi - ixMin; // 哈希网格中的列
                
                // 直接从预计算哈希网格中获取四个顶点的哈希值
                int hash00 = hashGrid[rowInHashGrid][colInHashGrid];
                int hash10 = hashGrid[rowInHashGrid][colInHashGrid + 1];
                int hash01 = hashGrid[rowInHashGrid + 1][colInHashGrid];
                int hash11 = hashGrid[rowInHashGrid + 1][colInHashGrid + 1];
                
                // 计算四个顶点的贡献（避免重复计算梯度）
                double n00 = dotGradient(hash00, xFrac, yFrac);
                double n10 = dotGradient(hash10, xFrac - 1, yFrac);
                double n01 = dotGradient(hash01, xFrac, yFrac - 1);
                double n11 = dotGradient(hash11, xFrac - 1, yFrac - 1);
                
                // 双线性插值
                noiseMap[yIdx][xIdx] = lerp(
                    lerp(n00, n10, xFade),
                    lerp(n01, n11, xFade),
                    yFade
                ) / Math.sqrt(2); // 归一化
            }
        }
        return noiseMap;
    }

    // 预计算哈希网格
    private static int[][] precomputeHashGrid(
        int ixMin, int ixMax, 
        int iyMin, int iyMax, 
        int seed
    ) {
        int width = ixMax - ixMin + 1;
        int height = iyMax - iyMin + 1;
        int[][] grid = new int[height][width];
        
        for (int y = iyMin; y <= iyMax; y++) {
            for (int x = ixMin; x <= ixMax; x++) {
                grid[y - iyMin][x - ixMin] = hash(x, y, seed);
            }
        }
        return grid;
    }

    // 直接从哈希值计算点积（避免重复哈希计算）
    private static double dotGradient(int hash, double dx, double dy) {
        int index = Math.abs(hash % 8);
        return GRADIENTS[index][0] * dx + GRADIENTS[index][1] * dy;
    }

    // 原有hash()、fade()、lerp()方法保持不变
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
public static void main(String[] args) {
    double x = 3.14, y = 2.718;
    int seed = 42;

    // 原算法结果
    double original = PerlinNoiseGenerator.noise(seed,x, y);

    // 优化算法结果（生成1x1点阵）
    double[][] optimized = OptimizedPerlinNoise.generateNoiseMap(
        x, y, x, y, seed
    );

    System.out.println("Original: " + original);
    System.out.println("Optimized: " + optimized[0][0]);
    System.out.println("差值: " + Math.abs(original - optimized[0][0]));
}
}