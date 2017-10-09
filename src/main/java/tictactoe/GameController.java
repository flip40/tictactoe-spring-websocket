package tictactoe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

	@Autowired
	private GameStateRepository repository;

	@MessageMapping("/move/{id}")
	@SendTo("/ttt/gamestate/{id}")
	public GameStateModel gamestate(@DestinationVariable String id, PlayerMoveMessage move) throws Exception {
		GameStateModel game = repository.findById(id);
		
		game.makeMove(move.getX(), move.getY(), move.getPlayer());
		repository.save(game);

		return game;
	}

}
