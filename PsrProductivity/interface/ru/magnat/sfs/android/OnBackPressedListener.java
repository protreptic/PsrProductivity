package ru.magnat.sfs.android;

import ru.magnat.sfs.bom.IEventListener;

public interface OnBackPressedListener extends IEventListener {
	abstract public Boolean onBackPressed();
}
