package iha.smap.startrainer.exercises;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

//Object type stored in 'EntryTable'
public class Entry
{
	private static final String TAG = "Entry";

	public Entry(String exerciseTypes, String notes)
	{
		ExerciseTypes = exerciseTypes;
		Notes = notes;
	}

	public String ExerciseTypes;
	public String Notes;

	// Helper function to format Date to String
	public static String FormatDateToTime(Date timeToFormat)
	{
		Log.i(TAG, "Formating Date to String");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder builder = new StringBuilder(format.format(timeToFormat));

		return builder.toString();
	}
}