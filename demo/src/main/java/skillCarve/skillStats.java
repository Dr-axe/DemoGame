package skillCarve;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import attributions.attributionsSet;
import dataDeals.*;
public class skillStats{
    Set<Integer> attributions = new LinkedHashSet<>();
    int basedStat,aoeSize,range;//类型，范围，有效距离
    double prepareState,weakState;
    double power;
    int[] buffs;//最多4个
    int[] debuffs;//最多4个
    public skillStats(LinkedHashSet<Integer> attributions,int basedStat,int aoeSize,int range,double power,
    int[] buffs,int[] debuffs,double prepareState,double weakState){
        /*传参初始化，用于预设的和特定生成的技能模板 */
        this.attributions=attributions;
        this.basedStat=basedStat;
        this.aoeSize=aoeSize;
        this.range=range;
        this.power=power;
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
    private boolean wordDecideAttribution(RK WC,String[] keyWords,int attribution){
        for (String string : keyWords) {if(WC.strFind(string)){return true;}}
        return false;
    }
    private int loadAttributions(long seed,String words){
        int cost=0;
        RK WC=new RK(words);
        if(WC.strFind("我是剑骨头")){return -200;}
        if(seed%114514==0){return -100;}
        String[][] keywords={
            {"风","Wind","Storm","wuuuuuu","飙","飓"},
            {"岩","石","Rock","Stone","Hard","飙","城"},
            {"金","Cyber","Magne","MAGA","人机","AI","MetalMind"},
            {"木","Organic","Wood","Plant","你妈"},
            {"水","氵","氺","Water","Storm","雷暴","😭","飓"},
            {"火","Fire","flame","炎","炽热","熔","huo","😡","😠","🔥","太阳出来我晒太阳"},
            {"土","Soi","地","山"},
            {"电","Elec","雷","Thunder","五连鞭"},
            {"光","Light","好亮的腿，啊不对,好白的灯"},
            {"暗","黑手","dark","😱","😨"},
            {"癫狂","Crazy","Joker","五连鞭","典","绷","孝","乐","急","🤡","😀","😃","😄","😁","😆","😅","😊","😋","😎","😍","😏","😥","😢","😂"}
        };
        int at=0;
        for (String[] strings : keywords) {
            if(wordDecideAttribution(WC, strings,at )){attributions.add(at);cost+=attributions.size()/2;}
            at++;
        }
        int size=attributionsSet.getSize();
        do{
            if(attributions.add(abs((int)seed)%size)){cost+=attributions.size();}//加入成功了就增加cost,最终
            seed^=seed<<21;
            seed^=seed>>42;
            seed^=seed<<35;
        }while(Math.abs(seed%(size*2))<size);
        return cost;
    }
    private void loadbasedStat(long seed){//0表示攻击力，1表示暴击率，2表示暴击伤害，3表示防御力，4表示生命值，5表示速度
        basedStat=abs((int)seed)%6;
    }
    private double loadAoeSize(long seed,int skillevel){//avgCost:18~32
        double sizeRand=Math.random();
        aoeSize=(int)Math.sqrt(sizeRand*skillevel*16)+1;
        return aoeSize*aoeSize*2.0;
    }
    private double loadRange(long seed,int skillevel){//avgCost:22
        double sizeRand=Math.random();
        range=(int)Math.sqrt(sizeRand*skillevel*200)+1;
        return range*2.0;
    }
    private double loadPre(long seed,int skillevel){
        double reduce=(double)(Math.abs(seed<<(skillevel)^seed))%200;
        if(reduce*skillevel>100){prepareState=0;return 25/skillevel;}
        prepareState=(100)/skillevel-reduce;
        return reduce/4;
    }
    private double loadWeakState(long seed,int skillevel){
        double reduce=(double)(Math.abs(seed<<(skillevel)^seed))%200;
        if(reduce*skillevel>100){weakState=100/skillevel;return 50/skillevel;}
        weakState=(200)/skillevel-reduce;
        return reduce/2;
    }
    private int abs(int a) {
        int mask = a >> 31;          // 符号位扩展（正数0，负数-1）
        return (a ^ mask) - mask;    // 等价于绝对值
    }
    private double loadbuff(long seed,int skillevel){
        double usedWeight=0;
        int[] x=new int[4];
        for(int j=0;j<4;j++){
        int way=abs((int)seed%13);
            for (int i = 0; i < 4; i++) {
                if(seed%3!=0&&i!=3){seed=(seed<<17)^seed;}
                else{
                    int the=way*4+i;
                    for(int n=j-1;n>=0;n--){
                        if(x[n]==abs(the)){
                            buffs=new int[j];while(j>0){buffs[j-1]=x[j-1];j--;}
                            return usedWeight;
                        }
                    }
                    x[j]=way*4+i;usedWeight+=skillevel*(4.0*i+5);
                    break;
                }
            }
        }
        buffs=x;
        return usedWeight;
    }
    private double loadDebuff(long seed,int skillevel){
        double usedWeight=0;seed=(seed^-1)<<skillevel|skillevel;
        if(seed%((skillevel+5)/skillevel)==0){return 0;}//判定成功则不出现debuff
        int[] x=new int[4];
        for(int j=0;j<4;j++){
        int way=abs((int)seed%13);
            for (int i = 0; i < 4; i++) {
                if(seed%3!=0&&i!=3){seed=(seed<<17)^seed;}
                else{
                    int the=way*4+i;
                    for(int n=j-1;n>=0;n--){
                        if(x[n]==-abs(the)){
                            debuffs=new int[j];while(j>0){debuffs[j-1]=x[j-1];j--;}
                            return usedWeight;
                        }
                    }
                    x[j]=-way*4-i;usedWeight-=skillevel*(4.0*i+5);}
                    break;
                }
        }
        debuffs=x;
        return usedWeight;
    }
    private void mkSpecialStats(String words,int skillevel){
        /*详细方法正在补充*/
        long seed =getSeed(words);
        int usefulLen=words.length()>8?words.length():8;
        usefulLen=usefulLen<30?usefulLen:30;
        usefulLen+=12;
        double weight=100+skillevel*2000/(usefulLen);//在语句长度有限的时候，每级提供100(暂定)权重
        weight-=loadAttributions(seed,words);//无属性算个彩蛋,提供+100权重，n属性权重为n*(n+1)/2,如果这一步权重就被爆到负数的话，后面就好玩了
        loadbasedStat(seed);
        weight-=loadAoeSize(seed,skillevel);
        weight -= loadRange(seed, skillevel);
        weight-=loadPre(seed, skillevel);
        weight-=loadWeakState(seed, skillevel);
        weight-=loadbuff(seed, skillevel);
        weight-=loadDebuff(seed, skillevel);
        power= 0.1>weight ? 0.1:weight;
    }
    public skillStats(String words,int skillevel){
        /* 种子+模板生成技能，用于随机生成和用户“抽奖”式生成 */
        mkSpecialStats(words,skillevel);
    }
    public void printAllAttributes() {
        System.out.println("=========== 技能属性详情 ===========");
        System.out.println("◆ 属性集合: " + safeCollectionToString(attributions));
        System.out.println("◆ 基础属性类型: " + getStatTypeName(basedStat));
        System.out.println("◆ 范围半径: " + aoeSize + " 格");
        System.out.println("◆ 有效射程: " + range + " 格");
        System.out.println("◆ 准备状态系数: " + String.format("%.2f", prepareState));
        System.out.println("◆ 虚弱状态系数: " + String.format("%.2f", weakState));
        System.out.println("◆ 技能强度: " + String.format("%.2f", power));
        System.out.println("◆ 增益效果: " + safeArrayToString(buffs));
        System.out.println("◆ 减益效果: " + safeArrayToString(debuffs));
        System.out.println("==================================");
    }
    private String safeCollectionToString(Set<?> collection) {
        return (collection == null || collection.isEmpty()) 
            ? "无" 
            : collection.toString();
    }
    private String safeArrayToString(int[] array) {
        if (array == null) return "未初始化";
        if (array.length == 0) return "无";
        return Arrays.toString(array);
    }
    private String getStatTypeName(int typeCode) {
        String[] statNames = {
            "攻击力", "暴击率", "暴击伤害", 
            "防御力", "生命值", "速度"
        };
        return (typeCode >= 0 && typeCode < statNames.length) 
            ? statNames[typeCode] 
            : "未知类型 (" + typeCode + ")";
    }
    public static void main(String[] args) {
        Scanner Scanner=new Scanner(System.in);
        System.out.println("分别输入法咒和技能等阶：\n");
        skillStats test=new skillStats(Scanner.nextLine(), Scanner.nextInt());
        test.printAllAttributes();
        Scanner.close();
        return ;
    }
}