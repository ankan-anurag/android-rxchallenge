package space.ankan.nyuyu.network;

import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import retrofit.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import space.ankan.nyuyu.model.Film;
import space.ankan.nyuyu.model.StarShipList;

import static space.ankan.nyuyu.Utils.AppConstants.LOGCAT;
import static space.ankan.nyuyu.Utils.CommonUtils.extractFilmId;

/**
 * Created by Ankan.
 * Starships Api Service Helper class with Observables for network calls
 */

public class StarshipApiService {

    public static Observable<String> getFilmObservable(final Map<Long, String> filmMap, final List<String> films) {
        return rx.Observable.create(new rx.Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (String filmUrl : films) {
                    String film;
                    Long id = extractFilmId(filmUrl);
                    film = filmMap.get(id);
                    if (film == null) {
                        film = fetchFilm(id);
                        filmMap.put(id, film);
                    }
                    subscriber.onNext(film);
                }

                subscriber.onCompleted();

            }
        })
                .subscribeOn(Schedulers.io()) // subscribeOn the I/O thread
                .observeOn(AndroidSchedulers.mainThread()); // observeOn the UI Thread
    }

    private static String fetchFilm(long id) {
        Log.i(LOGCAT, "fetching film " + id);
        try {
            Response<Film> response = RestClient.getApiService().getFilm(id).execute();
            if (response.code() / 100 == 2) // response successful
                return response.body().title;
        } catch (IOException io) {
            Log.e(LOGCAT, "IOException while fetching film with id " + id + " | message: " + io.getMessage());
        }
        return null;
    }

    public static Observable<StarShipList> getStarShipListObservable(final int PAGE_COUNT) {
        return Observable.create(new Observable.OnSubscribe<StarShipList>() {
            @Override
            public void call(Subscriber<? super StarShipList> subscriber) {
                int currentPage = 0;
                while (currentPage < PAGE_COUNT) {
                    StarShipList list = fetchStarShips(++currentPage);
                    if (list == null) break;
                    subscriber.onNext(list);
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io()) // subscribeOn the I/O thread
                .observeOn(AndroidSchedulers.mainThread()); // observeOn the UI Thread

    }

    private static StarShipList fetchStarShips(int page) {
        // do not enqueue to run on background thread. Code should already be executing on io thread.
        try {
            Response<StarShipList> response = RestClient.getApiService().listStarShips(page).execute();
            if (response.code() / 100 == 2) // response successful
                return response.body();
        } catch (IOException io) {
            Log.e(LOGCAT, "IOException while fetching page " + page + " | message: " + io.getMessage());
        }
        return null;
    }
}
