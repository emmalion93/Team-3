import java.awt.event.ActionListener;
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

    public GameModeButton(GameMode myGM, JPanel myTable, JFrame myFrame, int my_x_pos, int my_y_pos) {
        gameMode = myGM;
        name = myGM.getName();
        table = myTable;
        x_pos = my_x_pos;
        y_pos = my_y_pos;
        frame = myFrame;

        gameButton = new JButton(name);
        gameButton.setName(name);
        gameButton.addActionListener(new NewGameListener());

        String[] highScores = getHighScore(name).split(",");
        highScoreBox = new JTextPane();
        highScoreBox.setText("High Score \nScore: " + highScores[0] + "\n Time: " + highScores[1]);
        highScoreBox.setEditable(false);
        highScoreBox.setOpaque(false);

        checkBox= new JCheckBox();
        checkBox.addActionListener(new CheckFavoritesListener());
        checkBox.setName(name);

        gameinformationButton = new JButton("Description");
        gameinformationButton.setName(name);
        gameinformationButton.addActionListener(new ShowDescriptionListener());

        setPosition(x_pos, y_pos);
    }

    public void setPosition(int my_x_pos, int my_y_pos) {
        x_pos = my_x_pos;
        y_pos = my_y_pos;

        gameButton.setBounds(x_pos, my_y_pos, 120, 60);

        highScoreBox.setBounds(x_pos + 130, my_y_pos, 120, 60);
        
        checkBox.setBounds(x_pos - 20, my_y_pos, 20, 20);

        gameinformationButton.setBounds(x_pos + 250, my_y_pos + 5, 120, 30);
        addButtons();
    }

    private void addButtons() {
        table.add(gameButton);
        table.add(highScoreBox);
        table.add(checkBox);
        table.add(gameinformationButton);
    }

    public boolean getFavorite() { return favorite; }

    private class NewGameListener implements ActionListener
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
		}
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
}
