package ru.magnat.sfs.bom.query.distribution;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class QueryGetDistributionListFromHistory extends QueryGeneric<QueryGetDistributionListEntity> {

	static final String query = "SELECT DISTINCT Csku as Id FROM RegSales";
	final long _outlet;
	final int _type;

	public QueryGetDistributionListFromHistory(Context context, long outlet,int type) {
		super(context, QueryGetDistributionListEntity.class, query);
		_outlet = outlet;
		_type = type;
	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Outlet", _outlet));
		criteria.add(new SqlCriteria("datediff(month,Period,now())",3,"<"));
		switch (_type)
		{
		case 0:
			criteria.add(new SqlCriteria("COALESCE(NULLIF(NULLIF(ABC,1),3),-1)", -1)); //POWER CSKU
			break;
		case 1:
			criteria.add(new SqlCriteria("ABC", 1)); //GOLDEN CSKU
			break;
		case 2:
			 //ОПД считаем все
			break;	
		case 3:
			criteria.add(new SqlCriteria("IsDrive", 1));
			break;	
		default:
			return false;	
		
		}
		return super.Select(criteria, "");
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
