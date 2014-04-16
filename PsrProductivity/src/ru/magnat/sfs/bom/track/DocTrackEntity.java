package ru.magnat.sfs.bom.track;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.bom.track.OnDocTrackChangedListener.OnPositionSavedListener;
import ru.magnat.sfs.bom.track.OnDocTrackChangedListener.OnTrackBeginListener;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

@OrmEntityOwner(owner = DocTrackJournal.class)
public final class DocTrackEntity extends
		DocGenericEntity<DocTrackJournal, DocTrackLineEntity> implements
		LocationListener {

	@Override
	final protected Class<?> getLinesContainer() {

		return DocTrackLine.class;
	}

	public static final int GPS_FREQUENCY_SECUNDES = 30;
	public static final float GPS_ACCURACY = 101;

	public DocTrackLineEntity getLine(Context context, Integer object) {

		DocTrackLine lines = (DocTrackLine) getLines(context);

		return lines.getLine(context, new DocTrackLineEntity(object));

	}
	protected static Location _location = null;
	public static Location getLastPosition(){
		if (_location==null){
			_location = Globals.getLastLocation();
		}
		return _location;
	}
	public void addPosition(Context context, Date time, double lat, double lon, float batteryLevel) {
		addPosition(context, time, lat, lon, false, batteryLevel);
	}

	public Location getLastLocation(Context context) {

		DocTrackLine lines = (DocTrackLine) getLines(context);

		DocTrackLineEntity line = lines.getLastLine(context);
		lines.close();
		if (line == null)
			return null;
		Location location = new Location("lastLocation");
		location.setLatitude(line.Latitude);
		location.setLongitude(line.Longitude);
		location.setTime(line.SatTime.getTime());

		return location;
	}

	Date _last_save = null;

	public void addPosition(Context context, Date time, double lat, double lon, boolean force, float batteryLevel) {
		if (!force) {
			if (_last_save != null) {
				if ((time.getTime() - _last_save.getTime()) < (GPS_FREQUENCY_SECUNDES * 1000)) {
					return;
				}
			}
		}
		if (_location==null) _location = new Location("lastLocation");
		_location.setLatitude(lat);
		_location.setLongitude(lon);
		_location.setTime(time.getTime());
		
		DocTrackLine lines = (DocTrackLine) getLines(context);

		lines.NewEntity();
		DocTrackLineEntity entity = lines.Current();
		if (entity != null) {
			entity.SystemTime = new Date();
			entity.SatTime = time;
			entity.Latitude = (float) lat;
			entity.Longitude = (float) lon;
			entity.BatteryLevel = batteryLevel;
			if (lines.save()) {
				lines.notifyDataSetChanged();
				firePositionSaved(entity);
				_last_save = time;
			}
		}

	}

	@Override
	public String toString() {
		return String.format("%03d/%09d", Author.Id, Id);

	}

	Context _context = null;

	@Override
	final public void setDefaults(Context context, GenericEntity<?> owner) {
		_context = context;
		this.CreateDate = new Date();
		this.IsAccepted = false;
		this.IsMark = false;
		this.Employee = Globals.getEmployee();
		this.Author = Globals.getUser();
		this.MasterTask = (TaskWorkdayEntity) owner;
	}

	LocationManager locationManager = null;
	String locationProvider = LocationManager.GPS_PROVIDER;

	public void beginCollect(Context context) {
		//SFSActivity.sInstance.waitGps();
		if (locationManager == null) {
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			locationProvider = locationManager.getBestProvider(criteria, true);
		}
		if (locationManager != null) {

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			locationProvider = locationManager.getBestProvider(criteria, true);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_FREQUENCY_SECUNDES * 1000, 1, this);
		}

		fireTrackBegin();
	}

	public void stopCollect() {

		if (locationManager == null)
			return;
		locationManager.removeUpdates(this);
	}

	// listeners

	private final Set<IEventListener> _eventPositionSavedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnPositionSavedListener(OnPositionSavedListener eventListener) {
		EventListenerSubscriber.setListener(_eventPositionSavedListeners,
				eventListener);
	}

	public void addOnPositionSavedListener(OnPositionSavedListener eventListener) {
		EventListenerSubscriber.addListener(_eventPositionSavedListeners,
				eventListener);
	}

	public void firePositionSaved(DocTrackLineEntity position) {

		for (IEventListener eventListener : _eventPositionSavedListeners)
			((OnPositionSavedListener) eventListener).onPositionSaved(this,
					position);
	}

	private final Set<IEventListener> _eventTrackBeginListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnTrackBeginListener(OnTrackBeginListener eventListener) {
		EventListenerSubscriber.setListener(_eventTrackBeginListeners,
				eventListener);
	}

	public void addOnTrackBeginListener(OnTrackBeginListener eventListener) {
		EventListenerSubscriber.addListener(_eventTrackBeginListeners,
				eventListener);
	}

	public void fireTrackBegin() {

		for (IEventListener eventListener : _eventTrackBeginListeners)
			((OnTrackBeginListener) eventListener).onTrackBegin(this);
	}

	public void onLocationChanged(Location location) {
		if (location.hasAccuracy())
			if (location.getAccuracy() < GPS_ACCURACY) {
				addPosition(_context, new Date(location.getTime()), location.getLatitude(), location.getLongitude(), MainActivity.getInstance().getBatteryLevel());
			}
	}  

	public void onStatusChanged(String provider, int status, Bundle extras) {}

	Boolean _requestGps = false;

	public void onProviderEnabled(String provider) {
		_requestGps = false;
	}

	public void onProviderDisabled(String provider) {
		if (!_requestGps) {
			//SFSActivity.sInstance.waitGps();
			_requestGps = true;
		}

	}

}
