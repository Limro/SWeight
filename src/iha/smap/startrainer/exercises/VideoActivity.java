package iha.smap.startrainer.exercises;

import iha.smap.startrainer.googleAPI.YoutubeDeveloperKey;
import iha.smap.startrainer.googleAPI.YouTubeFailureRecoveryActivity;

import iha.smap.startrainer.R;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import android.os.Bundle;
import android.widget.TextView;

public class VideoActivity extends YouTubeFailureRecoveryActivity
{
	private String videoLink;
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.activity_video);

	    YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
	    youTubePlayerFragment.initialize(YoutubeDeveloperKey.DEVELOPER_KEY, this);
	    
	    Bundle extras = getIntent().getExtras();
	    videoLink = (String)extras.get("link");
	    String clicked = (String)extras.get("clickedItem");
	    
		TextView tEdit = (TextView)findViewById(R.id.TextView);
		tEdit.setText(clicked);	
	  }

	  @Override
	  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) 
	  {
	    if (!wasRestored) {
	      player.loadVideo(videoLink);
	    }
	  }

	  @Override
	  protected YouTubePlayer.Provider getYouTubePlayerProvider() 
	  {
	    return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
	  }
	
}
