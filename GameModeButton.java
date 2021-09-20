import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;


public class GameModeButton {

    private GameMode gameMode;
    private String name;
    private JPanel table;
    private JFrame frame;
    private JButton gameButton;
    private JTextPane highScoreBox;
    private JCheckBox checkBox;
    private JButton gameinformationButton;
    private int x_pos;
    private int y_pos;
    private boolean favorite;

    public GameModeButton(GameMode myGM, JPanel myTable, JFrame myFrame) {
        gameMode = myGM;
        name = myGM.getName();
        table = myTable;

        frame = myFrame;

        gameButton = new JButton(name);
        gameButton.setName(name);
        gameButton.addActionListener(new ChooseGameListener());

        String[] highScores = getGameInformation(name).split(",");
        highScoreBox = new JTextPane();
        highScoreBox.setText("Score: " + highScores[0] + "\n Time: " + highScores[1]);
        highScoreBox.setEditable(false);
		highScoreBox.setBackground(Color.GREEN);
		highScoreBox.setFont(new Font("Arial", Font.PLAIN, 12));

        checkBox= new JCheckBox();
        checkBox.addActionListener(new CheckFavoritesListener());
        checkBox.setName(name);
		checkBox.setBackground(Color.GREEN);
		checkBox.setSelected(favorite);

        gameinformationButton = new JButton("?");
        gameinformationButton.setName(name);
        gameinformationButton.addActionListener(new ShowDescriptionListener());
		gameinformationButton.setMargin(new Insets(1,1,1,1));
		gameinformationButton.setFont(new Font("Arial", Font.BOLD, 15));

    }

    public void setPosition(int my_x_pos, int my_y_pos) {
        x_pos = my_x_pos;
        y_pos = my_y_pos;

        gameButton.setBounds(x_pos, my_y_pos, 120, 60);

        highScoreBox.setBounds(x_pos + 21, my_y_pos + 60, 80, 30);
        
        checkBox.setBounds(x_pos + 1, my_y_pos + 60, 20, 30);

        gameinformationButton.setBounds(x_pos + 100, my_y_pos + 60, 20, 30);
        addButtons();
    }

    private void addButtons() {
		table.add(checkBox);
        table.add(gameButton);
        table.add(highScoreBox);
        table.add(gameinformationButton);
    }

    public boolean getFavorite() { return favorite; }

    private class ChooseGameListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
            gameMode.execute(table ,frame);
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

			
				rulesText = gameMode.getDesc();

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
				favorite = true;
			} else {
				favorite = false;
			}
			updateFavoriteInformation();
		}
	}

    private String getGameInformation(String name) {
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
				if(games.get(1).equals("0")) {
					favorite = false;
				} else {
					favorite = true;
				}
				highScores = games.get(2);
				break;
			}
		}

		return highScores;
	}

	private void updateFavoriteInformation() {
		BufferedReader reader;
		List<String> lines = new ArrayList<String>();
		String savedLines = "";

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
				if(favorite) {
					games.set(1, "1");
				} else {
					games.set(1, "0");
				}
				savedLines = savedLines + games.get(0) + ":" + games.get(1) + ":" + games.get(2) + "\n";
			} else {
				savedLines = savedLines + lines.get(x) + "\n";
			}
		}



		try {
			PrintWriter writer = new PrintWriter("SavedScores.txt");
			writer.print(savedLines);
			writer.close();
		} catch(IOException i) {
			i.printStackTrace();
		}
	}
}
