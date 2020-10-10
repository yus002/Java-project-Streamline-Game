/**
 * Author: Yue Sun
 * email: yus002@ucsd.edu
 * CSE8B Login : cs8bwadc
 * Date: 2/13/19
 * File: Streamline.java 
 * Sources of Help: PA3 writeup
 *  https://docs.oracle.com/javase/10/docs/api/java/util/List.html
 *  https://docs.oracle.com/javase/10/docs/api/java/util/Scanner.html
 *  https://docs.oracle.com/javase/10/docs/api/java/io/PrintWriter.html
 *  
 * This file is a solution to After-Checkpoint (Streamline.java) of PSA3
 * It works with GameState.java to build the Streamline game
 * This file contains the Streamline class
 */

import java.util.*;
import java.io.*;

/**
 * Uses Streamline class to contain method play(), 
 * method recordAndMove(), method undo(), 
 * method loadFromFile(), method saveToFile() 
 * to realize the function of these methods
 *
 * Instance variable:
 *      currentState - This is the current GameState at play that needs 
 *          to be updated with every move that the player makes. 
 *      previousStates - This is a list of all the previous GameState 
 *          objects of the game being played 
 */

public class Streamline {
    // for avoid using magic numbers
    private static final int obstacles = 3;  
    private static final String signOfPlay = ">"; 
    private static final String announcement = "Level Passed!";
    private static final String inputW = "w";
    private static final String inputA = "a";
    private static final String inputS = "s";
    private static final String inputD = "d";
    private static final String inputU = "u";
    private static final String inputO = "o";
    private static final String inputQ = "q";
    private static final String emptySpace = " ";
    //  private static final String 
    //  private static final String 
    //  private static final String 


    final static int DEFAULT_HEIGHT = 6; 
    final static int DEFAULT_WIDTH = 5;  

    final static String OUTFILE_NAME = "saved_streamline_game";

    GameState currentState;
    List<GameState> previousStates;
    
    /**
     * This constructor initializes currentState with a default height of 6, 
     * a default width of 5, a current player position at the lower left 
     * corner of the board, and a goal position of the top right
     * corner of the board.
     * Add 3 random obstacles to the current state.
     * Initialize previousStates to an empty ArrayList       
     * 
     * @param none  
     * @return nothing
     */

    public Streamline() {
        currentState = new GameState(DEFAULT_HEIGHT, DEFAULT_WIDTH, 
                DEFAULT_HEIGHT - 1, 0, 0, DEFAULT_WIDTH - 1);

        currentState.addRandomObstacles(obstacles);
        previousStates = new ArrayList<GameState>();
    }
    
    /**
     * This constructor takes in a String and uses it to invoke
     * method loadFromFile()
     * @param filename  a given String 
     * @return nothing
     */

