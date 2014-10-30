package ch.delavega_schumacher.schatzkarte;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import android.content.Context;

public class Marker {
	
	private int id;
	private GeoPoint markerPoint;
	private String description;
	
	/*Konstruktor/en*/
	
	public Marker(int id, double latitude, double longitude, String description)	
	{
		this.markerPoint = new GeoPoint(latitude, longitude);
		this.description = description;
		this.id = id;
	}

	public Marker(GeoPoint markerPoint, String description)
	{
		this.markerPoint = markerPoint;
		this.description = description;
	}
	
	// Funktion zur Umwandlung eines Markers in ein JSON Object
	public String getJsonFormat() throws JSONException
	{
		JSONObject jsonMarkerObj = new JSONObject();

		jsonMarkerObj.put("lat:", markerPoint.getLatitudeE6());
		jsonMarkerObj.put("lon:", markerPoint.getLongitudeE6());
		
		return jsonMarkerObj.toString();
	}
	
	public void save(Context context) // Save the Marker into the db
	{
		MapDBModel dbModel = new MapDBModel(context);
		dbModel.insert(this);
	}

	/* Getters and Setters */
	
	public int getId() {
		return id;
	}

	public double getLatitude() {
		return markerPoint.getLatitude();
	}
	
	public double getLongitude() {
		return markerPoint.getLongitude();
	}

	public String getDescription() {
		return description;
	}
}
