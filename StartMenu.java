import java.awt.Container;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.IOException;

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

	private JButton showFavoritesButton = new JButton("Show Favorites");
	private boolean showFavorites = true;

	private List<GameMode> myGameModes = new ArrayList<GameMode>();
	private List<GameModeButton> gameModeButtons = new ArrayList<GameModeButton>();

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
				int height = 40 + 70 * count;
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
			int height = 40 + 70 * x;
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

    private void playMainMenu()
	{
		table.removeAll();

		myGameModes.add(new FlowerBed());
		myGameModes.add(new Solitaire());

		for (int x = 0; x < myGameModes.size(); x++)
		{
			gameModeButtons.add(new GameModeButton(myGameModes.get(x), table, frame, 20, 40 + 70 * x));
		}

		showFavoritesButton.setBounds(420, 30, 150, 30);
		showFavoritesButton.addActionListener(new ShowFavoritesListener());

		
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
		optionsButton.setEnabled(false);

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
