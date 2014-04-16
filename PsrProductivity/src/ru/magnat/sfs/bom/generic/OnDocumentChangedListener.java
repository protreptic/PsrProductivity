/**
 * 
 */
package ru.magnat.sfs.bom.generic;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.IEventListener;

/**
 * @author alex_us
 * 
 */
public interface OnDocumentChangedListener {
	public interface OnMarkedListener extends IEventListener {
		public abstract void onMarked(
				@SuppressWarnings("rawtypes") GenericEntity sender,
				Boolean old_value, Boolean new_value);
	}

}
