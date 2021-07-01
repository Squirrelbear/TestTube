import java.awt.*;

/**
 * Test Tube
 * Author: Peter Mitchell (2021)
 *
 * Command class:
 * A class to represent a single action that has been applied to the
 * test tubes. It has methods to both execute() the original command,
 * and if the undo is applied the reverse() will undo that same command.
 */
public class Command {
    /**
     * The colour that is to be applied.
     */
    private Color colour;
    /**
     * The test tube the colour is going to.
     */
    private TestTube to;
    /**
     * The test tube the colour is coming from.
     */
    private TestTube from;
    /**
     * The count of colours that are being transferred.
     */
    private int removeCount;

    /**
     * @param from The test tube the colour is coming from.
     * @param to The test tube the colour is going to.
     * @param colour The colour that is to be applied.
     * @param removeCount The count of colours that are being transferred.
     */
    public Command(TestTube from, TestTube to, Color colour, int removeCount) {
        this.from = from;
        this.to = to;
        this.colour = colour;
        this.removeCount = removeCount;
    }

    /**
     * Executes the command by removing the specified number from the
     * "from" and transferring that same number of the colour to the "to".
     */
    public void execute() {
        from.remove(removeCount);
        to.add(removeCount, colour);
    }

    /**
     * Performs the reverse of command() by removing the specified number from the
     * "to" and transferring that same number of the colour to the "from".
     */
    public void reverse() {
        to.remove(removeCount);
        from.add(removeCount, colour);
    }
}
