package space.ankan.nyuyu.network;


import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import space.ankan.nyuyu.Utils.AppConstants;

/**
 * Created by Ankan.
 * Retrofit Rest Client Api Service object to make network calls
 */

class RestClient implements AppConstants {

    private static ApiService apiService;

    static ApiService getApiService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
