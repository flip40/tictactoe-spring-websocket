package tictactoe;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {

	@Autowired
	private GameStateRepository repository;

	@Autowired
	private SimpMessagingTemplate template;

	@RequestMapping(method = RequestMethod.GET, value = "/ttt/games")
	public List<AvailableGame> getServers() {
		List<AvailableGame> available = new ArrayList<AvailableGame>();
		List<GameStateModel> games = repository.findByStartedAndDisconnect(false, false);

		for(GameStateModel game : games) {
			available.add(new AvailableGame(game.id, game.name));
		}

		return available;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/ttt/game")
	public GameStateModel createGame(
			@RequestParam(value = "player") String player,
			@RequestParam(value = "name", defaultValue="A TicTacToe Game") String name) {

		GameStateModel game = new GameStateModel(player, name);
		repository.save(game);

		return game;
	}

	@RequestMapping(method = RequestMethod.PATCH, value = "/ttt/game")
	public GameStateModel updateGame(
			@RequestParam(value = "id") String id,
			@RequestParam(value = "player") String player,
			@RequestParam(value = "disconnect", required = false) boolean disconnect,
			@RequestParam(value = "rematch", required = false) boolean rematch) {

		GameStateModel game = repository.findById(id);

		// handle disconnect
		if (disconnect) {
			game.disconnect(player);
		}
		else if (rematch) {
			game.rematch(player);
		}
		else if (!game.started && !game.disconnect) {
			game.join(player);
		}
		else {
			// return null if third player is trying to join
			return null;
		}

		repository.save(game);
		updateGameState(id, game);

		return game;
	}

	// push new game state to websocket
	private void updateGameState(String id, GameStateModel game) {
		template.convertAndSend("/ttt/gamestate/" + id, game);
	}

}
