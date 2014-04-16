package ru.magnat.sfs.bom.query.order;

import ru.magnat.sfs.bom.IEventListener;

public interface OnOrderPickListSelectedListener extends IEventListener {
	public void onOrderPickListSelected(Boolean result);
}
