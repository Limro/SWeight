package com.example.ztartrainerworkoutprogram;



import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewProgramActicity extends Activity {

	List<String> nameList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_program_acticity);
		
		Intent intent = getIntent();
		
		if (intent.hasExtra(getString(R.string.program_name_list_message)))
		{
			nameList = intent.getStringArrayListExtra(getString(R.string.program_name_list_message));
		}				
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_program_acticity, menu);
		return true;
	}
	
    @SuppressLint("NewApi")
	public void saveProgram(View view)
    {
    	//Intent intent = new Intent(this, CurrentProgramActivity.class);
    	
		EditText name = (EditText) findViewById(R.id.name_edittext_id);
		EditText desc = (EditText) findViewById(R.id.desc_edittext_id);

		String nameString = name.getText().toString();
		String descString = desc.getText().toString();

		if (nameString.length() < 1) {
			showToast(getString(R.string.no_name_toast));
			return;
		}

		if (nameString.trim().isEmpty()) {
			showToast(getString(R.string.no_name_toast));
			return;
		}	
		if (nameString.length() > 20) {
			showToast(getString(R.string.program_name_to_long_toast));
			return;
		}
		
		for (String pName : nameList)
		{
			if (nameString.equals(pName))
			{
				showToast(getString(R.string.program_nameexists_message));
				return;
			}
		}
    	    
		Intent returnIntent = new Intent();
		returnIntent.putExtra(getString(R.string.new_program_added_name), nameString);
		returnIntent.putExtra(getString(R.string.new_program_added_description), descString);
		setResult(RESULT_OK, returnIntent); 
		finish();
    }

    public void cancelProgram(View view)
    {
    	Intent intent = new Intent(this, ProgramMainActicity.class);
    	this.startActivity(intent);
    }
    
	private void showToast(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 425);
		toast.setDuration(2);
		toast.show();
	}
}
