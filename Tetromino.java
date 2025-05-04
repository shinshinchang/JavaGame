import java.awt.*;
import java.util.*;

public class Tetromino {
    // 儲存方塊的形狀、顏色、位置
    public Point[] shape;
    public Color color;
    public int row, col;

    // 定義所有可能的 Tetromino 形狀，這些形狀使用 Point 陣列表示
    // 每一個形狀包含 4 個 Point，這些 Point 相對於其旋轉中心的偏移量
    private static final Point[][] SHAPES = {
        { new Point(0, -1), new Point(0, 0), new Point(0, 1), new Point(0, 2) },
        { new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1) },
        { new Point(0, 0), new Point(0, -1), new Point(0, 1), new Point(1, 0) },
        { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
        { new Point(0, 1), new Point(1, 1), new Point(1, 0), new Point(2, 0) },
        { new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(2, 1) },
        { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0) }
    };

    private static final Color[] COLORS = {
        Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.GREEN,
        Color.RED, Color.BLUE, Color.ORANGE
    };

    // 隨機生成一個 Tetromino，並設置其位置與顏色
    public Tetromino(int cols) {
        int i = new Random().nextInt(SHAPES.length);
        shape = Arrays.copyOf(SHAPES[i], 4);
        color = COLORS[i];
        row = 0;
        col = cols / 2 - 1;
    }

    // 畫出 Tetromino 的形狀在指定的圖形物件上
    public void draw(Graphics g) {
        g.setColor(color);
        // 遍歷形狀中的每一個 Point，根據它們的偏移量計算顯示位置
        for (Point p : shape) {
            int x = (col + p.y) * 30;
            int y = (row + p.x) * 30;
            g.fillRect(x, y, 30, 30);

            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, y, 30, 30);
            g.setColor(color);
        }
    }
}