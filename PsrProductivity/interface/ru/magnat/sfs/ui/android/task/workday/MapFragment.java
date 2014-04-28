package ru.magnat.sfs.ui.android.task.workday;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.query.getRoute.QueryGetRoute;
import ru.magnat.sfs.bom.query.getRoute.QueryGetRouteEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.generic.TaskGeneric.DateVariant;
import ru.magnat.sfs.bom.task.visit.TaskVisitJournal;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.bom.track.DocTrackLineEntity;
import ru.magnat.sfs.bom.track.OnDocTrackChangedListener.OnPositionSavedListener;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import ru.magnat.sfs.android.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class MapFragment extends Fragment implements OnPositionSavedListener {
	private MapView map = null;
	private TaskWorkdayEntity _workday = null;
	private CurrentLocationOverlay _currentOverlay = null;
	private CustomRouteOverlay _routeOverlay = null;
	private MarkersOverlay _visitsOverlay = null;
	private MarkersOverlay _outletsOverlay = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return (new FrameLayout(getActivity()));
	}

	public void setWorkday(TaskWorkdayEntity workday) {
		_workday = workday;
	}

	List<OverlayItem> fillVisitsArray(TaskVisitJournal visits,
			QueryGetRoute route) {
		List<OverlayItem> items = new ArrayList<OverlayItem>();
		Integer i = 0;
		visits.BeforeFirst();
		route.BeforeFirst();
		QueryGetRouteEntity routepoint = null;
		Location location = null;
		route.BeforeFirst();
		while (visits.Next()) {
			Date visitBegin = visits.Current().TaskBegin;
			Date visitEnd = visits.Current().TaskEnd;
			if (visitEnd == null) {
				continue;
			}
			while (route.Next()) {
				routepoint = route.Current();
				location = route.Current().getLocation();
				if (visitBegin.getTime() > routepoint.SatTime.getTime()) {
					continue;
				}
				break;
			}

			if (location != null) {
				items.add(new OverlayItem(Utils
						.getGeoPointFromLocation(location), (++i).toString(),
						visits.Current().toString()));
			}
		}
		return items;
	}

	List<OverlayItem> fillOutletsArray(TaskVisitJournal visits) {
		List<OverlayItem> items = new ArrayList<OverlayItem>();
		Integer i = 0;
		visits.BeforeFirst();

		while (visits.Next()) {
			RefOutletEntity outlet = visits.Current().Outlet;
			if (outlet == null)
				continue;
			Location location = outlet.getLocation();
			if (location == null)
				continue;
			try {
				items.add(new OverlayItem(Utils
						.getGeoPointFromLocation(location), (++i).toString(),
						visits.Current().Outlet.toString()));
			} catch (Exception e) {
				e.getStackTrace();
			}

		}
		return items;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		map = new MapView(getActivity(), "0n7IqmcMRCnDYfxEdmJWdykrbYwOBFa2xylOxIw");
		map.setClickable(true);
		map.getController().setZoom(15);
		map.setBuiltInZoomControls(true);

		Drawable marker = getResources().getDrawable(R.drawable.marker);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		
		Drawable yellowmarker = getResources().getDrawable(R.drawable.marker_yellow);
		yellowmarker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());

		Location currentLocation = Globals.getLastLocation();
		if (currentLocation != null) {
			map.getController().setCenter(Utils.getGeoPointFromLocation(currentLocation));
		}
		
		TaskVisitJournal visits = new TaskVisitJournal(MainActivity.sInstance);
		visits.SetMasterTask(_workday);
		visits.Select(new Date(), new Date(), DateVariant.BEGIN, false);
		QueryGetRoute route = new QueryGetRoute(MainActivity.sInstance);
		route.Select();
		_routeOverlay = new CustomRouteOverlay(route);
		_visitsOverlay = new MarkersOverlay(marker, fillVisitsArray(visits, route));
		_outletsOverlay = new MarkersOverlay(yellowmarker, fillOutletsArray(visits));
		route.close();
		visits.close();
		
		_currentOverlay = new CurrentLocationOverlay(getActivity(), map);
		_currentOverlay.enableMyLocation();
		map.getOverlays().add(_currentOverlay);
		map.getOverlays().add(_routeOverlay);
		map.getOverlays().add(_visitsOverlay);
		map.getOverlays().add(_outletsOverlay);
		((ViewGroup) getView()).addView(map);

	}

	@Override
	public void onResume() {
		super.onResume();

		_currentOverlay.enableCompass();
	}

	@Override
	public void onPause() {
		super.onPause();

		_currentOverlay.disableCompass();
	}

	private class MarkersOverlay extends ItemizedOverlay<OverlayItem> {
		private final List<OverlayItem> items;

		public MarkersOverlay(Drawable marker, List<OverlayItem> markers) {
			super(marker);
			items = markers;

			boundCenterBottom(marker);
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return (items.get(i));
		}

		@Override
		protected boolean onTap(int i) {
			try{
			  Toast.makeText(getActivity(),
                      items.get(i).getSnippet(),
                      Toast.LENGTH_SHORT).show();
			} catch (Exception e){
				Log.v(MainActivity.LOG_TAG,"������ ����������� ������� �� �����: "+e.getMessage());
			}
			return (true);
		}

		@Override
		public int size() {
			return (items.size());
		}
	}

	private class CurrentLocationOverlay extends MyLocationOverlay {

		public CurrentLocationOverlay(Context context, MapView mapView) {
			super(context, mapView);

		}

		@Override
		public void onLocationChanged(Location location) {
			_routeOverlay.setCurrentLocation(location);
			super.onLocationChanged(location);
		}
	}

	private class CustomRouteOverlay extends Overlay {

		final int RED = Globals.getColor(R.color.red);
		final ArrayList<Location> _points;
		static final float GEO_ACCURACY = 100;
		Location _currentLocation = null;

		public CustomRouteOverlay(QueryGetRoute route) {
			super();

			_points = new ArrayList<Location>();
			route.BeforeFirst();
			while (route.Next()) {

				addPoint(((QueryGetRouteEntity) route.Current()).getLocation());
			}
		}

		public void setCurrentLocation(Location location) {
			_currentLocation = location;
		}

		public void addPoint(Location location) {
			if (!_points.isEmpty()) {
				if (_points.get(_points.size() - 1).distanceTo(location) < GEO_ACCURACY)
					return;
			}
			_points.add(location);
		}

		void drawRouteSegment(Canvas canvas, Point a, Point b) {
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(RED);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(2);
			canvas.drawLine((float) a.x, (float) a.y, (float) b.x, (float) b.y,
					paint);
		}

		@Override
		public void draw(Canvas canvas, MapView mapV, boolean shadow) {

			Point a = null;
			Point b = null;

			if (shadow) {
				Projection projection = mapV.getProjection();
				for (Location loc : _points) {
					b = new Point();
					projection.toPixels(Utils.getGeoPointFromLocation(loc), b);
					if (a == null) {
						a = b;
						continue;
					}
					drawRouteSegment(canvas, a, b);
					a = b;
				}
				if (a != null && _currentLocation != null) {
					b = new Point();
					projection.toPixels(
							Utils.getGeoPointFromLocation(_currentLocation), b);
					drawRouteSegment(canvas, a, b);
				}
				super.draw(canvas, mapV, shadow);
			}
		}

	}

	public void onPositionSaved(GenericEntity sender, DocTrackLineEntity position) {
		_routeOverlay.addPoint(position.getLocation());
	}

}