package skillCarve;
import dataDeals.*;
public class skillStats{
    int[] attributions;
    int type,aoeSize,range;//类型，范围，有效距离
    double prepareState,weakState;
    String[] buffs;
    String[] debuffs;
    public skillStats(int[] attributions,int type,int aoeSize,int range,
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
        return seed;
    }
    private void mkSpecialStats(String words,int skillevel){
        /*详细方法暂略 */

    }
    public skillStats(String words,int skillevel){
        /* 种子+模板生成技能，用于随机生成和用户“抽奖”式生成 */
        mkSpecialStats(words,skillevel);
    }
}