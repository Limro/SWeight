package iha.smap.startrainer.running;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import iha.smap.startrainer.R;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;

@SuppressLint("SimpleDateFormat")
public class RunningDetailed extends Activity 
{
	private GoogleMap map_;
	private LocationManager locationManager_;
	private Marker startuserMarker_;
	
	@SuppressWarnings("unused")
	private Marker EnduserMarker_;
	
    LocationRequest mLocationRequest_;
   
    Button btnStart_;
    Button btnStop_;
    
    TextView tvAvgSpeed;
    TextView tvDate;
    TextView tvDistance;
    TextView tvMaxSpeed;
    TextView tvTime;
    
    Date startDate;
    
    Intent serviceIntent_;
    Intent intentFromMain_;
    
    int runningIDFromMain_;
    
    //Til stopur
    private Handler myHandler = new Handler();
    private long startTime = 0L;
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;
    
    private RunningDatabase db_ = null;

    //Bliver kaldt fra servicen enten når den er færdige(onDestory) / ved løbende opdateringer (onLocationChanged i service)
    private BroadcastReceiver broadCastRecieverUpdateWholeView = new BroadcastReceiver() 
    {
		@Override
		public void onReceive(Context context, Intent intent) 
		{	
			//Hvis servicen stopper sender jeg et broadcast med ID'et -> skal bruges til at finde punkter til mappet.
			if(intent.hasExtra(getString(R.string.BroadCastFromServiceRunningID)))
			{
				int runningID = intent.getIntExtra(getString(R.string.BroadCastFromServiceRunningID), -1);
				setupMapFromService(runningID);
			}
			else
			{
				//Eller opdateres textviews løbende med info fra servicen. 
				float distance = intent.getFloatExtra(getString(R.string.BroadCastFromLocationChanged_Distance), 0);
				double maxspeed = intent.getDoubleExtra(getString(R.string.BroadCastFromLocationChanged_MaxSpeed),0);
				double avgSpeed = intent.getDoubleExtra(getString(R.string.BroadCastFromLocationChanged_AvgSpeed), 0);
				
				tvDistance.setText(String.format("%.02f Km",distance));
				tvAvgSpeed.setText(String.format("%.02f Km/h",avgSpeed));
				tvMaxSpeed.setText(String.format("%.02f Km/h",maxspeed));
			}

		}
	};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running_detailed);
		
		setupMap();
		zoomInOnLastKnowLocation();
		setupButtonAndTextViews();
		
		intentFromMain_ = this.getIntent();
		
		db_ = new RunningDatabase(this);
		
		//Hvis en af ekstra er blevet sat = betyder det at det er en gemt rute der skal vises
		if(intentFromMain_.hasExtra(getString(R.string.RunningMainToDetail_AvgSpeed)))
		{
			setupMapWithInfoFromMainActivity();
		}   		
	}


	@SuppressLint("SimpleDateFormat")
	public void btnStartClicked(View v)
	{	
		btnStop_.setEnabled(true);
		btnStart_.setEnabled(false);
		
		startTime = SystemClock.uptimeMillis();
		myHandler.postDelayed(updateTime, 0);
		
		setupLocationManager();
		
		serviceIntent_ = new Intent(RunningDetailed.this, RunningMapService.class);
		serviceIntent_.putExtra(getString(R.string.ServiceIntentExtra_DateString), startDate.getTime());
		startService(serviceIntent_);
	}
    
	public void btnStoppedClicked(View v)
	{
		Intent returnIntent = new Intent();
		setResult(RESULT_OK,returnIntent); //Giver besked til main view om det kan opdatere..
		
		btnStop_.setEnabled(false);
		btnStart_.setEnabled(false);
		
		long resultTime = timeSwap + timeInMillies;
		
		int seconds = (int) (resultTime / 1000); 
		int minutes = (int) ((resultTime / 1000)/60) ; 
		int hours = (int) (((resultTime/1000)/60)/60);
		seconds = seconds % 60;
		minutes = minutes % 60;
		hours = hours % 60;
		
		String TimeToSend = hours + ":" + minutes + ":" + String.format("%02d", seconds);
		
		SharedPreferences prefs = this.getSharedPreferences(getString(R.string.TimeWhenClickOnStop_SharedPreference), Context.MODE_PRIVATE);
		
		prefs.edit().putString((getString(R.string.TimeWhenClickOnStop_SharedPreference)), TimeToSend).commit();
		
		myHandler.removeCallbacks(updateTime);
		
		stopService(serviceIntent_);
		serviceIntent_ = null;
	}

	private Runnable updateTime = new Runnable() 
	{
		
		@Override
		public void run() 
		{
			timeInMillies = SystemClock.uptimeMillis() - startTime; 
			finalTime = timeSwap + timeInMillies;

			int seconds = (int) (finalTime / 1000); 
			int minutes = (int) ((finalTime / 1000)/60) ; 
			int hours = (int) (((finalTime/1000)/60)/60);
			seconds = seconds % 60; 
			minutes = minutes % 60;
			hours = hours % 60;
			tvTime.setText(hours + ":" + minutes + ":" 
			+ String.format("%02d", seconds)); 
			myHandler.postDelayed(this, 0); 
			
		}
	};
	
	@Override
	protected void onPause() 
	{
		// TODO Auto-generated method stub
		super.onPause();
		
		unregisterReceiver(broadCastRecieverUpdateWholeView);
	}

	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		
		registerReceiver(broadCastRecieverUpdateWholeView, new IntentFilter(getString(R.string.BroadCastFromServiceOnDestroy)));
		
	}
	
	@Override
	protected void onDestroy() 
	{	
		super.onDestroy();
		
		//gør at onActivityResult bliver kaldt i RunningMain -> ved dermed den skal opdatere. 
		finish(); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.running_detailed, menu);
		return true;
	}
	
	private void setupLocationManager()
	{
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		
		String provider = locationManager_.getBestProvider(criteria, true);
		Log.d("ima.smap.ztartrainer_ver2_log", provider);
		
		
		
		Location lastLocation = locationManager_.getLastKnownLocation(provider);
		LatLng locationInLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
		map_.moveCamera(CameraUpdateFactory.newLatLng(locationInLatLng));
		map_.animateCamera(CameraUpdateFactory.zoomTo(15));
	}
	

	private void setupButtonAndTextViews() 
	{
		btnStart_ = (Button)findViewById(R.id.btnStart);
		btnStop_ = (Button)findViewById(R.id.btnStop);
		
		btnStop_.setEnabled(false);
		
		tvAvgSpeed = (TextView)findViewById(R.id.txtviewAvgSpeedResult);
		tvDate = (TextView)findViewById(R.id.txtviewDateResult);
		tvDistance = (TextView)findViewById(R.id.txtviewDistanceResult);
		tvMaxSpeed = (TextView)findViewById(R.id.txtviewMaxSpeedResult);
		tvTime = (TextView)findViewById(R.id.txtviewTimeResult);	
		
		tvDistance.setText("0,00 Km");
		tvMaxSpeed.setText("0,00 Km/h");
		tvAvgSpeed.setText("0,00 Km/h");
		tvTime.setText("0:0:00");
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		startDate = new Date();
		String startDateAsString = formatter.format(startDate);
		
		tvDate.setText(startDateAsString);
	}
	
	@SuppressLint("SimpleDateFormat")
	private void setupMapWithInfoFromMainActivity()
	{
		btnStart_.setVisibility(View.GONE);
		btnStop_.setVisibility(View.GONE);
		
		//Dato
		long dateInLong = intentFromMain_.getLongExtra(getString(R.string.RunningMainToDetail_Date), 0);
		Date dateCorrect = new Date(dateInLong);
    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String dateCorrectString = formatter.format(dateCorrect);
    	
    	//Tid
    	String timeArray[] = intentFromMain_.getStringExtra(getString(R.string.RunningMainToDetail_Time)).split(":");

		tvAvgSpeed.setText(String.format("%.02f Km/h",intentFromMain_.getDoubleExtra(getString(R.string.RunningMainToDetail_AvgSpeed), 0)));
		tvDate.setText(dateCorrectString);
		tvDistance.setText(String.format("%.02f Km",intentFromMain_.getDoubleExtra(getString(R.string.RunningMainToDetail_Distance), 0)));
		tvMaxSpeed.setText(String.format("%.02f Km/h",intentFromMain_.getDoubleExtra(getString(R.string.RunningMainToDetail_MaxSpeed), 0)));
		tvTime.setText(FormatTimeStringToCorrectString(timeArray));

		runningIDFromMain_ = intentFromMain_.getIntExtra(getString(R.string.RunningMainToDetail_RunID), -1);
		Log.d("ima.smap.ztartrainer_ver2_log","Værdi af Running ID = " + String.valueOf(runningIDFromMain_));
		
		db_ = new RunningDatabase(this);
		
		db_.open();
		
		Cursor cursor = db_.getAllEntriesInRunningPoints(runningIDFromMain_);
		
		int counter = 0;
		
		LatLng startPoint = null;
		LatLng endPoint = null;
		
		while(cursor.moveToNext())
		{
			double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("Latitude")));
			double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("Longitude")));
			//String id = cursor.getString(cursor.getColumnIndexOrThrow("RunID")); 
			
			if(counter == 0) //Ikke andre punkter at sammenligne med.
			{
				startPoint = new LatLng(lat, longitude);
				endPoint = new LatLng(lat, longitude);
				
				if(startuserMarker_ != null)
				{
					startuserMarker_.remove(); //hvis den er lavet så fjern den fra tidligere.
				}
				
				startuserMarker_ = map_.addMarker(new MarkerOptions()
			    .position(startPoint)
			    .title("Start Position")
			    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
			    .snippet("Start på rute"));
				map_.moveCamera(CameraUpdateFactory.newLatLng(startPoint));
				map_.animateCamera(CameraUpdateFactory.zoomTo(15));
				
			}
			else
			{
				endPoint = new LatLng(lat, longitude);
				
				PolylineOptions rectOptions = new PolylineOptions()
				.add(startPoint).width(5).color(Color.BLUE).geodesic(true)
				.add(endPoint).width(5).color(Color.BLUE).geodesic(true);
			
				map_.addPolyline(rectOptions);
			}
			
			startPoint = endPoint;
							
			counter++;
		}
		
		if(endPoint != null)
		{
			EnduserMarker_ = map_.addMarker(new MarkerOptions()
			.position(endPoint)
			.title("Slut position")
			.snippet("Slutningen af ruten"));
			
			map_.moveCamera(CameraUpdateFactory.newLatLng(endPoint));
			map_.animateCamera(CameraUpdateFactory.zoomTo(15));
		}

		db_.close();
	}
	
	@SuppressLint("SimpleDateFormat")
	private void setupMapFromService(int runningID)
	{
		RunningRouteData itemFromDB = db_.getEntryFromRunningRouteData(runningID);
		
		Log.d("ima.smap.ztartrainer_ver2_log", String.valueOf(runningID));
		
		//Dato 
		Date dateCorrect = new Date(itemFromDB.DateEntry);
    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String dateCorrectString = formatter.format(dateCorrect);
    	
    	//Tid
    	String timeArray[] = itemFromDB.TimeEntry.split(":");

		tvAvgSpeed.setText(String.format("%.02f Km/h", itemFromDB.AvgSpeedEntry));
		tvMaxSpeed.setText(String.format("%.02f Km/h", itemFromDB.MaxSpeedEntry));
		tvDate.setText(dateCorrectString);
		tvDistance.setText(String.format("%.02f Km",itemFromDB.DistanceEntry));
		tvTime.setText(FormatTimeStringToCorrectString(timeArray));

		int runningIDFromService = itemFromDB.RunningIDEntry;
		Log.d("ima.smap.ztartrainer_ver2_log","Værdi af Running ID = " + String.valueOf(runningIDFromService));
		
		db_ = new RunningDatabase(this);
		
		db_.open();
		
		Cursor cursor = db_.getAllEntriesInRunningPoints(runningIDFromService);
		
		int counter = 0;
		
		LatLng startPoint = null;
		LatLng endPoint = null;
		
		while(cursor.moveToNext())
		{
			double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("Latitude")));
			double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("Longitude")));
			//String id = cursor.getString(cursor.getColumnIndexOrThrow("RunID")); 
			
			if(counter == 0) //Ikke andre punkter at sammenligne med.
			{
				startPoint = new LatLng(lat, longitude);
				endPoint = new LatLng(lat, longitude);
				
				if(startuserMarker_ != null)
				{
					startuserMarker_.remove(); //hvis den er lavet så fjern den fra tidligere.
				}
				
				startuserMarker_ = map_.addMarker(new MarkerOptions()
			    .position(startPoint)
			    .title("Start Position")
			    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
			    .snippet("Start på rute"));
				map_.moveCamera(CameraUpdateFactory.newLatLng(startPoint));
				map_.animateCamera(CameraUpdateFactory.zoomTo(15));
				
			}
			else
			{
				endPoint = new LatLng(lat, longitude);
				
				PolylineOptions rectOptions = new PolylineOptions()
				.add(startPoint).width(5).color(Color.BLUE).geodesic(true)
				.add(endPoint).width(5).color(Color.BLUE).geodesic(true);
			
				map_.addPolyline(rectOptions);
			}
			
			startPoint = endPoint;
							
			counter++;
		}
		
		if(endPoint != null)
		{
			EnduserMarker_ = map_.addMarker(new MarkerOptions()
			.position(endPoint)
			.title("Slut position")
			.snippet("Slutningen af ruten"));
			
			map_.moveCamera(CameraUpdateFactory.newLatLng(endPoint));
			map_.animateCamera(CameraUpdateFactory.zoomTo(15));
		}

		db_.close();
	}
	
	private void setupMap()
	{
		if(map_ == null)
		{
			map_ = ((MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
		}
		
		map_.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}
	
	private void zoomInOnLastKnowLocation()
	{
		locationManager_ = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		
		String provider = locationManager_.getBestProvider(criteria, true);
		
		Location lastLocation = locationManager_.getLastKnownLocation(provider);
		
		double lat = lastLocation.getLatitude();
		double longitude = lastLocation.getLongitude();
		
		LatLng lastLatLng = new LatLng(lat, longitude);
		
		map_.moveCamera(CameraUpdateFactory.newLatLng(lastLatLng));
		map_.animateCamera(CameraUpdateFactory.zoomTo(15));
	}
	
    public String FormatTimeStringToCorrectString(String times[])
    {
    	String hour = times[0];
    	String min = times[1];
    	String sec = times[2];
    	
    	int seconds = Integer.parseInt(sec);
    	
    	String endString;
    	
    	if(hour.compareTo("0") == 0)
    	{
    		if(min.compareTo("0") == 0)
    		{
    			if(seconds < 10)
    			{
        			endString =  seconds+" seconds";
    			}
    			else
    			{
        			endString =  sec+" seconds";
    			}
    		}
    		else
    		{
    			if(seconds < 10)
    			{
        			endString =  min+".0" +sec + " minutes";
    			}
    			else
    			{
        			endString =  min+"." +sec + " minutes";
    			}
    		}
   		}
    	else
    	{
    		endString = hour + "." + min + "." + sec + " hours"; 
    	}
    	
    	return endString;
    }
}
