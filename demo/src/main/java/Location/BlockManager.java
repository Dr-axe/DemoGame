package Location;
/* 区块加载管理器 */

import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Iterator;

import display.DisplayUtil;

public class BlockManager {
    TreeSet<Block> blockMap;
    TreeMap<Integer,TreeMap<Integer,TreeSet<WayPoint>>> WayPoints;//三层分别是维度，x，y
    public BlockManager(){
        this.blockMap=new TreeSet<>();
        this.WayPoints=new TreeMap<>();
    }
    public Block findBlock(Block block){
        Block finder=blockMap.floor(block);
        if(block.equals(finder)){return finder;}
        else{return null;}
    }
    public void inputBlock(Block block){
        blockMap.add(block);
    }
    public short[][] generateBlock(int dimention,int chunkX,int chunkY){
        short[][] chunk = new short[64][64];
        Random rand = new Random(chunkX * 31 + chunkY);
        
        // 生成随机障碍物（30%概率）
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                chunk[i][j] = (short) (rand.nextFloat() < 0.3 ? 0x8001 : 0);
            }
        }

        //简单地图障碍物
        // chunk=ResetBlock.reset1;
        chunk[32][32]=2;        
        Block newBlock=new Block(dimention, chunkX, chunkY, chunk);
        newBlock.buildFlowField(this);
        blockMap.add(newBlock);
        return chunk;
    }
    public void addWayPoint(WayPoint wayPoint) {
        // 第一层：获取或创建维度对应的x坐标Map（x→TreeSet）
        TreeMap<Integer, TreeSet<WayPoint>> xMap = WayPoints.computeIfAbsent(
            wayPoint.dimention,  // 修正：维度字段名（假设原dimention是拼写错误）
            k -> new TreeMap<>()
        );
        // 第二层：获取或创建x坐标对应的y坐标Set（按y排序）
        TreeSet<WayPoint> ySet = xMap.computeIfAbsent(
            wayPoint.x ,         // 第二层键为x坐标
            k -> new TreeSet<>()
        );
        // 第三层：添加WayPoint到ySet（自动去重和排序）
        ySet.add(wayPoint);
    }
    public void addWayPointGroup(TreeSet<WayPoint> wg){
        for (WayPoint wayPoint : wg) {
            if (wayPoint!=null||WayPoints.size()<10) {
                addWayPoint(wayPoint);
            }
        }
    }
    public ArrayList<WayPoint> getWaypointsBetween(int dimention,int Minx,int Maxx,int Miny,int Maxy){
        Maxx--;Maxy--;
        ArrayList<WayPoint> wps=new ArrayList<>();//路径点组
        TreeMap<Integer,TreeSet<WayPoint>> dimen_sorted=WayPoints.get(dimention);//筛选维度
        if(dimen_sorted==null){return null;}
        SortedMap<Integer,TreeSet<WayPoint>> x_sorted=dimen_sorted.subMap(Minx,Maxx+1);//筛选x符合条件
        if(x_sorted==null){return null;}
        for (Map.Entry<Integer, TreeSet<WayPoint>> set : x_sorted.entrySet()) {
            SortedSet<WayPoint> temp=set.getValue().subSet(new WayPoint(dimention,set.getKey(),Miny),new WayPoint(dimention,set.getKey(),Maxy+1));//筛选y符合条件
            if (temp!=null&&!temp.isEmpty()) {
                for (WayPoint wayPoint : temp) {
                    wps.add(wayPoint);//将符合条件的路径点加入wps
                }
            }
        }
        if (wps.isEmpty()) {
            return null;
        }else{//当有符合条件的路径点的时候返回路径点组
            return wps;
        }
    }
    public ArrayList<WayPoint> findNearbyWayPoints(Block block){
        Block U=findBlock(block.getUpBlock()),D=findBlock(block.getDownBlock()),
        L=findBlock(block.getLeftBlock()),R=findBlock(block.getRightBlock());
        ArrayList<WayPoint> wpoints=new ArrayList<>();
        if(U!=null){
            ArrayList<WayPoint> temp=getWaypointsBetween(U.getDimention(), U.getLX(), U.getRX(), U.getUY(), U.getDY());
            if(temp!=null){wpoints.addAll(temp);}
        }
        if(D!=null){
            ArrayList<WayPoint> temp=getWaypointsBetween(D.getDimention(), D.getLX(), D.getRX(), D.getUY(), D.getDY());
            if(temp!=null){wpoints.addAll(temp);}
        }
        if(L!=null){
            ArrayList<WayPoint> temp=getWaypointsBetween(L.getDimention(), L.getLX(), L.getRX(), L.getUY(), L.getDY());
            if(temp!=null){wpoints.addAll(temp);}
        }
        if(R!=null){
            ArrayList<WayPoint> temp=getWaypointsBetween(R.getDimention(), R.getLX(), R.getRX(), R.getUY(), R.getDY());
            if(temp!=null){wpoints.addAll(temp);}
        }
        return wpoints;
    }

    public static void main(String[] args) {
        BlockManager tst=new BlockManager();
        tst.inputBlock(new Block(0, 0, 0, false));
        Block foundedBlock=tst.findBlock(new Block(0, 0, 0));
        DisplayUtil.display(foundedBlock.getContains(), 0.2, 0.2);
    }
}
