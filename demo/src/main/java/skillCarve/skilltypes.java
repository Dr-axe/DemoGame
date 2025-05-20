package skillCarve;

public class skilltypes {
    private static final String[] types={"攻击","防御","卫戍","削弱","辅助","治疗"};
    public static int getSize(){return types.length;}
    public static String getType(int x){
        if(x>=0&&x<types.length){return types[x];}
        else{return "未知类型技能";}
    }
    public Character[] chooseTargets(Character[] posibleTargets){
        return null;
    }
}
