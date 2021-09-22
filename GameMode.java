import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.MouseEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class GameMode {
    // Game information Variables
	protected String gameName;
	protected String gameDesc;
    protected String gameRules = "No Rules Provided"; 
    protected StartMenu mainMenu;
    protected int time;
    protected int score;
    public static String cardPath = "CardImages/greywyvern-cardset/";

    public String getName() { return gameName; }
	public String getDesc() { return gameDesc; }
    public String getRules() { return gameRules; }

    public void execute(JPanel myTable, JFrame myFrame, StartMenu myMenu, String myCardPath) { }

    public void newGame() { }
    public void saveGame() { }
    public void loadGame() { }
    public void refreshCards() { }
    public void undo() { }
    public void redo() { }
    public void updateTimer() { }
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseMoved(MouseEvent e) { }

    public void startMenu() {
        updateScores();
		score = 0;
		time = 0;
		mainMenu.returnToMenu();
     }
    public void updateScores() {
        mainMenu.updateScores(gameName, score, time, false);
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
