package Character.CharacterStat_common;

import decides.Rand;

import java.text.DecimalFormat;
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
    private int level,luck;
    private String profession;
    public CharStat_Common(int level){//仅有等级的随机NPC属性设置
        this.level=level;
        this.luck=Rand.randomLuck();
        basicStats=new double[16];
        this.profession="一般市民";
        for(int i=0;i<16;i++){
            basicStats[i]=BASE_STAT[i]+level*GROWTH_RATE[i]*(1+Rand.jieDuanZhengTaiFenBu()*OFFSET_RATE[i]);
        }
        mkComSkills_levelRand();
    }
    public CharStat_Common(String x){
        String[] types={
            "学生","战士"
        };
        
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
    public int getLuck(){return luck;}
    public int getLevel(){return level;}
    @Override
    public String toString(){
        String str1= "职业："+profession+"\n";
        String[] statNames={
            "攻击力","暴击率","暴击伤害","防御力","生命值","速度",
            "格挡率","格挡突破率","闪避率","闪避破解率","防御无视率","防御无视值",
            "坚韧度","意志力","技巧","感知"};
        str1+="====== 角色基础属性 ======\n";
        DecimalFormat df = new DecimalFormat("0.00");
        for (int i = 0; i < statNames.length; i++) {
            str1+=statNames[i]+" :"+df.format(basicStats[i])+"\n";
        }
        str1+="====== 角色生存技能 ======\n";
        str1+="工艺 "+craftSkill+"\t耕作 "+farmSkill+"\n";
        str1+="建筑 "+constructSkill+"\t守卫 "+guardSkill+"\n";
        str1+="烹饪 "+cookSkill+"\t交涉"+chatSkill+"\n";
        str1+="交易 "+tradeSkill+"\t统御"+lordSkill+"\n";
        str1+="角色等级："+level+"\n";
        str1+="角色运气："+luck+"\n";
        return str1;
    }
}
