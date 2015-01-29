import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Explosion {
    // * Explosion when the enemy dies.

    // FIELDS
    private double x;
    private double y;
    private int r;
    private int maxRadius; // * when the enemy dies.

    // CONSTRUCTOR
    public Explosion(double x, double y, int r, int max) {
	this.x = x;
	this.y = y;
	this.r = r;
	maxRadius = max;
    }

    public boolean update() {
	// * makes the explosions
	r += 2; // * set the time that the explosion will last.
	if (r >= maxRadius) {
	    return true; // * ones it reaches the maxRadius the radius
			 // disappears.
	}
	return false;
    }

    public void draw(Graphics2D g) {
	g.setColor(new Color(255, 255, 255, 128));
	// *set the color, the 128 shows the transparency.
	g.setStroke(new BasicStroke(3));
	g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
	g.setStroke(new BasicStroke(1));
    }
}