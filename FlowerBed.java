// created by team-3
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


public class FlowerBed extends GameMode
{
	// CONSTANTS
	public static final int TABLE_HEIGHT = Card.CARD_HEIGHT * 5;
	public static final int TABLE_WIDTH = (Card.CARD_WIDTH * 12) + 100;
	public static final int NUM_FINAL_DECKS = 4;
	public static final int NUM_PLAY_DECKS = 7;
    //public static final Point DECK_POS = new Point(5, 350);
    //public static final Point SHOW_POS = new Point(DECK_POS.x + Card.CARD_WIDTH + 5, 5);
	//public static final Point FINAL_POS = new Point(SHOW_POS.x + Card.CARD_WIDTH + 650, 5);
	public static final Point DECK_POS = new Point(5, 500);
	public static final Point SHOW_POS = new Point(DECK_POS.x + Card.CARD_WIDTH + 5, 5);
	public static final Point FINAL_POS = new Point(SHOW_POS.x + Card.CARD_WIDTH + 650, 35);
    public static final Point PLAY_POS = new Point(5, 35);

	// GUI COMPONENTS (top level)
	//private static final JFrame frame = new JFrame("Klondike Solitaire");
	//protected static final JPanel table = new JPanel();
	private JFrame frame;
	protected JPanel table = new JPanel();
	// other components
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

	// TIMER UTILITIES
	private Timer timer;
	private ScoreClock scoreClock;

	// GAMEPLAY STRUCTURES
	private static FlowerBedFinalStack[] final_cards;// Foundation Stacks
	private static FlowerBedCardStack[] playCardStack; // Tableau stacks
	private static FlowerBedCardStack deck; // populated with standard 52 card deck

	// CARD MOVEMENT
	private Card prevCard = null;// tracking card for waste stack
	private Card movedCard = null;// card moved from waste stack
	private boolean sourceIsFinalDeck = false;
	private boolean putBackOnDeck = true;// used for waste card recycling
	private boolean checkForWin = false;// should we check if game is over?
	private boolean gameOver = true;// easier to negate this than affirm it
	private Point start = null;// where mouse was clicked
	private Point stop = null;// where mouse was released
	private Card card = null; // card to be moved
	// used for moving single cards
	private FlowerBedCardStack source = null;
	private FlowerBedCardStack dest = null;
	// used for moving a stack of cards
	private FlowerBedCardStack transferStack = new FlowerBedCardStack(false);

	public FlowerBed() {
		gameName = "Flower Bed";
		gameDesc = "Move cards one at a time onto stacks regardless of suit/color to fill foundations.";
	
		gameRules = "<b>Klondike Solitaire Rules</b>"
				+ "<br><br> (From Wikipedia) Taking a shuffled standard 52-card deck of playing cards (without Jokers),"
				+ " one upturned card is dealt on the left of the playing area, then six downturned cards"
				+ " (from left to right).<p> On top of the downturned cards, an upturned card is dealt on the "
				+ "left-most downturned pile, and downturned cards on the rest until all piles have an "
				+ "upturned card. The piles should look like the figure to the right.<p>The four foundations "
				+ "(light rectangles in the upper right of the figure) are built up by suit from Ace "
				+ "(low in this game) to King, and the tableau piles can be built down by alternate colors,"
				+ " and partial or complete piles can be moved if they are built down by alternate colors also. "
				+ "Any empty piles can be filled with a King or a pile of cards with a King.<p> The point of "
				+ "the game is to build up a stack of cards starting with 2 and ending with King, all of "
				+ "the same suit. Once this is accomplished, the goal is to move this to a foundation, "
				+ "where the player has previously placed the Ace of that suit. Once the player has done this, "
				+ "they will have \"finished\" that suit- the goal being, of course, to finish all suits, "
				+ "at which time the player will have won.<br><br><b> Scoring </b><br><br>"
				+ "Moving cards directly from the Waste stack to a Foundation awards 10 points. However, "
				+ "if the card is first moved to a Tableau, and then to a Foundation, then an extra 5 points "
				+ "are received for a total of 15. Thus in order to receive a maximum score, no cards should be moved "
				+ "directly from the Waste to Foundation.<p>	Time can also play a factor in Windows Solitaire, if the Timed game option is selected. For every 10 seconds of play, 2 points are taken away."
				+ "<b><br><br>Notes On My Implementation</b><br><br>"
				+ "Drag cards to and from any stack. As long as the move is valid the card, or stack of "
				+ "cards, will be repositioned in the desired spot. The game follows the standard scoring and time"
				+ " model explained above with only one waste card shown at a time."
				+ "<p> The timer starts running as soon as "
				+ "the game begins, but it may be paused by pressing the pause button at the bottom of"
				+ "the screen. ";
	}

