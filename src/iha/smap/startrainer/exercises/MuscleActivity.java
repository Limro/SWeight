package iha.smap.startrainer.exercises;

import iha.smap.startrainer.exercises.DownloadService.LocalBinder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import iha.smap.startrainer.R;

import android.os.Bundle;
import android.os.IBinder;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ListView;

public class MuscleActivity extends ListActivity 
{ 	
	private ArrayAdapter<String> arrayAdapter;
	private ArrayList<String> exercises = new ArrayList<String>();
	private Bitmap Art = null;
	
	private DownloadService mService;
	private boolean mBound = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_muscle);
		
		String[] muscleGroup = getResources().getStringArray(R.array.muscle);
		for (String muscle : muscleGroup)
		{
			exercises.add(muscle);
		}
		
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exercises);
        setListAdapter(arrayAdapter);
        
   	    ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
   	    pb.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onStart() 
	{
		super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        
		IntentFilter iff = new IntentFilter();
		iff.addAction(getResources().getString(R.string.downloadBroadcast));
		this.registerReceiver(DownloadReceiver, iff);
	}
	
    @Override
    protected void onStop() 
    {
        super.onStop();
        this.unregisterReceiver(DownloadReceiver);
        
        // Unbind from the service
        if (mBound) 
        {
            unbindService(mConnection);
            mBound = false;
        }
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		
		String clickedItem = (String)l.getItemAtPosition(position);
		
		Intent intent = new Intent(this, ExerciseActivity.class);
		intent.putExtra("clickedItem", clickedItem);
		
		if (Art != null)
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Art.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();

			intent.putExtra("art",byteArray);
		}
		
   	   ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
   	   pb.setVisibility(View.GONE);
		startActivity(intent);
	}
	
    public void onButtonClick(View v) 
    {
        if (mBound) 
        {
           if (Art != null)
           {
        	   return;
           }
           // Call a method from the LocalService.
           // However, if this call were something that might hang, then this request should
           // occur in a separate thread to avoid slowing down the activity performance.
      	   mService.DownloadDataThread();

      	   ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
      	   pb.setVisibility(View.VISIBLE);
        }
    }
	
    private BroadcastReceiver DownloadReceiver = new BroadcastReceiver()
    {
    	  @Override
    	  public void onReceive(Context context, Intent intent) 
    	  {
         	  ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
          	  pb.setVisibility(View.GONE);
    		  
    		  ImageView img = (ImageView) findViewById(R.id.img);
    		  img.setImageBitmap(mService.getMainArt());
    		  Art = mService.getSecondArt();
    	  }
    };
    
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() 
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) 
        {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) 
        {
            mBound = false;
        }
    };
	
}