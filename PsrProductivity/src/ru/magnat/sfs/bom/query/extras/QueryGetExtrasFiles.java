package ru.magnat.sfs.bom.query.extras;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetExtrasFiles extends QueryGeneric<QueryGetExtrasFilesEntity> {

	static final String query = "SELECT  f.Id, f.Descr,f.FileName,f.Seq " 
			+ "FROM RefMediaFiles f "
;
    final int _monthOffset;
    final long _parentExt;
	public QueryGetExtrasFiles(Context context, int monthOffset,long parentext) {
		super(context, QueryGetExtrasFilesEntity.class, query);
		_monthOffset = monthOffset;
		_parentExt = parentext;
	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("f.ParentExt", _parentExt));
		criteria.add(new SqlCriteria("datediff(month,now(),BeginOfActivity)", _monthOffset,"<="));
		criteria.add(new SqlCriteria("datediff(month,now(),EndOfActivity)", _monthOffset,">="));
		return super.Select(criteria, "ORDER BY f.Seq,f.Descr ASC");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
