package ru.magnat.sfs.bom.ref.productitem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.order.PriceCalculatorActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class RefProductItemEntityView extends GenericEntityView<RefProductItem, RefProductItemEntity> implements OnItemClickListener {
    private Float _baseprice = null;
    private Float _price = null;
    private Float _discount = null;
    private RefProductItemEntity mProductItem;
    
	public RefProductItemEntityView(Context context) {
		super(context);
	}

	public RefProductItemEntityView(RefProductItem catalog,	RefProductItemEntity entity) {
		super(MainActivity.getInstance(), catalog, entity);
		this.mProductItem = entity;
	}
	
	public RefProductItemEntityView(RefProductItem catalog,	RefProductItemEntity entity, Float baseprice, Float price, Float discount) {
		super(MainActivity.getInstance(), catalog, entity);
		_baseprice	 = baseprice;
		_price		 = price;
		_discount	 = discount;
		this.mProductItem = entity;
	}

	AlertDialog _alertDialog;
	SimpleAdapter _adapter;
	private EditText _editText;

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		refresh();
	}

	@Override
	public SfsContentView inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_order_pick_list_item_details, this);
		_adapter = new SimpleAdapter(getContext(), _entity.getProperties(), android.R.layout.simple_list_item_2, new String[] { "value", "property" }, new int[] { android.R.id.text1, android.R.id.text2 });
		ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(_adapter);
		lv.setOnItemClickListener(this);

		StringBuilder sb = new StringBuilder();
		
		((TextView) findViewById(R.id.caption)).setText(_entity.toString());
		if (_baseprice != null && _price != null) {
			sb.append(String.format("Цена по базе: %,.2fр\n", _baseprice));
			sb.append(String.format("Цена продажи: %,.2fр\n", _price));
			sb.append(String.format("Скидка: %,.2f\n", _discount));
			((TextView) findViewById(R.id.priceinfotext)).setText(sb.toString());
		} else {
			((View) findViewById(R.id.priceinfo)).setVisibility(GONE);
		}
		
		ImageView view = (ImageView) this.findViewById(R.id.calc_button);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getContext(), PriceCalculatorActivity.class);
				intent.putExtra("productItemId", mProductItem.Id);
				intent.putExtra("itemId", mProductItem.Id);
				intent.putExtra("itemName", mProductItem.Fullname);
				intent.putExtra("basePrice", _baseprice);
				intent.putExtra("discountPrice", _price);
				intent.putExtra("discount", _discount);
				getContext().startActivity(intent);
			}
		});
		
		return this;
	}

	@Override
	public void refresh() {
		_adapter.notifyDataSetChanged();
	}
	
	private void showLongText(String comment){
		if (_alertDialog != null) {
			_alertDialog.cancel();
		}

		_editText = new EditText(getContext());
		_editText.setMinLines(1);
		_editText.setMaxLines(5);
		_editText.setMinimumHeight(300);
		_editText.setGravity(Gravity.TOP);
		
		_editText.setText(comment);
		_editText.setEnabled(false);
		_alertDialog = Dialogs.createDialog("", "", _editText, null,
				new Command() {
					public void execute() {
						Utils.hideInput();
					}
				}, new Command() {
					public void execute() {
						Utils.hideInput();
					}
				});
		_alertDialog.show();
	}
	
	@SuppressWarnings("unused")
	private void showFullname() {
		if (_entity.Fullname != null)
			showLongText(_entity.Fullname);
	}
	
	@SuppressWarnings("unused")
	private void showProfit() {
		if (_entity.ProfitDescription != null)
			showLongText(_entity.ProfitDescription);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		@SuppressWarnings("unchecked")
		HashMap<String, ?> menuItem = (HashMap<String, ?>) _adapter.getItem(position);
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
