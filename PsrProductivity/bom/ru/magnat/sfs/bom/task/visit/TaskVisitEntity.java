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
	@OrmEntityField(DisplayName = "�������� �����", isPrimary = 0, fields = "Outlet")
	public RefOutletEntity Outlet;
	@OrmEntityField(DisplayName = "��� ������", isPrimary = 0, fields = "VisitType")
	public int VisitType;
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "Latitude")
	public float Latitude;
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "Longitude")
	public float Longitude;
	@OrmEntityField(DisplayName = "����������", isPrimary = 0, fields = "Distance")
	public float Distance;
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "Goal")
	public String Goal;

	private final Set<IEventListener> _eventVisitTypeChangedListeners = new CopyOnWriteArraySet<IEventListener>();
	
	@Override
	public String toString() {
		return "����� " + DateFormat.format("dd MMMM yyyy", this.TaskBegin) + " " + Outlet.toString() + "(����: " + getTypeString() + ")";
	}

	public String getTypeString() {
		return getTypeString(VisitType);
	}

	static public String getTypeString(int visitType) {
		switch (visitType) {
		case 1:
			return "���������� �������";
		case 2:
			return "������ � �����������";
		case 3:
			return "������ � ������";
		default:
			return "�����";
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
		Log.v(MainActivity.LOG_TAG,(result)?"����� ��������":"����� �� ��������");
		return result;
	}
}
