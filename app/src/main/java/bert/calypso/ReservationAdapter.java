package bert.calypso;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bert.calypso.crawler.Reservation;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.MyViewHolder> {

    private final List<Reservation> reservations;

    public ReservationAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_layout, parent, false);
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.hall.setText(reservation.getHall());
        holder.period.setText(reservation.getDate() + ": " + reservation.getStartTime() + " - " + reservation.getEndTime());
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView hall;
        public TextView period;
        public MyViewHolder(LinearLayout v) {
            super(v);
            hall = v.findViewById(R.id.date);
            period = v.findViewById(R.id.hall);
        }
    }
}
