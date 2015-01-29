import java.awt.*;

public class Enemy {

    // FIELDS
    private double x;
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double rad;
    private double speed;
    private int health;
    private int type;
    private int rank;

    private Color color1;

    private boolean ready; // the enemy is inside the game screen
    private boolean dead; // health is zero or lower

    // CONSTRUCTOR
    public Enemy(int type, int rank) {

	this.type = type;
	this.rank = rank;

	// default enemy type
	if (type == 1) {
	    color1 = Color.BLUE;
	    if (rank == 1) { // the easiest enemy
		speed = 2;
		r = 5;
		health = 1;
	    }
	}

	x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4; // midlle(half) of the game screen
	y = -r; // off screen at the top

	double angle = Math.random() * 140 + 20; // angle is facing downwords
	rad = Math.toRadians(angle); // convert to radiant

	dx = Math.cos(rad) * speed;
	dy = Math.sin(rad) * speed;

	ready = false; // when we first start ready is false
	dead = false; // when we first start
    }

    // FUNCTIONS
    // we will do some setters here
    public double getx() {
	return x;
    }

    public double gety() {
	return y;
    }

    public double getr() {
	return r;
    }
    public int getType(){
	return type;
    }
    public int getRank(){
	return rank;
    }

    public boolean isDead() {
	return dead;
    }

    public void hit() {
	health--; // when it is hit the health decrease
	if (health <= 0) {
	    dead = true; // when is below 0 is dead
	}
    }

    public void update() {
	x += dx;
	y += dy;

	if (!ready) { // if it is in the game screen is ready
	    if (x > r && x < GamePanel.WIDTH - r && y > r // this is how we check if it is inside the screen or not
		    && y < GamePanel.HEIGHT - r) {
		ready = true;
	    }
	}
	if (x < r && dx < 0) // enemy is bouncing of the walls and going out of the screen
	    dx = -dx;
	if (y < r && dy < 0)
	    dy = -dy;
	if (x > GamePanel.WIDTH - r && dx > 0)
	    dx = -dx;
	if (y > GamePanel.HEIGHT - r && dy > 0)
	    dy = -dy;

    }

    public void draw(Graphics2D g) {

	g.setColor(color1);
	g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);

	g.setStroke(new BasicStroke(3)); // this set the line thickness to 3
	g.setColor(color1.darker()); // this is for the boarder
	g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
	g.setStroke(new BasicStroke(1)); // afterwards we set it back to 1
    }
}