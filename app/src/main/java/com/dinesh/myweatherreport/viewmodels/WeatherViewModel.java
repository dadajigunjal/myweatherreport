package com.dinesh.myweatherreport.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.dinesh.myweatherreport.BR;
import com.dinesh.myweatherreport.model.WeatherModel;

public class WeatherViewModel extends BaseObservable {
    private WeatherModel weatherModel;

    public WeatherViewModel() {
        this.weatherModel = new WeatherModel();
    }

    @Bindable
    public String getTemp_min(){
        try {
            return String.valueOf(weatherModel.getMain().getTemp_min());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public void setTemp_min(Double temp_min) {
        weatherModel.main.setTemp_min(temp_min);
        notifyPropertyChanged(BR.temp_min);
    }
}
