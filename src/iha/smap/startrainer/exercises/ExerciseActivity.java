package iha.smap.startrainer.exercises;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import iha.smap.startrainer.R;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class ExerciseActivity extends ListActivity 
{
	private ArrayAdapter<String> arrayAdapter;
	private String exerciseType;
	private ArrayList<String> exercises = new ArrayList<String>();
	private ArrayList<String> exercisesLinks = new ArrayList<String>();
	private Map<String, String[]> exerciseMap = new HashMap<String, String[]>(); 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise);
		
		String[] muscleGroup = getResources().getStringArray(R.array.muscle);
		for (String muscle : muscleGroup)
		{
			int identifier = getResources().getIdentifier(muscle, "array", getPackageName());
			String[] muscleArray = getResources().getStringArray(identifier);
			
			exerciseMap.put(muscle, muscleArray);	
		}
		
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exercises);
		setListAdapter(arrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.exercise, menu);
		return true;
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
		
		Bundle extras = getIntent().getExtras();
		exerciseType = (String)extras.get("clickedItem");
		byte[] byteArray = (byte[])extras.get("art");
		
		if (byteArray != null)
		{
			Bitmap art = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		    ImageView img = (ImageView) findViewById(R.id.img);
		    img.setImageBitmap(art);
		}
	
		exercises.clear();
		FillExerciseList(exerciseType);
		
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exercises);
		setListAdapter(arrayAdapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		
		String clickedItem = (String)l.getItemAtPosition(position);
		String link = null;
		
		for (String exerciseLink : exercisesLinks)
		{
			if (exerciseLink.contains(clickedItem))
			{
				link = exerciseLink.substring(exerciseLink.indexOf(":")+1);
			}		
		}
		
		Intent intent = new Intent(this, VideoActivity.class);
		intent.putExtra("clickedItem", clickedItem);
		intent.putExtra("link", link);
		startActivity(intent);
	}
	
    public void onNoteButtonClick(View v) 
    {
		Intent intent = new Intent(this, NoteActivity.class);
		intent.putExtra("exerciseType", exerciseType);
		startActivity(intent);
    }
	
	private void FillExerciseList(String exerciseType)
	{
		String[] exerciseArray = exerciseMap.get(exerciseType);
		
		for (String exercise : exerciseArray)
		{
			exercisesLinks.add(exercise);
			exercises.add(exercise.substring(0, exercise.indexOf(":")));
		}	
	}
	
}
