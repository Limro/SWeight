package iha.smap.startrainer.exercises;

import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class Database
{

	private static final String TAG = "Database";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_TABLE_ENTRY = "EntryTable";
	private static final String ENTRY_COL_ID = "Id";
	private static final String ENTRY_COL_EXERCISETYPE = "Type";
	private static final String ENTRY_COL_NOTES = "Notestext";

	public static final int COL_ID = 0;
	public static final int COL_TYPE = 1;
	public static final int COL_NOTES = 2;

	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE_ENTRY + " (" // begin
			+ ENTRY_COL_ID + " integer primary key autoincrement" + ", " // col1
			+ ENTRY_COL_EXERCISETYPE + " text not null" + ", " // col2
			+ ENTRY_COL_NOTES + " text not null);"; // col3

	// Variable to hold the database instance
	private SQLiteDatabase db;

	// Context of the application using the database.
	private final Context context;

	// Database open/upgrade helper
	private myDbHelper dbHelper;

	public Database(Context _context)
	{
		context = _context;
		dbHelper = new myDbHelper(context, DATABASE_TABLE_ENTRY, null,
				DATABASE_VERSION);
	}

	public Database open() throws SQLException
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
	public long insertEntry(Entry _myObject)
	{
		open();
		Log.i(TAG, "Inserting row in database");

		ContentValues val = new ContentValues();
		val.put(ENTRY_COL_EXERCISETYPE, _myObject.ExerciseTypes);
		val.put(ENTRY_COL_NOTES, _myObject.Notes);

		Long output = db.insert(DATABASE_TABLE_ENTRY, null, val);
		if (output == -1)
		{
			Log.d(TAG, "Value not inserted in database!");
		}

		close();
		return output;
	}

	public boolean removeEntry(String table, long _rowIndex)
	{
		return db.delete(table, ENTRY_COL_ID + "=" + _rowIndex, null) > 0;
	}

	// Will order after Date
	public Cursor getAllEntries()
	{
		open();
		Cursor cursor = db.query(DATABASE_TABLE_ENTRY, new String[]
		{ ENTRY_COL_ID, ENTRY_COL_EXERCISETYPE, ENTRY_COL_NOTES}, null, null, null,
				null, null);
		close();
		return cursor;
	}

	public Entry getEntry(String type)
	{
		open();
		Log.i(TAG, "Retrieving data from database, ID: " + type);

		String where = ENTRY_COL_EXERCISETYPE + " LIKE '" + type + "'";
		Log.i(TAG, where);
		Cursor entry = db.query(DATABASE_TABLE_ENTRY, null, where, null, null,
				null, null);

		if (entry != null)
		{
			if (entry.moveToFirst())
			{
				Entry e = new Entry(entry.getString(COL_TYPE), entry.getString(COL_NOTES));
				close();
				return e;
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
			return null;
		}
	}


	public boolean updateEntry(String type, Entry myObject)
	{
		open();
		String where = ENTRY_COL_EXERCISETYPE + " LIKE '" + type + "'";
		ContentValues val = new ContentValues();
		val.put(ENTRY_COL_EXERCISETYPE, myObject.ExerciseTypes);
		val.put(ENTRY_COL_NOTES, myObject.Notes);

		int output = db.update(DATABASE_TABLE_ENTRY, val, where, null);
		close();
		return (output == 0 ? false : true);
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
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ENTRY);
			// Create a new one.
			onCreate(_db);
		}
	}
}
