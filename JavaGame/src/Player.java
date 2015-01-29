import java.awt.*; // For the graphics

public class Player {

    // FIELDS
    private int x;
    private int y;
    private int r;

    private int dx;
    private int dy;
    private int speed;

    private boolean left; // that's gonna tell us if the player is moving in
			  // whatever direction
    private boolean right;
    private boolean up;
    private boolean down;

    private boolean firing;
    private long firingTimer;
    private long firingDelay;

    private boolean recovering;
    private long recoveryTimer;
    private int lives;
    private Color color1; // regular color
    private Color color2; // the color when we are hit

    private int score;

    // CONSTRUCTOR
    public Player() {
	x = GamePanel.WIDTH / 2;
	y = GamePanel.HEIGHT / 2;

	dx = 0;
	dy = 0;
	speed = 5;

	lives = 3;
	color1 = Color.WHITE;
	color2 = Color.RED; // the color when we are hit

	firing = false; // we initialize the firing
	firingTimer = System.nanoTime();
	firingDelay = 200; // 200 milliseconds (5 shots per second)

	recovering = false;
	recoveryTimer = 0;

	score = 0;
    }

    // FUNCTIONS
    public int getx() {
	return x;
    }

    public int gety() {
	return y;
    }

    public int getr() {
	return r;
    }

    public int getScore() {
	return score;
    }

    public int getLives() {
	return lives;
    }

    public boolean isDead() {
	return lives <= 0;
    }

    public boolean isRecovering() {
	return recovering; // if the player is recovering from a hit
    }

    public void setLeft(boolean b) {
	left = b;
    }

    public void setRight(boolean b) {
	right = b;
    }

    public void setUp(boolean b) {
	up = b;
    }

    public void setDown(boolean b) {
	down = b;
    }

    public void setFiring(boolean b) {
	firing = b;
    }

    public void addScore(int i) {
	score += i;
    }

    public void loselife() {
	lives--;
	recovering = true;
	recoveryTimer = System.nanoTime();
    }

    public void update() {
	if (left) {
	    dx = -speed;
	}
	if (right) {
	    dx = speed;
	}
	if (up) {
	    dy = -speed;
	}
	if (down) {
	    dy = speed;
	}

	x += dx;
	y += dy;

	if (x < r)
	    x = r;
	if (y < r)
	    y = r;
	if (x > GamePanel.WIDTH - r)
	    x = GamePanel.WIDTH - r;
	if (y > GamePanel.HEIGHT - r)
	    y = GamePanel.HEIGHT - r;

	dx = 0;
	dy = 0;

	if (firing) {
	    long elapsed = (System.nanoTime() - firingTimer) / 1000000;
	    if (elapsed > firingDelay) { // we compare the two
		GamePanel.bullets.add(new Bullet(270, x, y)); // we fire a
							      // bullet.The
							      // arguments that
							      // we gave for the
							      // constructor is
							      // 270 (Straight
							      // up) for the
							      // angle and the
							      // player
							      // possition x and
							      // y
		firingTimer = System.nanoTime(); // and then we reset the
						 // firingTimer
	    }
	}
	// firing
	if (firing) {
	    long elapsed = (System.nanoTime() - firingTimer) / 1000000;
	    if (elapsed > firingDelay) { // invinsible after being hit
		firingTimer = System.nanoTime();

		if (powerLevel < 2) {
		    GamePanel.bullets.add(new Bullet(270, x, y));
		} else if (powerLevel < 4) {
		    GamePanel.bullets.add(new Bullet(270, x + 5, y));
		    GamePanel.bullets.add(new Bullet(270, x - 5, y));
		} else {
		    GamePanel.bullets.add(new Bullet(270, x, y));
		    GamePanel.bullets.add(new Bullet(275, x + 5, y));
		    GamePanel.bullets.add(new Bullet(265, x + 5, y));
		}
	    }
	}

	if (recovering) {
	    long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
	    if (elapsed > 2000) {
		recovering = false;
		recoveryTimer = 0;
	    }
	}
    }

    public void draw(Graphics2D g) { // we draw the player here
	if (recovering) {
	    g.setColor(color2);
	    g.fillOval(x - r, y - r, 2 * r, 2 * r); // this makes our x and y
						    // coordinates at the center
						    // of
						    // the player

	    g.setStroke(new BasicStroke(3)); // this makes our lines 3 pixels
					     // weight
	    g.setColor(color2.darker());
	    g.drawOval(x - r, y - r, 2 * r, 2 * r); // we draw the boarder to
						    // make
						    // it look nice
	    g.setStroke(new BasicStroke(1)); // reset the stroke to 1 after this
	} else {
	    g.setColor(color1);
	    g.fillOval(x - r, y - r, 2 * r, 2 * r); // this makes our x and y
						    // coordinates at the center
						    // of
						    // the player

	    g.setStroke(new BasicStroke(3)); // this makes our lines 3 pixels
					     // weight
	    g.setColor(color1.darker());
	    g.drawOval(x - r, y - r, 2 * r, 2 * r); // we draw the boarder to
						    // make
						    // it look nice
	    g.setStroke(new BasicStroke(1));
	}
    }
}