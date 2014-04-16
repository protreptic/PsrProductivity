package ru.magnat.sfs.bom.task.workday;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.task.generic.TaskGenericEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.bom.track.DocTrackEntity;
import ru.magnat.sfs.bom.track.DocTrackJournal;
import android.content.Context;
import android.location.Location;

@OrmEntityOwner(owner = TaskWorkdayJournal.class)
public final class TaskWorkdayEntity extends
		TaskGenericEntity<TaskWorkdayJournal, TaskGenericEntity<?, ?>> {

	DocTrackEntity _track = null;

	public DocTrackEntity getTrack() {

		return _track;
	}

	public DocTrackEntity startGpsControl() {
		DocTrackJournal tracks = (DocTrackJournal) Globals.createOrmObject(DocTrackJournal.class);
		tracks.NewEntity();
		_track = tracks.Current();
		_track.setDefaults(Globals.CONTEXT, this);
		tracks.save();
		_track.beginCollect(Globals.CONTEXT);
		return _track;
	}

	public void stopGpsControl() {
		if (_track != null)
			_track.stopCollect();
	}

	public Location getLastLocation(Context context) {
		if (_track == null)
			return null;
		return _track.getLastLocation(context);
	}

	public void invalidateRoute() {
		onInvalidateRoute();
		
	}
	private final Set<IEventListener> _eventInvalidateRouteListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnInvalidateRouteListener(OnInvalidateRoute eventListener) {
		EventListenerSubscriber.setListener(_eventInvalidateRouteListeners,
				eventListener);
	}

	public void addOnInvalidateRouteListener(OnInvalidateRoute eventListener) {
		EventListenerSubscriber.addListener(_eventInvalidateRouteListeners,
				eventListener);
	}

	protected void onInvalidateRoute() {

		for (IEventListener eventListener : _eventInvalidateRouteListeners)
			((OnInvalidateRoute) eventListener).onInvalidateRoute();
	}
	
	
	private final Set<IEventListener> _eventOpenVisitRequestListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnOpenVisitRequestListener(OnOpenVisitRequest eventListener) {
		EventListenerSubscriber.setListener(_eventOpenVisitRequestListeners, eventListener);
	}

	public void addOnOpenVisitRequestListener(OnOpenVisitRequest eventListener) {
		EventListenerSubscriber.addListener(_eventOpenVisitRequestListeners, eventListener);
	}

	protected void onOpenVisitRequest(TaskVisitEntity visit) {
		for (IEventListener eventListener : _eventOpenVisitRequestListeners)
			((OnOpenVisitRequest) eventListener).onOpenVisitRequest(visit);
	}
	
	public void openVisitRequest(TaskVisitEntity visit) {
		onOpenVisitRequest(visit);
	}

}
