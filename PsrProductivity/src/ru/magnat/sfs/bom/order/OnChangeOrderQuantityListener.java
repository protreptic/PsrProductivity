package ru.magnat.sfs.bom.order;

import ru.magnat.sfs.bom.IEventListener;

public interface OnChangeOrderQuantityListener extends IEventListener {
	public void onChangeOrderQuantity();
}
