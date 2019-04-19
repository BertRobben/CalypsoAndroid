package bert.calypso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import bert.calypso.crawler.Reservations;

public class ShowReservationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reservations);

        RecyclerView recyclerView = findViewById(R.id.listOfHalls);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Reservations reservations = getIntent().getParcelableExtra(MainActivity.RESERVATIONS);
        recyclerView.setAdapter(new ReservationAdapter(reservations.getReservations()));
    }
}
