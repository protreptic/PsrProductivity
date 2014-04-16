package ru.magnat.sfs.bom.query.order;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import android.content.Context;
import ru.magnat.sfs.android.Log;

import com.ianywhere.ultralitejni12.ULjException;

public final class QueryGetOrderPickListSiblings extends QueryGetOrderPickList {

	static final String _query = "SELECT distinct ra.Id as Id "
			+ ", i.id as prodItem "
			+ ", p.Price*(1+(1-pt.IncludeVAT)*i.VAT) as prodPrice "
			+ ", COALESCE(s.ABC,0) as abc "
			+ ", COALESCE(s.IsPriority,0) as isPriority "
			+ ", COALESCE(s.IsDrive,0) as isDrive "
			+ ", COALESCE(h.Quantity,0) as Recommended "
			// + ", 0 as Recommended "
			+ ", COALESCE(w.Quantity,0) as Stock "
			+ ", c.OrderKey as OrderKey "
			+ ", c.Id as Csku "
			+ ", h.LastSale as LastSale "
			+ ", COALESCE(h.Novetly,0) as Novetly "
			+ ", COALESCE(h.Turnover,0) as Turnover "
			+ ", COALESCE(h.Cyclicity,0) as Cyclicity "
			+ ", i.Descr as prodItemDescr "
			+ ", c.Descr as CskuDescr "
			+ ", i.UnitFactor1 as UnitFactor1 "
			+ ", i.UnitFactor2 as UnitFactor2 "
			+ ", i.VAT as VAT "
			+ ", g.Descr as GcasState "
			+ ", i.ProfitDescription as ProfitDescription"
			+ ", c.ExtCode as ExtCode "
			+ ", COALESCE(s.IsNextPriority,0) as isNextPriority "
			+ ", COALESCE(s.IsNextDrive,0) as isNextDrive "
			+ ", COALESCE(c.Brand,0) as Brand"
			+ " FROM RegAssortment ra "
			+ " INNER JOIN RefProductItem i on ra.ProductItem=i.Id "
			+ " INNER JOIN RefGcasState g on i.GcasState=g.Id "
			+ " INNER JOIN RegPrice p on i.Id = p.ProductItem "
			+ " INNER JOIN RefCsku c on i.ParentExt=c.Id "
			+ " INNER JOIN RefCsku mc on mc.Parent=c.Parent "
			+ " LEFT JOIN RegCskuState s on c.Id=s.Csku "
			+ " and s.StoreChannel=? "
			// + " INNER JOIN DocOrderLine l on ra.ProductItem=l.Item  "
			// + " AND l.MasterDocId=? "
			// + " AND l.MasterDocAuthor=? "
			+ " LEFT JOIN RegStock w on ra.ProductItem = w.ProductItem and w.Warehouse=? "
			+ " INNER JOIN RefPriceType pt on p.PriceType=pt.Id "
			+ " LEFT JOIN RegOrderHelper h on c.Id=h.Csku "
			+ " and (1 = 1 OR h.Employee=?)  " 
			+ " and h.Outlet=? "
			+ " WHERE ra.Assortment=? " + " AND p.PriceType=? "
			// Условия-фильтры
			+ " AND (? = 0 OR COALESCE(w.Quantity, 0) > 0) " // Нет на складе
			+ " AND mc.Id = ? " + " ORDER BY c.OrderKey ";

	public QueryGetOrderPickListSiblings(Context context, DocOrderEntity entity, PickFilter filter) {
		super(context, entity, filter, _query);
	}
	private long _currentCskuGroup = -1;
	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		try {
			if (_cursor != null)
				_cursor.close();
		} catch (ULjException e) {

		}
		try {
			int p = 1;
			if (_channel == null)
				_select_statement.setNull(p++);
			else
				_select_statement.set(p++, _channel.Id);
			// _select_statement.set(2, _doc.Id);
			// _select_statement.set(3, _doc.Author.Id);
			if (_doc.Warehouse != null) {
				_select_statement.set(p++, _doc.Warehouse.Id);
			} else
				_select_statement.setNull(p++);
			_select_statement.set(p++, _doc.Employee.Id);
			_select_statement.set(p++, _doc.Outlet.Id);
			if (_doc.TradeRule == null)
				_select_statement.setNull(++p); //BUGFIX #251,252 ANALOGUE USMANOV
			else if (_doc.TradeRule.Assortment != null)
				_select_statement.set(p++, _doc.TradeRule.Assortment.Id);
			else if (_employee.Assortment != null)
				_select_statement.set(p++, _employee.Assortment.Id);
			else
				_select_statement.setNull(p++);
			if (_doc.TradeRule == null)
				_select_statement.setNull(++p); //BUGFIX #251,252 ANALOGUE USMANOV
			else if (_doc.TradeRule.PriceType != null) {
				if (_doc.TradeRule.PriceType.BaseType != null)
					_select_statement.set(p++,
							_doc.TradeRule.PriceType.BaseType.Id);
				else
					_select_statement.set(p++, _doc.TradeRule.PriceType.Id);
			} else
				_select_statement.setNull(p++);
			// Если установлен фильтр по товарам которые есть в наличии
			if (_pickFilter.getInStock()) {
				_select_statement.set(p++, 1);
			} else {
				_select_statement.set(p++, 0);
			}
			
//			if (_pickFilter.getProduct() != null) {
//				_select_statement.set(p++, _pickFilter.getProduct().ParentId);
//			} else
//				_select_statement.set(p++, -1);
			
			_select_statement.set(p++, _currentCskuGroup);
		} catch (ULjException e) {
			Log.v(TAG,
					this.getClass().getName() + ":error on set params: "
							+ e.getMessage());
			return false;
		}

		return true;
	}

	//private boolean mOutOfStockFilterIsActive = false;
	
    @Override
    protected void beforeFilterChanged() {
    	
    	super.beforeFilterChanged();
    	_currentCskuGroup = -1;
    	
    }
    
	public void setFilterByProduct(Long parentId) {
		long id = -1;
		if (parentId!=null){
			id = parentId;
		}
		if (id == _currentCskuGroup) return;
		_currentCskuGroup = id;
		asyncSelect();
		_orderKey = "";
		
	}
	

}
