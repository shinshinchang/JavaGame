import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import javax.swing.*; 

public class GamePanel extends JPanel implements KeyListener {

    private final int ROWS = 20, COLS = 10, BLOCK_SIZE = 30;
    private Color[][] board = new Color[ROWS][COLS];
    private Tetromino current, next;
    private Timer timer;
    private boolean gameOver = false;
    private int score = 0;
    private int delay = 500;
    private MainPanel mainPanel;
    private JButton quitButton;

    // 初始化遊戲
    public GamePanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.BLACK);

        // 設置退出按鈕
        quitButton = new JButton("Quit");
        quitButton.setBounds(330, 550, 100, 30);
        add(quitButton);
        quitButton.addActionListener(e -> mainPanel.showStart());  // 點擊退出按鈕跳回開始畫面
    }

    // 開始遊戲
    public void startGame() {
        board = new Color[ROWS][COLS];  // 清空遊戲區域
        gameOver = false;
        score = 0;
        next = new Tetromino(COLS);
        spawnNewTetromino();  // 生成新的 Tetromino
        startTimer();
    }

    // 停止遊戲
    public void stopGame() {
        if (timer != null) timer.cancel();
    }

    // 啟動計時器，每隔一段時間讓方塊下移
    private void startTimer() {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (!gameOver) moveDown();
            }
        }, 0, delay);  // 設定延遲
    }

    // 繪製遊戲區域與方塊
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(0.5f));

        // 每個方格
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] != null) {
                    g.setColor(board[r][c]);
                    g.fillRect(c * BLOCK_SIZE, r * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
                g.setColor(Color.DARK_GRAY);
                g.drawRect(c * BLOCK_SIZE, r * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
            }
        }

        // 當前方塊
        if (current != null) current.draw(g);

        // 分數
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("SCORE:", COLS * BLOCK_SIZE + 20, 50);
        g.drawString(String.valueOf(score), COLS * BLOCK_SIZE + 20, 80);

        // 下一個方塊
        g.drawString("NEXT:", COLS * BLOCK_SIZE + 20, 130);
        if (next != null) {
            for (Point p : next.shape) {
                int x = COLS * BLOCK_SIZE + 30 + p.y * 20;
                int y = 150 + p.x * 20;
                g.setColor(next.color);
                g.fillRect(x, y, 20, 20);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, 20, 20);
            }
        }
    }

    // 生成新的 Tetromino
    private void spawnNewTetromino() {
        current = next;
        next = new Tetromino(COLS);  // 生成新的下一個方塊
        if (!isValid(current.shape, current.row, current.col)) {  // 如果方塊無法放置，遊戲結束
            gameOver = true;
            timer.cancel();
            repaint();
            JOptionPane.showMessageDialog(this, "Game Over!\nFinal Score: " + score);
        }
    }

    // 方塊下移
    private void moveDown() {
        if (isValid(current.shape, current.row + 1, current.col)) {
            current.row++;
        } else {
            mergeToBoard();
            clearLines();
            spawnNewTetromino();  // 生成新的方塊
        }
        repaint();
    }

    // 合併當前方塊到遊戲區域
    private void mergeToBoard() {
        for (Point p : current.shape) {
            int r = current.row + p.x, c = current.col + p.y;
            board[r][c] = current.color;
        }
    }

    // 清除已滿的行
    private void clearLines() {
        int linesCleared = 0;
        for (int r = ROWS - 1; r >= 0; r--) {
            boolean full = true;
            for (int c = 0; c < COLS; c++) {
                if (board[r][c] == null) full = false;
            }
            if (full) {
                linesCleared++;
                for (int rr = r; rr > 0; rr--) {
                    board[rr] = Arrays.copyOf(board[rr - 1], COLS);  // 向下移動上面的行
                }
                board[0] = new Color[COLS];  // 清空最上面一行
                r++;
            }
        }
        // 根據清除的行數來加分(俄羅斯方塊規則)
        switch (linesCleared) {
            case 1 -> score += 100;
            case 2 -> score += 300;
            case 3 -> score += 500;
            case 4 -> score += 800;
        }
    }

    // 判斷方塊是否可以放置
    private boolean isValid(Point[] shape, int r, int c) {
        for (Point p : shape) {
            int nr = r + p.x, nc = c + p.y;
            if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS || board[nr][nc] != null) {
                return false;  // 如果超出邊界或有其他方塊，false
            }
        }
        return true;
    }

    // 旋轉方塊
    private void rotate() {
        Point[] newShape = new Point[4];
        for (int i = 0; i < 4; i++) {
            newShape[i] = new Point(current.shape[i].y, -current.shape[i].x);  // 計算旋轉後的形狀
        }
        if (isValid(newShape, current.row, current.col)) current.shape = newShape;  // 如果有效，就更新形狀
    }

    // 處理按鍵事件
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> {
                if (isValid(current.shape, current.row, current.col - 1)) current.col--;  // 左移
            }
            case KeyEvent.VK_D -> {
                if (isValid(current.shape, current.row, current.col + 1)) current.col++;  // 右移
            }
            case KeyEvent.VK_S -> moveDown();  // 下移
            case KeyEvent.VK_W -> rotate();  // 旋轉
        }
        repaint();
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}