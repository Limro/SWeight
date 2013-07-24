package iha.smap.startrainer.workout;

import iha.smap.startrainer.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExerciseDeleteListViewAdapter extends ArrayAdapter<Exercise> {

	Context context;
	List<Exercise> items;

	public ExerciseDeleteListViewAdapter(Context context, int resourceId,
			List<Exercise> items) {
		super(context, resourceId, items);
		this.context = context;
		this.items = items;
		
		//Exercise e = new Exercise("Name",0,0,0);
		//items();
		
		
		
	}

	private class ViewHolder {
		TextView exerciseName;
		TextView sets;
		TextView reps;
		TextView kilo;
		ImageView deleteImage;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Exercise exerciseItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.exercise_delete_lisvtiew, null);
			holder = new ViewHolder();
			
			
			holder.exerciseName = (TextView) convertView
					.findViewById(R.id.name_textview_id);
			holder.sets = (TextView) convertView
					.findViewById(R.id.sets_textview_id);		
			holder.reps = (TextView) convertView
					.findViewById(R.id.repititions_textview_id);
			holder.kilo = (TextView) convertView
					.findViewById(R.id.kilo_textview_id);
			holder.deleteImage = (ImageView) convertView
					.findViewById(R.id.delete_image_id);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.exerciseName.setText(exerciseItem.Name);
		holder.sets.setText(exerciseItem.Sets.toString());
		holder.reps.setText(exerciseItem.Repetitions.toString());
		holder.kilo.setText(exerciseItem.Kilo.toString());

		
		holder.deleteImage.setOnClickListener(new imageViewClickListener(position));

		return convertView;
	}
	
	private class imageViewClickListener implements OnClickListener {
		    int position;
		    public imageViewClickListener( int pos)
		        {
		            this.position = pos;
		        }

		    public void onClick(View v) {   
		        items.remove(position);
		        ExerciseDeleteListViewAdapter.this.notifyDataSetChanged();
		        
				Intent intent = new Intent();
				intent.setAction(context.getString(R.string.delete_button_pressed_broadcast));
				intent.putExtra(context.getString(R.string.deleted_exersice_pos), position );
				context.sendBroadcast(intent);
		}
	}
}
