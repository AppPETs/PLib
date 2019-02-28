package de.dailab.apppets.plib.async.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.dailab.apppets.plib.access.helper.PLibDataDecision;

/**
 * Database handler for user decisions with respect to data flows.
 * Created by arik on 16.02.2017.
 */

public class PLibDataDecisionDataBaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "DataDecisionDB";

    // DECISION table name
    private static final String TABLE_DECISION = "DECISION";

    // DECISION Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_STACK = "stack";
    private static final String KEY_TYPE = "type";
    private static final String KEY_HASH = "hash";
    private static final String KEY_TIME = "time";
    private static final String KEY_DECISION = "decision";

    /**
     * Constructor
     *
     * @param context
     */
    public PLibDataDecisionDataBaseHandler(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Adds or updates a new/old decision, identified by an id if available
     *
     * @param decision
     */
    public void addOrUpdateDecision(PLibDataDecision decision) {

        if (decision.getId() < 0) {
            addDecision(decision);
            return;
        }
        PLibDataDecision o = getDecision(decision.getId());
        if (o != null) {
            updateDecision(decision);
        } else {
            addDecision(decision);
        }
    }

    /**
     * Add a new decision to the data base.
     *
     * @param decision
     */
    public void addDecision(PLibDataDecision decision) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_HASH, decision.getHash()); // decision
        values.put(KEY_DESCRIPTION, decision.getDescription()); // description
        values.put(KEY_STACK, decision.getStackTrace()); // stack
        values.put(KEY_TYPE, decision.getDataType()); // type
        values.put(KEY_TIME, decision.getTime());
        values.put(KEY_DECISION, decision.getDecision().ordinal());
        db.insert(TABLE_DECISION, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Creating Tables
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_DECISION_TABLE =
                "CREATE TABLE " + TABLE_DECISION + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
                        KEY_DECISION + " INTEGER NOT NULL," + KEY_HASH + " INTEGER NOT NULL," +
                        KEY_TIME + " INTEGER NOT NULL," + KEY_DESCRIPTION + " TEXT," + KEY_STACK +
                        " TEXT NOT NULL," + KEY_TYPE + " TEXT NOT NULL" + ")";
        db.execSQL(CREATE_DECISION_TABLE);
    }

    /**
     * Upgrading database
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECISION);
        // Create tables again
        onCreate(db);
    }

    /**
     * Return a decision if available, identified by the db id.
     *
     * @param id
     *
     * @return
     */
    public PLibDataDecision getDecision(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DECISION,
                new String[] {KEY_ID, KEY_HASH, KEY_DESCRIPTION, KEY_STACK,
                        KEY_TYPE, KEY_TIME, KEY_DECISION}, KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        PLibDataDecision result = null;
        if (cursor != null) {
            cursor.moveToFirst();
            result = new PLibDataDecision();
            result.setId(cursor.getInt(0));
            result.setHash(cursor.getInt(1));
            result.setDescription(cursor.getString(2));
            result.setStackTrace(cursor.getString(3));
            result.setDataType(cursor.getString(4));
            result.setTime(cursor.getLong(5));
            result.setDecision(PLibDataDecision.PLibDecision.values()[cursor.getInt(6)]);
        }
        db.close();
        return result;
    }

    /**
     * Updating single decision, identified by the db id.
     *
     * @param decision
     *
     * @return
     */
    public int updateDecision(PLibDataDecision decision) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_HASH, decision.getHash()); // decision
        values.put(KEY_DESCRIPTION, decision.getDescription()); // description
        values.put(KEY_STACK, decision.getStackTrace()); // stack
        values.put(KEY_TYPE, decision.getDataType()); // type
        values.put(KEY_TIME, decision.getTime());
        values.put(KEY_DECISION, decision.getDecision().ordinal());
        int res = db.update(TABLE_DECISION, values, KEY_ID + " = ?",
                new String[] {String.valueOf(decision.getId())});
        db.close();
        return res;
    }

    /**
     * Return a decision if available, identified by a hash value.
     *
     * @param hash
     *
     * @return
     */
    public PLibDataDecision getDecisionByHash(int hash) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DECISION,
                new String[] {KEY_ID, KEY_HASH, KEY_DESCRIPTION, KEY_STACK,
                        KEY_TYPE, KEY_TIME, KEY_DECISION}, KEY_HASH + "=?",
                new String[] {String.valueOf(hash)}, null, null, null, null);
        PLibDataDecision result = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = new PLibDataDecision();
            result.setId(cursor.getInt(0));
            result.setHash(cursor.getInt(1));
            result.setDescription(cursor.getString(2));
            result.setStackTrace(cursor.getString(3));
            result.setDataType(cursor.getString(4));
            result.setTime(cursor.getLong(5));
            result.setDecision(PLibDataDecision.PLibDecision.values()[cursor.getInt(6)]);
        }
        db.close();
        return result;
    }

    /**
     * Returns all decisions, dependent on decision type.
     *
     * @return
     */
    public List<PLibDataDecision> getAllDecisions(PLibDataDecision.PLibDecision decision) {

        SQLiteDatabase db = this.getReadableDatabase();
        List<PLibDataDecision> resultList = new ArrayList<PLibDataDecision>();
        Cursor cursor = db.query(TABLE_DECISION,
                new String[] {KEY_ID, KEY_HASH, KEY_DESCRIPTION, KEY_STACK,
                        KEY_TYPE, KEY_TIME, KEY_DECISION}, KEY_DECISION + "=?",
                new String[] {String.valueOf(decision.ordinal())}, null, null, null,
                null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                PLibDataDecision result = new PLibDataDecision();
                result.setId(cursor.getInt(0));
                result.setHash(cursor.getInt(1));
                result.setDescription(cursor.getString(2));
                result.setStackTrace(cursor.getString(3));
                result.setDataType(cursor.getString(4));
                result.setTime(cursor.getLong(5));
                result.setDecision(PLibDataDecision.PLibDecision.values()[cursor.getInt(6)]);
                resultList.add(result);
            } while (cursor.moveToNext());
        }
        db.close();
        return resultList;
    }

    /**
     * Returns all decisions
     *
     * @return
     */
    public List<PLibDataDecision> getAllDecisions() {

        List<PLibDataDecision> resultList = new ArrayList<PLibDataDecision>();
        // Select All Query
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DECISION,
                new String[] {KEY_ID, KEY_HASH, KEY_DESCRIPTION, KEY_STACK,
                        KEY_TYPE, KEY_TIME, KEY_DECISION}, null,
                null, null, null, null,
                null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                PLibDataDecision result = new PLibDataDecision();
                result.setId(cursor.getInt(0));
                result.setHash(cursor.getInt(1));
                result.setDescription(cursor.getString(2));
                result.setStackTrace(cursor.getString(3));
                result.setDataType(cursor.getString(4));
                result.setTime(cursor.getLong(5));
                result.setDecision(PLibDataDecision.PLibDecision.values()[cursor.getInt(6)]);
                resultList.add(result);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return resultList;
    }

    /**
     * Returns the number of persistent decisions.
     *
     * @return
     */
    public int getDecisionCount() {

        String countQuery = "SELECT  * FROM " + TABLE_DECISION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        int res = cursor.getCount();
        db.close();
        return res;
    }

    /**
     * Removes decision, identified by the db id.
     *
     * @param decision
     */
    public void deleteDecision(PLibDataDecision decision) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DECISION, KEY_ID + " = ?", new String[] {String.valueOf(decision.getId())});
        db.close();
    }

    /**
     * Removes decision, identified by the  id.
     *
     * @param id
     */
    public void deleteDecision(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DECISION, KEY_ID + " = ?", new String[] {String.valueOf(id)});
        db.close();
    }


}