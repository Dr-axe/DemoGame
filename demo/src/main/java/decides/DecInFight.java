package decides;
public class DecInFight{
    public static int Block_Miss(double block_rate,double block_ignorance,
        double miss_rate,double miss_ignorance,int luck){
        /* 返回1表示格挡成功，2表示闪避成功，3表示触发弹反 */
        double blockChance=(block_rate-block_ignorance)/2;
        double missChance=(miss_rate-miss_ignorance)/2;
        if(blockChance+missChance>block_ignorance+miss_ignorance+1||ProDec.decide(0.001*luck)==1){
            return 3;
        }
        else {
            int ans=0;
            ans+=ProDec.decide(blockChance);
            ans+=ProDec.decide(missChance)*2;
            return ans;
        }
    }
    public static int critical(double critical_rate,int luck){//得到n，暴击乘区为(1+n*暴击伤害)
        if (critical_rate>1.5||ProDec.decide(critical_rate)+ProDec.decide(0.001*luck)==2) {//超暴击，造成的暴击带来的暴击伤害是一般暴击的两倍
            return 2;
        }
        else {return ProDec.decide(critical_rate);}
    }
    
}