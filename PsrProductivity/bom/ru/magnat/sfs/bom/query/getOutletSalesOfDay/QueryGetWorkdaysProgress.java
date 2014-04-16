package ru.magnat.sfs.bom.query.getOutletSalesOfDay;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetWorkdaysProgress extends
		QueryGeneric<QueryGetWorkdaysProgressEntity> {

	static final String query = "select distinct" 
 + " datepart(day,StartDate) as Id "
   +",cast(StartDate as date) as vis_date"
 + " , (case when datediff(day,StartDate,now())>-1 then 1 else 0 end) as inpast "
 + " from TaskVisitJournal ";
	final Integer _todate;
	final Integer _frommonth;
	final String _compare;

	public QueryGetWorkdaysProgress(Context context, Integer frommonth, Integer todate, String compare) {
		super(context, QueryGetWorkdaysProgressEntity.class, query);
		_todate = todate;
		_compare = compare;
		_frommonth = frommonth;

	}

	public Boolean Select() {

		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("datediff(month,StartDate,now())", _frommonth,"<="));
		criteria.add(new SqlCriteria("datediff(month,StartDate,now())", 0,">="));
		criteria.add(new SqlCriteria("datepart(day,StartDate)", _todate,_compare));
		return super.Select(criteria);
	}

	public Float execute() {
		
		Float total = 0f;
		Float inpast = 0f;
		if (this.Select()) {
			while (this.Next()) {
				total++;
				if (this.Current().inpast>0)
					inpast++;
				
			}

		}
		this.close();
		if (total>0)
			return inpast/total;
		return 0f;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
