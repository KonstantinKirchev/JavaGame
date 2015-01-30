import javax.swing.JPanel; // this is where all of the game code will go

import java.awt.*; // for the graphics
import java.awt.event.*; // because we are using a keyListener
import java.awt.image.*;
import java.util.*;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    // FIELDS
    public static int WIDTH = 400; // we made it static so that other class like
				   // Player can use it
    public static int HEIGHT = 400;

    private Thread thread; // we create a new thread(нишка(поток))
    private boolean running; // this is the boolean that will tell us if the
			     // game is running.

    private BufferedImage image; // this is our Canvas
    private Graphics2D g; // this is our paint brush

    private int FPS = 30; // set a speed limit to the game
    private double averageFPS;

    public static Player player; // we make it public static so everyone can see
				 // the player
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    public static ArrayList<PowerUp> powerups;
    public static ArrayList<Explosion> explosions;
    public static ArrayList<Text> texts;
    
    private long waveStartTimer;
    private long waveStartTimerDiff; // keep track how much time is passed by
    private int waveNumber;
    private boolean waveStart; // it tells us when to start create enemies or
			       // not
    private int waveDelay = 2000;

    private long slowDownTimer;
    private long slowDownTimerDiff;
    private int slowDownLength = 6000; // slowing down for 6 sec.

    // CONSTRUCTOR
    public GamePanel() {
	super();
	setPreferredSize(new Dimension(WIDTH, HEIGHT));
	setFocusable(true); // this makes our gamePanel to be focused on
	requestFocus(); // gets the focus . This is important of getting
			// keyboard improve from the user
    }

    // FUNCTIONS
    public void addNotify() { // this function is build into the JPanel. We are
			      // overwriting it.
	super.addNotify(); // we are calling it here. This says that if the
			   // Jpanel don't loading that we can start whatever we
			   // want to do.
	if (thread == null) {
	    thread = new Thread(this);
	    thread.start(); // we start our game here
	}
	addKeyListener(this); // keyboard input
    }

    public void run() { // the thread will look for run function. We are
			// overwriting it. This is what the thread is going to
			// run.
	running = true; // when we first start running it its going to be true.

	image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB); // we
									      // initialize
									      // the
									      // bufferedImage
									      // here
	g = (Graphics2D) image.getGraphics(); // this is the paint brush
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // the game is blogi
							    // and pixeling
							    // without this
							    // Antialiasing
		RenderingHints.VALUE_ANTIALIAS_ON); // we modify the graphics
						    // object
	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	player = new Player(); // here we initialize the player
	bullets = new ArrayList<Bullet>(); // here we initialize the bullet
	enemies = new ArrayList<Enemy>(); // here we initialize the enemy
	powerups = new ArrayList<PowerUp>();
	explosions = new ArrayList<Explosion>();
	texts = new ArrayList<Text>();
	
	waveStartTimer = 0;
	waveStartTimerDiff = 0;
	waveStart = true;
	waveNumber = 0;
	// for (int i = 0; i < 5; i++) { // we are adding 5 new enemies to test
	// the
	// // game
	// enemies.add(new Enemy(1, 1));
	// }

	long startTime;
	long URDTimeMillis;
	long waitTime;
	long totalTime = 0;

	int frameCount = 0; // start
	int maxFrameCount = 30; // end

	long targetTime = 1000 / FPS; // this is gonna give us the amount of
				      // time that it takes for 1 loop to run in
				      // order to maintain 30 FPS. (33
				      // milliseconds per loop at 30 FPS)

	// GAME LOOP // This is the heart of our game. The loop is running 30
	// times per second.
	while (running) { // this is gonna keep going while running is true.

	    startTime = System.nanoTime(); // it's build in Java option. It gets
					   // the current time in nanoseconds.(a
					   // bit too accurate)

	    gameUpdate();
	    gameRender();
	    gameDraw();

	    URDTimeMillis = (System.nanoTime() - startTime) / 1000000; // we
								       // divide
								       // by a
								       // million
								       // to get
								       // milliseconds.
	    waitTime = targetTime - URDTimeMillis; // the amount of extra time
						   // that we need to wait
	    // each loop has to be 33 milliseconds. If our updateRender draw
	    // only took 20 milliseconds then we still need to wait an extra 13
	    // milliseconds. That's what waitTime is for.
	    try {
		Thread.sleep(waitTime); // we are gonna get to sleep for that
					// amount of time(13sec.)
	    } catch (Exception e) {

	    }
	    totalTime += System.nanoTime() - startTime; // this is gonna give us
							// the total loopTime
	    frameCount++;
	    if (frameCount == maxFrameCount) { // this sould put a speed limit
					       // to our game loop
		averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000); // this
									    // is
									    // gonna
									    // give
									    // us
									    // the
									    // average
									    // frames
									    // per
									    // second
		frameCount = 0;
		totalTime = 0;
	    }
	}
	g.setColor(new Color(0, 100, 255));
	g.fillRect(0, 0, WIDTH, HEIGHT);
	g.setColor(Color.WHITE);
	g.setFont(new Font("Century Gothic", Font.PLAIN, 16));
	String s = "G A M E   O V E R";
	int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
	g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2);
	s = "Final Score: " + player.getScore();
	length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
	g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2 + 30);
	gameDraw(); // ends the game
    }

    private void gameUpdate() { // updating everything in the game(player
				// possition, enemy possition, projectiles and
				// also deal with collision, everything that the
				// game needs to do(all of the game logic goes
				// into the gameUpdate)) Изчислява всички
				// променливи в играта.
	// new wave
	if (waveStartTimer == 0 && enemies.size() == 0) {
	    waveNumber++;
	    waveStart = false; // we don't want to start creating enemies yet
			       // (this is like a delay timer)
	    waveStartTimer = System.nanoTime();
	} else { // if we already start the waveStartTimer
	    waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
	    if (waveStartTimerDiff > waveDelay) {
		waveStart = true;
		waveStartTimer = 0;
		waveStartTimerDiff = 0;

	    }
	}
	// create enemies
	if (waveStart && enemies.size() == 0) {
	    createNewEnemies();
	}
	// player update
	player.update();

	// bullet update
	for (int i = 0; i < bullets.size(); i++) {
	    boolean remove = bullets.get(i).update();
	    if (remove) {
		bullets.remove(i);
		i--;
	    }
	}

	// enemy update
	for (int i = 0; i < enemies.size(); i++) {
	    enemies.get(i).update();
	}
	// powerup update
	for (int i = 0; i < powerups.size(); i++) {
	    boolean remove = powerups.get(i).update(); // we have to check if we
						       // need to remove them
	    if (remove) {
		powerups.remove(i);
		i--;
	    }
	}
	// explosion update
	for (int i = 0; i < explosions.size(); i++) {
	    boolean remove = explosions.get(i).update(); // we have to check if
							 // we need to remove
							 // them
	    if (remove) {
		explosions.remove(i);
		i--;
	    }
	}
	// text update
	for (int i = 0; i < texts.size(); i++) {
	    boolean remove = texts.get(i).update();
	    if (remove) {
		texts.remove(i);
		i--;
	    }
	}
	// bullet-enemy collision (we start to shoot them)
	for (int i = 0; i < bullets.size(); i++) {

	    Bullet b = bullets.get(i);
	    double bx = b.getx();
	    double by = b.gety();
	    double br = b.getr();
	    for (int j = 0; j < enemies.size(); j++) {

		Enemy e = enemies.get(j);
		double ex = e.getx();
		double ey = e.gety();
		double er = e.getr();

		double dx = bx - ex;
		double dy = by - ey;
		double dist = Math.sqrt(dx * dx + dy * dy); // we use Pitagorian
							    // theorem to find
							    // the distance
							    // between two
							    // points

		if (dist < br + er) { // we compare the distance between the
				      // radius of the enemy and the bullet if
				      // it is less so they collaided
		    e.hit(); // enemy get hit
		    bullets.remove(i);
		    i--;
		    break;
		}
	    }
	}
	// check dead enemies
	for (int i = 0; i < enemies.size(); i++) {
	    if (enemies.get(i).isDead()) {
		Enemy e = enemies.get(i);
		player.addScore(e.getType() + e.getRank()); // add points for
							    // killing enemies
		enemies.remove(i);
		i--;
	    }
	}
	// chance for powerup
	double rand = Math.random();
	if (rand < 0.001) { // this is the chance for this event to happend
	    powerups.add(new PowerUp(1, e.getx(), e.gety()));
	} else if (rand < 0.020) {
	    powerups.add(new PowerUp(3, e.getx(), e.gety()));
	} else if (rand < 0.120) {
	    powerups.add(new PowerUp(2, e.getx(), e.gety()));
	} else if (rand < 0.130) {
	    powerups.add(new PowerUp(4, e.getx(), e.gety()));
	}
	player.addScore(e.getType() + e.getRank());
	enemies.remove(i);
	i--;

	e.explode();
	explosions.add(new Explosion(e.getx(), e.gety(), e.getr(),
		e.getr() + 30));

	// check dead player
	if (player.isDead()) {
	    running = false;
	}

	// player-enemy collision
	if (!player.isRecovering()) {
	    int px = player.getx();
	    int py = player.gety();
	    int pr = player.getr();
	    for (int i = 0; i < enemies.size(); i++) {

		Enemy e = enemies.get(i);
		double ex = e.getx();
		double ey = e.gety();
		double er = e.getr();

		double dx = px - ex;
		double dy = py - ey;
		double dist = Math.sqrt(dx * dx + dy * dy);

		if (dist < pr + er) { // we have a collision detection(hit)
		    player.loseLife();
		}

	    }
	}

	// player-powerup collision
	int px = player.getx();
	int py = player.gety();
	int pr = player.getr();
	for (int i = 0; i < powerups.size(); i++) {
	    PowerUp p = powerups.get(i);
	    double x = p.getx();
	    double y = p.gety();
	    double r = p.getr();
	    double dx = px - x;
	    double dy = py - y;
	    double dist = Math.sqrt(dx * dx + dy * dy);

	    // collected powerup
	    if (dist < pr + r) {

		int type = p.getType();

		if (type == 1) {
		    player.gainLife();
		    texts.add(new Text(player.getx(), player.gety(), 2000,
			    "Extra Life"));
		}
		if (type == 2) {
		    player.increasePower(1);
		    texts.add(new Text(player.getx(), player.gety(), 2000,
			    "Power"));
		}
		if (type == 3) {
		    player.increasePower(2);
		    texts.add(new Text(player.getx(), player.gety(), 2000,
			    "Double Power"));
		}
		if (type == 4) {
		    slowDownTimer = System.nanoTime(); // set the slowdowntimer
						       // to the current time
		    for (int j = 0; j < enemies.size(); j++) {
			enemies.get(j).setSlow(true);
		    }
		    texts.add(new Text(player.getx(), player.gety(), 2000,
			    "Slow Down"));
		}
		powerups.remove(i);
		i--;
	    }
	}

	// slowdown update
	if (slowDownTimer != 0) { // we are in a slowdown mode
	    slowDownTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
	    if (slowDownTimerDiff > slowDownLength) {
		slowDownTimer = 0;
		for (int j = 0; j < enemies.size(); j++) {
		    enemies.get(j).setSlow(false);
		}
	    }
	}
    }

    private void gameRender() { // this one is drawing everything that is
				// currently active on to the screen(the player,
				// the enemies, the background, the projectiles,
				// everything).Graphical representation.
				// Създаване на видео чрез рисуване.
	// draw background
	g.setColor(new Color(0, 100, 255)); // draw the offscreen image. This is
					    // the color of the background
					    // screen.
	g.fillRect(0, 0, WIDTH, HEIGHT);
	// g.setColor(Color.BLACK);
	// g.drawString("FPS: " + averageFPS, 10, 10);
	// g.drawString("num bullets" + bullets.size(), 10, 20); // check if our
	// bullet is
	// actually being
	// removed
	
	// draw slowdown screen
	if (slowDownTimer != 0) {
	    g.setColor(new Color(255, 255, 255, 64)); // very transperant white
	    g.fillRect(0, 0, WIDTH, HEIGHT); // fillup the entire screen
	}
	// draw player
	player.draw(g);

	// draw bullet
	for (int i = 0; i < bullets.size(); i++) {
	    bullets.get(i).draw(g);
	}

	// draw enemy
	for (int i = 0; i < enemies.size(); i++) {
	    enemies.get(i).draw(g);
	}
	// draw powerups
	for (int i = 0; i < powerups.size(); i++) {
	    powerups.get(i).draw(g);
	}
	// draw explosions
	for (int i = 0; i < explosions.size(); i++) {
	    explosions.get(i).draw(g);
	}
	// draw text
	for (int i = 0; i < texts.size(); i++) {
	    texts.get(i).draw(g);
	}
	// draw wave number
	if (waveStartTimer != 0) { // if it's not 0 we are in the process of
				   // creating enemies
	    g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
	    String s = "- W A V E  " + waveNumber + "   -"; // this is the start
							    // of every new
							    // level
	    int length = (int) g.getFontMetrics().getStringBounds(s, g)
		    .getWidth(); // this gives us the total length of the screen
				 // in pixels
	    int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff
		    / waveDelay)); // this is the transperancy
	    if (alpha > 255)
		alpha = 255;
	    g.setColor(new Color(255, 255, 255, alpha));
	    g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2);
	}
	// draw player lives
	for (int i = 0; i < player.getLives(); i++) {
	    g.setColor(Color.WHITE);
	    g.fillOval(20 + (20 * i), 20, player.getr() * 2, player.getr() * 2);
	    g.setStroke(new BasicStroke(3));
	    g.setColor(Color.WHITE.darker());
	    g.drawOval(20 + (20 * i), 20, player.getr() * 2, player.getr() * 2);
	    g.setStroke(new BasicStroke(1));
	}

	// draw player power
	g.setColor(Color.YELLOW);
	g.fillRect(20, 40, player.getPower() * 8, 8);
	g.setColor(Color.YELLOW.darker());
	g.setStroke(new BasicStroke(2));
	for (int i = 0; i < player.getRequiredPower(); i++) { // this loop
							      // create a bunch
							      // of boxes
	    g.drawRect(20 + 8 * i, 40, 8, 8);
	}
	g.setStroke(new BasicStroke(1));

	// draw player score
	g.setColor(Color.WHITE);
	g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
	g.drawString("Score: " + player.getScore(), WIDTH - 100, 30);

	// draw slowdown meter
	if (slowDownTimer != 0) {
	    g.setColor(Color.WHITE);
	    g.drawRect(20, 60, 100, 8);
	    g.fillRect(20, 60, (int) (100 - 100.0 * slowDownTimerDiff
		    / slowDownLength), 8);
	}
    }

    private void gameDraw() { // this is called double boofering, because we are
			      // drawing off screen image first and then we are
			      // drawing on to the game screen
	Graphics g2 = this.getGraphics(); // in order to actually draw onto the
					  // game screen we need to get the
					  // graphics object for our game
					  // pannel, and we are doing it with
					  // "this.getGraphics()"
	g2.drawImage(image, 0, 0, null); // g2 is the paint brush for our actual
					 // game screen. g2 will draw the
					 // offscreen image onto the screen
	g2.dispose();
    }

    private void createNewEnemies() {

	enemies.clear();
	Enemy e;

	if (waveNumber == 1) { // 1st level
	    for (int i = 0; i < 4; i++) {
		enemies.add(new Enemy(1, 1));
	    }
	}
	if (waveNumber == 2) { // 2nd level
	    for (int i = 0; i < 8; i++) {
		enemies.add(new Enemy(1, 1));
	    }
	}
	if (waveNumber == 3) { // 3rd level
	    for (int i = 0; i < 4; i++) {
		enemies.add(new Enemy(1, 1));
	    }
	    enemies.add(new Enemy(1, 2));
	    enemies.add(new Enemy(1, 2));
	}
	if (waveNumber == 4) { // 4rd level
	    enemies.add(new Enemy(1, 3)); // first one is type and 2nd is rank
	    enemies.add(new Enemy(1, 4));
	    for (int i = 0; i < 4; i++) {
		enemies.add(new Enemy(2, 1)); // type 2 enemies
	    }
	}
	if (waveNumber == 5) { // 5th level
	    enemies.add(new Enemy(1, 4));
	    enemies.add(new Enemy(1, 3));
	    enemies.add(new Enemy(2, 3));
	}
	if (waveNumber == 6) { // 6th level
	    enemies.add(new Enemy(1, 3));
	    for (int i = 0; i < 4; i++) {
		enemies.add(new Enemy(2, 1));
		enemies.add(new Enemy(3, 1));
	    }
	}
	if (waveNumber == 7) { // 7th level
	    enemies.add(new Enemy(1, 3));
	    enemies.add(new Enemy(2, 3));
	    enemies.add(new Enemy(3, 3));
	}
	if (waveNumber == 8) { // 8th level
	    enemies.add(new Enemy(1, 4));
	    enemies.add(new Enemy(2, 4));
	    enemies.add(new Enemy(3, 4));
	}
	if (waveNumber == 9) { // this is the last level the game is over
	    running = false;
	}
    }

    public void keyTyped(KeyEvent key) { // we need to overwrite 3 functions
    } // keyTyped doesn't really important. We ganna use keyPressed and
      // keyReleased.

    public void keyPressed(KeyEvent key) {
	int keyCode = key.getKeyCode();
	if (keyCode == KeyEvent.VK_LEFT) { // we pressed left button
	    player.setLeft(true);
	}
	if (keyCode == KeyEvent.VK_RIGHT) {
	    player.setRight(true);
	}
	if (keyCode == KeyEvent.VK_UP) {
	    player.setUp(true);
	}
	if (keyCode == KeyEvent.VK_DOWN) {
	    player.setDown(true);
	}
	if (keyCode == KeyEvent.VK_Z) { // we are using the Z key to fire the
					// bullet
	    player.setFiring(true); // create a new function setFiring for the
				    // player
	}
    }

    public void keyRelease(KeyEvent key) {
	int keyCode = key.getKeyCode();
	if (keyCode == KeyEvent.VK_LEFT) {
	    player.setLeft(false);
	}
	if (keyCode == KeyEvent.VK_RIGHT) {
	    player.setRight(false);
	}
	if (keyCode == KeyEvent.VK_UP) {
	    player.setUp(false);
	}
	if (keyCode == KeyEvent.VK_DOWN) {
	    player.setDown(false);
	}
	if (keyCode == KeyEvent.VK_Z) {
	    player.setFiring(false);
	}
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}