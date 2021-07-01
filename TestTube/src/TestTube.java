import java.awt.*;

/**
 * Test Tube
 * Author: Peter Mitchell (2021)
 *
 * TestTube class:
 * Represents a single test tube consisting of four colours.
 */
public class TestTube extends Rectangle {

    /**
     * The current four colours shown in the test tube.
     */
    private Color[] colours;

    /**
     * If the test tube is currently selected the position is offset to move it up.
     */
    private boolean isSelected;

    /**
     * Creates a test tube and fills it with block (empty).
     *
     * @param position Position to place the test tube.
     * @param width Width of the test tube.
     * @param height Height of the test tube.
     */
    public TestTube(Position position, int width, int height) {
        super(position, width, height);

        colours = new Color[4];
        for(int i = 0;i<4; i++) {
            colours[i] = Color.BLACK;
        }

        isSelected = false;
    }

    /**
     * Changes the current selection state of the test tube.
     *
     * @param isSelected True to make the test tube move upward, false to use default position.
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * Draws the four colours with an outline.
     *
     * @param g Reference to the Graphics object for rendering.
     */
    public void paint(Graphics g) {
        int sectionWidth = width/3;
        int sectionHeight = height/6;
        int offset = isSelected ? 0 : sectionHeight;
        // Draw the oval part at the bottom first so it can be drawn over to remove the lines.
        g.setColor(colours[3]);
        g.fillOval(position.x+sectionWidth, position.y+(3)*sectionHeight + offset+sectionHeight/2, sectionWidth, sectionHeight);
        g.setColor(Color.WHITE);
        g.drawOval(position.x+sectionWidth, position.y+(3)*sectionHeight + offset+sectionHeight/2, sectionWidth, sectionHeight);
        // Draw the four colours as rectangles
        for(int i = 0; i < colours.length; i++) {
            g.setColor(colours[i]);
            g.fillRect(position.x+sectionWidth, position.y+(i)*sectionHeight + offset, sectionWidth, sectionHeight);
        }
        // Draw a border
        g.setColor(Color.WHITE);
        g.drawRect(position.x+sectionWidth, position.y+offset, sectionWidth, sectionHeight*4);
        // Remove the bottom of the border
        g.setColor(colours[3]);
        g.fillRect(position.x+sectionWidth+1, position.y+(3)*sectionHeight + offset+1, sectionWidth-1, sectionHeight);
    }

    /**
     * Finds the top colour of the test tube if there is one.
     *
     * @return Black, or the first top colour that is not Black.
     */
    public Color topColour() {
        for(int i = 0; i < 4; i++) {
            if(colours[i] != Color.BLACK) return colours[i];
        }
        return Color.BLACK;
    }

    /**
     * Gets how many positions are empty (Black) in the test tube.
     *
     * @return A number between 0 and 4.
     */
    public int countRoom() {
        int freeCount = 0;
        for(int i = 0; i < 4; i++) {
            if(colours[i] == Color.BLACK) freeCount++;
        }
        return freeCount;
    }

    /**
     * Gets the number of occurrences of the top not black colour.
     *
     * @return A number between 0 and 4.
     */
    public int countTopColour() {
        for(int i = 0; i < 4; i++) {
            if(colours[i] != Color.BLACK) {
                int count = 1;
                for(int j = i+1; j < 4 && colours[i] == colours[j]; j++) {
                    count++;
                }
                return count;
            }
        }
        return 0;
    }

    /**
     * Removes count number of non-black elements by turning the black.
     *
     * @param count The number of non-black elements to remove.
     */
    public void remove(int count) {
        int removed = 0;
        for(int i = 0; i < 4 && removed != count; i++) {
            if(colours[i] != Color.BLACK)  {
                colours[i] = Color.BLACK;
                removed++;
            }
        }
    }

    /**
     * Finds the point to start inserting at. And then inserts
     * upward to fill count number of places with the colour.
     *
     * @param count Number of elements to add of colour.
     * @param colour The colour of the elements to add.
     */
    public void add(int count, Color colour) {
        int addFrom=0;
        for(int i = 0; i < 4; i++) {
            if(colours[i] != Color.BLACK) {
                addFrom = i-1;
                break;
            } else if(i == 3 && colours[i] == Color.BLACK) {
                // case when the test tube is empty start from the bottom.
                addFrom = i;
                break;
            }
        }
        int addCount = 1;
        for(int j = addFrom; j >= 0 && addCount <= count; j--) {
            addCount++;
            colours[j] = colour;
        }
    }

    /**
     * Test if the elements all match in the test tube. This is also true if it is empty.
     *
     * @return True if the elements in the test tube are all the same.
     */
    public boolean fourOfSameColour() {
        for(int i = 1; i < 4; i++) {
            if(colours[i] != colours[0])  {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets all the colours represented in this test tube.
     *
     * @return The array of colours for this object.
     */
    public Color[] getColours() {
        return colours;
    }

    /**
     * Overwrites the current colours with the new colours array.
     *
     * @param colours The colours to apply.
     */
    public void setColours(Color[] colours) {
        this.colours = colours;
    }
}
