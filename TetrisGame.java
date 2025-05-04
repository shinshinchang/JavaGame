import javax.swing.*;

public class TetrisGame {
    public static void main(String[] args) {
        // 使用 SwingUtilities.invokeLater 確保 GUI 的建立與更新在事件派發執行緒 (EDT) 上執行
        SwingUtilities.invokeLater(() -> {
            // 建立主面板視窗
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