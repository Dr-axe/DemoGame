package decides;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Rand {
    // 生成[min, max)的double
    public static double nextDouble(double min, double max) {return ThreadLocalRandom.current().nextDouble(min, max);}
    // 生成[min, max]的int
    public static ArrayList<Integer> NextBlock_seed(int seed,int times,int x,int y){
        seed^=(x<<16)|y;
        ArrayList<Integer> ans=new ArrayList<>();
        Random random=new Random(seed);
        for(int i=0;i<times;i++){
            ans.add(((random.nextInt()&0x3f)<<6)|(random.nextInt()&0x3f));
        }
        return ans;
    }
    public static int nextInt(int min, int max) {return ThreadLocalRandom.current().nextInt(min, max + 1);}
    // 生成[min, max)的double（无参数时默认0-1）
    public static double nextDouble() {return ThreadLocalRandom.current().nextDouble();}
    public static double jieDuanZhengTaiFenBu(){
        double x;
        do{x=ThreadLocalRandom.current().nextGaussian();
        }while(x>3||x<-3);
        return Math.abs(x/3);
    }
    public static int randomLuck(){
        double w;
        do{w=ThreadLocalRandom.current().nextGaussian()*30;
        }while(Math.abs(w)>100);
        return (int)Math.abs(w);
    }
}
