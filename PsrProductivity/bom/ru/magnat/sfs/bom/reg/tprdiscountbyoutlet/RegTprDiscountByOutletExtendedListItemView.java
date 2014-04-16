package ru.magnat.sfs.bom.reg.tprdiscountbyoutlet;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;

public final class RegTprDiscountByOutletExtendedListItemView extends
		GenericListItemView<RegTprDiscountByOutlet, RegTprDiscountByOutletEntity> {

	public RegTprDiscountByOutletExtendedListItemView(Context context, RegTprDiscountByOutlet orm,
			ListView lv) {
		super(context, orm, lv);
	}

	@Override
	public void fill() {
		_current = _orm.Current();

		String firstLineText = "";
		String secondLineText = "";
		if (_current != null) {
			RefGenericEntity<?,?> product = _current.Csku;
			if (product==null)  product = _current.ProductItem;
			
			if (product != null) {
				firstLineText = String.format("%s %.2f%%",  product.Descr,_current.DiscountValue);
				secondLineText = String.format("Действует с %td.%tm по %td.%tm", _current.BeginOfAction,_current.BeginOfAction,_current.EndOfAction,_current.EndOfAction);
			} else {
				firstLineText = "";
				secondLineText = "Ошибка получения данных о TPR-скидке!";
			}
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

		inflater.inflate(R.layout.simple_list_item_view, this);
		// inflater.inflate(R.layout.visit_item, this);

		return this;

	}

}
