package ru.magnat.sfs.android;

import ru.magnat.sfs.bom.IEventListener;

public interface OnOrderMenuEventListener extends IEventListener {
	public void showOrderInfo();
	public void showPromo();
	public void showTarget();
	public void showFilter();
	public void showProductFilter();
	public void recalcOrder();
	
}
