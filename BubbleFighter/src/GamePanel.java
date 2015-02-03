import javax.swing.JPanel;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener {

    // FIELDS
    public static int WIDTH = 800;
    public static int HEIGHT = 600;

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private Graphics2D g;

    private int FPS = 60;
    @SuppressWarnings("unused")
    private double averageFPS;

    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    public static ArrayList<PowerUp> powerups;
    public static ArrayList<Explosion> explosions;
    public static ArrayList<Text> texts;

    private long levelStartTimer;
    private long levelStartTimerDiff;
    private int levelNumber;
    private boolean levelStart;
    private int levelDelay = 2000;

    private long slowDownTimer;
    private long slowDownTimerDiff;
    private int slowDownLength = 6000;
    private BufferedImage backGround;
    private BufferedImage backGround1;

    // CONSTRUCTOR
    public GamePanel() {
	super();
	setPreferredSize(new Dimension(WIDTH, HEIGHT));
	setFocusable(true);
	requestFocus();
    }

    // FUNCTIONS
    public void addNotify() {
	super.addNotify();
	if (thread == null) {
	    thread = new Thread(this);
	    thread.start();
	}
	addKeyListener(this);
    }

    public void run() {

	running = true;

	image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	g = (Graphics2D) image.getGraphics();
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	player = new Player();
	bullets = new ArrayList<Bullet>();
	enemies = new ArrayList<Enemy>();
	powerups = new ArrayList<PowerUp>();
	explosions = new ArrayList<Explosion>();
	texts = new ArrayList<Text>();

	levelStartTimer = 0;
	levelStartTimerDiff = 0;
	levelStart = true;
	levelNumber = 0;

	long startTime;
	long URDTimeMillis;
	long waitTime;
	long totalTime = 0;

	int frameCount = 0;
	int maxFrameCount = 30;

	long targetTime = 1000 / FPS;

	// GAME LOOP
	Sound.BACKGROUNDMUSIC.loop();
	while (running) {
	   
	    startTime = System.nanoTime();

	    gameUpdate();
	    gameRender();
	    gameDraw();

	    URDTimeMillis = (System.nanoTime() - startTime) / 1000000;

	    waitTime = targetTime - URDTimeMillis;

	    try {
		Thread.sleep(waitTime);
	    } catch (Exception e) {
	    }

	    frameCount++;
	    if (frameCount == maxFrameCount) {
		averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
		frameCount = 0;
		totalTime = 0;
	    }

	}
	Sound.BACKGROUNDMUSIC.stop();
	Sound.GAMEOVER.play();
	String path1 = "./image1.jpg";
	try {
	    backGround1 = ImageIO.read(new File(path1));
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
	g.drawImage(backGround1, 0, 0, null);
	g.setColor(Color.RED);
	g.setFont(new Font("Century Gothic", Font.BOLD, 30));
	String s = "G A M E   O V E R";
	int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
	g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2);
	s = "F I N A L    S C O R E:  " + player.getScore();
	length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
	g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2 + 30);
	gameDraw();

    }

    private void gameUpdate() {

	// new wave
	if (levelStartTimer == 0 && enemies.size() == 0) {
	    levelNumber++;
	    levelStart = false;
	    levelStartTimer = System.nanoTime();
	} else {
	    levelStartTimerDiff = (System.nanoTime() - levelStartTimer) / 1000000;
	    if (levelStartTimerDiff > levelDelay) {
		levelStart = true;
		levelStartTimer = 0;
		levelStartTimerDiff = 0;
	    }
	}

	// create enemies
	if (levelStart && enemies.size() == 0) {
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
	    boolean remove = powerups.get(i).update();
	    if (remove) {
		powerups.remove(i);
		i--;
	    }
	}

	// explosion update
	for (int i = 0; i < explosions.size(); i++) {
	    boolean remove = explosions.get(i).update();
	    if (remove) {
		Sound.EXPLOSIONSOUND.play();
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

	// bullet-enemy collision
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
		double dist = Math.sqrt(dx * dx + dy * dy);

		if (dist < br + er) {
		    e.hit();
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

		// chance for powerup
		double rand = Math.random();
		if (rand < 0.001)
		    powerups.add(new PowerUp(1, e.getx(), e.gety()));
		else if (rand < 0.020)
		    powerups.add(new PowerUp(3, e.getx(), e.gety()));
		else if (rand < 0.120)
		    powerups.add(new PowerUp(2, e.getx(), e.gety()));
		else if (rand < 0.130)
		    powerups.add(new PowerUp(4, e.getx(), e.gety()));

		player.addScore(e.getType() + e.getRank());
		enemies.remove(i);
		i--;

		e.explode();
		explosions.add(new Explosion(e.getx(), e.gety(), e.getr(), e
			.getr() + 60));

	    }

	}

	// check dead player
	if (player.isDead()) { 
	    running = false;
	    Sound.GAMEOVER.play();
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

		if (dist < pr + er) {
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
		    slowDownTimer = System.nanoTime();
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
	if (slowDownTimer != 0) {
	    slowDownTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
	    if (slowDownTimerDiff > slowDownLength) {
		slowDownTimer = 0;
		for (int j = 0; j < enemies.size(); j++) {
		    enemies.get(j).setSlow(false);
		}
	    }
	}

    }

    private void gameRender() {

	// draw background
	g.drawImage(backGround, 0, 0, null);
	// g.setColor(new Color(0, 100, 255));
	// g.fillRect(0, 0, WIDTH, HEIGHT);

	// draw slowdown screen
	if (slowDownTimer != 0) {
	    g.setColor(new Color(255, 255, 255, 64));
	    g.fillRect(0, 0, WIDTH, HEIGHT);
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
	if (levelStartTimer != 0) {
	    if (levelNumber==1) {
		String path = "./toronto-at-night.jpg";
		try {
		    backGround = ImageIO.read(new File(path));
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	    }
	    else if (levelNumber==2) {
		String path = "./london_at_night-wide.jpg";
		try {
		    backGround = ImageIO.read(new File(path));
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	    }
	    else if (levelNumber==3) {
		String path = "./New-York-City-Night-lights-HD-Wallpapers-1024x576.jpg";
		try {
		    backGround = ImageIO.read(new File(path));
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	    }
	    else if (levelNumber==4) {
		String path = "./rio-de-janeiro-night.jpg";
		try {
		    backGround = ImageIO.read(new File(path));
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	    }
	    else if (levelNumber==5) {
		String path = "./Sydney-Night-Tours-1.jpg";
		try {
		    backGround = ImageIO.read(new File(path));
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	    }
	    else if (levelNumber==6) {
		String path = "./800px-Hong_Kong_Night_Skyline2.jpg";
		try {
		    backGround = ImageIO.read(new File(path));
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	    }
	    else if (levelNumber==7) {
		String path = "./800px-Independence_square,_Sofia_at_night_PD_2012_3.jpg";
		try {
		    backGround = ImageIO.read(new File(path));
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	    }
	    else if (levelNumber==8) {
		String path = "./warsaw.jpg";
		try {
		    backGround = ImageIO.read(new File(path));
		} catch (IOException e1) {
		    e1.printStackTrace();
		}
	    }
	    g.setFont(new Font("Century Gothic", Font.PLAIN, 24));
	    String s = "- L E V E L   " + levelNumber + "   -";
	    int length = (int) g.getFontMetrics().getStringBounds(s, g)
		    .getWidth();
	    int alpha = (int) (255 * Math.sin(3.14 * levelStartTimerDiff
		    / levelDelay));
	    if (alpha > 255)
		alpha = 255;
	    g.setColor(new Color(255, 255, 255, alpha));
	    g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2);
	}

	// draw player lives
	for (int i = 0; i < player.getLives(); i++) {
	    g.setColor(Color.CYAN);
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
	for (int i = 0; i < player.getRequiredPower(); i++) {
	    g.drawRect(20 + 8 * i, 40, 8, 8);
	}
	g.setStroke(new BasicStroke(1));

	// draw player score
	g.setColor(Color.WHITE);
	g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
	g.drawString("Score: " + player.getScore(), WIDTH - 100, 30);

	// draw slowdown meter
	if (slowDownTimer != 0) {
	    g.setColor(Color.WHITE);
	    g.drawRect(20, 60, 100, 8);
	    g.fillRect(20, 60, (int) (100 - 100.0 * slowDownTimerDiff
		    / slowDownLength), 8);
	}

    }

    private void gameDraw() {
	Graphics g2 = this.getGraphics();
	g2.drawImage(image, 0, 0, null);
	g2.dispose();
    }

    private void createNewEnemies() {

	enemies.clear();
	@SuppressWarnings("unused")
	Enemy e;

	if (levelNumber == 1) {
	    for (int i = 0; i < 4; i++) {
		enemies.add(new Enemy(1, 1));
	    }
	}
	if (levelNumber == 2) {
	    for (int i = 0; i < 8; i++) {
		enemies.add(new Enemy(1, 1));
	    }
	}
	if (levelNumber == 3) {
	    for (int i = 0; i < 4; i++) {
		enemies.add(new Enemy(1, 1));
	    }
	    enemies.add(new Enemy(1, 2));
	    enemies.add(new Enemy(1, 2));
	}
	if (levelNumber == 4) {
	    enemies.add(new Enemy(1, 3));
	    enemies.add(new Enemy(1, 4));
	    for (int i = 0; i < 4; i++) {
		enemies.add(new Enemy(2, 1));
	    }
	}
	if (levelNumber == 5) {
	    enemies.add(new Enemy(1, 4));
	    enemies.add(new Enemy(1, 3));
	    enemies.add(new Enemy(2, 3));
	}
	if (levelNumber == 6) {
	    enemies.add(new Enemy(1, 3));
	    for (int i = 0; i < 4; i++) {
		enemies.add(new Enemy(2, 1));
		enemies.add(new Enemy(3, 1));
	    }
	}
	if (levelNumber == 7) {
	    enemies.add(new Enemy(1, 3));
	    enemies.add(new Enemy(2, 3));
	    enemies.add(new Enemy(3, 3));
	}
	if (levelNumber == 8) {
	    enemies.add(new Enemy(1, 4));
	    enemies.add(new Enemy(2, 4));
	    enemies.add(new Enemy(3, 4));
	}
	if (levelNumber == 9) {
	    //Sound.BACKGROUNDMUSIC.stop();
	    Sound.GAMEOVER.play();
	    running = false;
	}

    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
	int keyCode = key.getKeyCode();
	if (keyCode == KeyEvent.VK_LEFT) {
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
	if (keyCode == KeyEvent.VK_Z) {
	    player.setFiring(true);
	}
    }

    public void keyReleased(KeyEvent key) {
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

}
