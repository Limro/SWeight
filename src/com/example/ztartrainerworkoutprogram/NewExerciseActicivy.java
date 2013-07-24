package com.example.ztartrainerworkoutprogram;

import java.io.Serializable;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewExerciseActicivy extends Activity {

	private boolean newExercise = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_exercise_acticivy);

		Intent intent = getIntent();
		if (intent.hasExtra(getString(R.string.edit_exercise_message))) {
			Serializable sExercise = getIntent().getSerializableExtra(
					getString(R.string.edit_exercise_message));

			Exercise exercise = (Exercise) sExercise;

			EditText name = (EditText) findViewById(R.id.name_edittext_id);
			EditText sets = (EditText) findViewById(R.id.sets_edittext_id);
			EditText reps = (EditText) findViewById(R.id.reps_edittext_id);
			EditText kilo = (EditText) findViewById(R.id.kilo_edittext_id);

			name.setText(exercise.Name);
			sets.setText(exercise.Sets.toString());
			reps.setText(exercise.Repetitions.toString());
			kilo.setText(exercise.Kilo.toString());

			newExercise = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_exercise_acticivy, menu);
		return true;
	}

	@SuppressLint("NewApi")
	public void saveExercise(View view) {
		Intent intent = new Intent(this, CurrentProgramActivity.class);

		EditText name = (EditText) findViewById(R.id.name_edittext_id);
		EditText sets = (EditText) findViewById(R.id.sets_edittext_id);
		EditText reps = (EditText) findViewById(R.id.reps_edittext_id);
		EditText kilo = (EditText) findViewById(R.id.kilo_edittext_id);

		String nameString = name.getText().toString();
		String setsString = sets.getText().toString();
		String repsString = reps.getText().toString();
		String kiloString = kilo.getText().toString();

		if (nameString.length() < 1) {
			showToast(getString(R.string.no_name_toast));
			return;
		}
		if (nameString.length() > 12) {
			showToast(getString(R.string.exercise_name_to_long_toast));
			return;
		}
		if (nameString.trim().isEmpty()) {
			showToast(getString(R.string.no_name_toast));
			return;
		}

		try {
			Integer repsInt = Integer.parseInt(repsString);
			Integer kiloInt = Integer.parseInt(kiloString);

			if (kiloInt > 2000 || repsInt > 200 || repsInt < 1) {
				showToast(getString(R.string.wrong_number_toast));
				return;

			}
		} catch (NumberFormatException e) {
			showToast(getString(R.string.wrong_number_toast));
			return;
		}

		Exercise exercise = new Exercise(nameString, Integer.parseInt(setsString),
				Integer.parseInt(repsString), Integer.parseInt(kiloString));
		
		
		
		Intent returnIntent = new Intent();
		

		if (newExercise)
		{
			returnIntent.putExtra(getString(R.string.new_exersice_added), exercise);
			setResult(RESULT_OK, returnIntent); 
			finish();
		}
		else
		{
			returnIntent.putExtra(getString(R.string.existing_exercise_changed),exercise);
			setResult(RESULT_OK, returnIntent); 
			finish();
		}
	}

	public void cancelAddExercise(View view) {
		Intent intent = new Intent(this, CurrentProgramActivity.class);
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
