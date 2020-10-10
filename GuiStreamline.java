/**
 * Author: Yue Sun
 * email: yus002@ucsd.edu
 * CSE8B Login : cs8bwadc
 * Date: 3/6/19
 * File: GuiStreamline.java 
 * Sources of Help: PA6 writeup
 *  https://docs.oracle.com/javase/10/docs/api/javafx/scene/paint/Color.html
 *  https://docs.oracle.com/javase/10/docs/api/javafx/scene/shape/
 *  Rectangle.html
 *  https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
 *
 * This file is a solution of PSA6
 * This file contains the GuiStreamline class
 */


import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.*;
import javafx.animation.PathTransition.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.*;
import javafx.util.Duration;

/**
 * Uses GuiStreamline class to contain 
 * method getBoardWidth(), getBoardHeight(), getSquareSize(), 
 * resetGrid(), updateTrailColors(), onPlayerMoved(), handleKeyCode(), 
 * handle(), onLevelLoaded(), onLevelFinished() and start() 
 * to realize the function of these methods
 *
 * Instance variable:
 *      grid    -   It contains obstacle and trail marker shapes.
 *      mainScene   -   main scene
 *      levelGroup  -   For obstacles and trails
 *      rootGroup   -   Parent group for everything else
 *      playerRect  -   GUI representation of the player
 *      goalRect  -   GUI representation of the goal
 *      game    -   the current level
 *      nextGames    -   Future levels
 *      myKeyHandler -   for keyboard input   
 */

public class GuiStreamline extends Application {
    static final double SCENE_WIDTH = 500; 
    static final double SCENE_HEIGHT = 600; 
    private static final double SCENE_MAX = 600;
    static final String TITLE = "CSE 8b Streamline GUI";
    static final String USAGE = 
        "Usage: \n" + 
        "> java GuiStreamline               - to start a game with default" +
        " size 6*5 and random obstacles\n" + 
        "> java GuiStreamline <filename>    - to start a game by reading g" +
        "ame state from the specified file\n" +
        "> java GuiStreamline <directory>   - to start a game by reading a" +
        "ll game states from files in\n" +
        "                                     the specified directory and " +
        "playing them in order\n";
    private static final String defaultOutput = 
        "Possible commands:\n w - up\n " + 
        "a - left\n s - down\n d - right\n u - undo\n " + 
        "q - quit level";

    static final Color TRAIL_COLOR = Color.PALEVIOLETRED;
    static final Color GOAL_COLOR = Color.MEDIUMAQUAMARINE;
    static final Color OBSTACLE_COLOR = Color.DIMGRAY;
    static final Color TRANSPARENT_COLOR = Color.TRANSPARENT;
        
    // Trail radius will be set to this fraction of the size of a board square.
    static final double TRAIL_RADIUS_FRACTION = 0.1;

    // Squares will be resized to this fraction of the size of a board square.
    static final double SQUARE_FRACTION = 0.8;

    Scene mainScene;
    Group levelGroup;                   // For obstacles and trails
    Group rootGroup;                    // Parent group for everything else
    Player playerRect;                  // GUI representation of the player
    RoundedSquare goalRect;             // GUI representation of the goal

    Shape[][] grid;                     // Same dimensions as the game board

    Streamline game;                    // The current level
    ArrayList<Streamline> nextGames;    // Future levels

    MyKeyHandler myKeyHandler;          // for keyboard input

    /** 
     * the helper method to get the board's width
     *
     * @param none
     * @return  the width of the board for the current level
     */

    public int getBoardWidth() {        
        // check whether the board is null
        if (game.currentState.board == null) return 0;

        return game.currentState.board[0].length;
    }

    /** 
     * the helper method to get the board's height
     *
     * @param none
     * @return  the height of the board for the current level
     */

    public int getBoardHeight() {
        if (game == null) return 0;
        if (game.currentState == null) return 0;
        if (game.currentState.board == null) return 0;        
        return game.currentState.board.length;  
    }

