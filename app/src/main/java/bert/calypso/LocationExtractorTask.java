package bert.calypso;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;

import bert.calypso.crawler.LocationExtractor;
import bert.calypso.crawler.ProgressPublisher;
import bert.calypso.crawler.Reservations;

public class LocationExtractorTask extends AsyncTask<Void, String, TaskResult<Reservations>> {

    private MainActivity currentActivity;

    @Override
    protected TaskResult<Reservations> doInBackground(Void... params) {
        LocationExtractor extractor = new LocationExtractor(new ProgressPublisher() {
            @Override
            public void publish(String message) {
                publishProgress(message);
            }
        });
        try {
            return new TaskResult<>(extractor.crawl());
        } catch (IOException e) {
            return new TaskResult(e);
        }
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        if (currentActivity != null) {
            TextView tv = currentActivity.findViewById(R.id.status);
            tv.setText(progress[0]);
        }
    }

    @Override
    protected void onPostExecute(TaskResult<Reservations> r) {
        if (currentActivity != null) {
            if (r.getResult() != null) {
                currentActivity.showReservations(r.getResult());
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
