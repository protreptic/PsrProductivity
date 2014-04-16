package ru.magnat.sfs.bom.reg.paymenttype;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;

public class RegPaymentTypeSimpleItemListView extends GenericListItemView {

	public RegPaymentTypeSimpleItemListView(Context context,
			RegPaymentType orm, ListView lv) {
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
		RegPaymentTypeEntity e = (RegPaymentTypeEntity) _orm.Current();

		String firstLineText = "";
		String secondLineText = "";
		if (e != null && e.PaymentType !=null) { //BUGFIX #79,114,107 USMANOV
			firstLineText = e.PaymentType.toString();
			if (e.Delay > 0)
				secondLineText = "Отсрочка " + e.Delay + " банк. дней";
			else
				secondLineText = "Без отсрочки";
			if (e.Limit>0){
				secondLineText+=String.format(" Лимит %.2f руб", e.Limit);
			}

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
