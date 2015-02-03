import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
    public static final AudioClip BACKGROUNDMUSIC = Applet
	    .newAudioClip(Sound.class.getResource("music.wav"));
    public static final AudioClip GAMEOVER = Applet.newAudioClip(Sound.class
	    .getResource("game_over.wav"));
    public static final AudioClip EXPLOSIONSOUND = Applet
	    .newAudioClip(Sound.class.getResource("Explosion6.wav"));
}