package ru.magnat.sfs.bom.query.getTradeRule;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;

public final class QueryGetTradeRuleEntity extends
		QueryGenericEntity<QueryGetTradeRule> {
	@OrmEntityField(DisplayName = "Торговое условие", isPrimary = 0, fields = "TradeRule")
	public RefTradeRuleEntity TradeRule;

}