    /** 
     * Find a size for a single square of the board that will fit nicely
     * in the current scene size.
     *
     * @param none
     * @return the size of a single square on the board
     */

    public double getSquareSize() {
        /* For example, given a scene size of 1000 by 600 and a board size
           of 5 by 6, we have room for each square to be 200x100. Since we
           want squares not rectangles, return the minimum which is 100 
           in this example. */

        // check null cases
        if ((mainScene == null) || (game == null) 
                || (game.currentState == null) 
                || (game.currentState.board == null)) return 0;

        double sceneWidth = mainScene.getWidth();
        double sceneHeight = mainScene.getHeight();
        int boardWidth = getBoardWidth();
        int boardHeight = getBoardHeight();

        // calculate the size of the square 
        // and find the minimum
        double widthSize = sceneWidth / boardWidth;
        double heightSize = sceneHeight / boardHeight;

        if (widthSize == heightSize){
            return widthSize;
        }
        else if (widthSize > heightSize){
            return heightSize;
        }
        else if (widthSize < heightSize){
            return widthSize;
        }
        return 0;
    }

    /** 
     * Check the instance variables and determine which ones of them 
     * should be cleared. Reset corresponding variables based on the 
     * current board stored in Streamline object
     * 
     * @param none
     * @return nothing
     */

    // Destroy and recreate grid and all trail and obstacle shapes.
    // Assumes the dimensions of the board may have changed.
    public void resetGrid() {

        // Hints: Empty out levelGroup and recreate grid.
        // Also makes sure grid is the right size, in case the size of the
        // board changed.

        grid = new Shape[game.currentState.board.length]
            [game.currentState.board[0].length];
        levelGroup.getChildren().clear();
        
        for (int i = 0; i < getBoardHeight(); i++){
            for (int j = 0; j < getBoardWidth(); j++){
                if ((game.currentState.board[i][j] == 
                        game.currentState.TRAIL_CHAR) ||
                 (game.currentState.board[i][j] == 
                        game.currentState.SPACE_CHAR)){
                    
                    // create the circle shape with a specific radius
                    // and position
                    grid[i][j] = new Circle
                        (boardIdxToScenePos(j,i)[0],
                         boardIdxToScenePos(j,i)[1],
                         TRAIL_RADIUS_FRACTION * getSquareSize());
                }
                else if (game.currentState.board[i][j] == 
                        game.currentState.OBSTACLE_CHAR){
                    
                    // create the rounded square with a specifc size
                    // position and color
                    grid[i][j] = new RoundedSquare
                        (boardIdxToScenePos(j,i)[0],
                         boardIdxToScenePos(j,i)[1],
                         SQUARE_FRACTION * getSquareSize());
                    grid[i][j].setFill(OBSTACLE_COLOR);
                }  
            }
        }  
        // use helper method to give circles in the grid
        // colors
        
        updateTrailColors();

        // add the shapes in the grid to the levelgroup
        for (int i = 0; i < getBoardHeight(); i++){
            for (int j = 0; j < getBoardWidth(); j++){
                levelGroup.getChildren().add(grid[i][j]);
            }
        }          
    }

    /** 
     * This is a helper method that recolors the non-obstacle shapes 
     * in the GUI. This could be used after every move or 
     * when we reload the game from Streamline backend. 
     * 
     * @param none
     * @return nothing
     */

    // Sets the fill color of all trail Circles making them visible or not
    // depending on if that board position equals TRAIL_CHAR.
    public void updateTrailColors() {
        for (int i = 0; i < getBoardHeight(); i++){
            for (int j = 0; j < getBoardWidth(); j++){

                // if the position in the board has a trail dot, 
                // update the color
                // of the shape in the same position in the grid
                if (game.currentState.board[i][j] == 
                        game.currentState.TRAIL_CHAR){
                    grid[i][j].setFill(TRAIL_COLOR);
                }
                else if (game.currentState.board[i][j] == 
                        game.currentState.SPACE_CHAR) {
                    grid[i][j].setFill(TRANSPARENT_COLOR);
                }
            }
        }
       
    }

