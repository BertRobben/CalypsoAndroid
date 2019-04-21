package bert.calypso;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bert.calypso.crawler.LocatedGame;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private GameOverviewTask gameOverviewTask;
    private GamesAdapter gamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView gamesView = findViewById(R.id.gamesView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        gamesView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        gamesView.setLayoutManager(layoutManager);
        gamesAdapter = new GamesAdapter();
        gamesView.setAdapter(gamesAdapter);

        gameOverviewTask = (GameOverviewTask) getLastCustomNonConfigurationInstance();
        if (gameOverviewTask != null) {
            gameOverviewTask.attach(this);
        }
        go();
    }

    private void go() {
        Log.i(TAG, "go");
        if (gameOverviewTask != null && gameOverviewTask.isDone()) {
            gameOverviewTask.detach();
            gameOverviewTask = null;
        }
        if (gameOverviewTask == null) {
            gameOverviewTask = new GameOverviewTask();
            gameOverviewTask.attach(this);
            gameOverviewTask.execute();
        }
    }

    public void showGame(LocatedGame game) {
        gamesAdapter.add(game);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Log.i(TAG, "onRetainCustomNonConfigurationInstance");
        if (gameOverviewTask != null) {
            gameOverviewTask.detach();
        }
        return gameOverviewTask;
    }

}
