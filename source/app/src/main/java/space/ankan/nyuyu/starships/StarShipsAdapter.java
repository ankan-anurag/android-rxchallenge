package space.ankan.nyuyu.starships;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import space.ankan.nyuyu.R;
import space.ankan.nyuyu.model.StarShip;
import space.ankan.nyuyu.network.StarshipApiService;

import static space.ankan.nyuyu.Utils.AppConstants.LOGCAT;

/**
 * Created by Ankan.
 * Adapter class to load Starships
 */

class StarShipsAdapter extends RecyclerView.Adapter<StarShipsAdapter.ViewHolder> {

    private Context mContext;
    private List<StarShip> mStarships;
    private Map<Long, String> filmMap;


    StarShipsAdapter(Context mContext) {
        this.mContext = mContext;
        this.mStarships = new ArrayList<>();
        filmMap = new ArrayMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_starship, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StarShip item = mStarships.get(position);
        holder.name.setText(item.name);
        holder.cost.setText(mContext.getString(R.string.cost, item.cost_in_credits));
        holder.cost.setEllipsize(TextUtils.TruncateAt.END);
        String filmText = item.getBoundedFilmText();

        if (TextUtils.isEmpty(filmText)) {
            filmText = "Found " + item.films.size() + " films";
            fetchFilms(item.films, position);
        }

        holder.films.setText(filmText);

    }

    @Override
    public int getItemCount() {
        return mStarships.size();
    }

    void addAll(List<StarShip> starShips) {
        mStarships.addAll(starShips);
        notifyDataSetChanged();
    }

    void trimTo(final int count) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Collections.sort(mStarships);
                List<StarShip> filteredStarShips = new ArrayList<>(count);
                for (int i = 0; i < count; i++)
                    filteredStarShips.add(mStarships.get(i));
                mStarships = filteredStarShips;
                notifyDataSetChanged();
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final View view;
        final TextView name;
        final TextView cost;
        final TextView films;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.name);
            cost = (TextView) view.findViewById(R.id.cost);
            films = (TextView) view.findViewById(R.id.films);
        }
    }

    private void fetchFilms(final List<String> films, final int position) {

        final StringBuilder builder = new StringBuilder();
        builder.append(mContext.getString(R.string.films)).append(" ");

        StarshipApiService.getFilmObservable(filmMap, films).subscribe(new Subscriber<String>() {

            @Override
            public void onCompleted() {
                builder.append(".");
                mStarships.get(position).setBoundedFilmText(builder.toString());
                notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOGCAT, "Error on adapter subscriber: " + e.toString());
            }

            @Override
            public void onNext(String film) {
                if (!TextUtils.isEmpty(film)) {
                    if (builder.length() > 7)
                        builder.append(", ");
                    builder.append(film);
                }

            }
        });

    }

}