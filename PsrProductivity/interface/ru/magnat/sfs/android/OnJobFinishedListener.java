package ru.magnat.sfs.android;

import ru.magnat.sfs.bom.IEventListener;

public interface OnJobFinishedListener extends IEventListener {
	void onJobFinished(Object sender);
}
