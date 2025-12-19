import javax.swing.Timer;
import javax.sound.midi.*;

public class MidiSynthSystem {
    private static Timer bgmTimer;
    private static MidiChannel[] channels;
    private static Synthesizer synth;

    public static void init() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();

            // Channel 0: BGM (Space Voice / Pad)
            channels[0].programChange(91); // 91 = Pad 3 (polysynth)
            
            // Channel 1: SFX (Retro Lead)
            channels[1].programChange(81); // 81 = Lead 2 (sawtooth)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startBGM() {
        // C Minor Pentatonic Scale
        int[] scale = {48, 51, 53, 55, 58, 60, 63, 65}; 
        
        bgmTimer = new Timer(4000, e -> { 
            if (channels == null) return;
            channels[0].allNotesOff();
            int note = scale[(int)(Math.random() * scale.length)];
            int velocity = 40; 
            channels[0].noteOn(note, velocity);
            channels[0].noteOn(note + 4, velocity); 
        });
        bgmTimer.setInitialDelay(0);
        bgmTimer.start();
    }

    public static void stopBGM() {
        if (bgmTimer != null) bgmTimer.stop();
        if (channels != null) channels[0].allNotesOff();
    }

    public static void playSFX(String type) {
        if (channels == null) return;
        new Thread(() -> {
            try {
                switch (type) {
                    case "roll":
                        for (int i = 0; i < 5; i++) {
                            channels[1].noteOn(70 + (i * 2), 80);
                            Thread.sleep(30);
                            channels[1].noteOff(70 + (i * 2));
                        }
                        break;
                    case "move":
                        channels[1].noteOn(40, 100); 
                        Thread.sleep(50);
                        channels[1].noteOff(40);
                        break;
                    case "warp":
                        for (int i = 0; i < 10; i++) {
                            channels[1].noteOn(60 + i, 90);
                            Thread.sleep(20);
                            channels[1].noteOff(60 + i);
                        }
                        break;
                    case "score":
                        channels[1].noteOn(84, 100); 
                        Thread.sleep(100);
                        channels[1].noteOn(88, 100); 
                        Thread.sleep(200);
                        channels[1].noteOff(84);
                        channels[1].noteOff(88);
                        break;
                    case "win":
                        int[] winNotes = {60, 64, 67, 72, 76, 84};
                        for (int n : winNotes) {
                            channels[1].noteOn(n, 100);
                            Thread.sleep(100);
                        }
                        Thread.sleep(1000);
                        channels[1].allNotesOff();
                        break;
                }
            } catch (InterruptedException ex) {}
        }).start();
    }
}
