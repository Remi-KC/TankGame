package tw.com.herpestesurva;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import tw.com.herpestesurva.gameobject.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameClient extends JComponent {
    private int screenWidth;
    private int screenHeight;

    private PlayerTank playerTank;

    private CopyOnWriteArrayList<GameObject> gameObjects = new CopyOnWriteArrayList<GameObject>();

    private Image[] bulletImage;
    private Image[] wallImg;
    private Image[] iTankImg;
    private Image[] eTankImg;
    private Image[] explosionImg;

    GameClient() {
        this(800, 600);
    }

    public GameClient(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));

        init();

    }

    public Image[] getbulletImage() {
        return bulletImage;
    }

    public Image[] getExplosionImg() {
        return explosionImg;
    }

    // 回傳所有遊戲物件容器
    public CopyOnWriteArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    // 啟動遊戲執行緒
    public void run() {
        new Thread(() -> { // 多執行緒
            while (true) {
                repaint();
                try {
                    Thread.sleep(25);// 25-50為合理範圍
                    System.out.println(gameObjects.size());// 現在有多少物件
                    // playerTank.getHP();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    // 重置遊戲
    void initGame() {

        // 釋放資源
        for (GameObject object : gameObjects) {
            gameObjects.remove(object);
        }

        // 玩家物件
        playerTank = new PlayerTank(iTankImg, 380, 500, Direction.UP);
        // playerTank.setEnemy(true);
        playerTank.setSpeed(5);
        gameObjects.add(playerTank);
        // 產生敵方
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                gameObjects.add(new Tank(eTankImg, 200 + j * 100, 50 + i * (eTankImg[0].getHeight(null) + 47),
                        Direction.DOWN, true));
            }
        }

        // 牆面配置
        gameObjects.add(new Wall(wallImg, 80, 10, false, 15));
        gameObjects.add(new Wall(wallImg, 140, 10, true, 10));
        gameObjects.add(new Wall(wallImg, 640, 10, false, 15));
        // genWall(35);
        // genEnemy(12);

    }

    // 讀取圖形跟初始遊戲物件
    void init() {
        // 牆面圖形
        wallImg = new Image[] { new ImageIcon("assets/images/brick.png").getImage() };
        String[] ext = { "U", "D", "L", "R", "LU", "RU", "LD", "RD" };

        iTankImg = new Image[ext.length];
        eTankImg = new Image[ext.length];
        bulletImage = new Image[ext.length];
        explosionImg = new Image[11];

        for (int i = 0; i < explosionImg.length; i++) {
            explosionImg[i] = new ImageIcon("assets/images/" + i + ".png").getImage();
        }

        // UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
        for (int i = 0; i < ext.length; i++) {
            iTankImg[i] = new ImageIcon("assets/images/itank" + ext[i] + ".png").getImage();
            eTankImg[i] = new ImageIcon("assets/images/etank" + ext[i] + ".png").getImage();
            bulletImage[i] = new ImageIcon("assets/images/missile" + ext[i] + ".png").getImage();
        }

        initGame();
    }

    public void genEnemy(int nums) {
        Random random = new Random();

        for (int i = 0; i < nums; i++) {
            int x = random.nextInt(screenWidth - eTankImg[0].getWidth(null));
            int y = random.nextInt(screenHeight - eTankImg[0].getHeight(null));
            Tank enemyTank = new Tank(eTankImg, x, y,
                    Direction.values()[random.nextInt(Direction.values().length)], true);
            enemyTank.setSpeed(5);
            gameObjects.add(enemyTank);
        }
    }

    public void genWall(int nums) {
        Random random = new Random();

        for (int i = 0; i < nums; i++) {
            int x = random.nextInt(screenWidth - wallImg[0].getWidth(null));
            int y = random.nextInt(screenHeight - wallImg[0].getHeight(null));

            gameObjects.add(new Wall(wallImg, x, y, false, 1));
        }
    }

    public int getScreenWidth() {
        return this.screenWidth;
    }

    public int getScreenHeight() {
        return this.screenHeight;
    }

    public void keyPressed(KeyEvent e) {
        boolean[] dirs = playerTank.getDirs();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                dirs[0] = true;
                break;
            case KeyEvent.VK_DOWN:
                dirs[1] = true;
                break;
            case KeyEvent.VK_LEFT:
                dirs[2] = true;
                break;
            case KeyEvent.VK_RIGHT:
                dirs[3] = true;
                break;
            case KeyEvent.VK_CONTROL:
                playerTank.fire();
                break;
            case KeyEvent.VK_S:
                playerTank.superFire();
                break;
            case KeyEvent.VK_Z:
                initGame();
                break;
        }

    }

    public void keyReleased(KeyEvent e) {
        boolean[] dirs = playerTank.getDirs();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                dirs[0] = false;
                break;
            case KeyEvent.VK_DOWN:
                dirs[1] = false;
                break;
            case KeyEvent.VK_LEFT:
                dirs[2] = false;
                break;
            case KeyEvent.VK_RIGHT:
                dirs[3] = false;
                break;
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // 多型
        for (GameObject object : gameObjects) {
            object.draw(g);
        }
        // 釋放資源
        for (GameObject object : gameObjects) {
            if (!object.getAlive()) {
                gameObjects.remove(object);
            }
        }
        // 判斷遊戲是否結束
        checkGameState();

    }

    // 遊戲結束？
    public void checkGameState() {
        boolean gameOver = true;

        for (GameObject object : gameObjects) {
            if (object instanceof Tank) {
                if (((Tank) object).isEnemy()) {
                    gameOver = false;
                    break;
                }
            }
        }

        if (!playerTank.getAlive()) {
            gameOver = true;
        }
        if (gameOver) {
            initGame();
        }

    }

}
