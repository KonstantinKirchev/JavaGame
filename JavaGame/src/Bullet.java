import java.awt.*;

public class Bullet {

    // FIELDS

    private double x; // double because we are dealing with radiants(angles)
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double rad;
    private double speed;

    private Color color1;

    // CONSTRUCTOR

    public Bullet(double angle, int x, int y) { // we are giving angle and starting possition
	this.x = x; // the start x
	this.y = y; // the start y
	r = 2;

	rad = Math.toRadians(angle);
	speed = 10; // this will give better hit detection
	dx = Math.cos(rad) * speed;
	dy = Math.sin(rad) * speed;

	color1 = Color.YELLOW;
    }

    // FUNCTIONS

    public double getx() {
	return x;
    }

    public double gety() {
	return y;
    }

    public double getr() {
	return r;
    }

    public boolean update() {

	x += dx;
	y += dy;

	if (x < -r || x > GamePanel.WIDTH + r || y < -r
		|| y > GamePanel.HEIGHT + r) {
	    return true; // if we need to remove the bullet when it hit an enemy, we want to take it off the list
	}

	return false; // false is the default value
    }

    public void draw(Graphics2D g) {
	g.setColor(color1);
	g.fillOval((int) x - r, (int) y - r, 2 * r, 2 * r);
    }
}