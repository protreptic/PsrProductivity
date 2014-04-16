package ru.magnat.sfs.bom.query.measures;

import android.content.Context;

public interface TprValueChangeListener {
	public enum TprType {
		PRICE_REDUCED,
		PRICE_LABEL_PRESENTED
	}
	
	public void onTprValueChange(Context context, TprType type, Integer productId, Boolean value);
	public void onTprPhoto(Context context, Integer productId);
}
