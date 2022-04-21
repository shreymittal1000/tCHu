package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Immutable class representing the card states that are visible only to those given access.
 * This includes the deck of cards and the discarded cards.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class CardState extends PublicCardState{
	
    private final static int FACE_UP_CARD_COUNT = 5;
	/**
	 * (Deck<Card>): is the deck of cards.
	 */
	private final Deck<Card> deckCards;
	
	/**
	 * (SortedBag<Card>): is the pile of discarded cards.
	 */
	private final SortedBag<Card> discardedCards;
	
	/**
	 * Private CardState constructor. Initializes the CardState using a starting deck (named deck).
	 * Works by assigning the first 5 cards of deck as face-up cards and the rest as deck cards.
	 * Initializes the pile of discarded cards as empty.
	 * @param deck (Deck<Card>): the starting deck used to initialize a CardState.
	 * @throws IllegalArgumentException if the size of deck is below 5.
	 */
	private CardState(Deck<Card> deck) {
		super(deck.topCards(FACE_UP_CARD_COUNT).toList(), deck.size()-FACE_UP_CARD_COUNT, 0);
		deckCards = deck.withoutTopCards(FACE_UP_CARD_COUNT);
		this.discardedCards = SortedBag.of();
	}
	
	/**
	 * Private CardState constructor. Initialized the CardState using a List of faceUpCards, a Deck of deckCards and a SortedBag of discardedCards.
	 * @param faceUpCards (List<Card>): the list of face-up cards.
	 * @param deckCards (Deck<Card>): the deck of cards.
	 * @param discardedCards (SortedBag<Card>): the pile of discarded cards.
	 * @throws IllegalArgumentException if the size of faceUpCards is not 5.
	 */
	private CardState(List<Card> faceUpCards, Deck<Card> deckCards, SortedBag<Card> discardedCards) {
		super(faceUpCards, deckCards.size(), discardedCards.size());
		this.deckCards = deckCards;
		this.discardedCards = discardedCards;
	}
	
	/**
	 * Creates a CardState using a starting deck (named deck).
	 * @param deck (Deck<Card>): the starting deck used to create a CardState.
	 * @return (CardState): a new CardState created using the starting deck provided as parameter.
	 * @throws IllegalArgumentException if the size of deck is below 5.
	 */
	public static CardState of(Deck<Card> deck) {
		Preconditions.checkArgument(deck.size() >= FACE_UP_CARD_COUNT);
		return new CardState(deck);
	}
	
	/**
	 * Returns an identical CardState to the caller, except that the face-up index slot card has been replaced by the card at 
	 * the top of the drawer, which is removed at the same time.
	 * @param slot (int): the index of the desired face-up card.
	 * @return (CardState): an identical CardState to the caller except replaced a given face-up card with the card at the top
	 * of the deck of cards.
	 * @throws IllegalArgumentException if the deck of cards (deckCards) is empty.
	 * @throws IndexOutOfBoundsException if slot is not between 0 and 4 (both inclusive).
	 */
	public CardState withDrawnFaceUpCard(int slot) {
		Preconditions.checkArgument(!isDeckEmpty());
		List<Card> newFaceUpCards = new ArrayList<>(super.faceUpCards());
		newFaceUpCards.set(Objects.checkIndex(slot, newFaceUpCards.size()), this.deckCards.topCard());
		return new CardState(newFaceUpCards, deckCards.withoutTopCard(), SortedBag.of(this.discardedCards));
	}
	
	/**
     * Returns the card on top of the deck of cards.
     * @return (Card): the card on top of the deck of cards.
     * @throws IllegalArgumentException if deck of cards (deckCards) is empty.
     */
	public Card topDeckCard() {
		Preconditions.checkArgument(!deckCards.isEmpty());
		return deckCards.topCard();
	}
	
	/**
	 * Returns an identical CardState to the receiver, except that the card on top of the deck of cards has been removed.
	 * @return (CardState): an identical CardState to the receiver, except that the card on top of the deck of cards has been
	 * removed.
	 * @throws IllegalArgumentException if deck of cards (deckCards) is empty.
	 */
	public CardState withoutTopDeckCard() {
		Preconditions.checkArgument(!deckCards.isEmpty());
		return new CardState(super.faceUpCards(), deckCards.withoutTopCard(), SortedBag.of(discardedCards));
	}
	
	/**
	 * Returns an identical CardState to the caller, except that the cards in the discard pile have been shuffled by means
	 * of the given random generator to form the new deck of cards.
	 * @param rng (Random): a random variable used to shuffle the cards.
	 * @return (CardState): an identical CardState to the caller, except that the cards in the discard pile have been shuffled
	 * by means of the given random generator to form the new deck of cards.
	 * @throws IllegalArgumentException if deck of cards (deckCards) is not empty.
	 */
	public CardState withDeckRecreatedFromDiscards(Random rng){
		Preconditions.checkArgument(deckCards.isEmpty());
		return new CardState(super.faceUpCards(), Deck.of(discardedCards, rng), SortedBag.of());
	}
	
	/**
	 * Returns an identical CardState to the caller, but with the given cards added to the pile of discarded cards.
	 * @param additionalDiscards (SortedBag<Card>): the cards to be added to the pile of discarded cards.
	 * @return (CardState): an identical CardState to the caller, but with the given cards added to the pile of discarded cards.
	 */
	public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
		return new CardState(super.faceUpCards(), deckCards, discardedCards.union(additionalDiscards));
	}
}
