/**
 * Author: Yue Sun
 * email: yus002@ucsd.edu
 * CSE8B Login : cs8bwadc
 * Date: 2/13/19
 * File: GameState.java
 * Sources of Help: PA3 writeup
 * https://docs.oracle.com/javase/10/docs/api/java/lang/Object.html
 *
 * This file is a solution to Implementation details (until checkpoint) 
 * of PSA3
 * It works with StreamLine.java to build the Streamline game
 * This file contains the GameState class
 */

import java.util.*;

/**
 * Uses GameState class to contain method addRandomObstacles(), 
 * method rotateClockwise(), method moveRight(), 
 * method move(), method equals() and method toString()
 * to realize the function of these methods
 *
 * Instance variable:
 *      board - This is a 2D char array that will hold everything 
 *          in the Streamline game
 *      playerRow - the row coordinate of the player's current position
 *      playerCol - the column coordinate of the player's current position
 *      goalRow - the row coordinate of the goal
 *      goalCol - the column coordinate of the goal
 *      levelPassed - This is the flag telling the program 
 *          whether the player has reached the goal or not.
 */
public class GameState {
    // for avioding using a magic number in method move()
    private static final int rotateBack = 4;

    // for avioding using magic numbers in method toString()
    private static final int factorTwo = 2;
    private static final int addendThree = 3;
    private static final char border = '-';
    private static final char sideBorder = '|';

    // Used to populate char[][] board below and to display the
    // current state of play.
    final static char TRAIL_CHAR = '.';
    final static char OBSTACLE_CHAR = 'X';
    final static char SPACE_CHAR = ' ';
    final static char CURRENT_CHAR = 'O';
    final static char GOAL_CHAR = '@';
    final static char NEWLINE_CHAR = '\n';

    // This represents a 2D map of the board
    char[][] board;

    // Location of the player
    int playerRow;
    int playerCol;

    // Location of the goal
    int goalRow;
    int goalCol;

    // true means the player completed this level
    boolean levelPassed;

    /**
     * This constructor initializes the board with the given parameters
     * and fill it with SPACE_CHARs. It also initializes all other instance 
     * variables with the given parameters.     
     * @param  height the given height of the board
     * @param width the given width of the board
     * @param playerRow  the given row index of the player
     * @param playerCol the given column index of the player
     * @param goalRow the given row index of the goal
     * @param goalCol the given column index of the goal
     * @return nothing
     */

    // initialize a board of given parameters, fill the board with SPACE_CHAR
    // set corresponding fields to parameters.
    public GameState(int height, int width, int playerRow, int playerCol, 
            int goalRow, int goalCol) {

        board = new char[height][width];
        for(int i = 0;  i < height; i ++){
            for(int j = 0; j < width; j ++){
                board[i][j] = SPACE_CHAR;
            }
        }
        this.playerRow = playerRow;
        this.playerCol = playerCol;
        this.goalRow = goalRow;
        this.goalCol = goalCol;
        boolean levelPassed = false;
    }
    
    /**
     * This constructor is the copy constructor of GameState. 
     * it uses GameState's detailed constructor to initialize all 
     * instance variables (not class variables) of this using the 
     * respective instance variables from other.
     * 
     * @param  other the given GameState object to copy
     * @return nothing
     */

