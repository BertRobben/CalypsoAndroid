package bert.calypso;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bert.calypso.crawler.Game;
import bert.calypso.crawler.GamesExtractor;
import bert.calypso.crawler.LocatedGame;
import bert.calypso.crawler.LocationExtractor;
import bert.calypso.crawler.ProgressPublisher;
import bert.calypso.crawler.Reservations;

public class GameOverviewTask extends AsyncTask<Void, String, TaskResult<List<LocatedGame>>> {

    private MainActivity currentActivity;

    @Override
    protected TaskResult<List<LocatedGame>> doInBackground(Void... params) {
        GamesExtractor ge = new GamesExtractor(new ProgressPublisher() {
            @Override
            public void publish(String message) {
                publishProgress(message);
            }
        });
        try {
            List<Game> games = ge.crawl();
            List<LocatedGame> locatedGames = locate(games);
            return new TaskResult<>(locatedGames);
        } catch (IOException e) {
            return new TaskResult(e);
        }
    }

    private List<LocatedGame> locate(List<Game> games) {
        List<LocatedGame> result = new ArrayList<>();
        for (Game game : games) {
            result.add(new LocatedGame(game));
        }
        return result;
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
        if (currentActivity != null) {
            if (r.getResult() != null) {
                currentActivity.showGames(r.getResult());
            } else {
                TextView tv = currentActivity.findViewById(R.id.status);
                tv.setText("Failed to extract results: " + r.getException().getMessage());
            }
        }
    }

    void attach(MainActivity a) {
        currentActivity = a;
    }
    void detach() {
        currentActivity = null;
    }
}
