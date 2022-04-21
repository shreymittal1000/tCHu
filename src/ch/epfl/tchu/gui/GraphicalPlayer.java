package ch.epfl.tchu.gui;

import java.util.List;
import java.util.Map;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ChooseTicketsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import static javafx.collections.FXCollections.observableArrayList;

/**
 * Class that represents a Graphical Interface of a player. It must be noted
 * that despite its name, it does not implement the Player interface.
 * 
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
public final class GraphicalPlayer {
    private final ObservableGameState gameState;
    private final ObservableList<Text> infos;
    private final ObjectProperty<DrawTicketsHandler> drawTicketProperty;
    private final ObjectProperty<DrawCardHandler> drawCardProperty;
    private final ObjectProperty<ClaimRouteHandler> claimRouteProperty;
    private final Stage mainStage;
    private final Node mapView;

    /**
     * Default GraphicalPlayer constructor. Initializes a GraphicalPlayer by
     * taking the ID of the player whose GUI it will represent, along with a map
     * of the names of the 2 players (accessible using their player IDs).
     * 
     * @param id
     *            (PlayerId): ID of the player whose GUI this instance of
     *            Graphical Player will represent.
     * @param playerNames
     *            (Map<PlayerId, String>): a map of the names of the 2 players
     *            (accessible using their player IDs).
     */
    public GraphicalPlayer(PlayerId id, Map<PlayerId, String> playerNames) {
        // Initializing the handler Properties and ObservableGameState
        infos = observableArrayList();
        gameState = new ObservableGameState(id);
        drawTicketProperty = new SimpleObjectProperty<>();
        drawCardProperty = new SimpleObjectProperty<>();
        claimRouteProperty = new SimpleObjectProperty<>();

        // Creation of the four main sections of the game
        mapView = MapViewCreator.createMapView(gameState, claimRouteProperty, this::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTicketProperty, drawCardProperty);
        Node handView = DecksViewCreator.createHandView(gameState);
        Node infoView = InfoViewCreator.createInfoView(id, playerNames, gameState, infos);

        // Creation + Modification of the stage, pane and scene on which the views are displayed. 
        mainStage = new Stage();
        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        Scene mainScene = new Scene(mainPane);
        mainStage.setScene(mainScene);
        mainStage.setTitle("tCHu — " + playerNames.get(id));
        mainStage.show();
    }

    /**
     * Updates all the attributes within the ObservableGameState using the
     * values provided in the parameters of the method. Does this by calling the
     * setState method onto the ObservableGameState of this instance of
     * GraphicalPlayer.
     * 
     * @param newGameState
     *            (PublicGameState): the new public game state.
     * @param newPlayerState
     *            (PlayerState): the new player state.
     */
    public void setState(PublicGameState newGameState,
            PlayerState newPlayerState) {
        assert Platform.isFxApplicationThread();
        gameState.setState(newGameState, newPlayerState);
    }

    /**
     * Takes a message about an event and adds it to the bottom of the Info View
     * of the GUI.
     * 
     * @param message
     *            (String): the message to be added to the bottom of the GUI.
     */
    public void receiveInfo(String message) {
        assert Platform.isFxApplicationThread();
        infos.add(new Text(message));
        if (infos.size() > 5) {
            infos.remove(0);
        }
    }

    /**
     * Defines the properties of all the action handlers of the GUI. Once a
     * button is clicked, it also disables the other actions to make sure only
     * one "action" is taken per turn.
     * 
     * @param dth
     *            (DrawTicketsHandler): the DrawTicketsHandler to be used.
     * @param dch
     *            (DrawCardHanlder): the DrawCardHandler to be used.
     * @param crh
     *            (ClaimRouteHandler): the ClaimRouteHandler to be used.
     */
    public void startTurn(DrawTicketsHandler dth, DrawCardHandler dch,
            ClaimRouteHandler crh) {
        assert Platform.isFxApplicationThread();

        drawTicketProperty.set(!gameState.canDrawTickets() ? null : () -> {
            disableHandlers();
            dth.onDrawTickets();
        });

        drawCardProperty.set(!gameState.canDrawCards() ? null : (int i) -> {
            disableHandlers();
            dch.onDrawCard(i);
            drawCard(dch);
        });

        claimRouteProperty.set((route, claimCards) -> {
            disableHandlers();
            crh.onClaimRoute(route, claimCards);
        });
    }

    /**
     * Opens the ticket choosing window, where a player can choose which tickets
     * they wish to keep.
     * 
     * @param ticketChoices
     *            (SortedBag<Ticket>): the choice of tickets the player has.
     * @param tch
     *            (ChooseTicketsHandler): the action handler allowing
     *            interaction between the player and the GUI window where
     *            tickets to be kept are chosen.
     */
    public void chooseTickets(SortedBag<Ticket> ticketChoices,
            ChooseTicketsHandler tch) {
        assert Platform.isFxApplicationThread();
        Preconditions.checkArgument(ticketChoices.size() == 5 || ticketChoices.size() == 3);

        Stage chooserStage = new Stage(StageStyle.UTILITY);

        String message = String.format(StringsFr.CHOOSE_TICKETS,
                ticketChoices.size() - 2,
                StringsFr.plural(ticketChoices.size() - 2));

        ObservableList<Ticket> observableList = observableArrayList(ticketChoices.toList());
        ListView<Ticket> list = new ListView<>(observableList);
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button chooserButton = new Button();

        chooserButton.disableProperty()
            .bind(Bindings.size(list.getSelectionModel().getSelectedItems())
            .lessThan(ticketChoices.size() - 2));

        chooserButton.setOnAction((e) -> {
            chooserStage.hide();
            tch.onChooseTickets(
                    SortedBag.of(list.getSelectionModel().getSelectedItems()));
        });

        createChooser(chooserStage, StringsFr.TICKETS_CHOICE, message, chooserButton, list);
        chooserStage.show();
    }

    /**
     * Allows the player to interact with the GUI to be able to draw either a
     * face-up card or a card from the deck of cards.
     * 
     * @param dch
     *            (DrawCardHandler): the action handler that allows the drawing
     *            of cards.
     */
    public void drawCard(DrawCardHandler dch) {
        assert Platform.isFxApplicationThread();
        drawCardProperty.set((int i) -> {
            disableHandlers();
            dch.onDrawCard(i);
        });
    }

    /**
     * Opens a card choosing window, where a player can choose which cards they
     * with to use to claim a route.
     * 
     * @param claimCards
     *            (List<SortedBag<Card>>): the possible combinations of claim
     *            cards that can be used to claim a route.
     * @param cch
     *            (ChooseCardsHandler): the action handler allowing interaction
     *            between the player and the GUI window where the player can
     *            choose which cards to use to claim a route.
     */
    public void chooseClaimCards(List<SortedBag<Card>> claimCards, ChooseCardsHandler cch) {
        assert Platform.isFxApplicationThread();

        String message = StringsFr.CHOOSE_CARDS;
        Stage chooserStage = new Stage(StageStyle.UTILITY);
        ObservableList<SortedBag<Card>> observableList = observableArrayList(claimCards);
        ListView<SortedBag<Card>> list = new ListView<>(observableList);
        list.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button chooserButton = new Button();

        

        chooserButton.disableProperty()
            .bind(Bindings.size(list.getSelectionModel().getSelectedItems())
            .lessThan(1));

        // Creation of Button Handler
        chooserButton.setOnAction((e) -> {
            chooserStage.hide();
            cch.onChooseCards(list.getSelectionModel().getSelectedItem());
        });
        createChooser(chooserStage, StringsFr.CARDS_CHOICE, message, chooserButton, list);
        chooserStage.show();
    }
    
    /**
     * Opens a card choosing window, where a player can choose which additional
     * cards they with to use to claim a route.
     * 
     * @param claimCards
     *            (List<SortedBag<Card>>): the possible combinations of
     *            additional claim cards that can be used to claim a route.
     * @param cch
     *            (ChooseCardsHandler): the action handler allowing interaction
     *            between the player and the GUI window where the player can
     *            choose which additional cards to use to claim a route.
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> claimCards,
            ChooseCardsHandler cch) {
        assert Platform.isFxApplicationThread();
        Preconditions.checkArgument(claimCards.size() > 0);
        Stage chooserStage = new Stage(StageStyle.UTILITY);
        String message = StringsFr.CHOOSE_ADDITIONAL_CARDS;
        
        ObservableList<SortedBag<Card>> observableList = observableArrayList(claimCards);
        ListView<SortedBag<Card>> list = new ListView<>(observableList);
        list.setCellFactory( v -> new TextFieldListCell<>(new CardBagStringConverter()) );
        
        Button chooserButton = new Button();

        createChooser(chooserStage, StringsFr.CARDS_CHOICE, message, chooserButton, list);

        // Creation of Button Handler
        chooserButton.setOnAction((e) -> {
            chooserStage.hide();
            cch.onChooseCards(list.getSelectionModel().getSelectedItem() == null ? SortedBag.of() : list.getSelectionModel().getSelectedItem());
        });
        chooserStage.show();
    }

    /**
     * Disables the 3 action handlers used by the GUI.
     */
    private void disableHandlers() {
        drawTicketProperty.set(null);
        drawCardProperty.set(null);
        claimRouteProperty.set(null);
    }

    /**
     * Returns a GUI pop-up window used for allowing the player to choose
     * between certain in-game options using a title and intro text, the minimum
     * number of options needed to be chosen by the player for the window to
     * become closeable, and whether there are multiple options that can be
     * chosen.
     * 
     * @param titleText
     *            (String): the title of the pop-up window.
     * @param introText
     *            (String): the introductory text of the pop-up window.
     * @param minimum
     *            (int): the minimum number of options needed to be chosen by
     *            the player for the window to become closeable.
     * @param multiple
     *            (boolean): whether there are multiple options that can be
     *            chosen.
     * @return (Stage): a GUI pop-up window used for allowing the player to
     *         choose between certain in-game options.
     */
    private void createChooser(Stage chooserStage, String titleText,
            String message, Button chooserButton, ListView<?> list) {

        // Creation of Necessary Components along with their heirarchy
        Text text = new Text(message);
        TextFlow texts = new TextFlow(text);
        VBox options = new VBox(texts, list, chooserButton);
        Scene chooser = new Scene(options);

        // Modification of the components
        chooser.getStylesheets().add("chooser.css");
        chooserButton.setText(StringsFr.CHOOSE);
        chooserStage.setScene(chooser);
        chooserStage.initOwner(mainStage);
        chooserStage.initModality(Modality.WINDOW_MODAL);
        chooserStage.setOnCloseRequest(Event::consume);
        chooserStage.setTitle(titleText);
    }

    /**
     * Class allowing for the representation of a sorted bag of cards as a
     * String.
     * 
     * @author Shrey Mittal (312275)
     * @author David Chernis (310298)
     */
    public final class CardBagStringConverter
    extends StringConverter<SortedBag<Card>> {

        @Override
        public String toString(SortedBag<Card> cards) {
            return Info.cardsRepresentation(cards);
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }

    };
}
