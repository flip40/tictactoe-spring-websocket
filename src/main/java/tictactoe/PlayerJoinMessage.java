package tictactoe;

public class PlayerJoinMessage {

	private String player;

	public PlayerJoinMessage() {
	}

	public PlayerJoinMessage(String player) {
		this.player = player;
	}

	public String getPlayer() {
		return player;
	}
}