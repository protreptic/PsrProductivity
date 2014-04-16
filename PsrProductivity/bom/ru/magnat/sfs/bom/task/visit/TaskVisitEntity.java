package ru.magnat.sfs.bom.task.visit;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.generic.TaskGenericEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import android.text.format.DateFormat;

@OrmEntityOwner(owner = TaskVisitJournal.class)
public final class TaskVisitEntity extends TaskGenericEntity<TaskVisitJournal, TaskWorkdayEntity> {
	@OrmEntityField(DisplayName = "Торговая точка", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;
	@OrmEntityField(DisplayName = "Тип визита", isPrimary = 0, fields = "VisitType")
	public int VisitType;
	@OrmEntityField(DisplayName = "Широта", isPrimary = 0, fields = "Latitude")
	public float Latitude;
	@OrmEntityField(DisplayName = "Долгота", isPrimary = 0, fields = "Longitude")
	public float Longitude;
	@OrmEntityField(DisplayName = "Расстояние", isPrimary = 0, fields = "Distance")
	public float Distance;
	@OrmEntityField(DisplayName = "Цель", isPrimary = 0, fields = "Goal")
	public String Goal;

	private final Set<IEventListener> _eventVisitTypeChangedListeners = new CopyOnWriteArraySet<IEventListener>();
	
	@Override
	public String toString() {
		return "Визит " + DateFormat.format("dd MMMM yyyy", this.TaskBegin) + " " + Outlet.toString() + "(Цель: " + getTypeString() + ")";
	}

	public String getTypeString() {
		return getTypeString(VisitType);
	}

	static public String getTypeString(int visitType) {
		switch (visitType) {
		case 1:
			return "Финансовые вопросы";
		case 2:
			return "Работа с документами";
		case 3:
			return "Работа с полкой";
		default:
			return "Заказ";
		}
	}

	public void setVisitType(int type) {
		Boolean fired = (VisitType != type);
		VisitType = type;
		if (fired) {
			fireVisitTypeChanged(type);
		}
	}

	public void setOnVisitTypeChangedListener(OnVisitTypeChangedListener eventListener) {
		EventListenerSubscriber.setListener(_eventVisitTypeChangedListeners, eventListener);
	}

	public void addOnVisitTypeChangedListener(OnVisitTypeChangedListener eventListener) {
		EventListenerSubscriber.addListener(_eventVisitTypeChangedListeners, eventListener);
	}

	public void fireVisitTypeChanged(int type) {
		for (IEventListener eventListener : _eventVisitTypeChangedListeners) {
			((OnVisitTypeChangedListener) eventListener).onVisitTypeChanged(this, type);
		}	
	}
	@Override
	public Boolean save() {
	
		Boolean result =  super.save();
		Log.v(MainActivity.LOG_TAG,(result)?"Визит сохранен":"Визит не сохранен");
		return result;
	}
}
