package iha.smap.startrainer.running;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RunningDatabase 
{
	private static final String TAG = "ima.smap.ztartrainer_ver2_log";

	private static final int DATABASE_VERSION = 1;
	//private static final String DATABASE_NAME = "SWeightDatabase.db";
	
	//Tabel til at holde alle løbepunkterne
	private static final String DATABASE_TABLE_RUNNINGPOINTS = "RunningTable";
	private static final String ENTRY_COL_ID = "Id";
	private static final String ENTRY_COL_RUNID = "RunID";
	private static final String ENTRY_COL_LATITUDE = "Latitude";
	private static final String ENTRY_COL_LONGITUDE = "Longitude";

	//Tabel til at lave generel oplysning om løberuterne
	private static final String DATABASE_TABLE_RUNNINGROUTE = "RunningRouteTable";
	private static final String ENTRY_COL_RR_ID = "Id";
	private static final String ENTRY_COL_RR_DATE = "Date";
	private static final String ENTRY_COL_RR_DISTANCE = "Distance";
	private static final String ENTRY_COL_RR_MAXSPEED = "MaxSpeed";
	private static final String ENTRY_COL_RR_AVGSPEED = "AvgSpeed";
	private static final String ENTRY_COL_RR_RUNID = "RunID"; //Giver adgang til alle punkterne i den anden tabel
	private static final String ENTRY_COL_RR_TIME = "Time";
	
	private static final int COL_RR_DATE = 1;
	private static final int COL_RR_DISTANCE = 2;
	private static final int COL_RR_MAXSPEED = 3;
	private static final int COL_RR_AVGSPEED = 4;
	private static final int COL_RR_TIME = 5;
	private static final int COL_RR_RUNID = 6;
	

	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE_RUNNINGPOINTS + " (" // begin
			+ ENTRY_COL_ID + " integer primary key autoincrement" + ", " // col1
			+ ENTRY_COL_RUNID + " integer not null" + ", " // col2
			+ ENTRY_COL_LATITUDE + " real not null" + ", "
			+ ENTRY_COL_LONGITUDE + " real not null);"; // col3

	private static final String DATABASE_RUNNINGROUTE_CREATE = "create table "
			+ DATABASE_TABLE_RUNNINGROUTE + " ("
			+ ENTRY_COL_RR_ID + " integer primary key autoincrement" + ", "
			+ ENTRY_COL_RR_DATE + " long not null" + ", "
			+ ENTRY_COL_RR_DISTANCE + " real not null" + ", "
			+ ENTRY_COL_RR_MAXSPEED + " real not null" + ", "
			+ ENTRY_COL_RR_AVGSPEED + " real not null" + ", "
			+ ENTRY_COL_RR_TIME + " text not null" + ", "
			+ ENTRY_COL_RR_RUNID + " integer not null);";
	
	// Variable to hold the database instance
	private SQLiteDatabase db;

	// Context of the application using the database.
	private final Context context;

	// Database open/upgrade helper
	private myDbHelper dbHelper;

	public RunningDatabase(Context _context)
	{
		context = _context;
		dbHelper = new myDbHelper(context, DATABASE_TABLE_RUNNINGPOINTS, null,
				DATABASE_VERSION);
	}

	public RunningDatabase open() throws SQLException
	{
		Log.i(TAG, "Opening database");
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close()
	{
		Log.i(TAG, "Closing database");
		db.close();
	}

	// Insert a 'Entry' in the database
	public long insertEntryInRunningPointsTable(RunningGPSPoint _myObject)
	{
		open();
		Log.i(TAG, "Inserting row in RunningPoints DB");

		ContentValues val = new ContentValues();
		val.put(ENTRY_COL_RUNID, _myObject.RunningID);
		val.put(ENTRY_COL_LATITUDE, _myObject.Latitude);
		val.put(ENTRY_COL_LONGITUDE, _myObject.Longitude);


		Long output = db.insert(DATABASE_TABLE_RUNNINGPOINTS, null, val);
		if (output == -1)
		{
			Log.d(TAG, "Value not inserted in database!");
		}

		close();
		return output;
	}
	
	public long insertEntryIntoRunningRoute(RunningRouteData runningRouteObj)
	{
		open();
		Log.i(TAG, "Inserting row in RunningRouteDB");

		ContentValues val = new ContentValues();
		val.put(ENTRY_COL_RR_RUNID, runningRouteObj.RunningIDEntry);
		val.put(ENTRY_COL_RR_AVGSPEED, runningRouteObj.AvgSpeedEntry);
		val.put(ENTRY_COL_RR_DATE, runningRouteObj.DateEntry);
		val.put(ENTRY_COL_RR_DISTANCE, runningRouteObj.DistanceEntry);
		val.put(ENTRY_COL_RR_MAXSPEED, runningRouteObj.MaxSpeedEntry);
		val.put(ENTRY_COL_RR_TIME, runningRouteObj.TimeEntry);

		Long output = db.insert(DATABASE_TABLE_RUNNINGROUTE, null, val);
		if (output == -1)
		{
			Log.d(TAG, "Value not inserted in database!");
		}

		close();
		return output;
	}

	// Will order after RUNID
	public Cursor getAllEntriesInRunningPoints(int runningID)
	{
		String where = ENTRY_COL_RUNID + "=" + runningID; 
		
		return db.query(DATABASE_TABLE_RUNNINGPOINTS, new String[]
		{ ENTRY_COL_ID, ENTRY_COL_RUNID, ENTRY_COL_LATITUDE, ENTRY_COL_LONGITUDE }, where, null, null,
				null, ENTRY_COL_RUNID);
	}

	public int getNumberOfRunningIDs()
	{
		open();
		
		Log.i(TAG, "Retrieving data from database");

		Cursor entry = db.query(true, DATABASE_TABLE_RUNNINGROUTE, new String[]
				{ ENTRY_COL_RR_RUNID }, null, null, ENTRY_COL_RR_RUNID, null, null, null);
		
		int returnValue = entry.getCount();
		
		close();
		
		return returnValue;
		
	}
	
	
	public RunningRouteData getEntryFromRunningRouteData(int rowIndex)
	{
		open();
		Log.i(TAG, "Retrieving data from database, ID: " + rowIndex);

		String where = ENTRY_COL_RR_RUNID + "=" + rowIndex;
		Cursor entry = db.query(DATABASE_TABLE_RUNNINGROUTE, null, where, null, null,
				null, null);
		
		if (entry != null)
		{
			if (entry.moveToFirst())
			{
				RunningRouteData runItem = new RunningRouteData(
						entry.getInt(COL_RR_RUNID), 
						entry.getLong(COL_RR_DATE), 
						entry.getDouble(COL_RR_MAXSPEED), 
						entry.getDouble(COL_RR_AVGSPEED), 
						entry.getDouble(COL_RR_DISTANCE), 
						entry.getString(COL_RR_TIME));
				
				close();
				return runItem;
			}
			else
			{
				Log.d(TAG, "No rows found!");
				close();
				return null;
			}
		}
		else
		{
			Log.d(TAG, "Nothing returned from database!");
			close();
			return null;
		}
	}

	
	public ArrayList<RunningRouteData> getAllRunningRoutes()
	{
		open();

		Cursor entry = db.query(DATABASE_TABLE_RUNNINGROUTE, null, null, null, null,
				null, null);

		if (entry != null)
		{
			if (entry.moveToFirst())
			{
				ArrayList<RunningRouteData> list = new ArrayList<RunningRouteData>();

				RunningRouteData runItem = new RunningRouteData(
						entry.getInt(COL_RR_RUNID), 
						entry.getLong(COL_RR_DATE), 
						entry.getDouble(COL_RR_MAXSPEED), 
						entry.getDouble(COL_RR_AVGSPEED), 
						entry.getDouble(COL_RR_DISTANCE), 
						entry.getString(COL_RR_TIME));
				
			
				list.add(runItem);

				while (entry.moveToNext())
				{
					RunningRouteData next =  new RunningRouteData(
							entry.getInt(COL_RR_RUNID), 
							entry.getLong(COL_RR_DATE), 
							entry.getDouble(COL_RR_MAXSPEED), 
							entry.getDouble(COL_RR_AVGSPEED), 
							entry.getDouble(COL_RR_DISTANCE), 
							entry.getString(COL_RR_TIME));
					list.add(next);
				}
				close();
				return list;
			}
		}
		else
		{
			Log.d(TAG, "No rows found!");
			close();
			return null;
		}
		return null;
	}

	private static class myDbHelper extends SQLiteOpenHelper
	{
		public myDbHelper(Context context, String name, CursorFactory factory,
				int version)
		{
			super(context, name, factory, version);
		}

		// Called when no database exists in disk and the helper class needs
		// to create a new one.
		@Override
		public void onCreate(SQLiteDatabase _db)
		{
			_db.execSQL(DATABASE_CREATE);
			_db.execSQL(DATABASE_RUNNINGROUTE_CREATE);
		}

		// Called when there is a database version mismatch meaning that the
		// version
		// of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion)
		{
			// Log the version upgrade.
			Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion
					+ " to " + _newVersion
					+ ", which will destroy all old data");

			// Upgrade the existing database to conform to the new version.
			// Multiple previous versions can be handled by comparing _oldVersion and
			// _newVersion values. The simplest case is to drop the old table and create a new one.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_RUNNINGPOINTS);
			// Create a new one.
			onCreate(_db);
		}
	}
	
}
