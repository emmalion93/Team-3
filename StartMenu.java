import java.awt.Container;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class StartMenu {

    // CONSTANTS
	public static final int TABLE_HEIGHT = Card.CARD_HEIGHT * 4;
	public static final int TABLE_WIDTH = (Card.CARD_WIDTH * 12) + 100;

    // GUI COMPONENTS (top level)
    private static final JFrame frame = new JFrame("Solitaire");
    protected static final JPanel table = new JPanel();
    // other components
    private static JEditorPane gameTitle = new JEditorPane("text/html", "");
    private static JButton showRulesButton = new JButton("Show Rules");
    private static JButton newGameButton = new JButton("New Game");
	private static JButton saveButton = new JButton("Save Game");
	private static JButton loadButton = new JButton("Load Game");
    private static JButton mainMenuButton = new JButton("Main Menu");
	private static JButton optionsButton = new JButton("Options");
    private static JButton toggleTimerButton = new JButton("Pause Timer");
    private static JTextField scoreBox = new JTextField();// displays the score
    private static JTextField timeBox = new JTextField();// displays the time
    private static JTextField statusBox = new JTextField();// status messages

	private JButton showFavoritesButton = new JButton("Show All");
	private boolean showFavorites = false;

	private List<GameMode> myGameModes = new ArrayList<GameMode>();
	private List<GameModeButton> gameModeButtons = new ArrayList<GameModeButton>();

	private Clip music;
	private int volumeMax = 6;
	private int volumeMin = -54;
	private int volumeStart = -20;
	public static int volume = -20;
	private static int musicVolume = -20;

	private class ShowFavoritesListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (showFavorites)
			{
				showFavorites();
				showFavorites = false;
			} else
			{
				showAll();
				showFavorites = true;
			}
		}
	}

	private void showFavorites() {
		table.removeAll();

		showFavoritesButton.setText("Show All");

		int count = 0;
		for (int x = 0; x < gameModeButtons.size(); x++)
		{
			if(gameModeButtons.get(x).getFavorite()) {
				int height = 40 + 90 * count;
				gameModeButtons.get(x).setPosition(20, height);
				count++;
			}
		}
		addButtons();
		table.repaint();
	}

	private void showAll() {
		table.removeAll();

		showFavoritesButton.setText("Show Favorites");
		for (int x = 0; x < gameModeButtons.size(); x++)
		{
			int height = 40 + 90 * x;
			gameModeButtons.get(x).setPosition(20, height);
		}
		addButtons();
		table.repaint();
	}

	private void addButtons() {
		

		table.add(statusBox);
		table.add(toggleTimerButton);
		table.add(gameTitle);
		table.add(timeBox);
		table.add(newGameButton);
		table.add(mainMenuButton);
		table.add(saveButton);
		table.add(loadButton);
		table.add(optionsButton);
		table.add(showRulesButton);
		table.add(scoreBox);
		table.add(showFavoritesButton);
	}

	private class optionsListener implements ActionListener
	{
		private JFrame ruleFrame = new JFrame("OPTIONS");
		private JPanel ruleTable = new JPanel();
		private JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, volumeMin, volumeMax, volumeStart);
		private JSlider musicVolumeSlider = new JSlider(JSlider.HORIZONTAL, volumeMin, volumeMax, volumeStart);
		private JEditorPane volumeText = new JEditorPane();
		private JEditorPane musicVolumeText = new JEditorPane();
		private JButton confirmButton = new JButton("Confirm");

		@Override
		public void actionPerformed(ActionEvent e)
		{
			ruleTable.removeAll();
			Container contentPane;


			ruleTable.setLayout(null);

			contentPane = ruleFrame.getContentPane();
			contentPane.add(ruleTable);

			ruleFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			ruleFrame.setSize(400, 400);

			volumeText.setText("Effects Volume: ");
			volumeText.setFont(new Font("Arial", Font.BOLD, 15));
			volumeText.setEditable(false);
			volumeText.setOpaque(false);
			volumeText.setBounds(5, 45, 120, 60);
			
			volumeSlider.setName("volumeSlider");
			volumeSlider.addChangeListener(new SliderListener());
			volumeSlider.setMajorTickSpacing(10);
			volumeSlider.setBounds(115, 50, 260, 30);
			volumeSlider.setPaintTicks(true);
			volumeSlider.setOpaque(false);

			musicVolumeText.setText("Music Volume: ");
			musicVolumeText.setFont(new Font("Arial", Font.BOLD, 15));
			musicVolumeText.setEditable(false);
			musicVolumeText.setOpaque(false);
			musicVolumeText.setBounds(5, 95, 120, 60);
			
			musicVolumeSlider.setName("musicSlider");
			musicVolumeSlider.addChangeListener(new SliderListener());
			musicVolumeSlider.setMajorTickSpacing(10);
			musicVolumeSlider.setBounds(115, 100, 260, 30);
			musicVolumeSlider.setPaintTicks(true);
			musicVolumeSlider.setOpaque(false);


			
			confirmButton.setBounds(135, 330, 130, 30);
			confirmButton.addActionListener(new confirmOptionsListener());
			confirmButton.setEnabled(true);

			ruleTable.add(confirmButton);
			ruleTable.add(volumeSlider);
			ruleTable.add(volumeText);
			ruleTable.add(musicVolumeSlider);
			ruleTable.add(musicVolumeText);

			ruleTable.setVisible(true);
			ruleFrame.setVisible(true);
		}

		private class confirmOptionsListener implements ActionListener
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				
				FloatControl volumeControl = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
				volumeControl.setValue(musicVolume);
				ruleFrame.dispose();
			}
		}

		private class SliderListener implements ChangeListener
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				JSlider slider = (JSlider) e.getSource();
				if(slider.getName() == "musicSlider") {
					musicVolume = (int)slider.getValue();
				} else {
					volume = (int)slider.getValue();
				}
			}
		}
	}

	private void startMusic() {
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(this.getClass().getResource("Sounds\\Loop_The_Old_Tower_Inn.wav"));
			music = AudioSystem.getClip();
			music.open(audioStream);
			music.loop(Clip.LOOP_CONTINUOUSLY);
			FloatControl volumeControl = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
			volumeControl.setValue(musicVolume);
			music.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void playMainMenu()
	{
		startMusic();

		table.removeAll();

		myGameModes.add(new FlowerBed());
		myGameModes.add(new Solitaire());

		int count = 0;
		for (int x = 0; x < myGameModes.size(); x++)
		{
			gameModeButtons.add(new GameModeButton(myGameModes.get(x), table, frame));
			if(gameModeButtons.get(x).getFavorite()) {
				int height = 40 + 90 * count;
				gameModeButtons.get(x).setPosition(20, height);
				count++;
			}
		}

		showFavoritesButton.setBounds(420, 30, 150, 30);
		showFavoritesButton.addActionListener(new ShowFavoritesListener());
		showFavorites();
		
		mainMenuButton.setBounds(0, TABLE_HEIGHT - 70, 120, 30);
		mainMenuButton.setEnabled(false);
		
		newGameButton.setBounds(120, TABLE_HEIGHT - 70, 120, 30);
		newGameButton.setEnabled(false);

		showRulesButton.setBounds(240, TABLE_HEIGHT - 70, 120, 30);
		showRulesButton.setEnabled(false);

		gameTitle.setText("<b>Team Three's Solitaire</b> <br> CPSC 4900 <br> Fall 2021");
		gameTitle.setEditable(false);
		gameTitle.setOpaque(false);
		gameTitle.setBounds(775, 20, 100, 100);

		scoreBox.setBounds(360, TABLE_HEIGHT - 70, 120, 30);
		scoreBox.setText("Score: 0");
		scoreBox.setEditable(false);
		scoreBox.setOpaque(false);

		timeBox.setBounds(480, TABLE_HEIGHT - 70, 120, 30);
		timeBox.setText("Seconds: 0");
		timeBox.setEditable(false);
		timeBox.setOpaque(false);

		toggleTimerButton.setBounds(600, TABLE_HEIGHT - 70, 125, 30);
		toggleTimerButton.setEnabled(false);

		statusBox.setBounds(725, TABLE_HEIGHT - 70, 180, 30);
		statusBox.setEditable(false);
		statusBox.setOpaque(false);

		saveButton.setBounds(905, TABLE_HEIGHT - 70, 125, 30);
		saveButton.setEnabled(false);

		loadButton.setBounds(1030, TABLE_HEIGHT - 70, 125, 30);
		loadButton.setEnabled(false);

		optionsButton.setBounds(1155, TABLE_HEIGHT - 70, 130, 30);
		optionsButton.addActionListener(new optionsListener());
		optionsButton.setEnabled(true);

		addButtons();
		table.repaint();
	}

	public static void execute() {
		Container contentPane;

		frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);

		table.setLayout(null);
		table.setBackground(new Color(0, 180, 0));

		contentPane = frame.getContentPane();
		contentPane.add(table);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		StartMenu menu = new StartMenu();
        menu.playMainMenu();

		frame.setVisible(true);
	}


	public static void main(String[] args)
	{
		execute();
	}
}
