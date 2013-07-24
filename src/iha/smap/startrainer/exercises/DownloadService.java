package iha.smap.startrainer.exercises;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import iha.smap.startrainer.R;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;

public class DownloadService extends Service 
{
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private Bitmap mainPicture;
    private Bitmap secondPicture;
	
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder 
    {
    	public DownloadService getService() 
        {
            // Return this instance of LocalService so clients can call public methods
            return DownloadService.this;
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) 
    {
        return mBinder;
    }
    
    // This method is called on the main GUI thread.
    public void DownloadDataThread() 
    {
    	// This moves the time consuming operation to a child thread.
    	Thread thread = new Thread(null, downloadDataProcessing, "Background");
    	thread.start();
    }
    
    // Runnable that executes the background processing method.
    private Runnable downloadDataProcessing = new Runnable() 
    {
    	public void run() 
    	{
    		downloadData();
    	}
	};
	
	private void downloadData() 
	{	
		mainPicture = DownloadImage("http://get-fit.biz/wp-content/themes/duotive-fortune-wordpress-theme/duotive-fortune/includes/timthumb.php?src=/wp-content/uploads/2012/05/95-tone-define-arms-lose-bingo-wings-nice-arms-triceps-biceps.jpg&h=250&w=650&a=c&zc=1&q=100");
		secondPicture = DownloadImage("http://get-fit.biz/wp-content/themes/duotive-fortune-wordpress-theme/duotive-fortune/includes/timthumb.php?src=/wp-content/uploads/2012/05/197-fit-for-life-toned-muscles-good-body-with-older-age.jpg&h=250&w=650&a=c&zc=1&q=100");
		
		Intent intent = new Intent();
		intent.setAction(getResources().getString(R.string.downloadBroadcast));
		sendBroadcast(intent); 
	}
	
	public Bitmap getMainArt()
	{
		return mainPicture;
	}
	
	public Bitmap getSecondArt()
	{
		return secondPicture;
	}
	
	private InputStream OpenHttpConnection (String urlString) throws IOException
	{
        InputStream in = null;
        int response = -1;
                
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
         
        HttpURLConnection httpConn = (HttpURLConnection) conn;
        httpConn.setAllowUserInteraction(false);
        httpConn.setInstanceFollowRedirects(true);
        httpConn.setRequestMethod("GET");
            
        httpConn.connect(); 
 
        response = httpConn.getResponseCode();                 
        if (response == HttpURLConnection.HTTP_OK) 
        {
            in = httpConn.getInputStream();                                 
        }                     

		return in;
	}
	
	private Bitmap DownloadImage(String urlString)
    {        
        Bitmap bitmap = null;
        InputStream in = null;        
        try 
        {
            in = OpenHttpConnection(urlString);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } 
        catch (IOException e1) 
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return bitmap;                
    }
	
}
