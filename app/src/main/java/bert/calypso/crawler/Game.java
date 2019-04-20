package bert.calypso.crawler;

import java.util.Date;

import bert.calypso.SimpleTime;

public class Game {

    private final Date date;
    private final String opponent;
    private final SimpleTime start;
    private final boolean homeGame;

    public Game(Date date, String opponent, SimpleTime start, boolean homeGame) {
        this.date = date;
        this.opponent = opponent;
        this.start = start;
        this.homeGame = homeGame;
    }

    public Date getDate() {
        return date;
    }

    public String getOpponent() {
        return opponent;
    }

    public boolean isHomeGame() {
        return homeGame;
    }

    public SimpleTime getStart() {
        return start;
    }

    @Override
    public String toString() {
        return date + " " + start + " " + getSetup();
    }

    public String getSetup() {
        return homeGame ? "CALYPSO - " + opponent : opponent + " - CALYPSO";
    }

}
