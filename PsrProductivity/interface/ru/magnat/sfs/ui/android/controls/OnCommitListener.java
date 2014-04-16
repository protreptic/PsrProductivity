package ru.magnat.sfs.ui.android.controls;

import ru.magnat.sfs.bom.IEventListener;

public interface OnCommitListener extends IEventListener {
	//public void onCommit(Object sender, float value);

	public void onCommit(Object sender, Integer value);
}
