package iha.smap.startrainer.running;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import iha.smap.startrainer.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class RunningMain extends Activity implements OnItemClickListener
{
	 ListView listView;
	 ArrayList<RunningRowItem> rowItems;
	 ArrayList<RunningRouteData> listWithAllRoutes;
	 
	 private RunningDatabase db = null;
	 
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running_main);
		
		Button mapBtn = (Button)findViewById(R.id.btnNewRunningRoute);
		
		mapBtn.setOnClickListener(new OnClickListener() 
		{	
			public void onClick(View v) 
			{
				Intent mapIntent = new Intent(RunningMain.this, RunningDetailed.class);
				startActivityForResult(mapIntent, 1);
			}
		});
				
		db = new RunningDatabase(this);

		setupDataSourceFromDatabase();
	}
    
    @SuppressLint("SimpleDateFormat")
	public void setupDataSourceFromDatabase()
    {
		
		listWithAllRoutes = db.getAllRunningRoutes();
		rowItems = new ArrayList<RunningRowItem>();

		if(listWithAllRoutes != null)
		{
			
	        for (int i = 0; i < listWithAllRoutes.size(); i++) 
	        {
	        	RunningRouteData item = listWithAllRoutes.get(i);
	        	Date dateCorrect = new Date(item.DateEntry);
	        	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	        	String dateCorrectString = formatter.format(dateCorrect);
	        	
	        	String timeArray[] = item.TimeEntry.split(":");
	        	
	            RunningRowItem rowItem = new RunningRowItem(dateCorrectString,FormatTimeStringToCorrectString(timeArray), String.format("%.02f KM",item.DistanceEntry));
	            rowItems.add(rowItem);
	        }
		}
		
        listView = (ListView) findViewById(R.id.RunningMain_Listview);
        RunningMainArrayAdapter adapter = new RunningMainArrayAdapter(this,
                R.layout.list_item, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) 
    {
    	RunningRouteData item = listWithAllRoutes.get(position);
    	Intent mapIntent = new Intent(RunningMain.this, RunningDetailed.class);
    	mapIntent.putExtra(getString(R.string.RunningMainToDetail_AvgSpeed), item.AvgSpeedEntry);
    	mapIntent.putExtra(getString(R.string.RunningMainToDetail_Date), item.DateEntry);
    	mapIntent.putExtra(getString(R.string.RunningMainToDetail_Distance), item.DistanceEntry);
    	mapIntent.putExtra(getString(R.string.RunningMainToDetail_MaxSpeed), item.MaxSpeedEntry);
    	mapIntent.putExtra(getString(R.string.RunningMainToDetail_Time), item.TimeEntry);
    	mapIntent.putExtra(getString(R.string.RunningMainToDetail_RunID), item.RunningIDEntry);
		startActivity(mapIntent);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 1)
		{
			if(resultCode == RESULT_OK)
			{
				setupDataSourceFromDatabase();
			}
		}		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.running_main, menu);
        return true;
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
    				String secondsString = sec.replace("0", "");
    				endString = secondsString + " seconds";
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
