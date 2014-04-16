package ru.magnat.sfs.bom.reg.traderule;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;

public class RegTradeRuleSimpleItemListView extends GenericListItemView {

	public RegTradeRuleSimpleItemListView(Context context, RegTradeRule orm,
			ListView lv) {
		super(context, orm, lv);

	}

	@Override
	public SfsContentView inflate() {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.simple_list_item_view, this);

		return this;

	}

	@Override
	public void fill() {
		RegTradeRuleEntity e = (RegTradeRuleEntity) _orm.Current();

		String firstLineText = "";
		String secondLineText = "";
		if (e != null) {
			firstLineText = e.TradeRule.toString();
			if (e.TradeRule.Assortment != null)
				secondLineText += "Ассортимент "
						+ e.TradeRule.Assortment.toString();
			if (e.TradeRule.PriceType != null)
				secondLineText += " Тип цен "
						+ e.TradeRule.PriceType.toString();

		}
		{
			TextView v = (TextView) findViewById(R.id.first_line);
			v.setText(firstLineText);
		}
		{
			TextView v = (TextView) findViewById(R.id.second_line);
			v.setText(secondLineText);
		}
		setBackgroundColor();
	}

}
