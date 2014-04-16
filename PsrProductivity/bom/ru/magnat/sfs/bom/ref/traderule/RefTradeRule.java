package ru.magnat.sfs.bom.ref.traderule;

import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.content.Context;
import android.widget.ListView;

public final class RefTradeRule extends RefGeneric {

	public RefTradeRule(Context context) {
		super(context, RefTradeRuleEntity.class);
		
	}

	public RefTradeRuleEntity Current() {
		return (RefTradeRuleEntity) super.Current();
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

}
