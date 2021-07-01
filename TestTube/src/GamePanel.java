import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Test Tube
 * Author: Peter Mitchell (2021)
 *
 * GamePanel class:
 * The primary driver of game interaction. Manages the buttons, and
 * controls the puzzle by passing it the mouse interactions.
 */
public class GamePanel extends JPanel implements MouseListener {
    /**
     * Width of the panel.
     */
    public static final int PANEL_WIDTH = 1050;
    /**
     * Height of the panel.
     */
    public static final int PANEL_HEIGHT = 500;

    /**
     * Reference to the puzzle that is currently active.
     */
    private Puzzle puzzle;
    /**
     * Reference to the difficulty dialog to show it when necessary.
     */
    private DifficultyDialog difficultyDialog;
    /**
     * When true the game ends.
     */
    private boolean gameOver;
    /**
     * A message indicating whether the game over was a win or a loss.
     */
    private String gameOverMessage = "Excellent! You beat the puzzle!";

    /**
     * The next button that can be clicked during the game over screen to create a new game.
     */
    private Rectangle nextButton;
    /**
     * The restart button that can be clicked during a puzzle to restart it.
     */
    private Rectangle restartButton;
    /**
     * The difficulty button that can be clicked at any time to show the difficulty dialog.
     */
    private Rectangle changeDifficultyButton;
    /**
     * The undo button that can be clicked during the puzzle when there is at least one move that can be undone.
     */
    private Rectangle undoButton;

    /**
     * Initialises the game with a default puzzle ready for interaction.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
        setBackground(Color.BLACK);
        puzzle = new Puzzle(10, 4, 2, 4);
        puzzle.newPuzzle();
        addMouseListener(this);

        difficultyDialog = new DifficultyDialog(this);
        nextButton = new Rectangle(PANEL_WIDTH/2-30,PANEL_HEIGHT/2+40, 60, 30);
        restartButton = new Rectangle(10,10,80,30);
        changeDifficultyButton = new Rectangle(100, 10, 100, 30);
        undoButton = new Rectangle(210, 10, 80, 30);
        gameOver = false;
    }

    /**
     * Pressing Escape will quit, R will trigger the reset action, and D will show the difficulty dialog.
     *
     * @param keyCode The key that was pressed.
     */
    public void handleInput(int keyCode) {
        if(keyCode == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if(keyCode == KeyEvent.VK_R) {
            puzzle.reset();
            repaint();
        } else if(keyCode == KeyEvent.VK_D) {
            difficultyDialog.show();
        }
    }

    /**
     * Draws the puzzle, any game over message, all the buttons, and the title.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        super.paint(g);
        puzzle.paint(g);
        if(gameOver) {
            drawGameOver(g);
        }
        drawButton(g, "Restart", restartButton, !gameOver);
        drawButton(g, "Difficulty", changeDifficultyButton, true);
        drawButton(g, "Undo", undoButton, !gameOver && puzzle.isUndoAvailable());
        drawTitle(g);
    }

    /**
     * Creates a new puzzle object with the specified properties and removes the old one.
     *
     * @param tubeCount The total number of test tubes to include.
     * @param emptyCount The number of test tubes that will be empty at the end of the solution.
     * @param emptyAtEndCount The number that begin empty at the end.
     * @param colourCount The number of different colours that can be randomly placed.
     */
    public void applyDifficulty(int tubeCount, int emptyCount, int emptyAtEndCount, int colourCount) {
        Puzzle newPuzzle = new Puzzle(tubeCount, emptyCount, emptyAtEndCount, colourCount);
        newPuzzle.newPuzzle();
        puzzle = newPuzzle;
        gameOver = false;
        repaint();
    }

    /**
     * Clears any game over state and generates a new puzzle with the current settings.
     */
    public void nextPuzzle() {
        puzzle.newPuzzle();
        gameOver = false;
    }

    /**
     * Checks for interaction with the buttons. Difficulty button can be left clicked at any time
     * to show the dialog. When the game is over the Next button can be clicked to create a new
     * puzzle with the same settings. When the game is still running, the restart and undo buttons
     * can be left clicked, the test tubes can be left clicked, or right click can be used to
     * clear the current tube selection.
     *
     * @param e Information about the mouse event.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        Position mousePosition = new Position(e.getX(), e.getY());
        if(e.getButton() == MouseEvent.BUTTON1
                && changeDifficultyButton.isPositionInside(mousePosition)) {
            difficultyDialog.show();
        } else if(gameOver) {
            if(nextButton.isPositionInside(mousePosition)) {
                nextPuzzle();
            }
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            if(restartButton.isPositionInside(mousePosition)) {
                puzzle.reset();
                repaint();
            } else if(undoButton.isPositionInside(mousePosition)) {
                puzzle.undo();
            } else {
                puzzle.handleClick(mousePosition);
                if (puzzle.gameWon()) {
                    gameOver = true;
                }
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            puzzle.clearSelection();
        }

        repaint();
    }

    /**
     * Draws a background with game over message centred in the middle of the panel.
     * Below the message is a "Next" button.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    private void drawGameOver(Graphics g) {
        g.setColor(new Color(99, 99, 99, 179));
        g.fillRect(0,0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setColor(new Color(141, 24, 24));
        g.fillRect(0,PANEL_HEIGHT/2-20, PANEL_WIDTH, 40);
        g.setColor(new Color(255, 34, 34));
        g.drawRect(0,PANEL_HEIGHT/2-20, PANEL_WIDTH, 40);
        g.setColor(new Color(255, 196, 0));
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int strWidth = g.getFontMetrics().stringWidth(gameOverMessage);
        g.drawString(gameOverMessage, PANEL_WIDTH/2-strWidth/2, PANEL_HEIGHT/2+10);

        drawButton(g, "Next", nextButton, true);
    }

    /**
     * Draws the button with a background, border, and centred text.
     *
     * @param g Reference to the Graphics object for rendering.
     * @param text The text to display on the button.
     * @param rectangle The bounds of the buttons hit box.
     * @param isEnabled Whether the button should appear as enabled or disabled. (greyed when disabled).
     */
    private void drawButton(Graphics g, String text, Rectangle rectangle, boolean isEnabled) {
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(isEnabled ? new Color(141, 24, 24) : new Color(88, 88, 88));
        g.fillRect(rectangle.position.x, rectangle.position.y, rectangle.width, rectangle.height);
        g.setColor(new Color(255, 34, 34));
        g.drawRect(rectangle.position.x, rectangle.position.y, rectangle.width, rectangle.height);
        g.setColor(new Color(255, 196, 0));
        int strWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, rectangle.position.x+ rectangle.width/2-strWidth/2, rectangle.position.y+22);
    }

    /**
     * Draws a simple bit of title text at the top of the game.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    private void drawTitle(Graphics g) {
        g.setColor(new Color(201, 8, 8));
        g.setFont(new Font("Arial", Font.BOLD, 40));
        int strWidth = g.getFontMetrics().stringWidth("TEST TUBE");
        g.drawString("TEST TUBE", PANEL_WIDTH/2-strWidth/2+60, 70);
    }

    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void mousePressed(MouseEvent e) {}
    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void mouseReleased(MouseEvent e) {}
    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void mouseEntered(MouseEvent e) {}
    /**
     * Not used.
     *
     * @param e Not used.
     */
    @Override
    public void mouseExited(MouseEvent e) {}
}
