package attributions;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class attributionsSet {
    private static final String[] attributions={"风","岩","金","木","水","火","土","电","光","暗","癫狂"};
    private static final String[] attributionDescription={"气体与机械波的属性","大质量坚硬物体的属性","机械与磁的属性","反能量定律地增生、操控有机质",
        "流体集合体有了意识···","火就是火","纯粹的抛瓦和操控地形的能力，但是精度E","操控电流的能力","操控电磁波的能力，甚至可以达到光学隐身",
        "来自亚空间（？）的超自然力量，能够侵蚀实体无视空间限制","侵蚀思维，把你洗脑成猫娘（bushi"};
    private static final double[][] effects_E={//effects_E[a][b]表示
        // 风   岩   金   木   水   火   土   电   光   暗   癫狂
        {1.0, 1.4, 0.5, 1.1, 0.3, 1.2, 0.8, 1.0, 0.3, 0.8, 1.0},  // 风 气体与波的属性
        {0.1, 1.0, 1.3, 1.0, 0.3, 2.5, 0.8, 2.0, 1.0, 0.5, 2.0},  // 岩 大质量坚硬物体的属性
        {0.8, 1.5, 1.2, 1.5, 1.0, 0.7, 1.0, 1.0, 1.0, 1.0, 2.0},  // 金 机械与磁的属性
        {0.5, 2.0, 0.3, 1.0, 1.2, 0.1, 1.3, 2.0, 1.0, 1.0, 1.0},  // 木 反能量定律地增生、操控有机质
        {0.5, 0.6, 0.8, 1.0, 2.0, 3.0, 1.0, 1.0, 0.5, 1.0, 1.0},  // 水 流体集合体也能产生意识？
        {1.0, 0.8, 3.0, 3.0, 2.0, 1.0, 1.0, 1.5, 2.0, 3.0, 2.0},  // 火 稳态下的高温，非稳态下的微观粒子高动能
        {0.5, 2.2, 1.5, 1.0, 1.0, 2.0, 0.8, 0.5, 0.5, 0.2, 1.0},  // 土 低可操控性只对地形有效但是能量很高的属性
        {0.5, 0.5, 3.0, 0.5, 2.0, 0.5, 1.0, 1.0, 2.0, 2.0, 6.6},  // 电 就只是电
        {0.5, 0.5, 2.0, 0.8, 1.0, 1.0, 1.0, 1.0, 2.0, 4.0, 2.0},  // 光 就只是电磁波
        {3.0, 3.0, 1.0, 3.0, 3.0, 0.2, 3.0, 1.0, 1.0, 1.0, 1.0},  // 暗 来自亚空间（？）的超自然力量，能够侵蚀实体无视空间限制
        {2.0, 2.0, 0.2, 2.0, 2.0, 2.0, 3.0, 2.0, 2.0, 5.0, 0.1}   // 癫狂 侵蚀思维
    };
    private static final double[][] effects_F={
        // 风   岩   金   木   水   火   土   电   光   暗   癫狂
        {5.0, 0.1, 0.8, 1.5, 1.0, 5.0, 1.0, 1.0, 1.0, 1.0, 1.1},  // 风 气体与波的属性
        {0.1, 2.0, 1.5, 1.0, 1.0, 0.1, 2.0, 0.0, 0.0, 1.0, 1.0},  // 岩 大质量坚硬物体的属性
        {0.1, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.5, 1.0, 1.0, 1.0},  // 金 机械与磁的属性
        {0.1, 1.2, 0.5, 2.0, 1.0, 3.0, 1.0, 0.1, 0.1, 1.0, 1.0},  // 木 反能量定律地增生、操控有机质
        {0.1, 1.0, 1.0, 1.0, 2.0, 0.1, 1.0, 1.2, 1.0, 1.0, 1.0},  // 水 流体集合体也能产生意识？
        {0.1, 1.0, 1.0, 0.3, 1.0, 2.0, 1.0, 1.0, 2.0, 0.5, 1.0},  // 火 稳态下的高温，非稳态下的微观粒子高动能
        {0.8, 2.0, 1.0, 1.5, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0},  // 土 低可操控性只对地形有效但是能量很高的属性
        {0.1, 1.0, 1.5, 0.2, 1.0, 1.5, 1.0, 2.0, 2.0, 1.0, 1.0},  // 电 就只是电
        {0.5, 1.5, 1.5, 2.0, 1.5, 2.0, 1.0, 1.5, 2.0, 0.5, 2.0},  // 光 就只是电磁波
        {0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0},  // 暗 来自亚空间（？）的超自然力量，能够侵蚀实体无视空间限制
        {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 5.0}   // 癫狂 侵蚀思维
    };
    public attributionsSet(){}
    public static int getSize(){return attributions.length;}
    public static String getAttName(int x){
        if(x < 0 || x >= attributions.length){return "No Such Attribution";}
        else{return attributions[x];}
    }
    public static double getEffect_E(int user,int target){
        double effectNum=effects_E[user][target];
        return effectNum;
    }
    public static double getEffect_F(int user,int target){
        double effectNum=effects_F[user][target];
        return effectNum;
    }
    public static LinkedHashSet<Double> calculateWeights(int k) {
        LinkedHashSet<Double> weights = new LinkedHashSet<>();
        if (k <= 0) {return weights;}
        double n = 1.0 / (2 * k);
        double a = (double) (k + 1) / k;
        double m = k;
        double aToM = Math.pow(a, m);
        for (int x = 1; x <= k; x++) {
            double term = aToM / Math.pow(a, x);
            double fx = n + term;
            weights.add(fx);
        }
        return weights;
    } 
    public static double getEffect_Enermy(LinkedHashSet<Integer> skillAttributions,int User,int target){
        if(skillAttributions.size()==0){return 1.2;}//以理服人
        if(target==-1){return 1.0;}
        double effectNum=getEffect_E(User, target);
        LinkedHashSet<Double>weights=calculateWeights(skillAttributions.size());
        Iterator<Double> it=weights.iterator();
        for (Integer integer : skillAttributions) {
            Double weight=it.next();
            effectNum+=weight*getEffect_E(integer, target)/2;
        }
        return effectNum;
    }
    public static double getEffect_Friend(LinkedHashSet<Integer> skillAttributions,int User,int target){
        if(skillAttributions.size()==0){return 1.2;}//以理服人
        if(target==-1){return 1.0;}
        double effectNum=getEffect_F(User, target);
        LinkedHashSet<Double>weights=calculateWeights(skillAttributions.size());
        Iterator<Double> it=weights.iterator();
        for (Integer integer : skillAttributions) {
            Double weight=it.next();
            effectNum+=weight*getEffect_E(integer, target)/2;
        }
        return effectNum;
    }
}
