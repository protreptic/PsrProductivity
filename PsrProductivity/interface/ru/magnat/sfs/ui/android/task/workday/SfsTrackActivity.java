package ru.magnat.sfs.ui.android.task.workday;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.bom.track.DocTrackEntity;
import android.os.Bundle;

import com.google.android.maps.MapActivity;

public final class SfsTrackActivity extends MapActivity {

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.track_view);
		MapFragment map = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		TaskWorkdayEntity wd = Globals.getCurrentWorkday();
		DocTrackEntity track = wd.getTrack();
		if (track == null)
			track = wd.startGpsControl();
		track.addOnPositionSavedListener(map);
	}
}
