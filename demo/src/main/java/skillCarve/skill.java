package skillCarve;
public class skill {
    private skillStats skillStats;
    private String skillName;
    private int skillType;
    /*部分暂时不加入，包括特效等等 */
    public skill(String skillName,skillStats skillStats,int skillType){
        this.skillName=skillName;
        this.skillStats=skillStats;
        this.skillType=skillType;
    }
    @Override
    public String toString(){
        return "技能名称："+skillName +"\n技能类型："+skilltypes.getType(skillType)+"\n"+skillStats.toString();
    }
}
