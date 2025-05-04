import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import javax.swing.*;

public class TetrisGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tetris");
            MainPanel mainPanel = new MainPanel();
            frame.add(mainPanel);
            frame.setSize(480, 630);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}

class MainPanel extends JPanel {
    private CardLayout cardLayout;
    private StartPanel startPanel;
    private GamePanel gamePanel;

    public MainPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        startPanel = new StartPanel(this);
        gamePanel = new GamePanel(this);

        add(startPanel, "Start");
        add(gamePanel, "Game");

        cardLayout.show(this, "Start");
    }

    public void showGame() {
        gamePanel.startGame();
        cardLayout.show(this, "Game");
        gamePanel.requestFocusInWindow();
    }

    public void showStart() {
        gamePanel.stopGame();
        cardLayout.show(this, "Start");
    }
}

class StartPanel extends JPanel {
    public StartPanel(MainPanel mainPanel) {
        setLayout(null);
        setBackground(Color.BLACK);

        JLabel title = new JLabel("Tetris Game");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setBounds(100, 150, 300, 50);
        add(title);

        JButton startButton = new JButton("start");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setBounds(160, 300, 150, 50);
        add(startButton);

        startButton.addActionListener(e -> mainPanel.showGame());
    }
}

class GamePanel extends JPanel implements KeyListener {
    private final int ROWS = 20, COLS = 10, BLOCK_SIZE = 30;
    private Color[][] board = new Color[ROWS][COLS];
    private Tetromino current, next;
    private Timer timer;
    private boolean gameOver = false;
    private int score = 0;
    private int delay = 500;

    private MainPanel mainPanel;
    private JButton quitButton;

    public GamePanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        setLayout(null);
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.BLACK);

        quitButton = new JButton("Quit");
        quitButton.setBounds(330, 550, 100, 30);
        add(quitButton);
        quitButton.addActionListener(e -> mainPanel.showStart());
    }

    public void startGame() {
        board = new Color[ROWS][COLS];
        gameOver = false;
        score = 0;
        next = new Tetromino(COLS);
        spawnNewTetromino();
        startTimer();
    }

    public void stopGame() {
        if (timer != null) timer.cancel();
    }

    private void startTimer() {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (!gameOver) moveDown();
            }
        }, 0, delay);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(0.5f));

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
        if (current != null) current.draw(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("SCORE:", COLS * BLOCK_SIZE + 20, 50);
        g.drawString(String.valueOf(score), COLS * BLOCK_SIZE + 20, 80);

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

    private void spawnNewTetromino() {
        current = next;
        next = new Tetromino(COLS);
        if (!isValid(current.shape, current.row, current.col)) {
            gameOver = true;
            timer.cancel();
            repaint();
            JOptionPane.showMessageDialog(this, "Game Over!\nFinal Score: " + score);
        }
    }

    private void moveDown() {
        if (isValid(current.shape, current.row + 1, current.col)) {
            current.row++;
        } else {
            mergeToBoard();
            clearLines();
            spawnNewTetromino();
        }
        repaint();
    }

    private void mergeToBoard() {
        for (Point p : current.shape) {
            int r = current.row + p.x, c = current.col + p.y;
            board[r][c] = current.color;
        }
    }

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
                    board[rr] = Arrays.copyOf(board[rr - 1], COLS);
                }
                board[0] = new Color[COLS];
                r++;
            }
        }
        switch (linesCleared) {
            case 1 -> score += 100;
            case 2 -> score += 300;
            case 3 -> score += 500;
            case 4 -> score += 800;
        }
    }

    private boolean isValid(Point[] shape, int r, int c) {
        for (Point p : shape) {
            int nr = r + p.x, nc = c + p.y;
            if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS || board[nr][nc] != null) {
                return false;
            }
        }
        return true;
    }

    private void rotate() {
        Point[] newShape = new Point[4];
        for (int i = 0; i < 4; i++) {
            newShape[i] = new Point(current.shape[i].y, -current.shape[i].x);
        }
        if (isValid(newShape, current.row, current.col)) current.shape = newShape;
    }

    public void keyPressed(KeyEvent e) {
        if (gameOver) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> {
                if (isValid(current.shape, current.row, current.col - 1)) current.col--;
            }
            case KeyEvent.VK_D -> {
                if (isValid(current.shape, current.row, current.col + 1)) current.col++;
            }
            case KeyEvent.VK_S -> moveDown();
            case KeyEvent.VK_W -> rotate();
        }
        repaint();
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}

class Tetromino {
    public Point[] shape;
    public Color color;
    public int row, col;
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

    public Tetromino(int cols) {
        int i = new Random().nextInt(SHAPES.length);
        shape = Arrays.copyOf(SHAPES[i], 4);
        color = COLORS[i];
        row = 0;
        col = cols / 2 - 1;
    }

    public void draw(Graphics g) {
        g.setColor(color);
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
