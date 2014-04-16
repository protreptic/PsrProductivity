package ru.magnat.sfs.bom.ref.outlet;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;

public class RefOutletSimpleItemListView extends GenericListItemView {

	public RefOutletSimpleItemListView(Context context, RefOutlet ref,
			ListView lv) {
		super(context, ref, lv);

	}

	@Override
	public SfsContentView inflate() {

		RefOutletEntity e = (RefOutletEntity) _orm.Current();

		String firstLineText = "";
		String secondLineText = "";
		if (e != null) {
			firstLineText = e.Descr;
			secondLineText = e.Address;

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
