package ru.magnat.sfs.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.os.AsyncTask;
import ru.magnat.sfs.android.Log;

public class CheckHostAvailableAsyncTask extends AsyncTask<String, Void, Boolean> {
	private String mHost;
	private String mPort;
	private Socket mSocket;
	private SocketAddress mSocketAddress;
	private int mConnectionTimeout;
	private boolean mResult;
	
	@Override
	protected Boolean doInBackground(String... arg0) {
		try {
			this.mHost = arg0[0];
			this.mPort = arg0[1];
			this.mSocketAddress = new InetSocketAddress(InetAddress.getByName(this.mHost), Integer.valueOf(this.mPort));
			this.mConnectionTimeout = 3500;
			this.mSocket = new Socket();
	    	this.mSocket.connect(this.mSocketAddress, this.mConnectionTimeout);
	    	if (this.mSocket.isConnected()) {
	    		this.mResult = true;
	    	} else {
	    		this.mResult = false;
	    	}
	    	this.mSocket.close();
    	} catch(Exception e) {
    		this.mResult = false;
    		Log.d("", "disconnected", e);
    	} 
		return this.mResult;
	}
}