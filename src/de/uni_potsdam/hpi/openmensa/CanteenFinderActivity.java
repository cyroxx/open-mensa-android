package de.uni_potsdam.hpi.openmensa;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CanteenFinderActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_canteen_finder);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// FIXME: use custom CanteenFinder menu
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

}