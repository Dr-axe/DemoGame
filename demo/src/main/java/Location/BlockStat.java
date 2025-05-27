package Location;

import java.text.DecimalFormat;

public class BlockStat {
    private double Moisture_degree;//环境湿度
    private double brokeness;//地形破碎程度
    private double rock_exposure_degree;//岩石裸露度
    private double ore_quality;//矿石丰度（影响产出矿石的产量）
    private double inffection;//被侵蚀程度
    private double Prolificacy;//繁茂度
    private double avgHeight;
    /*用两个大素数打散种子 */
    private static final int GOLDEN_RATIO_32 = 0x45D9F3B;
    private static final int LARGE_PRIME_32 = 0x27d4eb2d;

    public static int mixSeeds(int seed2, int seed1) {
        int hash = spreadBits(seed1) ^ seed2;
        hash ^= hash >>> 16;
        hash *= GOLDEN_RATIO_32;
        hash ^= hash >>> 13;
        hash *= LARGE_PRIME_32;
        hash ^= hash >>> 16;
        return hash;
    }
    // 位扩散函数（处理负数）
    private static int spreadBits(int value) {
        // 扩散位模式同时保留符号位信息
        value = (value ^ (value >>> 16)) * 0x85EBCA77;
        value = (value ^ (value >>> 13)) * 0xC2B2AE3D;
        return value ^ (value >>> 16);
    }
    private void loadMoisture_Degree(int seed,double x,double y){
        double mid=FractalPerlinNoise.fractalNoise(seed, (x*64+32)*0.0003,(64*y+32)*0.0003,6,0.6, 2.3, 0.5, 1);
        double up=FractalPerlinNoise.fractalNoise(seed, (x*64-32)*0.0003,(64*y+32)*0.0003,6,0.6, 2.3, 0.5, 1);
        double down=FractalPerlinNoise.fractalNoise(seed, (x*64+96)*0.0003,(64*y+32)*0.0003,6,0.6, 2.3, 0.5, 1);
        double left=FractalPerlinNoise.fractalNoise(seed, (x*64+32)*0.0003,(64*y-32)*0.0003,6,0.6, 2.3, 0.5, 1);
        double right=FractalPerlinNoise.fractalNoise(seed, (x*64+32)*0.0003,(64*y+96)*0.0003,6,0.6, 2.3, 0.5, 1);
        this.Moisture_degree=(mid+up+down+left+right)/5;
    }
    public BlockStat(int seed,int xx,int yy){
        double x=(0.5+xx),y=0.5+yy;
        loadMoisture_Degree(seed,x, y);
        this.brokeness=FractalPerlinNoise.fractalNoise(mixSeeds(-1331329648, seed), x*0.1, y*0.1, 4, 0.5,2.0);
        this.rock_exposure_degree=FractalPerlinNoise.fractalNoise(mixSeeds(-1563429846, seed), x*0.1, y*0.1, 4, brokeness,2.0);
        this.ore_quality=FractalPerlinNoise.fractalNoise(mixSeeds(-1975642974, seed), x*0.05, y*0.05, 4, brokeness,2.0);
        this.inffection=FractalPerlinNoise.fractalNoise(mixSeeds(285046167, seed), x*0.1*brokeness, y*0.1*brokeness, 4, brokeness,2.0);
        this.Prolificacy=FractalPerlinNoise.fractalNoise(mixSeeds(-750123551, seed), x*0.08*(1+Moisture_degree), y*0.08*(1+Moisture_degree), 4,0.5,1.5);
        // printOut();
    }
    public void loadHeight(double avgHeight){this.avgHeight=avgHeight;}
    public double getHeight(){return avgHeight;}
    public double getProlificacy(){return Prolificacy;}
    public double getMoisture(){return Moisture_degree;}
    public void printOut(){
        DecimalFormat df=new DecimalFormat("0.00");
        System.out.printf("环境湿度："+df.format(Moisture_degree));
        System.out.printf("地形破碎程度："+df.format(brokeness));
        System.out.printf("岩石裸露度："+df.format(rock_exposure_degree));
        System.out.printf("矿石丰度："+df.format(ore_quality));
        System.out.printf("被侵蚀程度："+df.format(inffection));
        System.out.printf("繁茂度："+df.format(Prolificacy));
        System.out.printf("平均海拔："+df.format(avgHeight));
        System.out.println();
    }
    public double getRock_exposure(){return rock_exposure_degree;}
    public static void main(String[] args) {
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                BlockStat test=new BlockStat(1794638139, i+100, j);
                System.out.println("===============");
                System.out.println("环境湿度："+test.Moisture_degree);
                System.out.println("地形破碎程度："+test.brokeness);
                System.out.println("岩石裸露度："+test.rock_exposure_degree);
                System.out.println("矿石丰度："+test.ore_quality);
                System.out.println("被侵蚀程度："+test.inffection);
                System.out.println("繁茂度："+test.Prolificacy);
            }
        }
        
    }
}
