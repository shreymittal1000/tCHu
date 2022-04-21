package ch.epfl.tchu.gui;

import java.util.Map;
import java.util.Random;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Ticket;
import javafx.application.Application;
import javafx.stage.Stage;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class Stage11Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        Map<PlayerId, String> names =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
        Map<PlayerId, Player> players =
                Map.of(PLAYER_1, new GraphicalPlayerAdapter(),
                        PLAYER_2, new GraphicalPlayerAdapter());
        Random rng = new Random();
        new Thread(() -> Game.play(players, names, tickets, rng, 0)).start();
    }


}
