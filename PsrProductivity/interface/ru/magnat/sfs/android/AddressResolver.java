package ru.magnat.sfs.android;

import java.io.IOException;
import java.util.List;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

public class AddressResolver extends AsyncTask<Location, Void, Address> {
	ProgressDialog dialog=null;
	final OnAddressResolvedListener _callback;
	public AddressResolver(OnAddressResolvedListener listener){
		_callback  = listener;
	}
	
	public interface OnAddressResolvedListener {
	        void onAddressResolved(Address value);
	}
	@Override
	protected void onPreExecute(){
		dialog= new ProgressDialog(MainActivity.getInstance());;
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setMessage("Поиск адреса...");
		dialog.show();
	}
    @Override
    protected Address doInBackground(Location... locs) {
    	Geocoder gc = new Geocoder(MainActivity.getInstance());
        Location loc = locs[0];
        
        try {
			List<Address> addresses  = gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			if (addresses.isEmpty()) return null;
			return addresses.get(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
  }

    @Override
    protected void onPostExecute(Address address) {
        if (dialog!=null) dialog.dismiss();
        if (address!=null){
        	if (_callback!=null) _callback.onAddressResolved(address);
        }

    }
}
