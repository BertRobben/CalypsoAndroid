package bert.calypso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import bert.calypso.crawler.Reservations;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    public static final String RESERVATIONS = "bert.calypso.reservations";

    private LocationExtractorTask locationExtractorTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationExtractorTask = (LocationExtractorTask) getLastNonConfigurationInstance();
        if (locationExtractorTask != null) {
            locationExtractorTask.attach(this);
        }
    }

    public void go(View view) {
//        if (locationExtractorTask != null && locationExtractorTask.isDone()) {
//            locationExtractorTask.detach();
//            locationExtractorTask = null;
//        }
        Log.i(TAG, "go");
        if (locationExtractorTask == null) {
            locationExtractorTask = new LocationExtractorTask();
            locationExtractorTask.attach(this);
            locationExtractorTask.execute();
        }
    }

    public void showReservations(Reservations reservations) {
        Log.i(TAG, "showReservations");
        Intent intent = new Intent(this, ShowReservationsActivity.class);
        intent.putExtra(RESERVATIONS, reservations);
        startActivity(intent);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        Log.i(TAG, "RetainNonConfigurationInstance");
        if (locationExtractorTask != null) {
            locationExtractorTask.detach();
        }
        return locationExtractorTask;
    }

}
