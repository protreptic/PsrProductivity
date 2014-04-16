package ru.magnat.sfs.update;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.SalesForceSolution;
import ru.magnat.sfs.util.Apps;
import ru.magnat.sfs.util.Crypto;
import ru.magnat.sfs.util.Network;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;

public class UpdateAsyncTask extends AsyncTask<UpdateParams, Void, Void> {
	
	private Context mContext;
	private static final String UPDATE_PATH = SalesForceSolution.EXTERNAL_STORAGE_DIRECTORY + "/update/"; 

	private String mUrl;
	private String mApkUrl;
	private String mUid;
	private String mVersion;
	private String mNewVersion;

	private String mApkFileName;
	private File mApkFile;
	private String mUpdateHash;
	private UpdateParams mUpdateParams;
	
	private boolean mIsUpdateRequered;
	private ProgressDialog mProgressDialog;
	
	private boolean mIsSilentMode;
	
	public UpdateAsyncTask(Context context, boolean silentMode) {
		mContext = context;
		mIsSilentMode = silentMode;
		
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage("�������� ����������");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
	}
	
	@Override
	protected void onPreExecute() {
		if (!mIsSilentMode) {
			mProgressDialog.show();
		}
	}
	
	protected Void doInBackground(UpdateParams... params) {
		if (!Network.isOnline(mContext)) {
			return null;
		}
		
		mUpdateParams = params[0];
		
		mUrl = mUpdateParams.getUrl();
		mUid = mUpdateParams.getUid();
		mVersion = mUpdateParams.getVersion();
		mApkUrl = mUpdateParams.getUrl2();

		if (checkUpdate()) {
			mApkFileName = getUpdate();
			if (mApkFileName == null) {
				return null;
			}
			mApkFile = new File(mApkFileName);
			if (mApkFile.exists() && !mApkFile.isDirectory()) {
		        mIsUpdateRequered = true;
			}
		}

		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		mProgressDialog.dismiss();
		
		if (!mIsUpdateRequered) {

		} else {
			Dialogs.createDialog("����������", "�������� ����� ������: " + mNewVersion + "\n\n����� ����������� ��������� �����, ����� ��� �������������� ������ ����� ��������\n\n���������� ����� ������ ������?", new Command() {
				
				@Override
				public void execute() {
					Apps.installApk(mContext, mApkFileName);
				}
			}, Command.NO_OP).show();
		}
	}
	
	private Boolean checkUpdate() {
		Version currentVersion = new Version(Apps.getVersionName(mContext));

		for (UpdateEntry entry : parse(getIndex())) {
			if (currentVersion.isMore(entry.getVersion())) {
				Log.v("", "====================================================");
				Log.v("", "Version " + entry.getVersion().toString() + " is available");
				Log.v("", "md5: " + entry.getHash());
				Log.v("", "date: " + entry.getDate());
				Log.v("", "description: " + entry.getDescription());
				Log.v("", "====================================================");
				
				mUpdateHash = entry.getHash();
				mNewVersion = entry.getVersion().toString();
				
				return true;
			}
		}
		
		return false;
	}
	
	private String getIndex() {
		StringBuilder sb = new StringBuilder();
		
		try {
			URL url = new URL(mUrl);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(10000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);	
			connection.setDoOutput(false);	
			
			connection.addRequestProperty("x-sfs-updater-uid", mUid);
			connection.addRequestProperty("x-sfs-updater-version", mVersion);
			
			connection.connect();
			
			int status = connection.getResponseCode();
			switch (status) {
				case HttpURLConnection.HTTP_OK: {
					InputStreamReader isr = new InputStreamReader(connection.getInputStream());
					char[] buffer = new char[1024];
					while (isr.read(buffer) != -1) {
						sb.append(buffer);
					}
			        isr.close();
				} break;
				default: {
					throw new RuntimeException("(" + status + "): �������� ���������� ����� �� ���������");
				}
			}
			
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	private List<UpdateEntry> parse(String index) {
		List<UpdateEntry> entries = new ArrayList<UpdateEntry>();
		
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(new StringReader(index));
			
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (parser.getEventType()) {
					case XmlPullParser.START_TAG: {
						if (parser.getName().equals("row")) {
							String apk = parser.getAttributeValue(null, "apk");
							String description = parser.getAttributeValue(null, "description");
							String version = parser.getAttributeValue(null, "version");
							String hash = parser.getAttributeValue(null, "hash");
							String date = parser.getAttributeValue(null, "date");
							String sizeApk = parser.getAttributeValue(null, "sizeApk");
							
							entries.add(new UpdateEntry(apk, description, new Version(version), hash, date, sizeApk));
						}
					} break;
				}
				
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return entries;
	}
	
	private String getUpdate() {
		String tmp = UPDATE_PATH + "sfs-" + mNewVersion + ".apk";
		String digestString = Crypto.getMd5HashString(tmp);
		File file = new File(UPDATE_PATH + "sfs-" + mNewVersion + ".apk");
		
		if (mUpdateHash.equals(digestString)) {
			return new File(tmp).getAbsolutePath();
		}
		
		try {
			URL url = new URL(mApkUrl);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setReadTimeout(10000);
			connection.setConnectTimeout(15000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.addRequestProperty("x-sfs-updater-uid", mUid);
			connection.addRequestProperty("x-sfs-updater-version", mVersion);
			connection.addRequestProperty("x-sfs-updater-new-version", mNewVersion);
			connection.connect();

			int status = connection.getResponseCode();
			switch (status) {
				case HttpURLConnection.HTTP_OK: {
					String tempFileName = "sfs-" + mNewVersion + ".apk~";
					FileOutputStream tempFos = mContext.openFileOutput(tempFileName, Context.MODE_PRIVATE);

					MessageDigest messageDigest = MessageDigest.getInstance("md5");
					byte[] buffer = new byte[1024 * 64];
					int count = 0;
					
					FileOutputStream fos = new FileOutputStream(file);
					DataInputStream dis = new DataInputStream(new BufferedInputStream(connection.getInputStream()));

			        while ((count = dis.read(buffer)) != -1) {
			        	messageDigest.update(buffer, 0, count);
			        	tempFos.write(buffer, 0, count);
			 	    }

	                if (Crypto.getMd5HashString(messageDigest.digest()).equals(mUpdateHash)) {
	                	FileInputStream fis = new FileInputStream(mContext.getFilesDir().getAbsolutePath()+ "/" + tempFileName);
				        while ((count = fis.read(buffer)) != -1) {
				        	fos.write(buffer, 0, count);
				        }
				        fis.close();
				        new File(mContext.getFilesDir().getAbsolutePath()+ "/" + tempFileName).delete();
	                }
	                
	                tempFos.close();
	                dis.close();
	                fos.close();
	                
	                connection.disconnect();
	                
	                return file.getAbsolutePath();
				}
				default: {
					Log.v("", "���������� ���, ������ " + mVersion + ". �������� ����� �� ���������, ��� ������ " + status + ".");
					
					connection.disconnect();
					
					return null;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mApkFileName = file.getAbsolutePath();
		
		if (mApkFileName == null) {
			return null;
		}
		
		mApkFile = new File(mApkFileName);
		
		if (mApkFile.exists() && !mApkFile.isDirectory()) {
			
		}
		
		return file.getAbsolutePath();
	}
	
}
