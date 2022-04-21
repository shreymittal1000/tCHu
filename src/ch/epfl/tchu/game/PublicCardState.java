package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;

import ch.epfl.tchu.Preconditions;

/**
 * Immutable class representing the card states that are publicly visible to everyone and do not require special access.
 * This includes the 5 face-up cards, the size of the deck and the number of discards.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public class PublicCardState {
	
    private static final int FACE_UP_CARD_COUNT = 5;
	/**
	 * (List<Card>): contains the 5 face-up cards in the game.
	 */
	private final List<Card> faceUpCards;
	
	/**
	 * (int): represents the size of the deck of cards.
	 */
	private final int deckSize;
	
	/**
	 * (int): represents the number of discarded cards.
	 */
	private final int discardsSize;
	
	/**
	 * Default public constructor of PublicCardState. Initializes the PublicCardState by giving it 5 face-up cards, the size of the deck and the number of discarded cards.
	 * @param faceUpCards (List<Card>): contains the 5 face-up cards in the game.
	 * @param deckSize (int): represents the size of the deck of cards.
	 * @param discardsSize (int): represents the number of discarded cards.
	 * @throws IllegalArgumentException if the size of the List faceUpCards is not 5 or if deckSize or discardsSize is negative.
	 */
	public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
		Preconditions.checkArgument(faceUpCards.size() == FACE_UP_CARD_COUNT && deckSize >= 0 && discardsSize >= 0);
		this.faceUpCards = List.copyOf(faceUpCards);
		this.deckSize = deckSize;
		this.discardsSize = discardsSize;
	}
	
	/**
	 * Returns the 5 cards face up, in the form of a List with exactly 5 items.
	 * @return (List<Card>): the 5 cards face up, in the form of a List with exactly 5 items.
	 */
	public List<Card> faceUpCards(){
		return faceUpCards;
	}
	
	/**
	 * Returns the face-up card at the given index.
	 * @param slot (int): the index of the desired face-up card.
	 * @return (Card): the face-up card at the given index.
	 * @throws IndexOutOfBoundsException if the index is not between 0 (included) and 5 (excluded).
	 */
	public Card faceUpCard(int slot) {
		return faceUpCards.get(Objects.checkIndex(slot, faceUpCards.size()));
	}
	
	/**
	 * Returns the size of the deck of cards.
	 * @return (int): the size of the deck of cards.
	 */
	public int deckSize() {
		return deckSize;
	}
	
	/**
	 * Returns true if the deck of cards is empty.
	 * @return (boolean): true if deckSize = 0, false otherwise.
	 */
	public boolean isDeckEmpty() {
		return deckSize == 0;
	}
	
	/**
	 * Returns the number of discarded cards.
	 * @return (int): the number of discarded cards.
	 */
	public int discardsSize() {
		return discardsSize;
	}
}
