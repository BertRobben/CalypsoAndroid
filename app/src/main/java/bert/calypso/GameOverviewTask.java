package bert.calypso;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bert.calypso.crawler.Game;
import bert.calypso.crawler.GamesExtractor;
import bert.calypso.crawler.LocatedGame;
import bert.calypso.crawler.ProgressPublisher;

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
        try {
            List<Game> games = ge.crawl();
            List<LocatedGame> locatedGames = locate(games);
            return new TaskResult<>(locatedGames);
        } catch (Exception e) {
            Log.e(TAG, "Failed to extract games", e);
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
        done = true;
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
