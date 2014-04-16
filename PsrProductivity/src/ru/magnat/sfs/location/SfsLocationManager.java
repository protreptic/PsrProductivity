package ru.magnat.sfs.location;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class SfsLocationManager implements LocationListener {
	
	private static final String LOG_TAG = "SFS_LOCATION_MANAGER"; 
	private static SfsLocationManager sInstance;
	
	private Context mContext;
	private LocationManager mLocationManager;
	
	//private static final int ONE_MINUTE = 1000 * 60 * 1;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final int FIVE_MINUTES = 1000 * 60 * 5;
	private static final float APPROXIMATION = 1500.0f;
		
	private SfsLocationManager(Context context) {
		mContext = context;
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public static SfsLocationManager getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new SfsLocationManager(context);
		}
		
		return sInstance;
	}
	
	public void start() {
		try {
			Log.i(LOG_TAG, "Location manager is started");
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FIVE_MINUTES, APPROXIMATION, this);
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FIVE_MINUTES, APPROXIMATION, this);
		} catch (IllegalArgumentException e) {
			Dialogs.createDialog("", mContext.getResources().getString(R.string.dialog_gps_violation), Command.NO_OP).show(); 
		}
	}
	
	public void stop() {
		Log.i(LOG_TAG, "Location manager is stopped");
		mLocationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		Log.i(LOG_TAG, "Location has changed, latitude " + location.getLatitude() + ", longitude " + location.getLongitude() + ", accuracy " + location.getAccuracy() + ", provider " + location.getProvider());
	}

	private Location mCurrentLocation;
	
	public double[] getCurrentLocation() {
		if (mCurrentLocation == null) {
			return null;
		}
		
		return new double[] { mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude() };
	}
	
	public String getLatitude() {
		return String.valueOf(mCurrentLocation.getLatitude());
	}
	
	public String getLongitude() {
		return String.valueOf(mCurrentLocation.getLongitude());
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		Log.i(LOG_TAG, "Provider " + provider + " unavailable");
		isGpsEnabled();
	}

	public void isGpsEnabled() {
		boolean enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setCancelable(false);
			builder.setIcon(0);
			builder.setMessage(mContext.getResources().getString(R.string.dialog_location_manager_unavailable)); 
			builder.setPositiveButton(mContext.getResources().getString(R.string.btn_activate), new android.content.DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					MainActivity.getInstance().startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), MainActivity.GPS_REQUEST);
				}
				
			});
			builder.show();
		}
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		Log.i(LOG_TAG, "Provider " + provider + " available");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		switch (status) {
			case LocationProvider.OUT_OF_SERVICE: {
				Log.i(LOG_TAG, "Provider " + provider + " OUT_OF_SERVICE");
				onProviderDisabled(provider);
			} break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE: {
				Log.i(LOG_TAG, "Provider " + provider + " TEMPORARILY_UNAVAILABLE");
			} break;
			case LocationProvider.AVAILABLE: {
				Log.i(LOG_TAG, "Provider " + provider + " AVAILABLE");
			} break;
			default: {
				throw new RuntimeException();
			}
		}
	}	
	
	@SuppressWarnings("unused")
	private boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}
	
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
}
