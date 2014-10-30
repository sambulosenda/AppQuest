package ch.delavega_schumacher.schatzkarte;

import java.io.File;

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
import org.osmdroid.views.overlay.TilesOverlay;

import ch.delavega_schumacher.appquestfunctions.Logging.Logbook;
import ch.delavega_schumacher.appquestfunctions.android.Application;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Schatzkarte extends Activity {

	private MapView map;
	private GeoPoint tempLocation;

	private LocationListener locationListener;
	private LocationManager locationManager;
	
	private static Logbook Log = Logbook.getInstance();
	private static Application application = Application.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		
		Button btnSetMarker = (Button)findViewById(R.id.btnSetMarker);
		btnSetMarker.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				setMarker(true);
			}
		});
		
		Button btnRemoveAllMarkers = (Button)findViewById(R.id.btnRemoveAllMarkers);
		btnRemoveAllMarkers.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) { 
				// löschen aller Marker (eventuell Methode, die nicht sofort alle, sondern auch einzelne löschen kann)
			}	
		});
		
		this.configureLocationListener();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schatzkarte);
	}

	public void configureLocationListener()
	{
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		locationListener = getLocationListener();
		
		int minimumTime = 0;
		int minimumDistance = 0;
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minimumTime, minimumDistance, locationListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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

	/* Eigene Funktionen */
	public void log()
	{			
		try
		{
			String taskname, logMessage;

			taskname = "Schatzkarte";
			logMessage = "Test"; // TODO: erhalten der Koordinaten in unserer Datenbank und umwandeln dieser in JSON - Format

			Intent Logger = Log.log(this, taskname, logMessage);
			startActivity(Logger);
		}
		catch(Exception ex)
		{
			application.showErrors(this, getResources().getString(R.string.error_logging_not_possible));
		}
	}

	public void setMarker(boolean currentPlacePerson)
	{
		if(currentPlacePerson)
		{
			Marker marker = new Marker(tempLocation, "");
			marker.save(this);
		}
		else
		{
			// alternativ an Standort setzen, wo getippt wurde

			// longitude and latitude in der Datenbank eintragen
		}
	}

	public void refreshMap()
	{
		// alle Marker auf der Karte setzen
	}

	public void configureMap()
	{
		map = (MapView) findViewById(R.id.map);
		map.setTileSource(TileSourceFactory.MAPQUESTOSM);

		map.setMultiTouchControls(true);
		map.setBuiltInZoomControls(true);

		IMapController controller = map.getController();
		controller.setZoom(18);

		XYTileSource treasureMapTileSource = new XYTileSource("mbtiles", ResourceProxy.string.offline_mode, 1, 20, 256, ".png", "http://example.org/");

		File file = new File(Environment.getExternalStorageDirectory() + getString(R.string.map_tiles_path), getString(R.string.map_tiles_file_name));

		MapTileModuleProviderBase treasureMapModuleProvider = new MapTileFileArchiveProvider(new SimpleRegisterReceiver(this),
				treasureMapTileSource, new IArchiveFile[] { MBTilesFileArchive.getDatabaseFileArchive(file) });

		MapTileProviderBase treasureMapProvider = new MapTileProviderArray(treasureMapTileSource, null,
				new MapTileModuleProviderBase[] { treasureMapModuleProvider });

		TilesOverlay treasureMapTilesOverlay = new TilesOverlay(treasureMapProvider, getBaseContext());
		treasureMapTilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
		map.getOverlays().add(treasureMapTilesOverlay);		
	}

	public LocationListener getLocationListener()
	{
		return new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				tempLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
			}
			
			public void onProviderDisabled(String provider)
			{
				
			}

			public void onProviderEnabled(String provider)
			{
				
			}

			public void onStatusChanged(String prodiver, int status, Bundle extras)
			{
				
			}
		};
	}

}
