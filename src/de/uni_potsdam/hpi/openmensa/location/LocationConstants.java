package de.uni_potsdam.hpi.openmensa.location;

import android.app.AlarmManager;

public class LocationConstants {

	  /**
	   * These values control the user experience of your app. You should
	   * modify them to provide the best experience based on how your
	   * app will actually be used.
	   * TODO Update these values for your app.
	   */
	  // The default search radius when searching for places nearby.
	  public static int DEFAULT_RADIUS = 150;
	  // The maximum distance the user should travel between location updates. 
	  public static int MAX_DISTANCE = DEFAULT_RADIUS/2;
	  // The maximum time that should pass before the user gets a location update.
	  public static long MAX_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

}
