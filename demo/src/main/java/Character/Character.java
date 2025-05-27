package Character;

import java.util.ArrayList;

import Character.CharacterStat_common.CharStat_Common;
import Character.CharacterStat_fight.CharStat_Fight;
import Character.RandomName.RandomName;
import Location.BlockManager;
import Location.Location;
import attributions.attributionsSet;
import decides.Rand;
import skillCarve.skill;
import skillCarve.skillStats;

public class Character implements Comparable<Character>{
    public static int CharacterIndex=0;
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
        this.CharacterID=CharacterIndex;
        CharacterIndex++;
        this.Attribution=Rand.nextInt(0,10);
        addSkill(new skill(name+" 的先天技能",new skillStats(RandomName.getRandomName(), level), 0));
    }
    public Character(String name,int level,Location location){
        this.comStat=new CharStat_Common(level);
        this.fightStat=new CharStat_Fight(comStat);
        this.name=name;
        this.location=location;
        this.Attribution=Rand.nextInt(0,10);
        this.CharacterID=CharacterIndex;
        CharacterIndex++;
        addSkill(new skill(name+" 的先天技能",new skillStats(RandomName.getRandomName(), level), 0));
    }
    public Character(int dimention,int x,int y){
        this.location=new Location(dimention, x, y);
        this.CharacterID=-1;
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
    public boolean samePosition(Character other){return this.location.equals(other.location);}
    @Override
    public int compareTo(Character other){
        int locationCMP=this.location.compareTo(other.location);
        if (locationCMP==0) {
            return Integer.compare(this.CharacterID,other.CharacterID);    
        }
        else{
            return locationCMP;
        }
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character other=(Character)o;
        return this.CharacterID==other.CharacterID;
    }
    public static void main(String[] args) {
        Character BEEP=new Character("余润东", 1, new Location(1, 999, -400));
        String testText=BEEP.getInfo();
        System.out.print(testText);
    }
}
