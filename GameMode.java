import javax.swing.JFrame;
import javax.swing.JPanel;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class GameMode {
    // Game information Variables
	protected String gameName;
	protected String gameDesc;
    protected StartMenu mainMenu;
    protected static boolean timeRunning = false;
    protected int time;
    protected int score;
    public static String cardPath = "CardImages\\original\\";

    public String getName() { return gameName; }
	public String getDesc() { return gameDesc; }
    public String getRules() { return "No Rules Provided"; }

    public void execute(JPanel myTable, JFrame myFrame, StartMenu myMenu, String myCardPath) { }

    public void playNewGame() { }
    public void startMenu() { }
    public void saveGame() { }
    public void loadGame() { }
    public void toggleTimer() { }
    public void mousePressed() { }
    public void mouseReleased() { }

    public void updateScores() {
        mainMenu.updateScores(gameName, score, time);
    }

    protected void playSound() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(this.getClass().getResource("Sounds\\mixkit-poker-card-flick-2002.wav"));
            Clip soundEffect = AudioSystem.getClip();
            soundEffect.open(audioStream);
            FloatControl volumeControl = (FloatControl) soundEffect.getControl(FloatControl.Type.MASTER_GAIN);
            int vol = StartMenu.volume;
            if(vol + 10 > 6) {
                vol = 6;
            } else {
                vol += 10;
            }
            volumeControl.setValue(vol);
            soundEffect.start();
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
}
