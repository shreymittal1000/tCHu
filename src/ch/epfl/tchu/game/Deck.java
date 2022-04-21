package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Immutable class representing a deck of cards in the game.
 * Is a generic class as the types of cards contained are not decided in advance.
 * Is used to represent a deck of tickets or a deck of cards in the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 * @param <C extends Comparable<C>> represents what type of object the deck will consist of.
 */
public final class Deck<C extends Comparable<C>> {

    /**
     * (List<C>): The List containing all the cards of the deck.
     */
    private final List<C> cards;

    /**
     * Private Deck constructor. Initializes a Deck constructor by giving it a SortedBag of generic type C.
     * @param cards (SortedBag<C>): the SortedBag of cards of the deck.
     */
    private Deck(List<C> cards) {
        this.cards = cards;
    }

    /**
     * Creates a shuffled deck containing the elements of a SortedBag<C>.
     * @param <C> The type of cards to be contained in the shuffled deck.
     * @param cards (SortedBag<C>): the set of cards to be shuffled and contained into a deck.
     * @param rng (Random): random variable used to shuffle the cards.
     * @return (Deck<C>): a shuffled deck of the elements given in cards.
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        List<C> cardList = cards.toList();
        Collections.shuffle(cardList, rng);
        return new Deck<C>(cardList);
    }

    /**
     * Returns the number of cards in the deck.
     * @return (int): the number of cards in the deck.
     */
    public int size() {
        return cards.size();
    }

    /**
     * Returns the logical answer to whether the deck is empty.
     * @return (boolean): true if the deck is empty, false otherwise.
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Returns the card on top of the deck.
     * @return (C): the card on top of the deck.
     * @throws IllegalArgumentException if cards is empty.
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());
        return topCards(1).get(0);
    }

    /**
     * Returns the deck of this instance of Deck without its top card.
     * @return (Deck<C>): the deck of this instance of Deck without its top card.
     * @throws IllegalArgumentException if cards is empty.
     */
    public Deck<C> withoutTopCard(){
        Preconditions.checkArgument(!isEmpty());
        return withoutTopCards(1);
    }

    /**
     * Returns the count amount of cards on top of the deck.
     * @param count (int): the number of cards on top of the deck to be returned.
     * @return (SortedBag<C>): a SortedBag of the count amount of cards on top of the deck.
     * @throws IllegalArgumentException if count is less than 0 or if count is more than the size of the deck.
     */
    public SortedBag<C> topCards(int count){
        Preconditions.checkArgument(0 <= count && count <= size());
        return SortedBag.of(cards.subList(0, count));
    }

    /**
     * Returns the deck of this instance of Deck without its count amount of top cards.
     * @param count (int): the number of cards on top of the deck to be removed.
     * @return (Deck<C>): the deck of this instance of Deck without its count amount of top cards.
     * @throws IllegalArgumentException if count is less than 0 or if count is more than the size of the deck.
     */
    public Deck<C> withoutTopCards(int count){
        Preconditions.checkArgument(0 <= count && count <= size());
        return new Deck<C>(cards.subList(count, cards.size()));
    }
}
