package de.uni_potsdam.hpi.openmensa;

import java.util.ArrayList;
import java.util.Map.Entry;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.uni_potsdam.hpi.openmensa.api.Canteen;
import de.uni_potsdam.hpi.openmensa.api.Canteens;
import de.uni_potsdam.hpi.openmensa.api.preferences.SettingsProvider;
import de.uni_potsdam.hpi.openmensa.helpers.OnFinishedFetchingCanteensListener;
import de.uni_potsdam.hpi.openmensa.helpers.RetrieveFeedTask;
import de.uni_potsdam.hpi.openmensa.location.ILastLocationFinder;
import de.uni_potsdam.hpi.openmensa.location.LastLocationFinder;
import de.uni_potsdam.hpi.openmensa.location.LocationConstants;

public class CanteenFinderActivity extends Activity implements OnFinishedFetchingCanteensListener {
	public static final String TAG = MainActivity.TAG;
	
	protected ILastLocationFinder lastLocationFinder;
	protected Location location;
	protected ArrayAdapter<Canteen> canteenListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_canteen_finder);
		
		lastLocationFinder = new LastLocationFinder(this);
		
		canteenListAdapter = new ArrayAdapter<Canteen>(this, android.R.layout.simple_list_item_1);
		ListView listView = (ListView) findViewById(R.id.canteen_list_view);
		listView.setAdapter(canteenListAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		getLocationAndUpdateCanteens(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// FIXME: use custom CanteenFinder menu
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	/**
	 * Find the last known location (using a {@link LastLocationFinder}) and
	 * updates the place list accordingly.
	 * 
	 * @param updateWhenLocationChanges
	 *            Request location updates
	 */
	protected void getLocationAndUpdateCanteens(boolean updateWhenLocationChanges) {
		// This isn't directly affecting the UI, so put it on a worker thread.
		AsyncTask<Void, Void, Void> findLastLocationTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// Find the last known location, specifying a required accuracy
				// of within the min distance between updates
				// and a required latency of the minimum time required between
				// updates.
				Location lastKnownLocation = lastLocationFinder
						.getLastBestLocation(LocationConstants.MAX_DISTANCE,
								System.currentTimeMillis()
										- LocationConstants.MAX_TIME);
				
				location = lastKnownLocation;
				
				// TODO Modify the search radius based on user settings?
				updateCanteens(lastKnownLocation, LocationConstants.DEFAULT_RADIUS, false);
				return null;
			}
		};
		findLastLocationTask.execute();
	}

	
	/**
	 * Update the list of nearby places centered on the specified Location,
	 * within the specified radius. This will start the
	 * {@link PlacesUpdateService} that will poll the underlying web service.
	 * 
	 * @param location
	 *            Location
	 * @param radius
	 *            Radius (meters)
	 * @param forceRefresh
	 *            Force Refresh
	 */
	protected void updateCanteens(Location location, int radius, boolean b) {
		if (location != null) {
			Log.d(TAG, "Updating canteens for location [" + location.getLatitude() + ", " + location.getLongitude() + "]");
			
			// fetch canteen list from server
			String baseUrl = SettingsProvider.getSourceUrl(this);
			String url = baseUrl + "canteens" + "?limit=50&near[lat]=" + location.getLatitude() + "&near[lng]=" + location.getLongitude();

			RetrieveFeedTask task = new RetrieveCanteenFeedTask(this, this, url, false);
			task.execute(url);
		} else
			Log.d(TAG, "Updating canteens list for: No Previous Location Found");
	}

	@Override
	public void onCanteenFetchFinished(RetrieveCanteenFeedTask task) {
		Canteens canteens = task.getCanteens();
		
		ArrayList<Canteen> canteenList = new ArrayList<Canteen>();
		for (Entry<String, Canteen> entry : canteens.entrySet()) {
			Canteen canteen = entry.getValue();
			
			canteenList.add(canteen);
			
			Log.d(TAG, "ID: " + canteen.key);
			Log.d(TAG, "Name: " + canteen.name);
			Log.d(TAG, "Distance: " + location.distanceTo( canteen.getLocation() ));
		}
		
		canteenListAdapter.clear();
		canteenListAdapter.addAll( canteenList );
		
	}

}