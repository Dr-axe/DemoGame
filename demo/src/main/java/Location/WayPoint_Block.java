package Location;

public class WayPoint_Block {
    double x,y;//中心点坐标
    boolean ifNS;//true则为南北走向，否则东西走向
    public WayPoint_Block(double x,double y,boolean ifNS){
        this.x=x;this.y=y;this.ifNS=ifNS;
    }
    public double getOdis(WayPoint_Block otherone){
        return Math.sqrt(Math.pow(x-otherone.x,2)+Math.pow(y-otherone.y, 2));
    }
}
