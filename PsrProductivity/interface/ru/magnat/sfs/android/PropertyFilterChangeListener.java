package ru.magnat.sfs.android;

import android.content.Context;
import ru.magnat.sfs.bom.IEventListener;

public interface PropertyFilterChangeListener extends IEventListener {

	public void onPropertyFilterChangeReceived(Context context, boolean[] filter);
}
