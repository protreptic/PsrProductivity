package ru.magnat.sfs.bom.query.order;

import java.util.Date;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetOrderPickListEntity extends QueryGenericEntity<QueryGetOrderPickList> {

	@OrmEntityField(DisplayName = "Товар", isPrimary = 0, fields = "prodItem")
	public long ProductItemId;
	@OrmEntityField(DisplayName = "Товар", isPrimary = 0, fields = "prodItemDescr")
	public String ProductItemDescr;
	@OrmEntityField(DisplayName = "Базовая цена", isPrimary = 0, fields = "prodPrice")
	public float ProductPrice;
	@OrmEntityField(DisplayName = "ABC", isPrimary = 0, fields = "abc")
	public long ABC;
	@OrmEntityField(DisplayName = "Приоритет", isPrimary = 0, fields = "isPriority")
	public long isPriority;
	@OrmEntityField(DisplayName = "Инициатива", isPrimary = 0, fields = "isDrive")
	public long isDrive;
	@OrmEntityField(DisplayName = "Приоритет след. месяца", isPrimary = 0, fields = "isNextPriority")
	public long isNextPriority;
	@OrmEntityField(DisplayName = "Инициатива след. месяца", isPrimary = 0, fields = "isNextDrive")
	public long isNextDrive;
	@OrmEntityField(DisplayName = "Рекомендовано", isPrimary = 0, fields = "Recommended")
	public long Recommended;
	@OrmEntityField(DisplayName = "Остаток", isPrimary = 0, fields = "Stock")
	public long Stock;
	@OrmEntityField(DisplayName = "Порядок", isPrimary = 0, fields = "OrderKey")
	public String OrderKey;
	@OrmEntityField(DisplayName = "Csku", isPrimary = 0, fields = "Csku")
	public long CskuId;
	@OrmEntityField(DisplayName = "Csku", isPrimary = 0, fields = "CskuDescr")
	public String CskuDescr;
	@OrmEntityField(DisplayName = "Последняя продажа", isPrimary = 0, fields = "LastSale")
	public Date LastSale;
	@OrmEntityField(DisplayName = "Новая", isPrimary = 0, fields = "Novetly")
	public Boolean Novetly;
	@OrmEntityField(DisplayName = "Оборачиваемость", isPrimary = 0, fields = "Turnover")
	public long Turnover;
	@OrmEntityField(DisplayName = "Цикличность", isPrimary = 0, fields = "Cyclicity")
	public long Cyclicity;
	@OrmEntityField(DisplayName = "В коробке", isPrimary = 0, fields = "UnitFactor1")
	public int UnitFactor1;
	@OrmEntityField(DisplayName = "В блоке", isPrimary = 0, fields = "UnitFactor2")
	public int UnitFactor2;
	@OrmEntityField(DisplayName = "Статус gcas", isPrimary = 0, fields = "GcasState")
	public String GcasState;
	@OrmEntityField(DisplayName = "НДС", isPrimary = 0, fields = "VAT")
	public float VAT;
	@OrmEntityField(DisplayName = "Внешний код", isPrimary = 0, fields = "ExtCode")
	public String ExtCode;
	@OrmEntityField(DisplayName = "Выгода", isPrimary = 0, fields = "ProfitDescription")
	public String ProfitDescription;
	@OrmEntityField(DisplayName = "Код бренда", isPrimary = 0, fields = "Brand")
	public long Brand;
}
