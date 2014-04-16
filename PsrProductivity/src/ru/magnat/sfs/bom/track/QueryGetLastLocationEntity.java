package ru.magnat.sfs.bom.track;

import java.util.Date;

import android.location.Location;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetLastLocationEntity extends
		QueryGenericEntity<QueryGetLastLocation> {
	@OrmEntityField(DisplayName = "Время", isPrimary = 0, fields = "SatTime")
	public Date Time;
	@OrmEntityField(DisplayName = "Широта", isPrimary = 0, fields = "Latitude")
	public float Latitude;
	@OrmEntityField(DisplayName = "Долгота", isPrimary = 0, fields = "Longitude")
	public float Longitude;

	public Location getLocation() {
		if (Time == null)
			return null;
		Location loc = new Location("location");
		loc.setLatitude(Latitude);
		loc.setLongitude(Longitude);
		loc.setTime(Time.getTime());
		return loc;
	}
}
