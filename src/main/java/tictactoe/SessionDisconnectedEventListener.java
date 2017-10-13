package tictactoe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectedEventListener implements ApplicationListener<SessionDisconnectEvent> {

	@Autowired
	private GameStateRepository repository;

	@Autowired
	private SimpMessagingTemplate template;

	private static final Logger logger = LoggerFactory.getLogger(SessionDisconnectedEventListener.class);

	@Override
	public void onApplicationEvent(SessionDisconnectEvent sessionDisconnectEvent) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());

		String playerId = headerAccessor.getSessionId();

		GameStateModel game = repository.findTopByPlayer1IdOrPlayer2IdOrderByCreatedDesc(playerId, playerId);

		game.disconnectById(playerId);

		repository.save(game);
		template.convertAndSend("/ttt/gamestate/" + game.id, game);
	}

}
