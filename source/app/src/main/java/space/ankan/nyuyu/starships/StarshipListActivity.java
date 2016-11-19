package space.ankan.nyuyu.starships;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import rx.Subscriber;
import space.ankan.nyuyu.R;
import space.ankan.nyuyu.Utils.AppConstants;
import space.ankan.nyuyu.model.StarShipList;
import space.ankan.nyuyu.network.StarshipApiService;

public class StarshipListActivity extends AppCompatActivity implements AppConstants {

    private StarShipsAdapter mAdapter;
    private TextView progressStatus;
    private View progressPanel;

    private Subscriber<StarShipList> subscriber = new Subscriber<StarShipList>() {
        @Override
        public void onCompleted() {
            progressStatus.setText(getString(R.string.progress_filter));
            mAdapter.trimTo(MAX_STARSHIP_COUNT);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressPanel.setVisibility(View.GONE);
                }
            }, DELAY_PROGRESS_HIDE);

        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOGCAT, "Error on subscriber: " + e.toString());
            progressPanel.setVisibility(View.GONE);
        }

        @Override
        public void onNext(StarShipList starShipList) {
            mAdapter.addAll(starShipList.results);
            progressStatus.setText(getString(R.string.progress_fetching, mAdapter.getItemCount()));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starship_list);
        progressPanel = findViewById(R.id.progress);
        progressStatus = (TextView) findViewById(R.id.progress_status);
        setupRecycler();
        StarshipApiService.getStarShipListObservable(STARSHIPS_PAGE_COUNT).subscribe(subscriber);
    }

    private void setupRecycler() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.starship_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StarShipsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        progressStatus.setText(getString(R.string.progress_init));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!subscriber.isUnsubscribed()) subscriber.unsubscribe();
    }
}
