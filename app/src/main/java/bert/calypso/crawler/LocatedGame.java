package bert.calypso.crawler;

public class LocatedGame {

    private final Game game;
    private final String location;

    public LocatedGame(Game game) {
        this(game, "");
    }

    public LocatedGame(Game game, String location) {
        this.game = game;
        this.location = location;
    }

    public Game getGame() {
        return game;
    }

    public String getLocation() {
        return location;
    }
}
