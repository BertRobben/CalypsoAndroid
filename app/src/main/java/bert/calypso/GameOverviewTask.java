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

public class GameOverviewTask extends AsyncTask<Void, String, TaskResult<List<LocatedGame>>> {

    private static final String TAG = "Gameoverview";

    private MainActivity currentActivity;
    private boolean done;

    @Override
    protected TaskResult<List<LocatedGame>> doInBackground(Void... params) {
        GamesExtractor ge = new GamesExtractor(new ProgressPublisher() {
            @Override
            public void publish(String message) {
                publishProgress(message);
            }
        });
        ReservationsExtractor reservationsExtractor = new ReservationsExtractor(new ProgressPublisher() {
            @Override
            public void publish(String message) {
                publishProgress(message);
            }
        });

        try {
            ReservationExtractor reservationExtractor = reservationsExtractor.createReservationExtractor();
            List<Game> games = ge.crawl();
            List<LocatedGame> locatedGames = locate(games, reservationExtractor);
            return new TaskResult<>(locatedGames);
        } catch (Exception e) {
            Log.e(TAG, "Failed to extract games", e);
            return new TaskResult(e);
        }
    }

    private List<LocatedGame> locate(List<Game> games, ReservationExtractor reservationExtractor) {
        List<LocatedGame> result = new ArrayList<>();
        for (Game game : games) {
            result.add(new LocatedGame(game, findLocation(game, reservationExtractor)));
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
            publishProgress("Failed to extract reservation (" + e.getMessage() + ")");
        }
        return "";
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        if (currentActivity != null) {
            TextView tv = currentActivity.findViewById(R.id.status);
            tv.setText(progress[0]);
        }
    }

    @Override
    protected void onPostExecute(TaskResult<List<LocatedGame>> r) {
        done = true;
        onProgressUpdate("");
        if (currentActivity != null) {
            if (r.getResult() != null) {
                currentActivity.showGames(r.getResult());
            } else {
                TextView tv = currentActivity.findViewById(R.id.status);
                tv.setText("Failed to extract results: " + r.getException().getMessage());
            }
        }
    }

    public boolean isDone() {
        return done;
    }

    void attach(MainActivity a) {
        currentActivity = a;
    }
    void detach() {
        currentActivity = null;
    }
}
