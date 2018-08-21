package rest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import model.WeatherModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    /********
     * URLS
     *******/
    public static final String ROOT_URL = "http://api.openweathermap.org/data/2.5/";
    private static Retrofit retrofit = null;

    /**
     * Get Retrofit Instance
     */
    private static Retrofit getRetrofitInstance() {
        if(retrofit == null){
            retrofit =  new Retrofit.Builder()
                    .baseUrl(ROOT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static ApiService getApiService(){
        return getRetrofitInstance().create(ApiService.class);
    }

    public LiveData<WeatherModel> getWeatherUpdate(int lat, int lon, String apiKey){
        final WeatherModel weatherModel = new WeatherModel();
        final MutableLiveData<WeatherModel> weatherModelMutableLiveData = new MutableLiveData<>();
        getRetrofitInstance().create(ApiService.class).getWeatherUpdate(lat, lon, apiKey)
                .enqueue(new Callback<WeatherModel>() {
                    @Override
                    public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                        WeatherModel model = response.body();
                        weatherModelMutableLiveData.setValue(model);
                    }

                    @Override
                    public void onFailure(Call<WeatherModel> call, Throwable t) {
                        Log.e("", "Error Getting Weather update Data Retrofit");
                    }
                });
        return weatherModelMutableLiveData;
    }
}