	// moves a card to abs location within a component
	protected static Card moveCard(Card c, int x, int y)
	{
		c.setBounds(new Rectangle(new Point(x, y), new Dimension(Card.CARD_WIDTH + 10, Card.CARD_HEIGHT + 10)));
        c.setXY(new Point(x, y));
		return c;
	}

	// add/subtract points based on gameplay actions
	protected void setScore(int deltaScore)
	{
		score += deltaScore;
		String newScore = "Score: " + score;
		scoreBox.setText(newScore);
		scoreBox.repaint();
	}

	// GAME TIMER UTILITIES
	protected void updateTimer()
	{
		time += 1;
		// every 10 seconds elapsed we take away 2 points
		if (time % 10 == 0)
		{
			setScore(-2);
		}
		String text = "Seconds: " + time;
		timeBox.setText(text);
		timeBox.repaint();
	}

	protected void startTimer()
	{
		scoreClock = new ScoreClock();
		timer = new Timer();
		// set the timer to update every second
		timer.scheduleAtFixedRate(scoreClock, 1000, 1000);
		timeRunning = true;
	}

	// the pause timer button uses this
	public void toggleTimer()
	{
		if (timeRunning && scoreClock != null)
		{
			scoreClock.cancel();
			timeRunning = false;
		} else
		{
			startTimer();
		}
	}

	private class ScoreClock extends TimerTask
	{
		@Override
		public void run()
		{
			updateTimer();
		}
	}

	// BUTTON LISTENERS

	public void startMenu() { 
		scoreClock.cancel();
		score = 0;
		time = 0;
		mainMenu.returnToMenu();
	}

