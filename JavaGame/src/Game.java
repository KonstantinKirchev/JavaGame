import javax.swing.JFrame;

public class Game { //this is the main class

    public static void main(String[] args) {
	JFrame window = new JFrame("First Game"); // create application window
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // when you click on the exit button closes the application and the window itself

	window.setContentPane(new GamePanel()); // we set it to new GamePanel class

	window.pack(); // windows side with whatever is inside of it
	window.setVisible(true);
    }

}
