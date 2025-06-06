package Utility;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class SoundUtil {
    private static boolean muted = false;

    public static void setMuted(boolean mute) {
        muted = mute;
    }

    public static boolean isMuted() {
        return muted;
    }

    public static void playSound(String soundFileName) {
        if (muted) {
            return;
        }
        try {
            System.out.println("Attempting to play sound: " + soundFileName);

            String resourcePath = soundFileName.startsWith("/") ? soundFileName : "/" + soundFileName;

            // Method 1: Try getResource (preferred for resources folder)
            URL soundURL = SoundUtil.class.getResource(resourcePath);
            if (soundURL != null) {
                System.out.println("Found sound file at: " + soundURL);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
                playAudioStream(audioInputStream, soundFileName);
                return;
            }

            // Method 2: Try getResourceAsStream
            InputStream audioSrc = SoundUtil.class.getResourceAsStream(resourcePath);
            if (audioSrc != null) {
                System.out.println("Found sound file via InputStream");
                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
                playAudioStream(audioInputStream, soundFileName);
                return;
            }

            // Method 3: Try from current directory (for development fallback)
            File soundFile = new File(soundFileName);
            if (soundFile.exists()) {
                System.out.println("Found sound file in filesystem: " + soundFile.getAbsolutePath());
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                playAudioStream(audioInputStream, soundFileName);
                return;
            }

            // Method 4: Try from resources directory (project root/resources)
            File resourceDirFile = new File("resources", soundFileName.replaceFirst("^/", ""));
            if (resourceDirFile.exists()) {
                System.out.println("Found sound file in resources directory: " + resourceDirFile.getAbsolutePath());
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resourceDirFile);
                playAudioStream(audioInputStream, soundFileName);
                return;
            }

            // If all methods fail
            System.err.println("Sound file not found: " + soundFileName);
            System.err.println("Tried:");
            System.err.println("1. getResource(\"" + resourcePath + "\")");
            System.err.println("2. getResourceAsStream(\"" + resourcePath + "\")");
            System.err.println("3. File system: " + soundFile.getAbsolutePath());
            System.err.println("4. resources dir: " + resourceDirFile.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void playAudioStream(AudioInputStream audioInputStream, String fileName) throws Exception {
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.start();
        System.out.println("Successfully playing sound: " + fileName);
    }
}
