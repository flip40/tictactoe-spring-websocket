package tictactoe;

public class AvailableGame {

	private String id;
	private String name;

	public AvailableGame() {
	}

	public AvailableGame(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
