//import java.io.*;
//import sun.audio.*;
//
//public class BackgroundSound {
//
//    public static void music() {
//	AudioPlayer musicPlayer = AudioPlayer.player;
//	AudioStream backgroundMusicData;
//	AudioData musicData;
//	sun.audio.ContinuousAudioDataStream loop = null;
//	try {
//	    InputStream file = new FileInputStream("../Harmonica.wav");
//	    backgroundMusicData = new AudioStream(file);
//	    musicPlayer.player.start(backgroundMusicData);
//	    // loop = new sun.audio.ContinuousAudioDataStream(musicData);
//
//	} catch (IOException error) {
//	    error.printStackTrace();
//	}
//
//	// musicPlayer.start(loop);
//    }
//}