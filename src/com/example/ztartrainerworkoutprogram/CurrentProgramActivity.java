package com.example.ztartrainerworkoutprogram;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adapters.ExerciseDeleteListViewAdapter;

public class CurrentProgramActivity extends Activity {
	private ExerciseDeleteListViewAdapter adapter;
	private Program program;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_program_activity);

		program = new Program();

		ExerciseFileHandler loader = new ExerciseFileHandler();
		loader.execute("");			

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Integer position = intent.getIntExtra(
						getString(R.string.deleted_exersice_pos), 0);
				program.Exercises.remove(position);
				ExerciseSave save = new ExerciseSave();
				save.execute("");			
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(getString(R.string.delete_button_pressed_broadcast));
		registerReceiver(receiver, filter);
	}

	private class ExerciseSave  extends AsyncTask<String, String, String> {
		
		@Override
		protected String doInBackground(String... params) {
			SaveExercises();
			return "true";
		}
	}
	
	
	private class ExerciseFileHandler extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			LoadExercises();						
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			UpdateExersicesList();
			
			if (program.Exercises.isEmpty()) {
				Button runBtn = (Button) findViewById(R.id.run_button_id);
				runBtn.setEnabled(false);
			}					
		}

	}

	private void LoadExercises() {
		SharedPreferences mySharedPreferences = getSharedPreferences(
				(getString(R.string.ztar_shared_preferences)),
				Activity.MODE_PRIVATE);
		String fileName = mySharedPreferences.getString(
				getString(R.string.current_program_name_pref), null);
		if (fileName != null) {

			try {
				FileInputStream fis = openFileInput(fileName);
				ObjectInputStream is = new ObjectInputStream(fis);
				Program newProgram = (Program) is.readObject();
				is.close();

				program.Name = newProgram.Name;
				program.Description = newProgram.Description;

				for (Exercise e : newProgram.Exercises) {
					program.Exercises.add(e);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void SaveExercises() {
		SharedPreferences mySharedPreferences = getSharedPreferences(
				(getString(R.string.ztar_shared_preferences)),
				Activity.MODE_PRIVATE);
		String fileName = mySharedPreferences.getString(
				getString(R.string.current_program_name_pref), null);
		if (fileName != null) {
			try {
				FileOutputStream fos = openFileOutput(fileName,
						Context.MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(fos);
				os.writeObject(program);
				os.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void UpdateExersicesList() {
		TextView programName = (TextView) this
				.findViewById(R.id.name_textview_id);
		programName.setText(program.Name);

		final ListView programsListView = (ListView) this
				.findViewById(R.id.exercises_listview_id);
		adapter = new ExerciseDeleteListViewAdapter(this,
				R.layout.exercise_delete_lisvtiew, program.Exercises);
		programsListView.setAdapter(adapter);

		programsListView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				SharedPreferences mySharedPreferences = getSharedPreferences(
						getString(R.string.ztar_shared_preferences),
						Activity.MODE_PRIVATE);

				SharedPreferences.Editor editor = mySharedPreferences.edit();
				editor.putInt(getString(R.string.current_exersice_pref),
						position);
				editor.apply();

				EditExercise(position);
			}
		});
	}

	public void EditExercise(int position) {
		
		Intent intent = new Intent(this, NewExerciseActicivy.class);
		
		intent.putExtra(getString(R.string.edit_exercise_message),
				program.Exercises.get(position));	
		startActivityForResult(intent, 2);
		//this.startActivity(intent);
		
		
	}

	private void SaveProgram() {

		SharedPreferences mySharedPreferences = getSharedPreferences(
				getString(R.string.ztar_shared_preferences),
				Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(getString(R.string.current_program_name_pref),
				program.Name);
		editor.commit();

		try {
			FileOutputStream fos = openFileOutput(program.Name,
					Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(program);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.current_program, menu);
		return true;
	}

	public void runProgram(View view) {

		SharedPreferences mySharedPreferences = getSharedPreferences(
				(getString(R.string.ztar_shared_preferences)),
				Activity.MODE_PRIVATE);
		String currentProgramName = mySharedPreferences.getString(
				getString(R.string.saved_running_program_pref), "");

		if (currentProgramName.equals(program.Name)) {
			// Put up the Yes/No message box
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(
					getString(R.string.continue_or_reload_program_message))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(
							getString(R.string.continue_program_popup),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									openProgramActivity(true);
								}
							})
					.setNegativeButton(getString(R.string.reset_program_popup),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									openProgramActivity(false);
								}
							}) // Do nothing on no
					.show();
		} else
			openProgramActivity(false);
	}

	private void openProgramActivity(boolean continueProgram) {
		if (continueProgram) {
			Intent intent = new Intent(this, RunningProgramActicity.class);
			intent.putExtra(getString(R.string.run_program_message), program);
			intent.putExtra(getString(R.string.continue_saved_program_message), true);
			this.startActivity(intent);
		} else {
			Intent intent = new Intent(this, RunningProgramActicity.class);
			intent.putExtra(getString(R.string.run_program_message), program);
			this.startActivity(intent);
		}
	}

	public void addExercise(View view) {
		Intent intent = new Intent(CurrentProgramActivity.this, NewExerciseActicivy.class);
		startActivityForResult(intent, 1);
		//this.startActivity(intent);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 1) //add
		{
			if(resultCode == RESULT_OK)
			{
				Serializable sExercise = data.getSerializableExtra(
						getString(R.string.new_exersice_added));
				Exercise exercise = (Exercise) sExercise;
				program.Exercises.add(exercise);
				SaveExercises();
				
				Button runBtn = (Button) findViewById(R.id.run_button_id);
				
				if (program.Exercises.isEmpty()) 
					runBtn.setEnabled(false);
				else 
					runBtn.setEnabled(true);
				
				adapter.notifyDataSetChanged();
			}
		}
		
		else if(requestCode == 2) //edit
		{
			if(resultCode == RESULT_OK)
			{
				Serializable sExercise = data.getSerializableExtra(
						getString(R.string.existing_exercise_changed));

				Exercise exercise = (Exercise) sExercise;

				SharedPreferences mySharedPreferences = getSharedPreferences(
						(getString(R.string.ztar_shared_preferences)),
						Activity.MODE_PRIVATE);
				Integer pos = mySharedPreferences.getInt(
						getString(R.string.current_exersice_pref), 0);

				program.Exercises.set(pos, exercise);
				SaveExercises();				
				Button runBtn = (Button) findViewById(R.id.run_button_id);
				
				if (program.Exercises.isEmpty()) 
					runBtn.setEnabled(false);
				else 
					runBtn.setEnabled(true);
				
				adapter.notifyDataSetChanged();
			}
		}	
	}
}
