package ch.epfl.tchu.net;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import static java.nio.charset.StandardCharsets.US_ASCII;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Class representing the proxy of a player playing from another device. Implements the Player interface
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class RemotePlayerProxy implements Player{

    /**
     * (Socket): the socket used to connect the client and the proxy.
     */
    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;


    /**
     * Default public RemotePlayerProxy constructor. Initializes an instance using the Socket provided
     * as a parameter.
     * @param socket (Socket): the socket provided as parameter
     */
    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }

    }
    
    @Override
    public String setPlayerName() {
        String toBeSent = MessageId.SET_PLAYER_NAME.name();
        this.sendThroughSocket(writer, toBeSent);
        String toBeReceived = Serdes.stringSerde.deserialize(this.receiveThroughSocket(reader));
        return toBeReceived;
    }
    
    @Override
    public void setPlayerNumber(int playerNum) {
        // TODO Auto-generated method stub
        Constants.THREE_PLAYER = playerNum == 1 ? true : false;
        PlayerId.ALL = Constants.THREE_PLAYER ? List.of(PlayerId.values()) : List.of(PlayerId.PLAYER_1, PlayerId.PLAYER_2);
        PlayerId.COUNT = PlayerId.ALL.size();
        Serdes.ticketSerde = Serde.oneOf(Constants.THREE_PLAYER ? ChMap.THREE_PLAYER_TICKETS : ChMap.TWO_PLAYER_TICKETS);
        Serdes.ticketBagSerde = Serde.bagOf(Serdes.ticketSerde, ',');
        String toBeSent = MessageId.SET_PLAYER_NUM.name() + " " + Serdes.intSerde.serialize(playerNum);
        
        this.sendThroughSocket(writer, toBeSent);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        List<String> namesList = new ArrayList<String>();
        for(int i = 0; i < playerNames.size(); i++) {
            namesList.add(playerNames.get(PlayerId.ALL.get(i)));
        }
        String toBeSent = MessageId.INIT_PLAYERS.name() + " " + Serdes.playerIdSerde.serialize(ownId)
        + " " + Serdes.stringListSerde.serialize(namesList);
        
        this.sendThroughSocket(writer, toBeSent);
    }

    @Override
    public void receiveInfo(String info) {
        String toBeSent = MessageId.RECEIVE_INFO.name() + " " + Serdes.stringSerde.serialize(info);
        this.sendThroughSocket(writer, toBeSent);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String toBeSent = MessageId.UPDATE_STATE.name() + " " + Serdes.publicGameStateSerde.serialize(newState) 
        + " " + Serdes.playerStateSerde.serialize(ownState);
        this.sendThroughSocket(writer, toBeSent);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String toBeSent = MessageId.SET_INITIAL_TICKETS.name() + " " + Serdes.ticketBagSerde.serialize(tickets);
        this.sendThroughSocket(writer, toBeSent);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        String toBeSent = MessageId.CHOOSE_INITIAL_TICKETS.name();
        this.sendThroughSocket(writer, toBeSent);
        return Serdes.ticketBagSerde.deserialize(this.receiveThroughSocket(reader));
    }

    @Override
    public TurnKind nextTurn() {
        String toBeSent = MessageId.NEXT_TURN.name();
        this.sendThroughSocket(writer, toBeSent);
        return Serdes.turnKindSerde.deserialize(this.receiveThroughSocket(reader));
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String toBeSent = MessageId.CHOOSE_TICKETS.name() + " " + Serdes.ticketBagSerde.serialize(options);
        this.sendThroughSocket(writer, toBeSent);
        return Serdes.ticketBagSerde.deserialize(this.receiveThroughSocket(reader));
    }

    @Override
    public int drawSlot() {
        String toBeSent = MessageId.DRAW_SLOT.name();
        this.sendThroughSocket(writer, toBeSent);
        return Serdes.intSerde.deserialize(this.receiveThroughSocket(reader));
    }

    @Override
    public Route claimedRoute() {
        String toBeSent = MessageId.ROUTE.name();
        this.sendThroughSocket(writer, toBeSent);
        return Serdes.routeSerde.deserialize(this.receiveThroughSocket(reader));
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        String toBeSent = MessageId.CARDS.name();
        this.sendThroughSocket(writer, toBeSent);
        return Serdes.cardBagSerde.deserialize(this.receiveThroughSocket(reader));
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String toBeSent = MessageId.CHOOSE_ADDITIONAL_CARDS.name() + " " + Serdes.cardListBagSerde.serialize(options);
        this.sendThroughSocket(writer, toBeSent);
        return Serdes.cardBagSerde.deserialize(this.receiveThroughSocket(reader));
    }

    /**
     * Returns the String that it receives from the socket connection.
     * @return (String): the String that it received from the socket connection.
     */
    private String receiveThroughSocket(BufferedReader reader) {
        try{     
            return reader.readLine();
        }
        catch(IOException e) {
            throw new UncheckedIOException(e);
        }
        finally {
        }
    }

    /**
     * Sends the given String through the socket connection to the remote player.
     * @param toBeSent (String): the String to be sent.
     */
    private void sendThroughSocket(BufferedWriter writer, String toBeSent) {
        try{
            writer.write(toBeSent + " " + "\n");
            writer.flush();
        }
        catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

   

    
}
