package ru.magnat.sfs.bom.marketingmeasure;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnMarketingMeasureChangedListener;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnOutletChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnClosingListener;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.ref.marketingmeasureobject.RefMarketingMeasureObjectEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import android.content.Context;

@OrmEntityOwner(owner = DocMarketingMeasureJournal.class)
public final class DocMarketingMeasureEntity extends DocGenericEntity<DocMarketingMeasureJournal, DocMarketingMeasureLineEntity> {

	private final Set<IEventListener> _eventOutletChangedListeners = new CopyOnWriteArraySet<IEventListener>();
	
	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;
	
	@OrmEntityField(DisplayName = "Измерение", isPrimary = 0, fields = "MarketingMeasure")
	public RefMarketingMeasureEntity MarketingMeasure;

	@Override
	final protected Class<?> getLinesContainer() {
		return DocMarketingMeasureLine.class;
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

	public void setMarketingMeasure(RefMarketingMeasureEntity entity) {
		if (MarketingMeasure.equals(entity)) {
			return;
		}
		onMarketingMeasureChanged(MarketingMeasure, entity);
		MarketingMeasure = entity;
	}

	public DocMarketingMeasureLineEntity getLine(Context context, RefMarketingMeasureObjectEntity object) {
		DocMarketingMeasureLine lines = (DocMarketingMeasureLine) getLines(context);
		return (DocMarketingMeasureLineEntity) lines.getLine(context, object);
	}
	public DocMarketingMeasureLineEntity getLine(Context context,
			long id) {
		DocMarketingMeasureLine lines = (DocMarketingMeasureLine) getLines(context);
		return (DocMarketingMeasureLineEntity) lines.getLine(context, id);
	}
	public void changeValue(Context context, long objectId, Float value) {
		DocMarketingMeasureLine lines = (DocMarketingMeasureLine) getLines(context);
		DocMarketingMeasureLineEntity entity = (DocMarketingMeasureLineEntity) lines.getLine(context, objectId);
		if (value == null) {
			if (entity != null) {
				lines.delete(entity);
			}
		} else {
			if (entity == null) {
				lines.NewEntity();
				entity = lines.Current();
				entity.setMarketingObject(objectId);
			}
			entity.setValue(value);
			lines.save();
		}
		lines.close();
	}

	public void changePhoto(Context context, RefMarketingMeasureObjectEntity object, String[] photos) {
		if (object != null){
			changePhoto(context, object.Id, photos);
		}
	}
	
	public void changePhoto(Context context, long objectId, String[] photos) {
		DocMarketingMeasureLine lines = (DocMarketingMeasureLine) getLines(context);
		DocMarketingMeasureLineEntity entity = (DocMarketingMeasureLineEntity) lines.getLine(context, objectId);

		if (entity == null) {
			lines.NewEntity();
			entity = lines.Current();
			entity.setMarketingObject(objectId);
		}
		entity.setPhoto(Utils.joinString(photos));
		lines.save();
		lines.close();
	}

	@Override
	final public void setDefaults(Context context, GenericEntity<?> owner) {
		this.CreateDate = new Date();
		this.IsAccepted = false;
		this.IsMark = false;
		this.Employee = Globals.getEmployee();
		TaskVisitEntity visit = (TaskVisitEntity) owner;
		if (visit != null) {
			this.Author = visit.Author;
			this.MasterTask = visit;
			this.Outlet = visit.Outlet;
			this.MarketingMeasure = Globals.getDefaultMarketingMeasure(this.Outlet);
		}
	}

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