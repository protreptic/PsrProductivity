package ru.magnat.sfs.bom.query.getTprDiscounts;

import java.util.ArrayList;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

import com.ianywhere.ultralitejni12.ULjException;

public class QueryGetTprDiscounts extends QueryGeneric<QueryGetTprDiscountsEntity> implements IBO<QueryGetTprDiscounts, QueryGetTprDiscountsEntity, RefOutletEntity> {

	final static String _query = 
		" select distinct " +
			" b.Id, " +
			" c.DescriptionSfa as Descr, " +
			" c.PromoType, " +
			" b.StartOfPromo as BeginOfAction, " +
			" b.EndOfPromo as EndOfAction " +
		" from " +
			" RegPromoAvailability a " + 
			" inner join RefPromoDetails b on " +
				" a.Promo = b.Id " +
			" inner join RefPromo c on " +
				" b.ParentExt = c.Id " +
		" where " +
			" a.Outlet = ? " +
			" and b.IsMark = 0" +
			" and a.IsAvailable is not null" +
			" and datediff(day, a.StartOfPromo, now()) <= ? " +
			" and datediff(day, now(), a.EndOfPromo) <= ? " +
		" order by " + 
			" 3, 2 ";
	
	public enum TprDiscountVariant {
		NEW,
		EXPIRING,
		CURRENT
	}

	final RefOutletEntity _outlet;
	final TprDiscountVariant _variant;
	
	public QueryGetTprDiscounts(Context context, RefOutletEntity entity, TprDiscountVariant variant) {
		super(context, QueryGetTprDiscountsEntity.class, _query);
		_outlet = entity;
		_variant = variant;
		
		if (prepareSelect(new ArrayList<SqlCriteria>(), "")) {
			setSelectParameters(new ArrayList<SqlCriteria>());
		}
	}

	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		int param = 0;
		try {
			if (_cursor != null) _cursor.close();
			if (_outlet == null) {
				_select_statement.setNull(++param);
			} else {
				_select_statement.set(++param, _outlet.Id);
			}
			switch (_variant) {
			case NEW: {
				_select_statement.set(++param, (int) 14);
				_select_statement.set(++param, (int) 1000);
			} break;
			case EXPIRING: {
				_select_statement.set(++param, (int) 1000);
				_select_statement.set(++param, (int) 14);
			} break;
			case CURRENT: {
				_select_statement.set(++param, (int) 1000);
				_select_statement.set(++param, (int) 1000);
			} break;
			default:
				break;
			}
		} catch (ULjException e) {
			Log.v(TAG,
			getClass().getName() + ":error on set params: " + e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return new QueryGetTprDiscountsExtendedListItemView(_context, this, lv);
	}

	@Override
	public GenericListView<QueryGetTprDiscounts, QueryGetTprDiscountsEntity, RefOutletEntity> GetSelectView(RefOutletEntity owner) {
		return null;
	}

	@Override
	public SfsContentView GetViewView(QueryGetTprDiscountsEntity entity) {
		return null;
	}

	@Override
	public GenericListView<QueryGetTprDiscounts, QueryGetTprDiscountsEntity, RefOutletEntity> GetListView(RefOutletEntity owner) {
		return null;
	}

	@Override
	public SfsContentView GetEditView(QueryGetTprDiscountsEntity entity) {
		return null;
	}
	
}
