package ch.epfl.tchu.game;

import java.util.List;

/**
 * The enumerable type Color represents the color of a card.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public enum Color {
    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE; 
	
	/**
	 * (List<Color>): a list of all the different Colors.
	 */
    public static final List<Color> ALL = List.of(Color.values());
    
    /**
     * (int): the number of the different Colors.
     */
    public static final int COUNT = ALL.size(); 
}