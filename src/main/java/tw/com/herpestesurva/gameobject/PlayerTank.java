package tw.com.herpestesurva.gameobject;

import java.awt.*;

import tw.com.herpestesurva.TankGame;

public class PlayerTank extends Tank implements SuperFire {

    public PlayerTank(Image[] image, int x, int y, Direction direction) {
        super(image, x, y, direction, false);
        speed = 5;
        hp = 3;
    }

    @Override
    public void superFire() {
        if (!alive) {
            return;
        }
        Bullet bullet = new Bullet(TankGame.gameClient.getbulletImage(), 0, 0, direction, getEnemy());
        int[] pos = getCenterPos(bullet.getRectangle());

        for (Direction direction : Direction.values()) {
            bullet = new Bullet(TankGame.gameClient.getbulletImage(), pos[0], pos[1], direction, getEnemy());
            TankGame.gameClient.getGameObjects().add(bullet);
        }

    }

}
