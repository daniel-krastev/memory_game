package memorygame;


/**
 * Enumeration class BoardType:
 * Used to store three types of boards and to keep
 * their values for the rows and the columns.
 * 
 * @author Daniel Krastev
 * @version 01/04/2016
 */
public enum BoardType
{
    BEGINNER(3,4),

    INTERMEDIATE(4,5),

    ADVANCED(5,6);

    private int rows;
    private int cols;

    /**
     * Construct new BoardType.
     * @param rows The rows for the BoardType.
     * @param cols The columns for the BoardType.
     */
    private BoardType(int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * Return the number of rows of the BoardType.
     * @return The rows of the BoardType.
     */
    public int getRows()
    {
        return rows;
    }

    /**
     * Return the number of columns of the BoardType.
     * @return The columns of the BoardType.
     */
    public int getCols()
    {
        return cols;	
    }
}
