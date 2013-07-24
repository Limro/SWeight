package iha.smap.startrainer.workout;

import iha.smap.startrainer.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RunningExersiceListViewAdapter extends ArrayAdapter<Exercise>  {

	Context context;

	public RunningExersiceListViewAdapter(Context context, int resourceId,
			List<Exercise> items) {
		super(context, resourceId, items);
		this.context = context;
	}
	
    private class ViewHolder {
        TextView exerciseName;
        TextView sets;
        TextView reps;
        TextView kilo; 
        String maxSets;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        ViewHolder holder = null;
        Exercise exerciseItem = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.exercise_listview, null);
            holder = new ViewHolder();
            holder.exerciseName = (TextView) convertView.findViewById(R.id.name_textview_id);
            holder.sets = (TextView) convertView.findViewById(R.id.sets_textview_id);
            holder.reps = (TextView) convertView.findViewById(R.id.repititions_textview_id);
            holder.kilo = (TextView) convertView.findViewById(R.id.kilo_textview_id);
            holder.maxSets = exerciseItem.MaxSets.toString();
            
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
 
        holder.exerciseName.setText(exerciseItem.Name);
        holder.sets.setText(exerciseItem.Sets.toString());
        holder.reps.setText(exerciseItem.Repetitions.toString());
        holder.kilo.setText(exerciseItem.Kilo.toString());
 
        String set = holder.sets.getText().toString();
        
        if (set.equals("0"))
        	convertView.setBackgroundResource(android.R.color.holo_green_light);
        else if  (set.equals(holder.maxSets))
        	convertView.setBackgroundResource(android.R.color.transparent);
        else 
        	convertView.setBackgroundResource(android.R.color.holo_blue_light);
        
        return convertView;
        
        
    }

}
