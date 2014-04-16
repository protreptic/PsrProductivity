package ru.magnat.sfs.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class LongTaskExecutor extends AsyncTask<Integer, Integer, Integer> {
	
	private Context mContext;
	private ProgressDialog mProgressDialog;

	protected Integer _action = 0;
	
	/**
	 * 
	 */
	public LongTaskExecutor(Context context) {
		mContext = context;
		
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage("Пожалуйста, подождите...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	
	@Override
	protected void onPreExecute() {
		mProgressDialog.show();
	}
	
	@Override
	protected Integer doInBackground(Integer... params) {
		_action = params[0];
		
		return null;
	}

	@Override
	protected void onPostExecute(Integer result) {
		mProgressDialog.dismiss();
	}

}
