package tw.com.herpestesurva.gameobject;

import java.awt.*;

public abstract class GameObject {
    protected int x;
    protected int y;
    protected int oldX;
    protected int oldY;
    protected int width;
    protected int height;
    protected boolean alive;
    protected int frame;

    protected Image[] image;

    public GameObject(Image[] image, int x, int y) {
        this.x = x;
        this.y = y;
        this.image = image;
        width = image[0].getWidth(null);
        height = image[0].getHeight(null);
        alive = true;
    }

    public boolean getAlive() {
        return alive;
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, width, height);

    }

    public int[] getCenterPos(Rectangle rect) {
        int[] pos = new int[2];
        pos[0] = this.x + (width - rect.width) / 2;
        pos[1] = this.y + (width - rect.height) / 2;
        return pos;
    }

    public abstract void draw(Graphics g);

    public abstract void ai();

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