    /** 
     * Coverts the given board column and row into scene coordinates.
     * Gives the center of the corresponding tile.
     * 
     * @param boardCol a board column to be converted to a scene x
     * @param boardRow a board row to be converted to a scene y
     * @return scene coordinates as length 2 array where index 0 is x
     */
    static final double MIDDLE_OFFSET = 0.5;
    public double[] boardIdxToScenePos (int boardCol, int boardRow) {
        double sceneX = ((boardCol + MIDDLE_OFFSET) * 
                (mainScene.getWidth() - 1)) / getBoardWidth();
        double sceneY = ((boardRow + MIDDLE_OFFSET) * 
                (mainScene.getHeight() - 1)) / getBoardHeight();
        return new double[]{sceneX, sceneY};
    }

    /**
     * Makes trail markers visible and changes player position.
     * To be called when the user moved the player and the GUI needs to be 
     * updated to show the new position.
     *
     * @param fromCol  old player col
     * @param fromRow   old player row
     * @param toCol     new player col
     * @param toRow     new player row
     * @param isUndo    whether it was an undo movement
     * @return nothing
     */

    public void onPlayerMoved(int fromCol, int fromRow, int toCol, int toRow, 
            boolean isUndo)
    {
        // If the position is the same, just return
        if (fromCol == toCol && fromRow == toRow) {
            return;
        }

        else{
            // update the trail colors
            updateTrailColors();
            
            // move the player to the new positions
            double [] playerPos = boardIdxToScenePos(toCol, toRow);
            double squareSize = getSquareSize() * SQUARE_FRACTION;
            playerRect.setSize(squareSize);
            playerRect.setCenterX(playerPos[0]);
            playerRect.setCenterY(playerPos[1]);

            // check if the level is passed
            if ((game.currentState.playerCol == game.currentState.goalCol)
                    && (game.currentState.playerRow == 
                        game.currentState.goalRow)){
                game.currentState.levelPassed = true;
                
                onLevelFinished();
                return;
            }
        }
    }
    
    /**
     * this method handles the interactive part of the program. 
     * @param keyCode The key code associated with the key 
     * in this key pressed or key released event.
     * @return nothing
     */

    // To be called when a key is pressed
    void handleKeyCode(KeyCode keyCode) {

        switch (keyCode) {
            case UP:
                game.recordAndMove(Direction.UP);      
                // find the old and new positions and
                // update the gui
                onPlayerMoved(game.previousStates.get
                        (game.previousStates.size() - 1).playerCol, 
                        game.previousStates.get
                        (game.previousStates.size() - 1).playerRow,
                        game.currentState.playerCol, 
                        game.currentState.playerRow, true);
                break;
           
            case DOWN:
                game.recordAndMove(Direction.DOWN);               
                onPlayerMoved(game.previousStates.get
                        (game.previousStates.size() - 1).playerCol, 
                        game.previousStates.get
                        (game.previousStates.size() - 1).playerRow,
                        game.currentState.playerCol, 
                        game.currentState.playerRow, true);
                break;                
            case LEFT:
                game.recordAndMove(Direction.LEFT);                
                onPlayerMoved(game.previousStates.get
                        (game.previousStates.size() - 1).playerCol, 
                        game.previousStates.get
                        (game.previousStates.size() - 1).playerRow,
                        game.currentState.playerCol, 
                        game.currentState.playerRow, true);
                break;  
            case RIGHT:
                game.recordAndMove(Direction.RIGHT);                
                onPlayerMoved(game.previousStates.get
                        (game.previousStates.size() - 1).playerCol, 
                        game.previousStates.get
                        (game.previousStates.size() - 1).playerRow,
                        game.currentState.playerCol, 
                        game.currentState.playerRow, true);
                break;  
                      
            case U:
                // if the player has already undoed all the 
                // instructions, stop move

                if (game.previousStates.isEmpty()){
                    break;
                }
                else{
                
                    // store the new and old positions first
                    int newCol = game.previousStates.get
                        (game.previousStates.size() - 1).playerCol;
                    int newRow = game.previousStates.get
                        (game.previousStates.size() - 1).playerRow;
                    int oldCol = game.currentState.playerCol;
                    int oldRow = game.currentState.playerRow;
                    // then delete information in the previousStates
                    game.undo();                
                    onPlayerMoved(oldCol,oldRow,newCol,newRow, true);
                    break;  
                }
            case O:
                game.saveToFile();                
                break;  
               
            default:
                System.out.println(defaultOutput);
                break;
        }

    }
    
