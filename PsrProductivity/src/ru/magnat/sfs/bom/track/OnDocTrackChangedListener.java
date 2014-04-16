/**
 * 
 */
package ru.magnat.sfs.bom.track;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.IEventListener;

/**
 * @author alex_us
 * 
 */
@SuppressWarnings("rawtypes")
public interface OnDocTrackChangedListener {
	public interface OnTrackBeginListener extends IEventListener {
		public abstract void onTrackBegin(GenericEntity sender);
	}

	public interface OnTrackEndListener extends IEventListener {
		public abstract void onTrackEnd(GenericEntity sender);
	}

	public interface OnPositionSavedListener extends IEventListener {
		public abstract void onPositionSaved(GenericEntity sender,
				DocTrackLineEntity position);
	}

}
