import javax.sound.sampled.FloatControl;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Container;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MenuButtons {

    public static enum Direction
	{
		LEFT, RIGHT, UP, DOWN;
	}

    private Direction _Direction = Direction.UP;

    private JFrame frame;
	protected JPanel table;

    private GameMode currentGameMode;

    private  JEditorPane gameTitle = new JEditorPane("text/html", "");
    private JButton showRulesButton = new JButton("Show Rules");
	private  JButton newGameButton = new JButton("New Game");
	private  JButton mainMenuButton = new JButton("Main Menu");
	private  JButton toggleTimerButton = new JButton("Pause Timer");
	private  JButton saveButton = new JButton("Save");
	private  JButton loadButton = new JButton("Load");
	private  JButton optionsButton = new JButton("Options");
	private  JTextField scoreBox = new JTextField();// displays the score
	private  JTextField timeBox = new JTextField();// displays the time
	private  JTextField statusBox = new JTextField();// status messages

	private ScoreClock scoreClock;

    public MenuButtons(JPanel myTable, JFrame myFrame) {
        table = myTable;
        frame = myFrame;
		//startTimer();
    }

    public void setGameMode(GameMode myGameMode) {
        currentGameMode = myGameMode;
    }

    // BUTTON LISTENERS
	private class NewGameListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
            table.removeAll();
            addButtons();
			currentGameMode.playNewGame();
		}

	}

	private class MainMenuListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			currentGameMode.startMenu();
		}
	}

	private class SaveGameListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			currentGameMode.saveGame();
		}
	}

	private class LoadGameListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			currentGameMode.loadGame();
		}
	}

    private class ToggleTimerListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
            if (!currentGameMode.timeRunning)
			{
				toggleTimerButton.setText("Start Timer");
			} else
			{
				toggleTimerButton.setText("Pause Timer");
			}
			currentGameMode.toggleTimer();
		}
	}

	protected void updateTimer()
	{
		try {
			String text = "Seconds: " + currentGameMode.time;
			String newScore = "Score: " + currentGameMode.score;
			scoreBox.setText(newScore);
			scoreBox.repaint();
			timeBox.setText(text);
			timeBox.repaint();
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
	}

	protected void startTimer()
	{
		scoreClock = new ScoreClock();
		
		// set the timer to update every second
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(scoreClock, 0, 500);
	}

	public void stopTimer()
	{
		scoreClock.cancel();
		String text = "Seconds: 0";
		String newScore = "Score: 0";
		scoreBox.setText(newScore);
		scoreBox.repaint();
		timeBox.setText(text);
		timeBox.repaint();
	}

	private class ScoreClock extends TimerTask
	{
		@Override
		public void run()
		{
			updateTimer();
		}
	}

	private class ShowRulesListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JDialog ruleFrame = new JDialog(frame, true);
			ruleFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			ruleFrame.setSize(FlowerBed.TABLE_HEIGHT, FlowerBed.TABLE_WIDTH);
			JScrollPane scroll;
			JEditorPane rulesTextPane = new JEditorPane("text/html", "");
			rulesTextPane.setEditable(false);
			String rulesText = currentGameMode.getRules();
			rulesTextPane.setText(rulesText);
			ruleFrame.add(scroll = new JScrollPane(rulesTextPane));

			ruleFrame.setVisible(true);
		}
	}

    private class optionsListener implements ActionListener
	{
		private JFrame ruleFrame = new JFrame("OPTIONS");
		private JPanel ruleTable = new JPanel();
		private JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, StartMenu.volumeMin, StartMenu.volumeMax, StartMenu.volumeStart);
		private JSlider musicVolumeSlider = new JSlider(JSlider.HORIZONTAL, StartMenu.volumeMin, StartMenu.volumeMax, StartMenu.volumeStart);
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
				
				FloatControl volumeControl = (FloatControl) StartMenu.music.getControl(FloatControl.Type.MASTER_GAIN);
				volumeControl.setValue(StartMenu.musicVolume);
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
					StartMenu.musicVolume = (int)slider.getValue();
				} else {
					StartMenu.volume = (int)slider.getValue();
				}
			}
		}
	}

	private class CardMovementManager extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e)
		{
			if(currentGameMode != null) {
				currentGameMode.mousePressed(e);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if(currentGameMode != null) {
				currentGameMode.mouseReleased(e);
			}
		}

	}


    public void generateButtons() {
        mainMenuButton.addActionListener(new MainMenuListener());

		newGameButton.addActionListener(new NewGameListener());

		showRulesButton.addActionListener(new ShowRulesListener());
		
		toggleTimerButton.addActionListener(new ToggleTimerListener());

		saveButton.addActionListener(new SaveGameListener());

		loadButton.addActionListener(new LoadGameListener());

		//mainMenuButton.setBounds(0, TABLE_HEIGHT - 70, 120, 30);

		//newGameButton.setBounds(120, TABLE_HEIGHT - 70, 120, 30);

		//showRulesButton.setBounds(240, TABLE_HEIGHT - 70, 120, 30);

		gameTitle.setText("<b>Team Three's Solitaire</b> <br> CPSC 4900 <br> Fall 2021");
		gameTitle.setEditable(false);
		gameTitle.setOpaque(false);
		//gameTitle.setBounds(775, 20, 100, 100);

		//scoreBox.setBounds(360, TABLE_HEIGHT - 70, 120, 30);
		scoreBox.setText("Score: 0");
		scoreBox.setEditable(false);
		scoreBox.setOpaque(false);

		//timeBox.setBounds(480, TABLE_HEIGHT - 70, 120, 30);
		timeBox.setText("Seconds: 0");
		timeBox.setEditable(false);
		timeBox.setOpaque(false);

		//toggleTimerButton.setBounds(600, TABLE_HEIGHT - 70, 125, 30);

		//statusBox.setBounds(725, TABLE_HEIGHT - 70, 180, 30);
		statusBox.setEditable(false);
		statusBox.setOpaque(false);

		//saveButton.setBounds(905, TABLE_HEIGHT - 70, 125, 30);

		//loadButton.setBounds(1030, TABLE_HEIGHT - 70, 125, 30);

		//optionsButton.setBounds(1155, TABLE_HEIGHT - 70, 130, 30);
		optionsButton.addActionListener(new optionsListener());
		optionsButton.setEnabled(true);

        positionButtons(_Direction);
        addButtons();
		table.addMouseListener(new CardMovementManager());
		table.addMouseMotionListener(new CardMovementManager());
    }

    public void positionButtons(Direction myDirection) {
        _Direction = myDirection;
        switch (_Direction)
		{
            case LEFT:
                break;
            case RIGHT:
                break;
            case UP:
                mainMenuButton.setBounds(0, 0, 120, 30);
                newGameButton.setBounds(120, 0, 120, 30);
                showRulesButton.setBounds(240, 0, 120, 30);
                gameTitle.setBounds(775, 35, 100, 100);
                scoreBox.setBounds(360, 0, 120, 30);
                timeBox.setBounds(480, 0, 120, 30);
                toggleTimerButton.setBounds(600, 0, 125, 30);
                statusBox.setBounds(725, 0, 180, 30);
                saveButton.setBounds(905, 0, 125, 30);
                loadButton.setBounds(1030, 0, 125, 30);
                optionsButton.setBounds(1155, 0, 130, 30);
                break;
            case DOWN:
                mainMenuButton.setBounds(0, FlowerBed.TABLE_HEIGHT - 70, 120, 30);
                newGameButton.setBounds(120, FlowerBed.TABLE_HEIGHT - 70, 120, 30);
                showRulesButton.setBounds(240, FlowerBed.TABLE_HEIGHT - 70, 120, 30);
                gameTitle.setBounds(775, 35, 100, 100);
                scoreBox.setBounds(360, FlowerBed.TABLE_HEIGHT - 70, 120, 30);
                timeBox.setBounds(480, FlowerBed.TABLE_HEIGHT - 70, 120, 30);
                toggleTimerButton.setBounds(600, FlowerBed.TABLE_HEIGHT - 70, 125, 30);
                statusBox.setBounds(725, FlowerBed.TABLE_HEIGHT - 70, 180, 30);
                saveButton.setBounds(905, FlowerBed.TABLE_HEIGHT - 70, 125, 30);
                loadButton.setBounds(1030, FlowerBed.TABLE_HEIGHT - 70, 125, 30);
                optionsButton.setBounds(1155, FlowerBed.TABLE_HEIGHT - 70, 130, 30);

                break;
		}
    }

    public void addButtons() {
		table.add(statusBox);
		table.add(toggleTimerButton);
		table.add(saveButton);
		table.add(loadButton);
		table.add(optionsButton);
		table.add(gameTitle);
		table.add(timeBox);
		table.add(mainMenuButton);
		table.add(newGameButton);
		table.add(showRulesButton);
		table.add(scoreBox);
		table.repaint();
	}

	public void disableMainMenuButtons() {
		toggleTimerButton.setEnabled(false);
		statusBox.setEnabled(false);
		saveButton.setEnabled(false);
		loadButton.setEnabled(false);
		timeBox.setEnabled(false);
		mainMenuButton.setEnabled(false);
		newGameButton.setEnabled(false);
		showRulesButton.setEnabled(false);
		scoreBox.setEnabled(false);
	}

	public void enableAllButtons() {
		toggleTimerButton.setEnabled(true);
		statusBox.setEnabled(true);
		saveButton.setEnabled(true);
		loadButton.setEnabled(true);
		optionsButton.setEnabled(true);
		timeBox.setEnabled(true);
		mainMenuButton.setEnabled(true);
		newGameButton.setEnabled(true);
		showRulesButton.setEnabled(true);
		scoreBox.setEnabled(true);
	}
}