    /**
    * Uses nested class MyKeyHandler to contain 
    * method handle() to realize the function of it
    *
    * Instance variable: none
    */
    
    class MyKeyHandler implements EventHandler<KeyEvent> {
        
        /**
         * This method handles keyboard input and calls handleKeyCode()
         *
         * @param e event object
         * @return nothing
         */
       
        @Override            
        public void handle(KeyEvent e) {
            KeyCode code = e.getCode();
            handleKeyCode(code);
        
        }
    }
    
    /** 
     * reset the grid, player position and goal position   
     * 
     * @param none
     * @return nothing
     */

    // To be called whenever the UI needs to be completely redone to reflect
    // a new level
    public void onLevelLoaded() {
        resetGrid();

        double squareSize = getSquareSize() * SQUARE_FRACTION;

        // Update the player position
        double[] playerPos = boardIdxToScenePos(
                game.currentState.playerCol, game.currentState.playerRow
                );
        playerRect.setSize(squareSize);
        playerRect.setCenterX(playerPos[0]);
        playerRect.setCenterY(playerPos[1]);
        // update the goal position
        double [] goalPos = boardIdxToScenePos(
                game.currentState.goalCol, game.currentState.goalRow
                );
        goalRect.setSize(squareSize);
        goalRect.setCenterX(goalPos[0]);
        goalRect.setCenterY(goalPos[1]);
        goalRect.setFill(GOAL_COLOR);

    }

    // Called when the player reaches the goal. Shows the winning animation
    // and loads the next level if there is one.
    static final double SCALE_TIME = 175;  // milliseconds for scale animation
    static final double FADE_TIME = 250;   // milliseconds for fade animation
    static final double DOUBLE_MULTIPLIER = 2;
    private static final double startValue = 1.0;
    private static final double endValue = 0.0;
    /** 
     * Check if there are more levels of games to read in
     * If yes, load next level of game; If not, end the game. 
     * 
     * @param none
     * @return nothing
     */

    public void onLevelFinished() {
        // Clone the goal rectangle and scale it up until it covers the screen

        // Clone the goal rectangle
        Rectangle animatedGoal = new Rectangle(
                goalRect.getX(),
                goalRect.getY(),
                goalRect.getWidth(),
                goalRect.getHeight()
                );
        animatedGoal.setFill(goalRect.getFill());

        // Add the clone to the scene
        List<Node> children = rootGroup.getChildren();
        children.add(children.indexOf(goalRect), animatedGoal);

        // Create the scale animation
        ScaleTransition st = new ScaleTransition(
                Duration.millis(SCALE_TIME), animatedGoal
                );
        st.setInterpolator(Interpolator.EASE_IN);

        // Scale enough to eventually cover the entire scene
        st.setByX(DOUBLE_MULTIPLIER * 
                mainScene.getWidth() / animatedGoal.getWidth());
        st.setByY(DOUBLE_MULTIPLIER * 
                mainScene.getHeight() / animatedGoal.getHeight());

        /*
         * This will be called after the scale animation finishes.
         * If there is no next level, quit. Otherwise switch to it and
         * fade out the animated cloned goal to reveal the new level.
         */
        st.setOnFinished(e1 -> {

            // check if there is no next game and if so, quit
            if (nextGames.isEmpty()){
                return;
            }
            
            // update the instances variables game and nextGames 
            // to switch to the next level
            else{
                game = nextGames.get(0);
                nextGames.remove(0);
                
                // Update UI to the next level, but it won't be visible yet
                // because it's covered by the animated cloned goal
                onLevelLoaded();

                /* use a FadeTransition on animatedGoal, 
                   with FADE_TIME as
                   the duration. Use setOnFinished() to schedule code to
                   run after this animation is finished. */ 

                FadeTransition fade = new FadeTransition(
                        Duration.millis(FADE_TIME), animatedGoal);
                fade.setFromValue(startValue);
                fade.setToValue(endValue);
                
                // When the animation
                // finishes, remove animatedGoal from rootGroup. 
                fade.setOnFinished(e -> {
                    rootGroup.getChildren().remove(animatedGoal);
                    });
                fade.play();  
            }
                });
        // Start the scale animation
        st.play();      
    }

