package memorygame;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

/**
 * This is a game logic class.The purpose of the class is 
 * to create a 2D array and to store pairs of equal values.
 * The player then selects different locations on the array and 
 * tries to match all values. 
 * It can be implemented with different users interfaces.
 * 
 * @author Daniel Krastev 
 * @version 01/04/2016
 */
public class PairEngine
{
    private final static String ABOUT_VERSION = "Version 1.0 \nAuthor: Daniel K.";

    //The array which will stores the random pairs.
    private String[][] stringArray;
    //The pair of values currently selected from the user.
    private String firstValue, secondValue;  
    public int firstRow, firstCol, maxLength;
    //The number of moves that the player has done.
    private int moves;

    /**
     * Create new PairEngine.
     * @param rows The number of rows for the pair engine.
     * @param cols The number of columns for the pair engine.
     */
    public PairEngine(int rows, int cols)
    {
        createRandomArray(rows, cols);
        initializeFirstRowsCols(rows, cols);
        firstValue = null;
        secondValue = null;
    }

    /**
     * Take a new coordinates for location on the array to be checked.
     * This method is supposed to be used everytime from the user, when he
     * wants to open a new value.
     * @param r The certain row of the array.
     * @param c The certain column of the array.
     * @return The result after the check. Could be:<br>
     * 
     * -1 - If there is no match with the frist value.<br>
     *  0 - If this is the first selected value.<br>
     *  1 - If there is a match with the first value.<br>
     *  2 - If the value on the selected place of the array is null, the same as the previous one or out of the array boundary.
     */
    public int checkResult(int r, int c)
    {
        int result; //If the value on the selected place of the array is null, the same as the previous one or out of the array boundary.

        try
        {
            if(stringArray[r][c] != null && (firstRow != r || firstCol != c)) {
                moves += 1;
                if(firstValue == null) {
                    result = 0;                           //If this is the first selected value. (0)
                    firstValue = stringArray[r][c];
                    firstRow = r;
                    firstCol = c;
                } else {
                    secondValue = stringArray[r][c];
                    if(secondValue.equals(firstValue)) {
                        result = 1;                     //If there is a match with the first value. (1)
                        stringArray[r][c] = null;
                        stringArray[firstRow][firstCol] = null;
                        initializeFirstSecondValue();
                        initializeFirstRowsCols();
                    } else {
                        result = -1;                 //If there is no match with the frist value. (-1)
                        initializeFirstSecondValue();
                        initializeFirstRowsCols();
                    }
                }
            } else {
                result = 2;
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println("Method checkResult used with illegal parameters:" +" rows: " + r + ", cols: " + c);
            result = 2;
        }

        return result;
    }

    /**
     * Conduct check to see whether the game is won or not.
     * @return True if the game is won.
     */
    public boolean isWon()
    {
        boolean isWon = true;
        List<String> list = new ArrayList<>();
        for (String[] array : stringArray) {
            list.addAll(Arrays.asList(array));
        }
        for(String s : list) {
            if(s != null) {
                isWon = false;
            }
        }

        return isWon;
    }

    /**
     * Return the moves counter.
     * @return The value of the moves counter.
     */
    public int getMoves()
    {
        return moves;
    }

    /**
     * Take two integer values for rows and columns and check
     * the value for these coordinates in the array.
     * @param r The number of row to be checked.
     * @param c The number of columns to be cheched.
     * @return The value for of the array for these coordinates.
     */
    public String getValueAt(int r, int c)
    {
        return stringArray[r][c];
    }

    /**
     * Return information about the game.
     * @return Information about the game.
     */
    public String aboutGame()
    {
        return ABOUT_VERSION;
    }

    /**
     * Fill a list with values depending on the number of rows
     * and columns selected from the user.
     * @param intList The list to be used.
     * @param rows The number of rows.
     * @param cols The number of columns.
     */
    private void fillArrayList(ArrayList<Integer> intList, int rows, int cols)
    {
        while(intList.size() != rows*cols) {
            for(int i = 0; i < rows*cols/2; i++) {
                intList.add(i);
            }
            fillArrayList(intList, rows, cols);
        }
    }

    /**
     * Initialize the main array that will be used in the game.
     * @param rows The number of rows.
     * @param cols The number of columns.
     */
    private void createRandomArray(int rows, int cols)
    {
        ArrayList<Integer> intList = new ArrayList<Integer>();
        fillArrayList(intList, rows, cols);
        Collections.shuffle(intList);

        stringArray = new String[rows][cols];
        String value = null;

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                value = String.valueOf(intList.get(0));
                intList.remove(0);
                stringArray[r][c] = value;
            }
        }
    }

    /**
     * Initialize the value of the firstRow and firstCol,
     * that are used in the game logic.It gives them value that is
     * always higher than the values of the real array's row and column.
     * @param rows The number of rows.
     * @param cols The number of columns.
     */
    private void initializeFirstRowsCols(int rows, int cols)
    {
        if(rows >= cols) {
            maxLength = rows;
        } else {
            maxLength = cols;
        }

        firstRow = maxLength;
        firstCol = maxLength;
    }

    /**
     * Initialize the value of the firstRow and firstCol,
     * that are used in the game logic.It gives them value that is
     * always higher than the values of the real array's row and column.
     */  
    private void initializeFirstRowsCols()
    {
        firstRow = maxLength;
        firstCol = maxLength;
    }

    /**
     * After the check of the check of the values is 
     * conducted, this method returns the initial state
     * of the firstValue and secondValue.
     */
    private void initializeFirstSecondValue()
    {
        firstValue = null;
        secondValue = null;
    }
}
