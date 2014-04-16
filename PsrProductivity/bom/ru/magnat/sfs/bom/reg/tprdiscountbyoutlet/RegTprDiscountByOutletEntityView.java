package ru.magnat.sfs.bom.reg.tprdiscountbyoutlet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class RegTprDiscountByOutletEntityView extends
		GenericEntityView<RegTprDiscountByOutlet,RegTprDiscountByOutletEntity> implements
		OnItemClickListener {
  
	public RegTprDiscountByOutletEntityView(
			RegTprDiscountByOutletEntity entity,
			Float price) {
		super(MainActivity.getInstance(), new RegTprDiscountByOutlet(MainActivity.getInstance()), entity);
		if (price==null) _recommendedPrice=0f;
		else
			_recommendedPrice = price*1.25f;
		
	}

	AlertDialog _alertDialog;
	SimpleAdapter _adapter;
	final Float _recommendedPrice;

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		// if (visibility==VISIBLE)
		refresh();
	}

	@Override
	public SfsContentView inflate() {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_order_pick_list_item_details, this);
		_adapter = new SimpleAdapter(getContext(), _entity.getProperties(),
				android.R.layout.simple_list_item_2, new String[] { "value",
						"property" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(_adapter);
		lv.setOnItemClickListener(this);
		((TextView) findViewById(R.id.caption)).setText("Временное снижение цены");
		
		if (_recommendedPrice>0){
			((TextView) findViewById(R.id.priceinfotext)).setText(
					String.format("Рекомендованная цена на полке должна быть не выше %,.2fр", _recommendedPrice)
					);
		}
		else
			((View) findViewById(R.id.priceinfo)).setVisibility(GONE);
		
		return this;
	}

	@Override
	public void refresh() {
		_adapter.notifyDataSetChanged();
	}

	public void dispose(){
		if (_catalog!=null){
			_catalog.close();
			_catalog = null;
		}
	}
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		@SuppressWarnings("unchecked")
		HashMap<String, ?> menuItem = (HashMap<String, ?>) _adapter
				.getItem(position);
		if (menuItem == null)
			return;
		String selectMethod = (String) menuItem.get("selectMethod");
		if ((selectMethod == null) || (selectMethod.isEmpty()))
			return;
		Method method = null;
		try {
			method = this.getClass().getDeclaredMethod(selectMethod);
			method.setAccessible(true);
		} catch (SecurityException e) {

			e.printStackTrace();
			return;
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
			return;
		}
		try {
			method.invoke(this);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

	}

}
