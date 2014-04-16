package ru.magnat.sfs.bom.track;

import java.util.Date;

import android.location.Location;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.generic.DocGenericLineEntity;

@OrmEntityOwner(owner = DocTrackLine.class)
public class DocTrackLineEntity extends DocGenericLineEntity<DocTrackLine> {
	public DocTrackLineEntity() {
	}

	public DocTrackLineEntity(Integer object) {
		Id = object;
	}

	@OrmEntityField(DisplayName = "Время системы", isPrimary = 0, fields = "SystemTime")
	public Date SystemTime;
	@OrmEntityField(DisplayName = "Время SAT", isPrimary = 0, fields = "SatTime")
	public Date SatTime;
	@OrmEntityField(DisplayName = "Широта", isPrimary = 0, fields = "Latitude")
	public float Latitude;
	@OrmEntityField(DisplayName = "Долгота", isPrimary = 0, fields = "Longitude")
	public float Longitude;
	@OrmEntityField(DisplayName = "УровеньБатареи", isPrimary = 0, fields = "BatteryLevel")
	public float BatteryLevel;
	
	private Location _location = new Location("routepoint");

	public Location getLocation() {
		try {
			_location.setTime(SatTime.getTime());
			_location.setLatitude(Latitude);
			_location.setLongitude(Longitude);
			return _location;
		} catch (Exception e) {
		}
		return null;

	}

}
