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
	start = System.nanoTime();// * We set the start time to the current time. NanoTime gives the current time
    }

    public boolean update() {
	long elapsed = (System.nanoTime() - start) / 1000000; // devided by a million so that we can get it in milliseconds
	if (elapsed > time) {
	    return true;// * remove it
	}
	return false;
    }

    public void draw(Graphics2D g) {
	g.setFont(new Font("Century Gothic", Font.PLAIN, 12)); // size 12
	long elapsed = (System.nanoTime() - start) / 1000000; // the amount of time that's left
	int alpha = (int) (255 * Math.sin(3.14 * elapsed / time));
	if (alpha > 255)
	    alpha = 255;
	g.setColor(new Color(255, 255, 255, alpha));
	int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
	g.drawString(s, (int) (x - (length / 2)), (int) y);
    }
}