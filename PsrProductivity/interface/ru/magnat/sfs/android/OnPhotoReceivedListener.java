package ru.magnat.sfs.android;

import android.content.Context;
import ru.magnat.sfs.bom.IEventListener;

public interface OnPhotoReceivedListener extends IEventListener {
	void onPhotoReceived(Context context, String[] photos);
}
