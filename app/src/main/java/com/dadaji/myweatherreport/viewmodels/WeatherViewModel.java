package com.dadaji.myweatherreport.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.dadaji.myweatherreport.BR;
import com.dadaji.myweatherreport.model.WeatherModel;

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

    @Bindable
    public String getTemp_max(){
        try {
            return String.valueOf(weatherModel.getMain().getTemp_max());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public void setTemp_max(Double temp_max) {
        weatherModel.main.setTemp_max(temp_max);
        notifyPropertyChanged(BR.temp_max);
    }

    @Bindable
    public String getTemp(){
        try {
            return String.valueOf(weatherModel.getMain().getTemp());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public void setTemp(Double temp) {
        weatherModel.main.setTemp(temp);
        notifyPropertyChanged(BR.temp);
    }
}
