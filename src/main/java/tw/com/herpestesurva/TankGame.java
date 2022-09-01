package tw.com.herpestesurva;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public final class TankGame extends JFrame {
    // 靜態唯一
    public static GameClient gameClient;

    private TankGame() {
        setTitle("坦克大戰");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gameClient = new GameClient();
        add(gameClient);

        pack();//
        setVisible(true);

        // 遊戲區域重新繪製
        gameClient.run();

        // 按鍵偵測
        addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                gameClient.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                gameClient.keyReleased(e);

            }

        });
    }

    public static void main(String[] args) {
        new TankGame();
    }
}
