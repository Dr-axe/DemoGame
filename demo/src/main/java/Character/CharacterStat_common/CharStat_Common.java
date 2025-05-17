package Character.CharacterStat_common;

import decides.Rand;
import java.util.concurrent.ThreadLocalRandom;

public class CharStat_Common {
    private static final double[] BASE_STAT={
        50,0.01,0.2,20,500,5,
        0.1,0.05,0.1,0.0,0.0,0,
        1,1,0,10};
    private static final double[] GROWTH_RATE={
        10,0.005,0.015,10,100,0.1,
        0.015,0.0075,0.015,0.0075,0.005,1,
        0.5,0.5,0.5,0.3};
    private static final double[] OFFSET_RATE={
        0.3,0.4,0.33,0.5,0.8,1.0,
        1.0,1.0,1.0,1.0,0.5,0.5,
        0.2,0.3,1.0,0.5};
    private double[] basicStats;
    int craftSkill,farmSkill,constructSkill,guardSkill,cookSkill,chatSkill,tradeSkill,lordSkill;
    int level,luck;
    public CharStat_Common(int level){//仅有等级的随机NPC属性设置
        this.level=level;
        this.luck=Rand.randomLuck();
        basicStats=new double[16];
        for(int i=0;i<16;i++){
            basicStats[i]=BASE_STAT[i]+level*GROWTH_RATE[i]*(1+Rand.jieDuanZhengTaiFenBu()*OFFSET_RATE[i]);
        }
        mkComSkills_levelRand();
    }

    public void mkComSkills_levelRand(){//根据人物等级随机分配技能等级
        craftSkill=Rand.nextInt(level/5,level);
        farmSkill=Rand.nextInt(level/5,level);
        constructSkill=Rand.nextInt(level/5,level);
        guardSkill=Rand.nextInt(level/5,level);
        cookSkill=Rand.nextInt(level/5,level);
        chatSkill=Rand.nextInt(level/5,level);
        tradeSkill=Rand.nextInt(level/5,level);
        lordSkill=Rand.nextInt(level/5,level);
    }
    public double[] getBasicStats(){
        return basicStats;
    }
    public double[] getBasicStats_clone() {
        return basicStats.clone(); // 返回新数组，修改不会影响原始值
    }
}
