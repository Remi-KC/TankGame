package tw.com.herpestesurva.gameobject;

import java.awt.*;

public class Explosion extends GameObject {
    public Explosion(Image[] image, int x, int y) {
        super(image, x, y);
        explosion();
    }

    void explosion() {
        new Thread(() -> { // 多執行緒
            while (alive) {
                try {
                    Thread.sleep(50);// 25-50為合理範圍
                    if (++frame >= image.length) {
                        alive = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void draw(Graphics g) {
        if (!alive) {
            return;
        }
        // 動畫
        g.drawImage(image[frame], x, y, null);
    }

    @Override
    public void ai() {

    }

}
