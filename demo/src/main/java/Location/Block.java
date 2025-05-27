package Location;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import Character.Character;

import java.util.Iterator;

import Location.Location;
import decides.Rand;

public class Block implements Comparable<Block> {
    private static long duration=0;
    private static int blockCounter=0;

    private int x, y, dimention;
    private short[][] contains;
    private boolean reconsructed;
    private BlockStat stat;
    private TreeSet<Character> characters;
    public Block(int x, int y, int dime, int seed) {
        this(x, y, dime);
        stat = new BlockStat(seed, x, y);
    }

    public Block(int x, int y, int dime) {
        this.x = x;
        this.y = y;
        this.dimention = dime;
    }

    public Block(int x, int y, int dime, boolean testIF) {
        this(x, y, dime);
        contains = new short[5][5];
        for (short[] s : contains) {
            for (int i = 0; i < 5; i++) {
                s[i] = 1;
            }
        }
    }

    public Block(int dimention, int x, int y, short[][] contains) {
        this(x, y, dimention);
        this.contains = contains;
    }

    private void BC_Rock(int seed) {
        /* 目前来说效果不错了，暂时不改动了 */
        if (stat.getRock_exposure() > 0.05) {
            double minx=x*64*0.05,miny=y*64*0.05;
            double[][] rockMap=OptimizedPerlinNoise.generateFractalNoiseMap(miny,minx,miny+3.15,minx+3.15,
            seed, 4,0.7,2.0);
            final double rockExposure = stat.getRock_exposure();
            final double thresholdNonWater = 0.25 - rockExposure * 0.5;
            final double thresholdWater = 0.5;
            for (int i = 0; i < 64; i++) {
                final short[] containsRow = contains[i];
                final double[] rockMapRow = rockMap[i];
                for (int j = 0; j < 64; j++) {
                    if (containsRow[j] != (short) 0xc003) { // 非水地块
                        containsRow[j] = (short) (rockMapRow[j] > thresholdNonWater ? 0xe001 : containsRow[j]);
                    } else { // 水地块
                        containsRow[j] = (short) (rockMapRow[j] > thresholdWater ? 0xe001 : containsRow[j]);
                    }
                }
            }
        }
    }

