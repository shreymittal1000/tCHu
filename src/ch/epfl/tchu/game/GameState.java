package ch.epfl.tchu.game;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

/**
 * Immutable class representing the game state that is visible only to those given access.
 * This includes the ticket decks, non-public card states and the non-public player state.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class GameState extends PublicGameState{

    /**
     * (SortedBag<Ticket>): represents the deck of tickets in the game.
     */
    private final Deck<Ticket> tickets;

    /**
     * (CardState): represents the card state of the game.
     */
    private final CardState cardState;

    /**
     * (Map<PlayerId, PlayerState>): represents the public player states of the 2 players
     * in the game.
     */
    private final Map<PlayerId, PlayerState> playerState;

    /**
     * Private GameState constructor. Initializes the GameState using a deck of tickets, a
     * card state and a map with keys as PlayerIDs and values as PlayerStates.
     * @param tickets (SortedBag<Ticket>): the deck of tickets in the game.
     * @param cardState (CardState): the card state of the game.
     * @param playerState (Map<PlayerId, PlayerState>): the public player states of the 2
     * players in the game.
     */
    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, 
            Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, GameState.privateToPublicPlayerState(playerState), lastPlayer);
        this.tickets = tickets;
        this.cardState = cardState;
        this.playerState = playerState;
    }

    /**
     * Returns the initial state of a game of tCHu in which the ticket deck contains the given tickets
     * and the card deck contains the Constants.ALL_CARDS cards, without the top 8 (2×4), dealt to the
     * players; these decks are shuffled using the given random generator, which is also used to
     * randomly choose the identity of the first player.
     * @param tickets (SortedBag<Ticket>): the initial set of tickets given.
     * @param rng (Random): a random variable used to randomize results.
     * @return (GameState): a GameState in an "initial" phase.
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Card> cardDeck = Deck.of(SortedBag.of(Constants.ALL_CARDS), rng);
        Map<PlayerId, PlayerState> playerStateStore = new EnumMap<>(PlayerId.class);
        
        for(PlayerId id : PlayerId.ALL) {
            PlayerState ps = PlayerState.initial(cardDeck.topCards(Constants.INITIAL_CARDS_COUNT));
            cardDeck = cardDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
            playerStateStore.put(id, ps);
        }
        
        return new GameState(Deck.of(tickets, rng), CardState.of(cardDeck), PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT)), playerStateStore, null);
    }
    
    /**
     * Returns the player state of the given player.
     * @param playerId (PlayerId): the PlayerId of the player.
     * @return (PublicPlayerState): the player state of the given player.
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Returns the player state of the current player.
     * @return (PublicPlayerState): the player state of the current player.
     */
    @Override
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId());
    }

    /**
     * Returns the count amount of cards on top of the deck of of tickets.
     * @param count (int): the number of cards on top of the deck of tickets to be returned.
     * @return (SortedBag<C>): a SortedBag of the count amount of cards on top of the deck of
     * tickets.
     * @throws IllegalArgumentException if count is less than 0 or if count is more than the
     * size of the deck.
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(0 <= count && count <= tickets.size());
        return tickets.topCards(count);
    }

    /**
     * Returns an identical GameState to the one that calls this method, however, the count
     * amount of tickets at the top have been removed.
     * @param count (int): the number of cards on top of the deck of tickets to be removed.
     * @return (GameState): an identical GameState to the one that calls this method, however,
     * the count amount of tickets at the top have been removed.
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(0 <= count && count <= tickets.size());
        return new GameState(tickets.withoutTopCards(count), cardState, currentPlayerId(), playerState, this.lastPlayer());
    }

    /**
     * Returns the card on top of the deck of cards.
     * @return (Card): the card on top of the deck of cards.
     * @throws IllegalArgumentException if deck of cards (deckCards) is empty.
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return cardState.topDeckCard();
    }

    /**
     * Returns an identical GameState to the caller, except that the card on top of the deck of cards has been removed.
     * @return (GameState): an GameState to the caller, except that the card on top of the deck of cards has been removed 
     * @throws IllegalArgumentException if deck of cards is empty.
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!cardState.isDeckEmpty());
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, this.lastPlayer());
    }

    /**
     * Returns an identical GameState to the caller, but with the given cards added to the pile of discarded cards.
     * @param additionalDiscards (SortedBag<Card>): the cards to be added to the pile of discarded cards.
     * @return (GameState): an identical GameState to the caller, but with the given cards added to the pile of
     * discarded cards.
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, this.lastPlayer());
    }

    /**
     * Returns an identical GameState to the caller, except that if needed, the cards in the discard pile are shuffled
     * by means of the given random generator to form the new deck of cards.
     * @return (GameState): an identical CardState to the caller, except that the cards in the discard pile have been
     * shuffled by means of the given random generator to form the new deck of cards.
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return cardState.isDeckEmpty() 
                ? new GameState(tickets, cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), playerState, this.lastPlayer()) 
                : this;
    }

    /**
     * Returns an identical GameState to the caller, except the given tickets have been added to the given player's hand.
     * @param playerId (PlayerId): the player's id who's hand the tickets must be added to.
     * @param chosenTickets (SortedBag<Ticket>): the tickets to be added to the given player's hand.
     * @return (GameState): an identical GameState to the caller, except the given tickets have been added to the given
     * player's hand.
     * @throws IllegalArgumentException if the player in question already has at least 1 ticket.
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState.get(playerId).ticketCount() < 1);
        
        PlayerState psNew = playerState.get(playerId).withAddedTickets(chosenTickets);
        
        return new GameState(tickets, cardState, currentPlayerId(), updatePlayerStateMap(playerId, psNew), this.lastPlayer());
    }

    /**
     * Returns a GameState identical to the caller, but in which the current player drew the drawnTickets
     * from the top of the deck, and chose to keep the ones contained in chosenTicket.
     * @param drawnTickets (SortedBag<Ticket>): the tickets drawn by the current player.
     * @param chosenTickets (SortedBag<Ticket>): the tickets kept by the current player.
     * @return (GameState): a GameState identical to the caller, but in which the current player drew the
     * drawnTickets from the top of the deck, and chose to keep the ones contained in chosenTicket.
     * @throws IllegalArgumentException if the chosenTickets is not a subset of drawnTickets.
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.subsetsOfSize(chosenTickets.size()).contains(chosenTickets));
        
        PlayerState ps = playerState.get(currentPlayerId()).withAddedTickets(chosenTickets);
        Deck<Ticket> newTickets = tickets.withoutTopCards(drawnTickets.size());
        
        return new GameState(newTickets, cardState, currentPlayerId(), updatePlayerStateMap(currentPlayerId(), ps), this.lastPlayer());
    }

    /**
     * Returns an identical GameState to the caller, except that the face-up index slot card has been replaced by the card at 
     * the top of the drawer, which is removed at the same time. The removed card goes into the current player's hand.
     * @param slot (int): the index of the desired face-up card.
     * @return (GameState): an identical GameState to the caller except replaced a given face-up card (which has moved to the
     * current player's hand) with the card at the top of the deck of cards.
     * @throws IllegalArgumentException if canDrawCards method of PublicGameState returns false.
     */
    public GameState withDrawnFaceUpCard(int slot) {
        PlayerState ps = playerState.get(currentPlayerId()).withAddedCard(cardState.faceUpCard(slot));
        CardState newCardState = cardState.withDrawnFaceUpCard(slot);
        
        return new GameState(tickets, newCardState, currentPlayerId(), updatePlayerStateMap(currentPlayerId(), ps)  , this.lastPlayer());
    }

    /**
     * Returns an identical GameState to the receiver, except that the card on top of the deck of cards has been removed and
     * added to the current player's hand.
     * @return (GameState): an identical GameState to the receiver, except that the card on top of the deck of cards has been
     * removed and added to the current player's hand.
     * @throws IllegalArgumentException if deck of cards (deckCards) is empty.
     */
    public GameState withBlindlyDrawnCard() {
        PlayerState ps = playerState.get(currentPlayerId()).withAddedCard(cardState.topDeckCard());
        CardState newCardState = cardState.withoutTopDeckCard();
        
        return new GameState(tickets , newCardState , currentPlayerId() , updatePlayerStateMap(currentPlayerId(), ps) , this.lastPlayer());
    }

    /**
     * Returns an identical GameState to the receiver, except that the given route has been added to the current player's
     * network having used the current cards.
     * @param route (Route): the route that was claimed.
     * @param cards (SortedBag<Card>): the cards used to claim the route.
     * @return (GameState): an identical GameState to the receiver, except that the given route has been added to the
     * current player's network having used the current cards.
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        PlayerState ps = playerState.get(currentPlayerId()).withClaimedRoute(route, cards);
        CardState newCardState = cardState.withMoreDiscardedCards(cards);
        
        return new GameState(tickets , newCardState , currentPlayerId() , updatePlayerStateMap(currentPlayerId(), ps)  ,  this.lastPlayer());
    }

    /**
     * Returns true if and only if the last turn has started, and false otherwise.
     * @return (boolean): true if and only if the last turn has started, and false otherwise.
     */
    public boolean lastTurnBegins() {
        return this.currentPlayerState().carCount() <= 2 && this.lastPlayer() == null;
    }

    /**
     * Ends the turn for the current player. If lastTurnBegins() returns true then initialize this player as the
     * last player.
     * @return (GameState): an identical GameState to the receiver, except the current player is changed to the other
     * player. If lastTurnBegins() returns true then it also designates the next player as the last player.
     */
    public GameState forNextTurn() {
        return lastTurnBegins() 
                ? new GameState(tickets, cardState, currentPlayerId().next(), playerState, currentPlayerId())
                : new GameState(tickets, cardState, currentPlayerId().next(), playerState, this.lastPlayer());
    }

    /**
     * Private and static method returning a Map that returns the public part of the
     * privatePlayerState's values (while keeping the same keys).
     * @param privatePlayerState (Map<PlayerId, PlayerState>): the map in question.
     * @return (Map<PlayerId, PublicPlayerState>): returns the public part of the
     * privatePlayerState's values (while keeping the same keys).
     */
    private static Map<PlayerId, PublicPlayerState> privateToPublicPlayerState(Map<PlayerId, PlayerState> privatePlayerState) {
        Map<PlayerId, PublicPlayerState> playerStateStore = new EnumMap<PlayerId, PublicPlayerState>(PlayerId.class);
        for(PlayerId id : PlayerId.ALL) {
        	playerStateStore.put(id, privatePlayerState.get(id));
        }
        return playerStateStore;
    }
    
    /**
     * private method that updates the playerState map by replacing the given playerState at the given playerStateId.
     * We made this method to modularize the code as 5 of the methods required updating the playerState map through 
     * the creation of an EnumMap and then replacing the playerState at the given playerId
     * @param Id (PlayerId) : the given playerId used to identify which of the elements in the map we want to update.
     * @param playerState (PlayerState) : the new playerState that we want to change the old playerState to. 
     * @return (Map<PlayerId, PlayerState>) : the new updated playerState map which we often use in the updated gameState.
     */
    private Map<PlayerId, PlayerState> updatePlayerStateMap(PlayerId Id, PlayerState playerState){
        Map<PlayerId, PlayerState> newMap = new EnumMap<PlayerId, PlayerState>(this.playerState);
        newMap.replace(Id, playerState);
        return newMap;
    }
}
