package ru.magnat.sfs.bom.query.measures;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public final class QueryGetTprMeasurePickList extends QueryGeneric {
	private final static String QUERY = 
		" select distinct " +
			" c.Id, " +
			" c.Descr " +
		" from " +
			" RegTprDiscountByOutlet as a " + 
			" inner join RefCsku as b on " +
				" a.Csku = b.Id " +
			" inner join RefCsku as c on " +
				" b.Parent = c.Id ";
	
	private DocTprMeasureEntity mDocTprMeasureEntity;
	private RefOutletEntity mOutletEntity;
	private TprValueChangeListener mTprValueChangeListener;
	
	@SuppressWarnings("unchecked")
	public QueryGetTprMeasurePickList(Context context, DocTprMeasureEntity entity, TprValueChangeListener listener) {
		super(context, QueryGetTprMeasurePickListEntity.class, QUERY);
		
		mDocTprMeasureEntity = entity;
		mOutletEntity = entity.Outlet;
		mTprValueChangeListener = listener;
	}

	public QueryGetTprMeasurePickListEntity Current() {
		return (QueryGetTprMeasurePickListEntity) super.Current();
	}

	@SuppressWarnings("unchecked")
	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria(" a.Outlet ", mOutletEntity.Id));
		criteria.add(new SqlCriteria(" datediff(day, a.BeginOfAction, now()) ", 1000, " <= "));
		criteria.add(new SqlCriteria(" datediff(day, now(), a.EndOfAction) ", 1000, " <= "));
		return super.Select(criteria, " order by c.Descr ");
	}
	
	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return new DocTprMeasurePickListItemView(_context, this, lv, mDocTprMeasureEntity, mTprValueChangeListener);
	}
	
	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
