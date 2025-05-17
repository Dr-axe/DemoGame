package Character.CharacterStat_fight;
import java.text.DecimalFormat;
import java.util.*;
import Time.TimeManager;
import BuffAndDebuff.Buff;
import BuffAndDebuff.BuffType;
import Character.CharacterStat_common.*;
public class CharStat_Fight {
    private double[] nowStats;
    private double[] basicStats;
    private double[] totalEffect;
    private Map<Integer,Buff> buffs=new HashMap<>();
    private NavigableMap<Double,Buff> buffTimes=new TreeMap<>();//以时间顺序为排序顺序维护一个buff表，方便后续对时间应该结束的buff的移除
    public CharStat_Fight(CharStat_Common comStat){
        this.nowStats=comStat.getBasicStats_clone();
        this.basicStats=comStat.getBasicStats();
        this.totalEffect=new double[16];
        Arrays.fill(totalEffect,1.0);
    }
    public void addbuff_ID(int buffID,int level){
        CheckBuff();
        Buff oldBuff=buffs.get(buffID);
        if(oldBuff==null){
            Buff newBuff=new Buff(buffID, level);
            int[] targets=newBuff.getTargets();
            double[] effects=newBuff.getEffects();
            for (int i = 0; i < effects.length; i++) {//更新总效果 
                totalEffect[targets[i]]+=effects[i];
            }
            buffs.put(buffID,newBuff);
            buffTimes.put(newBuff.getDuration(),newBuff);
        }
        else{
            buffTimes.remove(oldBuff.getDuration());
            if(oldBuff.getLevel()<level){//buff升级的时候更新等级和效果
                int[] targets=oldBuff.getTargets();
                double[] oldEffects=oldBuff.getEffects();
                oldBuff.mkitMaxLevel(level);
                double[] newEffects=oldBuff.getEffects();
                for (int i = 0; i < newEffects.length; i++) {//更新总效果
                    totalEffect[targets[i]]=totalEffect[targets[i]]-oldEffects[i]+newEffects[i];
                }
            }
            else{//等级不变的时候只需要延长时间
                BuffType.BuffData buff = BuffType.fromID(buffID).getBuff(level);
                oldBuff.increaseDuration(buff.getDuration());
            }
            buffTimes.put(oldBuff.getDuration(), oldBuff);
        }
    }
    public void CheckBuff() {
        NavigableMap<Double, Buff> expired = buffTimes.headMap(TimeManager.time,false);
        expired.values().forEach(buff -> {
            // 1. 从buffs按ID移除
            buffs.remove(buff.getID());
            // 2. 撤销totalEffect
            int[] targets = buff.getTargets();
            double[] effects = buff.getEffects();
            for (int i = 0; i < effects.length; i++) {
                totalEffect[targets[i]] -= effects[i];
            }
        });
        // 3. 从buffTimes移除
        expired.clear();
    }
    public void PrintBuffs(){
        Set<Map.Entry<Double,Buff>> entrySet=buffTimes.entrySet();
        Iterator<Map.Entry<Double,Buff>> iterator=entrySet.iterator();
        String[] statNames={
            "攻击力","暴击率","暴击伤害","防御力","生命值","速度",
            "格挡率","格挡突破率","闪避率","闪避破解率","防御无视率","防御无视值",
            "坚韧度","意志力","技巧","感知"
        };
        String headerFormat = "%-8s %12s %10s %12s%n";  // 表头格式
        String rowFormat = "%-8s %12.2f %+9.2f%% %12.2f%n"; // 数据行格式
        // 打印表头
        System.out.printf(headerFormat, "属性", "基础值", "加成", "实际值");
        System.out.println("-------------------------------------------");
        // 循环打印数据
        for (int i = 0; i < statNames.length; i++) {
            double baseValue = basicStats[i];
            double bonus = (totalEffect[i] - 1.0) * 100; // 转换为百分比数值（如 20.00% 对应 20.00）
            double actualValue = nowStats[i] * totalEffect[i];
            
            System.out.printf(
                rowFormat,
                statNames[i],     // 属性名（左对齐，8字符宽度）
                baseValue,        // 基础值（右对齐，保留2位小数）
                bonus,            // 加成（带正负号，右对齐，如 +20.00%）
                actualValue       // 实际值（右对齐，保留2位小数）
            );
        }
        while (iterator.hasNext()) {
            Map.Entry<Double,Buff> it=iterator.next();
            System.out.println("buff结束时间："+it.getKey()+" buff名称:"+it.getValue().getName()+" buff效果"+Arrays.toString(it.getValue().getEffects()));
        }
    }
    public void beAttacked(){
        /* ···· */
    }
    public static void main(String[] args) {
        CharStat_Fight test=new CharStat_Fight(new CharStat_Common(10));
        System.out.println("初始时间是："+TimeManager.time);
        test.addbuff_ID(0,1);
        test.addbuff_ID(1, 1);
        test.PrintBuffs();
        test.addbuff_ID(1, 2);
        test.PrintBuffs();
    }
}