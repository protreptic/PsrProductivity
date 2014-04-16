package ru.magnat.sfs.bom.track;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetLastLocation extends
		QueryGeneric<QueryGetLastLocationEntity> {

	static final String query = "SELECT TOP 1 Id, SatTime, Latitude,Longitude FROM DocTrackLine order by Id desc";

	public QueryGetLastLocation(Context context) {
		super(context, QueryGetLastLocationEntity.class, query);

	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		return super.Select(criteria);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
