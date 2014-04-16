package ru.magnat.sfs.bom.order;

import ru.magnat.sfs.bom.IEventListener;

public interface OnAmountUpdatedListener extends IEventListener {
	public void onAmountUpdated();
}
