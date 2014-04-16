package ru.magnat.sfs.android;

import java.io.Closeable;
import java.io.IOException;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

public class OnDismissCloseableListener implements OnDismissListener  {
	final Closeable _closeable;
	final OnDismissListener _postHandler;
	public OnDismissCloseableListener(Closeable closeable,OnDismissListener postHandler){
		_closeable = closeable;
		_postHandler = postHandler;
	}
	@Override
	public void onDismiss(DialogInterface dialog) {
		if (_closeable!=null)
			try {
				_closeable.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		if (_postHandler!=null)
			_postHandler.onDismiss(dialog);
	}

}
