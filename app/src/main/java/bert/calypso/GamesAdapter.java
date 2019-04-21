package bert.calypso;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bert.calypso.crawler.LocatedGame;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.MyViewHolder> {

    private static final int EVEN_BACKGROUND_COLOR = Color.parseColor("#FFFFFF");
    private static final int ODD_BACKGROUND_COLOR = Color.parseColor("#d2e0f7");

    private final List<LocatedGame> games;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("E dd/MM/yy");


    public GamesAdapter() {
        this.games = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_layout, parent, false);
        return new MyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        LocatedGame game = games.get(position);
        if (game.getGame().getDate() == null || game.getGame().getDate().getTime() > System.currentTimeMillis()) {
            holder.hall.setTypeface(null, Typeface.BOLD);
            holder.date.setTypeface(null, Typeface.BOLD);
            holder.game.setTypeface(null, Typeface.BOLD);
            holder.time.setTypeface(null, Typeface.BOLD);
        } else {
            holder.hall.setTypeface(null, Typeface.NORMAL);
            holder.date.setTypeface(null, Typeface.NORMAL);
            holder.game.setTypeface(null, Typeface.NORMAL);
            holder.time.setTypeface(null, Typeface.NORMAL);
        }
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
        public View layout;
        public MyViewHolder(View v) {
            super(v);
            layout = v;
            date = v.findViewById(R.id.date);
            game = v.findViewById(R.id.game);
            time = v.findViewById(R.id.time);
            hall = v.findViewById(R.id.hall);
        }
    }

    public void add(LocatedGame game) {
        int index = findIndex(game);
        games.add(index, game);
        notifyItemInserted(index);
        for (int i = index + 1; i < games.size(); i++) {
            // the background color of these items changed
            notifyItemChanged(i);
        }
    }

    private int findIndex(LocatedGame game) {
        int result = 0;
        for (LocatedGame g : games) {
            if (isAfter(g.getGame().getDate(), game.getGame().getDate())) {
                break;
            }
            result++;
        }
        return result;
    }

    private boolean isAfter(Date first, Date second) {
        if (first == null) {
            return true;
        }
        if (second == null) {
            return false;
        }
        return first.getTime() > second.getTime();
    }
}
