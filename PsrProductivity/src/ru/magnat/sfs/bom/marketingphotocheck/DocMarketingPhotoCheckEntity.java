package ru.magnat.sfs.bom.marketingphotocheck;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureEntity;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnMarketingMeasureChangedListener;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnOutletChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnClosingListener;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.ref.marketingmeasureobject.RefMarketingMeasureObjectEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import android.content.Context;

@OrmEntityOwner(owner = DocMarketingPhotoCheckJournal.class)
public final class DocMarketingPhotoCheckEntity
		extends
		DocGenericEntity<DocMarketingPhotoCheckJournal, DocMarketingPhotoCheckLineEntity> {

	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;
	@OrmEntityField(DisplayName = "Измерение", isPrimary = 0, fields = "MarketingMeasure")
	public RefMarketingMeasureEntity MarketingMeasure;
	@OrmEntityField(DisplayName = "Объект", isPrimary = 0, fields = "MarketingObject")
	public RefMarketingMeasureObjectEntity MarketingObject;
	@OrmEntityField(DisplayName = "Фото", isPrimary = 0, fields = "MarketingPhoto")
	public int MarketingPhotoId;
	@OrmEntityField(DisplayName = "Комментарий", isPrimary = 0, fields = "Notes")
	public String Notes;
	@OrmEntityField(DisplayName = "Автор комментария", isPrimary = 0, fields = "NotesAuthor")
	public String NotesAuthor;
	@OrmEntityField(DisplayName = "Оценка", isPrimary = 0, fields = "Rating")
	public int Rating;
	@Override
	final protected Class<?> getLinesContainer() {
		return DocMarketingPhotoCheckLine.class;
	}

	public RefOutletEntity getOutlet() {
		return Outlet;
	}

	public void setOutlet(RefOutletEntity val) {
		if (Outlet.equals(val))
			return;
		onOutletChanged(Outlet, val);
		Outlet = val;
	}

	public RefMarketingMeasureEntity getMarketingMeasure() {
		return MarketingMeasure;
	}

	public void setMarketingMeasure(RefMarketingMeasureEntity val) {
		if (MarketingMeasure.equals(val))
			return;
		onMarketingMeasureChanged(MarketingMeasure, val);
		MarketingMeasure = val;
	}

	public DocMarketingPhotoCheckLineEntity getLine(Context context,
			RefMarketingMeasureObjectEntity object) {
		return null;
		//DocMarketingPhotoCheckLine lines = (DocMarketingPhotoCheckLine) getLines(context);
		//return (DocMarketingPhotoCheckLineEntity) lines.getLine(context, object);

	}



	// SDV
	public void changePhoto(Context context,
			RefMarketingMeasureObjectEntity object, String oldphoto, String newphoto) {
		
		deletePhoto(context,object,oldphoto);
		addPhoto(context,object,newphoto);
	}
	public void addPhoto(Context context,
			RefMarketingMeasureObjectEntity object, String photo) {
		DocMarketingPhotoCheckLine lines = (DocMarketingPhotoCheckLine) getLines(context);
		DocMarketingPhotoCheckLineEntity entity = (DocMarketingPhotoCheckLineEntity) lines
				.getLine(context, photo);

		if (entity == null) {
			lines.NewEntity();
			entity = lines.Current();
			entity.setFileName(photo);
			lines.save();
		}
		
	}
	public void deletePhoto(Context context,
			RefMarketingMeasureObjectEntity object, String photo) {
		DocMarketingPhotoCheckLine lines = (DocMarketingPhotoCheckLine) getLines(context);
		DocMarketingPhotoCheckLineEntity entity = (DocMarketingPhotoCheckLineEntity) lines
				.getLine(context, photo);

		if (entity != null) {
			lines.delete(entity);
		}
		
	}

	@Override
	public String toString() {
		if (this.MarketingMeasure == null)
			return "Ошибка";
		return this.MarketingMeasure.Descr + " №"
				+ String.format("%03d/%09d", Author.Id, Id);

	}
	@Deprecated
	@Override
	final public void setDefaults(Context context, GenericEntity<?> owner) {
		this.CreateDate = new Date();
		this.IsAccepted = false;
		this.IsMark = false;
		this.Employee = Globals.getEmployee();
	
		
		DocMarketingMeasureEntity masterdoc = (DocMarketingMeasureEntity) owner;
		if (masterdoc != null) {
			this.Author = masterdoc.Author;
			this.MasterDoc = masterdoc;
			this.Outlet = masterdoc.Outlet;
			this.MarketingMeasure = masterdoc.MarketingMeasure;

		}
	}
	final public void setDefaults(Context context, DocMarketingMeasureEntity masterdoc, RefMarketingMeasureObjectEntity object){
		setDefaults(context,  masterdoc);
		this.MarketingObject = object;
	}

	// listeners

	private final Set<IEventListener> _eventOutletChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnOutletChangedListener(OnOutletChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventOutletChangedListeners,
				eventListener);
	}

	public void addOnOutletChangedListener(OnOutletChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventOutletChangedListeners,
				eventListener);
	}

	public void removeOnOutletChangedListener(
			OnOutletChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventOutletChangedListeners,
				eventListener);
	}

	public void onOutletChanged(RefOutletEntity old_value,
			RefOutletEntity new_value) {
		for (IEventListener eventListener : _eventOutletChangedListeners)
			((OnOutletChangedListener) eventListener).onOutletChanged(this,
					old_value, new_value);
	}

	private final Set<IEventListener> _eventMarketingMeasureChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnMarketingMeasureChangedListener(
			OnMarketingMeasureChangedListener eventListener) {
		EventListenerSubscriber.setListener(
				_eventMarketingMeasureChangedListeners, eventListener);
	}

	public void addOnMarketingMeasureChangedListener(
			OnMarketingMeasureChangedListener eventListener) {
		EventListenerSubscriber.addListener(
				_eventMarketingMeasureChangedListeners, eventListener);
	}

	public void removeOnMarketingMeasureChangedListener(
			OnMarketingMeasureChangedListener eventListener) {
		EventListenerSubscriber.removeListener(
				_eventMarketingMeasureChangedListeners, eventListener);
	}

	public void onMarketingMeasureChanged(RefMarketingMeasureEntity old_value,
			RefMarketingMeasureEntity new_value) {
		for (IEventListener eventListener : _eventMarketingMeasureChangedListeners)
			((OnMarketingMeasureChangedListener) eventListener)
					.onMarketingMeasureChanged(this, old_value, new_value);
	}

	private final Set<IEventListener> _eventClosingListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnClosingListener(OnClosingListener eventListener) {
		EventListenerSubscriber.setListener(_eventClosingListeners,
				eventListener);
	}

	public void addOnClosingListener(OnClosingListener eventListener) {
		EventListenerSubscriber.addListener(_eventClosingListeners,
				eventListener);
	}

	public void onClosing() {

		for (IEventListener eventListener : _eventClosingListeners)
			((OnClosingListener) eventListener).onClosing(this);
	}

}