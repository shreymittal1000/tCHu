package ch.epfl.tchu.net;

import java.util.List;

/**
 * The enumerable type MessageId represents the different types of message IDs in the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public enum MessageId {
    INIT_PLAYERS, RECEIVE_INFO, UPDATE_STATE, SET_INITIAL_TICKETS, CHOOSE_INITIAL_TICKETS,
    NEXT_TURN, CHOOSE_TICKETS, DRAW_SLOT, ROUTE, CARDS, CHOOSE_ADDITIONAL_CARDS, SET_PLAYER_NUM, SET_PLAYER_NAME;

    /**
     * (List<MessageId>): list of all the different types of MessageId.
     */
    public static final List<MessageId> ALL = List.of(MessageId.values());

    /**
     * (int): number of types of MessageId that exist.
     */
    public static final int COUNT = ALL.size();
}