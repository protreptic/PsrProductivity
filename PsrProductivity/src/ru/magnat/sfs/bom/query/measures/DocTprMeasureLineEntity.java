package ru.magnat.sfs.bom.query.measures;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericLineEntity;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnLinePhotoChangedListener;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnLineValueChangedListener;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;

@OrmEntityOwner(owner = DocTprMeasureLine.class)
public class DocTprMeasureLineEntity extends DocGenericLineEntity<DocTprMeasureLine> {
	
	@OrmEntityField(DisplayName = "TprSubject", isPrimary = 0, fields = "TprSubject")
	public RefCskuEntity TprSubject;
	
	@OrmEntityField(DisplayName = "PriceReduced", isPrimary = 0, fields = "PriceReduced")
	public Boolean PriceReduced;
	
	@OrmEntityField(DisplayName = "PriceCardPromoted", isPrimary = 0, fields = "PriceCardPromoted")
	public Boolean PriceCardPromoted;

	private final Set<IEventListener> _eventPhotoChangedListeners = new CopyOnWriteArraySet<IEventListener>();
	private final Set<IEventListener> _eventValueChangedListeners = new CopyOnWriteArraySet<IEventListener>();
	
	public void setTprSubject(RefCskuEntity entity) {
		TprSubject = entity;
	}
	
	public RefCskuEntity getTprSubject() {
		return TprSubject;
	}
	
	public void setPriceReduced(Boolean value) {
		PriceReduced = value;
	}
	
	public Boolean getPriceReduced() {
		return PriceReduced;
	}
	
	public void setPriceCardPromoted(Boolean value) {
		PriceCardPromoted = value;
	}
	
	public Boolean getPriceCardPromoted() {
		return PriceCardPromoted;
	}
	
	public void setOnLineValueChangedListener(OnLineValueChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventValueChangedListeners, eventListener);
	}

	public void addOnLineValueChangedListener(OnLineValueChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventValueChangedListeners, eventListener);
	}

	public void removeOnLineValueChangedListener(OnLineValueChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventValueChangedListeners, eventListener);
	}

	public void onLineValueChanged(Float old_value, Float new_value) {
		for (IEventListener eventListener : _eventValueChangedListeners)
			((OnLineValueChangedListener) eventListener).onLineValueChanged(this, old_value, new_value);
	}

	public void setOnLineUnitChangedListener(
			OnLinePhotoChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventPhotoChangedListeners,
				eventListener);
	}

	public void addOnLineUnitChangedListener(
			OnLinePhotoChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventPhotoChangedListeners,
				eventListener);
	}

	public void removeOnLineUnitChangedListener(
			OnLinePhotoChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventPhotoChangedListeners,
				eventListener);
	}

	public void onLinePhotoChanged(String old_value, String new_value) {
		for (IEventListener eventListener : _eventPhotoChangedListeners)
			((OnLinePhotoChangedListener) eventListener).onLinePhotoChanged(
					this, old_value, new_value);
	}

}