    // copy constructor
    public GameState(GameState other) {
        board = new char[other.board.length][other.board[0].length];
        for(int i = 0;  i < other.board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                board[i][j] = other.board[i][j];
            }
        }
        this.playerRow = other.playerRow;
        this.playerCol = other.playerCol;
        this.goalRow = other.goalRow;
        this.goalCol = other.goalCol;
        this.levelPassed = other.levelPassed;   
    }

    /** add count random blocks into this.board
     * Obstacles do not overwrite the player's position, goal position
     * or other obstacles
     * @param count  the number of random obstacles added to the board  
     * @return nothing
     */

    // add count random blocks into this.board
    // avoiding player position and goal position
    void addRandomObstacles(int count) {
        // edge cases check
        if ((board.length == 0) || (board[0].length == 0)){
            return;
        }
        else{
            int height = board.length;
            int width = board[0].length;
            if ((count > (height * width)) || (count < 0)){
                return;
            }
            else{
                Random rand = new Random();

                // add obstacles chars to the board "count" times
                for(int i = 0; i < count; i++){
                    int randomRowIndex = rand.nextInt(height);
                    int randomColIndex = rand.nextInt(width);

                    // make sure obstacles do not overwrite 
                    // the player's position,
                    // the goal position, or other obstacles
                    if (((randomRowIndex != playerRow) 
                            || (randomColIndex != playerCol))                            
                            && ((randomRowIndex != goalRow) 
                            || (randomColIndex != goalCol)) 
                            && (board[randomRowIndex][randomColIndex] 
                                != OBSTACLE_CHAR)){

                        board[randomRowIndex][randomColIndex] = OBSTACLE_CHAR;
                    }

                    // when if statement is false, making sure 
                    // the for loop still
                    // be able to add "count" number of obstacles 
                    // into the board
                    else{
                        i--;
                    }   
                }
            }
        }
    }

    /** rotate clockwise once
     * rotation should account for all instance var including board, current 
     * position, goal position 
     * @param none 
     * @return nothing
     */

    // rotate clockwise once
    // rotation should account for all instance var including board, current 
    // position, goal position 
    void rotateClockwise() {
        int height = board.length;
        int width = board[0].length;
        char[][] rotatedArray = new char[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                rotatedArray[i][j] = board[height - j - 1][i];
            }
        }
        board = rotatedArray;

        // store the old values of instance variables and
        // change the row indexes and col indexes of the player's position
        // and the position of the goal
        int originalPlayerRow = playerRow;
        int originalPlayerCol = playerCol;
        int originalGoalRow = goalRow;
        int originalGoalCol = goalCol;

        playerRow = originalPlayerCol;
        playerCol = height - originalPlayerRow - 1;
        goalRow = originalGoalCol;
        goalCol = height - originalGoalRow - 1;
    }
    
    /** move current position towards right until stopped by obstacle / edge
     * leave a trail of dots for all positions that we're walked through
     * before stopping
     * @param none 
     * @return nothing
     */

    // move current position towards right until stopped by obstacle / edge
    // leave a trail of dots for all positions that we're walked through
    // before stopping
    void moveRight() {


        // stop the loop if the current position has already hit the edge
        // or the goal before move the snake right
        //
        //if not,
        // start moving the snake and leave a trail of dots
        // and check if the position during moving is still valid
        while(playerCol != board[0].length - 1){

            // if the next position is the past trail
            // or obstacle, do not leave a trail of dots
            // in the current position and move right
            //
            // otherwise, leave a trail of dots and move right

            if ((playerRow == goalRow) && (playerCol == goalCol)){ 

                levelPassed = true;
                return;
            }
            else if ((board[playerRow][playerCol + 1] !=  OBSTACLE_CHAR)
                    && (board[playerRow][playerCol + 1] != TRAIL_CHAR)){

                board[playerRow][playerCol] = TRAIL_CHAR;
                playerCol = playerCol + 1 ;
            }
            else if ((board[playerRow][playerCol + 1] ==  OBSTACLE_CHAR)
                    || (board[playerRow][playerCol + 1] == TRAIL_CHAR)){

                break;
            }
        }
        // check whether moveRight() results in passing the level
        
        if ((playerRow == goalRow) && (playerCol == goalCol)){ 

            levelPassed = true;
            return;
        }

    }
    
    /** move towards any direction given
     * accomplish this by rotating, move right, rotating back
     * @param direction the Direction object used to denote the direction
     * of moving the snake
     * @return nothing
     */

    // move towards any direction given
    // accomplish this by rotating, move right, rotating back
    void move(Direction direction) {
        if (direction == Direction.RIGHT){
            moveRight();
        }
        else{  
            for (int i = 0; i < direction.getRotationCount(); i++){
                rotateClockwise();
            }
            moveRight();

            for (int j = 0; j < rotateBack - direction.getRotationCount()
                    ; j++){
                rotateClockwise();
            }
        }

    }
   
    /** compare two game state objects and check whether their fields
     * have the same value
     * @param other an object to be compared
     * @return boolean  the matching result
     * return true if the fields of these objects have the
     * same value, otherwise, return false
     * return false if parameter is null
     */

    @Override
        // compare two game state objects, returns true if all fields match
        public boolean equals(Object other) { 
            // We have exhausted all possibility of mismatch, 
            // they're identical

            // check whether the input object other is null first
            if (other == null){
                return false;
            }
            // then check whether the input object type is GameState
            else{
                if(!(other instanceof GameState)){
                    return false;
                }
                else{
                    // check int instance variables and boolean first
                    // then make a deep check on char[][] board
                    if((playerRow != ((GameState) other).playerRow) 
                            || (playerCol != ((GameState) other).playerCol) 
                            || (goalRow != ((GameState) other).goalRow)
                            || (goalCol != ((GameState) other).goalCol)
                            || (levelPassed 
                                != ((GameState) other).levelPassed)){

                        return false;
                    }
                    else{   
                        // first check whether the other.board 
                        // has the same size 
                        // as this.board does
                        if ((((GameState) other).board == null) 
                                || (board == null)
                                || (board.length 
                                    != ((GameState) other).board.length)
                                || (board[0].length 
                                    != ((GameState) other).board[0].length)){

                            return false;
                        }
                        else{
                            int height = board.length;
                            int width = board[0].length;

                            // the variable result is used to determine 
                            // whether the later nested for loop find out that
                            // two board have the same contents
                            int result = 1; 
                            for (int i = 0; i < height; i++){
                                for (int j = 0; j < width; j++){
                                    if (((GameState) other).board[i][j]
                                            != board[i][j]){

                                        return false;
                                    }
                                }
                            }

                        }
                    }
                }
            }
            return true;

        }

    /** take data stored in fields and represent them by
     * System.out.println()
     * The String representation generally contains a head border, top border.
     * a left side border, a right side border, obstacles, player,
     * trail dots and the goal.
     *
     * @param none
     * @return nothing
     */

    @Override
        public String toString() {
            int rows = board.length;
            int columns = board[0].length;
            int lengthofBorder = factorTwo * columns + addendThree; 

            StringBuilder stringbuilder = new StringBuilder();
            char[][] copyboard = new char[rows][columns];

            // add top border
            for (int i = 0; i < lengthofBorder; i++){
                stringbuilder.append(border);
            }
            stringbuilder.append(NEWLINE_CHAR);

            // check single column

            if (columns == 1) {
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        stringbuilder.append(sideBorder);
                        stringbuilder.append(SPACE_CHAR);
                        if ((i == playerRow) && (j == playerCol)
                                && (i == goalRow) && (j == goalCol)){
                            stringbuilder.append(CURRENT_CHAR);
                        }
                        else{
                            if ((i == playerRow) && (j == playerCol)){
                                stringbuilder.append(CURRENT_CHAR);
                            }
                            else if ((i == goalRow) && (j == goalCol)){
                                stringbuilder.append(GOAL_CHAR);
                            }

                            else{
                                stringbuilder.append(board[i][j]);
                            }
                        }
                        stringbuilder.append(SPACE_CHAR);
                        stringbuilder.append(sideBorder);
                        stringbuilder.append(NEWLINE_CHAR);
                    }
                }
            }
            else{
                // add side border and board contents
                // by separating them into three parts
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        // considering the side border on the left
                        if (j == 0){
                            stringbuilder.append(sideBorder);
                            stringbuilder.append(SPACE_CHAR);
                            // check whether the current element refers to 
                            // the player, obstacle, trail, or goal

                            // if the player reaches the goal
                            if ((i == playerRow) && (j == playerCol)
                                    && (i == goalRow) && (j == goalCol)){
                                stringbuilder.append(CURRENT_CHAR);
                            }
                            else{
                                if ((i == playerRow) && (j == playerCol)){
                                    stringbuilder.append(CURRENT_CHAR);
                                }
                                else if ((i == goalRow) && (j == goalCol)){
                                    stringbuilder.append(GOAL_CHAR);
                                }

                                // space char, trail char and obstacle char
                                // have been already stored in the board
                                else{
                                    stringbuilder.append(board[i][j]);
                                }
                            }
                        }
                        // consider the board contents between 
                        // the side boarders
                        else if ((j != 0) && (j != columns -1)){
                            stringbuilder.append(SPACE_CHAR);
                            if ((i == playerRow) && (j == playerCol)
                                    && (i == goalRow) && (j == goalCol)){
                                stringbuilder.append(CURRENT_CHAR);
                            }
                            else{
                                if ((i == playerRow) && (j == playerCol)){
                                    stringbuilder.append(CURRENT_CHAR);
                                }
                                else if ((i == goalRow) && (j == goalCol)){
                                    stringbuilder.append(GOAL_CHAR);
                                }

                                // space char, trail char and obstacle char
                                // have been already stored in the board
                                else{
                                    stringbuilder.append(board[i][j]);
                                }
                            }
                        }
                        // considering theh side border on the right
                        else if (j == columns - 1){
                            stringbuilder.append(SPACE_CHAR);
                            if ((i == playerRow) && (j == playerCol)
                                    && (i == goalRow) && (j == goalCol)){
                                stringbuilder.append(CURRENT_CHAR);
                            }
                            else{
                                if ((i == playerRow) && (j == playerCol)){
                                    stringbuilder.append(CURRENT_CHAR);
                                }
                                else if ((i == goalRow) && (j == goalCol)){
                                    stringbuilder.append(GOAL_CHAR);
                                }

                                // space char, trail char and obstacle char
                                // have been already stored in the board
                                else{
                                    stringbuilder.append(board[i][j]);
                                }
                            }
                            stringbuilder.append(SPACE_CHAR);
                            stringbuilder.append(sideBorder);
                            stringbuilder.append(NEWLINE_CHAR);
                        }
                    }
                }
            }
            // add bottom border
            for (int i = 0; i < lengthofBorder; i++){
                stringbuilder.append(border);
            }
            stringbuilder.append(NEWLINE_CHAR);

            return (stringbuilder.toString()); 

        }
}
