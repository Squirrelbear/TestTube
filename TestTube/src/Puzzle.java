import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test Tube
 * Author: Peter Mitchell (2021)
 *
 * Puzzle class:
 * Represents a puzzle with a collection of test tubes.
 */
public class Puzzle {

    /**
     * All the possible colours that can be used for the game.
     */
    public static final Color[] possibleColours = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.PINK,
                                                    new Color(63, 90, 38), new Color(134, 57, 57)};

    /**
     * The list of active test tubes as part of the puzzle.
     */
    private List<TestTube> testTubeList;
    /**
     * The currently selected test tube to be poured into another.
     */
    private TestTube selected;

    // Puzzle properties for new puzzles.
    /**
     * The total number of test tubes to include.
     */
    private int tubeCount;
    /**
     * The number of test tubes that will be empty at the end of the solution.
     */
    private int emptyCount;
    /**
     * The number that begin empty at the end.
     */
    private int emptyAtEndCount;
    /**
     * The number of different colours that can be randomly placed.
     */
    private int colourCount;

    // Properties for resetting and undoing
    /**
     * The data for all test tubes saved from the initial puzzle that can be restored.
     */
    private Color[] initialState;
    /**
     * All the commands that have been run to allow an undo action.
     */
    private List<Command> commandHistory;

    /**
     * Prepares the puzzle ready for a newPuzzle() to be created.
     *
     * @param tubeCount The total number of test tubes to include.
     * @param emptyCount The number of test tubes that will be empty at the end of the solution.
     * @param emptyAtEndCount The number that begin empty at the end.
     * @param colourCount The number of different colours that can be randomly placed.
     */
    public Puzzle(int tubeCount, int emptyCount, int emptyAtEndCount, int colourCount) {
        testTubeList = new ArrayList<>();
        this.tubeCount = tubeCount;
        this.emptyCount = emptyCount;
        this.emptyAtEndCount = emptyAtEndCount;
        this.colourCount = colourCount;
        commandHistory = new ArrayList<>();
    }

    /**
     * Generates a new puzzle by clearing the old one. And then using the
     * specified parameters for the class to generate a new set of test tubes filled as
     * required.
     */
    public void newPuzzle() {
        testTubeList.clear();
        commandHistory.clear();

        // Choose the colours that will be used by randomising the order of existing colours.
        List<Color> colourSelection = Arrays.asList(possibleColours.clone());
        Collections.shuffle(colourSelection);

        // Calculate the spacing to be used for visual elements
        int fitTubes = (tubeCount % 2 == 0) ? tubeCount/2 : (tubeCount+1)/2;
        int tubeWidth = Math.min(GamePanel.PANEL_WIDTH / fitTubes, 150);
        int tubeHeight = 200;
        int x = 0, y = 100;
        // Create all the test tubes split over up to two rows.
        for(int i = 0; i < tubeCount; i++) {
            testTubeList.add(new TestTube(new Position(x,y),tubeWidth,tubeHeight));
            x+=tubeWidth;
            if(x + tubeWidth > GamePanel.PANEL_WIDTH) {
                x = 0;
                y += tubeHeight;
            }
        }
        // Create a random selection of colour counts to apply colours randomly.
        int[] colourCounts = new int[colourCount];
        int totalColours = 0;
        for(int i = 0; i < tubeCount-emptyCount; i++) {
            colourCounts[(int)(Math.random()*colourCounts.length)] += 4;
            totalColours += 4;
        }
        // Distribute all the colours into the test tubes randomly until there are no more to distribute.
        while(totalColours > 0) {
            // Find a random colour that still has elements to remove
            int colourID;
            do {
                colourID = (int)(Math.random()*colourCounts.length);
            } while(colourCounts[colourID] == 0);
            // Find a random test tube that is not full
            int testTubeID;
            do {
                testTubeID = (int)(Math.random()*(testTubeList.size()-emptyAtEndCount));
            } while(testTubeList.get(testTubeID).countRoom() == 0);
            // Place that colour into the found test tube
            testTubeList.get(testTubeID).add(1, colourSelection.get(colourID));
            totalColours--;
            colourCounts[colourID]--;
        }
        // save the state to allow restarting
        saveInitialState();
    }

    /**
     * Clears any currently selected test tube if there is one.
     */
    public void clearSelection() {
        if(selected != null) {
            selected.setSelected(false);
        }
        selected = null;
    }

    /**
     * Finds a test tube that has been clicked on if there is one.
     * If one has previously been selected it will attempt to pour
     * into the new one and then clear the selection. Otherwise
     * it will select the one that has been clicked provided
     * it is not empty.
     *
     * @param mousePosition Position of the mouse.
     */
    public void handleClick(Position mousePosition) {
        TestTube testTubeAtMouse = getTestTubeAt(mousePosition);
        if(testTubeAtMouse == null) return;

        if(selected == null) {
            // Don't select empty test tubes as the first click
            if(testTubeAtMouse.topColour() != Color.BLACK) {
                selected = testTubeAtMouse;
                selected.setSelected(true);
            }
        } else if(selected != testTubeAtMouse){
            // attempt to pour, may fail if the pour is not valid
            pourTube(selected, testTubeAtMouse);
            clearSelection();
        }
    }

    /**
     * Checks if the game is won by testing if all the test tubes have
     * four of the same colour.
     *
     * @return True if the game has been won.
     */
    public boolean gameWon() {
        for (TestTube testTube : testTubeList) {
            if(!testTube.fourOfSameColour()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Draws all the test tubes.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        testTubeList.forEach(testTube -> testTube.paint(g));
    }

    /**
     * Attempts to find the test tube that has been clicked.
     *
     * @param mousePosition Position of the mouse.
     * @return A test tube if there is one at that position, or null.
     */
    private TestTube getTestTubeAt(Position mousePosition) {
        for (TestTube testTube : testTubeList) {
            if(testTube.isPositionInside(mousePosition)) {
                return testTube;
            }
        }
        return null;
    }

    /**
     * Tries to pour from one tube into the other. If any requirement is not met the pour fails and
     * nothing happens. If the pour succeeds the command is logged into the command history for undo.
     *
     * @param from The test tube to pour from.
     * @param to The test tube to pour into.
     * @return True if the pour was successful.
     */
    private boolean pourTube(TestTube from, TestTube to) {
        // The "from" must not be empty, the "to" must not be full, the
        // "to" can not have a different top colour to the "from" unless it is empty.
        if(from.topColour() == Color.BLACK || to.countRoom() == 0
                || (to.topColour() != Color.BLACK && from.topColour() != to.topColour())) {
            return false;
        }

        // Get the maximum number that can be poured
        int removeCount = Math.min(from.countTopColour(), to.countRoom());
        // Create the command to pour, execute it, and add it to the command history.
        Command command = new Command(from, to, from.topColour(), removeCount);
        commandHistory.add(command);
        command.execute();
        return true;
    }

    /**
     * Does nothing if there are no commands to undo.
     * Will reverse the most recent command and remove it from the command history.
     */
    public void undo() {
        if(commandHistory.size() == 0) return;

        clearSelection();
        Command command = commandHistory.get(commandHistory.size()-1);
        commandHistory.remove(commandHistory.size()-1);
        command.reverse();
    }

    /**
     * Checks if there are any commands in the command history to undo().
     *
     * @return True if undo() can be called.
     */
    public boolean isUndoAvailable() {
        return commandHistory.size() != 0;
    }

    /**
     * Saves the initial state of all test tubes to allow reset() to be called.
     */
    private void saveInitialState() {
        initialState = new Color[testTubeList.size()*4];
        int saveIndex = 0;
        for(int i = 0; i < testTubeList.size(); i++) {
            Color[] colours =  testTubeList.get(i).getColours();
            for(int j = 0; j < 4; j++, saveIndex++) {
                initialState[saveIndex] = colours[j];
            }
        }
    }

    /**
     * Resets back to the initial state of the current puzzle.
     * Clears any selection and command history then forces all the saved data into the test tubes.
     */
    public void reset() {
        commandHistory.clear();
        clearSelection();
        int saveIndex = 0;
        for(int i = 0; i < testTubeList.size(); i++) {
            Color[] colourData = new Color[4];
            for(int j = 0; j < 4; j++, saveIndex++) {
                colourData[j] = initialState[saveIndex];
            }
            testTubeList.get(i).setColours(colourData);
        }
    }
}
