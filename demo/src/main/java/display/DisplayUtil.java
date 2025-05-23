package display;

import javax.swing.*;
import java.awt.*;

public class DisplayUtil {
    private static JFrame frame;
    private static GridPanel panel;

    public static void display(short[][] map, double x, double y) {
        SwingUtilities.invokeLater(() -> {
            if (frame == null) { // 单例窗口
                frame = new JFrame("动态路径显示");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                panel = new GridPanel();
                frame.add(panel);
                frame.pack();
                frame.setSize(map[0].length * 16 + 20, map.length * 16 + 42);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
            panel.updateData(map, x, y); // 更新数据而非新建面板
        });
    }

    private static class GridPanel extends JPanel {
        private short[][] currentMap;
        private double currentX;
        private double currentY;

        public void updateData(short[][] map, double x, double y) {
            this.currentMap = map;
            this.currentX = x;
            this.currentY = y;
            setPreferredSize(new Dimension( // 动态调整尺寸
                    (map.length > 0 ? map[0].length : 0) * 16,
                    map.length * 16));
            revalidate();
            repaint(); // 触发重绘而非新建窗口
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentMap == null || currentMap.length == 0)
                return;

            // 绘制网格（优化性能）
            for (int i = 0; i < currentMap.length; i++) {
                for (int j = 0; j < currentMap[i].length; j++) {
                    drawGridCell(g, i, j);
                }
            }

            // 绘制动态小人
            drawCharacter(g);
        }

        private Color getColor(int value) {
            switch (value) {
                case 0:
                    return Color.WHITE;
                case 1:
                    return Color.BLACK;
                case 9:
                    return Color.GREEN;
                default:
                    return Color.WHITE;
            }
        }

        private void drawGridCell(Graphics g, int i, int j) {
            Color color = getColor(currentMap[i][j]);
            g.setColor(color);
            g.fillRect(j * 16, i * 16, 16, 16);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(j * 16, i * 16, 16, 16);
        }

        private void drawCharacter(Graphics g) {
            int px = (int) ((currentY + 0.5) * 16 - 10);
            int py = (int) ((currentX + 1) * 16 - 20);
            g.setColor(Color.RED);
            g.fillOval(px, py, 5, 5); // 调整到正确尺寸
            g.setColor(Color.BLACK);
            g.drawOval(px, py, 5, 5);
        }
    }

    public static void main(String[] args) {
        short[][] map = {
                { 9, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 },
                { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 9 }
        };
        display(map, 0.1, 0); // 在(a, b)坐标显示红色方块
    }
}