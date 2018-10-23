import java.awt.Image;
import java.awt.event.KeyEvent;

public class Plane {

    public int x;
    public int y;
    public int width;
    public int height;

    private Image image;

    public Plane () {
        x = 500;
        y = 100;
        width = 100;
        height = 100;
    }

    public Render getRender() {

        Render r = new Render();
        r.x = x;
        r.y = y;

        if (image == null) {
            image = Util.loadImage("lib/plane.png");     
        }
        r.image = image;

        return r;
    }
}