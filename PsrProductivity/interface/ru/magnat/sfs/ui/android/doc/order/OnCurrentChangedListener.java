package ru.magnat.sfs.ui.android.doc.order;

import ru.magnat.sfs.bom.IEventListener;

public interface OnCurrentChangedListener extends IEventListener {
	public void onCurrentChanged(Object sender, Object current, String orderkey);
}
