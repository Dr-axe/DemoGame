package Location;
public class FractalPerlinNoise {

    public static double fractalNoise(int seed, double x, double y, 
                                     int octaves, double persistence, 
                                     double lacunarity) {
        double total = 0;
        double frequency = 1.0;
        double amplitude = 1.0;
        double maxValue = 0;  // 用于结果归一化

        for(int i = 0; i < octaves; i++) {
            // 计算当前八度的噪声贡献
            double noiseValue = PerlinNoiseGenerator.noise(
                (seed<<i)^(seed>>>i), 
                x * frequency, 
                y * frequency
            );
            total += noiseValue * amplitude;
            maxValue += amplitude;
            // 更新下个八度的参数
            amplitude *= persistence;
            frequency *= lacunarity;
        }
        // 归一化到[-1, 1]范围
        return total / maxValue;
    }
    // 简化版（使用常用默认参数）
    public static double fractalNoise(int seed, double x, double y, int octaves) {
        return fractalNoise(seed, x, y, octaves, 0.5, 2.0);
    }
    // 新增参数：初始频率和初始振幅
    public static double fractalNoise(int seed, double x, double y, 
                                     int octaves, double persistence,
                                     double lacunarity, 
                                     double initialFrequency,
                                     double initialAmplitude) {
        double total = 0;
        double frequency = initialFrequency;
        double amplitude = initialAmplitude;
        double maxValue = 0;

        for(int i = 0; i < octaves; i++) {
            total += PerlinNoiseGenerator.noise(
                seed + i, 
                x * frequency, 
                y * frequency
            ) * amplitude;
            
            maxValue += amplitude;
            amplitude *= persistence;
            frequency *= lacunarity;
        }
        
        return total / maxValue;
    }

    // 使用示例
    public static void main(String[] args) {
        // 生成分形噪声（4个八度）
        for(int x = 3; x < 128; x+=8) {
            for(int y = 3; y < 64; y+=8) {
                double value = fractalNoise(
                    12345, 
                    x * 0.1, 
                    y * 0.1, 
                    4,    // 八度数
                    0.5,  // persistence
                    2.0   // lacunarity
                );
                System.out.printf("%.2f ", value);
            }
            System.out.println();
        }
    }
}