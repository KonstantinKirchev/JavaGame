import java.awt.*;

public class Text {
    // * text that appears on the screen.

    // FIELDS
    private double x;
    private double y;
    private long time;
    private String s;

    private long start;// * start time.

    // CONSTRUCTOR
    public Text(double x, double y, long time, String s) {
	this.x = x;
	this.y = y;
	this.time = time;
	this.s = s;
	start = System.nanoTime();// * nanoTime gives the current time
    }

    public boolean update() {
	long elapsed = (System.nanoTime() - start) / 1000000;
	if (elapsed > time) {
	    return true;// * remove it
	}
	return false;
    }

    public void draw(Graphics2D g) {
	g.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	long elapsed = (System.nanoTime() - start) / 1000000;
	int alpha = (int) (255 * Math.sin(3.14 * elapsed / time));
	if (alpha > 255)
	    alpha = 255;
	g.setColor(new Color(255, 255, 255, alpha));
	int lenght = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
	g.drawString(s, (int) (x - (lenght / 2)), (int) y);
    }
}