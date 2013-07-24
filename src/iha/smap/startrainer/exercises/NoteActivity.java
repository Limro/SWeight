package iha.smap.startrainer.exercises;

import iha.smap.startrainer.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.EditText;

public class NoteActivity extends Activity 
{
	private String exerciseType;
	private Database db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note, menu);
		return true;
	}
	
	@Override
	protected void onStart() 
	{
		// TODO Auto-generated method stub
		super.onStart();

		db = new Database(this);
		Bundle extras = getIntent().getExtras();
		exerciseType = (String)extras.get("exerciseType");

		Entry en = db.getEntry(exerciseType);

		if (en != null)
		{
			EditText mEdit = (EditText)findViewById(R.id.noteField);
			mEdit.setText(en.Notes);	
		}
	}
	
	@Override
	protected void onStop() 
	{
		// TODO Auto-generated method stub
		super.onStop();
		EditText mEdit = (EditText)findViewById(R.id.noteField);
		String text = mEdit.getText().toString();
		
		Entry newEn = new Entry(exerciseType, text);
		
		Entry oldEn = db.getEntry(exerciseType);
		if (oldEn == null)
		{
			db.insertEntry(newEn);
		}
		else
		{
			db.updateEntry(exerciseType, newEn);
		}
		
		db = null;
	}

}
