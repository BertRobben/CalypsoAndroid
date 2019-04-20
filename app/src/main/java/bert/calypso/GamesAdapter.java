package bert.calypso;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import bert.calypso.crawler.LocatedGame;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.MyViewHolder> {

    public static final int EVEN_BACKGROUND_COLOR = Color.parseColor("#FFFFFF");
    public static final int ODD_BACKGROUND_COLOR = Color.parseColor("#d2e0f7");
    private final List<LocatedGame> games;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("E dd/MM/yy");


    public GamesAdapter(List<LocatedGame> games) {
        this.games = games;
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
        LocatedGame game = games.get(position);
        holder.hall.setText(game.getLocation());
        holder.date.setText(game.getGame().getDate() == null ? "" : dateFormat.format(game.getGame().getDate()));
        holder.game.setText(game.getGame().getSetup());
        holder.time.setText(game.getGame().getStart() != null ? game.getGame().getStart().toString() : "");
        holder.layout.setBackgroundColor(position%2 == 0 ? EVEN_BACKGROUND_COLOR : ODD_BACKGROUND_COLOR);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView date;
        public TextView game;
        public TextView time;
        public TextView hall;
        public LinearLayout layout;
        public MyViewHolder(LinearLayout v) {
            super(v);
            layout = v;
            date = v.findViewById(R.id.date);
            game = v.findViewById(R.id.game);
            time = v.findViewById(R.id.time);
            hall = v.findViewById(R.id.hall);
        }
    }
}
