package ch.epfl.tchu.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import static java.nio.charset.StandardCharsets.US_ASCII;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.Player.TurnKind;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Represents the client device of the distant player.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class RemotePlayerClient {

	/**
	 * (Player): the player to which it must provide remote access.
	 */
    private final Player player;
    
    /**
     * (String): the name of the socket used for communication between itself and the proxy.
     */
    private final String name;
    
    /**
     * (int): the port to be used.
     */
    private final int port;
    
    private String playerName;

    /**
     * Default RemotePlayerClient constructor. Initializes an instance by using a player, a
     * socket name and a port provided as a parameter.
     * @param player (Player): the player to which it must provide remote access.
     * @param name (String): the name of the socket used for communication between itself and the proxy.
     * @param port (int): the port to be used.
     */
    public RemotePlayerClient(Player player, String name, int port, String playerName) {
        this.player = player;
        this.name = name;
        this.port = port;
        this.playerName = playerName;
    }

    /**
     * Method that sets up a socket and lets it communicate with the proxy.
     */
    public void run() {
        try {
            Socket s = new Socket(name, port);
            
            BufferedReader r =
                    new BufferedReader(
                            new InputStreamReader(s.getInputStream(), US_ASCII));
            
            BufferedWriter w =
                    new BufferedWriter(
                            new OutputStreamWriter(s.getOutputStream(), US_ASCII) );
            while(true) {
                String readLine = r.readLine();
                if(readLine == null) {
                    break;
                }
                List<String> messageList = Arrays.asList(readLine.split(Pattern.quote(" "), -1));
                switch(MessageId.valueOf(messageList.get(0))) {
                
                case SET_PLAYER_NAME: 
                    player.setPlayerName();
                    writeAndFlush(w, Serdes.stringSerde.serialize(playerName) + "\n");
                    break;
                    
                case SET_PLAYER_NUM: 
                    
                    player.setPlayerNumber(Serdes.intSerde.deserialize(messageList.get(1)));
                    Constants.THREE_PLAYER = Serdes.intSerde.deserialize(messageList.get(1)) == 1 ? true : false;
                    PlayerId.ALL = Constants.THREE_PLAYER ? List.of(PlayerId.values()) : List.of(PlayerId.PLAYER_1, PlayerId.PLAYER_2);
                    PlayerId.COUNT = PlayerId.ALL.size();
                    Serdes.ticketSerde = Serde.oneOf(Constants.THREE_PLAYER ? ChMap.THREE_PLAYER_TICKETS : ChMap.TWO_PLAYER_TICKETS);
                    Serdes.ticketBagSerde = Serde.bagOf(Serdes.ticketSerde, ',');
                    break;
                    
                case INIT_PLAYERS:
                    List<String> namesList = Serdes.stringListSerde.deserialize(messageList.get(2));
                    Map<PlayerId, String> namesMap = new HashMap<>();
                    for(int i = 0; i < namesList.size(); i++) {
                        namesMap.put(PlayerId.ALL.get(i), namesList.get(i));
                    }
                    player.initPlayers(Serdes.playerIdSerde.deserialize(messageList.get(1)), namesMap);
                    break;

                case RECEIVE_INFO:
                    String info = Serdes.stringSerde.deserialize(messageList.get(1));
                    player.receiveInfo(info);
                    break;

                case UPDATE_STATE: 
                    PublicGameState gs = Serdes.publicGameStateSerde.deserialize(messageList.get(1));
                    PlayerState ps = Serdes.playerStateSerde.deserialize(messageList.get(2));
                    player.updateState(gs, ps);
                    break;

                case SET_INITIAL_TICKETS:
                    SortedBag<Ticket> tickets = Serdes.ticketBagSerde.deserialize(messageList.get(1));
                    player.setInitialTicketChoice(tickets);
                    break;

                case CHOOSE_INITIAL_TICKETS:
                    SortedBag<Ticket> initialTickets = player.chooseInitialTickets();
                    writeAndFlush(w, Serdes.ticketBagSerde.serialize(initialTickets) + "\n");
                    break;

                case NEXT_TURN:
                    TurnKind nextTurn = player.nextTurn();
                    writeAndFlush(w, Serdes.turnKindSerde.serialize(nextTurn)+ "\n");
                    break;

                case CHOOSE_TICKETS:
                    SortedBag<Ticket> options = Serdes.ticketBagSerde.deserialize(messageList.get(1));
                    SortedBag<Ticket> chosenTickets = player.chooseTickets(options);
                    writeAndFlush(w, Serdes.ticketBagSerde.serialize(chosenTickets)+ "\n");
                    break;

                case DRAW_SLOT: 
                    int drawSlot = player.drawSlot();
                    writeAndFlush(w, Serdes.intSerde.serialize(drawSlot)+ "\n");
                    break;

                case ROUTE: 
                    Route claimedRoute = player.claimedRoute();
                    writeAndFlush(w, Serdes.routeSerde.serialize(claimedRoute)+ "\n");
                    break;

                case CARDS: 
                    SortedBag<Card> cards = player.initialClaimCards();
                    writeAndFlush(w, Serdes.cardBagSerde.serialize(cards)+ "\n");
                    break;

                case CHOOSE_ADDITIONAL_CARDS:    
                    List<SortedBag<Card>>  additionalOptions = Serdes.cardListBagSerde.deserialize(messageList.get(1));
                    SortedBag<Card> chosenAdditionalCards = player.chooseAdditionalCards(additionalOptions);
                    writeAndFlush(w, Serdes.cardBagSerde.serialize(chosenAdditionalCards)+ "\n");
                    break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
    
    /**
     * Method that writes the string provided as argument into a connection using the BufferedWriter provided
     * as parameter. Then, the method flushes the BufferedWriter to make sure all items are sent through.
     * @param w (BufferedWriter): the BufferedWriter used to send Strings through the Socket connection.
     * @param toWrite (String): the String to send through the connection.
     * @throws IOException if an I/O Error occurs.
     */
    private void writeAndFlush(BufferedWriter w, String toWrite) throws IOException{
		w.write(toWrite);
		w.flush();
    }
}




