package decides;

public class ComDec {
    int luckTime(double luck,int speed){//每移动一格触发随机事件的概率
        double chance=luck/(100000*Math.sqrt(speed));
        return ProDec.decide(chance);
    }
    int luckyOrNot(double luck,double hardship){
        return ProDec.decide(luck/100-hardship);
    }
    
}
