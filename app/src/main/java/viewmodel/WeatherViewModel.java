package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import model.WeatherModel;
import rest.RetroClient;

public class WeatherViewModel extends ViewModel {

    private LiveData<WeatherModel> modelLiveDataModel;
    private RetroClient client = new RetroClient();

    public LiveData<WeatherModel> getModel(int lat, int lon, String apiKey) {
        if(modelLiveDataModel == null){
            modelLiveDataModel = client.getWeatherUpdate(lat, lon, apiKey);
        }
        return modelLiveDataModel;
    }
}
