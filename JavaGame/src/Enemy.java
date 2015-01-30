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

    private boolean hit;
    private long hitTimer;
    private boolean slow;

    // CONSTRUCTOR
    public Enemy(int type, int rank) {

	this.type = type;
	this.rank = rank;

	// default enemy type
	if (type == 1) {
	    // color1 = Color.BLUE;
	    color1 = new Color(0, 0, 255, 128); // make it half transparent
	    if (rank == 1) {
		speed = 2;
		r = 5;
		health = 1;
	    }
	    if (rank == 2) {
		speed = 2;
		r = 10;
		health = 2;
	    }
	    if (rank == 3) {
		speed = 1.5;
		r = 20;
		health = 3;
	    }
	    if (rank == 4) {
		speed = 1.5;
		r = 30;
		health = 4;
	    }
	}
	// stronger, faster default
	if (type == 2) {
	    // color1 = Color.RED;
	    color1 = new Color(255, 0, 0, 128);
	    if (rank == 1) {
		speed = 3;
		r = 5;
		health = 2;
	    }
	    if (rank == 2) {
		speed = 3;
		r = 10;
		health = 3;
	    }
	    if (rank == 3) {
		speed = 2.5;
		r = 20;
		health = 3;
	    }
	    if (rank == 4) {
		speed = 2.5;
		r = 30;
		health = 4;
	    }
	}
	// slow, but hard to kill enemy
	if (type == 3) {
	    // color1 = Color.GREEN;
	    color1 = new Color(0, 255, 0, 128);
	    if (rank == 1) {
		speed = 1.5;
		r = 5;
		health = 3;
	    }
	    if (rank == 2) {
		speed = 1.5;
		r = 10;
		health = 4;
	    }
	    if (rank == 3) {
		speed = 1.5;
		r = 25;
		health = 5;
	    }
	    if (rank == 4) {
		speed = 1.5;
		r = 45; // size
		health = 5;
	    }

	}

	x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4; // midlle(half)
								       // of the
								       // game
								       // screen
	y = -r; // off screen at the top

	double angle = Math.random() * 140 + 20; // angle is facing downwords
	rad = Math.toRadians(angle); // convert to radiant

	dx = Math.cos(rad) * speed;
	dy = Math.sin(rad) * speed;

	ready = false; // when we first start ready is false
	dead = false; // when we first start

	hit = false;
	hitTimer = 0;
    }

    // FUNCTIONS
    // we will do some setters here
    public double getx() {
	return x;
    }

    public double gety() {
	return y;
    }

    public int getr() {
	return r;
    }

    public int getType() {
	return type;
    }

    public int getRank() {
	return rank;
    }

    public void setSlow(boolean b) {
	slow = b;
    }

    public boolean isDead() {
	return dead;
    }

    public void hit() {
	health--; // when it is hit the health decrease
	if (health <= 0) {
	    dead = true; // when is below 0 is dead
	}
	hit = true;
	hitTimer = System.nanoTime();
    }

    public void explode() {

	if (rank > 1) {

	    int amount = 0;
	    if (type == 1) { // when they explode they turn into 3 smaller ones
		amount = 3;
	    }
	    if (type == 2) {
		amount = 3;
	    }
	    if (type == 3) {
		amount = 4;
	    }

	    for (int i = 0; i < amount; i++) {

		Enemy e = new Enemy(getType(), getRank() - 1);
		e.setSlow(slow);
		e.x = this.x;
		e.y = this.y;
		double angle = 0;
		if (!ready) {
		    angle = Math.random() * 140 + 20;
		} else {
		    angle = Math.random() * 360;
		}
		e.rad = Math.toRadians(angle);

		GamePanel.enemies.add(e);
	    }
	}

    }

    public void update() {
	if (slow) {
	    x += dx * 0.3; // 30% of the speed
	    y += dy * 0.3;
	} else {
	    x += dx;
	    y += dy;
	}

	if (!ready) { // if it is in the game screen is ready
	    if (x > r && x < GamePanel.WIDTH - r && y > r // this is how we
							  // check if it is
							  // inside the screen
							  // or not
		    && y < GamePanel.HEIGHT - r) {
		ready = true;
	    }
	}
	if (x < r && dx < 0) // enemy is bouncing of the walls and going out of
			     // the screen
	    dx = -dx;
	if (y < r && dy < 0)
	    dy = -dy;
	if (x > GamePanel.WIDTH - r && dx > 0)
	    dx = -dx;
	if (y > GamePanel.HEIGHT - r && dy > 0)
	    dy = -dy;
	if (hit) {
	    long elapsed = (System.nanoTime() - hitTimer) / 1000000; // the
								     // amount
								     // of time
								     // that is
								     // past
								     // since
								     // the hit
	    if (elapsed > 50) { // we keep the enemy on for another 50
				// milliseconds
		hit = false;
		hitTimer = 0;
	    }
	}

    }

    public void draw(Graphics2D g) {

	if (hit) {
	    g.setColor(Color.WHITE); // flashing the enemy when it's hit
	    g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);

	    g.setStroke(new BasicStroke(3));
	    g.setColor(Color.WHITE.darker());
	    g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
	    g.setStroke(new BasicStroke(1));
	} else {
	    g.setColor(color1);
	    g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);

	    g.setStroke(new BasicStroke(3)); // this set the line thickness to 3
	    g.setColor(color1.darker()); // this is for the boarder
	    g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
	    g.setStroke(new BasicStroke(1)); // afterwards we set it back to 1
	}
    }
}