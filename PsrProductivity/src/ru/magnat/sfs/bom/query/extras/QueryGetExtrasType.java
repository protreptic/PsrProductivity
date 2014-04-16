package ru.magnat.sfs.bom.query.extras;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetExtrasType extends QueryGeneric<QueryGetExtrasTypeEntity> {

	static final String query = 
		" select " +
		" distinct " +
			" t.Id, " +
			" t.Descr, " +
			" t.Seq " + 
		"from " +
			" RefMediaContentType t " + 
			" inner join RefMediaFiles f on " + 
				" t.Id = f.ParentExt";
	
    final int _monthOffset;
	
    public QueryGetExtrasType(Context context, int monthOffset) {
		super(context, QueryGetExtrasTypeEntity.class, query);
		_monthOffset = monthOffset;
	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("datediff(month,now(),BeginOfActivity)", _monthOffset,"<="));
		criteria.add(new SqlCriteria("datediff(month,now(),EndOfActivity)", _monthOffset,">="));
		return super.Select(criteria, "order by t.Seq, t.Descr asc");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
