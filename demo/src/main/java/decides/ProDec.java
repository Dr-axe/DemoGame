package decides;
import java.util.concurrent.ThreadLocalRandom;
public class ProDec {//ProbabilityDecision
    public static int decide(double probability) {//输入概率，给出该概率分布的1和0
        if (probability < 0.0){return 0;}
        else if(probability > 1.0) {return 1;}
        return ThreadLocalRandom.current().nextDouble() < probability ? 1 : 0;
    }
    private ProDec(){
        throw new UnsupportedOperationException("工具类禁止实例化喵");
    }
    // 以下是测试区
    // public static void main(String[] args) {
    //     double probability = 0.3; // 30%概率返回1
    //     int trials = 10000;
    //     int count = 0;
    //     for (int i = 0; i < trials; i++) {
    //         count += decide(probability);
    //     }
    //     System.out.printf("执行 %d 次测试，返回1的比例为 %.2f%%（理论值应为 %.2f%%）%n",
    //             trials, (double) count / trials * 100, probability * 100);
    // }

}
