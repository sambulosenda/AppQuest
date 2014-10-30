package ch.delavega_schumacher.schatzkarte;

	import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import  ch.delavega_schumacher.schatzkarte.R;
	
	public class MapDBModel extends SQLiteOpenHelper {
	 
	    // Database Version
	    private static final int DATABASE_VERSION = 1;
	    // Database Name
	    private static final String DATABASE_NAME = "TreasureMapDb";
	    
	    private static final String TABLE_COORDINATES = "coordinates";
	    // all the columns of the table coordinates
	    private static final String COL_ID = "id";
	    private static final String COL_DESCRIPTION = "description";
	    private static final String COL_LONGITUDE = "longitude";
	    private static final String COL_LATITUDE = "latitude";
	    
	    /* Konstruktor/en */
	    
	    public MapDBModel(Context context) {
	    	
	        super(context, DATABASE_NAME, null, DATABASE_VERSION); 
	    }
	    
	    /*SQL STATEMENTS */
	    
	    public List<Marker> getAllMarkers() {
	        List<Marker> markers = new LinkedList<Marker>();
	 
	        // 1. build the query
	        String query = "SELECT  * FROM " + TABLE_COORDINATES;
	 
	        // 2. get reference to writable DB
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(query, null);
	 
	        Marker marker = null;
	        if (cursor.moveToFirst()) {
	            do {
	                marker = new Marker(Integer.parseInt(cursor.getString(0)),
	                					Float.parseFloat(cursor.getString(3)),
	                					Float.parseFloat(cursor.getString(2)),
	                					cursor.getString(1));
	    	 
	                // 
	                markers.add(marker);
	            } while (cursor.moveToNext());
	        }
	        return markers;
	    }
	    
	    public void delete(Marker marker) {
	    	 
	        SQLiteDatabase db = this.getWritableDatabase();
	 
	        db.delete(TABLE_COORDINATES,
	                COL_ID+" = ?",
	                new String[] { String.valueOf(marker.getId()) });
	 
	        db.close();
	 
	    }
	    
	    public void insert(Marker marker)
	    {
	         SQLiteDatabase db = this.getWritableDatabase();
	  
	         ContentValues values = new ContentValues();
	         values.put(COL_ID, marker.getId());
	         values.put(COL_DESCRIPTION, marker.getDescription()); // Description of the Marker Position
	         values.put(COL_LONGITUDE, marker.getLongitude()); // Longitude of the Marker Position
	         values.put(COL_LATITUDE, marker.getLatitude()); // Latitude of the Marker Position

	         db.insert(TABLE_COORDINATES,
	                 null, 
	                 values); 
	         db.close(); 
	    }
	    
	    /* States */
	    
	    public void onCreate(SQLiteDatabase db) {
	    	String CREATE_MAP_TABLE = "CREATE TABLE coordinates ( " +
	                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	                "description TEXT, "+
	                "latitude REAL(10) NOT NULL, " +
	                "longitude REAL(10) NOT NULL)";
	    	
	        db.execSQL(CREATE_MAP_TABLE);
	    }
	 
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	       
	    }

}