    public Streamline(String filename) {
        try {
            loadFromFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /** This helper method takes in the parameter filename. 
     * Read the file's content, and initialize the appropriate 
     * instance variables. After loading everything from 
     * the file, check whether the game is already over.
     *
     * @param String  the file to be read
     * @return nothing
     */
    
    protected void loadFromFile(String filename) throws IOException {
        File sourcefile = new File(filename);
        Scanner input = new Scanner(sourcefile);
        StringBuilder stringbuilder = new StringBuilder();

        int loadHeight = input.nextInt();
        int loadWidth = input.nextInt();
        int loadPlayerRow = input.nextInt();
        int loadPlayerCol = input.nextInt();
        int loadGoalRow = input.nextInt();
        int loadGoalCol = input.nextInt();
        
        // store the printed board into the stringbuilder
        // and then store the content of the stringbuilder
        // into the char[][] board later.
        while (input.hasNextLine()){
            stringbuilder.append(input.nextLine());
        }
        
        input.close();

        // check whether the game is already over
            
            currentState = new GameState(loadHeight,loadWidth,loadPlayerRow,
                    loadPlayerCol,loadGoalRow,loadGoalCol);
            previousStates = new ArrayList<GameState>();
            
            // convert String printedBoard into a char array
            // then copy the elements in the char array into the 2D char board
            
            char[] printedBoard = (stringbuilder.toString()).toCharArray();
            int charIndex = 0;

            for (int i = 0; i < loadHeight; i++){
                for (int j = 0; j < loadWidth; j++){
                    currentState.board[i][j] = printedBoard[charIndex++];
                }
            }  
        if ((loadPlayerRow == loadGoalRow) && (loadPlayerCol == loadGoalCol)){
        	currentState.levelPassed = true;
		return;
        }

    }
    
    /** This method first updates previousStates by saving a copy of 
     * the current state into the list and then makes a move in 
     * the given direction on currentState (by calling move()). 
     * If direction is null, do nothing
     *
     * @param direction the Direction object used to denote the direction
     * of moving the snake
     * @return nothing
     */

    void recordAndMove(Direction direction) {
        if (direction == null){
            return;
        }
        else{
            GameState copyOfcurrentState = new GameState(currentState);
            currentState.move(direction);

            // move before record
            // then check whether the board state changes or not
            if (currentState.equals(copyOfcurrentState)){
                return;
            }
            else{
                previousStates.add(copyOfcurrentState);
            }
        }

    } 
    
    /** this method is to allow the player to undo their last step. 
     * This method should update previousStates and currentState 
     * appropriately to undo the most recent move made by the player.
     *
     * @param none
     * @return nothing
     */

    void undo() {
        if (previousStates.isEmpty()){
            return;
        }
        else{
            int previousIndex = previousStates.size() - 1;
            currentState = previousStates.get(previousIndex);
            previousStates.remove(previousIndex);
        }

    }
    
    /** this method handles the interactive part of the program.  
     * Before returning from the method when the player passed the level, 
     * print the current game state one last time and print "Level Passed!".
     * 
     * @param none
     * @return nothing
     */

    void play() {

        // stop the loop when the level has been passed
        // and when the user call exit

        int exit = 0;
        while (currentState.levelPassed != true){
            System.out.println(currentState.toString());
            System.out.print(signOfPlay);

            Scanner inputReader = new Scanner(System.in);
            switch (inputReader.next()){
                case inputW:
                    recordAndMove(Direction.UP);              
                    break;
                case inputA:
                    recordAndMove(Direction.LEFT);                                 
                    break;
                case inputS:
                    recordAndMove(Direction.DOWN);
                    break;
                case inputD:
                    recordAndMove(Direction.RIGHT);
                    break;
                case inputU:
                    undo();
                    break;
                case inputO:
                    saveToFile();
                    break;
                case inputQ:
                    exit = 1;
                    break;
                default:
                    break;
            }
            // break the loop if the user calls exit
            if (exit == 1 ){
                break;
            }
        }
        if  (currentState.levelPassed == true){
            System.out.println(currentState.toString());
            System.out.println(announcement);
            return;
        }
        else if (exit == 1){
            return;
        }

    }
    
    /** This method writes the Streamline game to a file 
     * in a specific format 
     * it uses OUTFILE_NAME as the filename which you will save to
     * 
     * @param none
     * @return nothing
     */

    void saveToFile() {
        try {
            PrintWriter printwriter = new PrintWriter(OUTFILE_NAME);
            printwriter.print((currentState.board).length);
            printwriter.print(emptySpace);
            printwriter.println((currentState.board[0]).length);
            printwriter.print(currentState.playerRow);
            printwriter.print(emptySpace);
            printwriter.println(currentState.playerCol);
            printwriter.print(currentState.goalRow);
            printwriter.print(emptySpace);
            printwriter.println(currentState.goalCol);
            
            for (int i = 0; i < (currentState.board).length; i++){
                for (int j = 0; j < (currentState.board[0]).length; j++){
                    // before hitting the edge
                    if (j != (currentState.board[0]).length - 1){
                        printwriter.print(currentState.board[i][j]);
                    }
                    // get ready to enter a newline
                    else{
                        printwriter.println(currentState.board[i][j]);
                
                    }
                }
            }
            printwriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Saved current state to: saved_streamline_game");
    }
}
