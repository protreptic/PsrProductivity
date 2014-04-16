package ru.magnat.sfs.bom.query.getTprDiscounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;

public final class QueryGetTprDiscountsExtendedListItemView extends
		GenericListItemView<QueryGetTprDiscounts, QueryGetTprDiscountsEntity> {

	public QueryGetTprDiscountsExtendedListItemView(Context context, QueryGetTprDiscounts orm,
			ListView lv) {
		super(context, orm, lv);
	}

	@Override
	public void fill() {
		_current = _orm.Current();

		String firstLineText = "";
		String secondLineText = "";
		if (_current != null) {
			firstLineText =  _current.Descr;
			secondLineText = String.format("действует с %td.%tm по %td.%tm", _current.BeginOfAction, _current.BeginOfAction, _current.EndOfAction, _current.EndOfAction);
		}
		{
			// TextView v = (TextView)findViewById(R.id.outlet_descr);
			TextView v = (TextView) findViewById(R.id.first_line);
			v.setText(firstLineText);
		}
		{
			// TextView v = (TextView)findViewById(R.id.outlet_address);
			TextView v = (TextView) findViewById(R.id.second_line);
			v.setText(secondLineText);

		}

		
		this.setBackgroundColor();
	}

	@Override
	protected void setBackgroundColor() {
	
	}

	@Override
	public SfsContentView inflate() {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.simple_list_item_view_small_text, this);
		return this;
	}

}
