package tictactoe;

public class PlayerMoveMessage {

	private String player;
	private int x;
	private int y;

	public PlayerMoveMessage() {
	}

	public PlayerMoveMessage(String player, int x, int y) {
		this.player = player;
		this.x = x;
		this.y = y;
	}

	public String getPlayer() {
		return player;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
