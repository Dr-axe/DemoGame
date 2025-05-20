package Character.CharacterStat_fight;
import java.text.DecimalFormat;
import java.util.*;
import Time.TimeManager;
import decides.DecInFight;
import decides.ProDec;
import BuffAndDebuff.Buff;
import BuffAndDebuff.BuffType;
import Character.CharacterStat_common.*;
import Character.Character;
import dataDeals.*;
public class CharStat_Fight {
    private double[] nowStats;
    private double[] basicStats;
    private double[] totalEffect;
    private int luck;
    private Map<Integer,Buff> buffs=new HashMap<>();
    private NavigableMap<Double,Buff> buffTimes=new TreeMap<>();//以时间顺序为排序顺序维护一个buff表，方便后续对时间应该结束的buff的移除
    public CharStat_Fight(CharStat_Common comStat){
        this.nowStats=comStat.getBasicStats_clone();
        this.basicStats=comStat.getBasicStats();
        this.totalEffect=new double[16];
        this.luck =comStat.getLuck();
        Arrays.fill(totalEffect,1.0);
    }
    public int getLuck(){return luck;}
    public double getStat(int x){
        if(x>=0&&x<16){return nowStats[x];}
        else{return 0;}
    }
    public double getStat(String x){
        String[] statNames={
            "攻击力","暴击率","暴击伤害","防御力","生命值","速度",
            "格挡率","格挡突破率","闪避率","闪避破解率","防御无视率","防御无视值",
            "坚韧度","意志力","技巧","感知"
        };
        for (int i = 0; i < statNames.length; i++) {
            if (statNames[i].equals(x)) {
                if (i < nowStats.length) {return nowStats[i];}
                else {return 0.0;}
            }
        }
        return 0.0;
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
    @Override
    public String toString() {
        // 使用 StringBuilder 高效拼接字符串
        StringBuilder sb = new StringBuilder();
        String[] statNames={
            "攻击力","暴击率","暴击伤害","防御力","生命值","速度",
            "格挡率","格挡突破率","闪避率","闪避破解率","防御无视率","防御无视值",
            "坚韧度","意志力","技巧","感知"
        };
        // 表头格式（与 PrintBuffs 一致）
        String headerFormat = "%-8s %12s %10s %12s%n";
        String rowFormat = "%-8s %12.2f %+9.2f%% %12.2f%n";
        String separator = "-------------------------------------------%n";

        // 拼接表头
        sb.append(String.format(headerFormat, "属性", "基础值", "加成", "实际值"));
        sb.append(String.format(separator));

        // 拼接属性数据行（循环 statNames 长度）
        for (int i = 0; i < statNames.length; i++) {
            double baseValue = basicStats[i];
            double bonus = (totalEffect[i] - 1.0) * 100;  // 转换为百分比（如 20.00%）
            double actualValue = nowStats[i] * totalEffect[i];

            sb.append(String.format(
                rowFormat,
                statNames[i],     // 左对齐，8字符宽度
                baseValue,        // 右对齐，保留2位小数
                bonus,            // 带正负号，右对齐（如 +20.00%）
                actualValue       // 右对齐，保留2位小数
            ));
        }

        // 拼接 Buff 信息（遍历 buffTimes）
        if (buffTimes != null && !buffTimes.isEmpty()) {
            sb.append("\nBuff 信息：\n");
            Set<Map.Entry<Double, Buff>> entrySet = buffTimes.entrySet();
            Iterator<Map.Entry<Double, Buff>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<Double, Buff> entry = iterator.next();
                Double endTime = entry.getKey();
                Buff buff = entry.getValue();
                // 处理 Buff 可能为 null 的情况（根据实际业务调整）
                String buffName = (buff != null) ? buff.getName() : "未知Buff";
                double[] effects = (buff != null) ? buff.getEffects() : new double[0];
                sb.append(String.format(
                    "buff结束时间：%.2f  buff名称: %s  buff效果: %s%n",
                    endTime, buffName, Arrays.toString(effects)
                ));
            }
        }
        return sb.toString();
    }
    public double getDefedDamage(double basicDamage,double defIgnorance_rate,double defIgnorance_point){
        double def=nowStats[3]*(1-defIgnorance_rate)-defIgnorance_point;
        if(def>basicDamage){return 0;}
        else if(def>=0){return (basicDamage-def)*(600/(600+def));}
        else{return (basicDamage-def);}
    }
    public Pair<Double,Integer> beAttacked(Character attacker,double basicDamage){
        CharStat_Fight atkStat=attacker.getAttackerStat();
        Pair<Double,Integer> result=new Pair<Double,Integer>(null, 0);
        int decidedResult=DecInFight.Block_Miss(getStat(6),atkStat.getStat(7),
        getStat(8),atkStat.getStat(9),getLuck()-atkStat.getLuck());
        switch (decidedResult) {
            case 1://格挡
                basicDamage*=0.1;
                result.setRight(1);
                break;
            case 2://闪避
                result.setRight(2);
                return result;
            case 3://弹反
                result.setRight(3);
                result.setLeft(basicDamage);
                return result;
            default:
                break;
        }
        basicDamage*=1+atkStat.getStat(2)*DecInFight.critical(atkStat.getStat(1),atkStat.getLuck()-getLuck());
        result.setLeft(basicDamage);
        return result;
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