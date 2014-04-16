package ru.magnat.sfs.ui.android;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.IEventListener;

public interface OnGenericListListener {
	public interface OnGlItemShortClickListener extends IEventListener {
		public void onItemShortClick(Object sender, @SuppressWarnings("rawtypes") GenericEntity entity);
	}

	public interface OnGlItemLongClickListener extends IEventListener {
		public void onItemLongClick(Object sender, @SuppressWarnings("rawtypes") GenericEntity entity);
	}
}
