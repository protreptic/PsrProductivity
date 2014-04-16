package ru.magnat.sfs.ui.android.doc.order;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmObject;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderLineEntity;
import ru.magnat.sfs.bom.query.getPromoCsku.QueryGetPromoBrandEntity;
import ru.magnat.sfs.bom.query.getPromoCsku.QueryGetPromoCskuEntity;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListEntity;
import ru.magnat.sfs.bom.ref.promo.PromoType;
import ru.magnat.sfs.money.Money;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.controls.InputValueControl;
import ru.magnat.sfs.widget.PromotionIndicatorView;
import ru.magnat.sfs.widget.PromotionIndicatorView.ColorPointMode;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class DocOrderPickListItemView extends GenericListItemView {

	private Resources mResources;
	
	private QueryGetOrderPickListEntity _picklistitem; 
	private DocOrderEntity mOrderEntity;
	private TextView _order_text;

	@SuppressWarnings({ "unchecked" })
	public DocOrderPickListItemView(Context context, OrmObject list, ListView lv, DocOrderEntity entity) {
		super(context, list, lv);
	
		mResources = getContext().getResources();
		mOrderEntity = entity;
	}	
	
	@Override
	public SfsContentView inflate() {
		layoutInflater.inflate(R.layout.product_item, this);
		
		return this;
	}
	
	@Override
	public void fill(GenericEntity<?> entity) {
		if (entity == null) {
			return;
		}
		
		QueryGetOrderPickListEntity e = (QueryGetOrderPickListEntity) entity;
		_picklistitem = (QueryGetOrderPickListEntity) entity;
				
		Boolean icon_visible = false;

		// Product description
		TextView descriptionView = (TextView) findViewById(R.id.csku_name);
		descriptionView.setText(e.ProductItemDescr);
		descriptionView.setTypeface(typeface); 
		descriptionView.setTextSize(18f);  
		 
		// Product price
		TextView priceView = (TextView) findViewById(R.id.csku_price);
		priceView.setTypeface(typeface); 
		String priceText = Money.valueOf(e.ProductPrice).toSymbolString(new Locale("ru", "RU"));  
		priceView.setText(priceText);
		
		// Product tare
		TextView tareView = (TextView) findViewById(R.id.csku_case);
		tareView.setTypeface(typeface); 
		String tareText = String.format("%d шт/к", e.UnitFactor1);
		tareView.setText(tareText);
		
		// Product warehouse
		TextView warehouseView = (TextView) findViewById(R.id.csku_store_info);
		warehouseView.setTypeface(typeface);
		if (e.Stock > 0) {			
			String warehouseText = String.format("ост.: %d", e.Stock);
			warehouseView.setText(warehouseText);
		} else {
			descriptionView.setTextColor(mResources.getColor(R.color.red));
			warehouseView.setTextColor(mResources.getColor(R.color.red));
			
			String warehouseText = String.format("ост.: %d", e.Stock);
			warehouseView.setText(warehouseText);
		}
		
		// Priorities and initiatives
		ImageView cskuIniType = (ImageView) findViewById(R.id.csku_ini_type);
		if (e.isPriority == 1) {
			cskuIniType.setImageDrawable(_list_p_icon);
		} else if (e.isDrive == 1) {
			cskuIniType.setImageDrawable(_list_d_icon);
		} else if (e.isNextPriority == 1) {
			cskuIniType.setImageDrawable(_list_pn_icon);
		} else if (e.isNextDrive == 1) {
			cskuIniType.setImageDrawable(_list_dn_icon);
		}
		
		// Show picture if available
		ImageView pictureAvailable = (ImageView) findViewById(R.id.picture_empty);
		if (mOrderEntity.pictureAvailable(e.ExtCode)) {
			pictureAvailable.setVisibility(View.VISIBLE);
			pictureAvailable.setClickable(true);
			pictureAvailable.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (_picklistitem != null){		
						if (_picklistitem.ExtCode == null) {
							return;
						}
						String fileName = _picklistitem.ExtCode.trim();
						if (fileName.isEmpty()) {
							return;
						}
						fileName += ".jpg";
						Globals.openExtrasFile(getContext(), fileName,true);
					}
				}
			});
		} else {
			pictureAvailable.setVisibility(GONE);
		}
		
		// Qualification
		ImageView cskuStateIcon = (ImageView) findViewById(R.id.csku_list_type);
		switch ((int) e.ABC) {
			case CskuListType.LIST_A: {
				cskuStateIcon.setImageDrawable(_list_a_icon);
				cskuStateIcon.setTag("Золотая CSKU");
			} break;
			case CskuListType.LIST_B: {
				cskuStateIcon.setImageDrawable(_list_b_icon);
				cskuStateIcon.setTag("CSKU B");
			} break;
			case CskuListType.LIST_C: {
				cskuStateIcon.setImageDrawable(_list_c_icon);
				cskuStateIcon.setTag("Power CSKU");
			} break;
			default: {
				cskuStateIcon.setVisibility(View.INVISIBLE);
			}
		}
		cskuStateIcon.setClickable(true);
		cskuStateIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Object tag = v.getTag();
				if (tag != null) {
					Dialogs.createDialog("", tag.toString(), Command.NO_OP).show();
				}
			}
		});
		
		// Promotion indicator
		PromotionIndicatorView[] indicatorView = {
		(PromotionIndicatorView) findViewById(R.id.promotion_indicator_2)
		,(PromotionIndicatorView) findViewById(R.id.promotion_indicator_3)
		,(PromotionIndicatorView) findViewById(R.id.promotion_indicator_4)};
		
		QueryGetPromoCskuEntity pricing = mOrderEntity.getPromoCskuEntity(e.CskuId, PromoType.PRICING);
		QueryGetPromoCskuEntity buyAndGet = mOrderEntity.getPromoCskuEntity(e.CskuId, PromoType.BAG_ORDER);
		QueryGetPromoCskuEntity period = mOrderEntity.getPromoCskuEntity(e.CskuId, PromoType.BAG_PERIOD);
		QueryGetPromoBrandEntity volume = mOrderEntity.getPromoBrandEntity(e.Brand, PromoType.BAG_VOLUME);
		
		if (pricing==null) {
			PromotionIndicatorView ind = (PromotionIndicatorView) findViewById(R.id.promotion_indicator_1);
			ind.hide();
		}
		else {
			
			PromotionIndicatorView ind = (PromotionIndicatorView) findViewById(R.id.promotion_indicator_1);
			ind.enabledDiscountPoint();
			ind.setDiscountPointValue(pricing.Discount);
			ind.setDiscountPointDescription(pricing.Descr);
		
		}
		
		if (buyAndGet==null && period==null && volume==null){
			for (PromotionIndicatorView ind:indicatorView) ind.hide();
		}
		
		else {
			
			PromotionIndicatorView ind = indicatorView[0];
			ind.enabledColorPoint();
			if (buyAndGet != null) {
				ind.enabledColorPoint();
				ind.setDiscountPointDescription(buyAndGet.Descr);
				if (buyAndGet.IsAvailable) { 
					int multi = mOrderEntity.getPromotionMultiplicity(buyAndGet.Promo);
					if (multi > 0) { 
						ind.setColorPointMode(ColorPointMode.GREEN);
					} else {
						int progress = mOrderEntity.getPromoProgress(buyAndGet.Promo);
						if (progress >= 0 && progress < 50) {
							ind.setColorPointMode(ColorPointMode.RED);
						}
						if (progress >= 50 && progress < 100) {
							ind.setColorPointMode(ColorPointMode.YELLOW);
						}
						if (progress >= 100) {
							ind.setColorPointMode(ColorPointMode.GREEN);
						}
					}
				} else {
					ind.setColorPointMode(ColorPointMode.GRAY);
				}
			} else {
				ind.disableColorPoint();
			}
			ind = indicatorView[1];
			ind.enabledColorPoint();
			if (period != null) {
				ind.enabledColorPoint();
				ind.setDiscountPointDescription(period.Descr);
				if (period.CompensationType > 0) {
					ind.setColorPointMode(ColorPointMode.YELLOW);
				} else {
					ind.setColorPointMode(ColorPointMode.GRAY);
				}
			} else {
				ind.disableColorPoint();
			}
			ind = indicatorView[2];
			ind.enabledColorPoint();
			if (volume != null) {
				ind.enabledColorPoint();
				ind.setDiscountPointDescription(volume.Descr);
				if (volume.CompensationType > 0) {
					ind.setColorPointMode(ColorPointMode.YELLOW);
				} else {
					ind.setColorPointMode(ColorPointMode.GRAY);
				}
			} else {
				ind.disableColorPoint();
			}

		
		}
		// promo end
		
