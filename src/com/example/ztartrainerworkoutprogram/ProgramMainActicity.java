package com.example.ztartrainerworkoutprogram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import com.example.adapters.ProgramListViewAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ProgramMainActicity extends Activity {

	private List<Program> programs;
	ProgramListViewAdapter adapter;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		programs = new ArrayList<Program>();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.program_main_acticity);
		new ProgramLoader().execute("");

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Integer position = intent.getIntExtra(
						getString(R.string.deleted_exersice_pos), 0);
				DeleteProgram(position);
				programs.remove(position);
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(getString(R.string.program_delete_button_pressed_broadcast));
		registerReceiver(receiver, filter);
	}

	private void DeleteProgram(int pos) {
		this.deleteFile(programs.get(pos).Name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.program_main_acticity, menu);
		return true;
	}

	public void createNewProgram(View view) {
		Intent intent = new Intent(this, NewProgramActicity.class);
		List<String> nameList = new ArrayList<String>();

		for (Program p : programs) {
			nameList.add(p.Name);
		}

		intent.putExtra(getString(R.string.program_name_list_message),
				(ArrayList<String>) nameList);
		this.startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) // add
		{
			if (resultCode == RESULT_OK) {

				if (data.hasExtra(getString(R.string.new_program_added_name))) {
					String name = data
							.getStringExtra(getString(R.string.new_program_added_name));
					String desc = data
							.getStringExtra(getString(R.string.new_program_added_description));

					Program p = new Program(name, desc);
					SaveProgram(p);
					
					programs.add(p);
					
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	private void UpdateProgramList() {
		// I want to add the items by value - not by type
		// otherwise the adapter will have to delete the files!


		final ListView programsListView = (ListView) this
				.findViewById(R.id.program_list_view);
		adapter = new ProgramListViewAdapter(this, R.layout.program_listview,
				programs);
		programsListView.setAdapter(adapter);

		programsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				OpenProgram(position);
			}
		});

	}

	@SuppressLint("NewApi")
	public void OpenProgram(int position) {
		SharedPreferences mySharedPreferences = getSharedPreferences(
				getString(R.string.ztar_shared_preferences),
				Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(getString(R.string.current_program_name_pref),
				programs.get(position).Name);
		editor.apply();

		Intent intent = new Intent(this, CurrentProgramActivity.class);
		this.startActivity(intent);
	}

	private void SaveProgram(Program p) {
		SharedPreferences mySharedPreferences = getSharedPreferences(
				getString(R.string.ztar_shared_preferences),
				Activity.MODE_PRIVATE);

		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString(getString(R.string.current_program_name_pref), p.Name);
		editor.apply();

		String fileName = p.Name;
		if (fileName != null) {
			try {
				FileOutputStream fos = openFileOutput(fileName,
						Context.MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(fos);
				os.writeObject(p);
				os.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void LoadAllPrograms() throws StreamCorruptedException, IOException,
			ClassNotFoundException {

		File filePath = getFilesDir();
		File[] programFilesList = filePath.listFiles();

		for (File file : programFilesList) {
			FileInputStream fis = openFileInput(file.getName());
			ObjectInputStream is = new ObjectInputStream(fis);
			Program program = (Program) is.readObject();
			is.close();
			programs.add(program);
		}
	}

	private class ProgramLoader extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				LoadAllPrograms();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			UpdateProgramList();
		}
	}
}
