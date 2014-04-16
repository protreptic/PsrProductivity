package ru.magnat.sfs.bom.query.measures;

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
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnMarketingMeasureChangedListener;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnOutletChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnClosingListener;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import android.content.Context;

@OrmEntityOwner(owner = DocTprMeasureJournal.class)
public final class DocTprMeasureEntity extends DocGenericEntity<DocTprMeasureJournal, DocTprMeasureLineEntity> {

	private final Set<IEventListener> _eventMarketingMeasureChangedListeners = new CopyOnWriteArraySet<IEventListener>();
	private final Set<IEventListener> _eventClosingListeners = new CopyOnWriteArraySet<IEventListener>();
	private final Set<IEventListener> _eventOutletChangedListeners = new CopyOnWriteArraySet<IEventListener>();
	
	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;
	
	@Override
	final protected Class<?> getLinesContainer() {
		return DocTprMeasureLine.class;
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

	public DocTprMeasureLineEntity getLine(Context context) {
		return null;
	}

	@Override
	final public void setDefaults(Context context, GenericEntity<?> owner) {
		CreateDate = new Date();
		IsAccepted = false;
		IsMark = false;
		Employee = Globals.getEmployee();
		TaskVisitEntity visit = (TaskVisitEntity) owner;
		if (visit != null) {
			Author = visit.Author;
			MasterTask = visit;
			Outlet = visit.Outlet;
		}
	}

	public void setOnOutletChangedListener(OnOutletChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventOutletChangedListeners, eventListener);
	}

	public void addOnOutletChangedListener(OnOutletChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventOutletChangedListeners, eventListener);
	}

	public void removeOnOutletChangedListener(OnOutletChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventOutletChangedListeners, eventListener);
	}

	public void onOutletChanged(RefOutletEntity oldEntity, RefOutletEntity newEntity) {
		for (IEventListener eventListener : _eventOutletChangedListeners) {
			((OnOutletChangedListener) eventListener).onOutletChanged(this,	oldEntity, newEntity);
		}
	}

	public void setOnMarketingMeasureChangedListener(OnMarketingMeasureChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventMarketingMeasureChangedListeners, eventListener);
	}

	public void addOnMarketingMeasureChangedListener(OnMarketingMeasureChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventMarketingMeasureChangedListeners, eventListener);
	}

	public void removeOnMarketingMeasureChangedListener(OnMarketingMeasureChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_eventMarketingMeasureChangedListeners, eventListener);
	}

	public void onMarketingMeasureChanged(RefMarketingMeasureEntity old_value, RefMarketingMeasureEntity new_value) {
		for (IEventListener eventListener : _eventMarketingMeasureChangedListeners) {
			((OnMarketingMeasureChangedListener) eventListener).onMarketingMeasureChanged(this, old_value, new_value);
		}
	}

	public void setOnClosingListener(OnClosingListener eventListener) {
		EventListenerSubscriber.setListener(_eventClosingListeners, eventListener);
	}

	public void addOnClosingListener(OnClosingListener eventListener) {
		EventListenerSubscriber.addListener(_eventClosingListeners, eventListener);
	}

	public void onClosing() {
		for (IEventListener eventListener : _eventClosingListeners) {
			((OnClosingListener) eventListener).onClosing(this);
		}
	}
}