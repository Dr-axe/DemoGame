package Character;

import java.util.ArrayList;

import Character.CharacterStat_common.CharStat_Common;
import Character.CharacterStat_fight.CharStat_Fight;
import Character.RandomName.RandomName;
import Location.Location;
import attributions.attributionsSet;
import decides.Rand;
import skillCarve.skill;
import skillCarve.skillStats;

public class Character {
    private String name;
    private int CharacterID;
    private int Attribution;
    private CharStat_Common comStat;
    private CharStat_Fight fightStat;
    private ArrayList<skill> skills=new ArrayList<>();
    private Location location;
    private int hunger_MAX,hunger_MIN,hunger;
    public Character(int level,Location location){
        this.comStat=new CharStat_Common(level);
        this.fightStat=new CharStat_Fight(comStat);
        this.name=RandomName.getRandomName();
        this.location=location;
        this.Attribution=Rand.nextInt(0,10);
        addSkill(new skill(name+" 的先天技能",new skillStats(RandomName.getRandomName(), level), 0));
    }
    public Character(String name,int level,Location location){
        this.comStat=new CharStat_Common(level);
        this.fightStat=new CharStat_Fight(comStat);
        this.name=name;
        this.location=location;
        this.Attribution=Rand.nextInt(0,10);
        addSkill(new skill(name+" 的先天技能",new skillStats(RandomName.getRandomName(), level), 0));
    }
    private void addSkill(skill inputSkill){
        skills.add(inputSkill);
    }
    public CharStat_Fight getAttackerStat(){return fightStat;}
    public int getID(){return CharacterID;}
    public Location getLocation(){return location;}
    public String getInfo(){
        String AttributionName="角色属性："+attributionsSet.getAttribution(Attribution);
        String commonStatPlate=comStat.toString();
        String fightStatPlat=fightStat.toString();
        String prints="角色名称："+name+"\n"+AttributionName+"\n"+commonStatPlate+"\n"+fightStatPlat+"\n";
        for (skill skill : skills) {
            prints+=skill.toString()+"\n";
        }
        prints+=location.toString();
        return prints;
    }
    public boolean useSkill(){
        /*待完成 */
        return false;
    }
    public static void main(String[] args) {
        Character BEEP=new Character("余润东", 10, new Location(1, 999, -400));
        String testText=BEEP.getInfo();
        System.out.print(testText);
    }
}
