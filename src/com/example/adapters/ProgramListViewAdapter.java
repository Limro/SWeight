package com.example.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ztartrainerworkoutprogram.Program;
import com.example.ztartrainerworkoutprogram.R;

public class ProgramListViewAdapter extends ArrayAdapter<Program> {
	Context context;
	List<Program> items;

	public ProgramListViewAdapter(Context context, int resourceId,
			List<Program> items) {
		super(context, resourceId, items);
		this.context = context;
		this.items = items;
	}

	private class ViewHolder {
		TextView programName;
		TextView programDesc;
		ImageView deleteImage;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Program program = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.program_listview, null);
			holder = new ViewHolder();
			holder.programName = (TextView) convertView
					.findViewById(R.id.programname_textview_id);
			holder.programDesc = (TextView) convertView
					.findViewById(R.id.exercisedescription_textview_id);
			holder.deleteImage = (ImageView) convertView
					.findViewById(R.id.delete_image_id);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.programName.setText(program.Name);
		holder.programDesc.setText(program.Description);

		holder.deleteImage.setOnClickListener(new imageViewClickListener(
				position));

		return convertView;
	}

	private class imageViewClickListener implements OnClickListener {
		int position;

		public imageViewClickListener(int pos) {
			this.position = pos;
		}

		public void onClick(View v) {

			context.deleteFile(items.get(position).Name);			
			items.remove(position);
			ProgramListViewAdapter.this.notifyDataSetChanged();

			Intent intent = new Intent();
			intent.setAction(context
					.getString(R.string.program_delete_button_pressed_broadcast));
			intent.putExtra(context.getString(R.string.deleted_exersice_pos),
					position);
			context.sendBroadcast(intent);
		}
	}
}