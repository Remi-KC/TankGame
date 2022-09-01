package tw.com.herpestesurva.gameobject;

import java.awt.*;

import tw.com.herpestesurva.TankGame;

public class Bullet extends Tank {

    private int damage;

    public Bullet(Image[] image, int x, int y, Direction direction, boolean enemy) {
        super(image, x, y, direction, enemy);
        setSpeed(6);
        bulletRun();

    }

    void bulletRun() {
        new Thread(() -> {
            while (alive) {
                try {
                    Thread.sleep(25);
                    if (++speed >= 50) {
                        speed = 50;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void draw(Graphics g) {
        if (!alive) {
            return;
        }
        move();
        collision();
        g.drawImage(image[direction.ordinal()], x, y, null);
    }

    @Override
    public void collision() {
        // 邊界偵測
        if (iscollisionBound()) {
            alive = false;
            return;
        }
        // 跟其他物件偵測
        // 偵測其他物件(使用多型)
        for (GameObject object : TankGame.gameClient.getGameObjects()) {
            if (object == this) {
                continue;
            }
            if (object instanceof Tank) {
                // 向下轉型
                if (((Tank) object).isEnemy() == getEnemy()) {
                    continue;
                }
                // 子彈攻擊敵方
                if (getRectangle().intersects(object.getRectangle())) {
                    TankGame.gameClient.getGameObjects().add(new Explosion(
                            TankGame.gameClient.getExplosionImg(), object.getX(), object.getY()));

                    ((Tank) object).getHurt(damage);
                    alive = false;// 子彈消失
                    continue;
                }
            }

            // 實際偵測碰撞
            if (getRectangle().intersects(object.getRectangle())) {
                TankGame.gameClient.getGameObjects().add(new Explosion(
                        TankGame.gameClient.getExplosionImg(), object.getX(), object.getY()));
                alive = false;
                return;
            }
        }
    }

}
