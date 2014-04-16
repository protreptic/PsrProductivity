package ru.magnat.sfs.bom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.MainActivity;
import ru.magnat.sfs.android.SalesForceSolution;
import ru.magnat.sfs.util.Apps;
import ru.magnat.sfs.util.Files;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.ianywhere.ultralitejni12.ConfigFileAndroid;
import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.DatabaseManager;
import com.ianywhere.ultralitejni12.ULjException;

public class InternalStorage {

	private static InternalStorage sInstance;

	private ConfigFileAndroid mConfigFileAndroid;
	private Context mContext;
	private String mApplicationVersion;
	private String mDataBaseFilePath;
	private String mDataBaseFileName;
	private Connection mConnection;

	private InternalStorage() {
		mContext = MainActivity.getInstance();
		mApplicationVersion = Apps.getVersionName(mContext); 
		mDataBaseFilePath = mContext.getFilesDir().getAbsolutePath() + File.separator;
		mDataBaseFileName = mDataBaseFilePath + "sfs_remote-" + mApplicationVersion + ".udb";
		try {
			DatabaseManager.release();
			mConfigFileAndroid = DatabaseManager.createConfigurationFileAndroid(mDataBaseFileName, mContext);
			mConfigFileAndroid.setShadowPaging(true);
			mConfigFileAndroid.setAutocheckpoint(true);
			mConfigFileAndroid.setLazyLoadIndexes(false);
			mConfigFileAndroid.setCacheSize(1024 * 1024 * 2);
			File temp = new File(mDataBaseFileName);
			if (!temp.exists()) {
				if (Files.isMemorySizeAvailableAndroid(1024 * 1024 * 25, false)) {
					BufferedInputStream bis = new BufferedInputStream(mContext.getAssets().open("db/sfs_remote.udb"));//<--BEWARE!!!!! ÍÅ ÌÅÍßÒÜ ÃÅÍÅÐÀÒÎÐ ÁÀÇÛ ÍÀÑÒÐÎÅÍ ÍÀ ÝÒÎÒ ÏÓÒÜ !!!!!
					FileOutputStream fos = new FileOutputStream(new File(mDataBaseFileName));
					int byteCount = 0;
					byte[] buffer = new byte[1024 * 4];
					while ((byteCount = bis.read(buffer)) != -1) {
						fos.write(buffer, 0, byteCount);
					}
					bis.close();
					fos.close();
					bis = null;
					fos = null;
					buffer = null;
					mConnection = DatabaseManager.connect(mConfigFileAndroid);
					Globals.dropSummaryNotes();
				} else {
					Dialogs.createDialog("", mContext.getResources().getString(R.string.dialog_disk_space_unavailable), Command.NO_OP); 
				}
			} else {
				mConnection = DatabaseManager.connect(mConfigFileAndroid);
			}
		} catch (ULjException e) {
			throw new RuntimeException("Îøèáêà ïîäêëþ÷åíèÿ",e);
		} catch (IOException e) {
			throw new RuntimeException("Îøèáêà ââîäà-âûâîäà",e);
		}
	}

	
	
	public static InternalStorage getInstance(Boolean force) {
		if (force || InternalStorage.sInstance == null) {
			InternalStorage.sInstance = new InternalStorage();
		}

		return InternalStorage.sInstance;
	}
	
	public static InternalStorage getInstance() {
		return getInstance(false);
	}

	public static Connection getConnection() {
		return getConnection(false);
	}
	
	public static Connection getConnection(Boolean force) {
		return getInstance(force).mConnection;
	}
	
	public void release() {
		try {
			DatabaseManager.release();
		} catch (ULjException e) {
			e.printStackTrace();
		}
	}

	public static void deleteDatabase() {
		try {
			DatabaseManager.release();
			getConnection(true).dropDatabase();
			sInstance = null;
		} catch (ULjException e) {
			Log.v(MainActivity.LOG_TAG, "Íå óäàëîñü óäàëèòü áàçó: "+e.getMessage());
		}
	}
	
	public void backup() {
		new BackupDataBase().execute();
	}
	
	private class BackupDataBase extends AsyncTask<Void, Void, Void> {
		private ProgressDialog mProgressDialog;
		
		public BackupDataBase() {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage(mContext.getResources().getString(R.string.backup_database));
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(false);
		}
		
		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			Files.copyFile(mDataBaseFileName, SalesForceSolution.EXTERNAL_STORAGE_DIRECTORY);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		
	}
}
