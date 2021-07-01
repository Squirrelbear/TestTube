import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Test Tube
 * Author: Peter Mitchell (2021)
 *
 * DifficultyDialog class:
 * Acts as a separate JFrame that can be made visible with show().
 * Allows access to changing the four different properties for
 * generating puzzles. The Start button can be pushed to begin a new
 * game with the specified difficulty settings.
 */
public class DifficultyDialog implements ChangeListener, ActionListener {
    /**
     * Reference to the Game Panel to create the name game.
     */
    private GamePanel gamePanel;
    /**
     * Reference to the dialog's frame to show/hide it.
     */
    private JFrame frame;
    /**
     * Slider to set the number of test tubes.
     * Values 2 to 20, default 10.
     */
    private JSlider tubesSlider;
    /**
     * Slider to set the total number of empty test tubes in the solution.
     * Values 1 to tubeSlider value / 2. Default 4.
     */
    private JSlider emptySlider;
    /**
     * Slider to set the total number of empty test tubes that are forced to be at the end.
     * Values 0 to emptySlider value. Default 2.
     */
    private JSlider emptyAtEndSlider;
    /**
     * Slider to set the number of different colours that can be randomly used at max.
     * Values 1 to maximum total colours. Default 4.
     */
    private JSlider coloursSlider;
    /**
     * Label to show the number of tubes.
     */
    private JLabel tubesLabel;
    /**
     * Label to show the number of total empty tubes.
     */
    private JLabel emptyLabel;
    /**
     * Label to show the number of empty tubes at the end.
     */
    private JLabel emptyAtEndLabel;
    /**
     * Label to show the number of colours.
     */
    private JLabel coloursLabel;
    /**
     * Button to start a new game with the specified settings.
     */
    private JButton startButton;
    /**
     * Disables the state change until after all components have been loaded.
     */
    private boolean loaded;

    /**
     * Creates a JFrame ready to be shown with show().
     *
     * @param gamePanel Reference to the game panel for creating new games.
     */
    public DifficultyDialog(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        frame = new JFrame("Difficulty");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.getContentPane().add(createDifficultyPanel());
        frame.pack();
    }

    /**
     * Creates all the components to display in the dialog.
     *
     * @return A JPanel with all the components added.
     */
    private JPanel createDifficultyPanel() {
        loaded = false;
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(250, 210));
        tubesSlider = createSlider(2,20, 10);
        emptySlider = createSlider(1,tubesSlider.getValue()/2, 4);
        emptyAtEndSlider = createSlider(0,emptySlider.getValue(), 2);
        coloursSlider = createSlider(1,Puzzle.possibleColours.length, 4);
        tubesLabel = createLabel("");
        emptyLabel = createLabel("");
        emptyAtEndLabel = createLabel("");
        coloursLabel =createLabel("");
        startButton = createButton("Start");
        loaded = true;
        updateText();

        panel.add(tubesLabel);
        panel.add(tubesSlider);
        panel.add(emptyLabel);
        panel.add(emptySlider);
        panel.add(emptyAtEndLabel);
        panel.add(emptyAtEndSlider);
        panel.add(coloursLabel);
        panel.add(coloursSlider);
        panel.add(startButton);

        return panel;
    }

    /**
     * Creates a slider with all the required properties.
     *
     * @param min Minimum value
     * @param max Maximum value
     * @param startValue Initial value
     * @return The slider with everything configured.
     */
    private JSlider createSlider(int min, int max, int startValue) {
        JSlider slider = new JSlider(min, max);
        slider.setValue(startValue);
        slider.addChangeListener(this);
        slider.setForeground(new Color(255, 196, 0));
        slider.setBackground(Color.BLACK);
        return slider;
    }

    /**
     * Creates a label with modified colour.
     *
     * @param text The text to display on the label.
     * @return The label with everything configured.
     */
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(255, 196, 0));
        return label;
    }

    /**
     * Creates a button with modified colours and listener attached.
     *
     * @param text The test to display on the button.
     * @return The button with everything configured.
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(141, 24, 24));
        button.setForeground(new Color(255, 196, 0));
        button.addActionListener(this);
        return button;
    }

    /**
     * Does nothing if not fully loaded yet.
     * Updates the maximum values for the empty sliders,
     * and then updates the text to reflect current slider
     * values.
     *
     * @param e Information about the event.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        if(!loaded) return;
        emptySlider.setMaximum(tubesSlider.getValue()/2);
        emptyAtEndSlider.setMaximum(emptySlider.getValue());
        updateText();
    }

    /**
     * Shows the dialog.
     */
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Updates the labels to reflect current values of all the sliders.
     */
    private void updateText() {
        tubesLabel.setText("Tubes: " + tubesSlider.getValue());
        emptyLabel.setText("Empty: " + emptySlider.getValue());
        emptyAtEndLabel.setText("Empty At End: " + emptyAtEndSlider.getValue());
        coloursLabel.setText("Colours: " + coloursSlider.getValue());
    }

    /**
     * Triggered when the Start button is pressed.
     * Applies the difficulty to the game panel to start a new game, and
     * then hides this dialog.
     *
     * @param e Information about the event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        gamePanel.applyDifficulty(tubesSlider.getValue(),emptySlider.getValue(),
                                    emptyAtEndSlider.getValue(),coloursSlider.getValue());

        frame.setVisible(false);
    }
}
