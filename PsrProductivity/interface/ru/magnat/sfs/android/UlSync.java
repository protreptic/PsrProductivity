package ru.magnat.sfs.android;


import java.util.ArrayList;
import java.util.List;

import ru.magnat.sfs.bom.InternalStorage;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;





import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.StreamHTTPParms;
import com.ianywhere.ultralitejni12.SyncObserver;
import com.ianywhere.ultralitejni12.SyncParms;
import com.ianywhere.ultralitejni12.SyncResult;
import com.ianywhere.ultralitejni12.ULjException;

public abstract class UlSync extends AsyncTask<String, Void, Void> implements SyncObserver {
	
	protected Context mContext;
	
	public interface SynchronizationObserver {
		public void onSyncComplete();
	}
	
	private List<SynchronizationObserver> mSynchronizationObservers = new ArrayList<SynchronizationObserver>();
	
	public void addSynchronizationObserver(SynchronizationObserver observer) {
		if (observer != null)
			mSynchronizationObservers.add(observer);
	}
	
	public void removeSynchronizationObserver(SynchronizationObserver observer) {
		if (observer != null)
			mSynchronizationObservers.remove(observer);
	}
	
	private void notifySynchronizationObservers() {
		for (SynchronizationObserver observer : mSynchronizationObservers) {
			observer.onSyncComplete();
		}
	}
	
	protected SyncMaster mSyncMaster;
	protected ProgressDialog mProgressDialog;
	
	protected String mResult = "";

	private int mSyncState;
	protected SyncResult mSyncResult;
	
	private Connection mConnection;
	private SyncParms mSyncParms;
	
	private String mVersion;
	private String mHost;
	private Integer mPort;
	private String mUser;
	private String mPassword;
	private String mPasskey;
	private String mPublication;
	
	private Resources mResources;
	
	public UlSync(Context context) {
		mContext = context;
		mResources = mContext.getResources();
	}
	
	public void setSyncMaster(SyncMaster master) {
		mSyncMaster = master;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		
		mConnection = InternalStorage.getConnection();
	}

	public boolean syncProgress(int syncState, SyncResult syncResult) {
		mSyncState = syncState;
		mSyncResult = syncResult;
		mSyncResult.getStreamErrorCode();
		publishProgress();
		
		return false;
	}
	
	@Override
	protected void onProgressUpdate(Void... progress) {
		switch (mSyncState) {
			case SyncObserver.States.STARTING: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_starting));
			} break; 
			case SyncObserver.States.CONNECTING: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_connecting));
			} break;
			case SyncObserver.States.COMMITTING_DOWNLOAD: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_commiting_download));
			} break;
			case SyncObserver.States.DISCONNECTING: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_disconnecting));
			} break;
			case SyncObserver.States.DONE: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_done));
				mResult = "Ok";
			} break;
			case SyncObserver.States.ERROR: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_error));
				mResult = "Ошибка обмена";
				Log.d("", "" + mSyncResult.getStreamErrorCode() + " " + mSyncResult.getStreamErrorMessage());
			} break;
			case SyncObserver.States.FINISHING_UPLOAD: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_finishing_upload));
			} break;
			case SyncObserver.States.RECEIVING_DATA: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_receiving_data));
			} break;
			case SyncObserver.States.RECEIVING_TABLE: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_receiving_table));
			} break;
			case SyncObserver.States.RECEIVING_UPLOAD_ACK: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_receiving_upload_ack));
			} break;
			case SyncObserver.States.ROLLING_BACK_DOWNLOAD: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_rolling_back_download));
			} break;
			case SyncObserver.States.SENDING_DATA: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_sending_data));
			} break;
			case SyncObserver.States.SENDING_DOWNLOAD_ACK: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_sending_download_ack));
			} break;
			case SyncObserver.States.SENDING_HEADER: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_sending_header));
			} break;
			case SyncObserver.States.SENDING_TABLE: {
				mProgressDialog.setMessage(mResources.getString(R.string.sync_message_state_sending_table));
			} break;
			default: {
				throw new RuntimeException("");
			}
		}
	}

	@Override
	protected Void doInBackground(String... params) {
		mVersion = params[0];
		mHost = params[1];
		mPort = Integer.parseInt(params[2]);
		mUser = ((params[3] != null) ? (params[3]) : ("0"));
		mPassword = params[4];
		mPublication = ((params[5] != null) ? (params[5]) : (""));
		mPasskey = params[6];

		try {
			
			if (mUser == null || mVersion == null) {
				mResult = "Ошибка обмена: не задан пользователь или версия";
				return null;
			}
			
			if (mConnection == null) {
				mResult = "Ошибка обмена: нет соединения с базой";
				return null;
			}
			
			mSyncParms = mConnection.createSyncParms(mUser, mVersion);
			mSyncParms.setPassword(mPassword);
			mSyncParms.setAuthenticationParms(mPasskey);
			mSyncParms.setSyncObserver(this);
			mSyncParms.setPublications(mPublication);
			mSyncParms.setLivenessTimeout(120);
			mSyncParms.setAcknowledgeDownload(true);
			
			StreamHTTPParms streamHttpParms = mSyncParms.getStreamParms();
			streamHttpParms.setPort(mPort);
			streamHttpParms.setHost(mHost);
			streamHttpParms.setZlibCompression(true);
			
			mConnection.synchronize(mSyncParms);
		} catch (ULjException e) {
			mResult = "Ошибка обмена: " + e.getMessage();
			return null;
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		mProgressDialog.dismiss();

		ActivityPostAction();
		
		// Сообщить наблюдателям о завершении синхронизации
		notifySynchronizationObservers();
	}
	
	protected void ActivityPostAction() {
		mSyncMaster.saveSyncResult(true, mResult);
	}
	
}