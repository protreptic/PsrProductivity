package ru.magnat.sfs.bom.query.getRoute;

import java.util.Date;

import android.location.Location;

import com.google.android.maps.GeoPoint;

import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetRouteEntity extends
		QueryGenericEntity<QueryGetRoute> {
	@OrmEntityField(DisplayName = "Время", isPrimary = 0, fields = "SatTime")
	public Date SatTime;
	@OrmEntityField(DisplayName = "Долгота", isPrimary = 0, fields = "Longitude")
	public float Longitude;
	@OrmEntityField(DisplayName = "Широта", isPrimary = 0, fields = "Latitude")
	public float Latitude;

	private Location _location = new Location("routepoint");

	public GeoPoint getGeoPoint() {
		
		return Utils.getGeoPoint(Latitude, Longitude);
	}

	public Location getLocation() {
		_location.setTime(SatTime.getTime());
		_location.setLatitude(Latitude);
		_location.setLongitude(Longitude);
		return _location;
	}
}
