package Location;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DraggableMap extends JPanel {
    private static final int TILE_SIZE = 10; // 每个地块的像素大小
    private static final int CHUNK_SIZE = 64; // 每个区块包含的地块数量

    private BlockManager blockManager=new BlockManager();
    private int dimention=0;
    // 视口偏移量（像素单位）
    private int offsetX = 0;
    private int offsetY = 0;
    private double scaleFactor = 1.0;  

    // 区块缓存（区块坐标 -> 地块数据）
    private final Map<Point, short[][]> chunkCache = new HashMap<>();
    private ArrayList<WayPoint> wayPoints = new ArrayList<>();
    
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
            scaleFactor = Math.max(0.5, Math.min(3.0, scaleFactor * zoomFactor));
            
            // 计算新的偏移量保持鼠标位置不变
            offsetX = (int) (oldWorldX * TILE_SIZE * scaleFactor - mousePoint.x);
            offsetY = (int) (oldWorldY * TILE_SIZE * scaleFactor - mousePoint.y);
            
            repaint();
        });
    }

    private void initializeTestData() {
        // 添加测试路径点
        wayPoints.add(new WayPoint(0,32, 32));
        wayPoints.add(new WayPoint(0,-31, -31));
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

        changeWayPoints(minWorldX, maxWorldX, minWorldY, maxWorldY);
        // initializeTestData();

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
    }


    private void renderChunk(Graphics g, int chunkX, int chunkY) {
        Point key = new Point(chunkX, chunkY);
        short[][] chunk = chunkCache.computeIfAbsent(key, k -> blockManager.generateBlock(dimention,chunkX,chunkY));

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
                switch (chunk[i][j]) {
                    case 0:
                        theColor=Color.WHITE;
                        break;
                    case 1:
                        theColor=Color.BLACK;
                        break;
                    case 2:theColor=Color.ORANGE;break;
                    case (short)0x8001:theColor = Color.BLACK;break;
                    default:theColor=Color.BLUE;
                        break;
                }
                g.setColor(theColor);
                g.fillRect(screenX, screenY, tileSize, tileSize); //▲
            }
        }
    }
    private void drawWayPoints(Graphics g) {
        g.setColor(Color.RED);
        if (!wayPoints.isEmpty()) {
            for (WayPoint wp : wayPoints) {
                // 计算屏幕坐标（考虑缩放）
                int screenX = (int)((wp.x + 0.5) * TILE_SIZE * scaleFactor - offsetX); //▲
                int screenY = (int)((wp.y + 0.5) * TILE_SIZE * scaleFactor - offsetY); //▲
                g.fillOval(screenX - 3, screenY - 3, 6, 6);
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