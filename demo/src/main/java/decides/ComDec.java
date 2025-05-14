package decides;

public class ComDec {
    int luckTime(double luck,int speed){//每移动一格触发随机事件的概率
        double chance=luck/(100000*Math.sqrt(speed));
        return ProDec.decide(chance);
    }
    int luckyOrNot(double luck,double hardship){
        return ProDec.decide(luck/100-hardship);
    }
    int StrengthJudge(int Strength,double luck,double hardship){//数字越大代表结果越好
        int judge=5;
        if(ProDec.decide(luck/100-hardship)==1){
            return 6;
        }
        double chance=1-hardship/Strength;
        while (judge>0) {
            if(ProDec.decide(chance)==1){return judge;}
            judge--;
        }
        return judge;
    }
}
