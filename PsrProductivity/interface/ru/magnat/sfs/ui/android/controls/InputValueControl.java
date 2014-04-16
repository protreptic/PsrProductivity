package ru.magnat.sfs.ui.android.controls;

import ru.magnat.sfs.bom.OnValueChangedListener;

public interface InputValueControl {
	public void setOnValueChangedListener(OnValueChangedListener listener);

	public void addOnValueChangedListener(OnValueChangedListener listener);

	public void removeOnValueChangedListener(OnValueChangedListener listener);

	public void resetValue(Object value);
}