//		String profit = "";
//		if (e.ProfitDescription != null) {
//			profit = e.ProfitDescription.trim();
//		}

		String historyText = new String();
		int saleStatusPictureId = 0;
		if (e.LastSale != null) { 
			Long turnover = e.Turnover;
			Long cyclicity = e.Cyclicity;
			String lastSale = new SimpleDateFormat("dd.MM", new Locale("ru", "RU")).format(e.LastSale);
			
			historyText = String.format("заказ %d шт. каждые %d дней, последний раз %s", turnover, cyclicity, lastSale);

			switch (Utils.MonthToDate(e.LastSale, null)) {
				case QualificationMonthType.CURRENT_MONTH: {
					// Is it a new CSKU in store's assortment?
					if (e.Novetly) { 
						saleStatusPictureId = R.drawable.heart_full_up;
					} else {
						saleStatusPictureId = R.drawable.heart_full;
					}
				} break;
				case QualificationMonthType.CURRENT_MONTH_MINUS_ONE: {
					saleStatusPictureId = R.drawable.heart_full;
				} break;
				case QualificationMonthType.CURRENT_MONTH_MINUS_TWO: {
					saleStatusPictureId = R.drawable.heart_half;
				} break;
				case QualificationMonthType.CURRENT_MONTH_MINUS_THREE: {
					
				} break;
				default: {
					
				} break;
			}
		}
		
		ImageView qualificationView = (ImageView) findViewById(R.id.csku_qualifacation_status);
		if (saleStatusPictureId != 0) {
			qualificationView.setImageDrawable(mResources.getDrawable(saleStatusPictureId));
		}
		qualificationView.setVisibility((saleStatusPictureId != 0) ? View.VISIBLE : View.INVISIBLE);
		
		// History
		TextView historyView = (TextView) findViewById(R.id.csku_details);
		historyView.setTypeface(typeface);
		historyView.setText(historyText); 

		DocOrderLineEntity orderLine = mOrderEntity.getOrderLine(e.ProductItemId);
		_order_text = (TextView) findViewById(R.id.pick_list_order2);
		_order_text.setVisibility(View.INVISIBLE);
		_order_text.setTypeface(typeface);  
		if (orderLine == null || orderLine.Quantity == 0) {
			_order_text.setText(" ");
			_order_text.setVisibility(View.GONE);
		} else {
			_order_text.setVisibility(View.VISIBLE);
			switch (orderLine.UnitLevel) {
				case 1: {
					Utils.setConditionText(_order_text, Long.toString(orderLine.Quantity) + " КОР");
				} break;
				case 2: {
					Utils.setConditionText(_order_text,	Long.toString(orderLine.Quantity) + " БЛ");
				} break;
				default: {
					Utils.setConditionText(_order_text,Long.toString(orderLine.Quantity) + " ШТ");
				} break;
			}
		}

		ImageView cskuLogisticsStatus = (ImageView) findViewById(R.id.csku_log_status);
		
		String temp = e.GcasState;
		
		if (temp != null) {
			if (temp.equals("R")) {
				
			} else if (temp.equals("S")) {
				icon_visible = true;
				cskuLogisticsStatus.setImageDrawable(_sellout_icon);
				cskuLogisticsStatus.setTag("Товар имеет логистический статус Sell-Out (Распродажа)");
			} else if (temp.equals("Q")) {
				icon_visible = true;
				cskuLogisticsStatus.setImageDrawable(_quoted_icon);
				cskuLogisticsStatus.setTag("Товар имеет логистический статус Quoted (распределяется по лимитам)");
			}
		}
		if ((cskuLogisticsStatus.getVisibility() == View.VISIBLE) != icon_visible) {
			cskuLogisticsStatus.setVisibility((icon_visible) ? View.VISIBLE : View.INVISIBLE);
			if (icon_visible) {
				cskuLogisticsStatus.setClickable(true);
				cskuLogisticsStatus.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Object tag = v.getTag();
						if (tag!=null){
							Dialogs.createDialog("", tag.toString(), Command.NO_OP).show();
						}
						
					}
					
				});
			} else {
				cskuLogisticsStatus.setClickable(false);
			}
		}
	}

	private static final Drawable _list_a_icon=(MainActivity.sInstance.getResources()).getDrawable(R.drawable.medal_gold_1);
	private static final Drawable _list_b_icon=(MainActivity.sInstance.getResources()).getDrawable(R.drawable.medal_bronze_3);
	private static final Drawable _list_c_icon=(MainActivity.sInstance.getResources()).getDrawable(R.drawable.medal_silver_2);
	private static final Drawable _list_p_icon=(MainActivity.sInstance.getResources()).getDrawable(R.drawable.draw_star);
	private static final Drawable _list_d_icon=(MainActivity.sInstance.getResources()).getDrawable(R.drawable.rating_not_important2);
	private static final Drawable _sellout_icon=(MainActivity.sInstance.getResources()).getDrawable(R.drawable.sticker_blue_sale);
	private static final Drawable _quoted_icon=(MainActivity.sInstance.getResources()).getDrawable(R.drawable.lock);
	private static final Drawable _list_pn_icon = (MainActivity.sInstance.getResources()).getDrawable(R.drawable.draw_star_gray);
	private static final Drawable _list_dn_icon = (MainActivity.sInstance.getResources()).getDrawable(R.drawable.rating_not_important);

	@Override
	public void onValueChanged(Object sender, Object value) {
		if (sender instanceof InputValueControl) {
			_order_text.setText(Integer.toString((Integer) value));
		}
	}
	
}
