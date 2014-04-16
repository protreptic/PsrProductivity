package ru.magnat.sfs.bom.query.order;

import java.util.ArrayList;

import ru.magnat.sfs.bom.InternalStorage;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannelEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.order.DocOrderPickListItemView;
import android.content.Context;
import android.widget.ListView;

import com.ianywhere.ultralitejni12.ULjException;

public abstract class CopyOfQueryGetOrderPickList extends
		QueryGeneric<QueryGetOrderPickListEntity> implements PickupInterface {

	final DocOrderEntity _doc;
	final RefEmployeeEntity _employee;
	final RefStoreChannelEntity _channel;
	// фильтры
	RefCskuEntity _csku;

	static final String _query = "SELECT ra.Id as Id "
			+ ", i.id as prodItem "
			+ ", p.Price*(1+(1-pt.IncludeVAT)*i.VAT) as prodPrice "
			+ ", COALESCE(s.ABC,0) as abc "
			+ ", COALESCE(s.IsPriority,0) as isPriority "
			+ ", COALESCE(s.IsDrive,0) as isDrive "
			+ ", 0 as Recommended "
			+ ", COALESCE(w.Quantity,0) as Stock "
			+ ", COALESCE(c.OrderKey,0) as OrderKey "
			+ " FROM RegAssortment ra "
			+ " INNER JOIN RefProductItem i on ra.ProductItem=i.Id "
			+ " INNER JOIN RegPrice p on i.Id = p.ProductItem "
			+ " INNER JOIN RefCsku c on i.ParentExt=c.Id "
			+ " INNER JOIN RegCskuState s on c.Id=s.Csku AND s.ABC = 1"
			+ " and s.StoreChannel=? "
			+ " LEFT JOIN RegOrderHelper h on c.Id=h.Csku "
			+ " and h.Employee=? "
			+ " and h.Outlet=? "
			+ " LEFT JOIN RegStock w on ra.ProductItem = w.ProductItem and w.Warehouse=? "
			+ " INNER JOIN RefPriceType pt on p.PriceType=pt.Id "
			+ " WHERE ra.Assortment=? " + " AND p.PriceType=? "
			+ " AND c.Fullpath LIKE ? " + " AND h.Id IS NULL "
			+ " AND NOT NULLIF(p.Price,0) IS NULL " + " ORDER BY c.OrderKey ";
	final RefOutletEntity _outlet;
	private boolean _prepared = false;

	public CopyOfQueryGetOrderPickList(Context context, DocOrderEntity entity,
			RefCskuEntity productFilter) {

		super(context, QueryGetOrderPickListEntity.class, _query);
		_doc = entity;
		_employee = _doc.Employee;
		_csku = productFilter;
		_outlet = entity.Outlet;
		if (_outlet != null)
			_channel = _outlet.Channel;
		else
			_channel = null;
		if (prepareSelect(null, null))
			setSelectParameters(null);

	}

	@Override
	abstract public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria);

	@Override
	public Boolean prepareSelect(ArrayList<SqlCriteria> criteria,
			String orderFactor) {
		try {

			ChangeContext(OperationContext.FETCHING);
			if (_prepared)
				return true;

			if (_storage == null)
				return false;
			if (_cursor != null)
				this.close();

			if (_select_statement != null)
				try {
					_select_statement.close();
				} catch (ULjException e) {
				}

			this._select_statement = InternalStorage.getConnection()
					.prepareStatement(_query);

		} catch (ULjException e) {
			e.getStackTrace();
			return false;
		}
		_prepared = true;
		return true;
	}

	@Override
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return new DocOrderPickListItemView(_context, this, lv, _doc);
	}

	public void setFilterByProduct(RefCskuEntity value) {
		_csku = value;
		setSelectParameters(null);
		Select();
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
