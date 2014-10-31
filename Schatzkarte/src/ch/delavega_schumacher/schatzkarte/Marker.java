package ch.delavega_schumacher.schatzkarte;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import android.content.Context;

public class Marker {

	private GeoPoint markerPoint;
	
	/*Konstruktor/en*/
	
	public Marker()
	{
		
	}
	
	public Marker(GeoPoint markerPoint)
	{
		this.markerPoint = markerPoint;
	}
	
	public Marker(double latitude, double longitude)	
	{
		this.markerPoint = new GeoPoint(latitude, longitude);
	}

	// Funktion zur Umwandlung eines Markers in ein JSON Object
	public JSONObject getJsonObject() throws JSONException
	{
		JSONObject jsonMarkerObj = new JSONObject();
		
		jsonMarkerObj.put("lon", markerPoint.getLongitudeE6());
		jsonMarkerObj.put("lat", markerPoint.getLatitudeE6());
		
		
		return jsonMarkerObj;
	}
	/* Getters and Setters */

	public GeoPoint getMarkerLocation() {
		return markerPoint;
	}
	
	public double getLatitude() {
		return markerPoint.getLatitude();
	}
	
	public double getLongitude() {
		return markerPoint.getLongitude();
	}

	public void setMarkerPoint(double longitude, double latitude) {
		this.markerPoint = new GeoPoint(latitude, longitude);
	}
}
