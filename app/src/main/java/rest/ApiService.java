package rest;

import model.WeatherModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    /*
    Retrofit get annotation with our URL
    And our method that will return us the current weather
    */

    @GET("weather?")
    Call<WeatherModel> getWeatherUpdate(@Query("lat") int lat, @Query("lon") int lon,
                                        @Query("appid") String apikey);
}
