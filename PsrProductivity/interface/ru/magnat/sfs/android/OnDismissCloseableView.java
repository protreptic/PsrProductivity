package ru.magnat.sfs.android;

import java.io.Closeable;
import java.io.IOException;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

class OnDismissCloseableView implements  OnDismissListener
{
	final Closeable _closeable;
	public OnDismissCloseableView(Closeable closeable){
		_closeable = closeable;
	}
	@Override
	public void onDismiss(DialogInterface arg0) {
		try {
			_closeable.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
}
