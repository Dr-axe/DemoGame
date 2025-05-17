package BuffAndDebuff;
import Time.TimeManager;
public final class Buff {
    private int[] targets;
    private double[] effects;
    private double duration;//持续到什么时候
    private String name,description;
    private int level;
    private int buffID;
    public Buff(int buffID,int level){
        this.buffID=buffID;
        constructBuff(level);
    }
    private void constructBuff(int inputLevel){
            BuffType.BuffData buff = BuffType.fromID(buffID).getBuff(inputLevel);
            this.name=buff.getType().getName();
            this.description=buff.getType().getDescription();
            this.targets=buff.getTargets();
            this.effects=buff.getEffects();
            this.duration=buff.getDuration()+TimeManager.time;
            this.level=inputLevel;
    }
    public void increaseDuration(double addDuration){
        if (this.duration<addDuration/2) {
            this.duration=addDuration;
            return ;
        }
        else{
            this.duration+=addDuration/2;
        }
    }
    public void upadteLevel(int inputLevel){
        BuffType.BuffData buff = BuffType.fromID(buffID).getBuff(inputLevel);
        this.effects=buff.getEffects();
        increaseDuration(buff.getDuration());
        this.level=inputLevel;
    }
    public void mkitMaxLevel(int inputLevel){
        if (inputLevel>level) {
            upadteLevel(inputLevel);
        }
    }
    public int getID(){return buffID;}
    public double getDuration(){return duration;}
    public int getLevel(){return level;}
    public int[] getTargets(){return targets;}
    public double[] getEffects(){return effects;}
    public String getName(){return name;}
    public String getDescription(){return description;}
    public String getText(){return name+"\n"+description;}
}
