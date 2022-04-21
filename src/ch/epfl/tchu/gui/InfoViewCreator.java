package ch.epfl.tchu.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static javafx.geometry.Orientation.HORIZONTAL;
/**
 * Class that allows for the creation of the graphical elements needed to display the infos of the game.
 * @author Shrey Mittal (312275)
 * @author David Chernis (310298)
 */
final class InfoViewCreator {
    private static final int circleRadius = 5;
	/**
	 * Private InfoViewCreator constructor, whose sole aim is to make this class uninstanciable.
	 */
	private InfoViewCreator() {};
	
	/**
	 * Returns a graphical view of the infos of the players.
	 * @param ogs (ObservableGameState): the observable game state of the game.
	 * @return (VBox): a graphical view of the infos of the players.
	 */
	public static VBox createInfoView(PlayerId id, Map<PlayerId, String> playerNames,  ObservableGameState ogs, ObservableList<Text> infos) {
		//The VBox containing the player statistics
		VBox playerStats = new VBox();
		playerStats.setId("player-stats");
		
		for(int i = 0; i < playerNames.size(); i++) {
			int ordinal = (id.ordinal() + i) % playerNames.size();
			PlayerId currentId = PlayerId.ALL.get(ordinal);
			
			//The circles that represents the color of each player
			Circle circle = new Circle(circleRadius);
			circle.getStyleClass().add("filled");
			
			//The text part of the player statistics
			Text text = new Text();
			StringExpression stringExpression = Bindings.format(StringsFr.PLAYER_STATS, playerNames.get(currentId),
					ogs.playerTickets(currentId), ogs.playerCards(currentId),
					ogs.playerWagons(currentId), ogs.playerClaimPoints(currentId));
			text.textProperty().bind(stringExpression);
			
			//The TextFlow of each player
			TextFlow textFlow = new TextFlow(circle, text);
			textFlow.getStyleClass().add(currentId == PLAYER_1 ? "PLAYER_1" : (currentId == PLAYER_2 ? "PLAYER_2" : "PLAYER_3"));
			
			playerStats.getChildren().add(textFlow);
		}
		
		//The separator that separates the player statistics and the last 5 game infos
		Separator separator = new Separator();
		separator.orientationProperty().set(HORIZONTAL);
		
		//The TextFlow for the last 5 game infos
		TextFlow messages = new TextFlow();
		Bindings.bindContent(messages.getChildren(), infos);
		messages.setId("game-info");
		
		//The final Info View VBox, containing all the elements described above
		VBox infoView = new VBox(playerStats, separator, messages);
		infoView.getStylesheets().addAll("info.css", "colors.css");
		
		return infoView;
	}
	
}
