package ch.epfl.tchu.net;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * A class containing all the different Serde types we may need in the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class Serdes {

    /**
     * (Serde<Integer>): a Serde responsible for (de)serializing int fields.
     */
    public static final Serde<Integer> intSerde = Serde.of(
            (i) -> Integer.toString(i) ,
            Integer::parseInt);

    /**
     * (Serde<String>): a Serde responsible for (de)serializing String fields.
     */
    public static final Serde<String> stringSerde = Serde.of(
            (i) -> Base64.getEncoder().encodeToString(i.getBytes(StandardCharsets.UTF_8)) ,
            (i) -> new String (Base64.getDecoder().decode(i.getBytes(StandardCharsets.UTF_8)))
            
            );

    /**
     * (Serde<PlayerId>): a Serde responsible for (de)serializing PlayerId enumerates.
     */
    public static final Serde<PlayerId> playerIdSerde = Serde.oneOf(List.of(PlayerId.values()));
    /**
     * (Serde<TurnKind>): a Serde responsible for (de)serializing TurnKind enumerates.
     */
    public static final Serde<TurnKind> turnKindSerde = Serde.oneOf(TurnKind.ALL);
    /**
     * (Serde<Card>): a Serde responsible for (de)serializing Card enumerates.
     */
    public static final Serde<Card> cardSerde = Serde.oneOf(Card.ALL);
    /**
     * (Serde<Route>): a Serde responsible for (de)serializing Route instances.
     */
    public static final Serde<Route> routeSerde = Serde.oneOf(ChMap.routes());
    /**
     * (Serde<Ticket>): a Serde responsible for (de)serializing Ticket instances.
     */
    public static Serde<Ticket> ticketSerde = Serde.oneOf(ChMap.tickets());

    /**
     * (Serde<List<String>>): a Serde responsible for (de)serializing lists of Strings.
     */
    public static final Serde<List<String>> stringListSerde = Serde.listOf(stringSerde, ',');
    /**
     * (Serde<List<Card>>): a Serde responsible for (de)serializing lists of Cards.
     */
    public static final Serde<List<Card>> cardListSerde = Serde.listOf(cardSerde, ',');
    /**
     * (Serde<List<Route>>): a Serde responsible for (de)serializing lists of Routes.
     */
    public static final Serde<List<Route>> routeListSerde = Serde.listOf(routeSerde, ',');
    /**
     * (Serde<SortedBag<Card>>): a Serde responsible for (de)serializing sorted bags of Cards.
     */
    public static final Serde<SortedBag<Card>> cardBagSerde = Serde.bagOf(cardSerde, ',');
    /**
     * (Serde<SortedBag<Ticket>>): a Serde responsible for (de)serializing sorted bags of Tickets.
     */
    public static Serde<SortedBag<Ticket>> ticketBagSerde = Serde.bagOf(ticketSerde, ',');
    /**
     * (Serde<List<String>>): a Serde responsible for (de)serializing lists of sorted bags of Cards.
     */
    public static final Serde<List<SortedBag<Card>>> cardListBagSerde = Serde.listOf(cardBagSerde, ';');

    /**
     * (Serde<PublicCardState>): a Serde responsible for (de)serializing PublicCardState instances.
     */
    public static final Serde<PublicCardState> publicCardStateSerde = Serde.of(
            (i) -> cardListSerde.serialize(i.faceUpCards()) + 
            ";" + intSerde.serialize(i.deckSize()) + 
            ";" + intSerde.serialize(i.discardsSize())

            , 

            (i) -> {
                List<String> tempList = Arrays.asList(i.split(Pattern.quote(";"), -1));
                return new PublicCardState(
                        cardListSerde.deserialize(tempList.get(0)), 
                        intSerde.deserialize(tempList.get(1)), 
                        intSerde.deserialize(tempList.get(2)));
            });

    /**
     * (Serde<PublicPlayerState>): a Serde responsible for (de)serializing PublicPlayerState instances.
     */
    public static final Serde<PublicPlayerState> publicPlayerStateSerde = Serde.of(
            (i) -> intSerde.serialize(i.ticketCount()) + 
            ";" + intSerde.serialize(i.cardCount()) + 
            ";" + routeListSerde.serialize(i.routes())

            , 

            (i) -> {
                List<String> tempList = Arrays.asList(i.split(Pattern.quote(";"), -1));
                return new PublicPlayerState(
                        intSerde.deserialize(tempList.get(0)), 
                        intSerde.deserialize(tempList.get(1)), 
                        routeListSerde.deserialize(tempList.get(2)));
            });

    /**
     * (Serde<PlayerState>): a Serde responsible for (de)serializing PlayerState instances.
     */
    public static final Serde<PlayerState> playerStateSerde = Serde.of(
            (i) -> ticketBagSerde.serialize(i.tickets()) + 
            ";" + cardBagSerde.serialize(i.cards()) + 
            ";" + routeListSerde.serialize(i.routes())

            , 

            (i) -> {
                List<String> tempList = Arrays.asList(i.split(Pattern.quote(";"), -1));
                return new PlayerState(
                        ticketBagSerde.deserialize(tempList.get(0)), 
                        cardBagSerde.deserialize(tempList.get(1)), 
                        routeListSerde.deserialize(tempList.get(2)));
            });

    /**
     * (Serde<PublicGameState>): a Serde responsible for (de)serializing PublicGameState instances.
     */
    public static final Serde<PublicGameState> publicGameStateSerde = Serde.of(
            (i) -> {
                String playerIdString = i.lastPlayer() == null ? "" : playerIdSerde.serialize(i.lastPlayer());
                String playerStateString = "";
                for(PlayerId id: PlayerId.ALL) {
                    playerStateString += ":" + publicPlayerStateSerde.serialize(i.playerState(id));
                    
                }
                return intSerde.serialize(i.ticketsCount()) + 
                        ":" + publicCardStateSerde.serialize(i.cardState()) + 
                        ":" + playerIdSerde.serialize(i.currentPlayerId()) + 
                        playerStateString + 
                        ":" + playerIdString;
            }
            
            , 

            (i) -> {
                List<String> tempList = Arrays.asList(i.split(Pattern.quote(":"), -1));
                Map<PlayerId, PublicPlayerState> playerStates = new HashMap<>();
                for(PlayerId id: PlayerId.ALL) {
                    playerStates.put(id, publicPlayerStateSerde.deserialize(tempList.get(3+id.ordinal())));
                }

                PlayerId lastPlayer = tempList.get(tempList.size()-1).equals("") ? null : playerIdSerde.deserialize(tempList.get(tempList.size()-1));

                PublicGameState temp = new PublicGameState(
                        intSerde.deserialize(tempList.get(0)).intValue(),
                        publicCardStateSerde.deserialize(tempList.get(1)),
                        playerIdSerde.deserialize(tempList.get(2)),
                        playerStates,
                        lastPlayer
                        );
                return temp;
            }); 
}
