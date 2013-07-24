package com.example.ztartrainerworkoutprogram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adapters.RunningExersiceListViewAdapter;

public class RunningProgramActicity extends Activity {
	private List<Exercise> exercises;
	private List <Integer> exerciseClickedList;
	private RunningExersiceListViewAdapter adapter;
	private String programName;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.running_program_acticity);

		exercises = new ArrayList<Exercise>();
		exerciseClickedList = new ArrayList<Integer>();

		Serializable sprogram = getIntent().getSerializableExtra(
				getString(R.string.run_program_message));
		Program program = (Program) sprogram;
		programName = program.Name;

		TextView programName = (TextView) findViewById(R.id.name_textview_id);
		programName.setText(program.Name);

		for (Exercise exercise : program.Exercises) {
			exercises.add(exercise);
		}

		final ListView programsListView = (ListView) this
				.findViewById(R.id.exercises_listview_id);
		adapter = new RunningExersiceListViewAdapter(this,
				R.layout.exercise_listview, exercises);
		programsListView.setAdapter(adapter);

		programsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Exercise exersice = (Exercise) programsListView
						.getItemAtPosition(position);

				if (exersice.Sets > 0) {
					exerciseClickedList.add(position);
					exersice.Sets--;
					exercises.set(position, exersice);
					adapter.notifyDataSetChanged();
				}
			}
		});
		
		if (getIntent().hasExtra(getString(R.string.continue_saved_program_message)))
				{
			SharedPreferences mySharedPreferences = getSharedPreferences(
					(getString(R.string.ztar_shared_preferences)),
					Activity.MODE_PRIVATE);
			ClickedExercisesList cClass = (ClickedExercisesList) ObjectSerializer.deserialize(mySharedPreferences.getString(getString(R.string.saved_running_program__clicked_list_pref), ObjectSerializer.serialize(new ClickedExercisesList())));		
			
			for (Integer clickedE : cClass.clickedList)
			{				
				exerciseClickedList.add(clickedE);
				Exercise exersice = (Exercise) programsListView
						.getItemAtPosition(clickedE);
				exersice.Sets--;
				exercises.set(clickedE, exersice);
				adapter.notifyDataSetChanged();
			}
		}				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.running_program_acticity, menu);
		return true;
	}

	public void regret_click(View view) {
		if (exerciseClickedList.isEmpty())
			return;
		int pos = exerciseClickedList.get(exerciseClickedList.size()-1);
		Exercise exersice = exercises.get(pos);
		if (exersice.Sets < exersice.MaxSets) {
			
			exersice.Sets++;
			exercises.set(pos, exersice);
			exerciseClickedList.remove(exerciseClickedList.size()-1);
			adapter.notifyDataSetChanged();
		}
	}
	
    @SuppressLint("NewApi")
	@Override
    public void onDestroy()
    {
		SharedPreferences mySharedPreferences = getSharedPreferences(
				getString(R.string.ztar_shared_preferences),
				Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = mySharedPreferences.edit();
		
	
			
		ClickedExercisesList cClass = new ClickedExercisesList();
		
		for (Integer clickedEx : exerciseClickedList)
		{
			cClass.clickedList.add(clickedEx);
		}
		
		editor.putString(getString(R.string.saved_running_program__clicked_list_pref), ObjectSerializer.serialize(cClass));				
		editor.putString(getString(R.string.saved_running_program_pref),programName);
		editor.apply();
        super.onDestroy();    
    }
}
