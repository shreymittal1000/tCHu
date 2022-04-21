package ch.epfl.tchu.net;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import static ch.epfl.tchu.game.Card.*;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;

import static ch.epfl.tchu.game.PlayerId.*;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.PublicPlayerState;
import ch.epfl.tchu.game.Route;

public class SerdesTest {
    
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    @SuppressWarnings("unused")
	private static String randomName(Random rng, int length) {
        var sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        return sb.toString();
    }
    
    @Test
    void publicGameStateSerdeWorks() {
        List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
          PLAYER_1, new PublicPlayerState(10, 11, rs1),
          PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs = new PublicGameState(40, cs, PLAYER_2, ps, null);
        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:" , Serdes.publicGameStateSerde.serialize(gs));
        @SuppressWarnings("unused")
		PublicGameState gsNew = Serdes.publicGameStateSerde.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:");
    }
}