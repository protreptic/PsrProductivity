package ru.magnat.sfs.bom.query.distribution;

import java.util.ArrayList;

import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import ru.magnat.sfs.android.Log;
import android.widget.ListView;

public final class QueryGetDistributionListFromTodayOrder extends QueryGeneric<QueryGetDistributionListEntity> {

	static final String query = "SELECT DISTINCT p.ParentExt as Id FROM RefProductItem p " +
			" inner join DocOrderLine l on p.Id = l.Item " +
			" inner join DocOrderJournal j on l.MasterDocId  = j.Id and l.MasterDocAuthor = j.Author" +
			" inner join RefOutlet o on j.Outlet = o.Id" +
			" left join RegCskuState s on o.Channel = s.StoreChannel and p.ParentExt = s.Csku";
	final long _outlet;
	final long _current_order;
	final int _type;
	
	final Boolean _only_current;

	public QueryGetDistributionListFromTodayOrder(Context context, long outlet,long current_order, int type,Boolean only_current) {
		super(context, QueryGetDistributionListEntity.class, query);
		_outlet = outlet;
		_type = type;
		_current_order = current_order;
		_only_current = only_current;
	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("j.Id", _current_order,(_only_current)?"=":"<>"));
		criteria.add(new SqlCriteria("Outlet", _outlet));
		criteria.add(new SqlCriteria("datediff(day,CreateDate,now())",0));
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
		Log.v(MainActivity.LOG_TAG,"Начало выбора продаж дня");
		Boolean res =  super.Select(criteria, "");
		Log.v(MainActivity.LOG_TAG,"Конец выбора продаж дня");
		return res;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
