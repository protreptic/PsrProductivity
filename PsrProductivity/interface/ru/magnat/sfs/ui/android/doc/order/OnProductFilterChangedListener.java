package ru.magnat.sfs.ui.android.doc.order;

import ru.magnat.sfs.bom.IEventListener;

public interface OnProductFilterChangedListener extends IEventListener {
	public void onProductFilterChanged(Object sender, Long id);
}
