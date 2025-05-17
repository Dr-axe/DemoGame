package Character.CharacterStat_fight;
import java.util.Arrays;

import Character.CharacterStat_common.*;
public class CharStat_Fight {
    private double[] nowStats;
    private double[] basicStats;
    private double[] changeRate;
    public CharStat_Fight(CharStat_Common comStat){
        this.nowStats=comStat.getBasicStats();
        this.basicStats=comStat.getBasicStats();
        this.changeRate=new double[16];
        Arrays.fill(changeRate,1.0);
    }
}