    public static double sharpness_Mount=0.01;
    private void BC_MountAndLake(int seed) {
        
        int count=4096;
        double miny=(y<<6)*0.0002,minx=(x<<6)*0.0002;
        double[][] waterMap=OptimizedPerlinNoise.generateFractalNoiseMap(miny,minx,miny+0.0126, minx+0.0126,
        seed, 8,0.6,2.3);
        for(int i=0;i<64;i++){
            final short[] containsRow = contains[i];
            final double[] waterMapRow = waterMap[i]; // 假设 rockMap 是 double[][] 类型
            for(int j=0;j<64;j++){
                if (waterMapRow[j]>0.08) {
                    containsRow[j]=(short)0xc003;
                    count--;
                }else if (waterMapRow[j]<-0.1) {
                    containsRow[j]=2;
                }
            }
        }
        double sumHeight=0;
        long startTime = System.nanoTime();
        miny=(y<<6)*sharpness_Mount;minx=(x<<6)*sharpness_Mount;
        double[][] heightMap=OptimizedPerlinNoise.generateFractalNoiseMap(miny,minx,miny+0.63,minx+0.63,
         seed,6,0.4,1.8);
        for (int i = 0; i < 64; i++) {
            /* 山地生成和水面被覆盖的逻辑，条件循环至已优化 7000block 42ms 0.006ms/block */
            final double[] heights=heightMap[i];
            final short[] containsRow=contains[i];
            for (int j = 0; j < 64; j++) {
                double height = heights[j];
                if (height<0.09) {continue;}//小于0.09则不会发生改变，因此小于0.09直接进入下一步循环
                if (containsRow[j] == (short) 0xc003) {
                    if (height>0.15) {
                        if (height<0.22) {
                            containsRow[j] = (short) 0x0004;
                        }
                        if (height<0.28) {
                            containsRow[j] = (short) 0x4005;
                        }else{
                            containsRow[j] = (short) 0xe006;
                        }
                    }
                    else{
                        if (height>0.09) {//当高度足够大时从水转化成平地
                            containsRow[j] = 0;
                        }
                    }
                } else {
                    sumHeight += height;
                    if (height>0.25) {
                        if (height<0.30) {//最高峰分界线 0.35
                            containsRow[j] = (short) 0xe006;
                        }else{
                            containsRow[j] = (short) 0xe007;
                        }
                    }
                    else{
                        if (height > 0.12) {//1级隆起（可行走）
                            containsRow[j] = (short) 0x0004;
                        }else if (height>0.19) {//0.19~0.25 LV II
                            containsRow[j] = (short) 0x4005;
                        }
                    }
                }
            }
        }
        stat.loadHeight(sumHeight/count);
        blockCounter++;
        long endTime = System.nanoTime();
        duration += endTime - startTime;
        if ((blockCounter&0x3f)==0) {
            System.out.println("选择分支代码累计执行耗时: " + duration / 1_000_000.0 + " 毫秒");
        }
    }
    private void BC_River(int seed){
        for(int i=0;i<64;i++){
            for(int j=0;j<64;j++){
                if (FractalPerlinNoise.fractalNoise(seed, (x*64+i)*0.0025,(64*y+j)*0.0025,8,
                0.6, 2.3, 0.5, 1)>0.05) {
                    contains[i][j]=(short)0xc003;
                }
            }
        }
    }
    private void BC_Plants(int seed){
        /* 5200block 40ms 0.0076 毫秒/区块 */
        /* 繁荣度通常在 -0.3~0.3,对应 0.1~1.0的植被密度，(1-均高*2)为高度植被减益
         * 湿度和均高决定植被类型
         * 湿度>0.11则决定为雨林区  雨林植被
         * 湿度介于0.03~0.11决定为一般森林 
         * 湿度介于-0.02~0.03决定为草原
         * 湿度介于-0.1~-0.02决定为稀树原
         * 湿度<-0.1决定为沙地
         * 
         * 高度决定植被的分级
         * <0.15
         * >0.15
         * >0.2
         * >0.25
         * >0.3
         * 初期先采用全部都覆盖红木作为测试
        */
        int plantsCount=(int)((0.50+stat.getProlificacy()*1.5)*1024*(1-stat.getHeight()*2)*(0.4+stat.getMoisture()*2));
        ArrayList<Integer> treeMap=Rand.NextBlock_seed(seed, plantsCount, x,y);
        for (Integer integer : treeMap) {
            if(contains[integer>>6][integer&0x3f]!=(short)0xc003){
                contains[integer>>6][integer&0x3f]=(short)0xe008;
            }
        }
    }
    public short[][] BlockCreate(int seed) {
        this.contains = new short[64][64];
        for (short[] s : contains) {
            Arrays.fill(s,(short)0);
        }
        // BC_River(seed);
        BC_MountAndLake(seed);
        BC_Rock(seed);
        BC_Plants(seed);
        // this.stat.printOut();
        return contains;
    }

    private ArrayList<WayPoint> getNeighborWayPoints(BlockManager blockManager) {
        return blockManager.findNearbyWayPoints(this);
    }

    private int getABSmol(int x) {
        return Math.floorMod(x, 64);
    }

