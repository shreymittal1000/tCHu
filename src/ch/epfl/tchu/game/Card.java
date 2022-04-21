package ch.epfl.tchu.game;

import java.util.List;

import ch.epfl.tchu.Preconditions;

/**
 * The enumerable type Card represents the different types of cards a player can have.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public enum Card {
    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE, LOCOMOTIVE;
	
	/**
	 * (List<Card>): list of all the different types of Cards.
	 */
    public static final List<Card> ALL = List.of(Card.values());
    
    /**
     * (int): number of types of Cards.
     */
    public static final int COUNT = ALL.size();
    
    /**
     * (List<Card>): list of all the different types of Card that are cars.
     */
    public static final List<Card> CARS = ALL.subList(0, COUNT-1);
    
    /**
     * Method that returns the type of card that matches the given color.
     * @param color (Color): Color of the car.
     * @return (Card): car matching the Color.
     */
    public static Card of(Color color) {
        Preconditions.checkArgument(color != null);
        return CARS.get(color.ordinal());
    }
    
    /**
     * Method that return the Color of the caller of this method.
     * @return (Color): color matching the ordinal.
     */
    public Color color() {
        return this != Card.LOCOMOTIVE ? Color.ALL.get(this.ordinal()) : null; 
    }
}
