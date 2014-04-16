package ru.magnat.sfs.bom.query.order;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.order.DocOrderEntityView;
import ru.magnat.sfs.ui.android.doc.order.QueryOrderListView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetOrders extends QueryGeneric<QueryGetOrdersEntity> {

	private static final String query = 
			" select " +
				" a.Id, " +
				" a.IsMark as draft, " +
				" b.Descr as customer_legal_name, " +
				" b.Address as customer_address, " +
				" a.ShipmentDate as shipment_date, " +
				" a.ShipmentTime as shipment_time, " +
				" a.CreateDate as create_date, " +
				" a.SaveDate as save_date, " +
				" a.AcceptDate as accept_date, " +
				" a.ImportDate as import_date, " +
				" a.ReceiveDate as sent_date, " +
				" a.CancelDate as cancel_date, " +
				" a.Cfr as cfr, " +
				" a.Amount as amount " +
		    " from " +
		    	" DocOrderJournal as a " +
		    	" inner join RefOutlet as b on " +
		    		" a.Outlet = b.Id";
	
	private final Long mOutletId; 
	private final DateTime mCreateDate;
	
	public QueryGetOrders(Context context, final Long outletId, final DateTime createDate) {
		super(context, QueryGetOrdersEntity.class, query);
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
		return new QueryOrderListView.QueryOrderListViewItem(_context, this, lv, Current());
	}

	public SfsContentView GetEditView(DocOrderJournal journal, DocOrderEntity entity) {
		return new DocOrderEntityView(_context, journal, entity);
	}
	
}
