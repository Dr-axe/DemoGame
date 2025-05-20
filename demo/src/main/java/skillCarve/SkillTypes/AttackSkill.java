package skillCarve.SkillTypes;

import skillCarve.skill;
import skillCarve.skillStats;

public class AttackSkill extends skill {
    private static boolean firendOnly=false;
    private static boolean friendly=false;
    private static boolean selfOnly=false;
    public AttackSkill(String skillName,skillStats skillStats,int skillType){
        super(skillName,skillStats, skillType);
    }
}
