package XXLChess;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A class that plays sound clips in a separate thread.
 */
public class SoundPlayer implements Runnable {
    private static HashMap<String, Clip> soundClips = new HashMap<String, Clip>();
    private static HashMap<String, Clip> backupSoundClips = new HashMap<String, Clip>();
    private Clip soundClip;
    private Clip backupClip;
    
    /**
   * Plays the specified sound file.
   *
   * @param fileName the name of the sound file to be played
   */
  public static void playSound(String fileName) {
    Clip c = soundClips.get(fileName);
    Clip c2 = backupSoundClips.get(fileName);
    if (c == null || c2 == null) return;
    new Thread(new SoundPlayer(c, c2)).start();
  }

  /**
   * Preloads a sound effect file for later use.
   *
   * @param fileName the name of the sound file to be preloaded
   */
  public static void preloadSoundEffect(String fileName) {
    try {
      // Get the folder containing sound effects
      URL soundEffectsFolder = App.class.getClassLoader().getResource("XXLChess/sounds");

      // Construct the URL of the sound file
      URL soundFileURL = new URL(soundEffectsFolder.toExternalForm() + "/" + fileName);

      // Open an audio input stream for the sound file
      AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFileURL);

      // Open a clip to play the sound file
      Clip clip = AudioSystem.getClip();
      clip.open(audioIn);
      audioIn.close();

      // Open another audio input stream for backup clip
      AudioInputStream audioIn2 = AudioSystem.getAudioInputStream(soundFileURL);

      // Store the main clip and backup clip in the respective maps
      soundClips.put(fileName, clip);

      // Open a backup clip to avoid resource conflicts
      Clip clip2 = AudioSystem.getClip();
      clip2.open(audioIn2);
      audioIn.close();
      backupSoundClips.put(fileName, clip2);
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  /**
   * Constructs a SoundPlayer object with the specified sound clips.
   *
   * @param soundClip   the main sound clip to be played
   * @param backupClip  the backup sound clip to be played if the main clip is already running
   */
  public SoundPlayer(Clip soundClip, Clip backupClip) {
    this.soundClip = soundClip;
    this.backupClip = backupClip;
  }

  /**
   * Starts playing the sound clips.
   */
  public void run() {
    Clip c = soundClip;
    if (c.isRunning()) {
      c = backupClip;
    }
    c.setFramePosition(0);
    c.start();
    c.drain();
  }
}

