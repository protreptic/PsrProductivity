package ru.magnat.sfs.bom.query.order;

import ru.magnat.sfs.bom.IEventListener;

public interface OnPickFilterChangedListener extends IEventListener {
	public void onFilterChanged(PickFilter filter);

}
