package ru.magnat.sfs.bom.marketingmeasure;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericLineEntity;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.*;
import ru.magnat.sfs.bom.ref.marketingmeasureobject.RefMarketingMeasureObject;
import ru.magnat.sfs.bom.ref.marketingmeasureobject.RefMarketingMeasureObjectEntity;

@OrmEntityOwner(owner = DocMarketingMeasureLine.class)
public class DocMarketingMeasureLineEntity extends
		DocGenericLineEntity<DocMarketingMeasureLine> {
	@OrmEntityField(DisplayName = "Объект", isPrimary = 0, fields = "MarketingObject")
	public RefMarketingMeasureObjectEntity MarketingObject;
	@OrmEntityField(DisplayName = "Значение", isPrimary = 0, fields = "MarketingValue")
	public float MarketingValue;
	@OrmEntityField(DisplayName = "Фото", isPrimary = 0, fields = "Photo")
	public String Photo;

	public RefMarketingMeasureObjectEntity getMarketingObject() {
		return MarketingObject;
	}

	public void setMarketingObject(RefMarketingMeasureObjectEntity val) {
		MarketingObject = val;
	}
	public void setMarketingObject(long objectId) {
		RefMarketingMeasureObject cat = new RefMarketingMeasureObject(MainActivity.getInstance());
		RefMarketingMeasureObjectEntity entity = cat.FindById(objectId);
		if (entity!=null)
			setMarketingObject((RefMarketingMeasureObjectEntity) entity.clone());
		cat.close();
	}
	
	public Float getValue() {

		return MarketingValue;
	}

	public String getStringValue(int measureKind) {
		return getStringValue(measureKind, this.MarketingValue);
	}

	public static String getStringValue(int measureKind, Float value) {
		if (value == null)
			return "";
		switch (measureKind) {
		case 2:
			return String.valueOf(value) + "%";
		case 3:
		case 4:
			int intvalue = Math.round((Float) value);
			switch (intvalue) {
			case -1:
				return "Нет";
			case 1:
				return "Да";
			default:
				return "Н/Д";
			}
		default:
			return String.valueOf(value);
		}
	}

	public void setValue(Float val) {
		Float old = getValue();
		MarketingValue = val;
		onLineValueChanged(old, MarketingValue);
	}

	public String getPhoto() {
		return Photo;
	}

	public void setPhoto(String val) {
		String old = getPhoto();
		Photo = val;
		onLinePhotoChanged(old, Photo);
	}

	private final Set<IEventListener> _eventValueChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnLineValueChangedListener(
			OnLineValueChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventValueChangedListeners,
				eventListener);
	}

	public void addOnLineValueChangedListener(
			OnLineValueChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventValueChangedListeners,
				eventListener);
	}

	public void removeOnLineValueChangedListener(
			OnLineValueChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventValueChangedListeners,
				eventListener);
	}

	public void onLineValueChanged(Float old_value, Float new_value) {
		for (IEventListener eventListener : _eventValueChangedListeners)
			((OnLineValueChangedListener) eventListener).onLineValueChanged(
					this, old_value, new_value);
	}

	private final Set<IEventListener> _eventPhotoChangedListeners = new CopyOnWriteArraySet<IEventListener>();

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
