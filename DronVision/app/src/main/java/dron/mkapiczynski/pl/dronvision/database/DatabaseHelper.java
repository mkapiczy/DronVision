package dron.mkapiczynski.pl.dronvision.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Miix on 2016-01-13.
 *//*
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.TAG;

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Dron.db";
    private static final String TABLE_DRONES = "Drones";

    // Drons Table - column name
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "droneName";
    private static final String KEY_DESCRIPTION = "droneDescription";
    private static final String KEY_STATUS = "droneStatus";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_TRACKED = "tracked";
    private static final String KEY_VISUALIZED = "visualized";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_DRONES = "CREATE TABLE "
            + TABLE_DRONES + "(" + KEY_ID + "INTEGER PRIMARY KEY,"
            + KEY_NAME + "TEXT," + KEY_DESCRIPTION + "TEXT," + KEY_STATUS + "TEXT,"
            + KEY_LATITUDE + "NUMERIC," + KEY_LONGITUDE + "NUMERIC,"
            + KEY_TRACKED + "BOOLEAN," + KEY_VISUALIZED + "BOOLEAN" + ")";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DRONES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRONES);

        onCreate(db);
    }

    public long insertDrone(DBDrone drone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, drone.getDroneId());
        values.put(KEY_NAME, drone.getDroneName());
        values.put(KEY_DESCRIPTION, drone.getDroneDescription());
        values.put(KEY_STATUS, drone.getStatus().name());
        values.put(KEY_LATITUDE, drone.getLastLocation().getLatitude());
        values.put(KEY_LONGITUDE, drone.getLastLocation().getLongitude());


        long drone_id = db.insert(TABLE_DRONES, null, values);

        return drone_id;
    }

    public DBDrone findDrone(long droneId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String queryStr = "SELECT * FROM " + TABLE_DRONES + " WHERE "
                + KEY_ID + " = " + droneId;

        Log.d(TAG, queryStr);

        Cursor cursor = db.rawQuery(queryStr, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        DBDrone foundDrone = new DBDrone();
        foundDrone.setDroneId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        foundDrone.setDroneName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        foundDrone.setDroneDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
        foundDrone.setDroneStatus(DroneStatusEnum.valueOf(cursor.getString(cursor.getColumnIndex(KEY_STATUS))));
        foundDrone.setLastLocation(new GeoPoint(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)), cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))));
        int tracked = cursor.getInt(cursor.getColumnIndex(KEY_TRACKED));
        if(tracked!=0){
            foundDrone.setTracked(true);
        } else{
            foundDrone.setTracked(false);
        }
        int visualized = cursor.getInt(cursor.getColumnIndex(KEY_VISUALIZED));
        if(visualized!=0){
            foundDrone.setVisualized(true);
        } else{
            foundDrone.setVisualized(false);
        }

        return foundDrone;
    }

    public int updateDrone(DBDrone drone){
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_ID, drone.getDroneId());
        values.put(KEY_NAME, drone.getDroneName());
        values.put(KEY_DESCRIPTION, drone.getDroneDescription());
        values.put(KEY_STATUS, drone.getDroneStatus().name());
        values.put(KEY_LATITUDE, drone.getLastLocation().getLatitude());
        values.put(KEY_LONGITUDE, drone.getLastLocation().getLongitude());
        values.put(KEY_TRACKED, drone.getTracked());
        values.put(KEY_VISUALIZED, drone.getVisualized());

        return db.update(TABLE_DRONES, values, KEY_ID + " =?", new String[] { String.valueOf(drone.getDroneId())});
    }

    public List<DBDrone> getAllDrones(){
        SQLiteDatabase db = this.getReadableDatabase();

        String queryStr = "SELECT * FROM " + TABLE_DRONES;

        Log.d(TAG, queryStr);

        Cursor cursor = db.rawQuery(queryStr, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        List<DBDrone> drones = new ArrayList<>();

        do {
            DBDrone foundDrone = new DBDrone();
            foundDrone.setDroneId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            foundDrone.setDroneName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            foundDrone.setDroneDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
            foundDrone.setDroneStatus(DroneStatusEnum.valueOf(cursor.getString(cursor.getColumnIndex(KEY_STATUS))));
            foundDrone.setLastLocation(new GeoPoint(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)), cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))));
            int tracked = cursor.getInt(cursor.getColumnIndex(KEY_TRACKED));
            if(tracked!=0){
                foundDrone.setTracked(true);
            } else{
                foundDrone.setTracked(false);
            }
            int visualized = cursor.getInt(cursor.getColumnIndex(KEY_VISUALIZED));
            if(visualized!=0){
                foundDrone.setVisualized(true);
            } else{
                foundDrone.setVisualized(false);
            }
            drones.add(foundDrone);
        } while(cursor.moveToNext());

        return drones;
    }

    public List<DBDrone> getAlltrackedDrones(){
        SQLiteDatabase db = this.getReadableDatabase();

        String queryStr = "SELECT * FROM " + TABLE_DRONES + " WHERE " + KEY_TRACKED + " = " + 1;

        Log.d(TAG, queryStr);

        Cursor cursor = db.rawQuery(queryStr, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        List<DBDrone> drones = new ArrayList<>();

        do {
            DBDrone foundDrone = new DBDrone();
            foundDrone.setDroneId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            foundDrone.setDroneName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            foundDrone.setDroneDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
            foundDrone.setDroneStatus(DroneStatusEnum.valueOf(cursor.getString(cursor.getColumnIndex(KEY_STATUS))));
            foundDrone.setLastLocation(new GeoPoint(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)), cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))));
            int tracked = cursor.getInt(cursor.getColumnIndex(KEY_TRACKED));
            if(tracked!=0){
                foundDrone.setTracked(true);
            } else{
                foundDrone.setTracked(false);
            }
            int visualized = cursor.getInt(cursor.getColumnIndex(KEY_VISUALIZED));
            if(visualized!=0){
                foundDrone.setVisualized(true);
            } else{
                foundDrone.setVisualized(false);
            }
            drones.add(foundDrone);
        } while(cursor.moveToNext());

        return drones;
    }

    public List<DBDrone> getAllVisualizedDrones(){
        SQLiteDatabase db = this.getReadableDatabase();

        String queryStr = "SELECT * FROM " + TABLE_DRONES + " WHERE " + KEY_VISUALIZED + " = " + 1;

        Log.d(TAG, queryStr);

        Cursor cursor = db.rawQuery(queryStr, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        List<DBDrone> drones = new ArrayList<>();

        do {
            DBDrone foundDrone = new DBDrone();
            foundDrone.setDroneId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            foundDrone.setDroneName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            foundDrone.setDroneDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
            foundDrone.setDroneStatus(DroneStatusEnum.valueOf(cursor.getString(cursor.getColumnIndex(KEY_STATUS))));
            foundDrone.setLastLocation(new GeoPoint(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)), cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))));
            int tracked = cursor.getInt(cursor.getColumnIndex(KEY_TRACKED));
            if(tracked!=0){
                foundDrone.setTracked(true);
            } else{
                foundDrone.setTracked(false);
            }
            int visualized = cursor.getInt(cursor.getColumnIndex(KEY_VISUALIZED));
            if(visualized!=0){
                foundDrone.setVisualized(true);
            } else{
                foundDrone.setVisualized(false);
            }
            drones.add(foundDrone);
        } while(cursor.moveToNext());

        return drones;
    }
}*/
