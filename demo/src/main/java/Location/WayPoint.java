package Location;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.TreeSet;

/*基于区块进行的路径点生成，配合每个区块预处理的流场，进行群体中长距离寻路 */
public class WayPoint implements Comparable<WayPoint>{
    int x,y;//中心点坐标
    int dimention;
    FlowField flowField;
    TreeSet<WayPoint> neighbors;
    Block[] sideBlocks=new Block[2];
    public WayPoint(int dimention,int x,int y){
        this.dimention=dimention;this.x=x;this.y=y;this.neighbors=new TreeSet<>();
    }
    public double getOdis(WayPoint otherone){
        return Math.sqrt(Math.pow(x-otherone.x,2)+Math.pow(y-otherone.y, 2));
    }

    private void validateDimention() {
        if (dimention < 0 || dimention >= Location.dimentionChecker.length) {
            throw new IllegalArgumentException("无效的维度索引: " + dimention + "（有效范围: 0-" + (Location.dimentionChecker.length-1) + "）");
        }
    }
    public void addNeighbor(WayPoint other){
        if(other!=null){neighbors.add(other);};
    }
    public byte Contained(WayPoint other){
        /*返回值 
         *1表示输入的路径点的邻居路径点被该路径点包含或者真包含，输入路径点为无效路径点
         * 0表示两个路径点相互独立
         * -1表示输入的路径点真包含该路径点，该路径点为无效路径点
        */
        if (this.neighbors.size()==0||other.neighbors.size()==0) {
            return 0;
        }
        if (this.neighbors.size()>=other.neighbors.size()&&this.neighbors.containsAll(other.neighbors)) {return 1;}
        else if(this.neighbors.size()<other.neighbors.size()&&other.neighbors.containsAll(this.neighbors)){return -1;}
        return 0;
    }
    public boolean noNeighbors(){
        if (neighbors==null||neighbors.isEmpty()) {
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public int compareTo(WayPoint other){
        if (dimention!=other.dimention) {
            return Integer.compare(this.dimention, other.dimention);
        }
        else{
            if (x!=other.x) {
                return Integer.compare(x, other.x);
            }
            else{
                return Integer.compare(y,other.y);   
            }
        }
    }
    @Override
    public boolean equals(Object o) {//维度与坐标整数位相同即视为位置相同
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WayPoint WayPoint = (WayPoint) o;
        // 浮点数使用 Double.compare 避免精度问题
        return dimention == WayPoint.dimention &&
            WayPoint.x==x&&
            WayPoint.y==y;
    }
    @Override
    public int hashCode() {
        return Objects.hash(dimention,x,y);
    }
    @Override
    public String toString() {
        validateDimention(); // 校验维度索引
        DecimalFormat df = new DecimalFormat("0.00"); // 保留两位小数
        return "路径点在 " + Location.dimentionChecker[dimention] + " 的坐标 x=" + df.format(x) + ", y=" + df.format(y) + " 处";
    }
}
