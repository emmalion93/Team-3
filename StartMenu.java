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
    private static final JFrame frame = new JFrame("Klondike Solitaire");
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

	private Vector<JButton> gameButtons = new Vector<JButton>();
	private Vector<JButton> gameinformationButtons = new Vector<JButton>();
	private Vector<JTextPane> highScoreBox = new Vector<JTextPane>();

	private Vector<JCheckBox> checkBoxes = new Vector<JCheckBox>();
	private JButton showFavoritesButton = new JButton("Show Favorites");
	private List<String> myFavorites = new ArrayList<String>();

	private boolean showFavorites = true;

    // BUTTON LISTENERS
	private static class NewGameListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton) e.getSource();
			frame.setVisible(false);
			if(button.getName() == "Flower Bed") {
				FlowerBed fb = new FlowerBed();
				fb.execute();
				frame.dispose();
			} else if(button.getName() == "Klondike") {
				Solitaire.execute();
				frame.dispose();
			} else {
				frame.setVisible(true);
			}
		}

	}

	private class ShowDescriptionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton) e.getSource();

			JDialog ruleFrame = new JDialog(frame, true);
			ruleFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			ruleFrame.setSize(400, 100);
			JScrollPane scroll;
			JEditorPane rulesTextPane = new JEditorPane("text/html", "");
			rulesTextPane.setEditable(false);
			String rulesText;

			if(button.getName() == "Flower Bed") {
				rulesText = FlowerBed.getDesc();
			} else {
				rulesText = "None listed currently";
			}

			rulesTextPane.setText(rulesText);
			ruleFrame.add(scroll = new JScrollPane(rulesTextPane));

			ruleFrame.setVisible(true);

		}
	}

	private class CheckFavoritesListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JCheckBox myBox = (JCheckBox) e.getSource();
			if( myBox.isSelected()) {
				if(!myFavorites.contains(myBox.getName())) {
					myFavorites.add(myBox.getName());
				}
			} else {
				if(myFavorites.contains(myBox.getName())) {
					myFavorites.remove(myBox.getName());
				}
			}
		}
	}

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
		for (int x = 0; x < gameButtons.size(); x++)
		{
			if(myFavorites.contains(gameButtons.get(x).getName())) {
				int height = 40 + 70 * count;
				gameButtons.get(x).setBounds(20, height, 120, 60);

				String[] highScores = getHighScore(gameButtons.get(x).getName()).split(",");
				highScoreBox.add(new JTextPane());
				highScoreBox.get(x).setText("High Score \nScore: " + highScores[0] + "\n Time: " + highScores[1]);
				highScoreBox.get(x).setBounds(150, height, 120, 60);
				highScoreBox.get(x).setEditable(false);
				highScoreBox.get(x).setOpaque(false);

				checkBoxes.get(x).setBounds(0, height, 20, 20);

				gameinformationButtons.get(x).setBounds(270, height + 5, 120, 30);

				table.add(gameButtons.get(x));
				table.add(highScoreBox.get(x));
				table.add(checkBoxes.get(x));
				table.add(gameinformationButtons.get(x));

				count++;
			}
		}
		addButtons();
		table.repaint();
	}

	private void showAll() {
		table.removeAll();

		showFavoritesButton.setText("Show Favorites");
		for (int x = 0; x < gameButtons.size(); x++)
		{
			int height = 40 + 70 * x;
			gameButtons.get(x).setBounds(20, height, 120, 60);

			String[] highScores = getHighScore(gameButtons.get(x).getName()).split(",");
			highScoreBox.get(x).setText("High Score \nScore: " + highScores[0] + "\n Time: " + highScores[1]);
			highScoreBox.get(x).setBounds(150, height, 120, 60);
			highScoreBox.get(x).setEditable(false);
			highScoreBox.get(x).setOpaque(false);

			checkBoxes.get(x).setBounds(0, height, 20, 20);

			gameinformationButtons.get(x).setBounds(270, height + 5, 120, 30);

			table.add(gameButtons.get(x));
			table.add(highScoreBox.get(x));
			table.add(checkBoxes.get(x));
			table.add(gameinformationButtons.get(x));
		}
		addButtons();
		table.repaint();
	}

	private String getHighScore(String name) {
		BufferedReader reader;
		List<String> lines = new ArrayList<String>();
		String highScores = "-,-";

		try {
			reader = new BufferedReader(new FileReader("SavedScores.txt"));
			while(reader.ready()) {
				lines.add(reader.readLine());
			}
			reader.close();
		} catch(IOException i) {
			i.printStackTrace();
		}

		for(int x = 0; x < lines.size(); x++)
		{
			List<String> games = Arrays.asList(lines.get(x).split(":"));
			if(name.equals(games.get(0))) {

				highScores = games.get(1);
				break;
			}
		}

		return highScores;
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

		gameButtons.add(new JButton(FlowerBed.getName()));
		gameButtons.add(new JButton(Solitaire.getName()));

		//TODO: create these as a single game object(like a panel) instead of individuals
		for (int x = 0; x < gameButtons.size(); x++)
		{
			gameButtons.get(x).setName(gameButtons.get(x).getText());
			gameButtons.get(x).addActionListener(new NewGameListener());
			int height = 40 + 70 * x;
			gameButtons.get(x).setBounds(20, height, 120, 60);

			String[] highScores = getHighScore(gameButtons.get(x).getName()).split(",");
			highScoreBox.add(new JTextPane());
			highScoreBox.get(x).setText("High Score \nScore: " + highScores[0] + "\n Time: " + highScores[1]);
			highScoreBox.get(x).setBounds(150, height, 120, 60);
			highScoreBox.get(x).setEditable(false);
			highScoreBox.get(x).setOpaque(false);

			checkBoxes.add(new JCheckBox());
			checkBoxes.get(x).addActionListener(new CheckFavoritesListener());
			checkBoxes.get(x).setName(gameButtons.get(x).getName());
			checkBoxes.get(x).setBounds(0, height, 20, 20);

			gameinformationButtons.add(new JButton("Description"));
			gameinformationButtons.get(x).setName(gameButtons.get(x).getName());
			gameinformationButtons.get(x).addActionListener(new ShowDescriptionListener());
			gameinformationButtons.get(x).setBounds(270, height + 5, 120, 30);

			table.add(gameButtons.get(x));
			table.add(highScoreBox.get(x));
			table.add(checkBoxes.get(x));
			table.add(gameinformationButtons.get(x));
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
