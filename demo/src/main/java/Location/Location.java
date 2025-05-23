package Location;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;

import Character.Character;
public class Location implements Comparable<Location>{
    private int dimention;
    private double x,y;
    public static String[] dimentionChecker=new String[]{
        "地球","巢都","星界城","月球","火星","辛守村"
    };
    public Location(int dimention,double x,double y){//用于生成查找工具检索坐标
        this.dimention=dimention;
        this.x=x;
        this.y=y;
    }
    // 新增：校验维度索引的有效性（避免数组越界）
    private void validateDimention() {
        if (dimention < 0 || dimention >= dimentionChecker.length) {
            throw new IllegalArgumentException("无效的维度索引: " + dimention + "（有效范围: 0-" + (dimentionChecker.length-1) + "）");
        }
    }
    public int getRecognize() {//把维度和X坐标压缩到一个int里面方便进行排序和查询
        return dimention<<24|((int)x+8000000);
    }
    @Override
    public int compareTo(Location other){
        if (dimention!=other.dimention) {
            return Integer.compare(this.dimention, other.dimention);
        }
        else{
            if (x!=other.x) {
                return Double.compare(x, other.x);
            }
            else{
                return Double.compare(y,other.y);   
            }
        }
    }
    @Override
    public boolean equals(Object o) {//维度与坐标整数位相同即视为位置相同
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        // 浮点数使用 Double.compare 避免精度问题
        return dimention == location.dimention &&
            (int)location.x==(int)x&&
            (int)location.y==(int)y;
    }
    @Override
    public int hashCode() {
        return Objects.hash(dimention,(int)x, (int)y);
    }
    @Override
    public String toString() {
        validateDimention(); // 校验维度索引
        DecimalFormat df = new DecimalFormat("0.00"); // 保留两位小数
        return "现在正在 " + dimentionChecker[dimention] + " 的坐标 x=" + df.format(x) + ", y=" + df.format(y) + " 处";
    }
}
