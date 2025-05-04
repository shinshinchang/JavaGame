import java.awt.*;
import javax.swing.*;

public class MainPanel extends JPanel {
    private CardLayout cardLayout;
    private StartPanel startPanel;
    private GamePanel gamePanel;

    public MainPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        // 宣告開始畫面與遊戲畫面
        startPanel = new StartPanel(this);
        gamePanel = new GamePanel(this);

        add(startPanel, "Start");
        add(gamePanel, "Game");

        cardLayout.show(this, "Start");
    }

    // 按start顯示遊戲面板的方法
    public void showGame() {
        gamePanel.startGame();
        cardLayout.show(this, "Game");
        gamePanel.requestFocusInWindow();
    }

    // 按quit顯示開始面板的方法
    public void showStart() {
        gamePanel.stopGame();
        cardLayout.show(this, "Start");
    }
}