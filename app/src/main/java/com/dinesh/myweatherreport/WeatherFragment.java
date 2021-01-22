package com.dinesh.myweatherreport;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.dinesh.myweatherreport.apimanager.RemoteFetch;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class WeatherFragment extends Fragment {
    Typeface weatherFont;
     
    private TextView cityField;
	private TextView weatherIcon;
    private TextView updatedField;
	private TextView currentTemperatureField;
	private TextView detailsField;

	private TextView tv_temperature;
	private TextView tv_min_temp;
	private TextView tv_max_temp;
	private TextView tv_day;

     
    Handler handler;
 
    public WeatherFragment(){   
        handler = new Handler();
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
		cityField = (TextView)rootView.findViewById(R.id.city_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
		detailsField = (TextView)rootView.findViewById(R.id.details_field);
		weatherIcon.setTypeface(weatherFont);

		tv_temperature = (TextView)rootView.findViewById(R.id.tv_temperature);
		tv_min_temp = (TextView)rootView.findViewById(R.id.tv_min_temp);
		tv_max_temp = (TextView)rootView.findViewById(R.id.tv_max_temp);
		tv_day = (TextView)rootView.findViewById(R.id.tv_day);

        return rootView; 
    }


	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);  

	    weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
	    updateWeatherData(new CityPreference(getActivity()).getCity());
	}
     
	
	private void updateWeatherData(final String city){
	    new Thread(){
	        public void run(){
	            final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
	            if(json == null){
	                handler.post(new Runnable(){
	                    public void run(){
	                        Toast.makeText(getActivity(),
	                                getActivity().getString(R.string.place_not_found), 
	                                Toast.LENGTH_LONG).show();
	                    }
	                });
	            } else {
	                handler.post(new Runnable(){
	                    public void run(){
							Toast.makeText(getActivity(),
									"Data loaded successfully..!",
									Toast.LENGTH_LONG).show();
	                        renderWeather(json);
	                    }
	                });
	            }               
	        }
	    }.start();
	}
	
	private void renderWeather(JSONObject json){
    	Log.v("WeatherData => ", ""+json);
	    try {
	        cityField.setText(json.getString("name").toUpperCase(Locale.US) +
	                ", " + 
	                json.getJSONObject("sys").getString("country"));
	         
	        JSONObject details = json.getJSONArray("weather").getJSONObject(0);
	        JSONObject main = json.getJSONObject("main");
	        detailsField.setText(
	                details.getString("description").toUpperCase(Locale.US) +
	                "\n" + "Humidity: " + main.getString("humidity") + "%" +
	                "\n" + "Pressure: " + main.getString("pressure") + " hPa");
	         
	        currentTemperatureField.setText(
	                    String.format("%.2f", main.getDouble("temp"))+ " ℃");
	 
	        DateFormat df = DateFormat.getDateTimeInstance();
	        String updatedOn = df.format(new Date(json.getLong("dt")*1000));
	        updatedField.setText("Last update: " + updatedOn);
	 
	        setWeatherIcon(details.getInt("id"),
	                json.getJSONObject("sys").getLong("sunrise") * 1000,
	                json.getJSONObject("sys").getLong("sunset") * 1000);

			tv_temperature.setText(String.format("%.2f", main.getDouble("temp"))+ " ℃");
			tv_min_temp.setText(String.format("%.2f", main.getDouble("temp_min"))+ " ℃");
			tv_max_temp.setText(String.format("%.2f", main.getDouble("temp_max"))+ " ℃");
			tv_day.setText(updatedOn);

	    }catch(Exception e){
	        Log.e("SimpleWeather", "Field not present in JSON Received");
	    }
	}
	
	private void setWeatherIcon(int actualId, long sunrise, long sunset){
	    int id = actualId / 100;
	    String icon = "";
	    if(actualId == 800){
	        long currentTime = new Date().getTime();
	        if(currentTime>=sunrise && currentTime<sunset) {
	            icon = getActivity().getString(R.string.weather_sunny);
	        } else {
	            icon = getActivity().getString(R.string.weather_clear_night);
	        }
	    } else {
	        switch(id) {
	        case 2 : icon = getActivity().getString(R.string.weather_thunder);
	                 break;         
	        case 3 : icon = getActivity().getString(R.string.weather_drizzle);
	                 break;     
	        case 7 : icon = getActivity().getString(R.string.weather_foggy);
	                 break;
	        case 8 : icon = getActivity().getString(R.string.weather_cloudy);
	                 break;
	        case 6 : icon = getActivity().getString(R.string.weather_snowy);
	                 break;
	        case 5 : icon = getActivity().getString(R.string.weather_rainy);
	                 break;
	        }
	    }
	    weatherIcon.setText(icon);
	}
	
	public void changeCity(String city){
	    updateWeatherData(city);
	}
	
}