    private int[][] buildAroundBFS(BlockManager blockManager) {// 只返回中心区域的深度权重图
        Block U = blockManager.findBlock(getUpBlock()), D = blockManager.findBlock(getDownBlock()),
                L = blockManager.findBlock(getLeftBlock()), R = blockManager.findBlock(getRightBlock());
        int[][] centreArea = new int[64][64];
        for (int[] is : centreArea) {
            Arrays.fill(is, 0);
        }
        if (U != null) {
            ArrayList<WayPoint> temp = blockManager.getWaypointsBetween(U.getDimention(), U.getLX(), U.getRX(),
                    U.getUY(), U.getDY());
            if (temp != null) {
                short[][] tempContains = U.contains;
                byte[][] map = new byte[128][64];
                for (int i = 0; i < 64; i++) {
                    for (int j = 0; j < 64; j++) {
                        map[i][j] = (byte) ((tempContains[i][j] >> 15) & 1);
                        map[i + 64][j] = (byte) ((contains[i][j] >> 15) & 1);
                    }
                }
                TreeSet<Integer> thisDepth = new TreeSet<>();
                TreeSet<Integer> nextDepth = new TreeSet<>();
                for (int i = 0; i < temp.size(); i++) {
                    thisDepth.add(getABSmol(temp.get(i).x) << 10 | getABSmol(temp.get(i).y));
                }
                int[][] BFSmap = new int[map.length][map[0].length];
                for (int[] row : BFSmap) {
                    Arrays.fill(row, 0x7fffffff);
                }
                while (!thisDepth.isEmpty()) {
                    int depth = 0;
                    for (Integer location : thisDepth) {
                        int x = (location >> 10) & 0x3f;
                        int y = location & 0x3f;
                        BFSmap[x][y] = depth;
                        if (x > 63) {
                            centreArea[x][y] += depth;
                        }
                        if (x + 1 < 128 && map[x + 1][y] != 1 && BFSmap[x + 1][y] > depth) {
                            nextDepth.add((x + 1) << 10 | y);
                        }
                        if (x - 1 >= 0 && map[x - 1][y] != 1 && BFSmap[x - 1][y] > depth) {
                            nextDepth.add((x - 1) << 10 | y);
                        }
                        if (y + 1 < 64 && map[x][y + 1] != 1 && BFSmap[x][y + 1] > depth) {
                            nextDepth.add(x << 10 | (y + 1));
                        }
                        if (y - 1 >= 0 && map[x][y - 1] != 1 && BFSmap[x][y - 1] > depth) {
                            nextDepth.add(x << 10 | (y - 1));
                        }
                    }
                    thisDepth = nextDepth;
                    nextDepth = new TreeSet<>();
                    depth++;
                }
            }

        }
        if (D != null) {
            ArrayList<WayPoint> temp = blockManager.getWaypointsBetween(D.getDimention(), D.getLX(), D.getRX(),
                    D.getUY(), D.getDY());
            if (temp != null) {
                short[][] tempContains = D.contains;
                byte[][] map = new byte[128][64];
                for (int i = 0; i < 64; i++) {
                    for (int j = 0; j < 64; j++) {
                        map[i + 64][j] = (byte) ((tempContains[i][j] >> 15) & 1);
                        map[i][j] = (byte) ((contains[i][j] >> 15) & 1);
                    }
                }
                TreeSet<Integer> thisDepth = new TreeSet<>();
                TreeSet<Integer> nextDepth = new TreeSet<>();
                for (int i = 0; i < temp.size(); i++) {
                    thisDepth.add(getABSmol(temp.get(i).x) << 10 | getABSmol(temp.get(i).y));
                }
                int[][] BFSmap = new int[map.length][map[0].length];
                for (int[] row : BFSmap) {
                    Arrays.fill(row, 0x7fffffff);
                }
                while (!thisDepth.isEmpty()) {
                    int depth = 0;
                    for (Integer location : thisDepth) {
                        int x = (location >> 10) & 0x3f;
                        int y = location & 0x3f;
                        BFSmap[x][y] = depth;
                        if (x < 64) {
                            centreArea[x][y] += depth;
                        }
                        if (x + 1 < 128 && map[x + 1][y] != 1 && BFSmap[x + 1][y] > depth) {
                            nextDepth.add((x + 1) << 10 | y);
                        }
                        if (x - 1 >= 0 && map[x - 1][y] != 1 && BFSmap[x - 1][y] > depth) {
                            nextDepth.add((x - 1) << 10 | y);
                        }
                        if (y + 1 < 64 && map[x][y + 1] != 1 && BFSmap[x][y + 1] > depth) {
                            nextDepth.add(x << 10 | (y + 1));
                        }
                        if (y - 1 >= 0 && map[x][y - 1] != 1 && BFSmap[x][y - 1] > depth) {
                            nextDepth.add(x << 10 | (y - 1));
                        }
                    }
                    thisDepth = nextDepth;
                    nextDepth = new TreeSet<>();
                    depth++;
                }
            }
        }
        if (L != null) {
            ArrayList<WayPoint> temp = blockManager.getWaypointsBetween(L.getDimention(), L.getLX(), L.getRX(),
                    L.getUY(), L.getDY());
            if (temp != null) {
                short[][] tempContains = L.contains;
                byte[][] map = new byte[64][128];
                for (int i = 0; i < 64; i++) {
                    for (int j = 0; j < 64; j++) {
                        map[i][j] = (byte) ((tempContains[i][j] >> 15) & 1);
                        map[i][j + 64] = (byte) ((contains[i][j] >> 15) & 1);
                    }
                }
                TreeSet<Integer> thisDepth = new TreeSet<>();
                TreeSet<Integer> nextDepth = new TreeSet<>();
                for (int i = 0; i < temp.size(); i++) {
                    thisDepth.add(getABSmol(temp.get(i).x) << 10 | getABSmol(temp.get(i).y));
                }
                int[][] BFSmap = new int[map.length][map[0].length];
                for (int[] row : BFSmap) {
                    Arrays.fill(row, 0x7fffffff);
                }
                while (!thisDepth.isEmpty()) {
                    int depth = 0;
                    for (Integer location : thisDepth) {
                        int x = (location >> 10) & 0x3f;
                        int y = location & 0x3f;
                        BFSmap[x][y] = depth;
                        if (y > 63) {
                            centreArea[x][y] += depth;
                        }
                        if (x + 1 < 64 && map[x + 1][y] != 1 && BFSmap[x + 1][y] > depth) {
                            nextDepth.add((x + 1) << 10 | y);
                        }
                        if (x - 1 >= 0 && map[x - 1][y] != 1 && BFSmap[x - 1][y] > depth) {
                            nextDepth.add((x - 1) << 10 | y);
                        }
                        if (y + 1 < 128 && map[x][y + 1] != 1 && BFSmap[x][y + 1] > depth) {
                            nextDepth.add(x << 10 | (y + 1));
                        }
                        if (y - 1 >= 0 && map[x][y - 1] != 1 && BFSmap[x][y - 1] > depth) {
                            nextDepth.add(x << 10 | (y - 1));
                        }
                    }
                    thisDepth = nextDepth;
                    nextDepth = new TreeSet<>();
                    depth++;
                }
            }
        }
        if (R != null) {
            ArrayList<WayPoint> temp = blockManager.getWaypointsBetween(R.getDimention(), R.getLX(), R.getRX(),
                    R.getUY(), R.getDY());
            if (temp != null) {
                short[][] tempContains = R.contains;
                byte[][] map = new byte[64][128];
                for (int i = 0; i < 64; i++) {
                    for (int j = 0; j < 64; j++) {
                        map[i][j + 64] = (byte) ((tempContains[i][j] >> 15) & 1);
                        map[i][j] = (byte) ((contains[i][j] >> 15) & 1);
                    }
                }
                TreeSet<Integer> thisDepth = new TreeSet<>();
                TreeSet<Integer> nextDepth = new TreeSet<>();
                for (int i = 0; i < temp.size(); i++) {
                    thisDepth.add(getABSmol(temp.get(i).x) << 10 | getABSmol(temp.get(i).y));
                }
                int[][] BFSmap = new int[map.length][map[0].length];
                for (int[] row : BFSmap) {
                    Arrays.fill(row, 0x7fffffff);
                }
                while (!thisDepth.isEmpty()) {
                    int depth = 0;
                    for (Integer location : thisDepth) {
                        int x = (location >> 10) & 0x3f;
                        int y = location & 0x3f;
                        BFSmap[x][y] = depth;
                        if (y < 64) {
                            centreArea[x][y] += depth;
                        }
                        if (x + 1 < 64 && map[x + 1][y] != 1 && BFSmap[x + 1][y] > depth) {
                            nextDepth.add((x + 1) << 10 | y);
                        }
                        if (x - 1 >= 0 && map[x - 1][y] != 1 && BFSmap[x - 1][y] > depth) {
                            nextDepth.add((x - 1) << 10 | y);
                        }
                        if (y + 1 < 128 && map[x][y + 1] != 1 && BFSmap[x][y + 1] > depth) {
                            nextDepth.add(x << 10 | (y + 1));
                        }
                        if (y - 1 >= 0 && map[x][y - 1] != 1 && BFSmap[x][y - 1] > depth) {
                            nextDepth.add(x << 10 | (y - 1));
                        }
                    }
                    thisDepth = nextDepth;
                    nextDepth = new TreeSet<>();
                    depth++;
                }
            }

        }
        return centreArea;
    }

