package ru.magnat.sfs.ui.android.ref.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.ref.outlet.RefOutlet;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;

@SuppressWarnings("rawtypes")
public class RefOutletExtendedListItemView extends GenericListItemView {

	@SuppressWarnings("unchecked")
	public RefOutletExtendedListItemView(Context context, RefOutlet orm,
			ListView lv) {
		super(context, orm, lv);

	}

	@Override
	public void fill() {
		RefOutletEntity e = (RefOutletEntity) _orm.Current();

		String firstLineText = "";
		String secondLineText = "";
		if (e != null) {

			firstLineText = e.Descr;
			secondLineText = e.Address;

		}
		{
			TextView v = (TextView) findViewById(R.id.first_line);
			v.setText(firstLineText);

		}
		{
			TextView v = (TextView) findViewById(R.id.second_line);
			v.setText(secondLineText);

		}
		this.setBackgroundColor();
	}

	@Override
	public SfsContentView inflate() {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.simple_list_item_view, this);

		return this;

	}

}
