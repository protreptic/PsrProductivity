package ru.magnat.sfs.bom.marketingphotocheck;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericLineEntity;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.*;

@OrmEntityOwner(owner = DocMarketingPhotoCheckLine.class)
public class DocMarketingPhotoCheckLineEntity extends
		DocGenericLineEntity<DocMarketingPhotoCheckLine> {
	@OrmEntityField(DisplayName = "Имя файла", isPrimary = 0, fields = "FileName")
	public String FileName;
	@OrmEntityField(DisplayName = "Ссылка", isPrimary = 0, fields = "URL")
	public String URL;
	
	public String getFileName() {
		return FileName;
	}

	public void setFileName(String val) {
		String old = FileName;
		FileName = val;
		onLinePhotoChanged(old,val);
	}

	public String getURL() {

		return URL;
	}

	


	private final Set<IEventListener> _eventPhotoChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnLinePhotoChangedListener(
			OnLinePhotoChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventPhotoChangedListeners,
				eventListener);
	}

	public void addOnLinePhotoChangedListener(
			OnLinePhotoChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventPhotoChangedListeners,
				eventListener);
	}

	public void removeOnLinePhotoChangedListener(
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
