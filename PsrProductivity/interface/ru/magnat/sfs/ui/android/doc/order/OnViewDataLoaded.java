package ru.magnat.sfs.ui.android.doc.order;

import android.view.View;

public abstract class OnViewDataLoaded {
	final View mView;
	public OnViewDataLoaded(View v) {
		mView = v;
	}
	abstract public void onViewDataLoaded();
}
