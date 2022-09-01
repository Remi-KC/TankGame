package tw.com.herpestesurva.gameobject;

import java.awt.*;
import java.util.Random;

import tw.com.herpestesurva.TankGame;

public class Tank extends GameObject {
    protected int speed;
    protected Direction direction;
    private boolean[] dirs;
    private boolean enemy;
    protected boolean iscollision;
    protected int hp;

    public Tank(Image[] image, int x, int y, Direction direction, boolean enemy) {
        super(image, x, y);
        this.direction = direction;
        this.enemy = enemy;
        speed = 5;
        dirs = new boolean[4];
        hp = 2;

    }

    public int getHP() {
        return hp;
    }

    public boolean isEnemy() {
        return enemy;
    }

    public void setDirs(boolean[] dirs) {
        this.dirs = dirs;
    }

    public boolean getEnemy() {
        return this.enemy;
    }

    public void setEnemy(boolean enemy) {
        this.enemy = enemy;
    }

    // 傳址呼叫
    public boolean[] getDirs() {
        return dirs;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;

    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    // 為了避免上下或者左右一起按
    // 上下左右（0,1,2,3）
    private void determineDirection() {
        // 上左
        if (dirs[0] && dirs[2] && !dirs[1] && !dirs[3]) {
            direction = Direction.UP_LEFT;
        }
        // 上右
        else if (dirs[0] && dirs[3] && !dirs[1] && !dirs[2]) {
            direction = Direction.UP_RIGHT;
        }
        // 下左
        else if (dirs[1] && dirs[2] && !dirs[0] && !dirs[3]) {
            direction = Direction.DOWN_LEFT;
        }
        // 下右
        else if (dirs[1] && dirs[3] && !dirs[0] && !dirs[2]) {
            direction = Direction.DOWN_RIGHT;
        }
        // 上
        else if (dirs[0] && !dirs[1] && !dirs[2] && !dirs[3]) {
            direction = Direction.UP;
        }
        // 下
        else if (dirs[1] && !dirs[0] && !dirs[2] && !dirs[3]) {
            direction = Direction.DOWN;
        }
        // 左
        else if (dirs[2] && !dirs[0] && !dirs[1] && !dirs[3]) {
            direction = Direction.LEFT;
        }
        // 右
        else if (dirs[3] && !dirs[0] && !dirs[2] && !dirs[1]) {
            direction = Direction.RIGHT;
        }
    }

    // 移動方法
    public void move() {
        oldX = x;
        oldY = y;
        switch (direction) {
            case UP:
                y -= speed;
                break;
            case DOWN:
                y += speed;
                break;
            case LEFT:
                x -= speed;
                break;
            case RIGHT:
                x += speed;
                break;
            case UP_LEFT:
                x -= speed;
                y -= speed;
                break;
            case UP_RIGHT:
                x += speed;
                y -= speed;
                break;
            case DOWN_RIGHT:
                x += speed;
                y += speed;
                break;
            case DOWN_LEFT:
                x -= speed;
                y += speed;
                break;
        }

        // System.out.println(getRectangle().getX());
        // System.out.println(getRectangle().getY());
    }

    @Override
    public Rectangle getRectangle() {
        int padding = 8;
        return new Rectangle(x + padding, y + padding, width - padding, height - padding);
    }

    public void getHurt(int damage) {
        if (--hp <= 0) {
            hp = 0;
            alive = false;
        }
    }

    // 是否停止
    public boolean isStop() {
        for (boolean dir : dirs) {
            if (dir) {
                return false;
            }
        }
        return true;
    }

    public boolean iscollisionBound() {
        // 邊界偵測
        if (x < 0) {
            x = 0;
            return true;
        }
        if (x > TankGame.gameClient.getScreenWidth() - width) {
            x = TankGame.gameClient.getScreenWidth() - width;
            return true;
        }
        if (y < 0) {
            y = 0;
            return true;
        }
        if (y > TankGame.gameClient.getScreenHeight() - height) {
            y = TankGame.gameClient.getScreenHeight() - height;
            return true;
        }
        return false;

    }

    // 偵測邊界/敵方/牆面/子彈
    public void collision() {
        if (iscollisionBound()) {
            iscollision = true;
            return;
        }

        // 偵測其他物件(使用多型)
        for (GameObject object : TankGame.gameClient.getGameObjects()) {
            if (object == this) {
                continue;
            }
            if (object instanceof Tank) {
                // 向下轉型
                if (((Tank) object).enemy == enemy) {
                    continue;
                }
            }
            // 實際偵測碰撞
            if (getRectangle().intersects(object.getRectangle())) {
                x = oldX;
                y = oldY;
                // System.out.println("hit!");
                iscollision = true;
                return;
            }
        }
    }

    // 發射子彈
    public void fire() {
        if (!alive) {
            return;
        }
        Bullet bullet = new Bullet(TankGame.gameClient.getbulletImage(), 0, 0, direction, enemy);
        int[] pos = getCenterPos(bullet.getRectangle());
        bullet.setX(pos[0]);
        bullet.setY(pos[1]);

        TankGame.gameClient.getGameObjects().add(bullet);

    }

    public void draw(Graphics g) {
        if (!alive) {
            return;
        }
        ai();
        if (!isStop()) {
            determineDirection();
            move();
            collision();
        }
        g.drawImage(image[direction.ordinal()], x, y, null);

    }

    @Override
    public void ai() {
        if (!enemy) {
            return;
        }
        // 是否碰撞
        if (iscollision) {
            getNewDirection();
            iscollision = false;
            return;
        }
        // 進行亂數移動
        Random rand = new Random();
        // 移動
        if (rand.nextInt(50) == 1) {
            getNewDirection();
        }
        if (rand.nextInt(10) == 1) {
            fire();
        }
    }

    public void getNewDirection() {
        dirs = new boolean[4];

        Random rand = new Random();
        int dir = rand.nextInt(Direction.values().length);

        if (dir <= Direction.RIGHT.ordinal()) {
            dirs[dir] = true;
        } else {
            if (dir == Direction.UP_LEFT.ordinal()) {
                dirs[0] = true;
                dirs[2] = true;
            } else if (dir == Direction.UP_RIGHT.ordinal()) {
                dirs[0] = true;
                dirs[3] = true;
            } else if (dir == Direction.DOWN_LEFT.ordinal()) {
                dirs[1] = true;
                dirs[2] = true;
            } else if (dir == Direction.DOWN_RIGHT.ordinal()) {
                dirs[1] = true;
                dirs[3] = true;
            }
        }
    }

}
