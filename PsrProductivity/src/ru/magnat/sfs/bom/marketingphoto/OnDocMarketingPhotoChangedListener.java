/**
 * 
 */
package ru.magnat.sfs.bom.marketingphoto;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;

/**
 * @author alex_us
 * 
 */
@SuppressWarnings("rawtypes")
public interface OnDocMarketingPhotoChangedListener {
	public interface OnOutletChangedListener extends IEventListener {
		public abstract void onOutletChanged(GenericEntity sender,
				RefOutletEntity old_value, RefOutletEntity new_value);
	}

	public interface OnMarketingMeasureChangedListener extends IEventListener {
		public abstract void onMarketingMeasureChanged(GenericEntity sender,
				RefMarketingMeasureEntity old_value,
				RefMarketingMeasureEntity new_value);
	}

	public interface OnLineValueChangedListener extends IEventListener {
		public abstract void onLineValueChanged(GenericEntity sender,
				float old_value, float new_value);
	}

	public interface OnLinePhotoChangedListener extends IEventListener {
		public abstract void onLinePhotoChanged(GenericEntity sender,
				String old_value, String new_value);
	}

}
