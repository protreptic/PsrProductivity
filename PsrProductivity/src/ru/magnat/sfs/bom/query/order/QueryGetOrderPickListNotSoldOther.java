package ru.magnat.sfs.bom.query.order;

import java.util.ArrayList;

import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import android.content.Context;
import ru.magnat.sfs.android.Log;

import com.ianywhere.ultralitejni12.ULjException;

public final class QueryGetOrderPickListNotSoldOther extends
		QueryGetOrderPickList {

	static final String _query = "SELECT distinct ra.Id as Id "
			+ ", i.id as prodItem "
			+ ", p.Price*(1+(1-pt.IncludeVAT)*i.VAT) as prodPrice "
			+ ", COALESCE(s.ABC,0) as abc "
			+ ", COALESCE(s.IsPriority,0) as isPriority "
			+ ", COALESCE(s.IsDrive,0) as isDrive "
			+ ", COALESCE(h.Quantity,0) as Recommended "
			+ ", COALESCE(w.Quantity,0) as Stock "
			+ ", c.OrderKey as OrderKey "
			+ ", c.Id as Csku "
			+ ", null as LastSale "
			+ ", 0 as Novetly "
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
			+ " LEFT JOIN RegCskuState s on c.Id=s.Csku "
			+ " and s.StoreChannel=? "
			+ " LEFT JOIN RegOrderHelper h on c.Id=h.Csku "
			+ " and (1 = 1 OR h.Employee=?)  "
			+ " and h.Outlet=? "
			+ " and h.CurrentPeriod=1 "
			+ " LEFT JOIN RegStock w on ra.ProductItem = w.ProductItem and w.Warehouse=? "
			//+ " LEFT JOIN RegTprDiscountByOutlet tpr on tpr.Outlet = ? and (i.Id = tpr.ProductItem or c.Id = tpr.Csku)"
			+ " INNER JOIN RefPriceType pt on p.PriceType=pt.Id "
			+ " WHERE ra.Assortment=? " + " AND p.PriceType=? "
			+ " AND c.Fullpath LIKE ? " + " AND s.ABC IS NULL AND NULLIF(s.IsDrive,0) IS NULL  AND NULLIF(s.IsNextDrive,0) IS NULL AND NULLIF(s.IsPriority,0) IS NULL AND NULLIF(s.IsNextPriority,0) IS NULL "
			+ " AND h.Id IS NULL " + " AND NOT NULLIF(p.Price,0) IS NULL "
			// Условия-фильтры
			+ " AND (? = 0 OR COALESCE(w.Quantity, 0) > 0) " // Нет на складе
			//+ " AND (? = 0 OR COALESCE(tpr.Id,0) > 0  OR not nullif(trim(ProfitDescription),'') is null)" //TPR скидка
			+ " AND (? = 0 OR not nullif(trim(ProfitDescription),'') is null)" //Выгода
			+ " AND (? = 0 OR COALESCE(g.Id, 0) = 2 OR COALESCE(g.Id, 0) = 3) " //нерегулярные товары
			+ " AND (? = 0 OR COALESCE(c.IsKit, 0) = 1) " //набор
			+ " ORDER BY c.OrderKey ";

	public QueryGetOrderPickListNotSoldOther(Context context,
			DocOrderEntity entity, PickFilter filter) {
		super(context, entity, filter, _query);
	}

	@Override
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		int param = 0;
		try {
			if (_cursor != null) _cursor.close();
			if (_channel == null) {
				_select_statement.setNull(++param);
			} else {
				_select_statement.set(++param, _channel.Id);
			}
			_select_statement.set(++param, _employee.Id);
			_select_statement.set(++param, _outlet.Id);
			if (_doc.Warehouse != null) {
				_select_statement.set(++param, _doc.Warehouse.Id);
			} else
				_select_statement.setNull(++param);
			//_select_statement.set(++param, _outlet.Id);
			if (_doc.TradeRule == null)
				_select_statement.setNull(++param); //BUGFIX #252 USMANOV
			else if (_doc.TradeRule.Assortment != null)
				_select_statement.set(++param, _doc.TradeRule.Assortment.Id);
			else if (_employee.Assortment != null)
				_select_statement.set(++param, _employee.Assortment.Id);
			else
				_select_statement.setNull(++param);
			if (_doc.TradeRule == null)
				_select_statement.setNull(++param); //BUGFIX #252 USMANOV
			else if (_doc.TradeRule.PriceType != null) {
				if (_doc.TradeRule.PriceType.BaseType != null)
					_select_statement.set(++param,
							_doc.TradeRule.PriceType.BaseType.Id);
				else
					_select_statement.set(++param, _doc.TradeRule.PriceType.Id);
			} else
				_select_statement.setNull(++param);
			if (_pickFilter.getProduct()!=null){
				_select_statement.set(++param,_pickFilter.getProduct().Fullpath+"%");
			} else _select_statement.set(++param,"%");
			// Если установлен фильтр по товарам которые есть в наличии
			
			if (_pickFilter.getInStock()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
			// Фильтр по TPR скидкам
			if (_pickFilter.getOnlyTpr()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
			/*if (this.mOutOfStockFilterIsActive) {
				_select_statement.set(8, 1);
			} else {
				_select_statement.set(8, 0);
			}*/
			// Фильтр по нерегулярным товарам
			if (_pickFilter.getTemporary()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
			// Фильтр по наборам
			if (_pickFilter.getKits()) {
				_select_statement.set(++param, 1);
			} else {
				_select_statement.set(++param, 0);
			}
		} catch (ULjException e) {
			Log.v(TAG,
					this.getClass().getName() + ":error on set params: "
							+ e.getMessage());
			return false;
		}

		return true;
	}

//	private boolean mOutOfStockFilterIsActive = false;

}
