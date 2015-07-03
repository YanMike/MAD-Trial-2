package com.two.trial.yannick.todolist_2.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.two.trial.yannick.todolist_2.model.DataItem;
import com.two.trial.yannick.todolist_2.model.IDataItemCRUDOperations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Read out data items from an SQLite database
 *
 * @author Joern Kreutel
 *
 */
public class CRUDOperations implements IDataItemCRUDOperations {

    /**
     * the logger
     */
    protected static final String logger = CRUDOperations.class.getName();

    /**
     * the db name
     */
    public static final String DBNAME = "allAttr.db";

    /**
     * the initial version of the db based on which we decide whether to create
     * the table or not
     */
    public static final int INITIAL_DBVERSION = 0;

    /**
     * the table name
     */
    public static final String TABNAME = "dataitemsdescr";

    /**
     * the column names
     *
     * the _id column follows the convention required by the CursorAdapter usage
     */
    public static final String COL_ID       = "_id";
    public static final String COL_NAME     = "name";
    public static final String COL_EXPIRED  = "expired";
    public static final String COL_DESCR    = "description";
    public static final String COL_DONE     = "done";
    public static final String COL_FAV      = "favourite";

    /**
     * the creation query (if there is trouble try to change expired from integer to text...)
     */
    public static final String TABLE_CREATION_QUERY = "CREATE TABLE " + TABNAME
            + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + COL_NAME + " TEXT,\n"
            + COL_EXPIRED + " INTEGER,\n"
            + COL_DESCR + " TEXT,\n"
            + COL_DONE + " INTEGER,\n"
            + COL_FAV + " INTEGER);";

    /**
     * the -> where clause <- for item deletion
     */
    private static final String WHERE_IDENTIFY_ITEM = COL_ID + "=?";

    /**
     * we need to represent boolean attributes as int inside of the db
     */
    private static final int VALUE_EXPIRED = 1;

    /**
     * the database
     */
    private SQLiteDatabase db;

    /**
     * construct an instance and prepare the db
     */
    public CRUDOperations(Context context) {

        // prepare the db
        this.prepareSQLiteDatabase(context);
    }

    @Override
    public DataItem createDataItem(DataItem item) {
        Log.i(logger, "createDataItem(): " + item);

		/*
		 * first create the ContentValues object which will contain the column
		 * values to be inserted
		 */
            ContentValues values = createContentValues(item);

		/* insert the content values and take the id as return value */
        long id = db.insert(TABNAME, null,values);

		/* set the id on the item object */
        item.setId(id);

		/* return the item */
        return item;
    }

    @Override
    public List<DataItem> readAllDataItems() {
        Log.i(logger, "readAllDataItems()");
		/*
		 * declare a list of items that will keep the values read out from the
		 * db
		 */
        List<DataItem> items = new ArrayList<DataItem>();

		/*
		 * declare the columns to be read out (id, name and expired) as a String
		 * array
		 */

		/* declare an ASC ordering for the id column */

		/* query the db taking a cursor as return value */
            // expired = representation for latency in earlier versions of dataitem
            // ASC = aufsteigende Reihenfolge
//        Cursor cursor = db.query(TABNAME, new String[]{COL_ID, COL_NAME, COL_EXPIRED}, null, null, null, null, COL_ID + " ASC");
//        Cursor cursor = db.query(TABNAME, new String[]{COL_ID, COL_NAME, COL_EXPIRED, COL_DESCR}, null, null, null, null, COL_ID + " ASC");
        Cursor cursor = db.query(TABNAME, new String[]{COL_ID, COL_NAME, COL_EXPIRED, COL_DESCR, COL_DONE, COL_FAV}, null, null, null, null, COL_ID + " ASC");

		/* use the cursor, moving to the first dataset */
            // moveToFirst schlägt fehlt, wenn kein Inhalt da, andernfalls zeigt Cursor auf ersten Datensatz der auszugebenden Tabelle
        if (cursor.moveToFirst()) {
            /* iterate as long as we have reached the end */
			/* create an item from the current cursor position */
			/* move the cursor to the next item */
            do {
                DataItem currentItem = new DataItem();
                // "liefere mir vom aktuellen Datensatz wo du gerade drauf bist, den Wert der Namensspalte"; erwartet eigtl index, wenn man diesen nicht kennt -> .getColumnIndex()
                currentItem.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
                currentItem.setExpiry(cursor.getLong(cursor.getColumnIndex(COL_EXPIRED)));
                currentItem.setDescription(cursor.getString(cursor.getColumnIndex(COL_DESCR)));
                currentItem.setDone(cursor.getInt(cursor.getColumnIndex(COL_DONE)) == 1 ? true : false);
                currentItem.setFavourite(cursor.getInt(cursor.getColumnIndex(COL_FAV)) == 1 ? true : false);
                currentItem.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
                items.add(currentItem);
            } while(cursor.moveToNext());  // solange weitere Daten vorhanden sind, wird cursor.moveToNext() true sein/ weiter gehen
        } else {
            Log.i(logger, "no data for cursor");
        }
		/* return the items */
        return items;
    }

//	@Override
	public DataItem readDataItem(long dataItemId) {
		Log.i(logger, "CRUDOps - readDataItem(): " + dataItemId);


        // TODO: make query to find by ID
        DataItem currentItem = new DataItem();

        Cursor cursor = db.query(TABNAME, new String[]{COL_ID, COL_NAME, COL_EXPIRED, COL_DESCR, COL_DONE, COL_FAV}, null, null, null, null, COL_ID + " ASC");
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getLong(cursor.getColumnIndex(COL_ID)) == dataItemId) {
                    currentItem.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
                    currentItem.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
                    currentItem.setExpiry(cursor.getLong(cursor.getColumnIndex(COL_EXPIRED)));
                    currentItem.setDescription(cursor.getString(cursor.getColumnIndex(COL_DESCR)));
                    currentItem.setDone(cursor.getInt(cursor.getColumnIndex(COL_DONE)) == 1 ? true : false);
                    currentItem.setFavourite(cursor.getInt(cursor.getColumnIndex(COL_FAV)) == 1 ? true : false);
                    return currentItem;
                }

            } while(cursor.moveToNext());  // solange weitere Daten vorhanden sind, wird cursor.moveToNext() true sein/ weiter gehen
        } else {
            // TODO: give feedback (low prio)
            Log.i(logger, "no data for cursor");
        }
        return currentItem;

		/*
		 * query the db obtaining a cursor as return value and passing the where
		 * prepared statement and the id within a string array
		 */

		/* check how many items we have and move to first one */
			/* create the item from the cursor if we have got one */
