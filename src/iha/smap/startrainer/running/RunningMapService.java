package iha.smap.startrainer.running;

import iha.smap.startrainer.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class RunningMapService extends Service implements android.location.LocationListener
{	
	LocationManager locationManager_;
	long startDate;
	
	private RunningDatabase db = null;
	private int RunningID;
	
	float distanceTotal = 0;
	double maxSpeedTotal = 0;
	double avgSpeedTotal = 0;
	int counterToAvgSpeed = 0;
	int counterOfLocations = 0;
	Location startLocation = null;
	
	public RunningMapService() 
	{
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		
		db = new RunningDatabase(this);
		
		RunningID = db.getNumberOfRunningIDs();
		
		Log.d("ima.smap.ztartrainer_ver2_log", String.valueOf(RunningID));
		
		setupLocationProvider();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		startDate = intent.getLongExtra(getString(R.string.ServiceIntentExtra_DateString),0);
		
		return Service.START_STICKY; 
	}

	public void setupLocationProvider()
	{
		locationManager_ = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		
		String provider = locationManager_.getBestProvider(criteria, true);
		//String provider = LocationManager.NETWORK_PROVIDER;
		
		Log.d("ima.smap.ztartrainer_ver2_log", "Provider = " + provider);
	
		//Tjekker hver 2 sekund, men kun hvis der er en forskel på min 5 meter. 
		locationManager_.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, this);
	
	}
	
	
	
	@Override
	public void onDestroy() 
	{		
		Log.d("ima.smap.ztartrainer_ver2_log", "onDestory Kaldt i Service");
		
		locationManager_.removeUpdates(this); //Stopper GPS opdatering
		
		//DummyTest();
	 			
		SharedPreferences prefs = this.getSharedPreferences(getString(R.string.TimeWhenClickOnStop_SharedPreference), Context.MODE_PRIVATE);
		String endTimeString = prefs.getString((getString(R.string.TimeWhenClickOnStop_SharedPreference)), "0:0:00");
		
		double avgSpeedToSend = avgSpeedTotal/counterToAvgSpeed;
		
		if(Double.isNaN(avgSpeedToSend))
			avgSpeedToSend=0;
		
		RunningRouteData dummyObj = new RunningRouteData(RunningID, startDate, maxSpeedTotal, avgSpeedToSend, distanceTotal, endTimeString);
		
		db.insertEntryIntoRunningRoute(dummyObj);
		
		Intent intentToActivity = new Intent(getString(R.string.BroadCastFromServiceOnDestroy));
		intentToActivity.putExtra(getString(R.string.BroadCastFromServiceRunningID), RunningID);
		sendBroadcast(intentToActivity);
				
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location newlocation) 
	{
		// TODO Auto-generated method stub
		Log.d("ima.smap.ztartrainer_ver2_log", newlocation.toString());
				
		Location endLocation;
		float[] distanceResult = new float[1];
		
		if(counterOfLocations == 0)
		{
			startLocation = newlocation;
			counterToAvgSpeed++; //Bliver talt en op første gang så den er klar til der kommer en ny koordinat og der dermed er en hastighed (pga 2 koordinater)
		}
		else
		{
			endLocation = newlocation;

			Location.distanceBetween(startLocation.getLatitude(), startLocation.getLongitude(), endLocation.getLatitude(), endLocation.getLongitude(), distanceResult);
			distanceTotal += distanceResult[0];
			distanceTotal = distanceTotal/1000;
						
			double AvgSpeed = (double)endLocation.getSpeed();
			AvgSpeed = AvgSpeed * 3.6; //Km/t
			avgSpeedTotal += AvgSpeed;
			
			Log.d("ima.smap.ztartrainer_ver2_log", "AVG SPEED = " + AvgSpeed);
			
			if(AvgSpeed > maxSpeedTotal)
			{
				maxSpeedTotal = AvgSpeed;
			}
			
			Intent intentToActivity = new Intent(getString(R.string.BroadCastFromServiceOnDestroy));
			intentToActivity.putExtra(getString(R.string.BroadCastFromLocationChanged_AvgSpeed), avgSpeedTotal/counterToAvgSpeed);
			intentToActivity.putExtra(getString(R.string.BroadCastFromLocationChanged_Distance), distanceTotal);
			intentToActivity.putExtra(getString(R.string.BroadCastFromLocationChanged_MaxSpeed), maxSpeedTotal);
			sendBroadcast(intentToActivity);
			
			startLocation = endLocation;
		}
		
		RunningGPSPoint runningPoint = new RunningGPSPoint(newlocation.getLatitude(), newlocation.getLongitude(), RunningID);
		
		db.insertEntryInRunningPointsTable(runningPoint);
		
		counterToAvgSpeed++;
		counterOfLocations++;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}




}
