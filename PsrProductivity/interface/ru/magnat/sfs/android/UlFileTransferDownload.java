package ru.magnat.sfs.android;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.IEventListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.ianywhere.ultralitejni12.DatabaseManager;
import com.ianywhere.ultralitejni12.FileTransfer;
import com.ianywhere.ultralitejni12.FileTransferProgressData;
import com.ianywhere.ultralitejni12.FileTransferProgressListener;
import com.ianywhere.ultralitejni12.StreamHTTPParms;
import com.ianywhere.ultralitejni12.SyncParms;
import com.ianywhere.ultralitejni12.SyncResult;
import com.ianywhere.ultralitejni12.ULjException;

public class UlFileTransferDownload extends AsyncTask<String, String, String>
		implements FileTransferProgressListener {
	SyncMaster _master;
	String _result = "";
	int _state = -1;
	SyncResult _syncData;
	int _syncState;
	final Context _context;
	FileTransfer _transfer;
	//final String _password = "563241";
	//final String _version = "SFS002";
	//final String _host = "mob1.magnat.ru";
	//final int _port = 2439;
	final String _fileName;
	final String _filePath;
	final String _description;
	final long _totalSize;
	final long _totalReceived;
	final private boolean _keepProgressDialog;
	FileTransferProgressData _progressData;
	protected ProgressDialog _progressDialog;

	public UlFileTransferDownload(Context context, String fileName,
			String filePath, String username, String passkey,String description,long totalSize,long totalReceived, ProgressDialog progressDialog)
			throws ULjException {
		_context = context;
		_fileName = fileName;
		_filePath = filePath;
		_description = description;
		_totalSize = totalSize;
		_keepProgressDialog = (progressDialog!=null);
		_progressDialog = progressDialog;
		_totalReceived = totalReceived;
		_transfer = DatabaseManager.createFileTransferAndroid(_context,
				fileName, SyncParms.HTTP_STREAM, username, 
				MainActivity.sInstance.getResources().getString(R.string.ml_version));
		_transfer.setServerFileName(fileName);
		_transfer.setLocalPath(_filePath);
		_transfer.setAuthenticationParms(passkey);
		_transfer.setPassword(MainActivity.sInstance.getResources().getString(R.string.ml_pwd));
		StreamHTTPParms streamParms = _transfer.getStreamParms();
		streamParms.setHost(MainActivity.sInstance.getResources().getString(R.string.ml_primary_host));
		streamParms.setPort(Integer.parseInt(MainActivity.sInstance.getResources().getString(R.string.ml_port)));
		streamParms.setZlibCompression(true);

	}

	public String getFileName() {
		return _fileName;
	}

	public String getFilePath() {
		return _filePath;
	}




	public void SetMaster(SyncMaster master) {
		_master = master;
	}

	public String getResult() {
		return _result;
	}

	public int getState() {
		return _state;
	}

	@Override
	protected void onPreExecute() {
		if (_progressDialog==null)
			_progressDialog = new ProgressDialog(_context);

	}
	public void destroyProgressDialog(){
		if (_progressDialog.isShowing())
			_progressDialog.dismiss();
	}
	@Override
	protected void onPostExecute(String result) {
		if (!_keepProgressDialog) 
			destroyProgressDialog();
		onJobFinished();

	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		long size = _progressData.getFileSize();
		long transferred = _progressData.getBytesTransferred();
		String message = "Прием файла\n" + _description + "\nПолучено "
				+ String.format("%.3fМБ",transferred/1024f/1024f) + " из "
				+ String.format("%.3fМБ",size/1024f/1024f)
				+ ((size>0)?" ( "+String.format("%.0f",100f*transferred/size)+"%)":"")
				/*+"\nВсего "
				+ String.format("%.3fМБ",(_totalReceived+transferred)/1024f/1024f) + " из "
				+ String.format("%.3fМБ",_totalSize/1024f/1024f)
				+ ((_totalSize>0)?" ( "+String.format("%.0f",100f*(_totalReceived+transferred)/_totalSize)+"%)":"")*/
				;
		if (_keepProgressDialog) {
			_progressDialog.setProgress((int)((_totalReceived+transferred)/1024));
		
		}
		_progressDialog.setMessage(message);
		if (!_progressDialog.isShowing())
			_progressDialog.show();
	}

	public boolean syncProgress(int state, SyncResult data) {

		_syncState = state;
		_syncData = data;
		publishProgress();
		return false;
	}

	@Override
	protected String doInBackground(String... parms) {

		_result = "";

		try {
			//_transfer.uploadFile(this);
			_transfer.downloadFile(this);
			_result = "Ok";
		} catch (ULjException e) {
			_state = 0;
			_result = "Ошибка передачи: " + e.getMessage();

		}
		return _result;

	}

	public boolean fileTransferProgressed(FileTransferProgressData progressData) {
		_progressData = progressData;
		publishProgress();
		return false;
	}

	private final Set<IEventListener> _eventJobFinishedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnJobFinished(OnJobFinishedListener eventListener) {
		EventListenerSubscriber.setListener(_eventJobFinishedListeners,
				eventListener);
	}

	public void addOnJobFinished(OnJobFinishedListener eventListener) {
		EventListenerSubscriber.addListener(_eventJobFinishedListeners,
				eventListener);
	}

	public void onJobFinished() {

		for (IEventListener eventListener : _eventJobFinishedListeners)
			((OnJobFinishedListener) eventListener).onJobFinished(this);
	}

}