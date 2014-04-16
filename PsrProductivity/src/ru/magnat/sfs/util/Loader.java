package ru.magnat.sfs.util;

import ru.magnat.sfs.android.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class Loader extends AsyncTask<Object, Object, Object> {

	private ProgressDialog mProgressDialog;
	
	public Loader(Context context) {
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage(context.getResources().getString(R.string.data_loading));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
	}
	
	@Override
	protected void onPreExecute() {
		mProgressDialog.show();
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		return null;
	}
	
	@Override
	protected void onPostExecute(Object result) {
		mProgressDialog.dismiss();
		mProgressDialog = null;
	}

}
