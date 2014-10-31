package ch.delavega_schumacher.schatzkarte;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.content.Context;

public class MapFileModel {

	private String filename;

	private static final String markerPrefix = "(";
	private static final String markerSuffix = ")";
	private static final String markerCoordinateSplitter = "/";
	private static final String markerSplitter = ", ";

	public void saveFile(Context context, String path, ArrayList<Marker> data) throws IOException {
		String fileData = serialize(data);
		FileOutputStream fileOutputStream = context.openFileOutput(path, Context.MODE_PRIVATE);

		fileOutputStream.write(fileData.getBytes());
		fileOutputStream.close();
	}

	public static String serialize(List<Marker> markers) {
		StringBuffer markersAsString = new StringBuffer("");
		boolean firstItem = true;
		for (Marker marker : markers) {
			// Append comma if needed
			if (firstItem) {
				firstItem = false;
			}
			else {
				markersAsString.append(markerSplitter);
			}

			// Append data
			markersAsString.append(markerPrefix + marker.getMarkerLocation().getLatitudeE6() + markerCoordinateSplitter + marker.getMarkerLocation().getLongitudeE6() + markerSuffix);
		}

		return markersAsString.toString();
	}

	public List<Marker> loadData(Context context, String path) throws IOException {
		StringBuffer data = new StringBuffer("");
		File file = new File(context.getFilesDir(), path);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;

		while (true) {
			line = bufferedReader.readLine();

			if (line == null) {
				break;
			}
			else {
				data.append(line + "\n");
			}
		}

		bufferedReader.close();
		return deserialize(data.toString());
	}

	public static List<Marker> deserialize(String serializedData) {
		List<Marker> markers = new ArrayList<Marker>();
		List<String> markersAsString = new ArrayList<String>();

		// Split into single serialized items
		if (serializedData.contains(markerSplitter)) {
			markersAsString = Arrays.asList(serializedData.split(markerSplitter)); 
		}
		else {
			markersAsString.add(serializedData);
		}

		// Assign 
		for (String markerAsString: markersAsString) {
			// Remove unnecessary characters
			markerAsString = markerAsString.replace(markerPrefix, "");
			markerAsString = markerAsString.replace(markerSuffix, "");

			markerAsString = markerAsString.replace("\n", "");
			markerAsString = markerAsString.replace(" ", "");

			// Split coordinates
			if (!markerAsString.contains(markerCoordinateSplitter)) {
				return null;
			}

			List<String> coordinates = Arrays.asList(markerAsString.split(markerCoordinateSplitter));

			if (coordinates.size() != 2) {
				return null;
			}

			// Construct geo point
			String latitudeAsString = coordinates.get(0);
			String longitudeAsString = coordinates.get(1);

			markers.add(new Marker(new GeoPoint(Integer.parseInt(latitudeAsString), Integer.parseInt(longitudeAsString))));
		}

		return markers;
	}


}
