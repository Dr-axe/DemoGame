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
import Character.Character;
import decides.Rand;

import java.util.Iterator;

import display.DisplayUtil;

public class BlockManager {
    private static int blockCounter=0;
    private static long duration=0;
    private static TreeSet<Block> blockMap=new TreeSet<>();
    private static TreeMap<Integer,TreeMap<Integer,TreeSet<WayPoint>>> WayPoints=new TreeMap<>();//三层分别是维度，x，y
    private int seed;
    public BlockManager(){
        this.seed=Rand.nextInt(0, 0x7ffffffe);
        System.out.println(seed);
    }
    public BlockManager(int seed){
        this();this.seed=seed;
    }
    public Block findBlock(Block block){
        Block finder=blockMap.floor(block);
        if(block.equals(finder)){return finder;}
        else{return null;}
    }
    public Block findBlock(int dimention,int x,int y){
        Block block=new Block(x, y, dimention);
        Block finder=blockMap.floor(block);
        if(block.equals(finder)){return finder;}
        else{return null;}
    }
    public short[][] getBlockInfo(int dimention,int x,int y){
        Block block=new Block(x, y, dimention);
        Block finder=blockMap.floor(block);
        if(block.equals(finder)){return finder.getContains();}
        else{return generateBlock(dimention, x, y);}
    }
    public void inputBlock(Block block){
        blockMap.add(block);
    }
    public short[][] generateBlock(int dimention,int X,int Y){
        long startTime = System.nanoTime();
        short[][] chunk = new short[64][64];
        // System.out.println("生成新区块"+X+" "+Y);
        Block newBlock=new Block(X,Y,dimention,seed);
        chunk=newBlock.BlockCreate(seed);
        newBlock.buildFlowField(this);
        blockMap.add(newBlock);
        blockCounter++;
        long endTime = System.nanoTime();
        duration += endTime - startTime;
        if ((blockCounter&0x3f)==0) {
            System.out.println("创建区块数量："+blockCounter);
            System.out.println("区块创建代码累计执行耗时: " + duration / 1_000_000.0 + " 毫秒");
        }
        return chunk;
    }
    public void addWayPoint(WayPoint wayPoint) {
        // 第一层：获取或创建维度对应的x坐标Map（x→TreeSet）
        TreeMap<Integer, TreeSet<WayPoint>> xMap = WayPoints.computeIfAbsent(
            wayPoint.dimention,  
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
                // System.out.println("new Way Point:"+wayPoint.x+" "+wayPoint.y);
            }
        }
    }
    public Character getCharsAt(int dimention,int x,int y){
        Block seeker=findBlock(dimention,x,y);
        TreeSet<Character> characters=seeker.getCharacterSet();
        Character tool=new Character(dimention, x, y);
        Character finder=characters.ceiling(tool);
        if (finder.samePosition(tool)) {
            return finder;
        }else{
            return null;
        }
    }
    public void TravelTo(ArrayList<Character> ChoosedCharacters,int x,int y){
        
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

}