	private class ToggleTimerListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			toggleTimer();
			if (!timeRunning)
			{
				toggleTimerButton.setText("Start Timer");
			} else
			{
				toggleTimerButton.setText("Pause Timer");
			}
		}
	}

	public void saveGame() {
		String cardList = "";
		for (int x = 0; x < NUM_PLAY_DECKS; x++)
		{
			Vector stack = playCardStack[x].getStack();

			for (int y = 0; y < stack.size(); y++)
			{
				Card c = (Card) stack.get(y);
				cardList = cardList + c.getSuit() + "," + c.getValue() + ";";
			}
			if(stack.size() == 0) {
				cardList = cardList + ";";
			}
			cardList = cardList + "\n";
		}
		for (int x = 0; x < NUM_FINAL_DECKS; x++)
		{
			Vector stack = final_cards[x].getStack();

			for (int y = 0; y < stack.size(); y++)
			{
				Card c = (Card) stack.get(y);
				cardList = cardList + c.getSuit() + "," + c.getValue() + ";";
			}
			if(stack.size() == 0) {
				cardList = cardList + ";";
			}
			cardList = cardList + "\n";
		}
		Vector stack = deck.reverse().getStack();

		for (int y = 0; y < stack.size(); y++)
		{
			Card c = (Card) stack.get(y);
			cardList = cardList + c.getSuit() + "," + c.getValue() + ";";
		}
		if(stack.size() == 0) {
			cardList = cardList + ";";
		}
		deck.reverse();

		cardList = cardList + "\n" + score + "\n" + time;

		mainMenu.saveGame(cardList);
	}

	public void loadGame() {
		if (playCardStack != null && final_cards != null)
		{
			for (int x = 0; x < NUM_PLAY_DECKS; x++)
			{
				playCardStack[x].makeEmpty();
			}
			for (int x = 0; x < NUM_FINAL_DECKS; x++)
			{
				final_cards[x].makeEmpty();
			}
		}
		deck.makeEmpty();

		List<String> stacks = mainMenu.loadGame();
		
		for (int x = 0; x < NUM_PLAY_DECKS; x++)
		{
			List<String> cardList = Arrays.asList(stacks.get(x).split(";"));
			for (int y = 0; y < cardList.size(); y++)
			{
				List<String> cardInfo = Arrays.asList(cardList.get(y).split(","));
				Card c = new Card(Card.Suit.valueOf(cardInfo.get(0)), Card.Value.valueOf(cardInfo.get(1)));
				//c.setImage(cardPath);
				playCardStack[x].push(c.setFaceup());
			}
		}
		for (int x = 0; x < NUM_FINAL_DECKS; x++)
		{
			List<String> cardList = Arrays.asList(stacks.get(NUM_PLAY_DECKS + x).split(";"));
			for (int y = 0; y < cardList.size(); y++)
			{
				List<String> cardInfo = Arrays.asList(cardList.get(y).split(","));
				Card c = new Card(Card.Suit.valueOf(cardInfo.get(0)), Card.Value.valueOf(cardInfo.get(1)));
				//c.setImage(cardPath);
				final_cards[x].push(c.setFaceup());
			}
			final_cards[x].repaint();
		}
		List<String> cardList = Arrays.asList(stacks.get(NUM_PLAY_DECKS + NUM_FINAL_DECKS).split(";"));
		for (int y = 0; y < cardList.size(); y++)
		{
			List<String> cardInfo = Arrays.asList(cardList.get(y).split(","));
			Card c = new Card(Card.Suit.valueOf(cardInfo.get(0)), Card.Value.valueOf(cardInfo.get(1)));
			//c.setImage(cardPath);
			deck.push(c.setFaceup());
		}
		deck.reverse();

		score = Integer.parseInt(stacks.get(NUM_PLAY_DECKS + NUM_FINAL_DECKS + 1));
		time = Integer.parseInt(stacks.get(NUM_PLAY_DECKS + NUM_FINAL_DECKS + 2));
		
		scoreBox.setText("Score: " + score);
		scoreBox.repaint();

		timeBox.setText("Seconds: " + time);
		timeBox.repaint();
	}

	/*public String getRules() {
	 	String rulesText = "<b>Klondike Solitaire Rules</b>"
				+ "<br><br> (From Wikipedia) Taking a shuffled standard 52-card deck of playing cards (without Jokers),"
				+ " one upturned card is dealt on the left of the playing area, then six downturned cards"
				+ " (from left to right).<p> On top of the downturned cards, an upturned card is dealt on the "
				+ "left-most downturned pile, and downturned cards on the rest until all piles have an "
				+ "upturned card. The piles should look like the figure to the right.<p>The four foundations "
				+ "(light rectangles in the upper right of the figure) are built up by suit from Ace "
				+ "(low in this game) to King, and the tableau piles can be built down by alternate colors,"
				+ " and partial or complete piles can be moved if they are built down by alternate colors also. "
				+ "Any empty piles can be filled with a King or a pile of cards with a King.<p> The point of "
				+ "the game is to build up a stack of cards starting with 2 and ending with King, all of "
				+ "the same suit. Once this is accomplished, the goal is to move this to a foundation, "
				+ "where the player has previously placed the Ace of that suit. Once the player has done this, "
				+ "they will have \"finished\" that suit- the goal being, of course, to finish all suits, "
				+ "at which time the player will have won.<br><br><b> Scoring </b><br><br>"
				+ "Moving cards directly from the Waste stack to a Foundation awards 10 points. However, "
				+ "if the card is first moved to a Tableau, and then to a Foundation, then an extra 5 points "
				+ "are received for a total of 15. Thus in order to receive a maximum score, no cards should be moved "
				+ "directly from the Waste to Foundation.<p>	Time can also play a factor in Windows Solitaire, if the Timed game option is selected. For every 10 seconds of play, 2 points are taken away."
				+ "<b><br><br>Notes On My Implementation</b><br><br>"
				+ "Drag cards to and from any stack. As long as the move is valid the card, or stack of "
				+ "cards, will be repositioned in the desired spot. The game follows the standard scoring and time"
				+ " model explained above with only one waste card shown at a time."
				+ "<p> The timer starts running as soon as "
				+ "the game begins, but it may be paused by pressing the pause button at the bottom of"
				+ "the screen. ";
		return rulesText;
	}*/

	private boolean validPlayStackMove(Card source, Card dest)
	{
		int s_val = source.getValue().ordinal();
		int d_val = dest.getValue().ordinal();
		Card.Suit s_suit = source.getSuit();
		Card.Suit d_suit = dest.getSuit();

		// destination card should be one higher value
		if ((s_val + 1) == d_val)
		{
			return true;
		} else
			return false;
	}

	private boolean validFinalStackMove(Card source, Card dest)
	{
		int s_val = source.getValue().ordinal();
		int d_val = dest.getValue().ordinal();
		Card.Suit s_suit = source.getSuit();
		Card.Suit d_suit = dest.getSuit();
		if (s_val == (d_val + 1)) // destination must one lower
		{
			if (s_suit == d_suit)
				return true;
			else
				return false;
		} else
			return false;
	}

	public Card getTop(Vector stack, Point p) {
		Card c = null;
		for (int x = stack.size() - 1; x >= 0; x--)
		{
			Card temp = (Card) stack.get(x);
			temp.getSuit();
			if(temp.contains(p)) {
				c = temp;
			}
		}
		return c;
	}

	public void mousePressed(MouseEvent e)
	{
		  prevCard = null;// tracking card for waste stack
		  movedCard = null;// card moved from waste stack
		  sourceIsFinalDeck = false;
		  putBackOnDeck = true;// used for waste card recycling
		  checkForWin = false;// should we check if game is over?
		  gameOver = true;// easier to negate this than affirm it
		  start = null;// where mouse was clicked
		  stop = null;// where mouse was released
		  card = null; // card to be moved
		// used for moving single cards
		  source = null;
		  dest = null;
		// used for moving a stack of cards
		  transferStack = new FlowerBedCardStack(false);

		System.out.println("mouse");
		start = e.getPoint();
		boolean stopSearch = false;
		statusBox.setText("");
		transferStack.makeEmpty();

		/*
			* Here we use transferStack to temporarily hold all the cards above
			* the selected card in case player wants to move a stack rather
			* than a single card
			*/
		for (int x = 0; x < NUM_PLAY_DECKS; x++)
		{
			if (stopSearch)
				break;
			source = playCardStack[x];
			// pinpointing exact card pressed-
			if(source.contains(start) && source.showSize() > 0) {
				Card c = (Card) source.getStack().get(0);
				if(c.contains(start)) {
					transferStack.putFirst(c);
					card = c;
					stopSearch = true;
					System.out.println("Transfer Size: " + transferStack.showSize());
				}
			}
		}

		if(card == null) {
			source = deck;
			// pinpointing exact card pressed
			Vector stack = source.getStack();
			for (int x = 0; x < source.showSize(); x++)
			{
				transferStack.makeEmpty();
				Card c = (Card) stack.get(x);
				if(source.contains(start) && c == getTop(source.getStack(), start)) {
					transferStack.putFirst(c);
					card = c;
					stopSearch = true;
					System.out.println("Transfer Size: " + transferStack.showSize());
					break;
				}                                    
			}
		}
					
		
		

		// FINAL (FOUNDATION) CARD OPERATIONS
		for (int x = 0; x < NUM_FINAL_DECKS; x++)
		{
			if (final_cards[x].contains(start))
			{
				source = final_cards[x];
				card = source.getLast();
				transferStack.putFirst(card);
				sourceIsFinalDeck = true;
				break;
			}
		}
		putBackOnDeck = true;

	}

	public void mouseReleased(MouseEvent e)
	{
		stop = e.getPoint();
		// used for status bar updates
		boolean validMoveMade = false;

		// SHOW CARD MOVEMENTS
		if (movedCard != null)
		{
			// Moving from SHOW TO PLAY
			for (int x = 0; x < NUM_PLAY_DECKS; x++)
			{
				dest = playCardStack[x];
				// to empty play stack, only kings can go
				if (dest.empty() && movedCard != null && dest.contains(stop)
						&& movedCard.getValue() == Card.Value.KING)
				{
					System.out.print("moving new card to empty spot ");
					movedCard.setXY(dest.getXY());
					table.remove(prevCard);
					dest.putFirst(movedCard);
					table.repaint();
					movedCard = null;
					putBackOnDeck = false;
					setScore(5);
					System.out.println("-3");
					validMoveMade = true;
					break;
				}
				// this moves stuff from the deck out on the field automatically
				// to populated play stack
				/*if (movedCard != null && dest.contains(stop) && !dest.empty() && dest.getFirst().getFaceStatus()
						&& validPlayStackMove(movedCard, dest.getFirst()))
				{
					System.out.print("moving new card ");
					movedCard.setXY(dest.getFirst().getXY());
					table.remove(prevCard);
					dest.putFirst(movedCard);
					table.repaint();
					movedCard = null;
					putBackOnDeck = false;
					setScore(5);
					validMoveMade = true;
					break;
				}*/
			}
			// Moving from SHOW TO FINAL
			for (int x = 0; x < NUM_FINAL_DECKS; x++)
			{
				dest = final_cards[x];
				// only aces can go first
				if (dest.empty() && dest.contains(stop))
				{
					if (movedCard.getValue() == Card.Value.ACE)
					{
						dest.push(movedCard);
						table.remove(prevCard);
						dest.repaint();
						table.repaint();
						movedCard = null;
						putBackOnDeck = false;
						setScore(10);
						System.out.println("-2");
						validMoveMade = true;
						break;
					}
				}
				if (!dest.empty() && dest.contains(stop) && validFinalStackMove(movedCard, dest.getLast()))
				{
					System.out.println("Destin" + dest.showSize());
					dest.push(movedCard);
					table.remove(prevCard);
					dest.repaint();
					table.repaint();
					movedCard = null;
					putBackOnDeck = false;
					checkForWin = true;
					setScore(10);
					System.out.println("-1");
					validMoveMade = true;
					break;
				}
			}
		}// END SHOW STACK OPERATIONS

		// PLAY STACK OPERATIONS
		if (card != null && source != null)
		{ // Moving from PLAY TO PLAY
			for (int x = 0; x < NUM_PLAY_DECKS; x++)
			{
				dest = playCardStack[x];
				// MOVING TO POPULATED STACK
				if (card.getFaceStatus() == true && dest.contains(stop) && source != dest && !dest.empty()
						&& validPlayStackMove(card, dest.getFirst()) && transferStack.showSize() == 1)
				{
					Card c = card;
					source.removeCard(card);


					c.repaint();
					// if playstack, turn next card up
					if (source.getFirst() != null)
					{
						Card temp = source.getFirst().setFaceup();
						temp.repaint();
						source.repaint();
					}

					dest.setXY(dest.getXY().x, dest.getXY().y);
					dest.putFirst(c);

					dest.repaint();

					table.repaint();

					System.out.print("Destination ");
					dest.showSize();
					if (sourceIsFinalDeck)
						setScore(15);
					else
						setScore(10);
					System.out.println("0");
					validMoveMade = true;
					break;
				} else if (dest.empty() && transferStack.showSize() == 1 && dest.contains(stop))
				{// MOVING TO EMPTY STACK, ONLY KING ALLOWED
					Card c = card;
					source.removeCard(card);


					c.repaint();
					// if playstack, turn next card up
					if (source.getFirst() != null)
					{
						Card temp = source.getFirst().setFaceup();
						temp.repaint();
						source.repaint();
					}

					dest.setXY(dest.getXY().x, dest.getXY().y);
					dest.putFirst(c);

					dest.repaint();

					table.repaint();

					System.out.print("Destination ");
					dest.showSize();
					setScore(5);
					System.out.println("1");
					validMoveMade = true;
					break;
				}
			}
			// from PLAY TO FINAL
			for (int x = 0; x < NUM_FINAL_DECKS; x++)
			{
				dest = final_cards[x];

				if (card.getFaceStatus() == true && source != null && dest.contains(stop) && source != dest)
				{// TO EMPTY STACK
					if (dest.empty())// empty final should only take an ACE
					{
						if (card.getValue() == Card.Value.ACE)
						{
							Card c = card;
							source.removeCard(card);
							c.repaint();
							if (source.getFirst() != null)
							{

								Card temp = source.getFirst().setFaceup();
								temp.repaint();
								source.repaint();
							}

							dest.setXY(dest.getXY().x, dest.getXY().y);
							dest.push(c);

							dest.repaint();

							table.repaint();

							System.out.print("Destination ");
							dest.showSize();
							card = null;
							setScore(10);
							System.out.println("2");
							validMoveMade = true;
							break;
						}// TO POPULATED STACK
					} else if (validFinalStackMove(card, dest.getLast()))
					{
						Card c = card;
						source.removeCard(card);
						c.repaint();
						if (source.getFirst() != null)
						{

							Card temp = source.getFirst().setFaceup();
							temp.repaint();
							source.repaint();
						}

						dest.setXY(dest.getXY().x, dest.getXY().y);
						dest.push(c);

						dest.repaint();

						table.repaint();

						System.out.print("Destination ");
						dest.showSize();
						card = null;
						checkForWin = true;
						setScore(10);
						System.out.println("3");
						validMoveMade = true;
						break;
					}
				}

			}
		}// end cycle through play decks

		// SHOWING STATUS MESSAGE IF MOVE INVALID
		if (!validMoveMade && dest != null && card != null)
		{
			statusBox.setText("That Is Not A Valid Move");
		} else if(validMoveMade) {
			playSound();
		}
		// CHECKING FOR WIN
		if (checkForWin)
		{
			boolean gameNotOver = false;
			// cycle through final decks, if they're all full then game over
			for (int x = 0; x < NUM_FINAL_DECKS; x++)
			{
				dest = final_cards[x];
				if (dest.showSize() != 13)
				{
					// one deck is not full, so game is not over
					gameNotOver = true;
					break;
				}
			}
			if (!gameNotOver)
				gameOver = true;
		}

		if (checkForWin && gameOver)
		{
			updateScores();
			JOptionPane.showMessageDialog(table, "Congratulations! You've Won!");
			statusBox.setText("Game Over!");
		}
		// RESET VARIABLES FOR NEXT EVENT
		start = null;
		stop = null;
		source = null;
		dest = null;
		card = null;
		sourceIsFinalDeck = false;
		checkForWin = false;
		gameOver = false;
	}// end mousePressed()

	public void playNewGame()
	{
		score = 0;
		time = 0;
		deck = new FlowerBedCardStack(true); // deal 52 cards
		deck.shuffle();
		//table.removeAll();
        
		// reset stacks if user starts a new game in the middle of one
		if (playCardStack != null && final_cards != null)
		{
			for (int x = 0; x < NUM_PLAY_DECKS; x++)
			{
				playCardStack[x].makeEmpty();
			}
			for (int x = 0; x < NUM_FINAL_DECKS; x++)
			{
				final_cards[x].makeEmpty();
			}
		}
		// initialize & place final (foundation) decks/stacks
		final_cards = new FlowerBedFinalStack[NUM_FINAL_DECKS];
		for (int x = 0; x < NUM_FINAL_DECKS; x++)
		{
			final_cards[x] = new FlowerBedFinalStack();

			final_cards[x].setXY((FINAL_POS.x + (x * Card.CARD_WIDTH)) + 10, FINAL_POS.y);
			table.add(final_cards[x]);

		}
		// place new card distribution button
		// initialize & place play (tableau) decks/stacks
		playCardStack = new FlowerBedCardStack[NUM_PLAY_DECKS];
		for (int x = 0; x < NUM_PLAY_DECKS; x++)
		{
			playCardStack[x] = new FlowerBedCardStack(false);
            playCardStack[x].setXY((DECK_POS.x + (x * (Card.CARD_WIDTH + 10))), PLAY_POS.y);

			table.add(playCardStack[x]);
		}

		// Dealing new game
		for (int x = 0; x < NUM_PLAY_DECKS; x++)
		{
			int hld = 0;
			Card c = deck.pop().setFaceup();
			//c.setImage(cardPath);
			playCardStack[x].putFirst(c);

			
                        //here
                        for (int y = 0; y < 4; y++)
			{
				c = deck.pop().setFaceup();
				playCardStack[x].push(c);
			}
		}

        deck.setXY(DECK_POS.x, DECK_POS.y);
        table.add(deck);

		Vector stack = deck.getStack();
        for (int x = 0; x < deck.showSize(); x++)
		{
            Card c = (Card) stack.get(x);
			c.setFaceup();
		}


		// reset time
		time = 0;

		scoreBox.setText("Score: 0");
		timeBox.setText("Seconds: 0");
		
		/*table.add(statusBox);
		table.add(toggleTimerButton);
		table.add(saveButton);
		table.add(loadButton);
		table.add(optionsButton);
		table.add(gameTitle);
		table.add(timeBox);
		table.add(mainMenuButton);
		table.add(newGameButton);
		table.add(showRulesButton);
		table.add(scoreBox);*/
		table.repaint();
	}

	public void execute(JPanel myTable, JFrame myFrame, StartMenu myMenu, String myCardPath) {
		Container contentPane;
		table = myTable;
		frame = myFrame;
		mainMenu = myMenu;
		cardPath = myCardPath;


		frame.setSize(TABLE_WIDTH, TABLE_HEIGHT);

		table.setLayout(null);
		table.setBackground(new Color(0, 180, 0));

		contentPane = frame.getContentPane();
		contentPane.add(table);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        playNewGame();
		//addButtons();
		startTimer();


		/*table.addMouseListener(new CardMovementManager());
		table.addMouseMotionListener(new CardMovementManager());*/

		frame.setVisible(true);
	}
}