//		return null;
	}

	public DataItem updateDataItem(DataItem item) {
		Log.i(logger, "updateDataItem(): " + item);

		/* as in create, create the content values object from the item */
        ContentValues values = createContentValues(item);

		/*
		 * then update the item in the db using the prepared statement for the
		 * where clause and passing the id of the item as a string
		 * we get the number of updated rows as a return value
		 */

        int updated = db.update(TABNAME, values, WHERE_IDENTIFY_ITEM, new String[]{String.valueOf(item.getId())});
		/* and return the item */
        if(updated > 0) {
            Log.i(logger, "Updated");
            return item;
        }
        Log.i(logger, "Not updated");
		return null;
	}

	@Override
	public boolean deleteDataItem(long dataItemId) {
		Log.i(logger, "deleteDataItem(): " + dataItemId);

		/*
		 * delete the item passing the prepared where clause and the item id as
		 * string, capture the return value indicating how many items have been
		 * deleted
		 */
        int numOfDeletedRows = db.delete(TABNAME, WHERE_IDENTIFY_ITEM, new String[]{String.valueOf(dataItemId)});

		/* check the return value from the deletion and return it */
		return numOfDeletedRows > 0;
	}

	/*
	 * helper methods for ORM etc.
	 */

    /**
     * create a ContentValues object which can be passed to a db query
     *
     * @param item
     * @return
     */
    private ContentValues createContentValues(DataItem item) {

		/* create a content values object */

		/* put the name and the expired attributes from the item into the object */

		/* return the object */

        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_EXPIRED, item.getExpiry());
        values.put(COL_DESCR, item.getDescription());
        values.put(COL_DONE, item.isDone() ? 1 : 0);
        values.put(COL_FAV, item.isFavourite() ? 1 : 0);

        return values;
    }

    /**
     * create an item from the cursor
     *
     * @param c
     * @return
     */
    public DataItem createItemFromCursor(Cursor c) {
		/* create an instance of DataItem */

		/* then populate the item with the results from the cursor */
		/*
		 * access the fields via getColumnIndex and then use the type-specific
		 * accessors
		 */

		/* return the item */
        return null;
    }

    /**
     * prepare the database
     */
    protected void prepareSQLiteDatabase(Context context) {

		/* create the database or leave it as it is */
        this.db = context.openOrCreateDatabase(DBNAME,
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

		/* we need to check which version we have... */
        Log.d(logger, "db version is: " + db.getVersion());
        if (this.db.getVersion() == INITIAL_DBVERSION) {
            Log.i(logger,
                    "the db has just been created. Need to create the table...");
            db.setLocale(Locale.getDefault());
			/* update the version */
            db.setVersion(INITIAL_DBVERSION + 1);
			/* and excute the table creation */
            db.execSQL(TABLE_CREATION_QUERY);
        } else {
            Log.i(logger, "the db exists already. No need for table creation.");
        }

    }

    public void finalise() {
		/* close the db to avoid trouble */
        this.db.close();
        Log.i(logger, "db has been closed");
    }
}