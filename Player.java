/**
 * Author: Yue Sun
 * email: yus002@ucsd.edu
 * CSE8B Login : cs8bwadc
 * Date: 3/6/19
 * File: Player.java 
 * Sources of Help: PA6 writeup
 *
 * This file is a solution of PSA6
 * This file contains the Player class
 */


import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/**
 * Uses Player class to contain 
 * method setSize()
 * to realize the function of it
 *
 * Instance variable: none
 */

public class Player extends RoundedSquare {
    final static double STROKE_FRACTION = 0.1;
    static final Color PLAYER_COLOR = Color.GOLD;
    static final Color STROKE_COLOR = Color.RED;
    
    /**
     * This constructor is used to set a fill color, a stroke color, 
     * and set the stroke type to centered
     * @param none
     * @return nothing 
     */

    public Player() {
        setFill(PLAYER_COLOR);
        setStroke(STROKE_COLOR);
        setStrokeType(StrokeType.CENTERED);
  
    }
    
    /**
     * This method is used to update the stroke width based on the size and 
     * STROKE_FRACTION
     * call super setSize(), bearing in mind that the size
     * parameter we are passed here includes stroke but the
     * superclass's setSize() does not include the stroke
     *
     * @param size the stroke width
     * @return nothing 
     */
    
    @Override
    public void setSize(double size) {
    
        setStrokeWidth(STROKE_FRACTION * size);
        super.setSize(size - STROKE_FRACTION * size);

    }
}
