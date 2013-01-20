package de.uni_potsdam.hpi.openmensa;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import de.uni_potsdam.hpi.openmensa.location.ILastLocationFinder;
import de.uni_potsdam.hpi.openmensa.location.LastLocationFinder;
import de.uni_potsdam.hpi.openmensa.location.LocationConstants;

public class CanteenFinderActivity extends Activity {
	public static final String TAG = MainActivity.TAG;
	
	protected ILastLocationFinder lastLocationFinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_canteen_finder);
		
		lastLocationFinder = new LastLocationFinder(this);
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

				// Update the place list based on the last known location within
				// a defined radius.
				// Note that this is *not* a forced update. The Place List
				// Service has settings to
				// determine how frequently the underlying web service should be
				// pinged. This function
				// is called everytime the Activity becomes active, so we don't
				// want to flood the server
				// unless the location has changed or a minimum latency or
				// distance has been covered.
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
			Log.d(TAG, "Updating place list.");
			Log.d(TAG, location.getLatitude() + " - kkk " + location.getLongitude());
		} else
			Log.d(TAG, "Updating place list for: No Previous Location Found");
	}

}