    public TreeSet<WayPoint> buildWayPoints(int[][] centreArea) {// 获取该区块上的路径点
        byte[][] map = new byte[64][64];
        TreeSet<Integer> posibleWayPoints = new TreeSet<>();
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                map[i][j] = (byte) (contains[i][j] >> 15 & 1);
                if ((i == 0 || i == 63 || j == 0 || j == 63) && map[i][j] == 0) {
                    posibleWayPoints.add((i << 6) | j);
                }
            }
        }
        TreeSet<WayPoint> WayPoints = new TreeSet<>();
        while (!posibleWayPoints.isEmpty()) {
            Integer integer = posibleWayPoints.first();
            int beginx = (integer >> 6) & 0x3f, beginy = integer & 0x3f;
            TreeSet<Integer> thisDepth = new TreeSet<>();
            TreeSet<Integer> nextDepth = new TreeSet<>();
            thisDepth.add((beginx << 6) | beginy);
            int[][] BFSmap = new int[map.length][map[0].length];
            for (int[] row : BFSmap) {
                Arrays.fill(row, 0x7fffffff);
            }
            int nowx = beginx, nowy = beginy;
            while (!thisDepth.isEmpty()) {
                int depth = 0;
                for (Integer location : thisDepth) {
                    int x = (location >> 6) & 0x3f;
                    int y = location & 0x3f;
                    BFSmap[x][y] = depth;
                    if (centreArea[x][y] < centreArea[nowx][nowy]) {
                        nowx = x;
                        nowy = y;
                    }
                    if (x + 1 < 64 && map[x + 1][y] != 1 && BFSmap[x + 1][y] > depth) {
                        nextDepth.add((x + 1) << 6 | y);
                    }
                    if (x - 1 >= 0 && map[x - 1][y] != 1 && BFSmap[x - 1][y] > depth) {
                        nextDepth.add((x - 1) << 6 | y);
                    }
                    if (y + 1 < 64 && map[x][y + 1] != 1 && BFSmap[x][y + 1] > depth) {
                        nextDepth.add(x << 6 | (y + 1));
                    }
                    if (y - 1 >= 0 && map[x][y - 1] != 1 && BFSmap[x][y - 1] > depth) {
                        nextDepth.add(x << 6 | (y - 1));
                    }
                    if (x == 0 || y == 0 || x == 63 || y == 63) {
                        posibleWayPoints.remove((x << 6) | y);
                    }
                }
                thisDepth = nextDepth;
                nextDepth = new TreeSet<>();
                depth++;
            }
            WayPoints.add(new WayPoint(dimention, nowx + 64 * x, nowy + 64 * y));
            /* 此时BFS图拓展和路径点的选取已经完成 */
        }
        if (WayPoints.size() != 0) {
            return WayPoints;
        }
        return null;
    }
    public TreeSet<Character> getCharacterSet(){return characters;}
    public boolean checkIFSurrounded(byte[][] wholeMap, WayPoint wp) {
        int wpx = wp.x - x * 64 + 64, wpy = wp.y - y * 64 + 64;
        if (wholeMap[wpx + 1][wpy] == 1 && wholeMap[wpx - 1][wpy] == 1 && wholeMap[wpx][wpy - 1] == 1
                && wholeMap[wpx][wpy + 1] == 1) {
            return true;
        }
        return false;
    }

    public void buildFlowField(BlockManager blockManager) {
        int[][] centreArea = buildAroundBFS(blockManager);
        ArrayList<WayPoint> nearbyWPs = getNeighborWayPoints(blockManager);
        TreeSet<WayPoint> centreWayPoints = buildWayPoints(centreArea);
        if (centreWayPoints == null) {
            return;
        }
        byte[][] wholeMap = buildTheWholeMap(blockManager);
        TreeMap<WayPoint, int[][]> points_bfs = new TreeMap<>();
        if (centreWayPoints != null) {
            for (WayPoint wp : centreWayPoints) {
                int[][] temp = bfsTheArea(wp, wholeMap);
                points_bfs.put(wp, temp);
                for (WayPoint np : nearbyWPs) {
                    if (temp[np.x - 64 * x + 64][np.y - 64 * y + 64] < 256) {
                        wp.addNeighbor(np);
                    }
                }
            }
        }
        TreeSet<WayPoint> finalWayPoints = new TreeSet<>();
        Iterator<WayPoint> iterator = centreWayPoints.iterator();
        while (iterator.hasNext()) {
            WayPoint wp = iterator.next();
            if (wp.noNeighbors() && nearbyWPs.size() > 1) {
                iterator.remove();
                continue;
            }
            if (checkIFSurrounded(wholeMap, wp)) {
                iterator.remove();
                continue;
            }
            TreeSet<WayPoint> removingWPs = new TreeSet<>();
            boolean addornot = true;
            for (WayPoint wayPoint : finalWayPoints) {
                int ans = wayPoint.Contained(wp);
                if (ans == 1) {
                    addornot = false;
                    break;
                }
                if (ans == -1) {
                    removingWPs.add(wayPoint);
                }
            }
            if (addornot) {
                finalWayPoints.add(wp);
            }
            for (WayPoint wayPoint : removingWPs) {
                finalWayPoints.remove(wayPoint);
            }
        }
        /* 最后剩下精选的路径点，加入区块管理器中 */
        for (WayPoint wp : finalWayPoints) {
            for (WayPoint nb : wp.neighbors) {
                nb.addNeighbor(wp);
            }
        }
        blockManager.addWayPointGroup(finalWayPoints);
    }

    public int[][] bfsTheArea(WayPoint wp, byte[][] map) {
        int beginx = wp.x - 64 * x + 64, beginy = wp.y - 64 * x + 64;
        TreeSet<Integer> thisDepth = new TreeSet<>();
        TreeSet<Integer> nextDepth = new TreeSet<>();
        thisDepth.add((beginx << 10) | beginy);
        int[][] BFSmap = new int[map.length][map[0].length];
        for (int[] row : BFSmap) {
            Arrays.fill(row, 0x7fffffff);
        }
        while (!thisDepth.isEmpty()) {
            int depth = 0;
            for (Integer location : thisDepth) {
                int x = (location >> 10) & 0x3f;
                int y = location & 0x3f;
                BFSmap[x][y] = depth;
                if (x + 1 < 64 && map[x + 1][y] != 1 && BFSmap[x + 1][y] > depth) {
                    nextDepth.add((x + 1) << 10 | y);
                }
                if (x - 1 >= 0 && map[x - 1][y] != 1 && BFSmap[x - 1][y] > depth) {
                    nextDepth.add((x - 1) << 10 | y);
                }
                if (y + 1 < 64 && map[x][y + 1] != 1 && BFSmap[x][y + 1] > depth) {
                    nextDepth.add(x << 10 | (y + 1));
                }
                if (y - 1 >= 0 && map[x][y - 1] != 1 && BFSmap[x][y - 1] > depth) {
                    nextDepth.add(x << 10 | (y - 1));
                }
            }
            thisDepth = nextDepth;
            nextDepth = new TreeSet<>();
            depth++;
        }
        return BFSmap;
    }

    public byte[][] buildTheWholeMap(BlockManager blockManager) {
        Block U = blockManager.findBlock(getUpBlock()), D = blockManager.findBlock(getDownBlock()),
                L = blockManager.findBlock(getLeftBlock()), R = blockManager.findBlock(getRightBlock());
        byte[][] wholeMap = new byte[192][192];
        byte one = 1;
        for (byte[] bs : wholeMap) {
            Arrays.fill(bs, one);
        }
        if (L != null) {
            short[][] temp = L.contains;
            for (int i = 64; i < 128; i++) {
                for (int j = 0; j < 64; j++) {
                    wholeMap[i][j] = (byte) (temp[i - 64][j] >> 15 & 1);
                }
            }
        }
        if (U != null) {
            short[][] temp = U.contains;
            for (int i = 0; i < 64; i++) {
                for (int j = 64; j < 128; j++) {
                    wholeMap[i][j] = (byte) (temp[i][j - 64] >> 15 & 1);
                }
            }
        }
        if (R != null) {
            short[][] temp = R.contains;
            for (int i = 64; i < 128; i++) {
                for (int j = 128; j < 192; j++) {
                    wholeMap[i][j] = (byte) (temp[i - 64][j - 128] >> 15 & 1);
                }
            }
        }
        if (D != null) {
            short[][] temp = D.contains;
            for (int i = 128; i < 192; i++) {
                for (int j = 64; j < 128; j++) {
                    wholeMap[i][j] = (byte) (temp[i - 128][j - 64] >> 15 & 1);
                }
            }
        }
        return wholeMap;
    }

    public short[][] getContains() {
        return contains;
    }

    public Block getUpBlock() {
        return new Block(x - 1, y, dimention);
    }

    public Block getDownBlock() {
        return new Block(x + 1, y, dimention);
    }

    public Block getLeftBlock() {
        return new Block(x, y - 1, dimention);
    }

    public Block getRightBlock() {
        return new Block(x, y + 1, dimention);
    }

    public int getDimention() {
        return dimention;
    }

    public int getLX() {
        return x * 64;
    }

    public int getRX() {
        return x * 64 + 64;
    }

    public int getUY() {
        return y * 64;
    }

    public int getDY() {
        return y * 64 + 64;
    }

    private void validateDimention() {
        if (dimention < 0 || dimention >= Location.dimentionChecker.length) {
            throw new IllegalArgumentException(
                    "无效的维度索引: " + dimention + "（有效范围: 0-" + (Location.dimentionChecker.length - 1) + "）");
        }
    }

    @Override
    public int compareTo(Block other) {
        if (dimention != other.dimention) {
            return Integer.compare(dimention, other.dimention);
        } else {
            if (x != other.x) {
                return Integer.compare(x, other.x);
            }
            return Integer.compare(y, other.y);
        }
    }

    @Override
    public boolean equals(Object o) {// 维度与坐标整数位相同即视为位置相同
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Block other = (Block) o;
        return dimention == other.dimention &&
                other.x == x &&
                other.y == y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimention, x, y);
    }

    @Override
    public String toString() {
        validateDimention(); // 校验维度索引
        return "区块位于 " + Location.dimentionChecker[dimention] + " 坐标 x=" + getLX() + ", y=" + getUY()
                + " 和 坐标 x=" + (x << 6 + 64) + ", y=" + (y << 6 + 64) + " 之间";
    }
}
