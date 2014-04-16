package ru.magnat.sfs.bom.ref.marketingmeasure;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;

public class RefMarketingMeasureSimpleItemListView extends GenericListItemView {

	public RefMarketingMeasureSimpleItemListView(Context context,
			RefMarketingMeasure ref, ListView lv) {
		super(context, ref, lv);

	}

	@Override
	public SfsContentView inflate() {

		RefMarketingMeasureEntity e = (RefMarketingMeasureEntity) _orm
				.Current();

		String firstLineText = "";
		String secondLineText = "";
		if (e != null) {
			firstLineText = e.Descr;
			// secondLineText = e.Address;

		}
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.simple_list_item_view, this);
		{
			TextView v = (TextView) findViewById(R.id.first_line);
			v.setText(firstLineText);
		}
		{
			TextView v = (TextView) findViewById(R.id.second_line);
			v.setText(secondLineText);
		}
		setBackgroundColor();
		return this;
	}

}