    /** 
     * Performs file IO to populate game and nextGames using filenames from
     * command line arguments.
     *
     * @param none
     * @return nothing
     */
    public void loadLevels() {
        game = null;
        nextGames = new ArrayList<Streamline>();

        List<String> args = getParameters().getRaw();
        if (args.size() == 0) {
            System.out.println("Starting a default-sized random game...");
            game = new Streamline();
            return;
        }

        // at this point args.length == 1

        File file = new File(args.get(0));
        if (!file.exists()) {
            System.out.printf("File %s does not exist. Exiting...", 
                    args.get(0));
            return;
        }

        // if is not a directory, read from the file and start the game
        if (!file.isDirectory()) {
            System.out.printf("Loading single game from file %s...\n", 
                    args.get(0));
            game = new Streamline(args.get(0));
            return;
        }

        // file is a directory, walk the directory and load from all files
        File[] subfiles = file.listFiles();
        Arrays.sort(subfiles);
        for (int i=0; i<subfiles.length; i++) {
            File subfile = subfiles[i];

            // in case there's a directory in there, skip
            if (subfile.isDirectory()) continue;

            // assume all files are properly formatted games, 
            // create a new game for each file, and add it to nextGames
            System.out.printf("Loading game %d/%d from file %s...\n",
                    i+1, subfiles.length, subfile.toString());
            nextGames.add(new Streamline(subfile.toString()));
        }

        // Switch to the first level
        game = nextGames.get(0);
        nextGames.remove(0);
    }

    /**
     * The main entry point for all JavaFX Applications
     * Initializes instance variables, creates the scene, and sets up the UI
     *
     * @param primaryStage the stage to display
     * @return nothing
     */
    @Override
        public void start(Stage primaryStage) throws Exception {
            // Populate game and nextGames
            loadLevels();

            // Initialize the scene and our groups
            rootGroup = new Group();
                    
            mainScene = new Scene(rootGroup, SCENE_WIDTH , SCENE_HEIGHT, Color.GAINSBORO);
            levelGroup = new Group();
            rootGroup.getChildren().add(levelGroup);

            //  initialize goalRect and playerRect, add them to rootGroup,
            //  call onLevelLoaded(), and set up keyboard input handling

            goalRect = new RoundedSquare();
            playerRect = new Player();
            rootGroup.getChildren().addAll(goalRect, playerRect);
        

            onLevelLoaded();
            myKeyHandler = new MyKeyHandler();
            mainScene.setOnKeyPressed(myKeyHandler);
                        
            // Make the scene visible
            primaryStage.setTitle(TITLE);
            primaryStage.setScene(mainScene);
            primaryStage.setResizable(true);
            primaryStage.show();
        }

    /** 
     * Execution begins here, but at this point we don't have a UI yet
     * The only thing to do is call launch() which will eventually result in
     * start() above being called.
     *
     * @param args instructions to take
     * @return nothing
     */
    public static void main(String[] args) {
        if (args.length != 0 && args.length != 1) {
            System.out.print(USAGE);
            return;
        }

        launch(args);
    }
}
