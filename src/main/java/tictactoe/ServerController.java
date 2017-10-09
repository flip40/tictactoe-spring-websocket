package tictactoe;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {

	@Autowired
	private GameStateRepository repository;
	
	@Autowired
	private SimpMessagingTemplate template;

	@RequestMapping("/ttt/games")
	public List<AvailableGame> getServers() {
		List<AvailableGame> available = new ArrayList<AvailableGame>();
		List<GameStateModel> games = repository.findByStartedAndDisconnect(false, false);
		
		for(GameStateModel game : games) {
			available.add(new AvailableGame(game.id, game.name));
		}
		
		return available;
	}

	@RequestMapping("/ttt/create")
	public GameStateModel createGame(
			@RequestParam(value="player") String player,
			@RequestParam(value="name", defaultValue="A TicTacToe Game") String name) {
		
		GameStateModel game = new GameStateModel(player, name);
		repository.save(game);
		
		return game;
	}

	@RequestMapping("/ttt/join")
	public GameStateModel joinGame(
			@RequestParam(value="player") String player,
			@RequestParam(value="id") String id) {
		
		GameStateModel game = repository.findById(id);
		// make sure they aren't joining as a "third" player
		if (game.started) return null;
		
		game.join(player);
		repository.save(game);
		
		updateGameState(id, game);
		
		return game;
	}

	@RequestMapping("/ttt/disconnect")
	public GameStateModel joinGame(@RequestParam(value="id") String id) {
		GameStateModel game = repository.findById(id);
		
		game.disconnect = true;
		repository.save(game);
		
		updateGameState(id, game);
		
		return game;
	}
	
	private void updateGameState(String id, GameStateModel game) {
		this.template.convertAndSend("/ttt/gamestate/" + id, game);
	}

}
