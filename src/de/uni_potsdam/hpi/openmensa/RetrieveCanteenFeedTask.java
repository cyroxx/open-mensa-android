package de.uni_potsdam.hpi.openmensa;

import android.content.Context;
import android.util.Log;
import de.uni_potsdam.hpi.openmensa.api.Canteen;
import de.uni_potsdam.hpi.openmensa.api.Canteens;
import de.uni_potsdam.hpi.openmensa.helpers.OnFinishedFetchingCanteensListener;
import de.uni_potsdam.hpi.openmensa.helpers.RetrieveFeedTask;

/**
 * 
 * @author dominik
 * TODO: Parse the feed and write into the Hash instead of only saving the json
 */
public class RetrieveCanteenFeedTask extends RetrieveFeedTask {
	
	private Canteens canteens;
	private OnFinishedFetchingCanteensListener fetchListener;
	protected String name = "Canteens";
	private int currentPage = 1;
	private String url;
	
	public RetrieveCanteenFeedTask(Context context, OnFinishedFetchingCanteensListener fetchListener, String url, boolean showProgressDialog) {
		super(context);
		
		this.url = url;
		this.canteens = new Canteens();
		this.fetchListener = fetchListener;
		this.visible = showProgressDialog;
	}
	
	public RetrieveCanteenFeedTask(Context context, OnFinishedFetchingCanteensListener fetchListener, String url) {
		this(context, fetchListener, url, true);
	}
	
	public Canteens getCanteens() {
		return canteens;
	}
	
	protected void parseFromJSON(String jsonString)  {
		Canteen[] canteens_arr = gson.fromJson(jsonString, Canteen[].class);
		for(Canteen canteen : canteens_arr) {
			canteens.put(canteen.key, canteen);
		}

		if (canteens_arr.length > 0 && (totalPages == null || currentPage < totalPages)) {
			currentPage++;
			doInBackground(url + "&page=" + currentPage);
		}
	}
	
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		if (visible){
			dialog.setProgress(progress[0]);

			if (totalPages != null) {
				dialog.setTitle(String.format("Fetching [%s/%s]...", currentPage, totalPages));
			} else {
				dialog.setTitle(String.format("Fetching [%s]...", currentPage));
			}
		}
	}

	protected void onPostExecuteFinished() {
		Log.d(TAG, String.format("Fetched %s canteen items", canteens.size()));
		
		// notify that we are done
		fetchListener.onCanteenFetchFinished(this);
	}
}