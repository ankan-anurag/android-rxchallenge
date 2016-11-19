package space.ankan.nyuyu.network;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import space.ankan.nyuyu.model.Film;
import space.ankan.nyuyu.model.StarShipList;

/**
 * Created by Ankan.
 * TODO: Add a class comment
 */

interface ApiService {

    @GET("api/starships")
    Call<StarShipList> listStarShips(@Query("page") int page);

    @GET("api/films/{id}")
    Call<Film> getFilm(@Path("id") long id);
}
