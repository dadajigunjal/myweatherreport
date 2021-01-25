package com.dadaji.myweatherreport;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.dadaji.myweatherreport.apimanager.RemoteFetch;
import com.dadaji.myweatherreport.databinding.FragmentWeatherBinding;
import com.dadaji.myweatherreport.model.WeatherModel;
import com.dadaji.myweatherreport.viewmodels.WeatherViewModel;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class WeatherFragment extends Fragment {
    Typeface weatherFont;
     
    private TextView cityField;

	private TextView tv_temperature;
	private TextView tv_min_temp;
	private TextView tv_max_temp;
	private TextView tv_day;
	private EditText edtDate;

     
    Handler handler;
	DatePickerDialog datepicker;
	ProgressDialog pDialog;
 
    public WeatherFragment(){   
        handler = new Handler();
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		//Update viewmodel
		FragmentWeatherBinding fragmentWeatherBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false);
		View rootView = fragmentWeatherBinding.getRoot();
		fragmentWeatherBinding.setViewModel(new WeatherViewModel());

//        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
		cityField = (TextView)rootView.findViewById(R.id.city_field);
		edtDate = (EditText)rootView.findViewById(R.id.edtDate);

		tv_temperature = (TextView)rootView.findViewById(R.id.tv_temperature);
		tv_min_temp = (TextView)rootView.findViewById(R.id.tv_min_temp);
		tv_max_temp = (TextView)rootView.findViewById(R.id.tv_max_temp);
		tv_day = (TextView)rootView.findViewById(R.id.tv_day);

		edtDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Calendar cldr = Calendar.getInstance();
				int day = cldr.get(Calendar.DAY_OF_MONTH);
				int month = cldr.get(Calendar.MONTH);
				int year = cldr.get(Calendar.YEAR);
				// date picker dialog
				datepicker = new DatePickerDialog(getActivity(),
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								edtDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
								if(checkPrime(dayOfMonth)){
									updateWeatherData(new CityPreference(getActivity()).getCity());
									datepicker.dismiss();
								}else {
									Toast.makeText(getActivity(), "Selected Date is not prime number..!", Toast.LENGTH_SHORT).show();
									datepicker.dismiss();
								}
							}
						}, year, month, day);
				datepicker.show();
			}
		});

        return rootView;
    }

	public boolean checkPrime(int number) {
		for (int i = 2; i <= number / 2; i++) {
			if (number % i == 0) {
				return false;
			}
		}
		return true;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);  

	    weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
	    //init progress dialog
		pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("Loading, please wait...");
		pDialog.setTitle(getActivity().getResources().getString(R.string.app_name));
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//	    updateWeatherData(new CityPreference(getActivity()).getCity());
	}
     
	
	private void updateWeatherData(final String city){
    	if (pDialog != null && !pDialog.isShowing()){
    		pDialog.show();
		}
	    new Thread(){
	        public void run(){
	            final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
	            if(json == null){
	                handler.post(new Runnable(){
	                    public void run(){
	                        Toast.makeText(getActivity(),
	                                getActivity().getString(R.string.place_not_found), 
	                                Toast.LENGTH_LONG).show();
							if (pDialog != null && pDialog.isShowing()){
								pDialog.dismiss();
							}
	                    }
	                });
	            } else {
	                handler.post(new Runnable(){
	                    public void run(){
//							Toast.makeText(getActivity(),"Data loaded successfully..!",Toast.LENGTH_LONG).show();
							if (pDialog != null && pDialog.isShowing()){
								pDialog.dismiss();
							}
	                        renderWeather(json);
	                    }
	                });
	            }               
	        }
	    }.start();
	}
	
	private void renderWeather(JSONObject json){
		WeatherModel weatherModel = new Gson().fromJson(json.toString(), WeatherModel.class);
		Log.v("WeatherData => ", ""+weatherModel.toString());
    	try {
			cityField.setText(weatherModel.getName().toUpperCase() +
					", " +
					weatherModel.getSys().getCountry());

			tv_temperature.setText(String.format("%.2f", weatherModel.getMain().getTemp())+ " ℃");
			tv_min_temp.setText(String.format("%.2f", weatherModel.getMain().getTemp_min())+ " ℃");
			tv_max_temp.setText(String.format("%.2f", weatherModel.getMain().getTemp_max())+ " ℃");

			SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
			df.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
			String updatedOn = df.format(new Date(weatherModel.getDt() *1000L));
			tv_day.setText(updatedOn);


	    }catch(Exception e){
	        Log.e("SimpleWeather", "Field not present in JSON Received");
	    }
	}
	
	public void changeCity(String city){
	    updateWeatherData(city);
	}
	
}