package ru.magnat.sfs.bom.query.getRoute;

import java.util.ArrayList;

import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetRoute extends QueryGeneric<QueryGetRouteEntity> {

	static final String query = "SELECT l.Id, SatTime, Longitude, Latitude from DocTrackLine  l inner join DocTrackJournal j on l.MasterDocId=j.Id and l.MasterDocAuthor=j.Author ";

	public QueryGetRoute(Context context) {
		super(context, QueryGetRouteEntity.class, query);

	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("j.MasterTask", Globals
				.getCurrentWorkday().Id));
		return super.Select(criteria, "ORDER BY l.Id ASC");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
