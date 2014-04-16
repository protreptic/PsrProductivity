package ru.magnat.sfs.bom.contract;

import java.util.ArrayList;

import org.joda.time.DateTime;

import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.contract.QueryContractorListView;
import ru.magnat.sfs.ui.android.doc.order.DocOrderEntityView;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;

public final class QueryGetContractors extends QueryGeneric<QueryGetContractorsEntity> {

	private static final String query = 
			" select " +
				" a.Id, " +
				" a.IsMark as draft, " +
				" a.ContractorLegalName as contractor_legal_name, " +
				" a.LegalAddress as contractor_address, " +
				" a.CreateDate as create_date, " +
				" a.IsAccepted as is_accepted, " +
				" a.ApprovedComment as comment, " +
				" a.Approved as approved_status " +
			" from " +
		    	" DocContractJournal as a ";
	
	private final RefEmployeeEntity mEmployeeEntity; 
	private Boolean[] mFilter;
	
	public QueryGetContractors(Context context, final Long outletId, final DateTime createDate, Boolean[] filter) {
		super(context, QueryGetContractorsEntity.class, query);
		mEmployeeEntity = Globals.getEmployee();
		mFilter = filter;
	}
	
	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		if (mEmployeeEntity != null) {
			criteria.add(new SqlCriteria("a.PsrId", mEmployeeEntity.Id));
		}

		String query = " and (";
		for (int i = 0; i < mFilter.length; i++) {
			if (mFilter[i]) {
				query += " a.Approved = " + (i + 1) + " ";
				query += " or ";
			}
		}
		query += " 1 = 0) ";
		query += " order by a.CreateDate desc ";
		
		Log.d("", query);
		
		return super.Select(criteria, query);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return new QueryContractorListView.QueryContractorsViewItem(_context, this, lv, Current());
	}  

	public SfsContentView GetEditView(DocOrderJournal journal, DocOrderEntity entity) {
		return new DocOrderEntityView(_context, journal, entity);
	}
	
}
