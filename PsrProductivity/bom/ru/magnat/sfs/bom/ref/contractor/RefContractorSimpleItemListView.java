package ru.magnat.sfs.bom.ref.contractor;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;

public class RefContractorSimpleItemListView extends GenericListItemView {

	public RefContractorSimpleItemListView(Context context, RefContractor ref,
			ListView lv) {
		super(context, ref, lv);

	}

	@Override
	public SfsContentView inflate() {

		RefContractorEntity e = (RefContractorEntity) _orm.Current();

		String firstLineText = "";
		String secondLineText = "";
		if (e != null) {
			firstLineText = e.toString();
			if (e.ParentExt != null)
				secondLineText = "Заказчик: " + e.ParentExt.toString();

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
		this.setBackgroundColor();
		return this;

	}

}
