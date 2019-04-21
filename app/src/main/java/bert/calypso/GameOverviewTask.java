package bert.calypso;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bert.calypso.crawler.Game;
import bert.calypso.crawler.GamesExtractor;
import bert.calypso.crawler.LocatedGame;
import bert.calypso.crawler.ProgressPublisher;
import bert.calypso.crawler.Reservation;
import bert.calypso.crawler.ReservationsExtractor;
import bert.calypso.crawler.ReservationsExtractor.ReservationExtractor;
import bert.calypso.util.Either;

public class GameOverviewTask extends AsyncTask<Void, Either<LocatedGame, String>, TaskResult<Void>> {

    private static final String TAG = "GameOverview";

    private MainActivity currentActivity;
    private boolean done;
    private List<LocatedGame> games = new ArrayList<>();

    @Override
    protected TaskResult<Void> doInBackground(Void... params) {
        GamesExtractor ge = new GamesExtractor(new ProgressPublisher() {
            @Override
            public void publish(String message) {
                publishProgressMessage(message);
            }
        });
        ReservationsExtractor reservationsExtractor = new ReservationsExtractor(new ProgressPublisher() {
            @Override
            public void publish(String message) {
                publishProgressMessage(message);
            }
        });

        try {
            ReservationExtractor reservationExtractor = reservationsExtractor.createReservationExtractor();
            List<Game> games = ge.crawl();
            locate(games, reservationExtractor);
            return new TaskResult<>(null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to extract games", e);
            return new TaskResult(e);
        }
    }

    private List<LocatedGame> locate(List<Game> games, ReservationExtractor reservationExtractor) {
        List<LocatedGame> result = new ArrayList<>();
        for (Game game : games) {
            publishProgressGame(new LocatedGame(game, findLocation(game, reservationExtractor)));
        }
        return result;
    }

    private String findLocation(Game game, ReservationExtractor reservationExtractor) {
        if (!game.isHomeGame() || game.getDate() == null) {
            return "";
        }
        try {
            List<Reservation> reservations = reservationExtractor.getReservations(game.getDate());
            if (!reservations.isEmpty()) {
                return reservations.get(0).getHall();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to extract date", e);
            publishProgressMessage("Failed to extract reservation (" + e.getMessage() + ")");
        }
        return "";
    }

    @Override
    protected void onProgressUpdate(Either<LocatedGame, String> ... progress) {
        if (currentActivity != null) {
            for (Either<LocatedGame, String> p : progress) {
                if (p.getLeft() != null) {
                    currentActivity.showGame(p.getLeft());
                } else {
                    TextView tv = currentActivity.findViewById(R.id.status);
                    tv.setText(p.getRight());
                }
            }
        }
    }

    @Override
    protected void onPostExecute(TaskResult<Void> r) {
        done = true;
        onProgressUpdate(Either.<LocatedGame, String> right(""));
        if (currentActivity != null) {
            if (r.getException() != null) {
                TextView tv = currentActivity.findViewById(R.id.status);
                tv.setText("Failed to extract results: " + r.getException().getMessage());
            }
        }
    }

    private void publishProgressMessage(String message) {
        publishProgress(Either.<LocatedGame, String> right(message));
    }

    private void publishProgressGame(LocatedGame game) {
        publishProgress(Either.<LocatedGame, String> left(game));
    }

    public boolean isDone() {
        return done;
    }

    void attach(MainActivity a) {
        currentActivity = a;
        for (LocatedGame game : games) {
            currentActivity.showGame(game);
        }
    }

    void detach() {
        currentActivity = null;
    }
}
