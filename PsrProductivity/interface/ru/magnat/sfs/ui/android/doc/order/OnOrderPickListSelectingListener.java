package ru.magnat.sfs.ui.android.doc.order;

import ru.magnat.sfs.bom.IEventListener;

public interface OnOrderPickListSelectingListener extends IEventListener {
	public void onBeforeSelect();
	public void onSelect(String... values);
	public void onAfterSelect(Boolean result);

}
