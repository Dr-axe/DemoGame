package Location;
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import Character.Character;

public class DraggableMap extends JPanel {
    private static final int TILE_SIZE = 16; // 每个地块的像素大小
    private static final int CHUNK_SIZE = 64; // 每个区块包含的地块数量

    private static long duration=0;
    private static int drawTimes=0;

    private BlockManager blockManager=new BlockManager();
    private int dimention=0;
    // 视口偏移量（像素单位）
    private int offsetX = 0;
    private int offsetY = 0;
    private double scaleFactor = 1.0;  

    // 区块缓存（区块坐标 -> 地块数据）
    private final Map<Point, short[][]> chunkCache = new HashMap<>();
    private ArrayList<WayPoint> wayPoints = new ArrayList<>();
    private ArrayList<Character> characters=new ArrayList<>();
    
    // 鼠标拖动相关变量
    private int lastDragX;
    private int lastDragY;

    public DraggableMap() {
        setPreferredSize(new Dimension(1080, 800));
        setupMouseListeners();
        initializeTestData();
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragX = e.getX();
                lastDragY = e.getY();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastDragX;
                int dy = e.getY() - lastDragY;
                offsetX -= dx;
                offsetY -= dy;
                lastDragX = e.getX();
                lastDragY = e.getY();
                repaint();
            }
        });

        addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation();
            double zoomFactor = rotation > 0 ? 0.9 : 1.1; // 缩放系数
            Point mousePoint = e.getPoint(); // 鼠标当前位置
            
            // 保存旧的世界坐标
            double oldWorldX = (mousePoint.x + offsetX) / (TILE_SIZE * scaleFactor);
            double oldWorldY = (mousePoint.y + offsetY) / (TILE_SIZE * scaleFactor);
            
            
            // 应用新缩放因子并限制范围
            scaleFactor = Math.max(0.02, Math.min(10.0, scaleFactor * zoomFactor));
            // System.out.printf("%.2f\n",scaleFactor);
            // 计算新的偏移量保持鼠标位置不变
            offsetX = (int) (oldWorldX * TILE_SIZE * scaleFactor - mousePoint.x);
            offsetY = (int) (oldWorldY * TILE_SIZE * scaleFactor - mousePoint.y);
            
            repaint();
        });
    }

    private void initializeTestData() {
        // 添加测试数据
        Character BEEP=new Character("哔噗", 1, new Location(0, 0, -40));
        // blockManager.addWayPoint(null);
    }
    public void changeWayPoints(double minWorldX,double maxWorldX,double minWorldY,double maxWorldY){
        wayPoints=blockManager.getWaypointsBetween(dimention,(int)minWorldX,(int)maxWorldX,(int)minWorldY,(int)maxWorldY);
        if (wayPoints==null) {
            wayPoints=new ArrayList<>();
            wayPoints.add(new WayPoint(dimention, (int)(minWorldX+minWorldX)/2, (int)(minWorldY+maxWorldY)/2));   
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 计算可见区域的世界坐标范围（考虑缩放）
        int width = getWidth();
        int height = getHeight();
        double minWorldX = (double) offsetX / (TILE_SIZE * scaleFactor); //▲
        double maxWorldX = (double) (offsetX + width) / (TILE_SIZE * scaleFactor); //▲
        double minWorldY = (double) offsetY / (TILE_SIZE * scaleFactor); //▲
        double maxWorldY = (double) (offsetY + height) / (TILE_SIZE * scaleFactor); //▲

        // System.out.println("显示区域左上角对应地图坐标"+minWorldX+" "+minWorldY+" ");
        // System.out.println("显示区域右下角的对应坐标"+maxWorldX+" "+maxWorldY);
        initializeTestData();
        changeWayPoints(minWorldY, maxWorldY, minWorldX, maxWorldX);
        // 计算需要渲染的区块范围
        int startX = (int) Math.floor(minWorldX / CHUNK_SIZE);
        int endX = (int) Math.ceil(maxWorldX / CHUNK_SIZE);
        int startY = (int) Math.floor(minWorldY / CHUNK_SIZE);
        int endY = (int) Math.ceil(maxWorldY / CHUNK_SIZE);

        // 绘制所有可见区块
        for (int chunkX = startX; chunkX <= endX; chunkX++) {
            for (int chunkY = startY; chunkY <= endY; chunkY++) {
                renderChunk(g, chunkX, chunkY);
            }
        }
        // 绘制路径点
        drawWayPoints(g);
        drawCharacters(g);
    }


    private void renderChunk(Graphics g, int chunkX, int chunkY) {
        // long startTime = System.nanoTime();
        Point key = new Point(chunkX, chunkY);
        short[][] chunk = chunkCache.computeIfAbsent(key, k -> blockManager.getBlockInfo(dimention,chunkY,chunkX));
        int chunkWorldStartX = chunkX * CHUNK_SIZE;
        int chunkWorldStartY = chunkY * CHUNK_SIZE;
        for (int i = 0; i < CHUNK_SIZE; i++) {
            for (int j = 0; j < CHUNK_SIZE; j++) {
                int worldX = chunkWorldStartX + i;
                int worldY = chunkWorldStartY + j;
                
                // 计算屏幕坐标（考虑缩放）
                int screenX = (int)(worldX * TILE_SIZE * scaleFactor) - offsetX; //▲
                int screenY = (int)(worldY * TILE_SIZE * scaleFactor) - offsetY; //▲
                
                // 计算实际绘制尺寸（防止间隙）
                int tileSize = (int) Math.ceil(TILE_SIZE * scaleFactor); //▲

                // 只绘制可见区域
                if (screenX + tileSize < 0 || screenX > getWidth() ||
                    screenY + tileSize < 0 || screenY > getHeight()) {
                    continue;
                }

                // 绘制地块
                Color theColor;
                switch (chunk[j][i]) {
                    case 0:
                        theColor=Color.GREEN;
                        break;
                    case 1:
                        theColor=Color.BLACK;
                        break;
                    case 2:theColor=new Color(255,255,0);break;
                    case (short)0xe001:theColor = Color.GRAY;break;
                    case (short)0xc003:theColor =Color.BLUE;break;
                    case (short)0x0004:theColor =new Color(180,146,28);break;
                    case (short)0x4005:theColor =new Color(130,102,24);break;
                    case (short)0xe006:theColor =new Color(100,80,20);break;
                    case (short)0xe007:theColor =new Color(75,60,15);break;
                    // case (short)0x8008:theColor =new Color(50,150,70);break;
                    default:theColor=Color.BLACK;
                        break;
                }
                switch (chunk[j][i]&0xf) {
                    case 8:
                        theColor=Color.RED;
                        break;
                
                    default:
                        break;
                }
                g.setColor(theColor);
                g.fillRect(screenX, screenY, tileSize, tileSize); //▲
            }
        }
        // long endTime = System.nanoTime();
        // duration += endTime - startTime;
        // drawTimes++;
        // if ((drawTimes&0x3f)==0) {
        //     System.out.println("区块绘制累计消耗时间："+duration / 1_000_000.0 + " 毫秒");
        // }
    }
    private void drawCharacters(Graphics g){
        g.setColor(Color.BLACK);
        if (!characters.isEmpty()) {
            for (Character character : characters) {
                System.out.println("检查点2");
                Location location=character.getLocation();
                double x=location.getX(),y=location.getY();
                int screenX = (int)((y + 0.5) * TILE_SIZE * scaleFactor - offsetX); //▲
                int screenY = (int)((x + 0.5) * TILE_SIZE * scaleFactor - offsetY); //▲
                g.fillOval(screenX - 3, screenY - 3, 8, 8);
            }
        }
    }
    private void drawWayPoints(Graphics g) {
        g.setColor(Color.RED);
        if (!wayPoints.isEmpty()) {
            for (WayPoint wp : wayPoints) {
                // 计算屏幕坐标（考虑缩放）
                int screenX = (int)((wp.y + 0.5) * TILE_SIZE * scaleFactor - offsetX); //▲
                int screenY = (int)((wp.x + 0.5) * TILE_SIZE * scaleFactor - offsetY); //▲
                // System.out.println(wp.x+" "+wp.y);
                // System.out.println(screenX+" "+screenY);
                g.fillOval(screenX - 3, screenY - 3, 8, 8);
            }
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("可拖动地图");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new DraggableMap());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}