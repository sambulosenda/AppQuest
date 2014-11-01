package ch.delavega_schumacher.schatzkarte;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MBTilesFileArchive;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;


import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;

import ch.delavega_schumacher.appquestfunctions.Logging.Logbook;
import ch.delavega_schumacher.appquestfunctions.android.Application;
import ch.delavega_schumacher.schatzkarte.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Schatzkarte extends Activity implements LocationListener {

	private MapView map;
	IMapController controller;
	private ResourceProxy mResourceProxy;
	private ItemizedOverlay<OverlayItem> mMyMarkerOverlay;

	private GeoPoint tempLocation;
	private List<Marker> markers;

	private LocationManager locationManager;
	//private MapDBModel dbModel;
	private MapFileModel mapModel;
	
	private TextView tvResult;

	private static Logbook Log = Logbook.getInstance();
	private static Application application = Application.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schatzkarte);
		mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());

		mapModel = new MapFileModel();
		markers = new ArrayList<Marker>();
		try
		{
			if(application.doesFileExist(Environment.getExternalStorageDirectory() + getString(R.string.map_tiles_path), getString(R.string.map_tiles_file_name)))
			{
				this.configureMap();
			}
			else
			{
				application.showErrors(this, getResources().getString(R.string.error_map_file_not_found));
			}
		}
		catch(Exception ex)
		{
			application.showErrors(this, getResources().getString(R.string.error_map_creating));
		}

		ImageButton btnSetMarker = (ImageButton)findViewById(R.id.btnSetMarker);
		btnSetMarker.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				setMarker(true);
			}
		});

		ImageButton btnRemoveAllMarkers = (ImageButton)findViewById(R.id.btnRemoveAllMarkers);
		btnRemoveAllMarkers.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) { 
				deleteMarker(); // delete the current last set marker 
			}
		});

		tvResult = (TextView)findViewById(R.id.tvResult);
		
		this.configureLocationManager();

	}

	public void onResume()
	{
		super.onResume();

		mapModel = new MapFileModel();
		this.configureLocationManager();
	}

	public void onPause()
	{
		super.onPause();

		locationManager.removeUpdates(this);
		mapModel = new MapFileModel();
	}

	public void configureLocationManager()
	{
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			application.showErrors(this, getString(R.string.error_gps_not_enabled));			
		}

		int minimumTime = 0;
		int minimumDistance = 0;
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minimumTime, minimumDistance, (LocationListener) this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minimumTime, minimumDistance, (LocationListener) this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.schatzkarte, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case(R.id.log):
			this.log();
		break;
		}



		return super.onOptionsItemSelected(item);
	}

	/* Logging */

	public void log()
	{			
		try
		{
			String taskname, logMessage;
			logMessage = "Koordinaten";
			taskname = "Schatzkarte";

			if(application.doesFileExist(getFilesDir().toString(), getString(R.string.map_data_file_filename))){
				markers = mapModel.loadData(this, getString(R.string.map_data_file_filename));

				if(markers == null)
				{
					markers = new ArrayList<Marker>();
				}

				JSONArray jsonMarkerList = new JSONArray();

				for(Marker marker : markers)
				{
					jsonMarkerList.put(marker.getJsonObject());
				}

				logMessage = jsonMarkerList.toString();
			}	

			Intent Logger = Log.log(this, taskname, logMessage);
			startActivity(Logger);
		}
		catch(Exception ex)
		{
			application.showErrors(this, getResources().getString(R.string.error_logging_not_possible));
		}
	}

	public void refreshMap()
	{
		// alle Marker auf der Karte setzen
		markers = new ArrayList<Marker>();
		try {
			if(application.doesFileExist(getFilesDir().toString(), getString(R.string.map_data_file_filename))){
				markers = mapModel.loadData(this, getString(R.string.map_data_file_filename));

				if(markers == null)
				{
					markers = new ArrayList<Marker>();
				}

				ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

				for(Marker marker : markers)
				{
					items.add(new OverlayItem("", "", marker.getMarkerLocation()));			
					controller.setCenter(marker.getMarkerLocation());
				}

				Drawable drwMarker=getResources().getDrawable(R.drawable.marker);
				int markerWidth = drwMarker.getIntrinsicWidth();
				int markerHeight = drwMarker.getIntrinsicHeight();
				drwMarker.setBounds(0, markerHeight, markerWidth, 0);

				this.mMyMarkerOverlay = new ItemizedIconOverlay<OverlayItem>(items, drwMarker, null, mResourceProxy);//(items,
				map.getOverlays().add(this.mMyMarkerOverlay);
				map.invalidate();
			}
			else
			{
				application.showErrors(this, getString(R.string.error_file_not_found));
			}

		} catch (IOException e) {
			application.showErrors(this, getString(R.string.error_reading_file));
		}
	}

	public void configureMap()
	{
		map = (MapView) findViewById(R.id.map);
		map.getOverlays().clear();
		map.setTileSource(TileSourceFactory.MAPQUESTOSM);

		map.setMultiTouchControls(true);
		map.setBuiltInZoomControls(true);

		controller = map.getController();
		controller.setZoom(22);

		float defaultLatitute = Float.parseFloat (getResources().getString (R.string.map_default_location_latitute));
		float defaultLongitude = Float.parseFloat (getResources().getString (R.string.map_default_location_longitude));
		controller.setCenter(new GeoPoint(defaultLatitute, defaultLongitude));

		XYTileSource treasureMapTileSource = new XYTileSource("mbtiles", ResourceProxy.string.offline_mode, 1, 20, 256, ".png", "http://example.org/");

		File file = new File(Environment.getExternalStorageDirectory() + getString(R.string.map_tiles_path), getString(R.string.map_tiles_file_name));

		MapTileModuleProviderBase treasureMapModuleProvider = new MapTileFileArchiveProvider(new SimpleRegisterReceiver(this),
				treasureMapTileSource, new IArchiveFile[] { MBTilesFileArchive.getDatabaseFileArchive(file) });

		MapTileProviderBase treasureMapProvider = new MapTileProviderArray(treasureMapTileSource, null,
				new MapTileModuleProviderBase[] { treasureMapModuleProvider });

		TilesOverlay treasureMapTilesOverlay = new TilesOverlay(treasureMapProvider, getBaseContext());
		treasureMapTilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		map.getOverlays().add(treasureMapTilesOverlay);

		refreshMap();
	}

	/* Own Methods (Controls) */

	public void setMarker(boolean currentPlacePerson)
	{
		if(tempLocation != null)
		{
			Marker marker = new Marker(tempLocation); 
			controller.setCenter(tempLocation);
			markers.add(marker);
			
			tvResult.setText("Erfolgreich Marker gesetzt!");
			
			this.saveFile();

		}
		else
		{
			application.showErrors(this, "There is no location to set a marker!");
		}

	}

	private void deleteMarker() {
		if(markers.size() > 0)
		{
			markers.remove(markers.size() - 1);
			tvResult.setText("Marker erfolgreich gelöscht!");
			
			this.saveFile();
		}
		else
		{
			application.showErrors(this, getString(R.string.error_no_markers));
		}
	}	

	private void saveFile()
	{
		try {
			mapModel.saveFile(this, getString(R.string.map_data_file_filename), (ArrayList)markers);
			this.configureMap();
		} catch (IOException e) {
			application.showErrors(this, getString(R.string.error_save_file));
		}
	}

	/* Listener */

	@Override
	public void onLocationChanged(Location location) {
		tempLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
