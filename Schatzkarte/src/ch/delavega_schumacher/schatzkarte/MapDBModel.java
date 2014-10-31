package ch.delavega_schumacher.schatzkarte;

	import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import  ch.delavega_schumacher.schatzkarte.R;
	
	public class MapDBModel extends SQLiteOpenHelper {
	
	    // Database Version
	    private static final int DATABASE_VERSION = 1;
	    // Database Name
	    private static final String DATABASE_NAME = "TreasureMapDb";
	    
	    private static final String TABLE_COORDINATES = "coordinates";
	   
	    private static final String COL_ID = "id";
	    private static final String COL_DESCRIPTION = "description";
	    private static final String COL_LONGITUDE = "longitude";
	    private static final String COL_LATITUDE = "latitude";
	    
	    /* Konstruktor/en */
	    
	    public MapDBModel(Context context) {
	    	
	        super(context, DATABASE_NAME, null, DATABASE_VERSION); 
	    }
	    
	    public void onCreate(SQLiteDatabase db) {
	    	String CREATE_MAP_TABLE = "CREATE TABLE IF NOT EXISTS coordinates ( " +
	                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	                "description TEXT, "+
	                "latitude REAL(10) NOT NULL, " +
	                "longitude REAL(10) NOT NULL)";
	    	
	        db.execSQL(CREATE_MAP_TABLE);
	    }
	    
	    /*SQL STATEMENTS */
	    
	    public boolean doesDatatableExist()
	    {
	    	SQLiteDatabase db = this.getReadableDatabase();
	    	Cursor c = null;
	    	boolean tableExists = false;
	    	/* get cursor on it */
	    	try
	    	{
	    	    c = db.query(TABLE_COORDINATES, null,
	    	        null, null, null, null, null);
	    	        tableExists = true;
	    	}
	    	catch (Exception e) {
	   
	    	}

	    	return tableExists;	    	
	    }
	    
	    public List<Marker> getAllMarkers() {
	        List<Marker> markers = new LinkedList<Marker>();
	 
	        String query = "SELECT id, description, latitude, longitude FROM " + TABLE_COORDINATES;
	        SQLiteDatabase db = this.getReadableDatabase();
	        
	        Cursor cursor = db.rawQuery(query, null);
	 
	        if (cursor.moveToFirst()) {
	            do {
	            	Marker marker = new Marker();
	            	
	            	marker.setId(Integer.parseInt(cursor.getString(0)));
	            	marker.setDescription(cursor.getString(1));
	            	marker.setMarkerPoint(Float.parseFloat(cursor.getString(3)), Float.parseFloat(cursor.getString(2)));
	                markers.add(marker);
	                
	                Log.d("Marker", marker.toString());
	            } while (cursor.moveToNext());
	        }
	        Log.d("getAllMarkers()", markers.toString());
	        
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
	         ContentValues values = new ContentValues();
	         values.put(COL_ID, marker.getId());
	         values.put(COL_DESCRIPTION, marker.getDescription()); // Description of the Marker Position
	         values.put(COL_LONGITUDE, marker.getLongitude()); // Longitude of the Marker Position
	         values.put(COL_LATITUDE, marker.getLatitude()); // Latitude of the Marker Position

	         SQLiteDatabase db = getWritableDatabase();
	         
	         db.insert(TABLE_COORDINATES,
	                 null, 
	                 values); 
	         db.close(); 
	    }
	    
	    /* States */
	 
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	       
	    }

}
