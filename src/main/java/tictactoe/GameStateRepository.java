package tictactoe;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameStateRepository extends MongoRepository<GameStateModel, String> {

	public GameStateModel findById(String id);
	public List<GameStateModel> findByStartedAndDisconnect(boolean started, boolean disconnect);
	public GameStateModel findTopByPlayer1IdOrPlayer2IdOrderByCreatedDesc(String player1Id, String player2Id);

}
