package iha.smap.startrainer;

import iha.smap.startrainer.exercises.MuscleActivity;
import iha.smap.startrainer.running.RunningMain;
import iha.smap.startrainer.weight.WeightMain;
import iha.smap.startrainer.workout.ProgramMainActicity;

import iha.smap.startrainer.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void ExerciseBtnClicked(View v)
	{
		Intent excerciseIntent = new Intent(this,MuscleActivity.class);
		startActivity(excerciseIntent);
	}
	
	public void RunningBtnClicked(View v)
	{
		Intent excerciseIntent = new Intent(this,RunningMain.class);
		startActivity(excerciseIntent);
	}
	
	public void WorkoutBtnClicked(View v)
	{
		Intent excerciseIntent = new Intent(this,ProgramMainActicity.class);
		startActivity(excerciseIntent);
	}
	
	public void WeightBtnClicked(View v)
	{
		Intent excerciseIntent = new Intent(this,WeightMain.class);
		startActivity(excerciseIntent);
	}

}
