package ru.magnat.sfs.ui.android.doc.order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.ref.productitem.RefProductItem;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.reg.discount.RegDiscount;
import ru.magnat.sfs.bom.reg.discount.RegDiscountEntity;
import ru.magnat.sfs.money.Money;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class PriceCalculatorActivity extends Activity {

	private Context mActivityContext;
	
	private DocOrderEntity mDocOrder;
	private RefProductItemEntity mProductItem;
	
	private Long mProductItemId;
	private String mProductName;
	private Money mBasePriceValue;
	private Money mDiscountValue;
	private Money mDiscountPriceValue;
	
	private TextView mItemName;
	private Spinner mSpinner1;
	private Spinner mSpinner2;
	private TextView mAmountBase;
	private TextView mDiscountPrice;
	private TextView mDiscountAmount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_price_calculator);
		
		this.mActivityContext = this;
		
		this.mProductItemId = this.getIntent().getExtras().getLong("itemId");
		this.mProductName = this.getIntent().getExtras().getString("itemName");
		this.mBasePriceValue = Money.valueOf(this.getIntent().getExtras().getFloat("basePrice"));
		this.mDiscountPriceValue = Money.valueOf(this.getIntent().getExtras().getFloat("discountPrice"));
		this.mDiscountValue = Money.valueOf(this.getIntent().getExtras().getFloat("discount"));
		this.mProductItem = this.getProductItem(this.mProductItemId);
		
		this.mDocOrder = MainActivity.getInstance().mCurrentOrder;

		this.mItemName = (TextView) findViewById(R.id.item_name);
		this.mItemName.setText(this.mProductName);
		
		TreeSet<Integer> brackets1 = new TreeSet<Integer>();
		
		RegDiscount regDiscount = new RegDiscount(mActivityContext);
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("TradeRule", this.mDocOrder.TradeRule.Id));
		criteria.add(new SqlCriteria("Employee", this.mDocOrder.Employee.Id));
		criteria.add(new SqlCriteria("DiscountType", 1));
		regDiscount.Select(criteria);
		while (regDiscount.Next()) {
			brackets1.add((int) regDiscount.Current().Border);
		}
		regDiscount.close();
		
		this.mSpinner1 = (Spinner) findViewById(R.id.spinner1);
		this.mSpinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}	
		);

		List<String> list1 = new ArrayList<String>();
		
		for (Iterator<Integer> iterator = brackets1.iterator(); iterator.hasNext();) {
			list1.add(iterator.next().toString());
		}
		
		ArrayAdapter<String> dataAdapter1;
		if (list1.isEmpty()) {
			list1.add("Недоступно");
			dataAdapter1 = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, list1);
			this.mSpinner1.setAdapter(dataAdapter1);
		} else {
			dataAdapter1 = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, list1);
			this.mSpinner1.setAdapter(dataAdapter1);
			for (int i = 0; i < list1.size(); i++) {
				if (list1.contains("0")) {
					this.mSpinner1.setSelection(i);
					break;
				}
			}
		}
		
		//List<String> brackets2 = new ArrayList<String>();
		Set<Integer> brackets2 = new TreeSet<Integer>();
		
		RegDiscount regDiscount2 = new RegDiscount(mActivityContext);
		regDiscount2 = new RegDiscount(mActivityContext);
		criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("TradeRule", this.mDocOrder.TradeRule.Id));
		criteria.add(new SqlCriteria("Employee", this.mDocOrder.Employee.Id));
		criteria.add(new SqlCriteria("DiscountType", 2));
		
		regDiscount2.Select(criteria);
		while (regDiscount2.Next()) {
			RegDiscountEntity entity = regDiscount2.Current();
			if (this.mDocOrder.productInAssortment(this.mProductItem, entity.Assortment)) {
				brackets2.add((int) regDiscount2.Current().Border); 
			}
		}
		regDiscount2.close();
		
		this.mSpinner2 = (Spinner) findViewById(R.id.spinner2);
		this.mSpinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		}	
		);
		
		List<String> list2 = new ArrayList<String>();
		
		for (Iterator<Integer> iterator = brackets2.iterator(); iterator.hasNext();) {
			list2.add(iterator.next().toString());
		}
		
		ArrayAdapter<String> dataAdapter2;
		if (list2.isEmpty()) {
			list2.add("Недоступно");
			dataAdapter2 = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, list2);
			this.mSpinner2.setAdapter(dataAdapter2);
		} else {
			dataAdapter2 = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, list2);
			this.mSpinner2.setAdapter(dataAdapter2);
			for (int i = 0; i < list2.size(); i++) {
				if (list2.contains(String.valueOf(this.mDocOrder.Delay))) {
					this.mSpinner2.setSelection(i);
					break;
				}
			}
			
			
			
		}

		dataAdapter1.setDropDownViewResource(R.layout.spinner_drop_down_item);
		dataAdapter2.setDropDownViewResource(R.layout.spinner_drop_down_item);
		
		this.mAmountBase = (TextView) findViewById(R.id.amount_base);
		this.mAmountBase.setText(this.mBasePriceValue.toSymbolString());
		
		this.mDiscountPrice = (TextView) findViewById(R.id.sell_fee);
		this.mDiscountPrice.setText(this.mDiscountPriceValue.toSymbolString());
		
		this.mDiscountAmount = (TextView) findViewById(R.id.discount_amount);
		this.mDiscountAmount.setText(String.format("%.1f%%", Float.valueOf(this.mDiscountValue.toString()))); 
		
		updateData();
	} 
	
	private RefProductItemEntity getProductItem(final Long productItemId) {
		RefProductItemEntity result = null;
		RefProductItem refProductItem = new RefProductItem(this.mActivityContext);
		
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Id", productItemId));
		
		refProductItem.Select(criteria);
		refProductItem.Next();
		result = refProductItem.Current();
		refProductItem.close();
		
		return result;
	}
	
	private void updateData() {
		int delay = 0;
		float amount = 0;
		if (!this.mSpinner2.getSelectedItem().toString().equals("Недоступно")) {
			delay = Integer.valueOf(this.mSpinner2.getSelectedItem().toString());
		}
		if (!this.mSpinner1.getSelectedItem().toString().equals("Недоступно")) {
			amount = Float.valueOf(this.mSpinner1.getSelectedItem().toString());
		}

		float amountline = DocOrderEntity.getAmountLine(this.mDocOrder, amount, delay, mProductItem, Float.valueOf(this.mBasePriceValue.toString()));
		float discount = 0;
		
		if (Float.valueOf(this.mBasePriceValue.toString()) != 0) {
			discount = amountline / Float.valueOf(this.mBasePriceValue.toString()) * 100f - 100f;
		}

		this.mAmountBase.setText(Money.valueOf(this.mBasePriceValue).toSymbolString());
		this.mDiscountPrice.setText(Money.valueOf(amountline).toSymbolString());
		this.mDiscountAmount.setText(String.format("%.1f%%", discount)); 
	}
	
}
