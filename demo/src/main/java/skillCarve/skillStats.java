package skillCarve;
import java.util.LinkedHashSet;
import java.util.Set;

import attributions.attributionsSet;
import dataDeals.*;
public class skillStats{
    Set<Integer> attributions = new LinkedHashSet<>();
    int type,aoeSize,range;//类型，范围，有效距离
    double prepareState,weakState;
    String[] buffs;//最多4个
    String[] debuffs;//最多4个
    public skillStats(LinkedHashSet<Integer> attributions,int type,int aoeSize,int range,
    String[] buffs,String[] debuffs,double prepareState,double weakState){
        /*传参初始化，用于预设的和特定生成的技能模板 */
        this.attributions=attributions;
        this.type=type;
        this.aoeSize=aoeSize;
        this.range=range;
        this.buffs=buffs;
        this.debuffs=debuffs;
        this.prepareState=prepareState;
        this.weakState=weakState;
    }
    private long getSeed(String words){
        long seed=words.hashCode();
        long len=words.length();
        seed=(seed<<len)^seed;
        return seed;
    }
    private int loadAttributions(long seed){
        int cost=0;
        int size=attributionsSet.getSize();
        if(seed%114514==0){return -100;}
        do{
            if(attributions.add((int)seed%size)){cost+=attributions.size();}//加入成功了就增加cost,最终
            seed^=seed<<21;
            seed^=seed>>42;
            seed^=seed<<35;
        }while(seed%(size*2)<size);
        return cost;
    }
    private void mkSpecialStats(String words,int skillevel){
        /*详细方法正在补充*/
        long seed =getSeed(words);
        int usefulLen=words.length()>5?words.length():5;
        usefulLen+=15;
        double weight=skillevel*2000/(usefulLen);//在语句长度有限的时候，每级提供100(暂定)权重
        weight-=loadAttributions(seed);//无属性彩蛋提供+100权重，n属性权重为n*(n+1)/2,如果这一步权重就被爆到负数的话，后面就好玩了
        
    }
    public skillStats(String words,int skillevel){
        /* 种子+模板生成技能，用于随机生成和用户“抽奖”式生成 */
        mkSpecialStats(words,skillevel);
    }
}