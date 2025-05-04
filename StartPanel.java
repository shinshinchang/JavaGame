import java.awt.*;
import javax.swing.*;

public class StartPanel extends JPanel {
    public StartPanel(MainPanel mainPanel) {
        setLayout(null);
        setBackground(Color.BLACK);

        // 設置JLabel的屬性
        JLabel title = new JLabel("Tetris Game");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setBounds(100, 150, 300, 50);
        add(title);

        // 設置JBottom的屬性
        JButton startButton = new JButton("start");
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setBounds(160, 300, 150, 50);
        add(startButton);

        startButton.addActionListener(e -> mainPanel.showGame());
    }
}