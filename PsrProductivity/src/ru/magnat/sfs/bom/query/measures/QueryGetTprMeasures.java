package ru.magnat.sfs.bom.query.measures;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.order.DocOrderEntityView;
import android.content.Context;
import android.widget.ListView;

public class QueryGetTprMeasures extends QueryGeneric<QueryGetTprMeasureEntity> {

	private static final String query = 
			" select " +
				" a.Id, " +
				" a.IsMark as draft, " +
				" b.Descr as customer_legal_name, " +
				" b.Address as customer_address, " +
				" a.CreateDate as create_date, " +
				" a.IsAccepted as is_accepted " +
		    " from " +
		    	" DocTprMeasureJournal as a " +
		    	" inner join RefOutlet as b on " +
		    		" a.Outlet = b.Id";
	
	private final Long mOutletId; 
	private final DateTime mCreateDate;
	
	public QueryGetTprMeasures(Context context, final Long outletId, final DateTime createDate) {
		super(context, QueryGetTprMeasureEntity.class, query);
		mOutletId = outletId;
		mCreateDate = createDate;
	}
	
	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		if (mOutletId != null) {
			criteria.add(new SqlCriteria("a.Outlet", mOutletId));
		}
		if (mCreateDate != null) {
			criteria.add(new SqlCriteria("datediff(day, a.CreateDate, '" + mCreateDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd")) + "')", 0));
		}
		return super.Select(criteria, "order by a.CreateDate desc");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return new QueryTprMeasureListView.QueryTprMeasureViewItem(_context, this, lv, Current());
	}

	public SfsContentView GetEditView(DocOrderJournal journal, DocOrderEntity entity) {
		return new DocOrderEntityView(_context, journal, entity);
	}

}
