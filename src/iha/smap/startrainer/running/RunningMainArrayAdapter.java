package iha.smap.startrainer.running;

import java.util.List;

import iha.smap.startrainer.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RunningMainArrayAdapter extends ArrayAdapter<RunningRowItem> 
{
	Context context;
	 
    public RunningMainArrayAdapter(Context context, int resourceId,List<RunningRowItem> items) 
    {
        super(context, resourceId, items);
        this.context = context;
    }
 
    /*private view holder class*/
    private class ViewHolder 
    {
        TextView txtDate;
        TextView txtDistance;
        TextView txtTime;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        ViewHolder holder = null;
        RunningRowItem rowItem = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtDate = (TextView) convertView.findViewById(R.id.ListItemTxtViewDate);
            holder.txtDistance= (TextView) convertView.findViewById(R.id.ListItemtxtViewDistance);
            holder.txtTime = (TextView) convertView.findViewById(R.id.ListItemtxtViewTime);
            convertView.setTag(holder);
        } 
        else
            holder = (ViewHolder) convertView.getTag();
 
        holder.txtDate.setText(rowItem.Date);
        holder.txtDistance.setText(rowItem.Distance);
        holder.txtTime.setText(rowItem.Time);
 
        return convertView;
    }
}
